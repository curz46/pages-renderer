package me.dylancurzon.testgame.tile;

import me.dylancurzon.openglrenderer.sprite.Sprite;
import me.dylancurzon.testgame.gfx.Sprites;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public enum TileType {

    BLACK(0, Sprites.BLACK),
    UNDEFINED(1, Sprites.UNDEFINED),
    STONEBRICKS(2, Sprites.STONEBRICKS);

    public static Optional<TileType> forId(int id) {
        for (TileType type : TileType.values()) {
            if (type.id == id) return Optional.of(type);
        }
        return Optional.empty();
    }

    private final int id;
    private final List<Sprite> variations;

    TileType(int id, Sprite sprite) {
        this.id = id;
        variations = Collections.singletonList(sprite);
    }

    TileType(int id, Sprite... variations) {
        this.id = id;
        this.variations = Arrays.asList(variations);
    }

    public int getId() {
        return id;
    }

    public Sprite getSprite() {
        // TODO: Make TileType a normal class so this behaviour can be type-specific
        if (variations.size() == 1) {
            return variations.get(0);
        }
        return variations.get(ThreadLocalRandom.current().nextInt(0, variations.size()));
    }

}
