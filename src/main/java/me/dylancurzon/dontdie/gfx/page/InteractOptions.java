package me.dylancurzon.dontdie.gfx.page;

import com.sun.istack.internal.NotNull;
import jdk.nashorn.internal.ir.annotations.Immutable;
import me.dylancurzon.dontdie.gfx.page.elements.mutable.MutableElement;

import java.util.function.Consumer;

@Immutable
public class InteractOptions {

    private final boolean highlighting;
    private final Consumer<MutableElement> clickConsumer;

    public InteractOptions(final boolean highlighting, final Consumer<MutableElement> clickConsumer) {
        this.highlighting = highlighting;
        this.clickConsumer = clickConsumer;
    }

    public static InteractOptions empty() {
        return new InteractOptions(false, null);
    }

    public static InteractOptionsBuilder builder() {
        return new InteractOptionsBuilder();
    }

    public boolean shouldHighlight() {
        return this.highlighting;
    }

    public Consumer<MutableElement> getClickConsumer() {
        return this.clickConsumer;
    }

    public static class InteractOptionsBuilder {

        private boolean highlighting = false;
        private Consumer<MutableElement> clickConsumer;

        @NotNull
        public InteractOptionsBuilder setHighlighting(final boolean value) {
            this.highlighting = value;
            return this;
        }

        @NotNull
        public InteractOptionsBuilder click(final Consumer<MutableElement> consumer) {
            this.clickConsumer = consumer;
            return this;
        }

        @NotNull
        public InteractOptions build() {
            return new InteractOptions(this.highlighting, this.clickConsumer);
        }

    }

}
