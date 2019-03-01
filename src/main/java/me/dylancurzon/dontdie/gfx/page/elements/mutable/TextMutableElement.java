package me.dylancurzon.dontdie.gfx.page.elements.mutable;

import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.TextImmutableElement;
import me.dylancurzon.dontdie.util.Vector2i;

import java.util.function.Consumer;

public class TextMutableElement extends MutableElement {

    private final TextImmutableElement immutableElement;
    private TextType.TextSprite sprite;

    public TextMutableElement(final Spacing margin, final TextImmutableElement immutableElement) {
        super(margin, immutableElement.getInteractOptions());
        this.immutableElement = immutableElement;
        this.sprite = this.immutableElement.getSprite();
    }

    public void setSprite(final TextType.TextSprite sprite) {
        this.sprite = sprite;
    }

    @Override
    public Vector2i calculateSize() {
        return this.sprite.getSize();
    }

    @Override
    public void tick() {
        final Consumer<MutableElement> consumer = this.immutableElement.getTickConsumer();
        if (consumer != null) {
            consumer.accept(this);
        }
    }

    @Override
    public int[] getInteractMask() {
//        final PixelContainer container = new PixelContainer(
//            new int[this.getSize().getX() * this.getSize().getY()],
//            this.getSize().getX(),
//            this.getSize().getY()
//        );
//        this.render(container);

        final int[] mask = new int[this.getSize().getX() * this.getSize().getY()];
        for (int i = 0; i < mask.length; i++) {
//            mask[i] = container.getPixels()[i];
            mask[i] = 1;
        }
        return mask;
    }

    @Override
    public void render(final PixelContainer container) {
        this.sprite.render(container, 0, 0);
    }

}
