package me.dylancurzon.dontdie.gfx.page;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.dontdie.gfx.page.elements.ImmutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.container.DefaultImmutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.container.ImmutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.container.Positioning;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.WrappingMutableElement;
import me.dylancurzon.dontdie.util.Vector2i;

import java.awt.*;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PageTemplate extends DefaultImmutableContainer {

    private final Vector2i position;

    protected PageTemplate(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                           final List<Function<ImmutableContainer, ImmutableElement>> elements,
                           final Vector2i size, final Spacing padding, final Positioning positioning, final boolean centering,
                           final Vector2i position, final boolean scrollable,
                           final Color fillColor, final Color lineColor, final Integer lineWidth,
                           final Function<MutableElement, WrappingMutableElement> mutator,
                           final InteractOptions interactOptions) {
        super(margin, tickConsumer, elements, size, padding, positioning, centering, scrollable, fillColor, lineColor, lineWidth, mutator, interactOptions);
        this.position = position;
    }

    @NotNull
    public static Builder builder() {
        return new Builder();
    }

    @Override
    @NotNull
    public Page asMutable() {
        final MutableContainer container = super.asMutable();
        final Page page = new Page(this, container);
        container.setParent(page);
        return page;
    }

    @NotNull
    public Vector2i getPosition() {
        return this.position;
    }

    public static class Builder extends DefaultImmutableContainer.Builder<Builder> {

        private Vector2i position;

        @NotNull
        public Builder setPosition(final Vector2i position) {
            this.position = position;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        @NotNull
        public PageTemplate build() {
            if (super.centering && super.elements.size() > 1) {
                throw new RuntimeException(
                    "A centering PageTemplate may only contain a single ImmutableElement!"
                );
            }
            if (super.elements.size() == 0) {
                throw new RuntimeException("Empty PageTemplate is not permitted!");
            }
            if (this.position == null) {
                throw new RuntimeException("Position is a required attribute!");
            }
            if (this.size == null) {
                throw new RuntimeException("Size is a required attribute!");
            }

            return new PageTemplate(
                super.margin,
                super.tickConsumer,
                super.elements,
                super.size,
                super.padding,
                super.positioning,
                super.centering,
                this.position,
                super.scrollable,
                super.fillColor,
                super.lineColor,
                super.lineWidth,
                super.mutator,
                super.interactOptions
            );
        }

    }

}
