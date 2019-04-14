package me.dylancurzon.dontdie.gfx.page;

import me.dylancurzon.dontdie.gfx.Renderer;
import me.dylancurzon.dontdie.gfx.opengl.TextureArray;
import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.TextSprite;
import me.dylancurzon.dontdie.util.ShaderUtil;
import me.dylancurzon.pages.element.text.MutableTextElement;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import org.lwjgl.opengl.ARBShaderObjects;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class PageTextRenderer extends Renderer {

//    private final Map<TextSprite, Vector2i> textSprites = new HashMap<>();
    private final List<FlattenedElement> flattenedElements = new ArrayList<>();

    private int program;

    private Sprite[] sprites;
    private TextureArray textTextures;

    private VertexBuffer positions;
    private VertexBuffer textureCoords;
    private VertexBuffer textureIndices;
    private VertexBuffer depthBuffer;
    private VertexBuffer boundsBuffer;

    private int vertices;

    public void addTextElement(FlattenedElement textElement) {
        if (!(textElement.getMutableElement() instanceof MutableTextElement)) {
            throw new IllegalArgumentException(
                "Argument textElement#getMutableElement doesn ot refer to a MutableTextElement: "
                    + textElement.getMutableElement());
        }
        flattenedElements.add(textElement);
    }

    public void clearTextElements() {
        flattenedElements.clear();
    }

    @Override
    public void prepare() {
        program = ShaderUtil.createShaderProgram("page_text");
        positions = VertexBuffer.make();
        textureCoords = VertexBuffer.make();
        textureIndices = VertexBuffer.make();
        depthBuffer = VertexBuffer.make();
        boundsBuffer = VertexBuffer.make();

        // TODO: This is too hardcoded
        sprites = TextSprite.SPRITE_MAP.values().toArray(new Sprite[]{});
        textTextures = TextureArray.make(
            TextSprite.SPRITE_WIDTH,
            TextSprite.SPRITE_HEIGHT,
            sprites
        );

        update();
    }

    @Override
    public void cleanup() {
        // TODO: Destroy shader
        positions.destroy();
        textureCoords.destroy();
        textureIndices.destroy();
        depthBuffer.destroy();
        boundsBuffer.destroy();
        sprites = null;
        textTextures.destroy();
    }

    @Override
    public void render() {
        ARBShaderObjects.glUseProgramObjectARB(program);
//        this.packerTexture.bind();
//        glBindTexture(GL_TEXTURE_2D, this.textTextures.getId());
        textTextures.bind();

        positions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        textureCoords.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        textureIndices.bind();
        glEnableVertexAttribArray(2);
        // TODO: Investigate why GL_INT here doesn't work
        // (makes all characters a Z)
        glVertexAttribPointer(2, 1, GL_FLOAT, false, 0, 0);
        depthBuffer.bind();
        glEnableVertexAttribArray(3);
        // TODO: I'm just making this float as well because debugging this would probably take several hours which
        //       I don't have.
        glVertexAttribPointer(3, 1, GL_FLOAT, false, 0, 0);
        boundsBuffer.bind();
        glEnableVertexAttribArray(4);
        glVertexAttribPointer(4, 4, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_QUADS, 0, vertices);

        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    public void update() {
        int vertices = 0;
        for (FlattenedElement flattenedElement : flattenedElements) {
            MutableTextElement textElement = ((MutableTextElement) flattenedElement.getMutableElement());
            TextSprite textSprite = (TextSprite) textElement.getSprite();
            vertices += textSprite.getSprites().length * 4;
        }

        float[] positionsData = new float[vertices * 2];
        float[] coordsData = new float[vertices * 2];
        int[] indexData = new int[vertices];
        float[] depthData = new float[vertices];
        float[] boundsData = new float[vertices * 4];

        int iPosition = 0;
        int iCoord = 0;
        int iIndex = 0;
        int iDepth = 0;
        int iBounds = 0;

        for (FlattenedElement flattenedElement : flattenedElements) {
            MutableTextElement textElement = ((MutableTextElement) flattenedElement.getMutableElement());
            TextSprite textSprite = (TextSprite) textElement.getSprite();
            Vector2i spritePosition = flattenedElement.getPosition();

            // TODO: Hardcoding virtual size!
            Vector2i boundA = flattenedElement.getBoundA().orElse(Vector2i.of(0, 0));
            Vector2i boundB = flattenedElement.getBoundB().orElse(Vector2i.of(256, 192));
            // TODO: I have no idea why 192 - Y is necessary, but based on testing this is correct.
            float[] bounds = {boundA.getX(), 192 - boundB.getY(), boundB.getX(), 192 - boundA.getY()};
//            float[] bounds = toClipSpace(boundA, boundB);

            for (int i = 0; i < textSprite.getSprites().length; i++) {
                Sprite sprite = textSprite.getSprites()[i];
//                Vector2d pos1 =
//                    toClipSpace(
//                        spritePosition
//                            .add(Vector2i.of((sprite.getWidth() + textSprite.getSpacing()) * i, 0)))
//                    .mul(Vector2d.of(1.0f, -1.0f));
//                Vector2d pos2 =
//                    toClipSpace(
//                        spritePosition
//                            .add(Vector2i.of((sprite.getWidth() + textSprite.getSpacing()) * i, 0))
//                            .add(Vector2i.of(sprite.getWidth(), sprite.getHeight())))
//                        .mul(Vector2d.of(1.0f, -1.0f));
//
//                float[] glPositions = {
//                    (float) pos1.getX(), (float) pos1.getY(),
//                    (float) pos2.getX(), (float) pos1.getY(),
//                    (float) pos2.getX(), (float) pos2.getY(),
//                    (float) pos1.getX(), (float) pos2.getY()
//                };
//                for (float glPos : glPositions) {
//                    positionsData[iPosition++] = glPos;
//                }
                float[][] fillPositions = {
                    toClipSpace(spritePosition),
                    toClipSpace(spritePosition.add(Vector2i.of(
                        sprite.getWidth() + textSprite.getSpacing() * i,
                        0
                    ))),
                    toClipSpace(spritePosition.add(Vector2i.of(
                        sprite.getWidth() + textSprite.getSpacing() * i,
                        sprite.getHeight()
                    ))),
                    toClipSpace(spritePosition.add(Vector2i.of(
                        0,
                        sprite.getHeight()
                    )))
                };
                for (int j = 0; j < fillPositions.length; j++) {
                    for (int k = 0; k < 2; k++) {
                        positionsData[iPosition++] = fillPositions[j][k];
                    }
                }

                float[] texCoords = {
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f,
                    0.0f, 1.0f
                };
                for (float texCoord : texCoords) {
                    coordsData[iCoord++] = texCoord;
                }

                int index = getSpriteIndex(sprite);
                for (int j = 0; j < 4; j++) {
                    indexData[iIndex++] = index;
                }

                float depth = textElement.getZIndex() / 100.f;
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

        positions.bind();
        positions.upload(positionsData);
        textureCoords.bind();
        textureCoords.upload(coordsData);
        textureIndices.bind();
        textureIndices.upload(indexData);
        depthBuffer.bind();
        depthBuffer.upload(depthData);
        boundsBuffer.bind();
        boundsBuffer.upload(boundsData);
        VertexBuffer.unbind();

        this.vertices = vertices;
    }

    private float[] toClipSpace(Vector2i boundA, Vector2i boundB) {
        float[] clipA = toClipSpace(boundA);
        float[] clibB = toClipSpace(boundB);
        return new float[] {
            clipA[0], clipA[1],
            clibB[0], clibB[1]
        };
    }

    private float[] toClipSpace(Vector2i virtualPosition) {
        int x = virtualPosition.getX();
        int y = virtualPosition.getY();
        return new float[]{
            (x + 0) / 256.0f * 2.0f - 1.0f, -((y + 0) / 192.0f * 2.0f - 1.0f),
        };
    }

    private int getSpriteIndex(Sprite sprite) {
        for (int i = 0; i < sprites.length; i++) {
            Sprite candidate = sprites[i];
            if (sprite.equals(candidate)) return i;
        }
        throw new IllegalArgumentException("Sprite given is not a known Text Sprite");
    }

}
