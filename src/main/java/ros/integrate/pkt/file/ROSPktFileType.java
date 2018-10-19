package ros.integrate.pkt.file;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.psi.FileViewProvider;
import ros.integrate.pkt.lang.ROSPktLanguage;
import ros.integrate.pkt.psi.ROSPktFile;

public abstract class ROSPktFileType extends LanguageFileType {

    /**
     * for all ROS packet file types, use the ROS packet language
     */
    ROSPktFileType() {
        super(ROSPktLanguage.INSTANCE);
    }

    public abstract ROSPktFile newPktFile(FileViewProvider viewProvider);
}
