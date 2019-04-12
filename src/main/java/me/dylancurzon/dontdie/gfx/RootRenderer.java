package me.dylancurzon.dontdie.gfx;

import org.jetbrains.annotations.NotNull;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL30.*;

/**
 * The main renderer for the game. This renderer will change its contents (the sub-renderers that it calls) based on the
 * current state of the game.
 */
public class RootRenderer extends Renderer {

    private final GameWindow window;
    private final Renderer[] children;

    private long lastSecond;
    private int frames;

    private int windowWidth = 1024;
    private int windowHeight = 768;

    private int fboId;
    private int fboTextureId;

    public RootRenderer(@NotNull GameWindow window, @NotNull Renderer[] children) {
        this.window = window;
        this.children = children;
    }

    @NotNull
    public <T extends Renderer> T getChild(Class<T> clazz) {
        for (Renderer child : children) {
            if (clazz.isInstance(child)) {
                //noinspection unchecked
                return (T) child;
            }
        }
        throw new RuntimeException(

            "RootRenderer[" + this + "] does not have a child that extends the given class, " + clazz);
    }

    public Renderer[] getChildren() {
        return children;
    }

    @Override
    public void prepare() {
        // Taking reference from https://github.com/LWJGL/lwjgl3-wiki/wiki/2.2.-OpenGL

        // Make errors print to stderr
        GLFWErrorCallback errorCallback;
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        // Initialise GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwMakeContextCurrent(window.getId());
        try {
            // Check if capabilities have been created in this Thread
            GL.getCapabilities();
        } catch (IllegalStateException ex) {
            GL.createCapabilities(true);

        }
//        GLUtil.setupDebugMessageCallback();

        // Make a Framebuffer of a much lower resolution to emulate the display of older consoles
        createFBO(true);

        for (Renderer child : children) {
            child.prepare();
        }
    }

    @Override
    public void cleanup() {
        for (Renderer child : children) {
            child.cleanup();
        }

        glDeleteTextures(fboTextureId);
        glDeleteFramebuffers(fboId);
    }

    @Override
    public void update() {
        for (Renderer child : children) {
            if (child.isDirty()) {
                child.update();
                child.setDirty(false);
            }
        }
    }

    @Override
    public boolean isDirty() {
        return false;
    }

    @Override
    public void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        // Draw everything to Framebuffer
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        frames++;
        if (System.currentTimeMillis() - lastSecond > 1000) {
            lastSecond = System.currentTimeMillis();
//            glfwSetWindowTitle(this.window.getId(), "FPS: " + this.frames);
            frames = 0;
        }

        for (Renderer child : children) {
            child.render();
        }

        ARBShaderObjects.glUseProgramObjectARB(0);

        // now copy low res framebuffer to window-managed framebuffer
        glBindFramebuffer(GL_READ_FRAMEBUFFER, fboId);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);

        glBlitFramebuffer(
            0, 0, windowWidth / 4, windowHeight / 4,
            0, 0, windowWidth, windowHeight,
            GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST
        );

        glfwSwapBuffers(window.getId());
        glfwPollEvents();
    }

    // If we changed the resolution, we would need to call this
    private void recreateFBO() {
        glDeleteTextures(fboTextureId);
        glDeleteFramebuffers(fboId);
        createFBO(false);
    }

    private void createFBO(boolean makeBuffers) {
        int renderbufferId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderbufferId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT,
            windowWidth / 4, windowHeight / 4);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        fboTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, fboTextureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, windowWidth / 4, windowHeight / 4, 0,
            GL_RGBA, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, fboId);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, fboTextureId, 0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderbufferId);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

}
