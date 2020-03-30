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
import ros.integrate.pkg.xml.*;
import ros.integrate.pkg.xml.ROSPackageXml.Contributor;
import ros.integrate.pkg.xml.ROSPackageXml.Dependency;
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
        List<Dependency> dependencies = pkgXml.getDependencies(null);
        // TODO: 2/29/2020 Add group
        Optional<ExportTag> export = Optional.ofNullable(pkgXml.getExport());

        // step 2: group dependencies based on format. drop those that cannot fit.
        updateDependencies(dependencies);

        // step 3: add back all info. Ordering is taken care of in ROSPackageXml
        if (pkgXml.getRawXml().getRootTag() != null) {
            pkgXml.getRawXml().getRootTag().delete();
        }
        pkgXml.setFormat(targetFormat);
        name.ifPresent(pkgXml::setPkgName);
        version.ifPresent(pkgXml::setVersion);
        description.ifPresent(pkgXml::setDescription);
        maintainers.forEach(maintainer -> pkgXml.addMaintainer(maintainer.getName(), maintainer.getEmail()));
        licenses.forEach(pkgXml::addLicence);
        urls.forEach(url -> pkgXml.addURL(url.first, url.second));
        authors.forEach(author -> pkgXml.addAuthor(author.getName(), author.getEmail().isEmpty() ? null :
                author.getEmail()));
        dependencies.forEach(dependency -> pkgXml.addDependency(dependency.getType(), dependency.getPackage(),
                dependency.getVersionRange(), false));
        // groups
        export.map(ExportTag::getRawTag).ifPresent(pkgXml::setExport);
    }

    private void updateDependencies(@NotNull List<Dependency> dependencies) {
        // get actual dependency types
        HashMultimap<ROSPackage, DependencyType> depTypesForPackage = HashMultimap.create();
        Map<ROSPackage, VersionRange> versionRangeForPackage = new HashMap<>();
        dependencies.forEach(dependency -> {
            depTypesForPackage.putAll(dependency.getPackage(), Arrays.asList(dependency.getType()
                    .getCoveredDependencies()));
            versionRangeForPackage.put(dependency.getPackage(), dependency.getVersionRange()
                    .intersect(versionRangeForPackage.get(dependency.getPackage())));
        });

        // reform dependency types to actual dependencies.
        // Latter dependencies are grouped ones and should be prioritised.
        dependencies.clear();
        DependencyType[] values = DependencyType.values();
        for (ROSPackage pkg : depTypesForPackage.keySet()) {
            VersionRange versionRange = versionRangeForPackage.get(pkg);
            if (pkg == ROSPackage.ORPHAN || versionRange == null) {
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
                    dependencies.add(new Dependency(dep, pkg, versionRange));
                    dependencyTypes.removeAll(coveredDependencyTypes);
                }
            }
        }
    }
}
