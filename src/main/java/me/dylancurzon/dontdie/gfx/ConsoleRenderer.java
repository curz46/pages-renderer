package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.gfx.opengl.TextureArray;
import me.dylancurzon.dontdie.gfx.opengl.VertexBuffer;
import me.dylancurzon.dontdie.sprite.AnimatedSprite;
import me.dylancurzon.dontdie.util.ShaderUtil;
import org.lwjgl.opengl.ARBShaderObjects;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;

public class ConsoleRenderer implements Renderer {

    private int basicShader;

    private TextureArray loadingTexture;

    private VertexBuffer startupPositions;
    private VertexBuffer startupTexCoords;

    private int ticks;
    private int currentFrame;

    public void tick() {
        if (this.ticks++ % 15 == 0) {
            if (++this.currentFrame >= 19) {
                this.currentFrame = 0;
            }
        }
    }

    @Override
    public void prepare() {
//        this.startupShader = ShaderUtil.createShaderProgram("startup");
//        this.startupTexture = Texture.make(
//            Sprite.loadSprite("textures/zarggames.png")
//        );
        this.basicShader = ShaderUtil.createShaderProgram("loading");

        // TODO: load AnimatedSprite
        final AnimatedSprite sprite = AnimatedSprite.loadAnimatedSprite("loading", 20);
        this.loadingTexture = TextureArray.make(sprite);

        final float hWidth = (sprite.getWidth() / 256.0f) / 2.0f;
        final float hHeight = (sprite.getHeight() / 192.0f) / 2.0f;

//        final float[] positions = {
//            -1.0f, -1.0f,
//            1.0f, -1.0f,
//            1.0f, 1.0f,
//            -1.0f, 1.0f
//        };
        final float[] positions = {
            -hWidth, -hHeight,
            hWidth, -hHeight,
            hWidth, hHeight,
            -hWidth, hHeight
        };
        final float[] texCoords = {
            0.0f, 1.0f,
            1.0f, 1.0f,
            1.0f, 0.0f,
            0.0f, 0.0f
        };
        this.startupPositions = VertexBuffer.make();
        this.startupPositions.bind();
        this.startupPositions.upload(positions);
        this.startupTexCoords = VertexBuffer.make();
        this.startupTexCoords.bind();
        this.startupTexCoords.upload(texCoords);
        VertexBuffer.unbind();
    }

    @Override
    public void cleanup() {
        this.loadingTexture.destroy();
    }

    @Override
    public void render() {
        ARBShaderObjects.glUseProgramObjectARB(this.basicShader);
        this.loadingTexture.bind();

        this.startupPositions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        this.startupTexCoords.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        glUniform1i(0, this.currentFrame);

        glDrawArrays(GL_QUADS, 0, 4);

        ARBShaderObjects.glUseProgramObjectARB(0);
    }

    private float lerp(final float x, final float a, final float b, final float c, final float d) {
        return ((x - a) / (b - a)) * (d - c) + c;
    }

}
