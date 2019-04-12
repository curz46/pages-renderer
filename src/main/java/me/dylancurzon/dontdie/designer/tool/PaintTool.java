package me.dylancurzon.dontdie.designer.tool;

import me.dylancurzon.dontdie.designer.TileOverlayRenderer;
import me.dylancurzon.dontdie.gfx.Camera;
import me.dylancurzon.dontdie.gfx.GameWindow;
import me.dylancurzon.dontdie.gfx.TileRenderer;
import me.dylancurzon.dontdie.sprite.Sprites;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.dontdie.tile.TileType;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

public class PaintTool extends Tool {

    private final GameWindow window;
    private final Level level;
    private final Camera camera;

    private final TileRenderer tileRenderer;

    private final TileOverlayRenderer overlayRenderer;

    @Nullable
    private Vector2i hoverPosition;

    public PaintTool(GameWindow window, Level level, Camera camera, TileRenderer tileRenderer) {
        this.window = window;
        this.level = level;
        this.camera = camera;

        this.tileRenderer = tileRenderer;

        overlayRenderer = new TileOverlayRenderer(camera);
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

        Vector2d screenPosition = window.getMousePosition();
        // TODO: Hardcoding resolution, bad
        Vector2i virtualPosition = screenPosition.div(4).toInt();
        Vector2i tilePosition = camera.getTileForMousePosition(virtualPosition);

        hoverPosition = tilePosition;
        setDirty(true);

        if (window.isMousePressed()) {
            if (level.getTile(tilePosition).orElse(null) != TileType.STONEBRICKS) {
                level.setTile(tilePosition, TileType.STONEBRICKS);
                tileRenderer.setDirty(true);
            }
        }
    }

}
