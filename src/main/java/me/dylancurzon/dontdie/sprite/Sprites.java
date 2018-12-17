package me.dylancurzon.dontdie.sprite;

import com.google.common.collect.Sets;

import java.util.Set;

import static me.dylancurzon.dontdie.sprite.Sprite.loadSprite;

public interface Sprites {

    Sprite BLACK = loadSprite("textures/black.png");
    Sprite UNDEFINED = loadSprite("textures/undefined.png");
    Sprite STONEBRICKS = loadSprite("textures/stonebricks.png");

    static Set<Sprite> getSprites() {
        return Sets.newHashSet(
            BLACK,
            UNDEFINED,
            STONEBRICKS
        );
    }

}
