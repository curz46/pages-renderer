package me.dylancurzon.dontdie.gfx.page.elements.mutable;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import me.dylancurzon.dontdie.gfx.page.AlignedElement;
import me.dylancurzon.dontdie.gfx.page.Spacing;
import me.dylancurzon.dontdie.gfx.page.animation.Animation;
import me.dylancurzon.dontdie.gfx.page.animation.QuarticEaseInAnimation;
import me.dylancurzon.dontdie.gfx.page.elements.container.ImmutableContainer;
import me.dylancurzon.dontdie.util.Cached;
import me.dylancurzon.dontdie.util.Vector2d;
import me.dylancurzon.dontdie.util.Vector2i;

import java.awt.*;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static me.dylancurzon.dontdie.gfx.page.elements.container.Positioning.*;

public abstract class MutableContainer extends MutableElement {

    private static final double SCROLL_FACTOR = 8;

    private final ImmutableContainer container;

    private final List<MutableElement> elements;
    private final Cached<Map<MutableElement, Vector2i>> positions = new Cached<>();

    protected double scroll;
    protected double scrollVelocity;

    private TransformHandler transform;

    private Color fillColor;
    private Color lineColor;
    private Integer lineWidth;

    protected MutableContainer(final Spacing margin, final ImmutableContainer container,
                               final List<MutableElement> elements) {
        super(margin, container.getInteractOptions());
        this.container = container;
        this.elements = elements;

        this.fillColor = container.getFillColor().orElse(null);
        this.lineColor = container.getLineColor().orElse(null);
        this.lineWidth = container.getLineWidth().orElse(null);
    }

    public abstract List<AlignedElement> draw();

    @NotNull
    public List<MutableElement> getElements() {
        return this.elements;
    }

    public Map<MutableElement, Vector2i> getPositions() {
        Map<MutableElement, Vector2i> result = this.positions.get()
            .orElseGet(() -> {
                final Map<MutableElement, Vector2i> positions = this.calculatePositions();
                this.positions.set(positions);
                return positions;
            });

        return result;
    }

    /**
     * @return A map of each MutableElement (in {@link this#elements} and its calculated position. It factors in
     * if the {@link this#container} is centering, inline, padded and includes each MutableElement's margin.
     */
    private Map<MutableElement, Vector2i> calculatePositions() {
        final Map<MutableElement, Vector2i> positions = new LinkedHashMap<>();
        if (this.elements.isEmpty()) return positions;

        if (this.container.isCentering()) {
            for (final MutableElement mut : this.elements) {
                final Vector2i elementSize = mut.getSize();

                // find centered position based on this container's size
                final Vector2i centered = this.container.getSize()
                    .div(2)
                    .sub(elementSize.div(2))
                    .floor().toInt();
                positions.put(mut, centered.sub(Vector2i.of(0, (int) this.scroll)));
            }
        } else {
            final Spacing padding = this.container.getPadding();
            Vector2i pos = Vector2i.of(
                    padding.getLeft(),
                    padding.getTop()
            );
            for (final MutableElement mut : this.elements) {
                final Vector2i delta =
                    Vector2i.of(mut.getMargin().getLeft(), mut.getMargin().getTop());
                if (this.container.getPositioning() != OVERLAY) {
                    pos = pos.add(
                        Vector2i.of(mut.getMargin().getLeft(), mut.getMargin().getTop())
                    );
                } else {
                    pos = delta;
                }
                final Vector2i elementSize = mut.getSize();

                positions.put(mut, pos.sub(Vector2i.of(0, (int) this.scroll)));

                if (this.container.getPositioning() == INLINE) {
                    pos = pos.add(Vector2i.of(mut.getMargin().getRight() + elementSize.getX(), 0));
                } else if (this.container.getPositioning() == DEFAULT){
                    pos = pos.add(Vector2i.of(0, mut.getMargin().getBottom() + elementSize.getY()));
                }
            }
        }

        return positions;
    }

    @Override
    public void click(@NotNull final Vector2i position) {
        super.click(position);
        // for each MutableElement of this container, find the position relative to its position in this
        // container.
        final Map<MutableElement, Vector2i> elementPositionMap = this.getPositions();
        elementPositionMap.forEach((mut, elementPos) -> {
            final Vector2i relative = position.sub(elementPos);
            final Vector2i size = mut.getSize();
            // ensure in bounds
            if (relative.getX() < 0 || relative.getX() >= size.getX() ||
                relative.getY() < 0 || relative.getY() >= size.getY()) {
                return;
            }
            mut.click(relative);
        });
    }

    @Override
    public Vector2i getMousePosition(final MutableElement element) {
        final Vector2i position = this.getPositions().get(element);
        if (position == null || this.parent == null) return null;
        final Vector2i mousePosition = this.parent.getMousePosition(this);
        if (mousePosition == null) return null;
        return mousePosition.sub(position);
    }

    @Override
    public void tick() {
        if (!this.container.isScrollable()) return;
        final double originalScroll = this.scroll;
        this.scroll += this.scrollVelocity;
        this.scrollVelocity *= 0.8;
        this.checkBounds();
        if (originalScroll != this.scroll) {
            this.positions.clear();
        }
    }

    public void scroll(final double amount) {
        if (!this.container.isScrollable()) return;
        if (this.transform != null) {
            this.transform = null;
        }
        this.scrollVelocity += amount * SCROLL_FACTOR;
    }

    public Optional<Color> getFillColor() {
        return Optional.ofNullable(this.fillColor);
    }

    public Optional<Color> getLineColor() {
        return Optional.ofNullable(this.lineColor);
    }

    public Optional<Integer> getLineWidth() {
        return Optional.ofNullable(this.lineWidth);
    }

    private void checkBounds() {
        if ((this.scroll + 5) < 0) {
            this.transform = new TransformHandler(
                Vector2d.of(0, this.scroll),
                Vector2d.of(0, 0),
                new QuarticEaseInAnimation(0, 1, 20)
            );
        }
        final double max = this.getMaxScroll();
        if ((this.scroll - 5) > max) {
            this.transform = new TransformHandler(
                Vector2d.of(0, this.scroll),
                Vector2d.of(0, max),
                new QuarticEaseInAnimation(0, 1, 20)
            );
        }

        if (this.transform != null) {
            this.transform.tick();
            this.scroll = this.transform.getPosition().getY();
        }
    }

    private double getMaxScroll() {
        Vector2i size = Vector2i.of(
            0,
            0
        );
        for (final MutableElement mut : this.elements) {
            final Vector2i elementSize = mut.getSize()
                .add(Vector2i.of(
                    mut.getMargin().getLeft() + mut.getMargin().getRight(),
                    mut.getMargin().getBottom() + mut.getMargin().getTop()
                ));
            size = size.add(Vector2i.of(0, elementSize.getY()));
        }
        return size.getY() - this.container.getPaddedSize().getY();
    }

    public static class TransformHandler {

        private final Vector2d initialPosition;
        private final Vector2d destination;
        private final Animation animation;

        public TransformHandler(final Vector2d initialPosition, final Vector2d destination,
                                final Animation animation) {
            this.initialPosition = initialPosition;
            this.destination = destination;
            this.animation = animation;
        }

        public void tick() {
            this.animation.tick();
        }

        public Vector2d getPosition() {
            final double progress = this.animation.determineValue();
            final Vector2d delta = this.destination.sub(this.initialPosition).mul(progress);
            return this.initialPosition.add(delta);
        }

        public boolean isCompleted() {
            return this.animation.isCompleted();
        }

    }

}
