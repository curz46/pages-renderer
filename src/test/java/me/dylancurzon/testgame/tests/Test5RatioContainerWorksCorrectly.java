package me.dylancurzon.testgame.tests;

import me.dylancurzon.openglrenderer.sprite.Sprite;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.container.Axis;
import me.dylancurzon.pages.element.container.ImmutableRatioContainer;
import me.dylancurzon.pages.element.container.ImmutableStackingContainer;
import me.dylancurzon.pages.element.sprite.ImmutableSpriteElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

import java.awt.*;
import java.io.File;

public class Test5RatioContainerWorksCorrectly {

    public static PageTemplate testAPositionsCorrectly() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableRatioContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .setDecoration(ElementDecoration.builder()
                            .setFillColor(Color.GREEN)
                            .build())
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .setDecoration(ElementDecoration.builder()
                            .setFillColor(Color.BLUE)
                            .build())
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .setDecoration(ElementDecoration.builder()
                            .setFillColor(Color.RED)
                            .build())
                        .build(), 1)
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testBColorsCorrectly() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableRatioContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .setDecoration(ElementDecoration.builder()
                        .setFillColor(Color.RED)
                        .setLineColor(Color.BLUE)
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build(), 1)
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testCMajorAxis() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableRatioContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .setMajorAxis(Axis.HORIZONTAL)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build(), 1)
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testDMarginContainer() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableRatioContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .setMargin(Spacing.of(50))
                    .setDecoration(ElementDecoration.builder()
                        .setFillColor(Color.RED)
                        .setLineColor(Color.BLUE)
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build(), 1)
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testEMarginElement() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableRatioContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .setMargin(Spacing.of(500, 0, 0, 0))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build(), 1)
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testFCenterOnX() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableRatioContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .setCenterOnX(true)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .setMargin(Spacing.of(50))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build(), 1)
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testGCenterOnY() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableRatioContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .setCenterOnY(true)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .setMargin(Spacing.of(50))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build(), 1)
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testHCenterBoth() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableRatioContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .setCenterOnX(true)
                    .setCenterOnY(true)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .setMargin(Spacing.of(50))
                        .build(), 1)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build(), 1)
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


}
