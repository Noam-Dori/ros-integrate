package ros.integrate.cmake.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.HighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.highlight.CMakeSyntaxHighlighter;
import ros.integrate.cmake.psi.CMakeBracketComment;
import ros.integrate.cmake.psi.CMakeCommand;
import ros.integrate.cmake.psi.CMakeCommandName;
import ros.integrate.cmake.psi.CMakeLineComment;
import ros.integrate.settings.ROSSettings;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;

public class CMakeHomeAnnotator implements Annotator {
    private static final Logger LOG = Logger.getLogger("#ros.integrate.cmake.PackageXmlCompletionContributor");
    private static final List<String> KEYWORDS = loadKeywords();

    @NotNull
    private static List<String> loadKeywords() {
        Properties ret = new Properties();
        try {
            ret.load(ROSSettings.class.getClassLoader().getResourceAsStream("defaults.properties"));
            return Arrays.asList(ret.getProperty("cmakeKeywords").split(":"));
        } catch (IOException e) {
            LOG.warning("could not load configuration file, default values will not be loaded. error: " +
                    e.getMessage());
        }
        return Collections.emptyList();
    }

    @Override
    public void annotate(@NotNull PsiElement element, @NotNull AnnotationHolder holder) {
        if (element instanceof CMakeLineComment) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .textAttributes(CMakeSyntaxHighlighter.LINE_COMMENT)
                    .create();
        }
        if (element instanceof CMakeBracketComment) {
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .textAttributes(CMakeSyntaxHighlighter.BLOCK_COMMENT)
                    .create();
        }
        if (element instanceof CMakeCommandName) {
            TextAttributesKey textColor;
            if (KEYWORDS.contains(element.getText())) {
                textColor = CMakeSyntaxHighlighter.KEYWORD;
            } else {
                textColor = CMakeSyntaxHighlighter.COMMAND_CALL;
            }
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element)
                    .textAttributes(textColor)
                    .create();
        }
    }
}
