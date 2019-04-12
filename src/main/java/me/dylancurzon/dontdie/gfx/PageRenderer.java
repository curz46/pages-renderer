package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.gfx.opengl.Texture;
import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.SpritePacker;
import me.dylancurzon.dontdie.sprite.TextSprite;
import me.dylancurzon.dontdie.util.ShaderUtil;
import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.sprite.MutableSpriteElement;
import me.dylancurzon.pages.element.text.MutableTextElement;
import me.dylancurzon.pages.util.Vector2i;
import org.lwjgl.opengl.ARBShaderObjects;

import java.awt.*;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class PageRenderer extends Renderer {

    private final Page page;

    private int spriteProgram;
    private SpritePacker packer;
    private Texture packerTexture;

    private int spriteVertices = 0;
    private VertexBuffer spritePositions;
    private VertexBuffer spriteTexCoords;

    // Delegate Text rendering to the TextRenderer
    // This way every mutableElement can be rendered a Sprite
    private TextRenderer textRenderer;

    private int fillProgram;

    private VertexBuffer outlinePositions;
    private VertexBuffer outlineColors;
    private int outlineVertices = 0;

    private VertexBuffer fillPositions;
    private VertexBuffer fillColors;
    private int fillVertices = 0;

    public PageRenderer(Page page) {
        this.page = page;
    }

    @Override
    public void prepare() {
        spriteProgram = ShaderUtil.createShaderProgram("page");
        spritePositions = VertexBuffer.make();
        spriteTexCoords = VertexBuffer.make();

        fillProgram = ShaderUtil.createShaderProgram("fill");

        outlinePositions = VertexBuffer.make();
        outlineColors = VertexBuffer.make();
        fillPositions = VertexBuffer.make();
        fillColors = VertexBuffer.make();

        textRenderer = new TextRenderer();
        textRenderer.prepare();

        update();
    }

    @Override
    public void cleanup() {
        spritePositions.destroy();
        spriteTexCoords.destroy();

        outlinePositions.destroy();
        outlineColors.destroy();

        fillPositions.destroy();
        fillColors.destroy();

        spritePositions = null;
        spriteTexCoords = null;

        outlinePositions = null;
        outlineColors = null;

        fillPositions = null;
        fillColors = null;
    }

    @Override
    public void render() {
        ARBShaderObjects.glUseProgramObjectARB(fillProgram);

        fillPositions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        fillColors.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_QUADS, 0, fillVertices);

        ARBShaderObjects.glUseProgramObjectARB(0);
        ARBShaderObjects.glUseProgramObjectARB(fillProgram);

        outlinePositions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        outlineColors.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_LINES, 0, outlineVertices);

        ARBShaderObjects.glUseProgramObjectARB(0);
        ARBShaderObjects.glUseProgramObjectARB(spriteProgram);
