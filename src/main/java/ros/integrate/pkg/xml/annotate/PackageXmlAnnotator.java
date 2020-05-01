package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.intention.FixFormatQuickFix;
import ros.integrate.pkg.xml.intention.FixURLQuickFix;
import ros.integrate.pkg.xml.intention.RemoveURLQuickFix;

import java.util.List;

/**
 * enforces rules for package.xml files as specified by the manifest REPs:
 * <ul>
 *     <li>Format 1 (outdated): https://www.ros.org/reps/rep-0127.html</li>
 *     <li>Format 2 (current): https://www.ros.org/reps/rep-0140.html</li>
 *     <li>Format 3 (future): https://www.ros.org/reps/rep-0149.html</li>
 * </ul>
 * <b>Notes about implementation:</b>
 * <ul>
 *     <li>The plugin will consider package names NOT to be the one specified in the XML file,
 *     but rather the folder name. while REP 140 says otherwise, the many things that break in ROS when
 *     using different names for the package and the containing folder is simply not worth the effort to implement.</li>
 *     <li>package.xml files may be excluded from indexing, disabling ROS packages.</li>
 *     <li>The plugin considers the file a ROS package XML iff the root tag either does not exist,
 *     or its name is the start of {@code package}</li>
 * </ul>
 */
public class PackageXmlAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof XmlFile) {
            ROSPackageXml pkgXml = PackageXmlUtil.getWrapper((XmlFile) element);
            if (pkgXml == null) {
                return;
            }

            if (pkgXml.getFormat() == 0) {
                Annotation ann = holder.createErrorAnnotation(pkgXml.getFormatTextRange(),
                        "Invalid package format");
                ann.registerFix(new FixFormatQuickFix(pkgXml));
            }

            PackageIdAnnotator idAnn = new PackageIdAnnotator(pkgXml, holder);
            idAnn.annNoName();
            idAnn.annNameNotLowercase();
            idAnn.annPkgNameMatch();
            idAnn.annNoVersion();
            idAnn.annBadVersion();
            idAnn.annBadVersionCompatibility();
            idAnn.annCompatibilityHigherThanVersion();
            idAnn.annNoDescription();
            idAnn.annTooManyNames();
            idAnn.annTooManyVersions();
            idAnn.annTooManyDescriptions();

            PackageLicenseAnnotator licAnn = new PackageLicenseAnnotator(pkgXml, holder);
            licAnn.annNoLicenses();
            licAnn.annBadLicenses();
            licAnn.annTodoLicense();

            PackageContribAnnotator contribAnn = new PackageContribAnnotator(pkgXml, holder);
            contribAnn.annNoMaintainers();
            contribAnn.annBadMaintainer();
            contribAnn.annBadAuthor();

            List<Pair<String,ROSPackageXml.URLType>> urlList = pkgXml.getURLs();
            for (int i = 0; i < urlList.size(); i++) {
                if (urlList.get(i).first.isEmpty()) {
                    Annotation ann = holder.createErrorAnnotation(pkgXml.getURLTextRanges().get(i),
                            "Empty URL");
                    ann.registerFix(new RemoveURLQuickFix(pkgXml, i));
                }
                if (urlList.get(i).second == null) {
                    Annotation ann = holder.createErrorAnnotation(pkgXml.getURLTextRanges().get(i),
                            "URL type is unknown");
                    ann.registerFix(new FixURLQuickFix(pkgXml, i));
                }
            }

            PackageDependencyAnnotator depAnn = new PackageDependencyAnnotator(pkgXml, holder);
            depAnn.annSelfDependency();
            depAnn.annInvalidDependencyName();
            depAnn.annEmptyDependency();
            depAnn.annConflictingDependencies();
            depAnn.annDependencyNotFound();
            depAnn.annInvalidDependencyVersionAttr();
            depAnn.annConflictingVersionAttr();
            depAnn.annIgnoredCondition();

            PackageExportAnnotator expAnn = new PackageExportAnnotator(pkgXml, holder);
            expAnn.annEmptyMessageGenerator();
            expAnn.annEmptyBuildType();
            expAnn.annNonEmptyArchitectureIndependentTags();
            expAnn.annNonEmptyMetapackageTag();
            expAnn.annMultipleMessageGenerators();
            expAnn.annMultipleBuildTypes();
            expAnn.annMultipleArchitectureIndependentTags();
            expAnn.annMultipleMetapackageTags();
            expAnn.annMultipleDeprecated();
        }
    }
}
