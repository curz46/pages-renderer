package me.dylancurzon.dontdie;

import me.dylancurzon.dontdie.gfx.GameCamera;
import me.dylancurzon.dontdie.gfx.GameRenderer;
import me.dylancurzon.dontdie.tile.Level;
import me.dylancurzon.dontdie.tile.TileType;
import me.dylancurzon.dontdie.util.Keys;
import me.dylancurzon.dontdie.util.Vector2d;
import me.dylancurzon.dontdie.util.Vector2i;

import static org.lwjgl.glfw.GLFW.*;

public class Game {

    public static void main(final String[] args) {
        final Level level = new Level();
//        level.setTile(Vector2i.of(0, 0), TileType.UNDEFINED);
        for (int x = 10; x < 20; x++) {
            for (int y = 10; y < 20; y++) {
                level.setTile(Vector2i.of(x, y), (x + y) % 2 == 0 ? TileType.UNDEFINED : TileType.STONEBRICKS);
            }
        }
        level.setTile(Vector2i.of(10, 10), TileType.STONEBRICKS);
        final GameRenderer renderer = new GameRenderer(level);
        renderer.prepare();
        renderer.getTileRenderer().tilemapUpdate();

        glfwSetKeyCallback(renderer.getWindow(), (window, key, scancode, action, mods) -> {
            if (action == GLFW_PRESS) Keys.press(key);
            if (action == GLFW_RELEASE) Keys.release(key);
        });

        final GameCamera camera = renderer.getCamera();
        while (!renderer.windowShouldClose()) {
            final double v = 0.05;
            final Vector2d delta = Vector2d.of(
                Keys.isPressed(GLFW_KEY_A) ? -v : Keys.isPressed(GLFW_KEY_D) ? v : 0,
                Keys.isPressed(GLFW_KEY_W) ? v : Keys.isPressed(GLFW_KEY_S) ? -v : 0
            );
            if (delta.getX() != 0 || delta.getY() != 0) {
                camera.transform(delta);
            }
            renderer.render();
        }
        renderer.cleanup();
    }

}
