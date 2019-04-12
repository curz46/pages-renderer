package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.gfx.opengl.TextureArray;
import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.TextSprite;
import me.dylancurzon.dontdie.util.ShaderUtil;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import org.lwjgl.opengl.ARBShaderObjects;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class TextRenderer extends Renderer {

    private final Map<TextSprite, Vector2i> textSprites = new HashMap<>();

    private int program;

    private Sprite[] sprites;
    private TextureArray textTextures;

    private VertexBuffer positions;
    private VertexBuffer textureCoords;
    private VertexBuffer textureIndices;

    private int vertices;

    @Override
    public void prepare() {
        program = ShaderUtil.createShaderProgram("text");
        positions = VertexBuffer.make();
        textureCoords = VertexBuffer.make();
        textureIndices = VertexBuffer.make();

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

        glDrawArrays(GL_QUADS, 0, vertices);

        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    public void update() {
        int vertices = 0;
        for (TextSprite textSprite : textSprites.keySet()) {
            vertices += textSprite.getSprites().length * 4;
        }

        float[] positionsData = new float[vertices * 2];
        float[] coordsData = new float[vertices * 2];
        int[] indexData = new int[vertices];

        int iPosition = 0;
        int iCoord = 0;
        int iIndex = 0;

        for (Map.Entry<TextSprite, Vector2i> entry : textSprites.entrySet()) {
            TextSprite textSprite = entry.getKey();
            Vector2i spritePosition = entry.getValue();

            for (int i = 0; i < textSprite.getSprites().length; i++) {
                Sprite sprite = textSprite.getSprites()[i];
                Vector2d pos1 =
                    toClipSpace(
                        spritePosition
                            .add(Vector2i.of((sprite.getWidth() + textSprite.getSpacing()) * i, 0)))
                    .mul(Vector2d.of(1.0f, -1.0f));
                Vector2d pos2 =
                    toClipSpace(
                        spritePosition
                            .add(Vector2i.of((sprite.getWidth() + textSprite.getSpacing()) * i, 0))
                            .add(Vector2i.of(sprite.getWidth(), sprite.getHeight())))
                        .mul(Vector2d.of(1.0f, -1.0f));

                float[] glPositions = {
                    (float) pos1.getX(), (float) pos1.getY(),
                    (float) pos2.getX(), (float) pos1.getY(),
                    (float) pos2.getX(), (float) pos2.getY(),
                    (float) pos1.getX(), (float) pos2.getY()
                };
                for (float glPos : glPositions) {
                    positionsData[iPosition++] = glPos;
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
            }
        }

        positions.bind();
        positions.upload(positionsData);
        textureCoords.bind();
        textureCoords.upload(coordsData);
        textureIndices.bind();
        textureIndices.upload(indexData);
        VertexBuffer.unbind();

        this.vertices = vertices;
    }

    public Map<TextSprite, Vector2i> getSprites() {
        return textSprites;
    }

    private Vector2d toClipSpace(Vector2i pixel) {
        float width = 256.0f;
        float height = 192.0f;
        return Vector2d.of(
            pixel.getX() / width * 2 - 1.0f,
            pixel.getY() / height * 2 - 1.0f
        );
    }

    private int getSpriteIndex(Sprite sprite) {
        for (int i = 0; i < sprites.length; i++) {
            Sprite candidate = sprites[i];
            if (sprite.equals(candidate)) return i;
        }
        throw new IllegalArgumentException("Sprite given is not a known Text Sprite");
    }

}
