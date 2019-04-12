package me.dylancurzon.dontdie.designer.gameState;

import me.dylancurzon.dontdie.GameState;
import me.dylancurzon.dontdie.designer.LevelDesigner;
import me.dylancurzon.dontdie.designer.DesignerGame;
import me.dylancurzon.dontdie.gfx.Camera;
import me.dylancurzon.dontdie.gfx.GameWindow;
import me.dylancurzon.dontdie.gfx.Renderer;
import me.dylancurzon.dontdie.gfx.RootRenderer;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.pages.util.Vector2i;

import java.util.concurrent.atomic.AtomicBoolean;

public class DesignerLevelState implements GameState {

    private final DesignerGame game;
    private final Level level;

    private GameWindow window;
    private Camera camera;

    private final AtomicBoolean shouldContinueRender = new AtomicBoolean();
    private final AtomicBoolean renderThreadReady = new AtomicBoolean();
    private Thread renderingThread;

    private LevelDesigner designer;
    private RootRenderer rootRenderer;

    public DesignerLevelState(DesignerGame game, Level level) {
        this.game = game;
        this.level = level;
    }

    @Override
    public void start() {
        camera = new Camera();

        shouldContinueRender.set(true);

        // Setup rendering
        renderingThread = (new Thread(() -> {
            window = new GameWindow();
            window.initialize(true);

            designer = new LevelDesigner(window, level, camera);

            rootRenderer = new RootRenderer(
                window,
                new Renderer[] { designer }
            );
            rootRenderer.prepare();

            window.registerClickListener(screenPosition -> {
                Vector2i virtualPosition = screenPosition.div(4).toInt();
                designer.click(virtualPosition);
            });

            renderThreadReady.set(true);

            while (shouldContinueRender.get()) {
                rootRenderer.update();
                rootRenderer.render();
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
