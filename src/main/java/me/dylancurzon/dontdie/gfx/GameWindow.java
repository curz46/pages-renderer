package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.Tickable;
import me.dylancurzon.dontdie.util.Vector2d;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GameWindow implements Tickable {

    private static final String TITLE = "Don't Die";
    private static final int WIDTH = 1024;
    private static final int HEIGHT = 768;

    private static final int VIRTUAL_WIDTH = 256;
    private static final int VIRTUAL_HEIGHT = 192;

    private long id = -1;

    private boolean[] keys = new boolean[GLFW_KEY_LAST];

    private boolean mousePressed;
    private boolean mouseJustPressed;

    public void initialize(final boolean doShow) {
        // Make errors print to stderr
        GLFWErrorCallback errorCallback;
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        // Initialise GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        final String title = "Don't Die";
        // TODO: Consider these values more carefully; this is 4:3 and nobody likes that.
        final int width = 1024;
        final int height = 768;

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

        this.id = glfwCreateWindow(width, height, title, NULL, NULL); //Does the actual window creation
        if (this.id == NULL) throw new RuntimeException("Failed to create window");

        glfwMakeContextCurrent(this.id);
        GL.createCapabilities(true);
        GLUtil.setupDebugMessageCallback();

        glfwSetKeyCallback(this.id, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) this.keys[key] = true;
            if (action == GLFW_RELEASE) this.keys[key] = false;
        });

        glfwSwapInterval(0);
        glClearColor(0, 0, 0, 0);
        // TODO: look into if this is necessary, as FBO is leaking into this class
        glViewport(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        if (doShow) glfwShowWindow(this.id);

        glfwMakeContextCurrent(0);
    }

    public void destroy() {
        glfwDestroyWindow(this.id);
        this.id = -1;
    }

    @Override
    public void tick() {
        this.mouseJustPressed = false;
    }

    public Vector2d getMousePosition() {
        try (final MemoryStack stack = MemoryStack.stackPush()) {
            final DoubleBuffer x = stack.callocDouble(1);
            final DoubleBuffer y = stack.callocDouble(1);
            glfwGetCursorPos(this.id, x, y);
            return Vector2d.of(x.get(0), y.get(0));
        }
    }

    public boolean isKeyPressed(int code) {
        return this.keys[code];
    }

    public boolean isMousePressed() {
        return this.mousePressed;
    }

    public boolean isMouseJustPressed() {
        return this.mouseJustPressed;
    }

    public boolean shouldWindowClose() {
        return glfwWindowShouldClose(this.id);
    }

    public long getId() {
        return this.id;
    }

}
