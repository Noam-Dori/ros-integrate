package ros.integrate.msg.file;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.psi.FileViewProvider;
import ros.integrate.msg.ROSPktLanguage;
import ros.integrate.msg.psi.ROSPktFile;

public abstract class ROSPktFileType extends LanguageFileType {

    /**
     * for all ROS packet file types, use the ROS packet language
     */
    ROSPktFileType() {
        super(ROSPktLanguage.INSTANCE);
    }

    public abstract ROSPktFile newPktFile(FileViewProvider viewProvider);
}
