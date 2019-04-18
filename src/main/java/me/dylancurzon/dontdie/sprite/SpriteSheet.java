package me.dylancurzon.dontdie.sprite;

import de.matthiasmann.twl.utils.PNGDecoder;
import me.dylancurzon.dontdie.util.Buffers;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public class SpriteSheet {

    public static SpriteSheet loadSprite(URL source) throws IOException {
        return SpriteSheet.loadSprite(source.openStream());
    }

    public static SpriteSheet loadSprite(InputStream in) throws IOException {
        PNGDecoder decoder = new PNGDecoder(in);

        int width = decoder.getWidth();
        int height = decoder.getHeight();

        ByteBuffer buffer = BufferUtils.createByteBuffer(4 * width * height);
        decoder.decode(buffer, width * 4, PNGDecoder.Format.RGBA);
        buffer.flip();

        byte[] pixels = Buffers.asByteArray(buffer);
        return new SpriteSheet(width, height, pixels);
    }

    private final int width;
    private final int height;
    private final byte[] pixels;

    public SpriteSheet(int width, int height, byte[] pixels) {
        this.width = width;
        this.height = height;
        this.pixels = pixels;
    }

    public Sprite getSprite(int x, int y, int width) {
        return getSprite(x, y, width, width);
    }

    public Sprite getSprite(int x, int y, int width, int height) {
        byte[] data = new byte[width * height * 4];

        for (int dx = 0; dx < width; dx++) {
            for (int dy = 0; dy < height; dy++) {
                for (int b = 0; b < 4; b++) {
                    int xa = dx + x * width;
                    int ya = dy + y * height;
                    data[(dx + dy * width) * 4 + b] = (pixels[(xa + ya * this.width) * 4 + b]);
                }
            }
        }

        return new Sprite(width, height, 1, new byte[][] { data });
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public byte[] getPixels() {
        return pixels;
    }

}
