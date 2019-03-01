package me.dylancurzon.dontdie.gfx.page.elements.mutable;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.nea.gfx.PixelContainer;
import me.dylancurzon.nea.util.Vector2i;

public abstract class WrappingMutableElement extends MutableElement {

    @NotNull
    private final MutableElement element;

    protected WrappingMutableElement(@NotNull final MutableElement element) {
        super(element.getMargin(), element.getInteractOptions());
        this.element = element;
    }

    @NotNull
    public MutableElement getWrappedElement() {
        return this.element;
    }

    @Override
    public void tick() {
        this.element.tick();
    }

    @NotNull
    public int[] getInteractMask() {
        return this.element.getInteractMask();
    }

    @Override
    public Vector2i getSize() {
        return this.element.getSize();
    }

    @Override
    public Vector2i calculateSize() {
        return this.element.calculateSize();
    }

    @Override
    public void render(final PixelContainer container) {
        this.element.render(container);
    }

}
