package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.Sprites;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.dontdie.tile.TileType;
import me.dylancurzon.dontdie.util.ShaderUtil;
import me.dylancurzon.dontdie.util.Vector2d;
import me.dylancurzon.dontdie.util.Vector2i;
import org.lwjgl.opengl.ARBShaderObjects;

import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

/**
 * The TileRenderer is responsible for rendering the tilemap of the Level to the display. It does this by providing the
 * GPU with a few pieces of data...
 * in:
 * - vec2 (x, y)
 * - texture_2d_array
 * - texture coordinate
 * - current frame (assume tex_coord + frame = index)
 * uniform:
 * - Camera fixed position and delta
 */
public class TileRenderer implements Renderer {

    private final GameCamera camera;
    private final Level level;

    private int shaderProgram;

    private final Map<Sprite, Integer> spriteIndexMap = new HashMap<>();
    private int spritemapId;

    private int vertices;
    private VertexBuffer positions;
    private VertexBuffer texCoord;
    private VertexBuffer texIndex;
//    private VertexBuffer currentFrame;

    private Vector2d cameraFixed;
    private Vector2d cameraSize;
    private Vector2d cameraDelta;

    public TileRenderer(final GameCamera camera, final Level level) {
        this.camera = camera;
        this.level = level;
    }

    /**
     * Whenever the Camera tilemap is fully updated, the data in each of the VertexBuffers needs to be updated with the
     * new visible tilemap, as well as the uniform values.
     */
    public void tilemapUpdate() {
        final Vector2i pointA = this.camera.getVisibleA().sub(2);
        final Vector2i pointB = this.camera.getVisibleB().add(2);

        final int numX = pointB.getX() - pointA.getX();
        final int numY = pointB.getY() - pointA.getY();
        final int numTiles = numX * numY;

        final float[] positions = new float[numTiles * 2 * 4];
        final float[] texCoords = new float[numTiles * 2 * 4];
        final float[] texIndex = new float[numTiles * 4];

        int iPos = 0;
        int iCoords = 0;
        int iIndex = 0;
        for (int x = pointA.getX(); x < pointB.getX();  x++) {
            for (int y = pointA.getY(); y < pointB.getY(); y++) {
                final float[] pos = {
                    x, y,
                    x + 1, y,
                    x + 1, y + 1,
                    x, y + 1
                };
                for (int j = 0; j < pos.length; j++) {
                    positions[iPos++] = pos[j];
                }
    
                final float[] coords = {
                    0.0f, 0.0f,
                    1.0f, 0.0f,
                    1.0f, 1.0f,
                    0.0f, 1.0f
                };
                for (int j = 0; j < coords.length; j++) {
                    texCoords[iCoords++] = coords[j];
                }

                final TileType type = level.getTile(Vector2i.of(x, y)).orElse(TileType.BLACK);
                final int index = this.spriteIndexMap.get(type.getSprite());
                for (int j = 0; j < 4; j++) {
                    texIndex[iIndex++] = index;
                }
            }
        }

        this.vertices = numTiles * 4;
        this.positions.bind();
        this.positions.upload(positions);
        this.texCoord.bind();
        this.texCoord.upload(texCoords);
        this.texIndex.bind();
        this.texIndex.upload(texIndex);
        VertexBuffer.unbind();

        this.cameraSize = this.camera.getSize();
        this.cameraFixed = this.camera.getFixedPosition();
        this.cameraDelta = this.camera.getDelta();

//        System.out.println(this.cameraFixed);
    }

    public void deltaUpdate() {
        this.cameraDelta = this.camera.getDelta();
    }

    @Override
    public void prepare() {
        this.shaderProgram = ShaderUtil.createShaderProgram("tiles");

        this.positions = VertexBuffer.make();
        this.texCoord = VertexBuffer.make();
        this.texIndex = VertexBuffer.make();
//        this.currentFrame = VertexBuffer.make();

        final Set<Sprite> sprites = Sprites.getSprites();
        // Create GL_TEXTURE_2D_ARRAY
        this.spritemapId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_ARRAY, this.spritemapId);
        glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, 64, 64, sprites.size());
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);

        int i = 0;
        for (final Sprite sprite : sprites) {
            // TODO: Here, calculate the index position by iteratively adding the number of frames that this Sprite has.
            final int currentIndex = i++;
            System.out.println(currentIndex);
            this.spriteIndexMap.put(sprite, currentIndex);

            glTexSubImage3D(
                GL_TEXTURE_2D_ARRAY,
                0, 0, 0, currentIndex,
                64, 64, 1,
                GL_RGBA, GL_UNSIGNED_BYTE,
                sprite.getBuffer().duplicate()
            );
        }
    }

    @Override
    public void cleanup() {
        this.positions.destroy();
        this.texCoord.destroy();
        this.texIndex.destroy();
//        this.currentFrame.destroy();

        this.positions = null;
        this.texCoord = null;
        this.texIndex = null;
//        this.currentFrame = null;
    }

    @Override
    public void render() {
        ARBShaderObjects.glUseProgramObjectARB(shaderProgram);
        glBindTexture(GL_TEXTURE_2D_ARRAY, this.spritemapId);

        this.positions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        this.texCoord.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
        this.texIndex.bind();
        glEnableVertexAttribArray(2);
        glVertexAttribPointer(2, 1, GL_FLOAT, false, 0, 0);

//        glEnableVertexAttribArray(3);
//        glEnableVertexAttribArray(4);
//        glEnableVertexAttribArray(5);
        glUniform2fv(3, new float[] { (float) this.cameraFixed.getX(), (float) this.cameraFixed.getY() });
        glUniform2fv(4, new float[] { (float) this.cameraSize.getX(), (float) this.cameraSize.getY() });
        glUniform2fv(5, new float[] { (float) this.cameraDelta.getX(), (float) this.cameraDelta.getY() });

        glDrawArrays(GL_QUADS, 0, this.vertices);
    }

}
