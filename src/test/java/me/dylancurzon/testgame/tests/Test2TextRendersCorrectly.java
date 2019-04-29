package me.dylancurzon.testgame.tests;

import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.text.ImmutableTextElement;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.testgame.ExampleGame;

public class Test2TextRendersCorrectly {

    public static PageTemplate TEMPLATE = new PageTemplate.Builder()
        .setFixedSize(Vector2i.of(1920, 1080))
        .add(new ImmutableTextElement.Builder()
            .setText(ExampleGame.PROVIDER.getSprite("HELLO WORLD"))
            .build())
        .build();

}
