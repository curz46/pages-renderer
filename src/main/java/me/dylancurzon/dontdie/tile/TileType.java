package me.dylancurzon.dontdie.tile;

import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.Sprites;

import java.util.Optional;

public enum TileType {

    STONEBRICKS(0, Sprites.STONEBRICKS);

    public static Optional<TileType> forId(final int id) {
        for (TileType type : TileType.values()) {
            if (type.id == id) return Optional.of(type);
        }
        return Optional.empty();
    }

    private final int id;
    private final Sprite sprite;

    TileType(final int id, final Sprite sprite) {
        this.id = id;
        this.sprite = sprite;
    }

    public int getId() {
        return this.id;
    }

    public Sprite getSprite() {
        return this.sprite;
    }

}
