package me.dylancurzon.dontdie.designer;

import me.dylancurzon.dontdie.GameState;
import me.dylancurzon.dontdie.gfx.*;
import me.dylancurzon.dontdie.gfx.page.Page;
import me.dylancurzon.dontdie.gfx.page.PageTemplate;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.SpriteImmutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.container.ImmutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.container.LayoutImmutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.container.Positioning;
import me.dylancurzon.dontdie.sprite.Sprites;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.dontdie.util.Vector2d;
import me.dylancurzon.dontdie.util.Vector2i;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;

public class DesignerLevelState implements GameState {

    private final LevelDesigner designer;
    private final Level level;

    private GameWindow window;
    private Camera camera;

    private boolean shouldContinueRender;
    private Thread renderingThread;

    public DesignerLevelState(final LevelDesigner designer, final Level level) {
        this.designer = designer;
        this.level = level;
    }

    @Override
    public void start() {
        this.camera = new Camera();

        this.shouldContinueRender = true;

        final Page page = PageTemplate.builder()
            .setPosition(Vector2i.of(0, 0))
            .setSize(Vector2i.of(256, 192))
            .add(p1 -> LayoutImmutableContainer.builder()
                .setSize(p1.getSize())
                .add(7, ImmutableContainer.builder().build())
                .add(1, p2 -> ImmutableContainer.builder()
                    .setSize(p2.getSize())
                    .setCentering(true)
                    .setFillColor(new Color(0.1f, 0.1f, 0.1f, 1.0f))
                    .add(p3 -> ImmutableContainer.builder()
                        .setSize(p3.getSize().setY(16))
//                            .setCentering(true)
                        .setPadding(Spacing.of(5, 0))
                        .setPositioning(Positioning.INLINE)
                        .add(SpriteImmutableElement.builder()
                            .setMargin(Spacing.of(0, 0, 5, 0))
                            .setAnimatedSprite(Sprites.GUI_CURSOR_SELECTED)
                            .build())
                        .add(SpriteImmutableElement.builder()
                            .setAnimatedSprite(Sprites.GUI_PAINTBRUSH)
                            .build())
                        .build())
                    .build())
                .build())
            .build()
            .asMutable();

        // Setup rendering
        this.renderingThread = (new Thread(() -> {
            DesignerLevelState.this.window = new GameWindow();
            DesignerLevelState.this.window.initialize(true);

            final RootRenderer renderer = new RootRenderer(
                DesignerLevelState.this.window,
                new Renderer[] {
                    new TileRenderer(DesignerLevelState.this.camera, DesignerLevelState.this.level),
                    new PageRenderer(page)
                }
            );

            renderer.prepare();

            while (DesignerLevelState.this.shouldContinueRender) {
                renderer.render();
            }

            renderer.cleanup();
            DesignerLevelState.this.window.destroy();
            DesignerLevelState.this.window = null;
        }));
        this.renderingThread.start();

        final double tickInterval = 1000 / 144.0;
        long lastTick = 0;
        while (this.shouldContinueRender) {
            if (this.window == null) continue;
            final long now = System.currentTimeMillis();
            if (now - lastTick > tickInterval) {
                lastTick = now;

                final double v = 0.04;
                final Vector2d delta = Vector2d.of(
                    this.window.isKeyPressed(GLFW_KEY_A) ? -v : this.window.isKeyPressed(GLFW_KEY_D) ? +v : 0,
                    this.window.isKeyPressed(GLFW_KEY_W) ? +v : this.window.isKeyPressed(GLFW_KEY_S) ? -v : 0
                );
                if (delta.getX() != 0 || delta.getY() != 0) {
                    this.camera.transform(delta);
                }
            }
        }
    }

    @Override
    public void finish() {
        this.shouldContinueRender = false;
        try {
            this.renderingThread.join();
        } catch (final InterruptedException e) {
            throw new RuntimeException("Interrupted while joining render thread: ", e);
        }
    }

}
