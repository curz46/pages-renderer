package me.dylancurzon.dontdie.gfx;

import me.dylancurzon.dontdie.util.Vector2d;
import me.dylancurzon.dontdie.util.Vector2i;

public class GameCamera implements Camera {

    private static final double MAX_TILES_HORIZONTAL = 12;
    private static final double MAX_TILES_VERTICAL = MAX_TILES_HORIZONTAL;
    private static final int TILEMAP_UPDATE_STRIDE = 1;

    private final GameRenderer renderer;

    private Vector2d position = Vector2d.of(0.5, 0.5);
    private double aspectRatio = 1;
    private double zoom = 1;

    private Vector2d lastFixedPosition = this.getFixedPosition();

    public GameCamera(final GameRenderer renderer) {
        this.renderer = renderer;
    }

    public void transform(final Vector2d by) {
        this.position = this.position.add(by);

        final Vector2d fixedPosition = this.getFixedPosition();
        if (!this.lastFixedPosition.equals(fixedPosition)) {
            this.lastFixedPosition = fixedPosition;
            this.renderer.getTileRenderer().tilemapUpdate();
        } else {
            this.renderer.getTileRenderer().deltaUpdate();
//            System.out.println("deltaUpdate");
        }
    }

    public Vector2d getSize() {
        double hTiles = MAX_TILES_HORIZONTAL;
        double vTiles = hTiles / this.aspectRatio;

        if (vTiles > MAX_TILES_VERTICAL) {
            vTiles = MAX_TILES_VERTICAL;
            hTiles = vTiles * this.aspectRatio;
        }

        return Vector2d.of(hTiles, vTiles);
    }

    /**
     * @return The index positions of the visible tiles as calculated by this Camera.
     */
//    public List<Vector2i> getVisibleTiles() {
//        final List<Vector2i> tiles = new ArrayList<>();
//
//        final Vector2d size = this.getSize();
//        int hTiles = (int) Math.ceil(size.getX());
//        int vTiles = (int) Math.ceil(size.getY());
//
//        final int radius = 2;
//        final Vector2i fixed = this.getFixedPosition().ceil().toInt();
//        for (int x = fixed.getX() - radius; x < fixed.getX() + hTiles + radius; x++) {
//            for (int y = fixed.getY() - radius; y < fixed.getY() + vTiles + radius; y++) {
//                tiles.add(Vector2i.of(x, y));
//            }
//        }
//
//        return tiles;
//    }
    public Vector2i getVisibleA() {
        return this.getFixedPosition().sub(this.getSize().div(2)).ceil().toInt();
    }

    public Vector2i getVisibleB() {
        return this.getFixedPosition().add(this.getSize().div(2)).ceil().toInt();
    }

    /**
     * @return The difference between the actual position and {@link this#getFixedPosition()}. This allows the tilemap
     * to change only when new Tiles are visible while still being able to transform tiles to the correct position.
     */
    public Vector2d getDelta() {
        return this.position.sub(this.getFixedPosition());
    }

    /**
     * @return The fixed position of this Camera. This will change whenever the Camera moves
     * {@link this#TILEMAP_UPDATE_STRIDE} in a particular direction.
     */
    public Vector2d getFixedPosition() {
        return Vector2d.of(
            Math.round(this.position.getX() / TILEMAP_UPDATE_STRIDE) * TILEMAP_UPDATE_STRIDE,
            Math.round(this.position.getY() / TILEMAP_UPDATE_STRIDE) * TILEMAP_UPDATE_STRIDE
        );
    }

    @Override
    public void setPosition(final Vector2d position) {
        this.position = position;
    }

    @Override
    public void setAspectRatio(final double ratio) {
        this.aspectRatio = ratio;
    }

    @Override
    public void setZoom(final double zoom) {
        this.zoom = zoom;
    }

    @Override
    public Vector2d getPosition() {
        return this.position;
    }

    @Override
    public double getAspectRatio() {
        return this.aspectRatio;
    }

    @Override
    public double getZoom() {
        return this.zoom;
    }

}
