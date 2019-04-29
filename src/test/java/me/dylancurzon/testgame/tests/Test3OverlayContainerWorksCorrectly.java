package me.dylancurzon.testgame.tests;

import me.dylancurzon.openglrenderer.sprite.Sprite;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.container.ImmutableOverlayContainer;
import me.dylancurzon.pages.element.sprite.ImmutableSpriteElement;
import me.dylancurzon.pages.util.Spacing;
import me.dylancurzon.pages.util.Vector2i;

import java.awt.*;
import java.io.File;

public class Test3OverlayContainerWorksCorrectly {

    public static PageTemplate testAPositionsCorrectly() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableOverlayContainer.Builder()
//                    .setFixedSize(Vector2i.of(1920, 1080))
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build())
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
                .add(new ImmutableOverlayContainer.Builder()
                    .setDecoration(ElementDecoration.builder()
                        .setFillColor(Color.GREEN)
                        .setLineColor(Color.RED)
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build())
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testCContainerMarginWorksCorrectly() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableOverlayContainer.Builder()
                    .setMargin(Spacing.of(50))
                    .setDecoration(ElementDecoration.builder()
                        .setFillColor(Color.GREEN)
                        .setLineColor(Color.RED)
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build())
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testDElementMarginWorksCorrectly() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableOverlayContainer.Builder()
                    .setDecoration(ElementDecoration.builder()
                        .setFillColor(Color.GREEN)
                        .setLineColor(Color.RED)
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .setMargin(Spacing.of(500, 0, 0, 0))
                        .build())
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testEFixedSizeWorksCorrectly() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableOverlayContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .setDecoration(ElementDecoration.builder()
                        .setFillColor(Color.RED)
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build())
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testFCenterOnXWorksCorrectly() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableOverlayContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .setCenterOnX(true)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build())
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testGCenterOnYWorksCorrectly() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableOverlayContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .setCenterOnY(true)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build())
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static PageTemplate testHBothCenteredWorksCorrectly() {
        try {
            return new PageTemplate.Builder()
                .setFixedSize(Vector2i.of(1920, 1080))
                .add(new ImmutableOverlayContainer.Builder()
                    .setFixedSize(Vector2i.of(1920, 1080))
                    .setCenterOnX(true)
                    .setCenterOnY(true)
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\11-2-cat-png.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\cat-hd-png-cats-png-free-images-hd-wallpapers-347.png").toURI().toURL()))
                        .build())
                    .add(new ImmutableSpriteElement.Builder()
                        .setSprite(Sprite.loadSprite(new File("C:\\Users\\Dylan Curzon\\Desktop\\6-2-cat-png-13.png").toURI().toURL()))
                        .build())
                    .build())
                .build();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
