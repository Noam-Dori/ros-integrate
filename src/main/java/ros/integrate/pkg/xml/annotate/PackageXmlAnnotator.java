package ros.integrate.pkg.xml.annotate;

import com.intellij.codeInsight.daemon.impl.analysis.RemoveTagIntentionFix;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlFile;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.intention.FixFormatQuickFix;
import ros.integrate.pkg.xml.intention.FixURLQuickFix;
import ros.integrate.pkg.xml.intention.MoveToExportFix;
import ros.integrate.pkg.xml.intention.RemoveURLQuickFix;

import java.util.Arrays;
import java.util.List;

/**
 * enforces rules for package.xml files using annotations and intentions.
 * The rules are specified by the manifest REPs:
 * <ul>
 *     <li>Format 1 (outdated): https://www.ros.org/reps/rep-0127.html</li>
 *     <li>Format 2 (outdated): https://www.ros.org/reps/rep-0140.html</li>
 *     <li>Format 3 (current): https://www.ros.org/reps/rep-0149.html</li>
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
 * @author Noam Dori
 */
public class PackageXmlAnnotator implements Annotator {
    private final List<String> LEVEL_1_TAGS = Arrays.asList("name", "version", "description", "license", "maintainer",
            "author", "url", "export", "group_depend", "member_of_group");

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof XmlFile) {
            ROSPackageXml pkgXml = PackageXmlUtil.getWrapper((XmlFile) element);
            if (pkgXml == null) {
                return;
            }

            if (pkgXml.getFormat() == 0) {
                holder.newAnnotation(HighlightSeverity.ERROR, "Invalid package format")
                        .range(pkgXml.getFormatTextRange())
                        .withFix(new FixFormatQuickFix(pkgXml))
                        .create();
            }

            PackageIdAnnotator idAnn = new PackageIdAnnotator(pkgXml, holder);
            idAnn.annNameNotLowercase();
            idAnn.annPkgNameMatch();
            idAnn.annBadVersion();
            idAnn.annBadVersionCompatibility();
            idAnn.annCompatibilityHigherThanVersion();
            idAnn.annTooManyNames();
            idAnn.annTooManyVersions();
            idAnn.annTooManyDescriptions();

            PackageLicenseAnnotator licAnn = new PackageLicenseAnnotator(pkgXml, holder);
            licAnn.annBadLicenses();
            licAnn.annTodoLicense();

            PackageContribAnnotator contribAnn = new PackageContribAnnotator(pkgXml, holder);
            contribAnn.annBadMaintainer();
            contribAnn.annBadAuthor();

            List<Pair<String, ROSPackageXml.URLType>> urlList = pkgXml.getURLs();
            for (int i = 0; i < urlList.size(); i++) {
                if (urlList.get(i).first.isEmpty()) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Empty URL")
                            .range(pkgXml.getURLTextRanges().get(i))
                            .withFix(new RemoveURLQuickFix(pkgXml, i))
                            .create();
                }
                if (urlList.get(i).second == null) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "URL type is unknown")
                            .range(pkgXml.getURLTextRanges().get(i))
                            .withFix(new FixURLQuickFix(pkgXml, i))
                            .create();
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
            expAnn.annIgnoredCondition();

            PackageGroupAnnotator grpAnn = new PackageGroupAnnotator(pkgXml, holder);
            grpAnn.annTooLowFormat();
            grpAnn.annIgnoredCondition();
            grpAnn.annEmptyGroup();
            grpAnn.annConflictingGroups();

            for (XmlTag lvl1Tag : pkgXml.getSubTags()) {
                if (!LEVEL_1_TAGS.contains(lvl1Tag.getName()) && !PackageXmlUtil.isDependencyTag(lvl1Tag)) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Unsupported tag outside export")
                            .range(lvl1Tag)
                            .withFix(new MoveToExportFix(lvl1Tag, pkgXml))
                            .withFix(new RemoveTagIntentionFix(lvl1Tag.getName(), lvl1Tag))
                            .create();
                }
            }
        }
    }
}
