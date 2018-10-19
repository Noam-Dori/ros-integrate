package ros.integrate.pkt.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.pkt.file.ROSMsgFileType;
import ros.integrate.pkt.lang.ROSPktLanguage;
import ros.integrate.pkt.ROSPktUtil;

import javax.swing.*;

/**
 * a ROS message, a one-directional message sent between (and within) executables.
 */
public class ROSMsgFile extends ROSPktFile {
    public ROSMsgFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ROSPktLanguage.INSTANCE);
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
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @NotNull
            @Override
            public String getPresentableText() {
                return ROSPktUtil.trimPktFileName(getContainingFile().getName());
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

    @Nullable
    @Override
    public PsiElement getNameIdentifier() {
        return null;
    }

    @Override
    String getDotDefaultExtension() {
        return ROSMsgFileType.DOT_DEFAULT_EXTENSION;
    }

    @Override
    public int getMaxSeparators() {
        return 0;
    }

    @Override
    public String getTooManySeparatorsMessage() {
        return "ROS Messages cannot have service separators";
    }
}
