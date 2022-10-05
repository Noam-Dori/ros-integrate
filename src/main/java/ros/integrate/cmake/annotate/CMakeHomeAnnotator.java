package ros.integrate.cmake.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.psi.PsiElement;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.highlight.CMakeSyntaxHighlighter;
import ros.integrate.cmake.psi.*;
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
    private static final List<String> F_BLOCK_TYPES = Arrays.asList("function", "macro");

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
        if (element instanceof CMakeBlock && F_BLOCK_TYPES.contains(((CMakeBlock) element).getBlockType())) {
            CMakeBlock block = (CMakeBlock) element;
            List<CMakeArgument> args = block.getStartCommand().getArguments();
            boolean isFuncName = true;
            for (CMakeArgument arg : args) {
                TextAttributesKey textColor;
                String errorMessage;
                if (isFuncName) {
                    textColor = CMakeSyntaxHighlighter.COMMAND_DECLARATION;
                    errorMessage = StringUtils.capitalise(block.getBlockType()) + " name must be unquoted";
                } else {
                    textColor = CMakeSyntaxHighlighter.VARIABLE;
                    errorMessage = "Named arguments must be unquoted";
                }
                isFuncName = false;
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(arg.getArgTextRange())
                        .textAttributes(textColor)
                        .create();
                if (!(arg instanceof CMakeUnquotedArgument)) {
                    holder.newAnnotation(HighlightSeverity.ERROR, errorMessage)
                            .range(arg)
                            .create();
                }
            }
            // iterate over all functions and annotate the named argument from the definition, ARGC, ARGV, ARGV# as variables.
        }
    }
}
