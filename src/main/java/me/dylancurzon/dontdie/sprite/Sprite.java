package me.dylancurzon.dontdie.sprite;

import de.matthiasmann.twl.utils.PNGDecoder;
import me.dylancurzon.dontdie.Tickable;
import me.dylancurzon.dontdie.util.Buffers;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class Sprite implements me.dylancurzon.pages.util.Sprite {

    protected final int width;
    protected final int height;
    protected final int frameCount;
    protected final byte[][] frames;

    public static Sprite loadSprite(String name) {
        return Sprite.loadAnimatedSprite(name, 1);
    }

    public static Sprite loadAnimatedSprite(String name, int frameCount) {
        // Assume that this texture is located at textures/<name>.png
        try {
            InputStream in = Sprite.class.getClassLoader()
                .getResourceAsStream("textures/" + name + ".png");

            PNGDecoder decoder = new PNGDecoder(in);

            int width = decoder.getWidth();
            int imageHeight = decoder.getHeight();

//            ByteBuffer pixelBuffer = ByteBuffer.wrap(new byte[4 * width * imageHeight]);
            ByteBuffer spriteBuffer = BufferUtils.createByteBuffer(width * imageHeight * 4);
            decoder.decode(spriteBuffer, width * 4, PNGDecoder.Format.RGBA);
            spriteBuffer.flip();

            byte[] pixels = Buffers.asByteArray(spriteBuffer);

            if (imageHeight % frameCount != 0) {
                throw new RuntimeException("AnimatedSprite is malformed; not a multiple of frameCount");
            }
            int height = imageHeight / frameCount;

            byte[][] frames = new byte[frameCount][width * height * 4];
            for (int frameNum = 0; frameNum < frameCount; frameNum++) {
//                ByteBuffer buf = BufferUtils.createByteBuffer(4 * width * height);
//                final ByteBuffer buf = ByteBuffer.wrap(new byte[4 * width * height]);

                byte[] frame = new byte[width * height * 4];

//                final int initialY = i * height;
//                final int indexBegin = initialY * width * 4;
//                final int indexEnd = indexBegin + (width * height * 4);

//                for (int j = indexBegin; j < indexEnd; j++) {
//                    buf.put(pixels[j]);
//                }

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
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Sprite(int width, int height, int frameCount, byte[][] frames) {
        this.width = width;
        this.height = height;
        this.frameCount = frameCount;
        this.frames = frames;
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
