package me.dylancurzon.dontdie.sprite;

public class TextSprite implements me.dylancurzon.pages.util.TextSprite {

    TextSprite(int characterWidth, int characterHeight, int spacing, Sprite[] sprites) {
        this.characterWidth = characterWidth;
        this.height = characterHeight;
        this.spacing = spacing;
        this.sprites = sprites;
    }

    private final int characterWidth;
    private final int height;
    private final int spacing;
    private final Sprite[] sprites;

    @Override
    public int getWidth() {
        // TODO: Ensure that this is correct. I'm fairly certain that it is, but it was previously implemented
        //  differently.
        return characterWidth * sprites.length + spacing * (sprites.length - 1);
    }

    @Override
    public int getHeight() {
        return height;
    }

    public int getCharacterWidth() {
        return characterWidth;
    }

    public int getSpacing() {
        return spacing;
    }

    public Sprite[] getSprites() {
        return sprites;
    }

}
