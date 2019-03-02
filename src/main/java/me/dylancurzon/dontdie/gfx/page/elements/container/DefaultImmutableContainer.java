package me.dylancurzon.dontdie.gfx.page.elements.container;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.dontdie.gfx.page.InteractOptions;
import me.dylancurzon.dontdie.gfx.page.PositionedElement;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.elements.ImmutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableContainer;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableElement;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.WrappingMutableElement;
import me.dylancurzon.dontdie.util.Vector2i;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.dylancurzon.dontdie.gfx.page.elements.container.Positioning.INLINE;
import static me.dylancurzon.dontdie.gfx.page.elements.container.Positioning.OVERLAY;

@Immutable
public class DefaultImmutableContainer extends ImmutableElement implements ImmutableContainer {

    public static boolean DEBUG = false;

    protected final List<Function<ImmutableContainer, ImmutableElement>> elements;
    protected final Vector2i size;
    protected final Spacing padding;
    protected final Positioning positioning;
    protected final boolean centering;
    private final boolean scrollable;

    protected DefaultImmutableContainer(final Spacing margin, final Consumer<MutableElement> tickConsumer,
                                        final List<Function<ImmutableContainer, ImmutableElement>> elements,
                                        final Vector2i size, final Spacing padding,
                                        final Positioning positioning, final boolean centering,
                                        final boolean scrollable,
                                        final Function<MutableElement, WrappingMutableElement> mutator,
                                        final InteractOptions interactOptions) {
        super(margin, tickConsumer, mutator, interactOptions);
        this.elements = elements;
        this.size = size;
        if (padding == null) {
            this.padding = Spacing.ZERO;
        } else {
            this.padding = padding;
        }
        if (positioning == null) {
            this.positioning = Positioning.DEFAULT;
        } else {
            this.positioning = positioning;
        }
        this.centering = centering;
        this.scrollable = scrollable;
    }

