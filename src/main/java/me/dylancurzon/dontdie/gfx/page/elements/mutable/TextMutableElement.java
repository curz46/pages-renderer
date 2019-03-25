package me.dylancurzon.dontdie.gfx.page.elements.mutable;

import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.TextImmutableElement;
import me.dylancurzon.dontdie.sprite.TextSprite;
import me.dylancurzon.dontdie.util.Vector2i;

import java.util.function.Consumer;

public class TextMutableElement extends MutableElement {

    private final TextImmutableElement immutableElement;
    private TextSprite sprite;

    public TextMutableElement(final Spacing margin, final TextImmutableElement immutableElement) {
        super(margin, immutableElement.getInteractOptions());
        this.immutableElement = immutableElement;
        this.sprite = this.immutableElement.getSprite();
    }

    public void setSprite(final TextSprite sprite) {
        this.sprite = sprite;
    }

    public TextSprite getSprite() {
        return this.sprite;
    }

    @Override
    public Vector2i calculateSize() {
        return Vector2i.of(this.sprite.getWidth(), this.sprite.getHeight());
    }

    @Override
    public void tick() {
        final Consumer<MutableElement> consumer = this.immutableElement.getTickConsumer();
        if (consumer != null) {
            consumer.accept(this);
        }
    }

}
