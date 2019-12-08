package ros.integrate.pkg.xml.intention;

import com.intellij.openapi.util.TextRange;
import com.intellij.util.TripleFunction;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.ROSPackageXml.Contributor;

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
        String name = contrib.getName().isEmpty() ? "user" : contrib.getName() ;
        String email = contrib.getEmail().isEmpty() ? "user@todo.todo" : contrib.getEmail();

        return fixContrib.fun(pkgXml, id, new Contributor(name, email));
    }

    void remove(ROSPackageXml pkgXml, int id) {
        removeContrib.accept(pkgXml, id);
    }
}
