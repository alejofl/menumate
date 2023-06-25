package ar.edu.itba.paw.util;
import java.io.Serializable;
public class AverageCountPair implements Serializable {
    private final float average;
    private final int count;

    public AverageCountPair(float average, int count) {
        this.average = average;
        this.count = count;
    }

    public float getAverage() {
        return average;
    }

    public int getCount() {
        return count;
    }

    @Override
    public String toString() {
        return String.format("Average: %f, Count: %d", average, count);
    }

    @Override
    public int hashCode() {
        return Float.hashCode(average) * 13 + count;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (!(o instanceof Pair))
            return false;

        AverageCountPair pair = (AverageCountPair) o;
        return average == pair.average && count == pair.count;
    }
}