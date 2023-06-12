package ar.edu.itba.paw.util;

import java.util.Objects;

public final class MyBoolean {
    private final Boolean bool;

    public MyBoolean() {
        bool = false;
    }

    public MyBoolean(Boolean bool) {
        this.bool = bool;
    }

    public Boolean get() {
        return bool;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MyBoolean myBoolean = (MyBoolean) o;
        return Objects.equals(bool, myBoolean.bool);
    }

    @Override
    public int hashCode() {
        return Objects.hash(bool);
    }

    @Override
    public String toString() {
        return bool.toString();
    }
}
