package me.dylancurzon.dontdie.designer;

import me.dylancurzon.dontdie.Tickable;
import me.dylancurzon.dontdie.designer.button.ImmutableButtonElement;
import me.dylancurzon.dontdie.designer.button.MutableButtonElement;
import me.dylancurzon.dontdie.designer.tool.PaintTool;
import me.dylancurzon.dontdie.designer.tool.SelectTool;
import me.dylancurzon.dontdie.designer.tool.Tool;
import me.dylancurzon.dontdie.gfx.*;
import me.dylancurzon.dontdie.gfx.page.PageRenderer;
import me.dylancurzon.dontdie.sprite.Sprites;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.dontdie.tile.TileType;
import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.*;
import me.dylancurzon.pages.element.sprite.ImmutableSpriteElement;
import me.dylancurzon.pages.element.sprite.MutableSpriteElement;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.glfw.GLFW.*;

public class LevelDesigner extends Renderer implements Tickable {

    private final Map<TileType, MutableStackingContainer> tileBarSelectedElements = new HashMap<>();
    private final Page page = new PageTemplate.Builder()
        .setPosition(Vector2i.of(0, 0))
        .setFixedSize(Vector2i.of(256, 192))
        .add(p1 -> new ImmutableAbsoluteContainer.Builder()
            .add(p2 -> new ImmutableOverlayContainer.Builder()
                .setTag("tileBar")
                .setFixedSize(Vector2i.of(256, Sprites.DESIGNER_TILE_BAR.getHeight()))
                .setCenterOnX(true)
                .setVisible(false)
                .add(new ImmutableSpriteElement.Builder()
                    .setSprite(Sprites.DESIGNER_TILE_BAR)
                    .build())
                .add(new ImmutableStackingContainer.Builder()
                    .setFixedSize(Vector2i.of(Sprites.DESIGNER_TILE_BAR.getWidth() - 10 , Sprites.DESIGNER_TILE_BAR.getHeight()))
                    .setMajorAxis(Axis.HORIZONTAL)
                    .setCenterOnY(true)
                    .doOnCreate(container -> {
                        TileType[] types = TileType.values();
                        for (int i = 0; i < types.length; i++) {
                            TileType type = types[i];
                            ImmutableOverlayContainer tileContainer = new ImmutableOverlayContainer.Builder()
                                .setFixedSize(Vector2i.of(5 + 2, 5 + 2))
                                .setCenterOnX(true)
                                .setCenterOnY(true)
                                .setMargin(Spacing.of(0, 0, 5, 0))
                                .build();
                            ImmutableStackingContainer fillHover = new ImmutableStackingContainer.Builder()
                                .setTag("tileBar.fillHover")
                                .fillAllocatedSize()
                                .setDecoration(ElementDecoration.builder()
                                    .setFillColor(new Color(163, 44, 163))
                                    .build())
                                .doOnCreate(element -> {
                                    element.doOnHoverStart(e -> {
                                        element.setVisible(true);
                                        element.propagateUpdate();
                                    });
                                    element.doOnHoverEnd(e -> {
                                        element.setVisible(false);
                                        element.propagateUpdate();
                                    });
                                })
                                .setVisible(false)
                                .build();
                            ImmutableStackingContainer fillSelected = new ImmutableStackingContainer.Builder()
                                .setTag("tileBar.fillSelected")
                                .fillAllocatedSize()
                                .setDecoration(ElementDecoration.builder()
                                    .setFillColor(new Color(215, 57, 215))
                                    .build())
                                .doOnCreate(element -> tileBarSelectedElements.put(type, element))
                                .setVisible(false)
                                .build();
                            ImmutableSpriteElement tileSprite = new ImmutableSpriteElement.Builder()
                                .setSprite(type.getSprite())
                                .setForcedSize(Vector2i.of(5, 5))
                                .setZIndex(6)
                                .build();
                            tileContainer.getChildren().add(fillSelected);
                            tileContainer.getChildren().add(fillHover);
                            tileContainer.getChildren().add(tileSprite);
                            container.getChildren().add(tileContainer.asMutable(container));
                        }
                        container.propagateUpdate();
                    })
                    .build())
                .build(), Vector2i.of(0, 158))
            .add(new ImmutableOverlayContainer.Builder()
                .setTag("actionBarContainer")
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
                        .setZIndex(5)
                        .build())
                    .add(new ImmutableButtonElement.Builder(
                            Sprites.GUI_PAINTBRUSH, Sprites.GUI_PAINTBRUSH_HOVER, Sprites.GUI_PAINTBRUSH_SELECTED)
                        .setTag("paintButton")
                        .setMargin(Spacing.of(5, 0, 0, 0))
                        .setZIndex(5)
                        .build())
                    .build())
                .build(), Vector2i.of(0, 168))
            .build())
        .build()
        .create();

    private final GameWindow window;
    private final Level level;
    private final Camera camera;

    @Nullable
    private Tool selectedTool;

    private final TileRenderer tileRenderer;

    private final Map<Page, PageRenderer> pageRenderers = new HashMap<>();

    public LevelDesigner(GameWindow window, Level level, Camera camera) {
        this.window = window;
        this.level = level;
        this.camera = camera;

        tileRenderer = new TileRenderer(camera, level);

        // Get MutableElements
        MutableButtonElement selectButton = page
            .queryElement("selectButton", MutableButtonElement.class)
            .orElseThrow();
        MutableButtonElement paintButton = page
            .queryElement("paintButton", MutableButtonElement.class)
            .orElseThrow();
        MutableElement tileBar = page
            .queryElement("tileBar", MutableElement.class)
            .orElseThrow();

        // Tool button click handlers
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

            tileBar.setVisible(false);
            tileBar.propagateUpdate();
        });
        paintButton.doOnClick(e -> {
            Arrays.stream(buttons).forEach(button -> button.setSelected(false));
            paintButton.setSelected(true);
            paintButton.propagateUpdate();
            if (selectedTool != null) {
                selectedTool.cleanup();
            }

            selectedTool = new PaintTool(window, level, camera, tileRenderer, page);
            ((PaintTool) selectedTool).setPaintType(TileType.STONEBRICKS);

            // Reset selected state
            tileBarSelectedElements.forEach((otherType, otherElement) ->
                otherElement.setVisible(false));

            selectedTool.prepare();

            tileBar.setVisible(true);
            tileBar.propagateUpdate();
        });

        // Tile bar click handlers
        tileBarSelectedElements.forEach((type, element) -> element.doOnClick(e -> {
            if (selectedTool == null || !(selectedTool instanceof PaintTool)) {
                return;
            }
            tileBarSelectedElements.forEach((otherType, otherElement) ->
                otherElement.setVisible(false));
            element.setVisible(true);

            ((PaintTool) selectedTool).setPaintType(type);

            element.propagateUpdate();
        }));

        PageRenderer actionBarRenderer = new PageRenderer(page);
        page.doOnUpdate(() -> actionBarRenderer.setDirty(true));

        pageRenderers.put(page, actionBarRenderer);
    }

    public void click(Vector2i position) {
        page.click(position, MouseButton.LEFT_MOUSE_BUTTON);
        MutableElement container = page.queryElement("actionBarContainer", MutableElement.class)
            .orElseThrow();
        if (container.getMousePosition() != null) {
            // Clicked on action bar, don't pass through
            return;
        }
        if (selectedTool != null) selectedTool.click(position);
    }

    @Override
    public void prepare() {
        pageRenderers.values().forEach(PageRenderer::prepare);

        tileRenderer.prepare();

        if (selectedTool == null) {
            // Initial tool: select
            MutableButtonElement selectButton = page
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
            page.setMousePosition(null);
        } else {
            Vector2d screenPosition = window.getMousePosition();
            // TODO: Hardcoding resolution, bad
            Vector2i virtualPosition = screenPosition.div(4).toInt();

            page.setMousePosition(virtualPosition);
        }

        page.tick();
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
