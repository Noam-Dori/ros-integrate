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
import ros.integrate.pkg.xml.ROSPackageXml.GroupLink;
import ros.integrate.pkg.xml.ROSPackageXml.URLType;
import ros.integrate.pkg.xml.condition.psi.ROSCondition;

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
                description = Optional.ofNullable(pkgXml.getDescription());
        Optional<ROSPackageXml.Version> version = Optional.ofNullable(pkgXml.getVersion());
        List<Contributor> maintainers = pkgXml.getMaintainers(),
                authors = pkgXml.getAuthors();
        List<ROSPackageXml.License> licenses = pkgXml.getLicences();
        List<Pair<String, URLType>> urls = pkgXml.getURLs();
        List<Dependency> dependencies = pkgXml.getDependencies(null);
        List<GroupLink> groupDependencies = pkgXml.getGroupDepends(), groups = pkgXml.getGroups();
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
        licenses.forEach(license -> pkgXml.addLicence(license.getValue(), license.getFile()));
        urls.forEach(url -> pkgXml.addURL(url.first, url.second));
        authors.forEach(author -> pkgXml.addAuthor(author.getName(), author.getEmail().isEmpty() ? null :
                author.getEmail()));
        dependencies.forEach(dependency -> pkgXml.addDependency(dependency.getType(), dependency.getPackage(),
                dependency.getVersionRange(), dependency.getCondition(), false));
        groupDependencies.forEach(group -> pkgXml.addGroupDependency(group.getGroup(), group.getCondition()));
        groups.forEach(group -> pkgXml.addGroup(group.getGroup(), group.getCondition()));
        export.map(ExportTag::getRawTag).ifPresent(pkgXml::setExport);
    }

    private void updateDependencies(@NotNull List<Dependency> dependencies) {
        // get actual dependency types
        HashMultimap<Pair<ROSPackage, ROSCondition>, DependencyType> depTypesForPackageCond = HashMultimap.create();
        Map<Pair<ROSPackage, ROSCondition>, VersionRange> versionRangeForPackageCond = new HashMap<>();
        dependencies.forEach(dependency -> {
            Pair<ROSPackage, ROSCondition> key = Pair.create(dependency.getPackage(), dependency.getCondition());
            depTypesForPackageCond.putAll(key, Arrays.asList(dependency.getType().getCoveredDependencies()));
            versionRangeForPackageCond.put(key,
                    dependency.getVersionRange().intersect(versionRangeForPackageCond.get(key)));
        });

        // reform dependency types to actual dependencies.
        // Latter dependencies are grouped ones and should be prioritised.
        dependencies.clear();
        DependencyType[] values = DependencyType.values();
        for (Pair<ROSPackage, ROSCondition> pkgCond : depTypesForPackageCond.keySet()) {
            VersionRange versionRange = versionRangeForPackageCond.get(pkgCond);
            if (pkgCond.first == ROSPackage.ORPHAN || versionRange == null) {
                continue;
            }
            Set<DependencyType> dependencyTypes = depTypesForPackageCond.get(pkgCond);
            dependencyTypes.add(null); // used so that if all elements are removed, the key is not.
            for (int i = values.length - 1; i >= 0; i--) {
                DependencyType dep = values[i];
                if (!dep.relevant(targetFormat)) {
                    continue;
                }
                List<DependencyType> coveredDependencyTypes = Arrays.asList(dep.getCoveredDependencies());
                if (dependencyTypes.containsAll(coveredDependencyTypes)) {
                    dependencies.add(new Dependency(dep, pkgCond.first, versionRange, pkgCond.second));
                    dependencyTypes.removeAll(coveredDependencyTypes);
                }
            }
        }
    }
}
