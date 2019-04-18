package me.dylancurzon.testgame.gfx;

import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.SpriteSheet;

import java.io.IOException;
import java.util.Objects;

public interface SpriteSheets {

    SpriteSheet BRICKS = loadSpriteSheetFromJAR("stonebricks");
    SpriteSheet TEXT = loadSpriteSheetFromJAR("characters");
    SpriteSheet GUI = loadSpriteSheetFromJAR("gui");

    String JAR_PATH = "textures/%s.png";

    private static SpriteSheet loadSpriteSheetFromJAR(String name) {
        ClassLoader loader = Sprite.class.getClassLoader();
        try {
            return SpriteSheet.loadSprite(
                Objects.requireNonNull(loader.getResource(String.format(JAR_PATH, name)))
            );
        } catch (IOException e) {
            throw new IllegalArgumentException("Argument 'name' does not correspond to a Sprite in textures/<name>.png");
        }
    }

}
