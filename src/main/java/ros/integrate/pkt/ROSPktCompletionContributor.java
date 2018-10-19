package ros.integrate.pkt;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.*;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.project.Project;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;
import ros.integrate.pkt.lang.ROSPktLanguage;
import ros.integrate.pkt.psi.ROSPktField;
import ros.integrate.pkt.psi.ROSPktTypes;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * a class enabling and defining auto-completion within ROS messages
 */
public class ROSPktCompletionContributor extends CompletionContributor {
    private static LookupElement[] INTEGRAL_SIZES = Arrays.stream(new String[] {"8", "16", "32", "64"})
            .map(LookupElementBuilder::create)
            .map(LookupElementBuilder::bold)
            .toArray(LookupElement[]::new);
    private static LookupElement[] FLOAT_SIZES = Arrays.stream(new String[] {"32", "64"})
            .map(LookupElementBuilder::create)
            .map(LookupElementBuilder::bold)
            .toArray(LookupElement[]::new);

    public ROSPktCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(ROSPktTypes.CUSTOM_TYPE).withLanguage(ROSPktLanguage.INSTANCE),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        resultSet.addElement(LookupElementBuilder.create("Header")
                                .withTypeText("std_msgs/Header data-type"));
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
                        Project project = parameters.getEditor().getProject();
                        if(project != null) {
                            for (String projectMsg : ROSPktUtil.findMessageNames(project,
                                    null, parameters.getOriginalFile().getVirtualFile())) {
                                resultSet.addElement(LookupElementBuilder.create(projectMsg)
                                        .withIcon(ROSIcons.MsgFile));
                            }
                        }
                    }
                }
        );
        extend(CompletionType.BASIC,PlatformPatterns.psiElement(ROSPktTypes.NAME),
                new CompletionProvider<CompletionParameters>() {
                    public void addCompletions(@NotNull CompletionParameters parameters,
                                               ProcessingContext context,
                                               @NotNull CompletionResultSet resultSet) {
                        ROSPktNameSuggestionProvider provider = findProvider();
                        PsiElement element = parameters.getPosition();
                        Set<String> stringResults = new HashSet<>();
                        if (provider != null) {
                            provider.getSuggestedNames(element,((ROSPktField)element.getParent().getParent()).getType(),stringResults);
                        }

                        stringResults.forEach(result -> resultSet.addElement(LookupElementBuilder.create(result)));
                    }
                }
        );
    }

    private static void handleNumericalInserts(InsertionContext insertionContext,
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
    private static ROSPktNameSuggestionProvider findProvider() {
        Object[] extensions = Extensions.getExtensions(ROSPktNameSuggestionProvider.EP_NAME);

        for (Object extension : extensions) {
            if (extension instanceof ROSPktNameSuggestionProvider) {
                return (ROSPktNameSuggestionProvider)extension;
            }
        }
        return null;
    }
}