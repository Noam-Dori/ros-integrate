package ros.integrate.msg.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.msg.file.ROSSrvFileType;
import ros.integrate.msg.ROSMsgLanguage;
import ros.integrate.msg.ROSMsgUtil;

import javax.swing.*;

/**
 * a ROS message, a one-directional message sent between (and within) executables.
 */
public class ROSSrvFile extends ROSFile {
    public ROSSrvFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ROSMsgLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ROSSrvFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "ROS Service File";
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
                return ROSIcons.SrvFile;
            }
        };
    }

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    String getDotDefaultExtension() {
        return ROSSrvFileType.DOT_DEFAULT_EXTENSION;
    }

    @Override
    public int getMaxSeparators() {
        return 1;
    }

    @Override
    public String getTooManySeparatorsMessage() {
        return "ROS Services can only have one service separator";
    }
}
