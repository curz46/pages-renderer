package me.dylancurzon.testgame.designer;

import me.dylancurzon.dontdie.Game;
import me.dylancurzon.dontdie.GameState;
import me.dylancurzon.dontdie.HasState;
import me.dylancurzon.dontdie.gfx.GameWindow;
import me.dylancurzon.dontdie.gfx.RootRenderer;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.testgame.designer.gameState.DesignerMenuState;
import me.dylancurzon.testgame.gfx.Camera;
import me.dylancurzon.testgame.tile.Level;
import me.dylancurzon.testgame.tile.TileType;

public class DesignerGame implements Game, HasState {

    private Camera camera;
    private Level level;

    private GameWindow window;

    private RootRenderer menuRenderer;
    private RootRenderer levelRenderer;

    private boolean shouldRender;
    private boolean killed;

    private GameState currentState;

    @Override
    public void launch() {
        camera = new Camera();
        level = new Level();
        for (int x = -200; x < 200; x++) {
            for (int y = -200; y < 200; y++) {
//                this.level.setTile(Vector2i.of(x, y), (x + y) % 2 == 0 ? TileType.UNDEFINED : TileType.STONEBRICKS);
                level.setTile(Vector2i.of(x, y), TileType.STONEBRICKS);
            }
        }

//        this.window = new GameWindow();
//        this.window.initialize(true);

        setGameState(new DesignerMenuState(this));
    }

    @Override
    public void kill() {
        shouldRender = false;
        killed = true;
        window.destroy();

        levelRenderer = null;
        window = null;
    }

