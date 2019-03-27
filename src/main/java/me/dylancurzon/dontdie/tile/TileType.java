package me.dylancurzon.dontdie.tile;

import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.Sprites;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public enum TileType {

    BLACK(0, Sprites.BLACK),
    UNDEFINED(1, Sprites.UNDEFINED),
    STONEBRICKS(2, Sprites.STONEBRICKS1);

    public static Optional<TileType> forId(final int id) {
        for (TileType type : TileType.values()) {
            if (type.id == id) return Optional.of(type);
        }
        return Optional.empty();
    }

    private final int id;
    private final List<Sprite> variations;

    TileType(final int id, final Sprite sprite) {
        this.id = id;
        this.variations = Collections.singletonList(sprite);
    }

    TileType(final int id, final Sprite... variations) {
        this.id = id;
        this.variations = Arrays.asList(variations);
    }

    public int getId() {
        return this.id;
    }

    public Sprite getSprite() {
        // TODO: Make TileType a normal class so this behaviour can be type-specific
        if (this.variations.size() == 1) {
            return this.variations.get(0);
        }
        return this.variations.get(ThreadLocalRandom.current().nextInt(0, this.variations.size()));
    }

}
