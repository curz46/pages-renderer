package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.Tickable;
import me.dylancurzon.pages.util.Vector2d;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Consumer;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GameWindow implements Tickable {

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;

    public static final int VIRTUAL_WIDTH = 256;
    public static final int VIRTUAL_HEIGHT = 192;

    private static final String TITLE = "Don't Die";

    private long id = -1;

    private boolean[] keys = new boolean[GLFW_KEY_LAST];

    private boolean mousePressed;
    private boolean mouseJustPressed;

    private Set<Consumer<Vector2d>> clickListeners = new HashSet<>();

    public void initialize(boolean doShow) {
        // Make errors print to stderr
        GLFWErrorCallback errorCallback;
        glfwSetErrorCallback(errorCallback = GLFWErrorCallback.createPrint(System.err));
        // Initialise GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        String title = "Don't Die";
        // TODO: Consider these values more carefully; this is 4:3 and nobody likes that.
        int width = 1024;
        int height = 768;

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

        id = glfwCreateWindow(width, height, title, NULL, NULL); //Does the actual window creation
        if (id == NULL) throw new RuntimeException("Failed to create window");

        glfwMakeContextCurrent(id);
        GL.createCapabilities(true);
//        GLUtil.setupDebugMessageCallback();

        glfwSetKeyCallback(id, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) keys[key] = true;
            if (action == GLFW_RELEASE) keys[key] = false;
        });

        glfwSetMouseButtonCallback(id, (window, button, action, mods) -> {
            if (button == GLFW_MOUSE_BUTTON_1) {
                mousePressed = action != GLFW_RELEASE;
                mouseJustPressed = mousePressed;
                if (action == GLFW_PRESS) {
                    clickListeners.forEach(consumer -> consumer.accept(getMousePosition()));
                }
            }
        });

        glfwSwapInterval(0);
        glClearColor(0, 0, 0, 0);
        // TODO: look into if this is necessary, as FBO is leaking into this class
        glViewport(0, 0, VIRTUAL_WIDTH, VIRTUAL_HEIGHT);

        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        if (doShow) glfwShowWindow(id);

        glfwMakeContextCurrent(0);
    }

    public void destroy() {
        glfwDestroyWindow(id);
        id = -1;
    }

    @Override
    public void tick() {
        mouseJustPressed = false;
    }

    public void registerClickListener(Consumer<Vector2d> consumer) {
        clickListeners.add(consumer);
    }

    public Vector2d getMousePosition() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer x = stack.callocDouble(1);
            DoubleBuffer y = stack.callocDouble(1);
            glfwGetCursorPos(id, x, y);
            return Vector2d.of(x.get(0), y.get(0));
        }
    }

    public boolean isKeyPressed(int code) {
        return keys[code];
    }

    public boolean isMousePressed() {
        return mousePressed;
    }

    public boolean isMouseJustPressed() {
        return mouseJustPressed;
    }

    public boolean shouldWindowClose() {
        return glfwWindowShouldClose(id);
    }

    public long getId() {
        return id;
    }

}
