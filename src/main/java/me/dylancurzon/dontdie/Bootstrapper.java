package me.dylancurzon.dontdie;

import com.google.common.collect.Sets;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.sprite.SpritePacker;
import me.dylancurzon.dontdie.sprite.Sprites;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Bootstrapper {

    public static void main(final String[] args) throws IOException {
//        final LevelDesigner designer = new LevelDesigner();
//        designer.launch();

        final SpritePacker packer = new SpritePacker(Sets.union(Sprites.getSprites(), Sets.newHashSet(Sprite.loadAnimatedSprite("loading", 20))));

        final byte[] bytes = new byte[2048 * 2048 * 3];
        packer
            .getSpriteMap()
            .forEach((sprite, position) -> {
                for (int frame = 0; frame < sprite.getFrameCount(); frame++) {
                    final ByteBuffer buffer = sprite.getFrames()[frame];
                    final byte[] pixels = buffer.array();
//                    for (int xd = position.getX() + frame * sprite.getWidth(); xd < position.getX() + sprite.getWidth(); xd++) {
//                        for (int yd = position.getY(); yd < position.getY() + sprite.getHeight(); yd++) {
//                            bytes[(xd + (yd * 2048)) * 4] = pixels[];
//                        }
//                    }
                    for (int xd = 0; xd < sprite.getWidth(); xd++) {
                        for (int yd = 0; yd < sprite.getHeight(); yd++) {
                            final int xa = xd + position.getX() + frame * sprite.getWidth();
                            final int ya = yd + position.getY();
                            try {
                                bytes[(xa + (ya * 2048)) * 3] = pixels[(xd + yd * sprite.getWidth()) * 4];
                                bytes[(xa + (ya * 2048)) * 3 + 1] = pixels[(xd + yd * sprite.getWidth()) * 4 + 1];
                                bytes[(xa + (ya * 2048)) * 3 + 2] = pixels[(xd + yd * sprite.getWidth()) * 4 + 2];
//                                bytes[(xa + (yd * 2048)) * 4 + 3] = pixels[(xd + yd * sprite.getWidth()) * 4 + 3];
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }
            });
//        ByteArrayInputStream in = new ByteArrayInputStream(bytes);

        DataBuffer buffer = new DataBufferByte(bytes, bytes.length);

//3 bytes per pixel: red, green, blue
        WritableRaster raster = Raster.createInterleavedRaster(buffer, 2048, 2048, 3 * 2048, 3, new int[] {0, 1, 2}, (Point)null);
        ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), false, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
        BufferedImage image = new BufferedImage(cm, raster, true, null);

        ImageIO.write(image, "png", new File("output.png"));
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
