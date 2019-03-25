package me.dylancurzon.dontdie.sprite;

import de.matthiasmann.twl.utils.PNGDecoder;
import me.dylancurzon.dontdie.util.Buffers;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class SpriteSheet extends Sprite {

    public static SpriteSheet loadSprite(final String name) {
        final String filename = "textures/" + name + ".png";
        return SpriteSheet.loadSprite(Sprite.class.getClassLoader().getResourceAsStream(filename));
    }

    public static SpriteSheet loadSprite(final InputStream in) {
        try {
            final PNGDecoder decoder = new PNGDecoder(in);

            int width = decoder.getWidth();
            int height = decoder.getHeight();

            final ByteBuffer buffer = BufferUtils.createByteBuffer(4 * width * height);
            decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
            buffer.flip();

            final byte[] pixels = Buffers.asByteArray(buffer);

            return new SpriteSheet(width, height, pixels);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SpriteSheet(int width, int height, byte[] pixels) {
        super(width, height, 1, new byte[][] { pixels });
    }

    public Sprite getSprite(final int x, final int y, final int width) {
        return this.getSprite(x, y, width, width);
    }

    public Sprite getSprite(final int x, final int y, final int width, final int height) {
        final byte[] pixels = new byte[width * height * 4];
        final byte[] frame = super.frames[0];

        for (int dx = 0; dx < width; dx++) {
            for (int dy = 0; dy < height; dy++) {
                for (int b = 0; b < 4; b++) {
                    final int xa = dx + x * width;
                    final int ya = dy + y * height;
                    pixels[(dx + dy * width) * 4 + b] = (frame[(xa + ya * this.width) * 4 + b]);
                }
            }
        }

        return new Sprite(width, height, 1, new byte[][] { pixels });
    }

}
