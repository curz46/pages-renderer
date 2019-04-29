package me.dylancurzon.testgame.designer.gameState;

import me.dylancurzon.testgame.GameState;
import me.dylancurzon.openglrenderer.gfx.opengl.FrameBuffer;
import me.dylancurzon.openglrenderer.gfx.window.GLFWWindow;
import me.dylancurzon.openglrenderer.gfx.window.GLFWWindowOptions;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.testgame.designer.DesignerGame;
import me.dylancurzon.testgame.designer.LevelDesigner;
import me.dylancurzon.testgame.gfx.Camera;
import me.dylancurzon.openglrenderer.gfx.window.VirtualWindow;
import me.dylancurzon.testgame.tile.Level;
import org.lwjgl.opengl.GL;

import java.util.concurrent.atomic.AtomicBoolean;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class DesignerLevelState implements GameState {

    private final DesignerGame game;
    private final Level level;

    private GLFWWindow window;
    private Camera camera;

    private final AtomicBoolean shouldContinueRender = new AtomicBoolean();
    private final AtomicBoolean renderThreadReady = new AtomicBoolean();
    private Thread renderingThread;

    private LevelDesigner designer;
//    private RootRenderer rootRenderer;

    public DesignerLevelState(DesignerGame game, Level level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void start() {
        shouldContinueRender.set(true);

        // Setup rendering
        renderingThread = (new Thread(() -> {
            window = new GLFWWindow(GLFWWindowOptions.builder()
                .setDimensions(Vector2i.of(1024, 768))
                .setTitle("Level Designer")
                .setVisible(true)
                .build());
            window.initialize();
            window.focus();

            Vector2i virtualDimensions = Vector2i.of(256, 192);
            VirtualWindow virtualWindow = new VirtualWindow(window, virtualDimensions);

            glfwMakeContextCurrent(window.getId());
            GL.createCapabilities(false);

            camera = new Camera(virtualWindow.getDimensions());

            designer = new LevelDesigner(virtualWindow, level, camera);
            designer.prepare();

//            rootRenderer = new RootRenderer(
//                window,
//                new Renderer[] { designer }
//            );
//            rootRenderer.prepare();

            window.doOnMousePress(event -> designer.click(virtualWindow.getMousePosition().toInt()));
            window.doOnMouseScroll(event -> designer.scroll(event.getOffset()));

            renderThreadReady.set(true);

            FrameBuffer fbo = FrameBuffer.make(virtualDimensions.getX(), virtualDimensions.getY());
            glViewport(0, 0, virtualDimensions.getX(), virtualDimensions.getY());

            while (shouldContinueRender.get()) {
//                rootRenderer.update();
                designer.update();

                fbo.bind();

                glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
                glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

//                rootRenderer.render();

                designer.render();

                FrameBuffer.unbind();
                fbo.copy(window);

                glfwSwapBuffers(window.getId());
                glfwPollEvents();
            }

            window.destroy();
            window = null;
        }));
        renderingThread.start();

        double tickInterval = 1000 / 144.0;
        long lastTick = 0;
        while (shouldContinueRender.get()) {
            if (!renderThreadReady.get()) continue;

            long now = System.currentTimeMillis();
            if (now - lastTick > tickInterval) {
                lastTick = now;
                designer.tick();
            }
        }
    }

    @Override
    public void finish() {
        shouldContinueRender.set(false);
        try {
            renderingThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while joining render thread: ", e);
        }
    }

}
