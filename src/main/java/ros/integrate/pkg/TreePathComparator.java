package ros.integrate.pkg;

import org.jetbrains.annotations.NotNull;

import java.util.Comparator;

public class TreePathComparator implements Comparator<String> {

    @Override
    public int compare(@NotNull String path1, @NotNull String path2) {
        String[] components1 = path1.split("[/\\\\]"),
                components2 = path2.split("[/\\\\]");
        for (int i = 0; i < Math.min(components1.length, components2.length); i++) {
            int checkComponents = components1[i].compareTo(components2[i]);
            if (checkComponents != 0) {
                return checkComponents;
            }
        }
        return Integer.compare(components1.length, components2.length);
    }
}
