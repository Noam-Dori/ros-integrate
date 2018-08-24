package ros.integrate.msg;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
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

import java.util.HashSet;
import java.util.Set;

public class ROSMsgCompletionContributor extends CompletionContributor {
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
                                .withTypeText("string data-type"));
                        resultSet.addElement(LookupElementBuilder.create("time").bold()
                                .withTypeText("ROS time data-type"));
                        resultSet.addElement(LookupElementBuilder.create("duration").bold()
                                .withTypeText("ROS duration data-type"));
                        resultSet.addElement(LookupElementBuilder.create("bool").bold()
                                .withTypeText("u_byte/u_char data-type"));
                        resultSet.addElement(LookupElementBuilder.create("float").bold()
                                .withTypeText("float/double data-type").withInsertHandler((insertionContext, item) -> {
                                    CaretModel model = insertionContext.getEditor().getCaretModel();
                                    insertionContext.getDocument().insertString(model.getOffset(),"64");
                                    model.getCurrentCaret().moveCaretRelatively(2,0,true,false);
                                }));
                        resultSet.addElement(LookupElementBuilder.create("int").bold()
                                .withTypeText("integer data-type").withInsertHandler((insertionContext, item) -> {
                                    CaretModel model = insertionContext.getEditor().getCaretModel();
                                    insertionContext.getDocument().insertString(model.getOffset(),"32");
                                    model.getCurrentCaret().moveCaretRelatively(2,0,true,false);
                                }));
                        resultSet.addElement(LookupElementBuilder.create("uint").bold()
                                .withTypeText("unsigned integer data-type").withInsertHandler((insertionContext, item) -> {
                                    CaretModel model = insertionContext.getEditor().getCaretModel();
                                    insertionContext.getDocument().insertString(model.getOffset(),"8");
                                    model.getCurrentCaret().moveCaretRelatively(1,0,true,false);
                                }));
                        for (String projectMsg : ROSMsgUtil.findProjectMsgNames(parameters.getEditor().getProject(),
                                null,parameters.getOriginalFile().getVirtualFile())) {
                            resultSet.addElement(LookupElementBuilder.create(projectMsg).withIcon(ROSIcons.MSG_FILE));
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