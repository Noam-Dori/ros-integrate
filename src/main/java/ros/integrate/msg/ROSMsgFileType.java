package ros.integrate.msg;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;

import javax.swing.*;

/**
 * @author max
 */
public class ROSMsgFileType extends LanguageFileType {
    public static final LanguageFileType INSTANCE = new ROSMsgFileType();
    @NonNls public static final String DEFAULT_EXTENSION = "msg";
    @NonNls public static final String DOT_DEFAULT_EXTENSION = "." + DEFAULT_EXTENSION;

    /**
     * Creates a language file type for the specified language.
     */
    public ROSMsgFileType() {
        super(ROSMsgLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "ROSMsg";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "ROS Message File";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return DEFAULT_EXTENSION;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return ROSIcons.MsgFile;
    }
}