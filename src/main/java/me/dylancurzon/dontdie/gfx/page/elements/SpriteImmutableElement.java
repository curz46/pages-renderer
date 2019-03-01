package me.dylancurzon.dontdie.gfx.page.elements;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.dontdie.gfx.page.InteractOptions;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.WrappingMutableElement;
import me.dylancurzon.dontdie.sprite.AnimatedSprite;
import me.dylancurzon.dontdie.sprite.Sprite;
import me.dylancurzon.dontdie.util.Vector2i;

import java.util.function.Consumer;
import java.util.function.Function;

@Immutable
public abstract class SpriteImmutableElement extends ImmutableElement {

    private final Sprite sprite;
    private final AnimatedSprite animatedSprite;

    public SpriteImmutableElement(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                                  final Function<MutableElement, WrappingMutableElement> mutator,
                                  final InteractOptions interactOptions, final Sprite sprite,
                                  final AnimatedSprite animatedSprite) {
        super(margin, tickConsumer, mutator, interactOptions);
        this.sprite = sprite;
        this.animatedSprite = animatedSprite;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static Builder builder(final SpriteImmutableElement element) {
        return new Builder(element);
    }

    public Sprite getSprite() {
        return this.sprite;
    }

    public AnimatedSprite getAnimatedSprite() {
        return this.animatedSprite;
    }

    public static class StaticSpriteImmutableElement extends SpriteImmutableElement {

        private final Sprite sprite;

        public StaticSpriteImmutableElement(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                                            final Sprite sprite,
                                            final Function<MutableElement, WrappingMutableElement> mutator,
                                            final InteractOptions interactOptions) {
            super(margin, tickConsumer, mutator, interactOptions, sprite, null);
            this.sprite = sprite;
        }

        @Override
        public MutableElement asMutable() {
            return super.doMutate(new MutableElement(super.margin, super.interactOptions) {
                @Override
                public Vector2i calculateSize() {
                    final Sprite sprite = StaticSpriteImmutableElement.this.sprite;
                    return Vector2i.of(
                        sprite.getWidth(),
                        sprite.getHeight()
                    );
                }

                @Override
                public void tick() {
                    final Consumer<MutableElement> consumer = StaticSpriteImmutableElement.super.getTickConsumer();
                    if (consumer != null) {
                        consumer.accept(this);
                    }
                }

                @Override
                public int[] getInteractMask() {
                    // TODO: make this only evaluate when necessary in future
                    final StaticSprite sprite = StaticSpriteImmutableElement.this.sprite;
                    final int[] mask = new int[this.getSize().getX() * this.getSize().getY()];
                    final int[] pixels = sprite.getPixels();
                    for (int i = 0; i < pixels.length; i++) {
                        if (pixels[i] != 0) mask[i] = 1;
                    }
                    return mask;
                }

                @Override
                public void render(final PixelContainer pixelContainer) {
                    final Sprite sprite = StaticSpriteImmutableElement.this.sprite;
                    sprite.render(pixelContainer, 0, 0);
                }
            });
        }

    }

    public static class AnimatedSpriteImmutableElement extends SpriteImmutableElement {

        private final AnimatedSprite sprite;

        public AnimatedSpriteImmutableElement(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                                              final AnimatedSprite sprite,
                                              final Function<MutableElement, WrappingMutableElement> mutator,
                                              final InteractOptions interactOptions) {
            super(margin, tickConsumer, mutator, interactOptions, null, sprite);
            this.sprite = sprite;
        }

        @Override
        public MutableElement asMutable() {
            final TickContainer container = this.sprite.createContainer();
            return super.doMutate(new MutableElement(super.margin, super.interactOptions) {
                @Override
                public void tick() {
                    final Consumer<MutableElement> consumer = AnimatedSpriteImmutableElement.super.getTickConsumer();
                    if (consumer != null) {
                        consumer.accept(this);
                    }
                    container.tick();
                }

                @Override
                public int[] getInteractMask() {
                    // TODO: make this only evaluate when necessary in future
                    final PixelContainer container = new PixelContainer(
                        new int[this.getSize().getX() * this.getSize().getY()],
                        this.getSize().getX(),
                        this.getSize().getY()
                    );
                    this.render(container);
                    final int[] mask = new int[this.getSize().getX() * this.getSize().getY()];
                    final int[] pixels = container.getPixels();
                    for (int i = 0; i < pixels.length; i++) {
                        if (pixels[i] != 0) mask[i] = 1;
                    }
                    return mask;
                }

                @Override
                public Vector2i calculateSize() {
                    return Vector2i.of(
                        container.getWidth(),
                        container.getHeight()
                    );
                }

                @Override
                public void render(final PixelContainer pixelContainer) {
                    container.render(pixelContainer, 0, 0);
                }
            });
        }

    }

    public static class Builder extends ImmutableElement.Builder<SpriteImmutableElement, Builder> {

        protected StaticSprite sprite;
        protected AnimatedSprite animatedSprite;

        protected Builder() {}

        protected Builder(final SpriteImmutableElement element) {
            this.interactOptions = element.interactOptions;
            this.animatedSprite = element.animatedSprite;
            this.tickConsumer = element.tickConsumer;
            this.mutator = element.mutator;
            this.sprite = element.sprite;
            this.margin = element.margin;
        }

        @NotNull
        public Builder setSprite(final StaticSprite sprite) {
            if (this.animatedSprite != null) {
                throw new RuntimeException(
                    "SpriteImmutableElement.Builder: invalid usage, Sprite and AnimatedSprite "
                    + "should not both be set."
                );
            }
            this.sprite = sprite;
            return this;
        }

        @NotNull
        public Builder setAnimatedSprite(final AnimatedSprite animatedSprite) {
            if (this.sprite != null) {
                throw new RuntimeException(
                    "SpriteImmutableElement.Builder: invalid usage, Sprite and AnimatedSprite "
                        + "should not both be set."
                );
            }
            this.animatedSprite = animatedSprite;
            return this;
        }

        @Override
        public Builder self() {
            return this;
        }

        @Override
        @NotNull
        public SpriteImmutableElement build() {
            if (this.animatedSprite != null) {
                return new AnimatedSpriteImmutableElement(
                    super.margin,
                    super.tickConsumer,
                    this.animatedSprite,
                    super.mutator,
                    super.interactOptions
                );
            }
            if (this.sprite == null) {
                throw new RuntimeException(
                    "SpriteImmutableElement.Builder requires a Sprite or AnimatedSprite to build!"
                );
            }
            return new StaticSpriteImmutableElement(
                super.margin,
                super.tickConsumer,
                this.sprite,
                super.mutator,
                super.interactOptions
            );
        }

    }

}
