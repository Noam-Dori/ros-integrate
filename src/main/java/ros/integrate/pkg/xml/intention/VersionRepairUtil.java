package ros.integrate.pkg.xml.intention;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class VersionRepairUtil {
    @NotNull
    static String repairVersion(@Nullable String brokenVersion) {
        if (brokenVersion == null) {
            return "1.0.0";
        }
        String[] sections = brokenVersion.split("\\.");
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 3; i++) {
            builder.append(getNumber(sections, i));
        }
        return builder.toString();
    }

    @NotNull
    private static String getNumber(@NotNull String[] sections, int idx) {
        String num;
        if (sections.length <= idx || !sections[idx].matches("0|[1-9][0-9]*")) {
            if (idx == 0) {
                num = "1";
            } else {
                num = "0";
            }
        } else {
            num = Integer.valueOf(sections[idx]).toString();
        }
        return num + (idx == 2 ? "" : ".");
    }
}
