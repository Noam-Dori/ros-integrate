package ros.integrate.cmake.lang;

import com.intellij.lang.LanguageParserDefinitions;
import com.intellij.openapi.fileTypes.FileNameMatcher;
import com.intellij.openapi.fileTypes.FileTypeConsumer;
import com.intellij.openapi.fileTypes.FileTypeFactory;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.CMakeClasses;

/**
 * Due to the shortcomings of the IntelliJ SDK platform which does not allow negative conditional checks
 * for the CLion IDE
 * @author Noam Dori
 */
@SuppressWarnings("deprecation")
public class CMakeFileTypeFactory extends FileTypeFactory {
    @Override
    public void createFileTypes(@NotNull FileTypeConsumer consumer) {
        if (CMakeClasses.CLION) {
            return;
        }
        LanguageParserDefinitions.INSTANCE.addExplicitExtension(CMakeLanguage.INSTANCE, new CMakeParserDefinition());
        consumer.consume(CMakeFileType.INSTANCE, new FileNameMatcher() {
            @Override
            public @NotNull String getPresentableString() {
                return "CMake";
            }

            @Override
            public boolean acceptsCharSequence(@NotNull CharSequence fileName) {
                return fileName.toString().matches("CMakeLists\\.txt|.*\\.cmake");
            }
        });
    }
}
