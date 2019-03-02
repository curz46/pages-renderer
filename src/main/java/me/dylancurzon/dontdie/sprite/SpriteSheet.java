package me.dylancurzon.dontdie.sprite;

import de.matthiasmann.twl.utils.PNGDecoder;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class SpriteSheet extends OldSprite {

    public static SpriteSheet loadSprite(final String name) {
        final String filename = "textures/" + name + ".png";
        return SpriteSheet.loadSprite(OldSprite.class.getClassLoader().getResourceAsStream(filename));
    }

    public static SpriteSheet loadSprite(final InputStream in) {
        try {
            final PNGDecoder decoder = new PNGDecoder(in);

            int width = decoder.getWidth();
            int height = decoder.getHeight();

            ByteBuffer buf = BufferUtils.createByteBuffer(4 * width * height);
            decoder.decode(buf, width * 4, PNGDecoder.Format.RGBA);

            buf.flip();
            return new SpriteSheet(width, height, buf);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public SpriteSheet(int width, int height, ByteBuffer buffer) {
        super(width, height, buffer);
    }

    public Sprite getSprite(final int x, final int y, final int width) {
        return this.getSprite(x, y, width, width);
    }

    public Sprite getSprite(final int x, final int y, final int width, final int height) {
        final ByteBuffer buffer = BufferUtils.createByteBuffer(4 * width * height);
        final byte[] pixels = super.buffer.array();

        for (int dy = y; dy < y + height; dy++) {
            for (int dx = x; dx < x + width; dx++) {
                final int index = (x + y * width) * 4;
                buffer.put(pixels[index]);
            }
        }

        buffer.flip();
        return new Sprite(width, height, 1, new ByteBuffer[] { buffer });
    }

}
