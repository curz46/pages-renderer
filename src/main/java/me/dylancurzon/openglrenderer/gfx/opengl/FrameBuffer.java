package me.dylancurzon.openglrenderer.gfx.opengl;

import me.dylancurzon.openglrenderer.gfx.window.Window;
import me.dylancurzon.pages.util.Vector2i;

import static org.lwjgl.opengl.ARBFramebufferObject.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;

public class FrameBuffer {

    private final int width;
    private final int height;

    private int id;
    private int textureId;

    public static FrameBuffer make(int width, int height) {
        int renderbufferId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderbufferId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        int textureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, textureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        int id = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, id);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, textureId, 0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderbufferId);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);

        return new FrameBuffer(width, height, id, textureId);
    }

    public static void unbind() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void bind() {
        glBindFramebuffer(GL_FRAMEBUFFER, id);
    }

    public void copy(Window window) {
        glBindFramebuffer(GL_READ_FRAMEBUFFER, id);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);

        Vector2i dimensions = window.getDimensions();
        glBlitFramebuffer(
            0, 0, width, height,
            0, 0, dimensions.getX(), dimensions.getY(),
            GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST
        );

        glBindFramebuffer(GL_READ_FRAMEBUFFER, 0);
    }

    private FrameBuffer(int width, int height, int id, int textureId) {
        this.width = width;
        this.height = height;
        this.id = id;
        this.textureId = textureId;
    }

}
