package me.dylancurzon.testgame.tests;

import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.container.ImmutableStackingContainer;
import me.dylancurzon.pages.event.MouseClickEvent;
import me.dylancurzon.pages.util.Vector2i;

import java.awt.*;
import java.util.function.Consumer;

public class Test7MouseFunctionsWorkCorrectly {

    public static PageTemplate testAClickHandlersWorkCorrectly() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableStackingContainer.Builder()
                    .setFixedSize(Vector2i.of(200, 200))
                    .setDecoration(ElementDecoration.builder()
                        .setFillColor(Color.YELLOW)
                        .build())
                    .doOnCreate(element -> element.doOnClick(new Consumer<MouseClickEvent>() {
                        boolean toggle;

                        @Override
                        public void accept(MouseClickEvent mouseClickEvent) {
                            toggle = !toggle;
                            element.setDecoration(
                                toggle
                                    ? ElementDecoration.builder().setFillColor(Color.GREEN).build()
                                    : ElementDecoration.builder().setFillColor(Color.BLUE).build()
                            );
                            element.propagateUpdate();
                        }
                    }))
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testBHoverHandlersWorkCorrectly() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableStackingContainer.Builder()
                    .setFixedSize(Vector2i.of(200, 200))
                    .setDecoration(ElementDecoration.builder()
                        .setFillColor(Color.YELLOW)
                        .build())
                    .doOnCreate(element -> {
                        element.doOnHoverStart(e -> {
                            element.setDecoration(
                                ElementDecoration.builder()
                                    .setFillColor(Color.GREEN)
                                    .build()
                            );
                            element.propagateUpdate();
                        });
                        element.doOnHoverEnd(e -> {
                            element.setDecoration(
                                ElementDecoration.builder()
                                    .setFillColor(Color.BLUE)
                                    .build()
                            );
                            element.propagateUpdate();
                        });
                    })
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
