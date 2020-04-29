package ros.integrate.pkg.xml.condition;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkg.xml.condition.psi.ROSConditionTypes;

public class ROSConditionCompletionContributor extends CompletionContributor {
    public ROSConditionCompletionContributor() {
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(ROSConditionTypes.VARIABLE), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                for (String env : System.getenv().keySet()) {
                    result.addElement(LookupElementBuilder.create("$" + env).withPresentableText(env));
                }
            }
        });
        extend(CompletionType.BASIC, PlatformPatterns.psiElement(ROSConditionTypes.LITERAL), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, @NotNull ProcessingContext context, @NotNull CompletionResultSet result) {
                result.addElement(LookupElementBuilder.create("or").bold());
                result.addElement(LookupElementBuilder.create("and").bold());
            }
        });
    }
}
