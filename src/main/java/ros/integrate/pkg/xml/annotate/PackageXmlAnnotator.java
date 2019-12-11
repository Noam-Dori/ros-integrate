package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.intention.*;

/**
 * enforces rules for package.xml files as specified by https://www.ros.org/reps/rep-0140.html
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
            idAnn.annPkgNameMatch();
            idAnn.annNoVersion();
            idAnn.annBadVersion();
            idAnn.annNoDescription();
            idAnn.annTooManyNames();
            idAnn.annTooManyVersions();
            idAnn.annTooManyDescriptions();

            if (pkgXml.getLicences().isEmpty()) {
                Annotation ann = holder.createErrorAnnotation(pkgXml.getLicenceTextRanges().get(0),
                        "package must have at least one licence.");
                ann.registerFix(new AddLicenceQuickFix(pkgXml));
            }

            PackageContribAnnotator contribAnn = new PackageContribAnnotator(pkgXml, holder);
            contribAnn.annNoMaintainers();
            contribAnn.annBadMaintainer();
            contribAnn.annBadAuthor();

            for (int i = 0; i < pkgXml.getURLs().size(); i++) {
                if (pkgXml.getURLs().get(i).isEmpty()) {
                    Annotation ann = holder.createErrorAnnotation(pkgXml.getURLTextRanges().get(i),
                            "empty URL");
                    ann.registerFix(new RemoveURLQuickFix(pkgXml, i));
                }
            }
        }
    }
}
