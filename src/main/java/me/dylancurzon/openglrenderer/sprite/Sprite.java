package me.dylancurzon.openglrenderer.sprite;

import de.matthiasmann.twl.utils.PNGDecoder;
import me.dylancurzon.openglrenderer.Tickable;
import me.dylancurzon.openglrenderer.util.Buffers;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;

public class Sprite implements me.dylancurzon.pages.util.Sprite {

    final int width;
    final int height;
    final int frameCount;
    final byte[][] frames;

    public static Sprite loadSprite(URL source) throws IOException {
        return Sprite.loadSprite(source, 1);
    }

    public static Sprite loadSprite(URL source, int frameCount) throws IOException {
        InputStream in = source.openStream();

        PNGDecoder decoder = new PNGDecoder(in);

        int width = decoder.getWidth();
        int imageHeight = decoder.getHeight();

        ByteBuffer spriteBuffer = BufferUtils.createByteBuffer(width * imageHeight * 4);
        decoder.decode(spriteBuffer, width * 4, PNGDecoder.Format.RGBA);
        spriteBuffer.flip();

        byte[] pixels = Buffers.asByteArray(spriteBuffer);

        if (imageHeight % frameCount != 0) {
            throw new IllegalArgumentException(String.format(
                "Sprite URL is malformed; imageHeight, %d, is not a multiple of frameCount, %d",
                imageHeight,
                frameCount
            ));
        }
        int height = imageHeight / frameCount;

        byte[][] frames = new byte[frameCount][width * height * 4];
        for (int frameNum = 0; frameNum < frameCount; frameNum++) {
            byte[] frame = new byte[width * height * 4];

            for (int dx = 0; dx < width; dx++) {
                for (int dy = 0; dy < height; dy++) {
                    int xp = dx;
                    int yp = dy + frameNum * height;

                    for (int b = 0; b < 4; b++) {
                        frame[(dx + dy * width) * 4 + b] = pixels[(xp + yp * width) * 4 + b];
                    }
                }
            }

//                buf.flip();
//                frames[i] = buf;
            frames[frameNum] = frame;
        }

        return new Sprite(width, height, frameCount, frames);
    }

    public Sprite(int width, int height, int frameCount, byte[][] frames) {
        this.width = width;
        this.height = height;
        this.frameCount = frameCount;
        this.frames = frames;
    }

    public TickableSprite createTickable() {
        return new TickableSprite();
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getFrameCount() {
        return frameCount;
    }

    public byte[][] getFrames() {
        return frames;
    }

    public class TickableSprite implements Tickable {

        private int ticks;

        @Override
        public void tick() {
            ticks++;
        }

        public byte[] getCurrentFrame() {
            return frames[ticks % frameCount];
        }

        public int getTicks() {
            return ticks;
        }

        public Sprite getAnimatedSprite() {
            return Sprite.this;
        }

    }

}
