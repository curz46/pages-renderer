package me.dylancurzon.testgame.designer.tool;

import me.dylancurzon.dontdie.gfx.window.Window;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.testgame.designer.TileOverlayRenderer;
import me.dylancurzon.testgame.gfx.Camera;
import me.dylancurzon.testgame.gfx.Sprites;
import me.dylancurzon.testgame.tile.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class SelectTool extends Tool {

    private final Window window;
    private final Level level;
    private final Camera camera;

    private final TileOverlayRenderer overlayRenderer;

    @Nullable
    private Vector2i selectedPosition;
    @Nullable
    private Vector2i hoverPosition;

    public SelectTool(Window window, Level level, Camera camera) {
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

        Vector2i tilePosition = camera.getTileForMousePosition(window.getMousePosition().toInt());
        if (!Objects.equals(hoverPosition, tilePosition)) {
            hoverPosition = tilePosition;
            setDirty(true);
        }
    }

}