    @Override
    @NotNull
    public MutableContainer asMutable() {
        final List<MutableElement> mutableElements = this.elements.stream()
            .map(fn -> fn.apply(this))
            .map(ImmutableElement::asMutable)
            .collect(Collectors.toList());
        final MutableContainer container = new MutableContainer(super.margin, this, mutableElements) {
            @Override
            public Vector2i calculateSize() {
                Vector2i size = DefaultImmutableContainer.this.size;
                if (size == null || size.getX() == -1 || size.getY() == -1) {
                    Vector2i calculatedSize = Vector2i.of(0, 0);
                    for (final MutableElement mut : mutableElements) {
                        final Vector2i elementSize = mut.getMarginedSize();
                        calculatedSize = calculatedSize.add(
                            DefaultImmutableContainer.this.positioning == INLINE
                                ? Vector2i.of(elementSize.getX(), 0)
                                : Vector2i.of(0, elementSize.getY())
                        );
                        if (DefaultImmutableContainer.this.positioning != INLINE
                            && calculatedSize.getX() < elementSize.getX()) {
                            calculatedSize = calculatedSize.setX(elementSize.getX());
                        }
                        if (DefaultImmutableContainer.this.positioning == INLINE
                            && calculatedSize.getY() < elementSize.getY()) {
                            calculatedSize = calculatedSize.setY(elementSize.getY());
                        }
                    }

                    if (size == null) {
                        return calculatedSize;
                    }
                    if (size.getX() == -1) {
                        size = size.setX(calculatedSize.getX());
                    }
                    if (size.getY() == -1) {
                        size = size.setY(calculatedSize.getY());
                    }
                }
                return size;
            }

            @Override
            public List<PositionedElement> draw() {
//                final Map<MutableElement, Vector2i> positions = super.getPositions();
//                positions.forEach((mut, pos) -> {
//                    final Vector2i elementSize = mut.getSize();
//                    final PixelContainer elementContainer = new PixelContainer(
//                        new int[elementSize.getX() * elementSize.getY()],
//                        elementSize.getX(),
//                        elementSize.getY()
//                    );
//                    mut.render(elementContainer);
//
//                    if (mut.getInteractOptions().shouldHighlight()) {
//                        final Vector2i mousePos = mut.getMousePosition();
//                        final int[] mask = mut.getInteractMask();
//                        for (int dx = 0; dx < elementSize.getX(); dx++) {
//                            for (int dy = 0; dy < elementSize.getY(); dy++) {
//                                if (mask[dx + dy * elementSize.getX()] != 0) {
//                                    final Vector2i dpos = Vector2i.of(dx, dy);
//                                    if (dpos.equals(mousePos)) {
//                                        this.applyHighlight(elementContainer.getPixels());
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }

                return super.getPositions().entrySet().stream()
                    .flatMap(entry ->
                        entry.getKey() instanceof MutableContainer
                            ? ((MutableContainer) entry.getKey()).draw().stream()
                            : Stream.of(new PositionedElement(entry.getKey(), entry.getValue())
                    ))
                    .collect(Collectors.toList());

//                    container.copyPixels(
//                        pos.getX(),
//                        pos.getY(),
//                        elementSize.getX(),
//                        elementContainer.getPixels()
//                    );
//                });
//
//                return null;
            }

            @Override
            public void tick() {
                super.tick();
                mutableElements.forEach(MutableElement::tick);
                final Consumer<MutableElement> consumer = DefaultImmutableContainer.super.getTickConsumer();
                if (consumer != null) {
                    consumer.accept(this);
                }
            }

//            @Override
//            public void render(final PixelContainer container) {
//                final Map<MutableElement, Vector2i> positions = super.getPositions();
//                positions.forEach((mut, pos) -> {
//                    final Vector2i elementSize = mut.getSize();
//                    final PixelContainer elementContainer = new PixelContainer(
//                            new int[elementSize.getX() * elementSize.getY()],
//                            elementSize.getX(),
//                            elementSize.getY()
//                    );
//                    mut.render(elementContainer);
//
//                    if (mut.getInteractOptions().shouldHighlight()) {
//                        final Vector2i mousePos = mut.getMousePosition();
//                        final int[] mask = mut.getInteractMask();
//                        for (int dx = 0; dx < elementSize.getX(); dx++) {
//                            for (int dy = 0; dy < elementSize.getY(); dy++) {
//                                if (mask[dx + dy * elementSize.getX()] != 0) {
//                                    final Vector2i dpos = Vector2i.of(dx, dy);
//                                    if (dpos.equals(mousePos)) {
//                                        this.applyHighlight(elementContainer.getPixels());
//                                        break;
//                                    }
//                                }
//                            }
//                        }
//                    }
//
//                    container.copyPixels(
//                        pos.getX(),
//                        pos.getY(),
//                        elementSize.getX(),
//                        elementContainer.getPixels()
//                    );
//                });
//
//                if (DefaultImmutableContainer.DEBUG) {
//                    for (int x = 0; x < container.getWidth(); x++) {
//                        for (int y = 0; y < container.getHeight(); y++) {
//                            if (x == 0 || x == (container.getWidth() - 1) || y == 0 || y == (container.getHeight() - 1)) {
//                                container.setPixel(x, y, 0xFFFF69B4);
//                            }
//                        }
//                    }
//                }
//
////                if (DefaultImmutableContainer.this.centering) {
////                    // standard draw logic
////                    final MutableElement mut = mutableElements.get(0);
////                    final Vector2i elementSize = mut.getSize();
////                    final PixelContainer elementContainer = new PixelContainer(
////                        new int[elementSize.getX() * elementSize.getY()],
////                        elementSize.getX(),
////                        elementSize.getY()
////                    );
////                    mut.render(elementContainer);
////
////                    // == HIGHLIGHTING ==
////                    this.applyHighlight(elementContainer.getPixels());
////
////                    // find centered position based on this container's size
////                    final Vector2i centered = DefaultImmutableContainer.this.size
////                        .div(2)
////                        .sub(elementSize.div(2))
////                        .floor().toInt();
////                    container.copyPixels(
////                        centered.getX(),
////                        centered.getY(),
////                        elementSize.getX(),
////                        elementContainer.getPixels()
////                    );
////                } else {
////                    final Spacing padding = DefaultImmutableContainer.this.padding;
////                    Vector2i pos = Vector2i.of(
////                        padding.getLeft(),
////                        padding.getTop()
////                    );
////                    for (final MutableElement mut : mutableElements) {
////                        pos = pos.add(
////                            Vector2i.of(mut.getMargin().getLeft(), mut.getMargin().getTop())
////                        );
////                        final Vector2i elementSize = mut.getSize();
////
////                        final int[] elementPixels = new int[elementSize.getX() * elementSize.getY()];
////                        final PixelContainer elementContainer = new PixelContainer(
////                            elementPixels,
////                            elementSize.getX(),
////                            elementSize.getY()
////                        );
////                        mut.render(elementContainer);
////
////                        // == HIGHLIGHTING ==
////                        // This is where we need to apply highlighting to the MutableElement's rendered pixels based on
////                        // the interact-able region specified by MutableElement#getInteractMask.
////                        // They should have a 1:1 ratio, so we can just apply the effect if the mask is not zero, if the
////                        // MutableElement has been marked as currently highlighted.
////
////                        this.applyHighlight(elementPixels);
////
////                        container.copyPixels(
////                            pos.getX(),
////                            pos.getY() - (int) Math.floor(super.scroll),
////                            elementSize.getX(),
////                            elementPixels
////                        );
////                        if (DefaultImmutableContainer.this.inline) {
////                            pos = pos.add(Vector2i.of(mut.getMargin().getRight() + elementSize.getX(), 0));
////                        } else {
////                            pos = pos.add(Vector2i.of(0, mut.getMargin().getBottom() + elementSize.getY()));
////                        }
////                    }
////                }
//            }

            private void applyHighlight(final int[] pixels) {
                for (int i = 0; i < pixels.length; i++) {
                    // In order to darken the pixels, we need to convert them to rgb values first so that they
                    // can be affected individually.
                    final double factor = 0.7;
                    final int value = pixels[i];
                    final int a = (value >> 24) & 0xFF;
                    final int nr = (int) ((value >> 16 & 0xFF) * factor);
                    final int ng = (int) ((value >> 8 & 0xFF) * factor);
                    final int nb = (int) ((value & 0xFF) * factor);
                    pixels[i] = (a << 24) | (nr << 16) | (ng << 8) | nb;
                }
            }
        };
        mutableElements.forEach(mut -> mut.setParent(container));
        return container;
    }

    @NotNull
    public List<Function<ImmutableContainer, ImmutableElement>> getElements() {
        return this.elements;
    }

    @NotNull
    public Vector2i getSize() {
        return this.size;
    }

    @Override
    public Vector2i getMarginedSize() {
        return this.getSize().add(
            Vector2i.of(
                super.margin.getLeft() + super.margin.getRight(),
                super.margin.getBottom() + super.margin.getTop()
            )
        );
    }

    @Override
    public Vector2i getPaddedSize() {
        return this.getSize().sub(
            Vector2i.of(
                this.padding.getLeft() + this.padding.getRight(),
                this.padding.getBottom() + this.padding.getTop()
            )
        );
    }

    @Override
    public boolean isScrollable() {
        return this.scrollable;
    }

    @NotNull
    public Spacing getPadding() {
        return this.padding;
    }

    public boolean isCentering() {
        return this.centering;
    }

    @Override
    public Positioning getPositioning() {
        return this.positioning;
    }

    public static class ContainerBuilder extends Builder<ContainerBuilder> {

        @Override
        public ContainerBuilder self() {
            return this;
        }

    }

    public static abstract class Builder<T extends Builder> extends ImmutableElement.Builder<DefaultImmutableContainer, T> {

        protected final List<Function<ImmutableContainer, ImmutableElement>> elements = new ArrayList<>();
        protected Vector2i size;
        protected Spacing padding;
        protected Positioning positioning;
        protected boolean centering;
        protected boolean scrollable;

        @NotNull
        public T add(final ImmutableElement element) {
            this.elements.add(page -> element);
            return this.self();
        }

        @NotNull
        public T add(final ImmutableElement... elements) {
            for (final ImmutableElement el : elements) {
                this.add(el);
            }
            return this.self();
        }

        @NotNull
        public T add(final List<ImmutableElement> elements) {
            elements.forEach(this::add);
            return this.self();
        }

        @NotNull
        public T add(final Function<ImmutableContainer, ImmutableElement> fn) {
            this.elements.add(fn);
            return this.self();
        }

        @NotNull
        public T setSize(final Vector2i size) {
            this.size = size;
            return this.self();
        }

        @NotNull
        public T setPadding(final Spacing padding) {
            this.padding = padding;
            return this.self();
        }

        @NotNull
        public T setPositioning(final Positioning positioning) {
            this.positioning = positioning;
            return this.self();
        }

        @NotNull
        public T setCentering(final boolean centering) {
            this.centering = centering;
            return this.self();
        }

        @NotNull
        public T setScrollable(final boolean scrollable) {
            this.scrollable = scrollable;
            return this.self();
        }

        @Override
        @NotNull
        public DefaultImmutableContainer build() {
            if (this.centering && this.elements.size() > 1 && this.positioning != OVERLAY) {
                throw new RuntimeException(
                    "A centering ImmutableContainer may only contain a single ImmutableElement!"
                );
            }
            if (this.elements.size() == 0) {
                throw new RuntimeException("Empty ImmutableContainer is not permitted!");
            }
            return new DefaultImmutableContainer(
                super.margin,
                super.tickConsumer,
                this.elements,
                this.size,
                this.padding,
                this.positioning,
                this.centering,
                this.scrollable,
                super.mutator,
                super.interactOptions
            );
        }

    }

}
