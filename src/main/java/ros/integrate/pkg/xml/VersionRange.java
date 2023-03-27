package ros.integrate.pkg.xml;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Objects;

/**
 * describes a range of versions and provides a bunch of methods to build and manipulate version ranges
 * @author Noam Dori
 */
public class VersionRange {
    public static final String VERSION_REGEX = "(0|[1-9][0-9]*)(\\.(0|[1-9][0-9]*)){2}";

    /**
     * a utility class used to create version ranges.
     */
    public static class Builder {
        @NotNull
        private final VersionRange ret;

        /**
         * construct a new builder. It starts with the default version range, {@link VersionRange#any()}
         */
        public Builder() {
            ret = any();
        }

        /**
         * copy constructor of another builder. It simply copies over the properties of the version range
         * @param other the builder to copy
         */
        public Builder(@NotNull Builder other) {
            ret = any();
            ret.min = other.ret.min;
            ret.max = other.ret.max;
            ret.strictMax = other.ret.strictMax;
            ret.strictMin = other.ret.strictMin;
        }

        /**
         * set the version range to fit exactly one version. Nothing else
         * @param version the version string to use
         * @return the result version range. No point to continue building from here
         */
        public VersionRange exactVersion(String version) {
            ret.max = ret.min = version;
            ret.strictMax = ret.strictMin = false;
            return ret;
        }

        /**
         * sets the maximum version allowed in the range
         * @param version the version string to use as max
         * @param strict whether this specific version is allowed.
         *               If true, the restriction is strong and the version is NOT allowed.
         *               If false, the restriction is weak and the version specified IS allowed
         * @return this builder with the modification applied
         */
        @NotNull
        public Builder max(String version, boolean strict) {
            ret.max = version;
            ret.strictMax = strict;
            return this;
        }

        /**
         * sets the minimum version allowed in the range
         * @param version the version string to use as min
         * @param strict whether this specific version is allowed.
         *               If true, the restriction is strong and the version is NOT allowed.
         *               If false, the restriction is weak and the version specified IS allowed
         * @return this builder with the modification applied
         */
        @NotNull
        public Builder min(String version, boolean strict) {
            ret.min = version;
            ret.strictMin = strict;
            return this;
        }

        /**
         * finishes building and constructs the version range
         * @return the result version range
         */
        @NotNull
        public VersionRange build() {
            return ret;
        }
    }

    @Nullable
    private String min = null, max = null;
    private boolean strictMin = false, strictMax = false;

    /**
     * disables outside construction. Use either the builder or the static methods to create a range
     */
    private VersionRange() {}

    /**
     * constructs a version range that only allows ONE specific version
     * @param version version the version string to use
     * @return the result version range
     */
    public static VersionRange exactVersion(String version) {
        return new Builder().exactVersion(version);
    }

    /**
     * the "default" constructor for version ranges. This range allows ANY version to be used
     * @return the result version range
     */
    @NotNull
    @Contract(value = " -> new", pure = true)
    public static VersionRange any() {
        return new VersionRange();
    }

    /**
     * checks the validity of the range. IT is possible that the range provided either uses illegal version strings
     * or the max is strictly smaller than the min
     * @return true if one of the following is true:
     * <ol>
     *     <li>the min describes an invalid version (null is valid)</li>
     *     <li>the max describes an invalid version (null is valid)</li>
     *     <li>max is strictly smaller than min (in version string comparison)</li>
     *     <li>max and min are equal, but one of the two is a strict restriction (like version_lt)</li>
     * </ol>
     * otherwise, false.
     */
    public boolean isNotValid() {
        return (min != null && !min.matches(VERSION_REGEX)) || (max != null && !max.matches(VERSION_REGEX))
                || (min != null && max != null && (compareVersions(min, max) > 0 ||
                (min.equals(max) && (strictMax || strictMin))));
    }

    /**
     * checks if the version specified is within this version range
     * @param version the version string to check
     * @return true if version is in this range, false otherwise.
     */
    @SuppressWarnings({"unused", "RedundantSuppression"})
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
            if (maxParts[i] > minParts[i]) {
                return true;
            }
            if (maxParts[i] < minParts[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * construct a new version that is the intersection of this range and another one
     * @param other the version range to intersect with
     * @return a new version range that is the intersection of the two input ones. This range answers this property:
     * for any version v, given version ranges R1,R2:<br/>
     * if <code>R1.intersect(R2).contains(v)</code>, then
     * <code>R1.contains(v) && R2.contains(v)</code>
     */
    @Nullable
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

    /**
     * @return the maximum version allowed in this range.
     * This can be inclusive or not inclusive depending on {@link VersionRange#isStrictMax()}
     * If this returns null, there is no max version restriction
     */
    @Nullable
    public String getMax() {
        return max;
    }

    /**
     * @return the minimum version allowed in this range.
     * This can be inclusive or not inclusive depending on {@link VersionRange#isStrictMin()}
     * If this returns null, there is no min version restriction
     */
    @Nullable
    public String getMin() {
        return min;
    }

    /**
     * @return true if the max version is not allowed in the version range, false otherwise
     */
    public boolean isStrictMax() {
        return strictMax;
    }

    /**
     * @return true if the min version is not allowed in the version range, false otherwise
     */
    public boolean isStrictMin() {
        return strictMin;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VersionRange range)) return false;
        return isStrictMin() == range.isStrictMin() &&
                isStrictMax() == range.isStrictMax() &&
                Objects.equals(getMin(), range.getMin()) &&
                Objects.equals(getMax(), range.getMax());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMin(), getMax(), isStrictMin(), isStrictMax());
    }

    @Override
    public String toString() {
        return (strictMin ? "(" : "[") +
                (min == null ? "INF" : min) + "," +
                (max == null ? "INF" : max) +
                (strictMax ? ")" : "]");
    }
}
