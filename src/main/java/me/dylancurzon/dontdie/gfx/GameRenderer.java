package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.tile.Level;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glViewport;
import static org.lwjgl.opengl.GL11C.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * The main renderer for the game. This renderer will change its contents (the sub-renderers that it calls) based on the
 * current state of the game.
 */
public class GameRenderer implements Renderer {

    public enum GameState {

        MENU_STATE,
        LEVEL_STATE

    }

    private final GameCamera camera = new GameCamera(this);
    private final Level level;

    private GameState gameState = GameState.LEVEL_STATE;

    private TileRenderer tileRenderer;

    private long window;

    private long lastSecond;
    private int frames;

    public GameRenderer(final Level level) {
        this.level = level;
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

        this.window = this.createWindow();

        glfwMakeContextCurrent(this.window);
        // TODO: I'm paranoid that making this true will result in terrifying errors in the future.
        GL.createCapabilities(true);
        GLUtil.setupDebugMessageCallback();

        glfwSwapInterval(1);
        glClearColor(0, 0, 0, 0);
        glfwSetFramebufferSizeCallback(window, (window, width, height) -> {
            glViewport(0, 0, width, height);
            this.camera.setAspectRatio((double) width / height);
            this.tileRenderer.tilemapUpdate();
        });
        glViewport(0, 0, 1024, 768);
        this.camera.setAspectRatio((double) 1024 / 768);
        glfwShowWindow(this.window);

        this.tileRenderer = new TileRenderer(this.camera, this.level);
        this.tileRenderer.prepare();
    }

    @Override
    public void cleanup() {
        this.tileRenderer.cleanup();
        this.tileRenderer = null;
    }

    @Override
    public void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        if (this.gameState == GameState.LEVEL_STATE) {
            this.tileRenderer.render();
        }

        frames++;
        if (System.currentTimeMillis() - this.lastSecond > 1000) {
            this.lastSecond = System.currentTimeMillis();
            System.out.println(frames);
            frames = 0;
        }

        glfwSwapBuffers(this.window);
        glfwPollEvents();
    }

    /**
     * Notifies this {@link GameRenderer} that the {@link GameCamera} has been updated in a way that affects rendering.
     */
    public void updateCamera() {
        this.tileRenderer.deltaUpdate();
    }

    public void setState(final GameState gameState) {
        this.gameState = gameState;
    }

    public boolean windowShouldClose() {
        return glfwWindowShouldClose(this.window);
    }

    public long getWindow() {
        return this.window;
    }

    public GameState getState() {
        return this.gameState;
    }

    public GameCamera getCamera() {
        return this.camera;
    }

    public TileRenderer getTileRenderer() {
        return this.tileRenderer;
    }

    private long createWindow() {
        // Create the Display.
        final String title = "Don't Die";
        // TODO: Consider these values more carefully; this is 4:3 and nobody likes that.
        final int width = 1024;
        final int height = 768;

        glfwDefaultWindowHints();
        glfwWindowHint(GLFW_VISIBLE, GL_TRUE);
        // TODO: The window *should* be resizable, but content will have to scale appropriately. For now, leave it off.
        // Note: This *is* done automatically by OpenGL, but it will cause content to distort should the aspect ratio
        // not be maintained.
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);

        final long id = glfwCreateWindow(width, height, title, NULL, NULL); //Does the actual window creation
        if (id == NULL) throw new RuntimeException("Failed to create window");

        return id;
    }

}
