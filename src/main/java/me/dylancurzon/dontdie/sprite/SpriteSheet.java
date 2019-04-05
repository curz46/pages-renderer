package me.dylancurzon.dontdie.sprite;

import de.matthiasmann.twl.utils.PNGDecoder;
import me.dylancurzon.dontdie.util.Buffers;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class SpriteSheet extends Sprite {

    public static SpriteSheet loadSprite(String name) {
        String filename = "textures/" + name + ".png";
//        System.out.println(filename);
        return SpriteSheet.loadSprite(Sprite.class.getClassLoader().getResourceAsStream(filename));
    }

    public static SpriteSheet loadSprite(InputStream in) {
        try {
            PNGDecoder decoder = new PNGDecoder(in);

            int width = decoder.getWidth();
            int height = decoder.getHeight();

            ByteBuffer buffer = BufferUtils.createByteBuffer(4 * width * height);
            decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
            buffer.flip();

            byte[] pixels = Buffers.asByteArray(buffer);

            return new SpriteSheet(width, height, pixels);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SpriteSheet(int width, int height, byte[] pixels) {
        super(width, height, 1, new byte[][] { pixels });
    }

    public Sprite getSprite(int x, int y, int width) {
        return getSprite(x, y, width, width);
    }

    public Sprite getSprite(int x, int y, int width, int height) {
        byte[] pixels = new byte[width * height * 4];
        byte[] frame = super.frames[0];

        for (int dx = 0; dx < width; dx++) {
            for (int dy = 0; dy < height; dy++) {
                for (int b = 0; b < 4; b++) {
                    int xa = dx + x * width;
                    int ya = dy + y * height;
                    pixels[(dx + dy * width) * 4 + b] = (frame[(xa + ya * this.width) * 4 + b]);
                }
            }
        }

        return new Sprite(width, height, 1, new byte[][] { pixels });
    }

}
