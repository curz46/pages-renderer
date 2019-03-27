package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.gfx.opengl.Texture;
import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import me.dylancurzon.dontdie.gfx.page.AlignedElement;
import me.dylancurzon.dontdie.gfx.page.Page;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.SpriteMutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.TextMutableElement;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.SpritePacker;
import me.dylancurzon.dontdie.sprite.TextSprite;
import me.dylancurzon.dontdie.util.ShaderUtil;
import me.dylancurzon.dontdie.util.Vector2i;
import org.lwjgl.opengl.ARBShaderObjects;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL14.glSecondaryColor3b;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class PageRenderer implements Renderer {

    private final Page page;

    private int spriteProgram;
    private SpritePacker packer;
    private Texture packerTexture;

    private int spriteVertices = 0;
    private VertexBuffer spritePositions;
    private VertexBuffer spriteTexCoords;

    // Delegate Text rendering to the TextRenderer
    // This way every element can be rendered a Sprite
    private TextRenderer textRenderer;

    private int fillProgram;

    private VertexBuffer outlinePositions;
    private VertexBuffer outlineColors;
    private int outlineVertices = 0;

    private VertexBuffer fillPositions;
    private VertexBuffer fillColors;
    private int fillVertices = 0;

    public PageRenderer(final Page page) {
        this.page = page;
    }

    @Override
    public void prepare() {
        this.spriteProgram = ShaderUtil.createShaderProgram("page");
        this.spritePositions = VertexBuffer.make();
        this.spriteTexCoords = VertexBuffer.make();

        this.fillProgram = ShaderUtil.createShaderProgram("fill");

        this.outlinePositions = VertexBuffer.make();
        this.outlineColors = VertexBuffer.make();
        this.fillPositions = VertexBuffer.make();
        this.fillColors = VertexBuffer.make();

        this.textRenderer = new TextRenderer();
        this.textRenderer.prepare();

        this.update();
    }

    @Override
    public void cleanup() {
        this.spritePositions.destroy();
        this.spriteTexCoords.destroy();

        this.outlinePositions.destroy();
        this.outlineColors.destroy();

        this.fillPositions.destroy();
        this.fillColors.destroy();

        this.spritePositions = null;
        this.spriteTexCoords = null;

        this.outlinePositions = null;
        this.outlineColors = null;

        this.fillPositions = null;
        this.fillColors = null;
    }

    @Override
    public void render() {
        ARBShaderObjects.glUseProgramObjectARB(this.fillProgram);

        this.fillPositions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        this.fillColors.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_QUADS, 0, this.fillVertices);

        ARBShaderObjects.glUseProgramObjectARB(0);
        ARBShaderObjects.glUseProgramObjectARB(this.fillProgram);

        this.outlinePositions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        this.outlineColors.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_LINES, 0, this.outlineVertices);

        ARBShaderObjects.glUseProgramObjectARB(0);
        ARBShaderObjects.glUseProgramObjectARB(this.spriteProgram);
