package ros.integrate.pkt.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.PsiQualifiedNamedElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.ROSPktUtil;
import ros.integrate.pkt.lang.ROSPktLanguage;
import ros.integrate.pkg.psi.ROSPackage;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * an instance of a packet containing template file, sent by the ROS framework.
 * the class name is short for "ROS Packet File", which will be used throughout the language.
 */
public abstract class ROSPktFile extends PsiFileBase implements PsiNameIdentifierOwner, PsiQualifiedNamedElement, Comparable<ROSPktFile> {
    private ROSPackage parentPackage;

    ROSPktFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ROSPktLanguage.INSTANCE);
        parentPackage = ROSPackage.ORPHAN;
    }

    @Override
    public int compareTo(@NotNull ROSPktFile o) {
        return getQualifiedName().compareTo(o.getQualifiedName());
    }

    @Override
    public String toString() {
        return getQualifiedName() + getDotDefaultExtension();
    }

    /**
     * @return the ROS package this pkt file belongs to.
     */
    public ROSPackage getPackage() {
        return parentPackage;
    }

    public void setPackage(ROSPackage newPackage) {
        parentPackage = newPackage;
    }

    @NotNull
    @Override
    public String getQualifiedName() {
        return getPackage().getName() + "/" + getPacketName();
    }

    /**
     * gets the default extension for the file type.
     * @return the extension for the file type with a dot.
     */
    abstract String getDotDefaultExtension();

    /**
     * fetches the maximum amount of separators allowed in the file.
     * @return what is said above.
     */
    public abstract int getMaxSeparators();

    /**
     * fetches the message declaring there are too many message separators in the file.
     * @return some non-empty string
     */
    public abstract String getTooManySeparatorsMessage();

    /**
     * declare whether or not the "remove all separators fix should be suggested"
     * @param separatorCount the number of separators found in this file.
     * @return true if the quickfix should be activated, false otherwise.
     */
    public boolean flagRemoveAll(int separatorCount) {
        return separatorCount > getMaxSeparators() + 1;
    }

    /**
     * determines how many service separators are present in this file
     * @return the number of valid service separators in this file
     */
    public int countSectionSeparators() {
        ROSPktSeparator[] fields = PsiTreeUtil.getChildrenOfType(this, ROSPktSeparator.class);
        if (fields != null) {
            return fields.length;
        }
        return 0;
    }

    @NotNull
    private List<ROSPktSection> getSections() {
        return PsiTreeUtil.getChildrenOfTypeAsList(this, ROSPktSection.class);
    }

    /**
     * gets all available (and valid) fields in this file.
     * @return a list of all available fields in this file in textual order.
     * @param queryClass the class of which to search. If limited to complete fields, use {@link ROSPktField}
     *                   if fragments need be searched use {@link ROSPktFieldFrag}.
     *                   if you want both, use {@link ROSPktFieldBase}
     * @param includeConstants whether or not constant fields should be included
     */
    @NotNull
    public <T extends ROSPktFieldBase> List<T> getFields(Class<T> queryClass, boolean includeConstants) {
        List<T> result = new ArrayList<>();
        getSections().stream()
                .map(section -> PsiTreeUtil.getChildrenOfTypeAsList(section, queryClass))
                .forEach(result::addAll);
        if (includeConstants) {
            return result;
        }
        return result.stream().filter(field -> field.getConst() == null)
                .collect(Collectors.toList());
    }

    public List<PsiElement> getFieldsAndComments() {
        List<PsiElement> result = new ArrayList<>();
        getSections().stream()
                .map(section -> PsiTreeUtil.getChildrenOfAnyType(section, ROSPktFieldBase.class, ROSPktComment.class))
                .forEach(result::addAll);
        return result;
    }

    /**
     * @return the file's name without the extension
     */
    @NotNull
    public String getPacketName() {
        return ROSPktUtil.trimPktFileName(getName());
    }

    @NotNull
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return super.setName(name + getDotDefaultExtension());
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}
