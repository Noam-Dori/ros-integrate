package ros.integrate.cmake.annotate;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import org.codehaus.plexus.util.StringUtils;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.psi.*;
import ros.integrate.settings.ROSSettings;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.intellij.lang.annotation.HighlightSeverity.INFORMATION;
import static ros.integrate.cmake.highlight.CMakeSyntaxHighlighter.*;

public class CMakeHomeAnnotator implements Annotator {
    private static final Logger LOG = Logger.getLogger("#ros.integrate.cmake.PackageXmlCompletionContributor");
    private static final List<String> KEYWORDS = loadKeywords();
    private static final List<String> F_BLOCK_TYPES = Arrays.asList("function", "macro");
    private static final List<String> VAR_TYPES = Arrays.asList("set", "unset");

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
            holder.newSilentAnnotation(INFORMATION)
                    .textAttributes(LINE_COMMENT)
                    .create();
        }
        if (element instanceof CMakeBracketComment) {
            holder.newSilentAnnotation(INFORMATION)
                    .textAttributes(BLOCK_COMMENT)
                    .create();
        }
        if (element instanceof CMakeCommandName) {
            TextAttributesKey textColor;
            if (KEYWORDS.contains(element.getText())) {
                textColor = KEYWORD;
            } else {
                textColor = COMMAND_CALL;
            }
            holder.newSilentAnnotation(INFORMATION)
                    .range(element)
                    .textAttributes(textColor)
                    .create();
        }
        if (element instanceof CMakeBlock block && F_BLOCK_TYPES.contains(((CMakeBlock) element).getBlockType())) {
            List<CMakeArgument> args = block.getStartCommand().getArguments();
            boolean isFuncName = true;
            for (CMakeArgument arg : args) {
                TextAttributesKey textColor;
                String errorMessage;
                if (isFuncName) {
                    textColor = COMMAND_DECLARATION;
                    errorMessage = StringUtils.capitalise(block.getBlockType()) + " name must be unquoted";
                } else {
                    textColor = VARIABLE;
                    errorMessage = "Named arguments must be unquoted";
                }
                isFuncName = false;
                holder.newSilentAnnotation(INFORMATION)
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
        if (element instanceof CMakeCommand cmd) {
            List<CMakeArgument> args = cmd.getArguments();
            if (VAR_TYPES.contains(cmd.getCommandName().getText().toLowerCase())) {
                // first argument is a new variable name to keep track of
                if (!args.isEmpty()) {
                    CMakeArgument varArg = args.get(0);
                    holder.newSilentAnnotation(INFORMATION)
                            .range(varArg.getArgTextRange())
                            .textAttributes(VARIABLE)
                            .create();
                    if (!(varArg instanceof CMakeUnquotedArgument)) {
                        holder.newAnnotation(HighlightSeverity.ERROR, "Named arguments must be unquoted")
                                .range(varArg)
                                .create();
                    }
                }
            }
        }
        if (element instanceof CMakeArgument arg && !(element instanceof CMakeBracketArgument)) {
            // highlight ${} and rerun on insides
            annVarUse(arg.getArgText(), arg.getTextOffset(), holder);
        }
    }

    private static void annVarUse(String text, int offset, @NotNull AnnotationHolder holder) {
        Matcher varMatcher = Pattern.compile("\\$(?:ENV)?\\{(.*)}").matcher(text);
        while (varMatcher.find()) {
            holder.newSilentAnnotation(INFORMATION)
                    .range(TextRange.create(varMatcher.start(), varMatcher.start(1)).shiftRight(offset))
                    .textAttributes(VARIABLE_BRACES)
                    .create();
            holder.newSilentAnnotation(INFORMATION)
                    .range(TextRange.create(varMatcher.end(1), varMatcher.end()).shiftRight(offset))
                    .textAttributes(VARIABLE_BRACES)
                    .create();
            if (varMatcher.start(1) < varMatcher.end(1)) {
                holder.newSilentAnnotation(INFORMATION)
                        .range(TextRange.create(varMatcher.start(1), varMatcher.end(1)).shiftRight(offset))
                        .textAttributes(VARIABLE)
                        .create();
                annVarUse(varMatcher.group(1), offset + varMatcher.start(1), holder);
            }
        }
    }
}
