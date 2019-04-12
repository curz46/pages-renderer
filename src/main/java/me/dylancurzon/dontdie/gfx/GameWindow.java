package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.Tickable;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
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

    private Vector2d lastMousePosition;
    private boolean inFocus;

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

        glfwFocusWindow(id);
        inFocus = true;

        glfwSetWindowFocusCallback(id, (window, focused) -> {
            inFocus = focused;
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

    /**
     * Gets the mouse position, but in a more predictable fashion. GLFW will provide a mouse position regardless of if
     * the mouse is currently within the window's bounds or even if the window is in focus. This method will catch
     * instances where the mouse has exited the window's bounds and stop tracking it, and will do the same for when the
     * window becomes unfocused. This is a more user friendly experience since, in most cases, an unfocused window
     * should leave the user alone.
     * @return A mouse position with real screen coordinates; a minimum of (0, 0) and a maximum of
     * ({@link this#WIDTH}, {@link this#HEIGHT}).
     */
    public Vector2d getMousePosition() {
        // TODO: The implementation of this method is such that an accurate "lastMousePosition" requires that this
        //       method is frequently called. That is arbitrary behaviour as far as the user is concerned, so really
        //       the user should be able to "update" this Window and the lastMousePosition should be as of last "update"

        // If window not in focus, return the last known position
        if (!inFocus) {
            return lastMousePosition;
        }

        Vector2d mousePosition = queryMousePosition();

        // If mouse position is outside of window bounds, return last known position
        boolean withinBounds = mousePosition.getX() > 0 && mousePosition.getY() > 0 &&
            mousePosition.getX() <= WIDTH && mousePosition.getY() <= HEIGHT;
        if (!withinBounds) {
            return lastMousePosition;
        }

        lastMousePosition = mousePosition;
        return mousePosition;
    }

    public boolean isWindowFocused() {
        return inFocus;
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

    private Vector2d queryMousePosition() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer x = stack.callocDouble(1);
            DoubleBuffer y = stack.callocDouble(1);
            glfwGetCursorPos(id, x, y);
            return Vector2d.of(x.get(0), y.get(0));
        }
    }

}