//        this.packerTexture.bind();
        glBindTexture(GL_TEXTURE_2D, this.packerTexture.getId());

        this.spritePositions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        this.spriteTexCoords.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_QUADS, 0, this.spriteVertices);

        ARBShaderObjects.glUseProgramObjectARB(0);

        this.textRenderer.render();
    }

    private void update() {
        final List<AlignedElement> elements = this.page.draw();
        final List<AlignedElement> spriteElements = elements.stream()
            .filter(element -> element.getElement() instanceof SpriteMutableElement)
            .collect(Collectors.toList());
        final List<AlignedElement> textElements = elements.stream()
            .filter(element -> element.getElement() instanceof TextMutableElement)
            .collect(Collectors.toList());
        final List<AlignedElement> containerElements = elements.stream()
            .filter(element -> element.getElement() instanceof MutableContainer)
            .collect(Collectors.toList());

        this.updateSprites(spriteElements);
        this.updateText(textElements);
        this.updateOutlines(containerElements);
    }

    private void updateSprites(final List<AlignedElement> elements) {
        final Set<Sprite> sprites = new HashSet<>(TextSprite.SPRITE_MAP.values());

        final int spriteCount = sprites.size();
        for (final AlignedElement alignedElement : elements) {
            final MutableElement element = alignedElement.getElement();
            sprites.add(((SpriteMutableElement) element).getSprite());
        }

        this.packer = new SpritePacker(new HashSet<>(sprites));
        this.packerTexture = Texture.make(this.packer);

        int iPosition = 0;
        final float[] positionsData = new float[spriteCount * 2 * 4];
        int iTexCoord = 0;
        final float[] texCoordsData = new float[spriteCount * 2 * 4];

        for (final AlignedElement alignedElement : elements) {
            final MutableElement element = alignedElement.getElement();
            final Vector2i position = alignedElement.getPosition();
            final int x = position.getX();
            final int y = position.getY();

            if (element instanceof SpriteMutableElement) {
                final Sprite sprite = ((SpriteMutableElement) element).getSprite();
                final int width = sprite.getWidth();
                final int height = sprite.getHeight();

                final Vector2i texCoord = this.packer.getSpritePosition(sprite).orElse(null);
                if (texCoord == null) {
                    System.out.println("A Sprite couldn't be packed by our SpritePacker: " + sprite);
                    continue;
                }

                // TODO: I don't know why I'm multiplying these by 2, but I am.
                // Subtract 1.0f because apparently this is -1 to 1 even though I'm pretty sure the other ones aren't.
                final float[] spritePositions = {
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f)
                };

                for (int j = 0; j < spritePositions.length; j++) {
                    positionsData[iPosition++] = spritePositions[j];
                }

                final float startX = ((float) texCoord.getX()) / this.packer.getWidth();
                final float startY = ((float) texCoord.getY()) / this.packer.getHeight();
                final float endX = ((float) (texCoord.getX() + sprite.getWidth())) / this.packer.getWidth();
                final float endY = ((float) (texCoord.getY() + sprite.getHeight())) / this.packer.getHeight();

                final float[] spriteTexCoords = {
                    startX, startY,
                    endX, startY,
                    endX, endY,
                    startX, endY
                };

                for (int j = 0; j < spriteTexCoords.length; j++) {
                    texCoordsData[iTexCoord++] = spriteTexCoords[j];
                }
            }
        }

        this.spriteVertices = sprites.size() * 4;
        this.spritePositions.bind();
        this.spritePositions.upload(positionsData);
        this.spriteTexCoords.bind();
        this.spriteTexCoords.upload(texCoordsData);
        VertexBuffer.unbind();
    }

    private void updateText(final List<AlignedElement> elements) {
        this.textRenderer.getSprites().clear();
        elements.forEach(element ->
            this.textRenderer.getSprites()
                .put(((TextMutableElement) element.getElement()).getSprite(), element.getPosition()));
        this.textRenderer.update();
    }

    private void updateOutlines(final List<AlignedElement> elements) {
        Collections.reverse(elements);

        int outlines = 0;
        int fills = 0;

        for (final AlignedElement alignedElement : elements) {
            final MutableContainer container = (MutableContainer) alignedElement.getElement();
            if (Page.DEBUG_CONTAINERS || container.getLineColor().isPresent()) outlines++;
            if (container.getFillColor().isPresent()) fills++;
        }

        final int outlineVertices = outlines * 4 * 2;
        final int fillVertices = fills * 4;

        final float[] outlinePositionData = new float[outlineVertices * 2];
        final float[] outlineColorData = new float[outlineVertices * 4];

        final float[] fillPositionData = new float[fillVertices * 2];
        final float[] fillColorData = new float[fillVertices * 4];

        int iOutlinePosition = 0;
        int iOutlineColor = 0;
        int iFillPosition = 0;
        int iFillColor = 0;

        for (final AlignedElement alignedElement : elements) {
            final MutableContainer container = (MutableContainer) alignedElement.getElement();
            // Check if we need to outline
            if (!(Page.DEBUG_CONTAINERS || container.getLineColor().isPresent()) && !container.getFillColor().isPresent()) continue;

            final Vector2i position = alignedElement.getPosition();

            final int x = position.getX();
            final int y = position.getY();

            final int width = container.getSize().getX();
            final int height = container.getSize().getY();

            final float d = 0.5f / 256.0f * 2.0f;

            if (Page.DEBUG_CONTAINERS || container.getLineColor().isPresent()) {
                final float[] outlinePositions = {
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
                    (x - 1 + width) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
                    //
                    (x - 1 + width) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
                    (x - 1 + width) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
                    //
                    (x - 1 + width) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
                    //
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f)
                };
                for (int j = 0; j < outlinePositions.length; j++) {
                    outlinePositionData[iOutlinePosition++] = outlinePositions[j] + d;
                }
                for (int j = 0; j < 4 * 2; j++) {
                    if (Page.DEBUG_CONTAINERS || container.getLineColor().isPresent()) {
                        final Color lineColor = Page.DEBUG_CONTAINERS
                            ? Color.MAGENTA
                            : container.getLineColor().get();
                        outlineColorData[iOutlineColor++] = lineColor.getRed() / 255.0f;
                        outlineColorData[iOutlineColor++] = lineColor.getGreen() / 255.0f;
                        outlineColorData[iOutlineColor++] = lineColor.getBlue() / 255.0f;
                        outlineColorData[iOutlineColor++] = lineColor.getAlpha() / 255.0f;
                    }
                }
            }
            if (container.getFillColor().isPresent()) {
                final float[] fillPositions = {
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f)
                };
                for (int j = 0; j < fillPositions.length; j++) {
                    fillPositionData[iFillPosition++] = fillPositions[j];
                }
                for (int j = 0; j < 4; j++) {
                    if (container.getFillColor().isPresent()) {
                        final Color fillColor = container.getFillColor().get();
                        fillColorData[iFillColor++] = fillColor.getRed() / 255.0f;
                        fillColorData[iFillColor++] = fillColor.getGreen() / 255.0f;
                        fillColorData[iFillColor++] = fillColor.getBlue() / 255.0f;
                        fillColorData[iFillColor++] = fillColor.getAlpha() / 255.0f;
                    }
                }
            }
        }

        this.outlineVertices = outlineVertices;
        this.fillVertices = fillVertices;

        this.outlinePositions.bind();
        this.outlinePositions.upload(outlinePositionData);
        this.outlineColors.bind();
        this.outlineColors.upload(outlineColorData);

        this.fillPositions.bind();
        this.fillPositions.upload(fillPositionData);
        this.fillColors.bind();
        this.fillColors.upload(fillColorData);

        VertexBuffer.unbind();
    }

}
