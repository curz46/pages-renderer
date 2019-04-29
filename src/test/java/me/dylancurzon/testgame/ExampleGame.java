package me.dylancurzon.testgame;

import me.dylancurzon.openglrenderer.gfx.page.PageRenderer;
import me.dylancurzon.openglrenderer.gfx.window.GLFWWindow;
import me.dylancurzon.openglrenderer.gfx.window.GLFWWindowOptions;
import me.dylancurzon.openglrenderer.sprite.SpriteSheet;
import me.dylancurzon.openglrenderer.sprite.TextSpriteProvider;
import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.util.Vector2d;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.testgame.tests.*;
import org.lwjgl.opengl.GL;

import java.io.File;
import java.io.IOException;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class ExampleGame {

    public static TextSpriteProvider PROVIDER;

    public static void main(String[] args) throws IOException {
        int WIDTH = 1920;
        int HEIGHT = 1080;

        SpriteSheet sheet = SpriteSheet.loadSprite(new File("D:\\MEGA\\code\\dont-die\\src\\test\\resources\\textures\\characters.png").toURI().toURL());
        PROVIDER = new TextSpriteProvider("ABCDEFGHIJKLMNOPQRSTUVWXYZ ".toCharArray(), sheet);

        GLFWWindow window = new GLFWWindow(GLFWWindowOptions.builder()
            .setDimensions(Vector2i.of(WIDTH, HEIGHT))
            .setTitle("Test Page")
            .setVisible(true)
            .build());
        window.initialize();
        window.focus();

        PageTemplate[] tests = new PageTemplate[] {
            Test1ImagesRenderCorrectly.TEMPLATE,
            Test2TextRendersCorrectly.TEMPLATE,
            Test3OverlayContainerWorksCorrectly.testAPositionsCorrectly(),
            Test3OverlayContainerWorksCorrectly.testBColorsCorrectly(),
            Test3OverlayContainerWorksCorrectly.testCContainerMarginWorksCorrectly(),
            Test3OverlayContainerWorksCorrectly.testDElementMarginWorksCorrectly(),
            Test3OverlayContainerWorksCorrectly.testEFixedSizeWorksCorrectly(),
            Test3OverlayContainerWorksCorrectly.testFCenterOnXWorksCorrectly(),
            Test3OverlayContainerWorksCorrectly.testGCenterOnYWorksCorrectly(),
            Test3OverlayContainerWorksCorrectly.testHBothCenteredWorksCorrectly(),
            Test4StackingContainerWorksCorrectly.testAPositionsCorrectly(),
            Test4StackingContainerWorksCorrectly.testBColorsCorrectly(),
            Test4StackingContainerWorksCorrectly.testCMajorAxis(),
            Test4StackingContainerWorksCorrectly.testDMarginContainer(),
            Test4StackingContainerWorksCorrectly.testEMarginElement(),
            Test4StackingContainerWorksCorrectly.testFFixedSize(),
            Test4StackingContainerWorksCorrectly.testGCenterOnX(),
            Test4StackingContainerWorksCorrectly.testHCenterOnY(),
            Test4StackingContainerWorksCorrectly.testICenterBoth(),
            Test5RatioContainerWorksCorrectly.testAPositionsCorrectly(),
            Test5RatioContainerWorksCorrectly.testBColorsCorrectly(),
            Test5RatioContainerWorksCorrectly.testCMajorAxis(),
            Test5RatioContainerWorksCorrectly.testDMarginContainer(),
            Test5RatioContainerWorksCorrectly.testEMarginElement(),
            Test5RatioContainerWorksCorrectly.testFCenterOnX(),
            Test5RatioContainerWorksCorrectly.testGCenterOnY(),
            Test5RatioContainerWorksCorrectly.testHCenterBoth(),
            Test6AbsoluteContainerWorksCorrectly.testAPositionsCorrectly(),
            Test6AbsoluteContainerWorksCorrectly.testBColorsCorrectly(),
            Test6AbsoluteContainerWorksCorrectly.testCMarginContainer(),
            Test6AbsoluteContainerWorksCorrectly.testDMarginElement(),
            Test7MouseFunctionsWorkCorrectly.testAClickHandlersWorkCorrectly(),
            Test7MouseFunctionsWorkCorrectly.testBHoverHandlersWorkCorrectly()
        };
        int testIndex = 32;

        Page page = tests[testIndex].create();
        window.doOnMousePress(event -> page.click(window.getMousePosition().toInt(), event.getButton()));

        glfwMakeContextCurrent(window.getId());
        GL.createCapabilities(false);

        PageRenderer renderer = new PageRenderer(window, page, PROVIDER);
        renderer.prepare();

        page.doOnUpdate(() -> renderer.setDirty(true));

        while (!window.shouldWindowClose()) {
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            Vector2d mousePosition = window.getMousePosition();
            page.setMousePosition(mousePosition == null ? null : mousePosition.toInt());

            if (renderer.isDirty()) {
                renderer.update();
                renderer.setDirty(false);
            }

            renderer.render();

            glfwSwapBuffers(window.getId());
            glfwPollEvents();
        }
    }

}
