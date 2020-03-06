package ros.integrate.settings;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class PathListUtil {
    static List<String> parsePathList(@NotNull String rawPathList) {
        return Arrays.stream(rawPathList.split("(?<!(:|^)[A-Z]):(?!/{2,})"))
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    static List<String> parsePathList(@NotNull String rawPathList, char delimiter, boolean hasFilePaths) {
        String splitRegex = hasFilePaths && delimiter == ':' ? "(?<!(:|^)[A-Z]):(?!/{2,})" : String.valueOf(delimiter);
        return Arrays.stream(rawPathList.split(splitRegex))
                .filter(item -> !item.isEmpty())
                .collect(Collectors.toList());
    }

    static String serializePathList(@NotNull List<String> pathList) {
        return pathList.stream().filter(path -> !path.isEmpty()).collect(Collectors.joining(":"));
    }

    static String serializePathList(@NotNull List<String> pathList, char delimiter) {
        return pathList.stream().filter(path -> !path.isEmpty()).collect(Collectors.joining(String.valueOf(delimiter)));
    }
}
