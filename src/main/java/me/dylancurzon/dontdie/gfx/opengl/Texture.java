package me.dylancurzon.dontdie.gfx.opengl;

import me.dylancurzon.dontdie.sprite.Sprite;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.glGenTextures;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class Texture {

    private final int id;

    public static Texture make() {
        return new Texture(glGenTextures());
    }

    public static Texture make(final Sprite sprite) {
        final Texture texture = Texture.make();

        texture.bind();
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, sprite.getWidth(), sprite.getHeight(), 0,
            GL_RGBA, GL_UNSIGNED_BYTE, sprite.getFrames()[0]);
        Texture.unbind();

        return texture;
    }

    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    private Texture(final int id) {
        this.id = id;
    }

    public int getId() {
        return this.id;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D, this.id);
    }

    public void destroy() {
        glDeleteTextures(this.id);
    }

}
