package ros.integrate.cmake.ref;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.*;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import ros.integrate.cmake.CMakeClasses;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CMakeReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(CMakeClasses.getCommandClass()), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                return new PsiReference[]{element.getReference()};
            }
        });
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(CMakeClasses.getUnquotedArgClass()), new PsiReferenceProvider() {
            @NotNull
            @Override
            public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                return getVarTextRanges(element.getText(), 0).stream()
                        .map(range -> new CMakeVariableReference(element, range))
                        .toArray(CMakeVariableReference[]::new);
            }
        });
    }

    @NotNull
    private static List<TextRange> getVarTextRanges(String text, int offset) {
        Matcher varMatcher = Pattern.compile("\\$(?:ENV)?\\{(.*)}").matcher(text);
        List<TextRange> result = new ArrayList<>();
        while (varMatcher.find()) {
            if (varMatcher.start(1) < varMatcher.end(1)) {
                // there is a found variable
                List<TextRange> insideResult = getVarTextRanges(varMatcher.group(1), offset + varMatcher.start(1));
                if (insideResult.isEmpty()) {
                    // this is the leaf variable level - add a reference!
                    result.add(TextRange.create(varMatcher.start(1), varMatcher.end(1)).shiftRight(offset));
                }
            }
        }
        return result;
    }
}
