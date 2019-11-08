package ros.integrate.settings;

import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

class PathListUtil {
    static List<String> parsePathList(@NotNull String rawPathList) {
        return Arrays.stream(rawPathList.split("(?<!(:|^)[A-Z]):"))
                .filter(item -> !item.equals(""))
                .collect(Collectors.toList());
    }

    static String serializePathList(@NotNull List<String> pathList) {
        return pathList.stream().filter(path -> !path.isEmpty()).collect(Collectors.joining(":"));
    }
}
