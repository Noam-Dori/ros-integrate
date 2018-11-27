package ros.integrate.elementFinder;

import com.intellij.openapi.extensions.ExtensionPointName;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.xml.XmlFile;
import com.intellij.util.Processor;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.workspace.psi.ROSPackage;

public abstract class ROSPsiElementFinder {
    public static final ExtensionPointName<ROSPsiElementFinder> EP_NAME = ExtensionPointName.create("ros.integrate.ros.elementFinder");

    public abstract boolean processPackageDirectories(ROSPackage pkg, GlobalSearchScope scope, Processor<PsiDirectory> processor, boolean includeLibrarySources);

    public abstract ROSPackage findPackage(String qualifiedName);

    public abstract Condition<PsiFile> getPackageFilesFilter(ROSPackage pkg, GlobalSearchScope scope);

    public abstract PsiFile[] getPackageFiles(ROSPackage pkg, GlobalSearchScope scope);

    public abstract Condition<ROSPktFile> getPacketsFilter(GlobalSearchScope scope);

    public abstract ROSPktFile[] getPackets(ROSPackage pkg, GlobalSearchScope scope);

    public abstract Condition<PsiFile> getSourcesFilter(GlobalSearchScope scope);

    public abstract PsiFile[] getSources(ROSPackage pkg, GlobalSearchScope scope);

    public abstract PsiFile getCMakeLists(ROSPackage pkg);

    public abstract XmlFile getPackageXml(ROSPackage pkg);
}
