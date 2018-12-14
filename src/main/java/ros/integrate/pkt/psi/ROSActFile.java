package ros.integrate.pkt.psi;

import com.intellij.navigation.ItemPresentation;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.pkt.ROSPktUtil;
import ros.integrate.pkt.file.ROSActFileType;
import ros.integrate.pkt.lang.ROSPktLanguage;

import javax.swing.*;

/**
 * a ROS message, a one-directional message sent between (and within) executables.
 */
public class ROSActFile extends ROSPktFile {
    public ROSActFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, ROSPktLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return ROSActFileType.INSTANCE;
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
                return ROSIcons.ActFile;
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
        return ROSActFileType.DOT_DEFAULT_EXTENSION;
    }

    @Override
    public int getMaxSeparators() {
        return 2;
    }

    @Override
    public String getTooManySeparatorsMessage() {
        return "ROS Services can only have two separators";
    }
}
