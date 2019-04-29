package me.dylancurzon.testgame.tests;

import me.dylancurzon.openglrenderer.sprite.Sprite;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.sprite.ImmutableSpriteElement;
import me.dylancurzon.pages.util.Vector2i;

import java.io.File;
import java.io.IOException;

public class Test1ImagesRenderCorrectly {

    public static PageTemplate TEMPLATE;

    static {
        try {
            TEMPLATE = new PageTemplate.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build())
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
