package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;
import ros.integrate.pkg.xml.intention.FixFormatQuickFix;
import ros.integrate.pkg.xml.intention.FixNameQuickFix;

public class PackageXmlAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof XmlFile) {
            ROSPackageXml pkgXml = PackageXmlUtil.getWrapper((XmlFile) element);
            if (pkgXml == null) {
                return;
            }

            if (pkgXml.getFormat() == 0) {
                Annotation ann = holder.createErrorAnnotation(pkgXml.getFormatTextRange(),"Invalid package format");
                ann.registerFix(new FixFormatQuickFix(pkgXml));
            }

            if (pkgXml.getPkgName() == null) {
                Annotation ann = holder.createErrorAnnotation(pkgXml.getRootTextRange(),"package should give a name to the package");
                ann.registerFix(new FixNameQuickFix(pkgXml, "Add"));
            } else if (!pkgXml.getPkgName().equals(pkgXml.getPackage().getName())) {
                Annotation ann = holder.createErrorAnnotation(pkgXml.getNameTextRange(),"package name should match its parent directory");
                ann.registerFix(new FixNameQuickFix(pkgXml, "Fix"));
            }
        }
    }
}
