package ros.integrate.pkg.xml;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Objects;

public class VersionRange {
    public static final String VERSION_REGEX = "(0|[1-9][0-9]*)(\\.(0|[1-9][0-9]*)){2}";

    public static class Builder {
        @NotNull
        private final VersionRange ret;

        public Builder() {
            ret = any();
        }

        public VersionRange exactVersion(String version) {
            ret.max = ret.min = version;
            ret.strictMax = ret.strictMin = false;
            return ret;
        }

        public void max(String version, boolean strict) {
            ret.max = version;
            ret.strictMax = strict;
        }

        public void min(String version, boolean strict) {
            ret.min = version;
            ret.strictMin = strict;
        }

        public VersionRange build() {
            return ret;
        }
    }

    @Nullable
    private String min = null, max = null;
    private boolean strictMin = false, strictMax = false;

    private VersionRange() {}

    public static VersionRange exactVersion(String version) {
        return new Builder().exactVersion(version);
    }

    @NotNull
    @Contract(value = " -> new", pure = true)
    public static VersionRange any() {
        return new VersionRange();
    }

    public boolean isNotValid() {
        return (min != null && !min.matches(VERSION_REGEX)) || (max != null && !max.matches(VERSION_REGEX))
                || (min != null && max != null && (compareVersions(min, max) > 0 ||
                (min.equals(max) && (strictMax || strictMin))));
    }

    public boolean contains(@NotNull String version) {
        if (isNotValid() || !version.matches(VERSION_REGEX)) {
            return false;
        }
        if (min != null && !runCheck(min, version, strictMin)) {
            return false;
        }
        return max == null || runCheck(version, max, strictMax);
    }

    private boolean runCheck(String shouldMin, String shouldMax, boolean strict) {
        if (strict && shouldMin.equals(shouldMax)) {
            return false;
        }
        Integer[] minParts = Arrays.stream(shouldMin.split("\\.")).map(Integer::valueOf).toArray(Integer[]::new),
                maxParts = Arrays.stream(shouldMax.split("\\.")).map(Integer::valueOf).toArray(Integer[]::new);
        for (int i = 0; i < maxParts.length; i++) {
            if (maxParts[i] < minParts[i]) {
                return false;
            }
        }
        return true;
    }

    public VersionRange intersect(VersionRange other) {
        if (other == null) {
            other = any();
        }
        if (isNotValid() || other.isNotValid()) {
            return null;
        }
        String newMax = compareVersions(this.max, other.max, this.strictMax, other.strictMax, true) < 0
                ? this.max : other.max;
        String newMin = compareVersions(this.min, other.min, this.strictMin, other.strictMin, false) > 0
                ? this.min : other.min;
        boolean strictMax = compareVersions(this.max, other.max, this.strictMax, other.strictMax, true) < 0
                ? this.strictMax : other.strictMax;
        boolean strictMin = compareVersions(this.min, other.min, this.strictMin, other.strictMin, false) > 0
                ? this.strictMin : other.strictMin;
        if (newMax != null && newMin != null && (compareVersions(newMin,newMax) > 0 ||
                (newMin.equals(newMax) && (strictMax || strictMin)))) {
            return null;
        }
        VersionRange ret = any();
        ret.strictMin = strictMin;
        ret.strictMax = strictMax;
        ret.min = newMin;
        ret.max = newMax;
        return ret;
    }

    private static int compareVersions(@Nullable String s1, @Nullable String s2) {
        return compareVersions(s1, s2, true, true, true);
    }

    private static int compareVersions(@Nullable String s1, @Nullable String s2, boolean s1Strict, boolean s2Strict,
                                       boolean nullIsPositive) {
        if (s1 == null && s2 == null) {
            return 0;
        }
        if (s1 == null) {
            return nullIsPositive ? 1 : -1;
        }
        if (s2 == null) {
            return nullIsPositive ? -1 : 1;
        }
        Integer[] s1Parts = Arrays.stream(s1.split("\\.")).map(Integer::valueOf).toArray(Integer[]::new),
                s2Parts = Arrays.stream(s2.split("\\.")).map(Integer::valueOf).toArray(Integer[]::new);
        for (int i = 0; i < s1Parts.length; i++) {
            if (!s1Parts[i].equals(s2Parts[i])) {
                return Integer.compare(s1Parts[i], s2Parts[i]);
            }
        }
        return nullIsPositive ? Boolean.compare(s2Strict, s1Strict) : Boolean.compare(s1Strict, s2Strict);
    }

    @Nullable
    public String getMax() {
        return max;
    }

    @Nullable
    public String getMin() {
        return min;
    }

    public boolean isStrictMax() {
        return strictMax;
    }

    public boolean isStrictMin() {
        return strictMin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VersionRange)) return false;
        VersionRange range = (VersionRange) o;
        return isStrictMin() == range.isStrictMin() &&
                isStrictMax() == range.isStrictMax() &&
                Objects.equals(getMin(), range.getMin()) &&
                Objects.equals(getMax(), range.getMax());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMin(), getMax(), isStrictMin(), isStrictMax());
    }
}
