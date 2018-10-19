package ros.integrate.msg.file;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.msg.ROSMsgLanguage;

import javax.swing.*;

/**
 * @author Noam Dori
 * a class defining the ROS Msg file type (and perhaps the service one as well)
 */
public class ROSMsgFileType extends LanguageFileType {
    public static final LanguageFileType INSTANCE = new ROSMsgFileType();
    @NonNls private static final String DEFAULT_EXTENSION = "msg";
    @NonNls public static final String DOT_DEFAULT_EXTENSION = "." + DEFAULT_EXTENSION;

    /**
     * Creates a language file type for the specified language.
     */
    private ROSMsgFileType() {
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