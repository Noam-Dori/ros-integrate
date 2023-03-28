package ros.integrate.pkt.file;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.lang.ROSPktLanguage;
import ros.integrate.pkt.psi.ROSPktFile;

/**
 * the base template for all packet file types.
 * Takes care of linking file type to language and provides a common interface for creating files
 * @author Noam Dori
 */
public abstract class ROSPktFileType extends LanguageFileType {

    /**
     * for all ROS packet file types, use the ROS packet language
     */
    ROSPktFileType() {
        super(ROSPktLanguage.INSTANCE);
    }

    /**
     * construct a new packet file according to this file type
     * @param viewProvider the view provider to use
     * @return a new file of the type of this class.
     */
    public abstract ROSPktFile newPktFile(FileViewProvider viewProvider);

    @Override
    public @Nls @NotNull String getDisplayName() {
        return "ROS " + getDescription();
    }
}
