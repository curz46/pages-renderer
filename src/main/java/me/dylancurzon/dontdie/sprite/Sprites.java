package me.dylancurzon.dontdie.sprite;

import com.google.common.collect.Sets;

import java.util.Set;

public interface Sprites {

    Sprite BLACK = Sprite.loadSprite("black");
    Sprite UNDEFINED = Sprite.loadSprite("undefined");

//    Sprite STONEBRICKS = Sprite.loadSprite("stonebricks");
    Sprite STONEBRICKS1 = SpriteSheets.BRICKS.getSprite(0, 0, 16);
    Sprite STONEBRICKS2 = SpriteSheets.BRICKS.getSprite(1, 0, 16);
    Sprite STONEBRICKS3 = SpriteSheets.BRICKS.getSprite(0, 1, 16);
    Sprite STONEBRICKS4 = SpriteSheets.BRICKS.getSprite(1, 1, 16);

    static Set<Sprite> getSprites() {
        return Sets.newHashSet(
            BLACK,
            UNDEFINED,
            STONEBRICKS1,
            STONEBRICKS2,
            STONEBRICKS3,
            STONEBRICKS4
        );
    }

}
