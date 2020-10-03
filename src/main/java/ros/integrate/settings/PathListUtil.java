package ros.integrate.settings;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * a collection of utility functions for handling path list de/serialization
 * @author Noam Dori
 */
class PathListUtil {
    /**
     * deserializes a path list string using : as delimiter.
     * @param rawPathList the string containing all paths to be split up
     * @return a list of paths to all relevant files. This deserializer respects Windows drives as part of the path
     * (for example C:)
     */
    static List<String> parsePathList(@NotNull String rawPathList) {
        return Arrays.stream(rawPathList.split("(?<!(:|^)[A-Z]):(?!/{2,})"))
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * deserializes a path list string using : as delimiter.
     * @param rawPathList the string containing all paths to be split up
     * @param delimiter a custom delimiter to use for splitting the paths
     * @param hasFilePaths whether or not the paths described are not file paths, but rather something else like URLs
     *                     (or not even a path)
     * @return a list of paths to all relevant files. This deserializer respects Windows drives as part of the path
     * (for example C:)
     */
    static List<String> parsePathList(@NotNull String rawPathList, char delimiter, boolean hasFilePaths) {
        String splitRegex = hasFilePaths && delimiter == ':' ? "(?<!(:|^)[A-Z]):(?!/{2,})" : String.valueOf(delimiter);
        return Arrays.stream(rawPathList.split(splitRegex))
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    /**
     * serializes a list of paths into one string. This uses the ':' character as delimiter
     * @param pathList the list of paths ot serialize
     * @return a string containing all paths
     */
    static String serializePathList(@NotNull List<String> pathList) {
        return pathList.stream().filter(path -> !path.isEmpty()).collect(Collectors.joining(":"));
    }

    /**
     * serializes a list of paths into one string with a custom delimiter
     * @param pathList the list of paths ot serialize
     * @param delimiter the delimiter to use between the paths
     * @return a string containing all paths
     */
    static String serializePathList(@NotNull List<String> pathList, char delimiter) {
        return pathList.stream().filter(path -> !path.isEmpty()).collect(Collectors.joining(String.valueOf(delimiter)));
    }
}
