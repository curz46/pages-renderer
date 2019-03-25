package me.dylancurzon.dontdie.gfx.page;

import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableElement;
import me.dylancurzon.dontdie.util.Vector2i;

public class AlignedElement {

    private final MutableElement element;
    private final Vector2i position;

    public AlignedElement(final MutableElement element, final Vector2i position) {
        this.element = element;
        this.position = position;
    }

    public MutableElement getElement() {
        return this.element;
    }

    public Vector2i getPosition() {
        return this.position;
    }

    @Override
    public String toString() {
        return "AlignedElement{" +
            "element=" + this.element.toString() +
            ", position=" + this.position.toString() +
            "}";
    }

}
