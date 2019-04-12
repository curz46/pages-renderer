package me.dylancurzon.dontdie.designer.button;

import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.pages.element.MutableElement;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.element.sprite.ImmutableSpriteElement;
import me.dylancurzon.pages.element.sprite.MutableSpriteElement;

import java.util.function.Function;

public class ImmutableButtonElement extends ImmutableSpriteElement {

    private final Sprite selectedSprite;

    public ImmutableButtonElement(ImmutableSpriteElement.Builder builder, Sprite selectedSprite) {
        super(builder);
        this.selectedSprite = selectedSprite;
    }

    @Override
    public Function<MutableContainer, MutableElement> asMutable() {
        return parent -> {
            MutableSpriteElement element = new MutableButtonElement(parent, margin, tag, zPosition, sprite, decoration, selectedSprite);
            listeners.forEach(element::subscribe);
            onCreate.forEach(consumer -> consumer.accept(element));
            return element;
        };
    }

    public static class Builder extends ImmutableSpriteElement.Builder {

        private final Sprite selectedSprite;

        public Builder(Sprite unhoveredSprite, Sprite hoveredSprite, Sprite selectedSprite) {
            this.selectedSprite = selectedSprite;
            setSprite(unhoveredSprite);
            doOnCreate(element -> {
                element.doOnHoverEnd(e -> element.setSprite(unhoveredSprite));
                element.doOnHoverStart(e -> element.setSprite(hoveredSprite));
            });
        }

        @Override
        public ImmutableButtonElement build() {
            return new ImmutableButtonElement(this, selectedSprite);
        }

    }

}