    @Override
    public void setGameState(GameState gameState) {
        if (currentState != null) {
            currentState.finish();
        }
        currentState = gameState;
        currentState.start();
    }

//    public class MenuState implements GameState {
//
//        private RootRenderer renderer;
//        private boolean running;
//
//        @Override
//        public void start() {
////            final Page page = PageTemplate.builder()
////                .setSize(Vector2i.of(256, 192))
////                .setPosition(Vector2i.of(0, 0))
////                .add(p1 -> LayoutImmutableContainer.builder()
////                    .setSize(p1.getSize())
////                    .add(1, ImmutableContainer.builder().build())
////                    .add(4, p2 -> LayoutImmutableContainer.builder()
////                        .setSize(p2.getSize())
////                        .setCentering(true)
////                        .add(1, ImmutableContainer.builder()
////                            .setSize(Vector2i.of(60, 20))
////                            .setCentering(true)
////                            .setLineColor(Color.WHITE)
////                            .setInteractOptions(InteractOptions.builder()
////                                .click(mut -> {
////                                    MenuState.this.finish();
////                                    LevelDesigner.this.currentState = new LevelState(LevelDesigner.this.camera, new Level());
////                                    LevelDesigner.this.currentState.start();
////                                })
////                                .build())
////                            .add(TextImmutableElement.builder()
////                                .setText(TextSprite.of("PLAY", 2))
////                                .build())
////                            .build())
////                        .add(1, ImmutableContainer.builder()
////                            .setSize(Vector2i.of(60, 20))
////                            .setCentering(true)
////                            .setLineColor(Color.WHITE)
////                            .setInteractOptions(InteractOptions.builder()
////                                .click(mut -> System.out.println("OPTIONS!"))
////                                .build())
////                            .add(TextImmutableElement.builder()
////                                .setText(TextSprite.of("OPTIONS", 2))
////                                .build())
////                            .build())
////                        .add(1, ImmutableContainer.builder()
////                            .setSize(Vector2i.of(60, 20))
////                            .setCentering(true)
////                            .setLineColor(Color.WHITE)
////                            .setInteractOptions(InteractOptions.builder()
////                                .click(mut -> System.out.println("QUIT!"))
////                                .build())
////                            .add(TextImmutableElement.builder()
////                                .setText(TextSprite.of("QUIT", 2))
////                                .build())
////                            .build())
////                        .build())
////                    .add(1, ImmutableContainer.builder().build())
////                    .build())
////                .build()
////                .asMutable();
//
////            LevelDesigner.this.window.registerClickListener(
////                screenPosition -> page.click(
////                    screenPosition
////                        .div(Vector2d.of(GameWindow.WIDTH, GameWindow.HEIGHT))
////                        .mul(Vector2d.of(GameWindow.VIRTUAL_WIDTH, GameWindow.VIRTUAL_HEIGHT))
////                        .toInt())
////            );
//
////            this.renderer = new RootRenderer(
////                LevelDesigner.this.window,
////                new Renderer[] { new PageRenderer(page) }
////            );
//            LevelDesigner.this.shouldRender = true;
//
//            this.renderer.prepare();
//            long lastTick = 0;
//            while (LevelDesigner.this.shouldRender) {
//            //                final Vector2d mousePosition = LevelDesigner.this.window.getMousePosition();
//                this.renderer.render();
//                final long now = System.currentTimeMillis();
//                if (now - lastTick > 1000.0 / 144) { // 144tps
//                    this.tick();
//                    lastTick = now;
//                }
//            }
//            this.renderer.cleanup();
//        }
//
//        @Override
//        public void finish() {
//            LevelDesigner.this.shouldRender = false;
////            try {
////                LevelDesigner.this.renderThread.join();
////            } catch (final InterruptedException e) {
////                throw new RuntimeException("Failed to join render thread in MenuState: ", e);
////            }
//        }
//
//        @Override
//        public void tick() {
//        }
//
//    }

//    class LevelState implements GameState {
//
//        private final Camera camera;
//        private final Level level;
//
//        private RootRenderer renderer;
//
//        LevelState(final Camera camera, final Level level) {
//            this.camera = camera;
//            this.level = level;
//        }
//
//        @Override
//        public void start() {
//            final Page page = PageTemplate.builder()
//                    .setPosition(Vector2i.of(0, 0))
//                    .setSize(Vector2i.of(256, 192))
//                    .add(p1 -> LayoutImmutableContainer.builder()
//                            .setSize(p1.getSize())
//                            .add(6, ImmutableContainer.builder().build())
//                            .add(1, p2 -> ImmutableContainer.builder()
//                                    .setSize(p2.getSize())
//                                    .setCentering(true)
//                                    .setFillColor(new Color(30, 30, 30))
//                                    .add(p3 -> ImmutableContainer.builder()
//                                            .setSize(p3.getSize().setY(16))
////                            .setCentering(true)
//                                            .setPadding(Spacing.of(10, 0))
//                                            .setPositioning(Positioning.INLINE)
//                                            .add(SpriteImmutableElement.builder()
//                                                    .setMargin(Spacing.of(0, 0, 10, 0))
//                                                    .setAnimatedSprite(Sprites.GUI_CURSOR)
//                                                    .build())
//                                            .add(SpriteImmutableElement.builder()
//                                                    .setAnimatedSprite(Sprites.GUI_PAINTBRUSH)
//                                                    .build())
//                                            .build())
//                                    .build())
//                            .build())
//                    .build()
//                    .asMutable();
//
//            this.renderer = new RootRenderer(
//                LevelDesigner.this.window,
//                new Renderer[] {
//                    new PageRenderer(page),
//                    new TileRenderer(this.camera, this.level)
//                }
//            );
//            LevelDesigner.this.shouldRender = true;
//
//            this.renderer.prepare();
//            long lastTick = 0;
//            while (LevelDesigner.this.shouldRender) {
//                this.renderer.render();
//                final long now = System.currentTimeMillis();
//                if (now - lastTick > 1000.0 / 144) { // 144tps
//                    this.tick();
//                    lastTick = now;
//                }
//            }
//            this.renderer.cleanup();
//        }
//
//        @Override
//        public void finish() {
//            LevelDesigner.this.shouldRender = false;
//        }
//
//        @Override
//        public void tick() {
//            final double v = 0.02;
//            final Vector2d delta = Vector2d.of(
//                LevelDesigner.this.window.isKeyPressed(GLFW_KEY_A) ? -v : LevelDesigner.this.window.isKeyPressed(GLFW_KEY_D) ? +v : 0,
//                LevelDesigner.this.window.isKeyPressed(GLFW_KEY_W) ? +v : LevelDesigner.this.window.isKeyPressed(GLFW_KEY_S) ? -v : 0
//            );
//            if (delta.getX() != 0 || delta.getY() != 0) {
//                this.camera.transform(delta);
//            }
//        }
//
//    }

}
