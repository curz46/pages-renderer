package me.dylancurzon.dontdie.gfx.page;

import me.dylancurzon.dontdie.gfx.Renderer;
import me.dylancurzon.dontdie.gfx.opengl.Texture;
import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.SpritePacker;
import me.dylancurzon.dontdie.sprite.TextSpriteProvider;
import me.dylancurzon.dontdie.util.ShaderUtil;
import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.sprite.MutableSpriteElement;
import me.dylancurzon.pages.element.text.MutableTextElement;
import me.dylancurzon.pages.util.Vector2i;
import org.lwjgl.opengl.ARBShaderObjects;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class PageRenderer extends Renderer {

    private final Page page;
    private final TextSpriteProvider[] supportedProviders;

    private int spriteProgram;
    private SpritePacker packer;
    private Texture packerTexture;

    private int spriteVertices = 0;
    private VertexBuffer spritePositions;
    private VertexBuffer spriteTexCoords;
    private VertexBuffer spriteDepths;
    private VertexBuffer spriteBounds;

    // Delegate Text rendering to the TextElementRenderer
    // This way every mutableElement can be rendered a Sprite
    private TextElementRenderer textRenderer;

    private int fillProgram;

    private VertexBuffer outlinePositions;
    private VertexBuffer outlineColors;
    private VertexBuffer outlineDepths;
    private VertexBuffer outlineBounds;
    private int outlineVertices = 0;

    private VertexBuffer fillPositions;
    private VertexBuffer fillColors;
    private VertexBuffer fillDepths;
    private VertexBuffer fillBounds;
    private int fillVertices = 0;

    public PageRenderer(Page page, TextSpriteProvider... supportedProviders) {
        this.page = page;
        this.supportedProviders = supportedProviders;
    }

    @Override
    public void prepare() {
        spriteProgram = ShaderUtil.createShaderProgram("page_sprite");

        spritePositions = VertexBuffer.make();
        spriteTexCoords = VertexBuffer.make();
        spriteDepths = VertexBuffer.make();
        spriteBounds = VertexBuffer.make();

        fillProgram = ShaderUtil.createShaderProgram("page_fill");

        outlinePositions = VertexBuffer.make();
        outlineColors = VertexBuffer.make();
        outlineDepths = VertexBuffer.make();
        outlineBounds = VertexBuffer.make();

        fillPositions = VertexBuffer.make();
        fillColors = VertexBuffer.make();
        fillDepths = VertexBuffer.make();
        fillBounds = VertexBuffer.make();

        textRenderer = new TextElementRenderer(supportedProviders);
        textRenderer.prepare();

        update();
    }

    @Override
    public void cleanup() {
        spritePositions.destroy();
        spriteTexCoords.destroy();
        spriteDepths.destroy();
        spriteBounds.destroy();

        outlinePositions.destroy();
        outlineColors.destroy();
        outlineDepths.destroy();
        outlineBounds.destroy();

        fillPositions.destroy();
        fillColors.destroy();
        fillDepths.destroy();
        fillBounds.destroy();

        spritePositions = null;
        spriteTexCoords = null;
        spriteDepths = null;
        spriteBounds = null;

        outlinePositions = null;
        outlineColors = null;
        outlineDepths = null;
        outlineBounds = null;

        fillPositions = null;
        fillColors = null;
        fillDepths = null;
        fillBounds = null;

        // TODO: Destroy shader programs
    }

    @Override
    public void render() {
        glClearDepth(0.0f);
        glClear(GL_DEPTH_BUFFER_BIT);

//        glEnable(GL_DEPTH_TEST);
//        glDepthFunc(GL_GEQUAL);

        glEnable(GL_DEPTH_TEST);
        glEnable(GL_ALPHA_TEST);
        glDepthFunc(GL_GEQUAL);
        glAlphaFunc(GL_GREATER, 0);

        textRenderer.render();

        ARBShaderObjects.glUseProgramObjectARB(fillProgram);

        outlinePositions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        outlineColors.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
        outlineDepths.bind();
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, 0, 0);
        outlineBounds.bind();
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, 0, 0);

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
        spriteDepths.bind();
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, 0, 0);
        spriteBounds.bind();
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_QUADS, 0, spriteVertices);

        ARBShaderObjects.glUseProgramObjectARB(0);
        ARBShaderObjects.glUseProgramObjectARB(fillProgram);

        fillPositions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        fillColors.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 4, GL_FLOAT, false, 0, 0);
        fillDepths.bind();
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, 0, 0);
        fillBounds.bind();
        glEnableVertexAttribArray(3);
        glVertexAttribPointer(3, 4, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_QUADS, 0, fillVertices);

        ARBShaderObjects.glUseProgramObjectARB(0);

