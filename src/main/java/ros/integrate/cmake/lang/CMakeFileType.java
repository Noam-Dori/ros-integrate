package ros.integrate.cmake.lang;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;

import javax.swing.Icon;

public class CMakeFileType extends LanguageFileType {
    public static final FileType INSTANCE = new CMakeFileType();

    protected CMakeFileType() {
        super(CMakeLanguage.INSTANCE);
    }

    @Override
    public @NotNull String getName() {
        return "CMake File";
    }

    @Override
    public @NotNull
    String getDescription() {
        return "CMake File";
    }

    @Override
    public @NotNull String getDefaultExtension() {
        return "cmake";
    }

    @Override
    public @Nullable Icon getIcon() {
        return ROSIcons.CMAKE_FILE;
    }
}
