package me.dylancurzon.testgame.gfx;

import com.google.common.collect.Sets;
import me.dylancurzon.openglrenderer.sprite.Sprite;

import java.io.IOException;
import java.util.Set;

public interface Sprites {

    Sprite DESIGNER_ACTION_BAR = loadSpriteFromJAR("designer_action_bar");
    Sprite DESIGNER_TILE_BAR = loadSpriteFromJAR("designer_tile_bar");
    Sprite DESIGNER_METADATA_WINDOW = loadSpriteFromJAR("designer_metadata_window");

    Sprite OVERLAY_SELECT_HOVER = SpriteSheets.GUI.getSprite(3, 0, 16);
    Sprite OVERLAY_SELECT_SELECTED = SpriteSheets.GUI.getSprite(2, 0, 16);
    Sprite OVERLAY_PAINT_HOVER = SpriteSheets.GUI.getSprite(2, 1, 16);

    Sprite GUI_CURSOR = SpriteSheets.GUI.getSprite(0, 0, 16);
    Sprite GUI_CURSOR_HOVER = SpriteSheets.GUI.getSprite(0, 1, 16);
    Sprite GUI_CURSOR_SELECTED = SpriteSheets.GUI.getSprite(0, 2, 16);
    Sprite GUI_PAINTBRUSH = SpriteSheets.GUI.getSprite(1, 0, 16);
    Sprite GUI_PAINTBRUSH_HOVER = SpriteSheets.GUI.getSprite(1, 1, 16);
    Sprite GUI_PAINTBRUSH_SELECTED = SpriteSheets.GUI.getSprite(1, 2, 16);

    Sprite BLACK = loadSpriteFromJAR("black");
    Sprite UNDEFINED = loadSpriteFromJAR("undefined");

    Sprite STONEBRICKS = loadSpriteFromJAR("stonebricks");

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

    String JAR_PATH = "textures/%s.png";

    private static Sprite loadSpriteFromJAR(String name) {
        ClassLoader loader = Sprite.class.getClassLoader();
        try {
            return Sprite.loadSprite(loader.getResource(String.format(JAR_PATH, name)));
        } catch (IOException e) {
            throw new IllegalArgumentException("Argument 'name' does not correspond to a Sprite in textures/<name>.png");
        }
    }

}
