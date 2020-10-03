package ros.integrate.pkg;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * represents a difference between two sets. Based on Google Guava's MapDifference
 * @param <T> the contained element in the sets
 * @author Noam Dori
 */
public class SetDifference<T> {
    @NotNull
    private final Set<T> onlyOnRight, onlyOnLeft;

    private SetDifference(@NotNull Set<T> onlyOnRight, @NotNull Set<T> onlyOnLeft) {
        this.onlyOnRight = onlyOnRight;
        this.onlyOnLeft = onlyOnLeft;
    }

    /**
     * find the difference between two sets
     * @param left a set to compare
     * @param right another set to compare
     * @param <S> the type of element held in the sets
     * @return the difference between the two sets
     */
    @NotNull
    public static <S> SetDifference<S> difference(Set<S> left, Set<S> right) {
        Set<S> leftDiff = new HashSet<>(left), rightDiff = new HashSet<>(right);
        leftDiff.removeAll(rightDiff);
        rightDiff.removeAll(leftDiff);
        return new SetDifference<>(rightDiff, leftDiff);
    }

    /**
     * @return true if the two input sets had the same items, false otherwise
     */
    public boolean areEqual() {
        return onlyOnLeft.isEmpty() && onlyOnRight.isEmpty();
    }

    /**
     * @return the set of all items that were in "left" but not in "right"
     */
    public Set<T> entriesOnlyOnLeft() {
        return onlyOnLeft;
    }

    /**
     * @return the set of all items that were in "right" but not in "left"
     */
    public Set<T> entriesOnlyOnRight() {
        return onlyOnRight;
    }


}
