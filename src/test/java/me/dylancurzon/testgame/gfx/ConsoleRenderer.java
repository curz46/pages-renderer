package me.dylancurzon.testgame.gfx;

import me.dylancurzon.dontdie.gfx.Renderer;
import me.dylancurzon.dontdie.gfx.opengl.Texture;
import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import me.dylancurzon.dontdie.util.ShaderUtil;
import org.lwjgl.opengl.ARBShaderObjects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.*;

public class ConsoleRenderer extends Renderer {

    private int basicShader;

    private Texture loadingTexture;

    private VertexBuffer startupPositions;
    private VertexBuffer startupTexCoords;

    private int ticks;
    private int currentFrame;

    public void tick() {
        if (ticks++ % 15 == 0) {
            if (++currentFrame >= 19) {
                currentFrame = 0;
            }
        }
    }

    @Override
    public void prepare() {
//        this.startupShader = ShaderUtil.createShaderProgram("startup");
//        this.startupTexture = Texture.make(
//            Sprite.loadSprite("textures/zarggames.png")
//        );
        basicShader = ShaderUtil.createShaderProgram("loading");

        // TODO: load AnimatedSprite
//        final Sprite sprite = Sprite.loadSprite("loading", 20);
//        final Sprite sprite = TextSprite.of("HelloWorld", 4);
//        this.loadingTexture = Texture.make(sprite);
//
//        final float hWidth = (sprite.getWidth() / 256.0f) * 2.0f;
//        final float hHeight = (sprite.getHeight() / 192.0f) * 2.0f;
//
////        final float[] positions = {
////            -1.0f, -1.0f,
////            1.0f, -1.0f,
////            1.0f, 1.0f,
////            -1.0f, 1.0f
////        };
//        final float[] positions = {
//            -hWidth, -hHeight,
//            hWidth, -hHeight,
//            hWidth, hHeight,
//            -hWidth, hHeight
//        };
//        final float[] texCoords = {
//            0.0f, 0.0f,
//            1.0f, 0.0f,
//            1.0f, 1.0f,
//            0.0f, 1.0f
//        };
//        this.startupPositions = VertexBuffer.make();
//        this.startupPositions.bind();
//        this.startupPositions.upload(positions);
//        this.startupTexCoords = VertexBuffer.make();
//        this.startupTexCoords.bind();
//        this.startupTexCoords.upload(texCoords);
//        VertexBuffer.unbind();
    }

    @Override
    public void cleanup() {
        loadingTexture.destroy();
    }

    @Override
    public void update() {

    }

    @Override
    public void render() {
        ARBShaderObjects.glUseProgramObjectARB(basicShader);
        loadingTexture.bind();

        startupPositions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        startupTexCoords.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glUniform1i(0, currentFrame);

        glDrawArrays(GL_QUADS, 0, 4);

        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    private float lerp(float x, float a, float b, float c, float d) {
        return ((x - a) / (b - a)) * (d - c) + c;
    }

}
