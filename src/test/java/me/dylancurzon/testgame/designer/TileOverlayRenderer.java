package me.dylancurzon.testgame.designer;

import me.dylancurzon.dontdie.gfx.Renderer;
import me.dylancurzon.dontdie.gfx.opengl.Texture;
import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.SpritePacker;
import me.dylancurzon.testgame.gfx.Sprites;
import me.dylancurzon.dontdie.util.ShaderUtil;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.testgame.gfx.Camera;
import org.lwjgl.opengl.ARBShaderObjects;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class TileOverlayRenderer extends Renderer {

    private final Camera camera;

    private List<OverlaySprite> overlaySprites = new ArrayList<>();

    private int shader;
    private int vertexCount;

    private VertexBuffer positionsBuffer;
    private VertexBuffer texCoordBuffer;

    private SpritePacker spritePacker;
    private Texture packerTexture;

    public TileOverlayRenderer(Camera camera) {
        this.camera = camera;
    }

    @Override
    public void prepare() {
        shader = ShaderUtil.createShaderProgram("tiles");

        positionsBuffer = VertexBuffer.make();
        texCoordBuffer = VertexBuffer.make();

        spritePacker = new SpritePacker(Sprites.getSprites());
        packerTexture = Texture.make(spritePacker);

        update();
    }

    @Override
    public void cleanup() {
        positionsBuffer.destroy();
        texCoordBuffer.destroy();
        positionsBuffer = null;
        texCoordBuffer = null;

        spritePacker = null;
        packerTexture.destroy();
        packerTexture = null;

        // TODO: Destroy shader
    }

    @Override
    public void render() {
        Vector2d fixed = camera.getFixedPosition();
        Vector2d size = camera.getSize();
        Vector2d delta = camera.getDelta();

        ARBShaderObjects.glUseProgramObjectARB(shader);
        glBindTexture(GL_TEXTURE_2D, packerTexture.getId());
//        this.tileTexture.bind();

        positionsBuffer.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        texCoordBuffer.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glUniform2fv(3, new float[] { (float) fixed.getX(), (float) fixed.getY() });
        glUniform2fv(4, new float[] { (float) size.getX(), (float) size.getY() });
        glUniform2fv(5, new float[] { (float) delta.getX(), (float) delta.getY() });

        glDrawArrays(GL_QUADS, 0, vertexCount);

        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    @Override
    public void update() {
        int renderCount = overlaySprites.size();

        float[] positions = new float[renderCount * 2 * 4];
        float[] texCoords = new float[renderCount * 2 * 4];

        int positionIndex = 0;
        int texCoordIndex = 0;

        for (OverlaySprite overlaySprite : overlaySprites) {
            Vector2i position = overlaySprite.getPosition();
            Sprite sprite = overlaySprite.getSprite();

            float[] pos = getTileBounds(position);
            for (int i = 0; i < pos.length; i++) {
                positions[positionIndex++] = pos[i];
            }

            Vector2i packerPosition = spritePacker.getSpritePosition(sprite)
                .orElseThrow(() -> new IllegalStateException("SpritePacker does not contain required Sprite: " + sprite));
            float startX = ((float) packerPosition.getX()) / spritePacker.getWidth();
            float startY = ((float) packerPosition.getY()) / spritePacker.getHeight();
            float endX = ((float) (packerPosition.getX() + sprite.getWidth())) / spritePacker.getWidth();
            float endY = ((float) (packerPosition.getY() + sprite.getHeight())) / spritePacker.getHeight();

            float[] coords = {
                startX, endY,
                endX,   endY,
                endX,   startY,
                startX, startY
            };
            for (int i = 0; i < coords.length; i++) {
                texCoords[texCoordIndex++] = coords[i];
            }
        }

        vertexCount = renderCount * 4;
        positionsBuffer.bind();
        positionsBuffer.upload(positions);
        texCoordBuffer.bind();
        texCoordBuffer.upload(texCoords);
        VertexBuffer.unbind();
    }

    public List<OverlaySprite> getOverlaySprites() {
        return overlaySprites;
    }

    private float[] getTileBounds(Vector2i position) {
        float x = position.getX();
        float y = position.getY();
        return new float[] {
            x, y,
            x + 1, y,
            x + 1, y + 1,
            x, y + 1
        };
    }

    public static class OverlaySprite {

        private final Vector2i position;
        private final Sprite sprite;

        public static OverlaySprite of(Vector2i position, Sprite sprite) {
            return new OverlaySprite(position, sprite);
        }

        private OverlaySprite(Vector2i position, Sprite sprite) {
            this.position = position;
            this.sprite = sprite;
        }

        public Vector2i getPosition() {
            return position;
        }

        public Sprite getSprite() {
            return sprite;
        }

    }

}
