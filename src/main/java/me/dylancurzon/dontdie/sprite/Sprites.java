package me.dylancurzon.dontdie.sprite;

import com.google.common.collect.Sets;

import java.util.Set;

import static me.dylancurzon.dontdie.sprite.Sprite.loadSprite;

public interface Sprites {

    Sprite BLACK = loadSprite("black");
    Sprite UNDEFINED = loadSprite("undefined");
    Sprite STONEBRICKS = loadSprite("stonebricks");

    static Set<Sprite> getSprites() {
        return Sets.newHashSet(
            BLACK,
            UNDEFINED,
            STONEBRICKS
        );
    }

}
