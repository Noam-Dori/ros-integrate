package ros.integrate.pkg;

import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

public class SetDifference<T> {
    @NotNull
    private final Set<T> onlyOnRight, onlyOnLeft;

    private SetDifference(@NotNull Set<T> onlyOnRight, @NotNull Set<T> onlyOnLeft) {
        this.onlyOnRight = onlyOnRight;
        this.onlyOnLeft = onlyOnLeft;
    }

    @NotNull
    public static <S> SetDifference<S> difference(Set<S> left, Set<S> right) {
        Set<S> leftDiff = new HashSet<>(left), rightDiff = new HashSet<>(right);
        leftDiff.removeAll(rightDiff);
        rightDiff.removeAll(leftDiff);
        return new SetDifference<>(rightDiff, leftDiff);
    }

    public boolean areEqual() {
        return onlyOnLeft.isEmpty() && onlyOnRight.isEmpty();
    }

    public Set<T> entriesOnlyOnLeft() {
        return onlyOnLeft;
    }

    public Set<T> entriesOnlyOnRight() {
        return onlyOnRight;
    }


}
