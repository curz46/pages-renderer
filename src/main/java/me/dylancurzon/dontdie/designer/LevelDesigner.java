package me.dylancurzon.dontdie.designer;

import me.dylancurzon.dontdie.Game;
import me.dylancurzon.dontdie.GameState;
import me.dylancurzon.dontdie.gfx.*;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.dontdie.tile.TileType;
import me.dylancurzon.dontdie.Tickable;
import me.dylancurzon.dontdie.util.Vector2d;
import me.dylancurzon.dontdie.util.Vector2i;

import static org.lwjgl.glfw.GLFW.*;

public class LevelDesigner implements Game, Tickable {

    private Camera camera;
    private Level level;

    private GameWindow window;

    private RootRenderer menuRenderer;
    private RootRenderer levelRenderer;

    private Thread renderThread;

    private boolean shouldRender;
    private boolean killed;

    private GameState currentState;

    @Override
    public void launch() {
        this.camera = new Camera();
        this.level = new Level();
        for (int x = -200; x < 200; x++) {
            for (int y = -200; y < 200; y++) {
//                this.level.setTile(Vector2i.of(x, y), (x + y) % 2 == 0 ? TileType.UNDEFINED : TileType.STONEBRICKS);
                this.level.setTile(Vector2i.of(x, y), TileType.STONEBRICKS);
            }
        }

        this.window = new GameWindow();
        this.window.initialize(true);

//        long lastTick = 0;
//        while (!this.killed) {
//            final long now = System.currentTimeMillis();
//            if (now - lastTick > 1000.0 / 144) { // 144tps
//                this.tick();
//                lastTick = now;
//            }
//        }

        this.currentState = new LevelState(this.camera, this.level);
        this.currentState.start();
    }

    @Override
    public void kill() {
        this.killed = true;
        try {
            this.renderThread.join();
        } catch (final InterruptedException e) {
            e.printStackTrace();
        } finally {
            this.window.destroy();

            this.levelRenderer = null;
            this.window = null;
        }
    }

    @Override
    public void tick() {

    }

    public class MenuState implements GameState {

        private RootRenderer renderer;
        private boolean running;

        @Override
        public void start() {
            this.renderer = new RootRenderer(
                LevelDesigner.this.window,
                new Renderer[] { new DesignerMenuRenderer() }
            );
            LevelDesigner.this.shouldRender = true;
            LevelDesigner.this.renderThread = new Thread(() -> {
                this.renderer.prepare();
                while (LevelDesigner.this.shouldRender) {
                    this.renderer.render();
                }
                this.renderer.cleanup();
            });
            LevelDesigner.this.renderThread.start();

            while (this.running) {
                this.tick();
            }
        }

        @Override
        public void finish() {
            LevelDesigner.this.shouldRender = false;
            try {
                LevelDesigner.this.renderThread.join();
            } catch (final InterruptedException e) {
                throw new RuntimeException("Failed to join render thread in MenuState: ", e);
            }
        }

        @Override
        public void tick() {
            long lastTick = 0;
            while (!LevelDesigner.this.killed) {
                final long now = System.currentTimeMillis();
                if (now - lastTick > 1000.0 / 144) { // 144tps
                    this.tick();
                    lastTick = now;
                }
            }
        }

    }

    class LevelState implements GameState {

        private final Camera camera;
        private final Level level;

        private RootRenderer renderer;

        LevelState(final Camera camera, final Level level) {
            this.camera = camera;
            this.level = level;
        }

        @Override
        public void start() {
            this.renderer = new RootRenderer(
                LevelDesigner.this.window,
                new Renderer[] { new TileRenderer(this.camera, this.level) }
            );
            LevelDesigner.this.shouldRender = true;
//            LevelDesigner.this.renderThread = new Thread(() -> {
//                this.renderer.prepare();
//                while (LevelDesigner.this.shouldRender) {
//                    this.renderer.render();
//                }
//                this.renderer.cleanup();
//            });
//            LevelDesigner.this.renderThread.start();

//            long lastTick = 0;
//            while (!LevelDesigner.this.killed) {
//                final long now = System.currentTimeMillis();
//                if (now - lastTick > 1000.0 / 144) { // 144tps
//                    this.tick();
//                    lastTick = now;
//                }
//            }

            this.renderer.prepare();
            long lastTick = 0;
            while (LevelDesigner.this.shouldRender) {
                this.renderer.render();
                final long now = System.currentTimeMillis();
                if (now - lastTick > 1000.0 / 144) { // 144tps
                    this.tick();
                    lastTick = now;
                }
            }
            this.renderer.cleanup();
        }

        @Override
        public void finish() {

        }

        @Override
        public void tick() {
            final double v = 0.02;
            final Vector2d delta = Vector2d.of(
                LevelDesigner.this.window.isKeyPressed(GLFW_KEY_A) ? -v : LevelDesigner.this.window.isKeyPressed(GLFW_KEY_D) ? +v : 0,
                LevelDesigner.this.window.isKeyPressed(GLFW_KEY_W) ? +v : LevelDesigner.this.window.isKeyPressed(GLFW_KEY_S) ? -v : 0
            );
            if (delta.getX() != 0 || delta.getY() != 0) {
                this.camera.transform(delta);
            }
        }

    }

}
