package me.dylancurzon.dontdie;

import me.dylancurzon.dontdie.designer.LevelDesigner;

public class Bootstrapper {

    public static void main(final String[] args) {
        final LevelDesigner designer = new LevelDesigner();
        designer.launch();
    }

//    public void temp() {
//        final Level level = new Level();
////        level.setTile(Vector2i.of(0, 0), TileType.UNDEFINED);
//        for (int x = -200; x < 200; x++) {
//            for (int y = -200; y < 200; y++) {
//                level.setTile(Vector2i.of(x, y), (x + y) % 2 == 0 ? TileType.UNDEFINED : TileType.STONEBRICKS);
//            }
//        }
//        final RootRenderer renderer = new RootRenderer(level);
//        renderer.prepare();
////        renderer.getTileRenderer().tilemapUpdate();
//
//        glfwSetKeyCallback(renderer.getWindow(), (window, key, scancode, action, mods) -> {
//            if (action == GLFW_PRESS) Keys.press(key);
//            if (action == GLFW_RELEASE) Keys.release(key);
//        });
//
//        final Camera camera = renderer.getCamera();
//
//
//        long lastTick = 0;
//        while (!renderer.windowShouldClose()) {
//            final double v = 0.1;
//            final Vector2d delta = Vector2d.of(
//                Keys.isKeyPressed(GLFW_KEY_A) ? -v : Keys.isKeyPressed(GLFW_KEY_D) ? v : 0,
//                Keys.isKeyPressed(GLFW_KEY_W) ? v : Keys.isKeyPressed(GLFW_KEY_S) ? -v : 0
//            );
//            if (delta.getX() != 0 || delta.getY() != 0) {
//                camera.transform(delta);
//            }
//
//            final long now = System.currentTimeMillis();
//            if (now - lastTick > (1000.0 / 144)) {
//                renderer.tick();
//                lastTick = now;
//            }
//            renderer.render();
//        }
//        renderer.cleanup();
//    }

}
