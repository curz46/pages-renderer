package me.dylancurzon.dontdie.util;

import java.util.Optional;

public class Cached<T> {

    private T object;

    public Cached() {}

    public Cached(T object) {
        this.object = object;
    }

    public Optional<T> get() {
        return Optional.ofNullable(object);
    }

    public void set(T object) {
        this.object = object;
    }

    public void clear() {
        object = null;
    }

}
