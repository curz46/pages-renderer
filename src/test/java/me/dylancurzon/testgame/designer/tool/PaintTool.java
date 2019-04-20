package me.dylancurzon.testgame.designer.tool;

import me.dylancurzon.dontdie.gfx.window.Window;
import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.util.MouseButton;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.testgame.designer.TileOverlayRenderer;
import me.dylancurzon.testgame.gfx.Camera;
import me.dylancurzon.testgame.gfx.Sprites;
import me.dylancurzon.testgame.gfx.TileRenderer;
import me.dylancurzon.testgame.tile.Level;
import me.dylancurzon.testgame.tile.TileType;
import org.jetbrains.annotations.Nullable;

public class PaintTool extends Tool {

    private final Window window;
    private final Level level;
    private final Camera camera;

    private final TileRenderer tileRenderer;
    private final Page page;
    private final MutableElement actionBarContainer;
    private final MutableElement tileBar;

    private final TileOverlayRenderer overlayRenderer;

    @Nullable
    private Vector2i hoverPosition;
    private TileType paintType;

    public PaintTool(Window window, Level level, Camera camera, TileRenderer tileRenderer, Page page) {
        this.window = window;
        this.level = level;
        this.camera = camera;

        this.tileRenderer = tileRenderer;
        this.page = page;

        actionBarContainer = page.queryElement("actionBarContainer", MutableElement.class)
            .orElseThrow();
        tileBar = page.queryElement("tileBar", MutableElement.class)
            .orElseThrow();

        overlayRenderer = new TileOverlayRenderer(camera);
    }

    public void setPaintType(TileType paintType) {
        this.paintType = paintType;
    }

    @Override
    public void click(Vector2i position) {}

    @Override
    public void prepare() {
        overlayRenderer.prepare();
    }

    @Override
    public void cleanup() {
        overlayRenderer.cleanup();
    }

    @Override
    public void update() {
        overlayRenderer.getOverlaySprites().clear();
        if (hoverPosition != null) {
            overlayRenderer.getOverlaySprites()
                .add(TileOverlayRenderer.OverlaySprite.of(hoverPosition, Sprites.OVERLAY_PAINT_HOVER));
        }

        overlayRenderer.update();
    }

    @Override
    public void render() {
        overlayRenderer.render();
    }

    @Override
    public void tick() {
        if (window.getMousePosition() == null) return;

        Vector2i tilePosition = camera.getTileForMousePosition(window.getMousePosition().toInt());

        hoverPosition = tilePosition;
        setDirty(true);

        if (window.isMousePressed(MouseButton.LEFT_MOUSE_BUTTON)
            && actionBarContainer.getMousePosition() == null
            && tileBar.getMousePosition() == null) {
            if (level.getTile(tilePosition).orElse(null) != paintType) {
                level.setTile(tilePosition, paintType);
                tileRenderer.setDirty(true);
            }
        }
    }

}
