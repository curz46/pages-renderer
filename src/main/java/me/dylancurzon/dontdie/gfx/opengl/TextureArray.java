package me.dylancurzon.dontdie.gfx.opengl;

import me.dylancurzon.dontdie.sprite.Sprite;

import java.nio.ByteBuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

public class TextureArray {

    private final int id;

    public static TextureArray make(final Sprite sprite) {
        final int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_ARRAY, id);
        glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, sprite.getWidth(), sprite.getHeight(), sprite.getFrameCount());
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        for (int i = 0; i < sprite.getFrameCount(); i++) {
            final ByteBuffer buf = sprite.getFrames()[i];
            glTexSubImage3D(
                GL_TEXTURE_2D_ARRAY, 0,
                0, 0, i,
                sprite.getWidth(), sprite.getHeight(), 1,
                GL_RGBA, GL_UNSIGNED_BYTE, buf
            );
        }
        return new TextureArray(id);
    }

    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
    }

    private TextureArray(final int id) {
        this.id = id;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D_ARRAY, this.id);
    }

    public void destroy() {
        glDeleteTextures(this.id);
    }

}
