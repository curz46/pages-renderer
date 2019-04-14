package me.dylancurzon.dontdie.sprite;

import com.google.common.collect.Sets;

import java.util.Set;

public interface Sprites {

    Sprite DESIGNER_ACTION_BAR = Sprite.loadSprite("designer_action_bar");
    Sprite DESIGNER_TILE_BAR = Sprite.loadSprite("designer_tile_bar");
    Sprite DESIGNER_METADATA_WINDOW = Sprite.loadSprite("designer_metadata_window");

    Sprite OVERLAY_SELECT_HOVER = SpriteSheets.GUI.getSprite(3, 0, 16);
    Sprite OVERLAY_SELECT_SELECTED = SpriteSheets.GUI.getSprite(2, 0, 16);
    Sprite OVERLAY_PAINT_HOVER = SpriteSheets.GUI.getSprite(2, 1, 16);

    Sprite GUI_CURSOR = SpriteSheets.GUI.getSprite(0, 0, 16);
    Sprite GUI_CURSOR_HOVER = SpriteSheets.GUI.getSprite(0, 1, 16);
    Sprite GUI_CURSOR_SELECTED = SpriteSheets.GUI.getSprite(0, 2, 16);
    Sprite GUI_PAINTBRUSH = SpriteSheets.GUI.getSprite(1, 0, 16);
    Sprite GUI_PAINTBRUSH_HOVER = SpriteSheets.GUI.getSprite(1, 1, 16);
    Sprite GUI_PAINTBRUSH_SELECTED = SpriteSheets.GUI.getSprite(1, 2, 16);

    Sprite BLACK = Sprite.loadSprite("black");
    Sprite UNDEFINED = Sprite.loadSprite("undefined");

    Sprite STONEBRICKS = Sprite.loadSprite("stonebricks");

    // TODO: Sprites should be registered in categories, like Tiles, GUI, Text, which can be picked up by globally
    //       shared SpritePackers.
    static Set<Sprite> getSprites() {
        return Sets.newHashSet(
            OVERLAY_SELECT_HOVER,
            OVERLAY_SELECT_SELECTED,
            OVERLAY_PAINT_HOVER,
            GUI_CURSOR,
            GUI_CURSOR_HOVER,
            GUI_CURSOR_SELECTED,
            GUI_PAINTBRUSH,
            GUI_PAINTBRUSH_HOVER,
            GUI_PAINTBRUSH_SELECTED,
            BLACK,
            UNDEFINED,
            STONEBRICKS
        );
    }

}
