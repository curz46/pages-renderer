package me.dylancurzon.dontdie.designer;

import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.pages.element.sprite.ImmutableSpriteElement;

public class ImmutableButtonElement extends ImmutableSpriteElement {

    public ImmutableButtonElement(ImmutableSpriteElement.Builder builder) {
        super(builder);
    }

    public static class Builder extends ImmutableSpriteElement.Builder {

        public Builder(Sprite unhoveredSprite, Sprite hoveredSprite) {
            setSprite(unhoveredSprite);
            doOnCreate(element -> {
                element.doOnHoverEnd(e -> element.setSprite(unhoveredSprite));
                element.doOnHoverStart(e -> element.setSprite(hoveredSprite));
            });
        }

        @Override
        public ImmutableButtonElement build() {
            return new ImmutableButtonElement(this);
        }

    }

}
