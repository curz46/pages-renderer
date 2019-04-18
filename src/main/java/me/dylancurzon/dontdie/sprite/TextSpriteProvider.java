package me.dylancurzon.dontdie.sprite;

public class TextSpriteProvider {

    public static final char[] ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz".toCharArray();
    public static final char[] UPPERCASE_ALPHA = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    public static final char[] ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
    public static final char[] UPPERCASE_ALPHANUMERIC = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();

    private static final int DEFAULT_SPACING = 2;

    private final char[] characters;
    private final Sprite[] sprites;
    private final int characterWidth;
    private final int characterHeight;
    private final Sprite emptySprite;

    public TextSpriteProvider(char[] characters, SpriteSheet sheet) {
        this.characters = characters;
        System.out.println(characters.length);
        // +1 for emptySprite
        sprites = new Sprite[characters.length + 1];

        characterWidth = sheet.getWidth() / characters.length;
        if (characterWidth != ((double) sheet.getWidth()) / characters.length) {
            throw new IllegalArgumentException(String.format(
                "SpriteSheet width, %d, is not a multiple of characters length, %d",
                sheet.getWidth(), characters.length
            ));
        }
        characterHeight = sheet.getHeight();

        byte[][] bytes = new byte[1][characterWidth * characterHeight * 4];
        emptySprite = new Sprite(characterWidth, characterHeight, 1, bytes);
        sprites[0] = emptySprite;

        for (int i = 0; i < characters.length; i++) {
            sprites[i + 1] = sheet.getSprite(i, 0, characterWidth, sheet.getHeight());
        }
    }

    public TextSprite getSprite(String content) {
        return getSprite(content, DEFAULT_SPACING);
    }

    public TextSprite getSprite(String content, int spacing) {
        Sprite[] contentSprites = new Sprite[content.length()];
        char[] contentCharArray = content.toUpperCase().toCharArray();
        for (int i = 0; i < contentCharArray.length; i++) {
            char character = contentCharArray[i];

            // Whenever we find a space character, use our emptySprite
            if (character == ' ') {
                contentSprites[i] = emptySprite;
                continue;
            }

            int index = -1;
            for (int j = 0; j < characters.length; j++) {
                if (characters[j] == character) {
                    index = j;
                    break;
                }
            }

            if (index == -1) {
                throw new IllegalArgumentException("Argument 'content' contains unsupported character: " + character);
            }

            contentSprites[i] = sprites[index + 1];
            System.out.println(sprites[index + 1]);
        }

        return new TextSprite(characterWidth, characterHeight, spacing, contentSprites);
    }

    public Sprite[] getSprites() {
        return sprites;
    }

    public char[] getCharacters() {
        return characters;
    }

}
