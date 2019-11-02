package ros.integrate.pkg.xml.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import com.intellij.psi.xml.XmlFile;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.PackageXmlUtil;
import ros.integrate.pkg.xml.ROSPackageXml;

public class PackageXmlAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof XmlFile) {
            ROSPackageXml pkgXml = PackageXmlUtil.getWrapper((XmlFile) element);
            if (pkgXml == null) {
                return;
            }

            if (pkgXml.getFormat() == 0) {
                holder.createErrorAnnotation(pkgXml.getFormatTextRange(),"Invalid package format");
            }

            if (pkgXml.getPkgName() == null) {
                holder.createErrorAnnotation(pkgXml.getRootTextRange(),"package should give a name to the package");
            } else if (!pkgXml.getPkgName().equals(pkgXml.getPackage().getName())) {
                holder.createErrorAnnotation(pkgXml.getNameTextRange(),"package name should match its parent directory");
            }
        }
    }
}
