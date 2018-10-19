package ros.integrate.pkt.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.Language;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNameIdentifierOwner;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.ROSPktUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * an instance of a packet containing template file, sent by the ROS framework.
 * the class name is short for "ROS Packet File", which will be used throughout the language.
 */
public abstract class ROSPktFile extends PsiFileBase implements PsiNameIdentifierOwner {
    ROSPktFile(@NotNull FileViewProvider viewProvider, @NotNull Language language) {
        super(viewProvider, language);
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
    public int countServiceSeparators() {
        ROSPktSeparator[] fields = PsiTreeUtil.getChildrenOfType(this, ROSPktSeparator.class);
        if (fields != null) {
            return fields.length;
        }
        return 0;
    }

    /**
     * gets all available (and valid) fields in this file.
     * @return a list of all available fields in this file in textual order.
     */
    @NotNull
    public List<ROSPktField> getFields() {
        List<ROSPktField> result = new ArrayList<>();
        ROSPktField[] fields = PsiTreeUtil.getChildrenOfType(this, ROSPktField.class);
        if (fields != null) {
            Collections.addAll(result, fields);
        }
        return result;
    }

    /**
     * counts how many times the field name {@param name} appear in this file
     * @param name the name to search for. should be a non-empty string
     * @return the number of times the field name {@param name} appears in the file.
     */
    public int countNameInFile(@NotNull String name) {
        int count = 0;
        ROSPktField[] fields = PsiTreeUtil.getChildrenOfType(this, ROSPktField.class);
        if (fields != null) {
            for (ROSPktField field : fields) {
                if (name.equals(field.getLabel().getText())) {
                    count++;
                }
            }
        }
        return count;
    }

    /**
     * checks whether of not the label provided is the first label in this file that has its name.
     * @param name the field to test
     * @return <code>true</code> if {@param field} is the first first defined label with the provided name in this file,
     *         <code>false</code> otherwise.
     */
    public boolean isFirstDefinition(@NotNull ROSPktLabel name) {
        return name.equals(getFirstNameInFile(name.getText()));
    }

    /**
     * fetches the first name provided in this file with the name {@param name}
     * @param name the field name to search for
     * @return <code>null</code> if a field labeled {@param name} does not exist in this file, otherwise,
     *         the first psi label in the file holding that name.
     */
    @Nullable
    private ROSPktLabel getFirstNameInFile(@NotNull String name) {
        ROSPktField[] fields = PsiTreeUtil.getChildrenOfType(this, ROSPktField.class);
        if (fields != null) {
            for (ROSPktField field : fields) {
                if (name.equals(field.getLabel().getText())) {
                    return field.getLabel();
                }
            }
        }
        return null;
    }

    @NotNull
    @Override
    public String getName() {
        return ROSPktUtil.trimPktFileName(super.getName());
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
