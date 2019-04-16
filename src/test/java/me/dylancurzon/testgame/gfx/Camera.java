package me.dylancurzon.testgame.gfx;

import me.dylancurzon.dontdie.gfx.GameWindow;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;

public class Camera {

    private static final int TILE_WIDTH = 16;

    private static final double MAX_TILES_HORIZONTAL = 16;
    private static final double MAX_TILES_VERTICAL = 12;
    private static final int TILEMAP_UPDATE_STRIDE = 1;

    private Vector2d position = Vector2d.of(0, 0);
    private double zoom = 1;

    private Vector2d lastFixedPosition = getFixedPosition();

    public Vector2i getTileForMousePosition(Vector2i mousePosition) {
        // In order to find the Tile position, first we need the bounds
        Vector2d boundA = getPosition().sub(getSize().div(2));
        // Find relative position in the window
        Vector2d normalizedPosition = mousePosition
            .div(Vector2d.of(GameWindow.VIRTUAL_WIDTH, GameWindow.VIRTUAL_HEIGHT))
            .mul(Vector2d.of(1, -1))
            .add(Vector2d.of(0, 1));
        // Find tilePosition
        Vector2d relativePosition = normalizedPosition
            .mul(Vector2d.of(MAX_TILES_HORIZONTAL, MAX_TILES_VERTICAL));
        // Round down to left Tile corner
        return boundA.add(relativePosition)
            .floor()
            .toInt();
    }

    public void transform(Vector2d by) {
        position = position.add(by);

        Vector2d fixedPosition = getFixedPosition();
        if (!lastFixedPosition.equals(fixedPosition)) {
            lastFixedPosition = fixedPosition;
        }
    }

    public Vector2d getSize() {
        return Vector2d.of(MAX_TILES_HORIZONTAL, MAX_TILES_VERTICAL);
    }

    public Vector2i getVisibleA() {
        return getFixedPosition().sub(getSize().div(2)).ceil().toInt();
    }

    public Vector2i getVisibleB() {
        return getFixedPosition().add(getSize().div(2)).ceil().toInt();
    }

    /**
     * @return The difference between the actual position and {@link this#getFixedPosition()}. This allows the tilemap
     * to change only when new Tiles are visible while still being able to transform tiles to the correct position.
     */
    public Vector2d getDelta() {
        return position.sub(getFixedPosition());
    }

    /**
     * @return The fixed position of this Camera. This will change whenever the Camera moves
     * {@link this#TILEMAP_UPDATE_STRIDE} in a particular direction.
     */
    public Vector2d getFixedPosition() {
        return Vector2d.of(
            Math.round(position.getX() / TILEMAP_UPDATE_STRIDE) * TILEMAP_UPDATE_STRIDE,
            Math.round(position.getY() / TILEMAP_UPDATE_STRIDE) * TILEMAP_UPDATE_STRIDE
        );
    }

    public void setPosition(Vector2d position) {
        this.position = position;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

    public Vector2d getPosition() {
        return position;
    }

    public double getZoom() {
        return zoom;
    }

}