//        glDisable(GL_DEPTH_TEST);

        glDisable(GL_DEPTH_TEST);
        glDisable(GL_ALPHA_TEST);
    }

    @Override
    public void update() {
        if (page == null) {
            updateSprites(Collections.emptyList());
            updateText(Collections.emptyList());
            updateOutlines(Collections.emptyList());
        } else {
            Map<MutableElement, Vector2i> flattened = page.flatten();
            List<FlattenedElement> elements = flattened.entrySet().stream()
                .filter(entry -> entry.getKey().isVisible())
                .map(entry -> new FlattenedElement(entry.getValue(), entry.getKey(), null))
                .collect(Collectors.toList());
            // Set parent FlattenedElement afterwards now it is accessible
            elements.stream()
                .filter(element -> element.getMutableElement().getParent() != null)
                .forEach(element -> elements.stream()
                    .filter(parentCandidate -> element.getMutableElement().getParent().equals(parentCandidate.getMutableElement()))
                    .forEach(element::setParentElement));
            List<FlattenedElement> spriteElements = elements.stream()
                .filter(element -> element.getMutableElement() instanceof MutableSpriteElement)
                .collect(Collectors.toList());
            List<FlattenedElement> textElements = elements.stream()
                .filter(element -> element.getMutableElement() instanceof MutableTextElement)
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
            MutableElement mutableElement = element.getMutableElement();
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
        int iDepth = 0;
        float[] depthData = new float[elementCount * 4];
        int iBounds = 0;
        float[] boundsData = new float[elementCount * 4 * 4];

        for (FlattenedElement flattenedElement : elements) {
            MutableElement mutableElement = flattenedElement.getMutableElement();
            Vector2i position = flattenedElement.getPosition();
            int x = position.getX();
            int y = position.getY();

            // TODO: Hardcoding virtual size!
            Vector2i boundA = flattenedElement.getBoundA().orElse(Vector2i.of(0, 0));
            Vector2i boundB = flattenedElement.getBoundB().orElse(Vector2i.of(256, 192));
//            float[] bounds = toClipSpace(boundA, boundB);
            // TODO: I have no idea why 192 - Y is necessary, but based on testing this is correct.
            float[] bounds = {boundA.getX(), 192 - boundB.getY(), boundB.getX(), 192 - boundA.getY()};

            if (mutableElement instanceof MutableSpriteElement) {
                Sprite sprite = (Sprite) ((MutableSpriteElement) mutableElement).getSprite();
                int width = mutableElement.getSize().getX();
                int height = mutableElement.getSize().getY();

                Vector2i texCoord = packer.getSpritePosition(sprite).orElse(null);
                if (texCoord == null) {
//                    System.out.println("A Sprite couldn't be packed by our SpritePacker: " + sprite);
                    continue;
                }

                // TODO: I don't know why I'm multiplying these by 2, but I am.
                // Subtract 1.0f because apparently this is -1 to 1 even though I'm pretty sure the other ones aren't.
//                float[] spritePositions = {
//                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
//                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
//                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
//                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f)
//                };
                float[][] spritePositions = {
                    toClipSpace(Vector2i.of(x, y)),
                    toClipSpace(Vector2i.of(x + width, y)),
                    toClipSpace(Vector2i.of(x + width, y + height)),
                    toClipSpace(Vector2i.of(x, y + height))
                };

                for (int j = 0; j < spritePositions.length; j++) {
                    for (int k = 0; k < 2; k++) {
                        positionsData[iPosition++] = spritePositions[j][k];
                    }
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

                float depth = mutableElement.getZIndex() / 100.0f;
                for (int j = 0; j < 4; j++) {
                    depthData[iDepth++] = depth;
                }

                // 4 vertices for each Sprite
                for (int j = 0; j < 4; j++) {
                    // 4 floats for each Vertex
                    for (int k = 0; k < 4; k++) {
                        boundsData[iBounds++] = bounds[k];
                    }
                }
            }
        }

        spriteVertices = elementCount * 4;
        spritePositions.bind();
        spritePositions.upload(positionsData);
        spriteTexCoords.bind();
        spriteTexCoords.upload(texCoordsData);
        spriteDepths.bind();
        spriteDepths.upload(depthData);
        spriteBounds.bind();
        spriteBounds.upload(boundsData);
        VertexBuffer.unbind();
    }

    private void updateText(List<FlattenedElement> elements) {
        textRenderer.clearElements();
        elements.forEach(textRenderer::addElement);
        textRenderer.update();
    }

    public static final boolean DEBUG_CONTAINERS = false;

    private void updateOutlines(List<FlattenedElement> elements) {
        Collections.reverse(elements);

        int outlines = 0;
        int fills = 0;

        for (FlattenedElement element : elements) {
            MutableElement mutableElement = element.getMutableElement();
            if (DEBUG_CONTAINERS || mutableElement.getDecoration().getLineColor().isPresent()) outlines++;
            if (mutableElement.getDecoration().getFillColor().isPresent()) fills++;
        }

        int outlineVertices = outlines * 4 * 2;
        int fillVertices = fills * 4;

        float[] outlinePositionData = new float[outlineVertices * 2];
        float[] outlineColorData = new float[outlineVertices * 4];
        float[] outlineDepthData = new float[outlineVertices];
        float[] outlineBoundsData = new float[outlineVertices * 4];

        float[] fillPositionData = new float[fillVertices * 2];
        float[] fillColorData = new float[fillVertices * 4];
        float[] fillDepthData = new float[fillVertices];
        float[] fillBoundsData = new float[fillVertices * 4];

        int iOutlinePosition = 0;
        int iOutlineColor = 0;
        int iOutlineDepth = 0;
        int iOutlineBounds = 0;
        int iFillPosition = 0;
        int iFillColor = 0;
        int iFillDepth = 0;
        int iFillBounds = 0;

        for (FlattenedElement flattenedElement : elements) {
            MutableElement mutableElement = flattenedElement.getMutableElement();
            // Check if we need to outline
            if (!(DEBUG_CONTAINERS
                || mutableElement.getDecoration().getLineColor().isPresent())
                && !mutableElement.getDecoration().getFillColor().isPresent()) continue;

            Vector2i position = flattenedElement.getPosition();

            int x = position.getX();
            int y = position.getY();
            // When DEBUG_CONTAINERS is enabled show above other elements
            float depth = DEBUG_CONTAINERS ? 1.0f : mutableElement.getZIndex() / 100.0f;

            int width = mutableElement.getSize().getX();
            int height = mutableElement.getSize().getY();

            Vector2i boundA = flattenedElement.getBoundA().orElse(Vector2i.of(0, 0));
            Vector2i boundB = flattenedElement.getBoundB().orElse(Vector2i.of(256, 192));
            // TODO: I have no idea why 192 - Y is necessary, but based on testing this is correct.
            float[] bounds = {boundA.getX(), 192 - boundB.getY(), boundB.getX(), 192 - boundA.getY()};
//            float[] bounds = toClipSpace(boundA, boundB);

            if (DEBUG_CONTAINERS || mutableElement.getDecoration().getLineColor().isPresent()) {
//                float[] outlinePositions = {
//                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
//                    (x - 1 + width) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
//                    //
//                    (x - 1 + width) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
//                    (x - 1 + width) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
//                    //
//                    (x - 1 + width) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
//                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
//                    //
//                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
//                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f)
//                };
                // TODO: Why are we taking away 1 here??
                float[][] outlinePositions = {
                    // Line
                    toClipSpace(Vector2i.of(x, y)),
                    toClipSpace(Vector2i.of(x + width - 1, y)),
                    // Line
                    toClipSpace(Vector2i.of(x + width - 1, y)),
                    toClipSpace(Vector2i.of(x + width - 1, y + height)),
                    // Line
                    toClipSpace(Vector2i.of(x + width - 1, y + height)),
                    toClipSpace(Vector2i.of(x, y + height)),
                    // Line
                    toClipSpace(Vector2i.of(x, y + height)),
                    toClipSpace(Vector2i.of(x, y)),
                };
                for (int j = 0; j < outlinePositions.length; j++) {
                    for (int k = 0; k < 2; k++) {
                        // TODO: Is this really necessary?
                        float d = 0.5f / 256.0f * 2.0f;
                        outlinePositionData[iOutlinePosition++] = outlinePositions[j][k] + d;
                    }
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
                for (int j = 0; j < 4; j++) {
                    outlineDepthData[iOutlineDepth++] = DEBUG_CONTAINERS ? 1.0f : depth;
                }
                // For each Vertex (8 per outline -- 4sides and 2 per side)
                for (int i = 0; i < 4 * 2; i++) {
                    // For each bound (boundA, boundB, two dimensions per bound)
                    for (int j = 0; j < 4; j++) {
                        outlineBoundsData[iOutlineBounds++] = bounds[j];
                    }
                }
            }
            if (mutableElement.getDecoration().getFillColor().isPresent()) {
//                float[] fillPositions = {
//                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
//                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
//                    (x + width) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f),
//                    (x + 0) / 256.0f * 2.0f - 1.0f, -((y + height) / 192.0f * 2.0f - 1.0f)
//                };
                float[][] fillPositions = {
                    toClipSpace(Vector2i.of(x, y)),
                    toClipSpace(Vector2i.of(x + width, y)),
                    toClipSpace(Vector2i.of(x + width, y + height)),
                    toClipSpace(Vector2i.of(x, y + height))
                };
                for (int j = 0; j < fillPositions.length; j++) {
                    for (int k = 0; k < 2; k++) {
                        fillPositionData[iFillPosition++] = fillPositions[j][k];
                    }
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
                for (int j = 0; j < 4; j++) {
                    fillDepthData[iFillDepth++] = depth;
                }
                // For each Vertex (4 per fill -- 4 sides)
                for (int i = 0; i < 4; i++) {
                    // For each bound (boundA, boundB, two dimensions per bound)
                    for (int j = 0; j < 4; j++) {
                        fillBoundsData[iFillBounds++] = bounds[j];
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
        outlineDepths.bind();
        outlineDepths.upload(outlineDepthData);
        outlineBounds.bind();
        outlineBounds.upload(outlineBoundsData);

        fillPositions.bind();
        fillPositions.upload(fillPositionData);
        fillColors.bind();
        fillColors.upload(fillColorData);
        fillDepths.bind();
        fillDepths.upload(fillDepthData);
        fillBounds.bind();
        fillBounds.upload(fillBoundsData);

        VertexBuffer.unbind();
    }

    private float[] toClipSpace(Vector2i boundA, Vector2i boundB) {
        float[] clipA = toClipSpace(boundA);
        float[] clipB = toClipSpace(boundB);
        return new float[] {
            clipA[0], clipA[1],
            clipB[0], clipB[1]
        };
    }

    private float[] toClipSpace(Vector2i virtualPosition) {
        int x = virtualPosition.getX();
        int y = virtualPosition.getY();
        return new float[]{
            (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
        };
    }

}
