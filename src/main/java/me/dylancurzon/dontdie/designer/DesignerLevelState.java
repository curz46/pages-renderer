package me.dylancurzon.dontdie.designer;

import me.dylancurzon.dontdie.GameState;
import me.dylancurzon.dontdie.gfx.*;
import me.dylancurzon.dontdie.sprite.Sprites;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.container.Axis;
import me.dylancurzon.pages.element.container.ImmutableRatioContainer;
import me.dylancurzon.pages.element.container.ImmutableStackingContainer;
import me.dylancurzon.pages.element.sprite.ImmutableSpriteElement;
import me.dylancurzon.pages.event.UpdateEvent;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.lwjgl.glfw.GLFW.*;

public class DesignerLevelState implements GameState {

    private final LevelDesigner designer;
    private final Level level;

    private GameWindow window;
    private Camera camera;

    private boolean shouldContinueRender;
    private Thread renderingThread;

    public DesignerLevelState(LevelDesigner designer, Level level) {
        this.designer = designer;
        this.level = level;
    }

    @Override
    public void start() {
        camera = new Camera();

        shouldContinueRender = true;

//        final Page page = PageTemplate.builder()
//            .setPosition(Vector2i.of(0, 0))
//            .setSize(Vector2i.of(256, 192))
//            .add(p1 -> LayoutImmutableContainer.builder()
//                .setSize(p1.getSize())
//                .add(7, ImmutableContainer.builder().build())
//                .add(1, p2 -> ImmutableContainer.builder()
//                    .setSize(p2.getSize())
//                    .setCentering(true)
//                    .setFillColor(new Color(0.1f, 0.1f, 0.1f, 1.0f))
//                    .add(p3 -> ImmutableContainer.builder()
//                        .setSize(p3.getSize().setY(16))
////                            .setCentering(true)
//                        .setPadding(Spacing.of(5, 0))
//                        .setPositioning(Positioning.INLINE)
//                        .add(SpriteImmutableElement.builder()
//                            .setMargin(Spacing.of(0, 0, 5, 0))
//                            .setAnimatedSprite(Sprites.GUI_CURSOR_SELECTED)
//                            .build())
//                        .add(SpriteImmutableElement.builder()
//                            .setAnimatedSprite(Sprites.GUI_PAINTBRUSH)
//                            .build())
//                        .build())
//                    .build())
//                .build())
//            .build()
//            .asMutable();

        AtomicBoolean shouldUpdate = new AtomicBoolean();
        AtomicInteger ticks = new AtomicInteger();

        ImmutableSpriteElement sampleSprite = new ImmutableSpriteElement.Builder()
            .setSprite(Sprites.GUI_PAINTBRUSH)
            .build();

        Page page = new PageTemplate.Builder()
            .setPosition(Vector2i.of(0, 0))
            .setFixedSize(Vector2i.of(256, 192))
            .add(p1 -> new ImmutableRatioContainer.Builder()
                .fillParentContainer()
                .add(new ImmutableStackingContainer.Builder().build(), 7)
                .add(p2 -> new ImmutableStackingContainer.Builder()
                    .fillAllocatedSize()
                    .setFixedSize(Vector2i.of(0, 0))
                    .setDecoration(ElementDecoration.builder()
                        .setFillColor(Color.GRAY)
                        .build())
                    .setMajorAxis(Axis.HORIZONTAL)
                    .setCenterOnY(true)
                    .add(new ImmutableButtonElement.Builder(Sprites.GUI_CURSOR, Sprites.GUI_CURSOR_SELECTED)
                        .setMargin(Spacing.of(5, 0, 0, 0))
                        .build())
                    .add(new ImmutableButtonElement.Builder(Sprites.GUI_PAINTBRUSH, Sprites.GUI_PAINTBRUSH_SELECTED)
                        .setMargin(Spacing.of(5, 0, 0, 0))
                        .build())
                    .build(), 1)
                .build())
            .build()
            .create();

        page.subscribe(UpdateEvent.class, e -> shouldUpdate.set(true));

        // Setup rendering
        renderingThread = (new Thread(() -> {
            window = new GameWindow();
            window.initialize(true);

            window.registerClickListener(screenPosition -> {
                Vector2i virtualPosition = screenPosition.div(4).toInt();
                page.click(virtualPosition, MouseButton.LEFT_MOUSE_BUTTON);
            });

            PageRenderer pageRenderer = new PageRenderer(page);
            RootRenderer renderer = new RootRenderer(
                window,
                new Renderer[] {
                    new TileRenderer(camera, level),
                    pageRenderer
                }
            );

            renderer.prepare();
            pageRenderer.update();

            while (shouldContinueRender) {
                if (shouldUpdate.getAndSet(false) || window.isKeyPressed(KeyEvent.VK_U)) {
                    pageRenderer.update();
                }

                renderer.render();
                page.setMousePosition(window.getMousePosition().div(4).toInt());
            }

            renderer.cleanup();
            window.destroy();
            window = null;
        }));
        renderingThread.start();

        double tickInterval = 1000 / 144.0;
        long lastTick = 0;
        while (shouldContinueRender) {
            if (window == null) continue;
            long now = System.currentTimeMillis();
            if (now - lastTick > tickInterval) {
                lastTick = now;

                double v = 0.04;
                Vector2d delta = Vector2d.of(
                    window.isKeyPressed(GLFW_KEY_A) ? -v : window.isKeyPressed(GLFW_KEY_D) ? +v : 0,
                    window.isKeyPressed(GLFW_KEY_W) ? +v : window.isKeyPressed(GLFW_KEY_S) ? -v : 0
                );
                if (delta.getX() != 0 || delta.getY() != 0) {
                    camera.transform(delta);
                }

                page.tick();
            }
        }
    }

    @Override
    public void finish() {
        shouldContinueRender = false;
        try {
            renderingThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while joining render thread: ", e);
        }
    }

}
