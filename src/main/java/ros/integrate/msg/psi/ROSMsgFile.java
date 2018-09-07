package ros.integrate.msg.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.util.PsiTreeUtil;
import ros.integrate.ROSIcons;
import ros.integrate.msg.ROSMsgFileType;
import ros.integrate.msg.ROSMsgLanguage;
import ros.integrate.msg.ROSMsgUtil;
import org.jetbrains.annotations.NotNull;

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
}
