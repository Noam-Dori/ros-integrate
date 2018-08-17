package ros.integrate.msg;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import ros.integrate.ROSIcons;
import ros.integrate.msg.psi.ROSMsgTypes;
import org.jetbrains.annotations.NotNull;

public class ROSMsgCompletionContributor extends CompletionContributor {
    public ROSMsgCompletionContributor() {
        extend(CompletionType.BASIC,
                PlatformPatterns.psiElement(ROSMsgTypes.TYPE).withLanguage(ROSMsgLanguage.INSTANCE),
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
    }
}