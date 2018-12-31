package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.util.ShaderUtil;
import me.dylancurzon.dontdie.util.Vector2d;
import org.lwjgl.opengl.ARBShaderObjects;

import static org.lwjgl.opengl.GL11.GL_FLOAT;
import static org.lwjgl.opengl.GL11.GL_QUADS;
import static org.lwjgl.opengl.GL11.glDrawArrays;
import static org.lwjgl.opengl.GL20.*;

public class ConsoleRenderer implements Renderer {

    private final int BOUNCE_DURATION = 8; // seconds
    private final int BOUNCE_FPS = 60;
    private Vector2d[] bounceFrames;
    private int currentFrame;

    private int ticks;

    private int startupShader;
    private Texture startupTexture;

    private VertexBuffer startupPositions;
    private VertexBuffer startupTexCoords;

    public void tick() {
        if (this.currentFrame == BOUNCE_FPS * BOUNCE_DURATION - 1) {
            return;
        }
        if (ticks++ % ((float) 60 / BOUNCE_FPS) == 0) {
            this.currentFrame++;
        }
    }

    @Override
    public void prepare() {
        this.startupShader = ShaderUtil.createShaderProgram("startup");
        this.startupTexture = Texture.make(
            Sprite.loadSprite("textures/zarggames.png")
        );

        final float[] positions = {
            -1.0f, -1.0f,
            1.0f, -1.0f,
            1.0f, 1.0f,
            -1.0f, 1.0f
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

        // Simulate a ball bouncing and fill bounceFrames
        final int numFrames = BOUNCE_FPS * BOUNCE_DURATION;
        this.bounceFrames = new Vector2d[numFrames];

        final float acc = -0.008f;
        final float timeStep = 1.0f / BOUNCE_FPS * 6;
        float height = 1.0f;
        float roll = -0.4f;
        float hVelocity = 0.0f;
        float vVelocity = 0.006f;
        for (int i = 0; i < numFrames; i++) {
            hVelocity += acc * timeStep;
            height += hVelocity;

//            vVelocity *= 0.8825;
            roll += vVelocity;

            if (height <= 0) {
                height = 0;
//                hVelocity = -hVelocity * 0.55f;
                hVelocity = -hVelocity * 0.55f;
                vVelocity *= 0.4;
            }

//            System.out.println(height);
            this.bounceFrames[i] = Vector2d.of(roll, height);
        }
    }

    @Override
    public void cleanup() {
        this.startupTexture.destroy();
    }

    @Override
    public void render() {
        ARBShaderObjects.glUseProgramObjectARB(this.startupShader);
        this.startupTexture.bind();

        this.startupPositions.bind();
        glEnableVertexAttribArray(0);
        glVertexAttribPointer(0, 2, GL_FLOAT, false, 0, 0);
        this.startupTexCoords.bind();
        glEnableVertexAttribArray(1);
        glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0);

        final Vector2d frame = this.bounceFrames[this.currentFrame];
        glUniform2f(0, (float) frame.getX(), (float) frame.getY());

        glDrawArrays(GL_QUADS, 0, 4);

        ARBShaderObjects.glUseProgramObjectARB(0);
    }

}
