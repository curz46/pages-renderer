package me.dylancurzon.dontdie.gfx.page.elements.mutable;

import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.TextImmutableElement;
import me.dylancurzon.dontdie.util.Vector2i;

import java.util.function.Consumer;

public class TextMutableElement extends MutableElement {

    private final TextImmutableElement immutableElement;
//    private TextType.TextSprite sprite;

    public TextMutableElement(final Spacing margin, final TextImmutableElement immutableElement) {
        super(margin, immutableElement.getInteractOptions());
        this.immutableElement = immutableElement;
//        this.sprite = this.immutableElement.getSprite();
    }

//    public void setSprite(final TextType.TextSprite sprite) {
//        this.sprite = sprite;
//    }

    @Override
    public Vector2i calculateSize() {
//        return this.sprite.getSize();
        return Vector2i.of(0, 0);
    }

    @Override
    public void tick() {
        final Consumer<MutableElement> consumer = this.immutableElement.getTickConsumer();
        if (consumer != null) {
            consumer.accept(this);
        }
    }

}