//        this.packerTexture.bind();
        glBindTexture(GL_TEXTURE_2D, packerTexture.getId());

        spritePositions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        spriteTexCoords.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_QUADS, 0, spriteVertices);

        ARBShaderObjects.glUseProgramObjectARB(0);

        textRenderer.render();
    }

    @Override
    public void update() {
        if (page == null) {
            updateSprites(Collections.emptyList());
            updateText(Collections.emptyList());
            updateOutlines(Collections.emptyList());
        } else {
            List<FlattenedElement> elements = page.flatten().entrySet().stream()
                .map(entry -> new FlattenedElement(entry.getValue(), entry.getKey()))
                .collect(Collectors.toList());
            List<FlattenedElement> spriteElements = elements.stream()
                .filter(element -> element.mutableElement instanceof MutableSpriteElement)
                .collect(Collectors.toList());
            List<FlattenedElement> textElements = elements.stream()
                .filter(element -> element.mutableElement instanceof MutableTextElement)
                .collect(Collectors.toList());

            updateSprites(spriteElements);
            updateText(textElements);
            updateOutlines(elements);
        }
    }

    private void updateSprites(List<FlattenedElement> elements) {
        Set<Sprite> sprites = new HashSet<>();

        int elementCount = elements.size();
        for (FlattenedElement element : elements) {
            MutableElement mutableElement = element.mutableElement;
            // Duplicate Sprites here won't be added twice, because Sets ignore duplicate elements
            sprites.add((Sprite) ((MutableSpriteElement) mutableElement).getSprite());
        }

        packer = new SpritePacker(new HashSet<>(sprites));
        if (packerTexture != null) packerTexture.destroy();
        packerTexture = Texture.make(packer);

        int iPosition = 0;
        float[] positionsData = new float[elementCount * 2 * 4];
        int iTexCoord = 0;
        float[] texCoordsData = new float[elementCount * 2 * 4];

        for (FlattenedElement element : elements) {
            MutableElement mutableElement = element.mutableElement;
            Vector2i position = element.position;
            int x = position.getX();
            int y = position.getY();

            if (mutableElement instanceof MutableSpriteElement) {
                Sprite sprite = (Sprite) ((MutableSpriteElement) mutableElement).getSprite();
                int width = sprite.getWidth();
                int height = sprite.getHeight();

                Vector2i texCoord = packer.getSpritePosition(sprite).orElse(null);
                if (texCoord == null) {
//                    System.out.println("A Sprite couldn't be packed by our SpritePacker: " + sprite);
                    continue;
                }

                // TODO: I don't know why I'm multiplying these by 2, but I am.
                // Subtract 1.0f because apparently this is -1 to 1 even though I'm pretty sure the other ones aren't.
                float[] spritePositions = {
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f)
                };

                for (int j = 0; j < spritePositions.length; j++) {
                    positionsData[iPosition++] = spritePositions[j];
                }

                float startX = ((float) texCoord.getX()) / packer.getWidth();
                float startY = ((float) texCoord.getY()) / packer.getHeight();
                float endX = ((float) (texCoord.getX() + sprite.getWidth())) / packer.getWidth();
                float endY = ((float) (texCoord.getY() + sprite.getHeight())) / packer.getHeight();

                float[] spriteTexCoords = {
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

        spriteVertices = elementCount * 4;
        spritePositions.bind();
        spritePositions.upload(positionsData);
        spriteTexCoords.bind();
        spriteTexCoords.upload(texCoordsData);
        VertexBuffer.unbind();
    }

    private void updateText(List<FlattenedElement> elements) {
        textRenderer.getSprites().clear();
        elements.forEach(element ->
            textRenderer.getSprites()
                .put((TextSprite) ((MutableTextElement) element.mutableElement).getSprite(), element.position));
        textRenderer.update();
    }

    public static final boolean DEBUG_CONTAINERS = false;

    private void updateOutlines(List<FlattenedElement> elements) {
        Collections.reverse(elements);

        int outlines = 0;
        int fills = 0;

        for (FlattenedElement element : elements) {
            MutableElement mutableElement = element.mutableElement;
            if (DEBUG_CONTAINERS || mutableElement.getDecoration().getLineColor().isPresent()) outlines++;
            if (mutableElement.getDecoration().getFillColor().isPresent()) fills++;
        }

        int outlineVertices = outlines * 4 * 2;
        int fillVertices = fills * 4;

        float[] outlinePositionData = new float[outlineVertices * 2];
        float[] outlineColorData = new float[outlineVertices * 4];

        float[] fillPositionData = new float[fillVertices * 2];
        float[] fillColorData = new float[fillVertices * 4];

        int iOutlinePosition = 0;
        int iOutlineColor = 0;
        int iFillPosition = 0;
        int iFillColor = 0;

        for (FlattenedElement element : elements) {
            MutableElement mutableElement = element.mutableElement;
            // Check if we need to outline
            if (!(DEBUG_CONTAINERS
                || mutableElement.getDecoration().getLineColor().isPresent())
                && !mutableElement.getDecoration().getFillColor().isPresent()) continue;

            Vector2i position = element.position;

            int x = position.getX();
            int y = position.getY();

            int width = mutableElement.getSize().getX();
            int height = mutableElement.getSize().getY();

            float d = 0.5f / 256.0f * 2.0f;

            if (DEBUG_CONTAINERS || mutableElement.getDecoration().getLineColor().isPresent()) {
                float[] outlinePositions = {
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
                    if (DEBUG_CONTAINERS || mutableElement.getDecoration().getLineColor().isPresent()) {
                        Color lineColor = DEBUG_CONTAINERS
                            ? Color.MAGENTA
                            : mutableElement.getDecoration().getLineColor().get();
                        outlineColorData[iOutlineColor++] = lineColor.getRed() / 255.0f;
                        outlineColorData[iOutlineColor++] = lineColor.getGreen() / 255.0f;
                        outlineColorData[iOutlineColor++] = lineColor.getBlue() / 255.0f;
                        outlineColorData[iOutlineColor++] = lineColor.getAlpha() / 255.0f;
                    }
                }
            }
            if (mutableElement.getDecoration().getFillColor().isPresent()) {
                float[] fillPositions = {
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f)
                };
                for (int j = 0; j < fillPositions.length; j++) {
                    fillPositionData[iFillPosition++] = fillPositions[j];
                }
                for (int j = 0; j < 4; j++) {
                    if (mutableElement.getDecoration().getFillColor().isPresent()) {
                        Color fillColor = mutableElement.getDecoration().getFillColor().get();
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

        outlinePositions.bind();
        outlinePositions.upload(outlinePositionData);
        outlineColors.bind();
        outlineColors.upload(outlineColorData);

        fillPositions.bind();
        fillPositions.upload(fillPositionData);
        fillColors.bind();
        fillColors.upload(fillColorData);

        VertexBuffer.unbind();
    }

    private static class FlattenedElement {

        protected final Vector2i position;
        protected final MutableElement mutableElement;

        public FlattenedElement(Vector2i position, MutableElement mutableElement) {
            this.position = position;
            this.mutableElement = mutableElement;
        }

    }

}
