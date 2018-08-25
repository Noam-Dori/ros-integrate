package ros.integrate.msg;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.msg.psi.ROSMsgProperty;
import ros.integrate.msg.psi.ROSMsgTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class ROSMsgCompletionContributor extends CompletionContributor {
    private static LookupElement[] INTEGRAL_SIZES = Arrays.stream(new String[] {"8", "16", "32", "64"})
            .map(LookupElementBuilder::create)
            .toArray(LookupElement[]::new);
    private static LookupElement[] FLOAT_SIZES = Arrays.stream(new String[] {"32", "64"})
            .map(LookupElementBuilder::create)
            .toArray(LookupElement[]::new);

    public ROSMsgCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(ROSMsgTypes.CUSTOM_TYPE).withLanguage(ROSMsgLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("Header")
                                .withTypeText("std_msgs/Header data-type")
                                .withCaseSensitivity(false));
                        resultSet.addElement(LookupElementBuilder.create("string").bold()
                                .withTypeText("string of characters"));
                        resultSet.addElement(LookupElementBuilder.create("time").bold()
                                .withTypeText("ROS time"));
                        resultSet.addElement(LookupElementBuilder.create("duration").bold()
                                .withTypeText("ROS duration"));
                        resultSet.addElement(LookupElementBuilder.create("bool").bold()
                                .withTypeText("1 or 0"));
                        resultSet.addElement(LookupElementBuilder.create("float").bold()
                                .withTypeText("floating point number")
                                .withInsertHandler((insertionContext, item) ->
                                        handleNumericalInserts(insertionContext,FLOAT_SIZES,FLOAT_SIZES[1])));
                        resultSet.addElement(LookupElementBuilder.create("int").bold()
                                .withTypeText("signed integral number")
                                .withInsertHandler((insertionContext, item) ->
                                        handleNumericalInserts(insertionContext,INTEGRAL_SIZES,INTEGRAL_SIZES[2])));
                        resultSet.addElement(LookupElementBuilder.create("uint").bold()
                                .withTypeText("unsigned integral number")
                                .withInsertHandler((insertionContext, item) ->
                                        handleNumericalInserts(insertionContext,INTEGRAL_SIZES,INTEGRAL_SIZES[0])));
                        for (String projectMsg : ROSMsgUtil.findProjectMsgNames(parameters.getEditor().getProject(),
                                null,parameters.getOriginalFile().getVirtualFile())) {
                            resultSet.addElement(LookupElementBuilder.create(projectMsg).withIcon(ROSIcons.MsgFile));
                        }
                    }
                }
        );
        extend(CompletionType.BASIC,PlatformPatterns.psiElement(ROSMsgTypes.NAME),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        ROSMsgNameSuggestionProvider provider = findProvider();
                        PsiElement element = parameters.getPosition();
                        Set<String> stringResults = new HashSet<>();
                        if (provider != null) {
                            provider.getSuggestedNames(element,((ROSMsgProperty)element.getParent().getParent()).getType(),stringResults);
                        }

                        stringResults.forEach(result -> resultSet.addElement(LookupElementBuilder.create(result)));
                    }
                }
        );
    }

    private void handleNumericalInserts(InsertionContext insertionContext,
                                        LookupElement[] nextLookup,
                                        @Nullable LookupElement currentSelection) {
        if (currentSelection != null) {
            String defaultSelection = currentSelection.getLookupString();
            CaretModel model = insertionContext.getEditor().getCaretModel();

            insertionContext.getDocument().insertString(model.getOffset(),defaultSelection);
            model.getCurrentCaret().moveCaretRelatively(defaultSelection.length(),0,true,false);
        }
        Objects.requireNonNull(LookupManager.getInstance(insertionContext.getProject())
                .showLookup(insertionContext.getEditor(), nextLookup))
                .setCurrentItem(currentSelection);
    }

    @Nullable
    private static ROSMsgNameSuggestionProvider findProvider() {
        Object[] extensions = Extensions.getExtensions(ROSMsgNameSuggestionProvider.EP_NAME);

        for (Object extension : extensions) {
            if (extension instanceof ROSMsgNameSuggestionProvider) {
                return (ROSMsgNameSuggestionProvider)extension;
            }
        }
        return null;
    }
}