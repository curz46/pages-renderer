package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.gfx.opengl.TextureArray;
import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.TextSprite;
import me.dylancurzon.dontdie.util.ShaderUtil;
import me.dylancurzon.dontdie.util.Vector2f;
import me.dylancurzon.dontdie.util.Vector2i;
import org.lwjgl.opengl.ARBShaderObjects;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL20.*;

public class TextRenderer implements Renderer {

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
        this.program = ShaderUtil.createShaderProgram("text");
        this.positions = VertexBuffer.make();
        this.textureCoords = VertexBuffer.make();
        this.textureIndices = VertexBuffer.make();

        // TODO: This is too hardcoded
        this.sprites = TextSprite.SPRITE_MAP.values().toArray(new Sprite[]{});
        this.textTextures = TextureArray.make(
            TextSprite.SPRITE_WIDTH,
            TextSprite.SPRITE_HEIGHT,
            this.sprites
        );

        this.update();
    }

    @Override
    public void cleanup() {
        // TODO: Destroy shader
        this.positions.destroy();
        this.textureCoords.destroy();
        this.textureIndices.destroy();
        this.sprites = null;
        this.textTextures.destroy();
    }

    @Override
    public void render() {
        ARBShaderObjects.glUseProgramObjectARB(this.program);
//        this.packerTexture.bind();
//        glBindTexture(GL_TEXTURE_2D, this.textTextures.getId());
        this.textTextures.bind();

        this.positions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        this.textureCoords.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        this.textureIndices.bind();
        glEnableVertexAttribArray(2);
        // TODO: Investigate why GL_INT here doesn't work
        // (makes all characters a Z)
        glVertexAttribPointer(2, 1, GL_FLOAT, false, 0, 0);

        glDrawArrays(GL_QUADS, 0, this.vertices);

        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    public void update() {
        int vertices = 0;
        for (final TextSprite textSprite : this.textSprites.keySet()) {
            vertices += textSprite.getSprites().length * 4;
        }

        final float[] positionsData = new float[vertices * 2];
        final float[] coordsData = new float[vertices * 2];
        final int[] indexData = new int[vertices];

        int iPosition = 0;
        int iCoord = 0;
        int iIndex = 0;

        for (final Map.Entry<TextSprite, Vector2i> entry : this.textSprites.entrySet()) {
            final TextSprite textSprite = entry.getKey();
            final Vector2i spritePosition = entry.getValue();

            for (int i = 0; i < textSprite.getSprites().length; i++) {
                final Sprite sprite = textSprite.getSprites()[i];
                final Vector2f pos1 =
                    this.toClipSpace(
                        spritePosition
                            .add(Vector2i.of((sprite.getWidth() + textSprite.getSpacing()) * i, 0)))
                    .mul(Vector2f.of(1.0f, -1.0f));
                final Vector2f pos2 =
                    this.toClipSpace(
                        spritePosition
                            .add(Vector2i.of((sprite.getWidth() + textSprite.getSpacing()) * i, 0))
                            .add(Vector2i.of(sprite.getWidth(), sprite.getHeight())))
                        .mul(Vector2f.of(1.0f, -1.0f));

                final float[] glPositions = {
                    pos1.getX(), pos1.getY(),
                    pos2.getX(), pos1.getY(),
                    pos2.getX(), pos2.getY(),
                    pos1.getX(), pos2.getY()
                };
                for (final float glPos : glPositions) {
                    positionsData[iPosition++] = glPos;
                }

                final float[] texCoords = {
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f,
                    0.0f, 1.0f
                };
                for (final float texCoord : texCoords) {
                    coordsData[iCoord++] = texCoord;
                }

                final int index = this.getSpriteIndex(sprite);
                for (int j = 0; j < 4; j++) {
                    indexData[iIndex++] = index;
                }
            }
        }

        this.positions.bind();
        this.positions.upload(positionsData);
        this.textureCoords.bind();
        this.textureCoords.upload(coordsData);
        this.textureIndices.bind();
        this.textureIndices.upload(indexData);
        VertexBuffer.unbind();

        this.vertices = vertices;
    }

    public Map<TextSprite, Vector2i> getSprites() {
        return this.textSprites;
    }

    private Vector2f toClipSpace(final Vector2i pixel) {
        final float width = 256.0f;
        final float height = 192.0f;
        return Vector2f.of(
            pixel.getX() / width * 2 - 1.0f,
            pixel.getY() / height * 2 - 1.0f
        );
    }

    private int getSpriteIndex(final Sprite sprite) {
        for (int i = 0; i < this.sprites.length; i++) {
            final Sprite candidate = this.sprites[i];
            if (sprite.equals(candidate)) return i;
        }
        throw new IllegalArgumentException("Sprite given is not a known Text Sprite");
    }

}
