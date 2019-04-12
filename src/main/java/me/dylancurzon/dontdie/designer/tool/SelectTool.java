package me.dylancurzon.dontdie.designer.tool;

import me.dylancurzon.dontdie.designer.TileOverlayRenderer;
import me.dylancurzon.dontdie.gfx.Camera;
import me.dylancurzon.dontdie.gfx.GameWindow;
import me.dylancurzon.dontdie.sprite.Sprites;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.dontdie.tile.TileType;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SelectTool extends Tool {

    private final GameWindow window;
    private final Level level;
    private final Camera camera;

    private final TileOverlayRenderer overlayRenderer;

    @Nullable
    private Vector2i selectedPosition;
    @Nullable
    private Vector2i hoverPosition;

    public SelectTool(GameWindow window, Level level, Camera camera) {
        this.window = window;
        this.level = level;
        this.camera = camera;

        overlayRenderer = new TileOverlayRenderer(camera);
    }

    @Override
    public void click(Vector2i position) {
        Vector2i newPosition = camera.getTileForMousePosition(position);
        if (!Objects.equals(selectedPosition, newPosition)) {
            selectedPosition = newPosition;
            setDirty(true);
        }
    }

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
                .add(TileOverlayRenderer.OverlaySprite.of(hoverPosition, Sprites.OVERLAY_SELECT_HOVER));
        }
        if (selectedPosition != null) {
            overlayRenderer.getOverlaySprites()
                .add(TileOverlayRenderer.OverlaySprite.of(selectedPosition, Sprites.OVERLAY_SELECT_SELECTED));
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

        if (!Objects.equals(hoverPosition, tilePosition)) {
            hoverPosition = tilePosition;
            System.out.println(hoverPosition);
            setDirty(true);
        }
    }

}
