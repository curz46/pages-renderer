package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.dontdie.util.ShaderUtil;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.ARBShaderObjects;
import org.lwjgl.opengl.GL;
import org.lwjgl.opengl.GLUtil;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.ARBFramebufferObject.GL_DRAW_FRAMEBUFFER;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11C.GL_DEPTH_COMPONENT;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20C.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.*;
import static org.lwjgl.system.MemoryUtil.NULL;

/**
 * The main renderer for the game. This renderer will change its contents (the sub-renderers that it calls) based on the
 * current state of the game.
 */
public class GameRenderer implements Renderer {

    public enum GameState {

        CONSOLE_STATE,
        LEVEL_STATE

    }

    private final GameCamera camera = new GameCamera(this);
    private final Level level;

    private GameState gameState = GameState.CONSOLE_STATE;

    private ConsoleRenderer consoleRenderer;
    private TileRenderer tileRenderer;

    private long window;

    private long lastSecond;
    private int frames;

    private int windowWidth = 1024;
    private int windowHeight = 768;

    private int fboId;
    private int fboShader;
    private int fboTextureId;
    private VertexBuffer fboPositions;
    private VertexBuffer fboTexCoords;

    public GameRenderer(final Level level) {
        this.level = level;
    }

    public void tick() {
        this.consoleRenderer.tick();
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
            this.windowWidth = width;
            this.windowHeight = height;
            glViewport(0, 0, width / 4, height / 4);
            this.camera.setAspectRatio((double) width / height);
//            this.tileRenderer.updateDimensions(width, height);
            this.tileRenderer.tilemapUpdate();
        });
        glViewport(0, 0, 265, 192);
//        glOrtho(0, 256, 192, 0, 1, -1);
        this.camera.setAspectRatio((double) 1024 / 768);
        glfwShowWindow(this.window);

        this.fboShader = ShaderUtil.createShaderProgram("fbo");
        // Make a Framebuffer of a much lower resolution to emulate the display of older consoles
        this.createFBO(true);

        this.consoleRenderer = new ConsoleRenderer();
        this.consoleRenderer.prepare();
        this.tileRenderer = new TileRenderer(this.camera, this.level);
//        this.tileRenderer.prepare();
    }

    @Override
    public void cleanup() {
        this.consoleRenderer.cleanup();
        this.consoleRenderer = null;
        this.tileRenderer.cleanup();
        this.tileRenderer = null;
    }

    @Override
    public void render() {
        glClear(GL_COLOR_BUFFER_BIT);

        // Draw everything to Framebuffer
        glBindFramebuffer(GL_FRAMEBUFFER, this.fboId);

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (this.gameState == GameState.CONSOLE_STATE) {
            this.consoleRenderer.render();
        }

        if (this.gameState == GameState.LEVEL_STATE) {
            this.tileRenderer.render();
        }

        frames++;
        if (System.currentTimeMillis() - this.lastSecond > 1000) {
            this.lastSecond = System.currentTimeMillis();
            System.out.println(frames);
            frames = 0;
        }

        ARBShaderObjects.glUseProgramObjectARB(0);

        // now copy low res framebuffer to window-managed framebuffer
        glBindFramebuffer(GL_READ_FRAMEBUFFER, this.fboId);
        glBindFramebuffer(GL_DRAW_FRAMEBUFFER, 0);

        glBlitFramebuffer(
            0, 0, this.windowWidth / 4, this.windowHeight / 4,
            0, 0, this.windowWidth, this.windowHeight,
            GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT, GL_NEAREST
        );

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

    private void recreateFBO() {
        glDeleteTextures(this.fboTextureId);
        glDeleteFramebuffers(this.fboId);
        this.createFBO(false);
    }

    private void createFBO(final boolean makeBuffers) {
        if (makeBuffers) {
            final float[] positions = {
                0.0f, 0.0f,
                1.0f, 0.0f,
                1.0f, 1.0f,
                0.0f, 1.0f
            };
            final float[] texCoords = {
                0.0f, 1.0f,
                1.0f, 1.0f,
                1.0f, 0.0f,
                0.0f, 0.0f
            };
            this.fboPositions = VertexBuffer.make();
            this.fboPositions.bind();
            this.fboPositions.upload(positions);
            this.fboTexCoords = VertexBuffer.make();
            this.fboTexCoords.bind();
            this.fboTexCoords.upload(texCoords);
        }

        final int renderbufferId = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderbufferId);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT,
            this.windowWidth / 4, this.windowHeight / 4);
        glBindRenderbuffer(GL_RENDERBUFFER, 0);

        // create a texture object
//        GLuint textureId;
        this.fboTextureId = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.fboTextureId);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_CLAMP_TO_EDGE);
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.windowWidth / 4, this.windowHeight / 4, 0,
            GL_RGBA, GL_UNSIGNED_BYTE, 0);
        glBindTexture(GL_TEXTURE_2D, 0);

        this.fboId = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fboId);
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.fboTextureId, 0);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderbufferId);
        System.out.println(glCheckFramebufferStatus(this.fboId));
//        Runtime.getRuntime().exit(0);

        glBindFramebuffer(GL_FRAMEBUFFER, 0);
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
        // =========================
        // TODO: GameCamera allows resizing currently, but this is not necessarily something I want.
//        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE);
        glfwWindowHint(GLFW_RESIZABLE, GL_FALSE);

        final long id = glfwCreateWindow(width, height, title, NULL, NULL); //Does the actual window creation
        if (id == NULL) throw new RuntimeException("Failed to create window");

        return id;
    }

}
