package me.dylancurzon.openglrenderer.gfx.opengl;

import me.dylancurzon.openglrenderer.sprite.Sprite;
import me.dylancurzon.openglrenderer.util.Buffers;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL12.glTexSubImage3D;
import static org.lwjgl.opengl.GL30.GL_TEXTURE_2D_ARRAY;
import static org.lwjgl.opengl.GL42.glTexStorage3D;

public class TextureArray {

    private final int id;

    public static TextureArray make(int width, int height, Sprite[] sprites) {
        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_ARRAY, id);
        glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, width, height, sprites.length);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        for (int i = 0; i < sprites.length; i++) {
            Sprite sprite = sprites[i];
            glTexSubImage3D(
                GL_TEXTURE_2D_ARRAY, 0,
                0, 0, i,
                sprite.getWidth(), sprite.getHeight(), 1,
                GL_RGBA, GL_UNSIGNED_BYTE, Buffers.asHeapBuffer(sprite.getFrames()[0])
            );
        }
        return new TextureArray(id);
    }

    public static TextureArray make(Sprite sprite) {
        int id = glGenTextures();
        glBindTexture(GL_TEXTURE_2D_ARRAY, id);
        glTexStorage3D(GL_TEXTURE_2D_ARRAY, 1, GL_RGBA8, sprite.getWidth(), sprite.getHeight(), sprite.getFrameCount());
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D_ARRAY, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        for (int i = 0; i < sprite.getFrameCount(); i++) {
            glTexSubImage3D(
                GL_TEXTURE_2D_ARRAY, 0,
                0, 0, i,
                sprite.getWidth(), sprite.getHeight(), 1,
                GL_RGBA, GL_UNSIGNED_BYTE, Buffers.asHeapBuffer(sprite.getFrames()[i])
            );
        }
        return new TextureArray(id);
    }

    public static void unbind() {
        glBindTexture(GL_TEXTURE_2D_ARRAY, 0);
    }

    private TextureArray(int id) {
        this.id = id;
    }

    public void bind() {
        glBindTexture(GL_TEXTURE_2D_ARRAY, id);
    }

    public void destroy() {
        glDeleteTextures(id);
    }

}
