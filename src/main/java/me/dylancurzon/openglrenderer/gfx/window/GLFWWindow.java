package me.dylancurzon.openglrenderer.gfx.window;

import me.dylancurzon.pages.event.MouseScrollEvent;
import me.dylancurzon.pages.event.bus.SimpleEventBus;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.system.MemoryStack;

import java.nio.DoubleBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL11.GL_TRUE;
import static org.lwjgl.system.MemoryUtil.NULL;

public class GLFWWindow extends SimpleEventBus implements Window {

    private long id = -1;

    private final boolean[] keys = new boolean[GLFW_KEY_LAST];
    private final Map<MouseButton, Boolean> buttonPressedMap = new HashMap<>();

    private final GLFWWindowOptions options;

    private Vector2i dimensions;
    private String title;

    private boolean initialized;
    private Vector2d lastMousePosition;
    private boolean focused;

    /**
     * Sets whether or not the current context window is vsync.
     */
    public static void setVsync(boolean vsync) {
        glfwSwapInterval(vsync ? 1 : 0);
    }

    public GLFWWindow(GLFWWindowOptions options) {
        this.options = options;
        dimensions = options.getDimensions();
        title = options.getTitle();
    }

    /**
     * Initialize this window:
     * - Setup error callback
     * - Create GLFW window of configured properties
     * - Link current and future subscribers of events to the underlying GLFW window events
     */
    public void initialize() {
        // Make errors print to stderr
        if (options.getErrorCallback() != null) {
            glfwSetErrorCallback(options.getErrorCallback());
        }
        // Initialise GLFW
        if (!glfwInit()) {
            throw new IllegalStateException("Unable to initialize GLFW");
        }

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, options.isVisible() ? GL_TRUE : GL_FALSE);
        glfwWindowHint(GLFW_RESIZABLE, options.isResizable() ? GL_TRUE : GL_FALSE);

        id = glfwCreateWindow(dimensions.getX(), dimensions.getY(), title, NULL, NULL); //Does the actual window creation
        if (id == NULL) throw new RuntimeException("Failed to create window");

        glfwMakeContextCurrent(id);

        glfwSetKeyCallback(id, (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) keys[key] = true;
            if (action == GLFW_RELEASE) keys[key] = false;

            WindowKeyEvent event;
            switch (action) {
                case GLFW_PRESS:
                    event = new WindowKeyEvent.Press(key);
                    break;
                case GLFW_RELEASE:
                    event = new WindowKeyEvent.Release(key);
                    break;
                case GLFW_REPEAT:
                    event = new WindowKeyEvent.Repeat(key);
                    break;
                default: throw new IllegalStateException("Unexpected action: " + action);
            }
            post(event);
        });

        glfwSetMouseButtonCallback(id, (window, buttonCode, actionCode, mods) -> {
            MouseButton button;
            switch (buttonCode) {
                case GLFW_MOUSE_BUTTON_LEFT:
                    button = MouseButton.LEFT_MOUSE_BUTTON;
                    break;
                case GLFW_MOUSE_BUTTON_MIDDLE:
                    button = MouseButton.MIDDLE_MOUSE_BUTTON;
                    break;
                case GLFW_MOUSE_BUTTON_RIGHT:
                    button = MouseButton.RIGHT_MOUSE_BUTTON;
                    break;
                default: button = null;
            }
            MouseAction action;
            switch (actionCode) {
                case GLFW_PRESS:
                    action = MouseAction.PRESS;
                    break;
                case GLFW_RELEASE:
                    action = MouseAction.RELEASE;
                    break;
                default: action = null;
            }
            if (button == null || action == null) return;

            WindowMouseActionEvent event = action == MouseAction.PRESS
                ? new WindowMouseActionEvent.Press(action, button)
                : new WindowMouseActionEvent.Release(action, button);
            buttonPressedMap.put(button, action == MouseAction.PRESS);
            post(event);
        });

        glfwSetScrollCallback(id, (window, offsetX, offsetY) -> {
            // Ignore offsetX
            post(new MouseScrollEvent(offsetY));
        });

        glfwSetWindowFocusCallback(id, (window, focused) -> {
            this.focused = focused;
            WindowFocusEvent event = focused
                ? new WindowFocusEvent.Start()
                : new WindowFocusEvent.End();
            post(event);
        });

        glfwSwapInterval(options.isVsync() ? 1 : 0);

        glfwMakeContextCurrent(0);
        initialized = true;
    }

    /**
     * Show the window.
     */
    public void show() {
        glfwShowWindow(id);
    }

    /**
     * Focus the window.
     */
    public void focus() {
        glfwFocusWindow(id);
        // Event not emitted when we manually focus the Window
        focused = true;
    }

    public void makeCurrentContext() {
        glfwMakeContextCurrent(id);
    }

    /**
     * Destroy the window.
     */
    public void destroy() {
        glfwDestroyWindow(id);
        initialized = false;
        id = -1;
    }

    public void setDimensions(Vector2i newDimensions) {
        Objects.requireNonNull(newDimensions);
        glfwSetWindowSize(id, newDimensions.getX(), newDimensions.getY());
        dimensions = newDimensions;
    }

    public void setTitle(String newTitle) {
        Objects.requireNonNull(newTitle);
        glfwSetWindowTitle(id, newTitle);
        title = newTitle;
    }

    /**
     * Gets the mouse position, but in a more predictable fashion. GLFW will provide a mouse position regardless of if
     * the mouse is currently within the window's bounds or even if the window is in focus. This method will catch
     * instances where the mouse has exited the window's bounds and stop tracking it, and will do the same for when the
     * window becomes unfocused. This is a more user friendly experience since, in most cases, an unfocused window
     * should leave the user alone.
     * @return A mouse position with real screen coordinates; a minimum of (0, 0) and a maximum of
     * ({@link this#getDimensions()}.
     */
    public Vector2d getMousePosition() {
        // TODO: The implementation of this method is such that an accurate "lastMousePosition" requires that this
        //       method is frequently called. That is arbitrary behaviour as far as the user is concerned, so really
        //       the user should be able to "update" this Window and the lastMousePosition should be as of last "update"

        // If window not in focus, return the last known position
        if (!focused) {
            return lastMousePosition;
        }

        Vector2d mousePosition = getRealMousePosition();

        // If mouse position is outside of window bounds, return last known position
        boolean withinBounds = mousePosition.getX() > 0
            && mousePosition.getY() > 0
            && mousePosition.getX() <= dimensions.getX()
            && mousePosition.getY() <= dimensions.getY();
        if (!withinBounds) {
            return lastMousePosition;
        }

        lastMousePosition = mousePosition;
        return mousePosition;
    }

    /**
     * @return The mouse position as provided by GLFW.
     */
    public Vector2d getRealMousePosition() {
        try (MemoryStack stack = MemoryStack.stackPush()) {
            DoubleBuffer x = stack.callocDouble(1);
            DoubleBuffer y = stack.callocDouble(1);
            glfwGetCursorPos(id, x, y);
            return Vector2d.of(x.get(0), y.get(0));
        }
    }

    @Override
    public boolean isFocused() {
        return focused;
    }

    public boolean isKeyPressed(int code) {
        return keys[code];
    }

    @Override
    public boolean isMousePressed(MouseButton button) {
        return buttonPressedMap.get(button);
    }

    public boolean shouldWindowClose() {
        return glfwWindowShouldClose(id);
    }

    public long getId() {
        return id;
    }

    @Nullable
    @Override
    public String getTitle() {
        return title;
    }

    @NotNull
    @Override
    public Vector2i getDimensions() {
        return dimensions;
    }

}
