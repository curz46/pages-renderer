package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.gfx.opengl.Texture;
import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.SpritePacker;
import me.dylancurzon.dontdie.sprite.Sprites;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.dontdie.tile.TileType;
import me.dylancurzon.dontdie.util.ShaderUtil;
import me.dylancurzon.dontdie.util.Vector2d;
import me.dylancurzon.dontdie.util.Vector2i;
import org.lwjgl.opengl.ARBShaderObjects;

import java.util.Arrays;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

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

    private final Camera camera;
    private final Level level;

    private int shaderProgram;

//    private final Map<OldSprite, Integer> spriteIndexMap = new HashMap<>();
//    private int spritemapId;

    private int vertices;
    private VertexBuffer positions;
    private VertexBuffer texCoord;
//    private VertexBuffer texIndex;
//    private VertexBuffer currentFrame;

    private SpritePacker tilePacker;
    private Texture tileTexture;

    private Vector2d oldFixed = null;

    public TileRenderer(final Camera camera, final Level level) {
        this.camera = camera;
        this.level = level;
    }

    @Override
    public void prepare() {
        this.shaderProgram = ShaderUtil.createShaderProgram("tiles");

        this.positions = VertexBuffer.make();
        this.texCoord = VertexBuffer.make();
//        this.texIndex = VertexBuffer.make();
//        this.currentFrame = VertexBuffer.make();

//        final Set<AnimatedSprite> sprites = Sprites.getSprites();
//        // Create GL_TEXTURE_2D_ARRAY
//        this.spritemapId = glGenTextures();
//        glBindTexture(GL_TEXTURE_2D_ARRAY, this.spritemapId);
//        glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, 16, 16, sprites.size());
//        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
//        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
//        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
//
//        int i = 0;
//        for (final AnimatedSprite sprite : sprites) {
//            // TODO: Here, calculate the index position by iteratively adding the number of frames that this Sprite has.
//            final int currentIndex = i++;
//            System.out.println(currentIndex);
//            this.spriteIndexMap.put(sprite, currentIndex);
//
//            glTexSubImage3D(
//                GL_TEXTURE_2D_ARRAY,
//                0, 0, 0, currentIndex,
//                16, 16, 1,
//                GL_RGBA, GL_UNSIGNED_BYTE,
//                sprite.getBuffer().duplicate()
//            );
//        }

        // Prepare dynamic Tile spritemap
//        final Set<Sprite> tileSprites = Arrays.stream(TileType.values())
//            .map(TileType::getSprite)
//            .collect(Collectors.toSet());
        this.tilePacker = new SpritePacker(Sprites.getSprites());
        this.tileTexture = Texture.make(this.tilePacker);
//        this.tileTexture = Texture.make(Sprites.STONEBRICKS);

        this.updateTilemap();
    }

    @Override
    public void cleanup() {
        this.positions.destroy();
        this.texCoord.destroy();
//        this.texIndex.destroy();
//        this.currentFrame.destroy();

        this.positions = null;
        this.texCoord = null;
//        this.texIndex = null;
//        this.currentFrame = null;
    }

    @Override
    public void render() {
//        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        final Vector2d fixed = this.camera.getFixedPosition();
        final Vector2d size = this.camera.getSize();
        final Vector2d delta = this.camera.getDelta();

        if (!Objects.equals(this.oldFixed, fixed)) {
            this.updateTilemap();
            this.oldFixed = fixed;
        }

        ARBShaderObjects.glUseProgramObjectARB(this.shaderProgram);
        glBindTexture(GL_TEXTURE_2D, this.tileTexture.getId());
//        this.tileTexture.bind();

        this.positions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        this.texCoord.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);
//        this.texIndex.bind();
//        glEnableVertexAttribArray(2);
//        glVertexAttribPointer(2, 1, GL_FLOAT, false, 0, 0);

//        glEnableVertexAttribArray(3);
//        glEnableVertexAttribArray(4);
//        glEnableVertexAttribArray(5);

        glUniform2fv(3, new float[] { (float) fixed.getX(), (float) fixed.getY() });
        glUniform2fv(4, new float[] { (float) size.getX(), (float) size.getY() });
        glUniform2fv(5, new float[] { (float) delta.getX(), (float) delta.getY() });

        glDrawArrays(GL_QUADS, 0, this.vertices);
//        glDrawArrays(GL_QUADS, 0, 4);

        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    /**
     * Whenever the Camera tilemap is fully updated, the data in each of the VertexBuffers needs to be updated with the
     * new visible tilemap, as well as the uniform values.
     */
    private void updateTilemap() {
        final Vector2i pointA = this.camera.getVisibleA().sub(2);
        final Vector2i pointB = this.camera.getVisibleB().add(2);

        final int numX = pointB.getX() - pointA.getX();
        final int numY = pointB.getY() - pointA.getY();
        final int numTiles = numX * numY;

        final float[] positions = new float[numTiles * 2 * 4];
        final float[] texCoords = new float[numTiles * 2 * 4];
//        final float[] texIndex = new float[numTiles * 4];

        int iPos = 0;
        int iCoords = 0;
//        int iIndex = 0;
        for (int x = pointA.getX(); x < pointB.getX();  x++) {
            for (int y = pointA.getY(); y < pointB.getY(); y++) {
                final TileType type = this.level.getTile(Vector2i.of(x, y)).orElse(TileType.BLACK);
//                if (type == null) continue;

                final float[] pos = {
                    x, y,
                    x + 1, y,
                    x + 1, y + 1,
                    x, y + 1
                };
                for (int j = 0; j < pos.length; j++) {
                    positions[iPos++] = pos[j];
                }

//                System.out.println(type);
                final Sprite sprite = type.getSprite();
                final Vector2i position = this.tilePacker.getSpritePosition(sprite)
                    .orElseThrow(() -> new RuntimeException("Sprite of TileType[" + type + "] is not in SpritePacker!"));
                final float startX = ((float) position.getX()) / this.tilePacker.getWidth();
                final float startY = ((float) position.getY()) / this.tilePacker.getHeight();
                final float endX = ((float) (position.getX() + sprite.getWidth())) / this.tilePacker.getWidth();
                final float endY = ((float) (position.getY() + sprite.getHeight())) / this.tilePacker.getHeight();

//                System.out.println(startX + ", " + startY  +", " + endX + ", " + endY);

                final float[] coords = {
                    startX, endY,
                    endX, endY,
                    endX, startY,
                    startX, startY
                };

//                final float[] coords = {
//                    0.0f, 0.0f,
//                    1.0f, 0.0f,
//                    1.0f, 1.0f,
//                    0.0f, 1.0f
//                };

                for (int j = 0; j < coords.length; j++) {
                    texCoords[iCoords++] = coords[j];
                }
            }
        }

        this.vertices = numTiles * 4;
        this.positions.bind();
        this.positions.upload(positions);
        this.texCoord.bind();
        this.texCoord.upload(texCoords);
//        this.texIndex.bind();
//        this.texIndex.upload(texIndex);
        VertexBuffer.unbind();
    }

}
