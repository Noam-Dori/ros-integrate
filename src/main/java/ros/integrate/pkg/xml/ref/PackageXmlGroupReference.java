package ros.integrate.pkg.xml.ref;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.PsiPolyVariantReferenceBase;
import com.intellij.psi.ResolveResult;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;

import java.util.*;

/**
 * defines the reference of group_depend tags to member_of_group tags with the same value and vice versa.
 * This is part of package.xml file features.
 * @author Noam Dori
 */
public class PackageXmlGroupReference extends PsiPolyVariantReferenceBase<XmlTag> {
    @NotNull
    private final String groupName;
    @NotNull
    private final String lookupString;
    @NotNull
    private final ROSPackageManager manager;

    /**
     * construct a new reference
     * @param element the referencing group tag
     * @param isDepend true if the tag is a group_depend tag, false if it is a member_of_group tag
     */
    public PackageXmlGroupReference(XmlTag element, boolean isDepend) {
        super(element, getTextRange(element), true);
        groupName = element.getValue().getText();
        manager = element.getProject().getService(ROSPackageManager.class);
        lookupString = isDepend ? "member_of_group" : "group_depend";
    }

    @NotNull
    private static TextRange getTextRange(@NotNull XmlTag element) {
        return element.getValue().getTextRange().shiftLeft(element.getTextOffset());
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        List<ResolveResult> ret = new ArrayList<>();
        manager.getAllPackages().stream().map(ROSPackage::getPackageXml).filter(Objects::nonNull)
                .forEach(pkgXml -> Arrays.stream(pkgXml.findSubTags(lookupString))
                        .filter(tag -> tag.getValue().getText().equals(groupName))
                        .forEach(tag -> ret.add(new PsiElementResolveResult(tag))));
        return ret.toArray(ResolveResult.EMPTY_ARRAY);
    }
}
