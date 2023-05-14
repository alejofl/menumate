package ar.edu.itba.paw.util;


import java.io.Serializable;
import java.util.Objects;

public class Triplet<X, Y, Z> implements Serializable {

    private final X x;
    private final Y y;
    private final Z z;

    public Triplet(X x, Y y, Z z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }


    public X getX() {
        return x;
    }

    public Y getY() {
        return y;
    }

    public Z getZ() {
        return z;
    }

    @Override
    public String toString() {
        return "(" + x + ", " + y + ", " + z + ")";
    }

    @Override
    public int hashCode() {
        return (x == null ? 0 : x.hashCode()) * 37 + (y == null ? 0 : y.hashCode()) * 13 + (z == null ? 0 : z.hashCode());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Triplet))
            return false;

        Triplet triplet = (Triplet) o;
        return Objects.equals(x, triplet.x) && Objects.equals(y, triplet.y) && Objects.equals(z, triplet.z);
    }
}