package ros.integrate.pkg.xml.intention;

import com.google.common.collect.HashMultimap;
import com.intellij.codeInsight.intention.impl.BaseIntentionAction;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.psi.ROSPackage;
import ros.integrate.pkg.xml.DependencyType;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.ROSPackageXml.Contributor;
import ros.integrate.pkg.xml.ROSPackageXml.URLType;

import java.util.*;

public class ReformatPackageXmlFix extends BaseIntentionAction implements LocalQuickFix {
    @NotNull
    private final ROSPackageXml pkgXml;
    private final int targetFormat;

    public ReformatPackageXmlFix(@NotNull ROSPackageXml pkgXml, boolean updateFormat) {
        this.pkgXml = pkgXml;
        targetFormat = updateFormat ? ROSPackageXml.getLatestFormat() : pkgXml.getFormat();
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getFamilyName() {
        return "ROS XML";
    }

    @Nls(capitalization = Nls.Capitalization.Sentence)
    @NotNull
    @Override
    public String getName() {
        return "Reformat manifest";
    }

    @NotNull
    @Override
    public String getText() {
        return getName();
    }

    @Override
    public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
        reformatManifest();
    }

    @Override
    public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
        return PackageXmlUtil.getWrapper(file) != null;
    }

    @Override
    public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
        reformatManifest();
    }

    /**
     * @apiNote while this does not
     */
    private void reformatManifest() {
        // step 1: get all data in pkg xml
        Optional<String> name = Optional.ofNullable(pkgXml.getPkgName()),
                version = Optional.ofNullable(pkgXml.getVersion()),
                description = Optional.ofNullable(pkgXml.getDescription());
        List<Contributor> maintainers = pkgXml.getMaintainers(),
                authors = pkgXml.getAuthors();
        List<String> licenses = pkgXml.getLicences();
        List<Pair<String, URLType>> urls = pkgXml.getURLs();
        List<Pair<DependencyType, ROSPackage>> dependencies = pkgXml.getDependenciesTyped();
        // TODO: 2/29/2020 Add groups, export

        Collections.reverse(authors);
        Collections.reverse(urls);
        Collections.reverse(licenses);
        Collections.reverse(maintainers);

        // step 2: group dependencies based on format. drop those that cannot fit.
        updateDependencies(dependencies);

        // step 3: add back all info, ordered.
        if (pkgXml.getRawXml().getRootTag() != null) {
            pkgXml.getRawXml().getRootTag().delete();
        }
        pkgXml.setFormat(targetFormat);
        // export
        // groups
        dependencies.forEach(pair -> pkgXml.addDependency(pair.first, pair.second));
        authors.forEach(author -> pkgXml.addAuthor(author.getName(), author.getEmail().isEmpty() ? null :
                author.getEmail()));
        urls.forEach(url -> pkgXml.addURL(url.first, url.second));
        licenses.forEach(pkgXml::addLicence);
        maintainers.forEach(maintainer -> pkgXml.addMaintainer(maintainer.getName(), maintainer.getEmail()));
        description.ifPresent(pkgXml::setDescription);
        version.ifPresent(pkgXml::setVersion);
        name.ifPresent(pkgXml::setPkgName);
    }

    private void updateDependencies(@NotNull List<Pair<DependencyType, ROSPackage>> dependencies) {
        // get actual dependency types
        HashMultimap<ROSPackage, DependencyType> depTypesForPackage = HashMultimap.create();
        dependencies.forEach(pair -> depTypesForPackage.putAll(pair.second,
                Arrays.asList(pair.first.getCoveredDependencies())));

        // reform dependency types to actual dependencies.
        // Latter dependencies are grouped ones and should be prioritised.
        dependencies.clear();
        DependencyType[] values = DependencyType.values();
        for (ROSPackage pkg : depTypesForPackage.keySet()) {
            if (pkg == ROSPackage.ORPHAN) {
                continue;
            }
            Set<DependencyType> dependencyTypes = depTypesForPackage.get(pkg);
            dependencyTypes.add(null); // used so that if all elements are removed, the key is not.
            for (int i = values.length - 1; i >= 0; i--) {
                DependencyType dep = values[i];
                if (!dep.relevant(targetFormat)) {
                    continue;
                }
                List<DependencyType> coveredDependencyTypes = Arrays.asList(dep.getCoveredDependencies());
                if (dependencyTypes.containsAll(coveredDependencyTypes)) {
                    dependencies.add(new Pair<>(dep, pkg));
                    dependencyTypes.removeAll(coveredDependencyTypes);
                }
            }
        }

        dependencies.sort((o1, o2) -> {
            int ret = DependencyType.compare(o1.first, o2.first);
            return ret != 0 ? -ret : -o1.second.compareTo(o2.second);
        });
    }
}
