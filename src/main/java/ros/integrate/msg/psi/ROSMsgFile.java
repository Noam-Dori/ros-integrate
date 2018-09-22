package ros.integrate.msg.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.msg.ROSMsgFileType;
import ros.integrate.msg.ROSMsgLanguage;
import ros.integrate.msg.ROSMsgUtil;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ROSMsgFile extends PsiFileBase {
    public ROSMsgFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ROSMsgLanguage.INSTANCE);
    }

    @NotNull
    public List<ROSMsgField> getFields() {
        List<ROSMsgField> result = new ArrayList<>();
        ROSMsgField[] fields = PsiTreeUtil.getChildrenOfType(this, ROSMsgField.class);
        if (fields != null) {
            Collections.addAll(result, fields);
        }
        return result;
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ROSMsgFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "ROS Message File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return ROSMsgUtil.trimMsgFileName(getContainingFile().getName());
            }

            @Override
            public String getLocationString() {
                return getProject().getBaseDir().getName() + "/" + getPresentableText();
            }

            @Override
            public Icon getIcon(boolean unused) {
                return ROSIcons.MsgFile;
            }
        };
    }

    @NotNull
    @Override
    public String getName() {
        return ROSMsgUtil.trimMsgFileName(super.getName());
    }


    @NotNull
    @Override
    public PsiElement setName(@NotNull String name) throws IncorrectOperationException {
        return super.setName(name + ROSMsgFileType.DOT_DEFAULT_EXTENSION);
    }

    /**
     * determines how many service separators are present in this file
     * @return the number of valid service separators in this file
     */
    public int countServiceSeparators() {
        ROSMsgSeparator[] fields = PsiTreeUtil.getChildrenOfType(this, ROSMsgSeparator.class);
        if (fields != null) {
            return fields.length;
        }
        return 0;
    }

    /**
     * counts how many times the field name {@param name} appear in this file
     * @param name the name to search for. should be a non-empty string
     * @return the number of times the field name {@param name} appears in the file.
     */
    public int countNameInFile(@NotNull String name) {
        int count = 0;
        ROSMsgField[] fields = PsiTreeUtil.getChildrenOfType(this, ROSMsgField.class);
        if (fields != null) {
            for (ROSMsgField field : fields) {
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
    public boolean isFirstDefinition(@NotNull ROSMsgLabel name) {
        return name.equals(getFirstNameInFile(name.getText()));
    }

    /**
     * fetches the first name provided in this file with the name {@param name}
     * @param name the field name to search for
     * @return <code>null</code> if a field labeled {@param name} does not exist in this file, otherwise,
     *         the first psi label in the file holding that name.
     */
    @Nullable
    private ROSMsgLabel getFirstNameInFile(@NotNull String name) {
        ROSMsgField[] fields = PsiTreeUtil.getChildrenOfType(this, ROSMsgField.class);
        if (fields != null) {
            for (ROSMsgField field : fields) {
                if (name.equals(field.getLabel().getText())) {
                    return field.getLabel();
                }
            }
        }
        return null;
    }
}
