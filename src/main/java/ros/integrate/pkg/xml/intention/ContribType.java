package ros.integrate.pkg.xml.intention;

import com.intellij.openapi.util.TextRange;
import com.intellij.util.TripleFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.ROSPackageXml.Contributor;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

/**
 * An adapter class for the two contributor types: author and maintainer.
 * Used by classes that don't distinguish between authors and maintainers (without resorting to switches)
 */
public enum ContribType {
    AUTHOR((pkgXml, id) -> pkgXml.getAuthors().get(id), (pkgXml, id) -> pkgXml.getAuthorTextRanges().get(id),
            ROSPackageXml::setAuthor, ROSPackageXml::removeAuthor),
    MAINTAINER((pkgXml, id) -> pkgXml.getMaintainers().get(id), (pkgXml, id) -> pkgXml.getMaintainerTextRanges().get(id),
            ROSPackageXml::setMaintainer, ROSPackageXml::removeMaintainer);

    @NotNull
    private final BiFunction<ROSPackageXml, Integer, Contributor> getContrib;
    @NotNull
    private final BiFunction<ROSPackageXml, Integer, TextRange> getContribTr;
    @NotNull
    private final TripleFunction<ROSPackageXml, Integer, Contributor, Boolean> fixContrib;
    @NotNull
    private final BiConsumer<ROSPackageXml, Integer> removeContrib;

    @Contract(pure = true)
    ContribType(@NotNull BiFunction<ROSPackageXml, Integer, Contributor> getContrib,
                @NotNull BiFunction<ROSPackageXml, Integer, TextRange> getContribTr,
                @NotNull TripleFunction<ROSPackageXml, Integer, Contributor, Boolean> fixContrib,
                @NotNull BiConsumer<ROSPackageXml, Integer> removeContrib) {
        this.getContrib = getContrib;
        this.fixContrib = fixContrib;
        this.removeContrib = removeContrib;
        this.getContribTr = getContribTr;
    }

    Contributor get(ROSPackageXml pkgXml, int id) {
        return getContrib.apply(pkgXml, id);
    }

    TextRange getTr(ROSPackageXml pkgXml, int id) {
        return getContribTr.apply(pkgXml, id);
    }

    boolean fix(ROSPackageXml pkgXml, int id) {
        Contributor contrib = get(pkgXml, id);
        String name = contrib.getName().isEmpty() ? "user" : contrib.getName();
        String email = repairEmail(contrib.getEmail());

        return fixContrib.fun(pkgXml, id, new Contributor(name, email));
    }
    @NotNull
    private String repairEmail(@NotNull String email) {
        if (email.isEmpty()) {
            return this == AUTHOR ? "" : "user@todo.todo";
        }
        // a valid email is: a series of "tokens" split by dots and ONE @.
        // the last token is special, and must be alphabetic, while the rest may include -+_% and numbers
        // first we repair the bad characters away if any exist.
        // All invalid characters are converted to either, - or _, whichever is found first.
        int hyphenIndex = email.indexOf('-'), underscoreIndex = email.indexOf('_');
        char fixChar = hyphenIndex == -1 ? '_' :
                underscoreIndex == -1 ? '-' : email.charAt(Math.min(underscoreIndex, hyphenIndex));
        email = email.replaceAll("[^-A-Za-z0-9_%+@.]", String.valueOf(fixChar));

        // we then split by @, then by "." to get all non-separator tokens
        String[] tokens = Arrays.stream(email.split("[@.]"))
                .map(token -> token.isEmpty() ? "todo" : token)
                .toArray(String[]::new);
        // we also remember where the first @ is since we will use it later on.
        int atIndex = Arrays.asList(email.split("[^@.]+")).indexOf("@");
        if (atIndex == -1 || atIndex > tokens.length - 3) {
            atIndex = tokens.length - 3;
        }

        String repaired;
        switch (tokens.length) {
            case 0:
                repaired = "user@todo.todo";
                break;
            case 1: {
                repaired = tokens[0] + "@todo.todo";
                break;
            }
            case 2: {
                repaired = tokens[0] + "@" + tokens[1] + ".todo";
                break;
            }
            case 3: {
                repaired = tokens[0] + "@" + tokens[1] + "." + tokens[2];
                break;
            }
            default: {
                repaired = String.join("#", tokens);
            }
        }
        for (int i = 0; i < tokens.length - 1; i++) {
            repaired = repaired.replaceFirst("#",i == atIndex ? "@" : ".");
        }
        if (repaired.matches(".*\\..$")) {
            repaired += repaired.charAt(repaired.length() - 1);
        }

        // last, we do the special repair for the last token, and delete any non alphanumerical character from it.
        repaired = repaired.replaceAll("[^a-zA-Z.](?=[^.]*$)","");
        return repaired;
    }

    void remove(ROSPackageXml pkgXml, int id) {
        removeContrib.accept(pkgXml, id);
    }
}
