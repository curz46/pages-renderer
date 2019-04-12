package me.dylancurzon.dontdie.designer;

import me.dylancurzon.dontdie.Tickable;
import me.dylancurzon.dontdie.designer.button.ImmutableButtonElement;
import me.dylancurzon.dontdie.designer.button.MutableButtonElement;
import me.dylancurzon.dontdie.designer.tool.PaintTool;
import me.dylancurzon.dontdie.designer.tool.SelectTool;
import me.dylancurzon.dontdie.designer.tool.Tool;
import me.dylancurzon.dontdie.gfx.*;
import me.dylancurzon.dontdie.sprite.Sprites;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.container.Axis;
import me.dylancurzon.pages.element.container.ImmutableAbsoluteContainer;
import me.dylancurzon.pages.element.container.ImmutableOverlayContainer;
import me.dylancurzon.pages.element.container.ImmutableStackingContainer;
import me.dylancurzon.pages.element.sprite.ImmutableSpriteElement;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class LevelDesigner extends Renderer implements Tickable {

    private static final PageTemplate ACTION_BAR = new PageTemplate.Builder()
        .setPosition(Vector2i.of(0, 0))
        .setFixedSize(Vector2i.of(256, 192))
        .add(p1 -> new ImmutableAbsoluteContainer.Builder()
            .fillParentContainer()
            .add(new ImmutableOverlayContainer.Builder()
                .setFixedSize(Vector2i.of(
                    Sprites.DESIGNER_ACTION_BAR.getWidth(),
                    Sprites.DESIGNER_ACTION_BAR.getHeight()
                ))
                .add(new ImmutableSpriteElement.Builder()
                    .setSprite(Sprites.DESIGNER_ACTION_BAR)
                    .build())
                .add(new ImmutableStackingContainer.Builder()
                    .fillAllocatedSize()
                    .setFixedSize(Vector2i.of(0, 0))
                    .setMajorAxis(Axis.HORIZONTAL)
                    .setCenterOnY(true)
                    .add(new ImmutableButtonElement.Builder(
                            Sprites.GUI_CURSOR, Sprites.GUI_CURSOR_HOVER, Sprites.GUI_CURSOR_SELECTED)
                        .setTag("selectButton")
                        .setMargin(Spacing.of(5, 0, 0, 0))
                        .build())
                    .add(new ImmutableButtonElement.Builder(
                            Sprites.GUI_PAINTBRUSH, Sprites.GUI_PAINTBRUSH_HOVER, Sprites.GUI_PAINTBRUSH_SELECTED)
                        .setTag("paintButton")
                        .setMargin(Spacing.of(5, 0, 0, 0))
                        .build())
                    .build())
                .build(), Vector2i.of(0, 168))
            .build())
        .build();

    private final GameWindow window;
    private final Level level;
    private final Camera camera;

    @Nullable
    private Tool selectedTool;

    private final Page actionBar = ACTION_BAR.create();

    private final TileRenderer tileRenderer;

    private final Map<Page, PageRenderer> pageRenderers = new HashMap<>();

    public LevelDesigner(GameWindow window, Level level, Camera camera) {
        this.window = window;
        this.level = level;
        this.camera = camera;

        tileRenderer = new TileRenderer(camera, level);

        MutableButtonElement selectButton = actionBar
            .queryElement("selectButton", MutableButtonElement.class)
            .orElseThrow();
        MutableButtonElement paintButton = actionBar
            .queryElement("paintButton", MutableButtonElement.class)
            .orElseThrow();
        MutableButtonElement[] buttons = new MutableButtonElement[] { selectButton, paintButton };
        selectButton.doOnClick(e -> {
            Arrays.stream(buttons).forEach(button -> button.setSelected(false));
            selectButton.setSelected(true);
            selectButton.propagateUpdate();
            if (selectedTool != null) {
                selectedTool.cleanup();
            }
            selectedTool = new SelectTool(window, level, camera);
            selectedTool.prepare();
        });
        paintButton.doOnClick(e -> {
            Arrays.stream(buttons).forEach(button -> button.setSelected(false));
            paintButton.setSelected(true);
            paintButton.propagateUpdate();
            if (selectedTool != null) {
                selectedTool.cleanup();
            }
            selectedTool = new PaintTool(window, level, camera, tileRenderer);
            selectedTool.prepare();
        });

        PageRenderer actionBarRenderer = new PageRenderer(actionBar);
        actionBar.doOnUpdate(() -> actionBarRenderer.setDirty(true));

        pageRenderers.put(actionBar, actionBarRenderer);
    }

    public void click(Vector2i position) {
        actionBar.click(position, MouseButton.LEFT_MOUSE_BUTTON);
        if (selectedTool != null) selectedTool.click(position);
    }

    @Override
    public void prepare() {
        pageRenderers.values().forEach(PageRenderer::prepare);

        tileRenderer.prepare();

        if (selectedTool == null) {
            // Initial tool: select
            MutableButtonElement selectButton = actionBar
                .queryElement("selectButton", MutableButtonElement.class)
                .orElseThrow();
            selectButton.setSelected(true);

            selectedTool = new SelectTool(window, level, camera);
            selectedTool.prepare();
        }
    }

    @Override
    public void cleanup() {
        for (PageRenderer renderer : pageRenderers.values()) {
            renderer.cleanup();
        }
        tileRenderer.cleanup();
        if (selectedTool != null) selectedTool.cleanup();
    }

    @Override
    public void update() {
        for (PageRenderer renderer : pageRenderers.values()) {
            if (renderer.isDirty()) {
                renderer.setDirty(false);
                renderer.update();
            }
        }
        if (tileRenderer.isDirty()) {
            tileRenderer.setDirty(false);
            tileRenderer.update();
        }
        if (selectedTool != null && selectedTool.isDirty()) {
            selectedTool.setDirty(false);
            selectedTool.update();
        }
    }

    @Override
    public void render() {
        tileRenderer.render();
        if (selectedTool != null) selectedTool.render();
        for (PageRenderer renderer : pageRenderers.values()) {
            renderer.render();
        }
    }

    @Override
    public void tick() {
        if (selectedTool != null) selectedTool.tick();

        double v = 0.04;
        Vector2d delta = Vector2d.of(
            window.isKeyPressed(GLFW_KEY_A) ? -v : window.isKeyPressed(GLFW_KEY_D) ? +v : 0,
            window.isKeyPressed(GLFW_KEY_W) ? +v : window.isKeyPressed(GLFW_KEY_S) ? -v : 0
        );
        if (delta.getX() != 0 || delta.getY() != 0) {
            camera.transform(delta);
        }

        if (window.getMousePosition() == null) {
            actionBar.setMousePosition(null);
        } else {
            Vector2d screenPosition = window.getMousePosition();
            // TODO: Hardcoding resolution, bad
            Vector2i virtualPosition = screenPosition.div(4).toInt();

            actionBar.setMousePosition(virtualPosition);
        }

        actionBar.tick();
    }

    public void setDirty() {
        throw new UnsupportedOperationException("Designer's Renderer cannot be set as dirty");
    }

    public boolean isDirty() {
        for (PageRenderer renderer : pageRenderers.values()) {
            if (renderer.isDirty()) {
                return true;
            }
        }

        if (tileRenderer.isDirty()) {
            return true;
        }

        return selectedTool != null && selectedTool.isDirty();
    }

}
