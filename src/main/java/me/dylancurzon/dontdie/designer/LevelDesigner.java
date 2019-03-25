package me.dylancurzon.dontdie.designer;

import me.dylancurzon.dontdie.Game;
import me.dylancurzon.dontdie.GameState;
import me.dylancurzon.dontdie.Tickable;
import me.dylancurzon.dontdie.gfx.*;
import me.dylancurzon.dontdie.gfx.page.Page;
import me.dylancurzon.dontdie.gfx.page.PageTemplate;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.SpriteImmutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.TextImmutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.container.DefaultImmutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.container.ImmutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.container.LayoutImmutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.container.Positioning;
import me.dylancurzon.dontdie.sprite.SpriteSheets;
import me.dylancurzon.dontdie.sprite.Sprites;
import me.dylancurzon.dontdie.sprite.TextSprite;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.dontdie.tile.TileType;
import me.dylancurzon.dontdie.util.Vector2d;
import me.dylancurzon.dontdie.util.Vector2i;

import java.awt.*;

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

        this.currentState = new MenuState();
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
            final Page page = PageTemplate.builder()
                .setPosition(Vector2i.of(0, 0))
                .setSize(Vector2i.of(256, 192))
//                .setCentering(true)
                .add(p1 -> LayoutImmutableContainer.builder()
                    .setSize(p1.getSize())
                    .add(1, p2 -> ImmutableContainer.builder()
                        .setSize(p2.getSize())
                        .setLineColor(Color.WHITE)
                        .setCentering(true)
                        .add(p3 -> LayoutImmutableContainer.builder()
                            .setSize(p3.getSize())
                            .setCentering(true)
                            .add(1, TextImmutableElement.builder()
                                .setText(TextSprite.of("guess who has mastered", 2))
                                .build())
                            .add(1, TextImmutableElement.builder()
                                .setText(TextSprite.of("pages in opengl", 2))
                                .build())
                            .build())
                        .build())
                    .add(1, p2 -> ImmutableContainer.builder()
                        .setSize(p2.getSize())
//                        .setFillColor(Color.RED)
                        .setCentering(true)
                        .setFillColor(Color.BLUE)
                        .add(p3 -> ImmutableContainer.builder()
                            .setFillColor(Color.GREEN)
                            .setLineColor(Color.RED)
                            .add(TextImmutableElement.builder()
                                .setMargin(Spacing.of(10))
                                .setText(TextSprite.of("this guy", 2))
                                .build())
                            .build())
                        .build())
                    .build())
                .build().asMutable();

//            final TextRenderer renderer = new TextRenderer();
//            renderer.getSprites().put(TextSprite.of("Helloworld", 2), Vector2i.of(100, 100));

            this.renderer = new RootRenderer(
                LevelDesigner.this.window,
                new Renderer[] { new PageRenderer(page) }
//                new Renderer[] { new ConsoleRenderer() }
//                new Renderer[] { renderer }
            );
            LevelDesigner.this.shouldRender = true;

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
            LevelDesigner.this.shouldRender = false;
            try {
                LevelDesigner.this.renderThread.join();
            } catch (final InterruptedException e) {
                throw new RuntimeException("Failed to join render thread in MenuState: ", e);
            }
        }

        @Override
        public void tick() {
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
