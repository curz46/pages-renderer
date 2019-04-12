package me.dylancurzon.dontdie.designer.button;

import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.pages.element.ElementDecoration;
import me.dylancurzon.pages.element.container.MutableContainer;
import me.dylancurzon.pages.element.sprite.MutableSpriteElement;
import me.dylancurzon.pages.util.Spacing;
import org.jetbrains.annotations.Nullable;

public class MutableButtonElement extends MutableSpriteElement {

    @Nullable
    private Sprite selectedSprite;
    private boolean selected;

    public MutableButtonElement(@Nullable MutableContainer parent,
                                Spacing margin,
                                @Nullable String tag,
                                @Nullable Integer zPosition,
                                me.dylancurzon.pages.util.Sprite sprite,
                                ElementDecoration decoration,
                                Sprite selectedSprite) {
        super(parent, margin, tag, zPosition, sprite, decoration);
        this.selectedSprite = selectedSprite;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public me.dylancurzon.pages.util.Sprite getSprite() {
        return (selected && selectedSprite != null) ? selectedSprite : sprite;
    }

}
