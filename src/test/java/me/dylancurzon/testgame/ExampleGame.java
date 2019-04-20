package me.dylancurzon.testgame;

import me.dylancurzon.dontdie.gfx.page.PageRenderer;
import me.dylancurzon.dontdie.gfx.window.GLFWWindow;
import me.dylancurzon.dontdie.gfx.window.GLFWWindowOptions;
import me.dylancurzon.pages.Page;
import me.dylancurzon.pages.PageTemplate;
import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.sprite.ImmutableSpriteElement;
import me.dylancurzon.pages.util.Vector2i;
import me.dylancurzon.testgame.gfx.Sprites;
import org.lwjgl.opengl.GL;

import java.awt.*;
import java.awt.event.KeyEvent;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;

public class ExampleGame {

    public static void main(String[] args) {
        int WIDTH = 1920;
        int HEIGHT = 1080;

        GLFWWindow window = new GLFWWindow(GLFWWindowOptions.builder()
            .setDimensions(Vector2i.of(WIDTH, HEIGHT))
            .setTitle("Example Game")
            .setVisible(true)
            .build());
        window.initialize();
        window.focus();

        Page page = new PageTemplate.Builder()
            .setFixedSize(Vector2i.of(WIDTH, HEIGHT))
            .setDecoration(ElementDecoration.builder()
                .setFillColor(Color.GREEN)
                .build())
            .setCenterOnX(true)
            .setCenterOnY(true)
            .add(new ImmutableSpriteElement.Builder()
                .setSprite(Sprites.STONEBRICKS)
                .build())
            .doOnClick(event -> System.out.println(event.getPosition()))
            .build()
            .create();

        window.doOnMousePress(event -> page.click(window.getMousePosition().toInt(), event.getButton()));
        window.doOnKeyPress(KeyEvent.VK_W, e -> System.out.println("W pressed!"));

        glfwMakeContextCurrent(window.getId());
        GL.createCapabilities(false);

        PageRenderer renderer = new PageRenderer(window, page);
        renderer.prepare();

        page.doOnUpdate(() -> renderer.setDirty(true));

        while (!window.shouldWindowClose()) {
            glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
            glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

            renderer.render();

            glfwSwapBuffers(window.getId());
            glfwPollEvents();
        }

        // Update thread
        (new Thread(page::tick)).start();
    }

}
