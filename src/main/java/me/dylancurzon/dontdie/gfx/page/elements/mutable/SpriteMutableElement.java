package me.dylancurzon.dontdie.gfx.page.elements.mutable;

import me.dylancurzon.dontdie.gfx.page.InteractOptions;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.util.Vector2i;

public abstract class SpriteMutableElement extends MutableElement {

    protected final Sprite sprite;

    protected SpriteMutableElement(final Spacing margin, final InteractOptions interactOptions, final Sprite sprite) {
        super(margin, interactOptions);
        this.sprite = sprite;
    }

    @Override
    public Vector2i calculateSize() {
        return Vector2i.of(
            this.sprite.getWidth(),
            this.sprite.getHeight()
        );
    }

    public Sprite getSprite() {
        return this.sprite;
    }

}
