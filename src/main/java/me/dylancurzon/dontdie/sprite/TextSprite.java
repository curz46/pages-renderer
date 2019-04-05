package me.dylancurzon.dontdie.sprite;

import java.util.HashMap;
import java.util.Map;

public class TextSprite implements me.dylancurzon.pages.util.TextSprite {

    public static final Map<Character, Sprite> SPRITE_MAP = new HashMap<>();
    public static final int SPRITE_WIDTH;
    public static final int SPRITE_HEIGHT;
    private static final char[] SPRITE_CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ".toCharArray();

    // Not very safe to do this here, probably should be called by a register method
    static {
        // TODO: Make SpriteSheet registered, not hardcoded
        SpriteSheet sheet = SpriteSheets.TEXT;
        // TODO: Perform validation of SpriteSheet
        int spriteWidth = sheet.getWidth() / SPRITE_CHARACTERS.length;
        for (int i = 0; i < SPRITE_CHARACTERS.length; i++) {
            char character = SPRITE_CHARACTERS[i];
            SPRITE_MAP.put(character, sheet.getSprite(i, 0, spriteWidth));
        }
        SPRITE_WIDTH = spriteWidth;
        SPRITE_HEIGHT = sheet.getHeight();
    }

    public static TextSprite of(String content, int spacing) {
        Sprite[] sprites = new Sprite[content.length()];
        char[] characters = content.toUpperCase().toCharArray();
        for (int i = 0; i < characters.length; i++) {
            char character = characters[i];
            if (!SPRITE_MAP.containsKey(character)) throw new IllegalArgumentException("Unsupported character: " + character);
            sprites[i] = SPRITE_MAP.get(character);
        }
        return new TextSprite(SPRITE_WIDTH, content.length() * (SPRITE_WIDTH + spacing), SPRITE_HEIGHT, spacing, sprites);
    }

//    /**
//     * Create a TextSprite given a {@link SpriteSheet} of characters, a String and a spacing.
//     * @param spriteSheet an {@link SpriteSheet} of characters. This is assumed to be a single row of Sprites in the
//     *                    order specified by {@link this#SPRITE_CHARACTERS}.
//     */
//    public static TextSprite of(final SpriteSheet spriteSheet, final String content, final int spacing) {
//        if (spriteSheet.getWidth() % SPRITE_CHARACTERS.length != 0) {
//            throw new IllegalArgumentException("Argument 'spritesheet' contains too few/many characters!");
//        }
//
//        final int spriteWidth = spriteSheet.getWidth() / SPRITE_CHARACTERS.length;
//        final int width = content.length() * spriteWidth + Math.max(0, content.length() - 1) * spacing;
//        final byte[] pixels = new byte[width * spriteSheet.getHeight() * 4];
//
//        final char[] characters = content.toUpperCase().toCharArray();
//
//        for (int i = 0; i < characters.length; i++) {
//            final char character = characters[i];
//
//            int spriteIndex = -1;
//            for (int j = 0; j < SPRITE_CHARACTERS.length; j++) {
//                if (SPRITE_CHARACTERS[j] == character) {
//                    spriteIndex = j;
//                    break;
//                }
//            }
//
//            if (spriteIndex == -1) {
//                throw new IllegalArgumentException("Argument 'content' contains an invalid character: " + character);
//            }
//
//            final Sprite sprite = spriteSheet.getSprite(spriteIndex, 0, spriteWidth, spriteSheet.getHeight());
//            final byte[] data = sprite.getFrames()[0];
//
////            DataBuffer dataBuffer = new DataBufferByte(data, data.length);
////
//////3 bytes per pixel: red, green, blue
////            WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, 5, 5, 4 * 5, 4, new int[] {0, 1, 2, 3}, (Point)null);
////            ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), true, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
////            BufferedImage image = new BufferedImage(cm, raster, true, null);
////
////            try {
////                ImageIO.write(image, "png", new File("output.png"));
////            } catch (IOException e) {
////                e.printStackTrace();
////            }
//
//            for (int dx = 0; dx < spriteWidth; dx++) {
//                for (int dy = 0; dy < spriteSheet.getHeight(); dy++) {
////                    System.out.println("dx: " + dx);
////                    System.out.println("dy: " + dy);
//                    final int x = dx + (i * (spriteWidth + spacing));
//                    final int y = dy;
//
////                    System.out.println("x: " + x);
////                    System.out.println("y: " + y);
//
//                    for (int b = 0; b < 4; b++) { // rgba
//                        pixels[(x + y * width) * 4 + b] = data[(dx + dy * spriteWidth) * 4 + b];
//                    }
//                }
//            }
//        }
//
////        DataBuffer dataBuffer = new DataBufferByte(pixels, pixels.length);
////
//////3 bytes per pixel: red, green, blue
////        WritableRaster raster = Raster.createInterleavedRaster(dataBuffer, width, spriteSheet.getHeight(), 4 * width, 4, new int[] {0, 1, 2, 3}, (Point)null);
////        ColorModel cm = new ComponentColorModel(ColorModel.getRGBdefault().getColorSpace(), true, true, Transparency.OPAQUE, DataBuffer.TYPE_BYTE);
////        BufferedImage image = new BufferedImage(cm, raster, true, null);
////
////        try {
////            ImageIO.write(image, "png", new File("output.png"));
////        } catch (IOException e) {
////            e.printStackTrace();
////        }
//
//        return new TextSprite(width, spriteSheet.getHeight(), pixels);
//    }

    private TextSprite(int spriteWidth, int width, int height, int spacing, Sprite[] sprites) {
        this.spriteWidth = spriteWidth;
//        super(width, height, 1, new byte[][] { pixels });
        this.width = width;
        this.height = height;
        this.spacing = spacing;
        this.sprites = sprites;
    }

    private final int spriteWidth;
    private final int width;
    private final int height;
    private final int spacing;
    private final Sprite[] sprites;

    public int getSpriteWidth() {
        return spriteWidth;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getSpacing() {
        return spacing;
    }

    public Sprite[] getSprites() {
        return sprites;
    }

}
