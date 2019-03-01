package me.dylancurzon.dontdie.gfx.page;

import com.sun.istack.internal.NotNull;
import me.dylancurzon.dontdie.gfx.page.elements.ImmutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.container.DefaultImmutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.container.ImmutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.container.Positioning;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.WrappingMutableElement;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.util.Vector2i;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

public class PageTemplate extends DefaultImmutableContainer {

    private final Sprite backgroundSprite;
    private final Vector2i position;

    protected PageTemplate(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                           final List<Function<ImmutableContainer, ImmutableElement>> elements,
                           final Vector2i size, final Spacing padding, final Positioning positioning, final boolean centering,
                           final Sprite backgroundSprite, final Vector2i position, final boolean scrollable,
                           final Function<MutableElement, WrappingMutableElement> mutator,
                           final InteractOptions interactOptions) {
        super(margin, tickConsumer, elements, size, padding, positioning, centering, scrollable, mutator, interactOptions);
        this.backgroundSprite = backgroundSprite;
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
    public Sprite getBackgroundSprite() {
        return this.backgroundSprite;
    }

    @NotNull
    public Vector2i getPosition() {
        return this.position;
    }

    public static class Builder extends DefaultImmutableContainer.Builder<Builder> {

        private Sprite backgroundSprite;
        private Vector2i position;

        @NotNull
        public Builder setBackground(final Sprite backgroundSprite) {
            this.backgroundSprite = backgroundSprite;
            return this;
        }

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
                throw new RuntimeException("Position is a required attributea!");
            }

            return new PageTemplate(
                super.margin,
                super.tickConsumer,
                super.elements,
                Vector2i.of(
                    super.size == null ? this.backgroundSprite.getWidth() : super.size.getX(),
                    super.size == null ? this.backgroundSprite.getHeight() : super.size.getY()
                ),
                super.padding,
                super.positioning,
                super.centering,
                this.backgroundSprite,
                this.position,
                super.scrollable,
                super.mutator,
                super.interactOptions
            );
        }

    }

}
