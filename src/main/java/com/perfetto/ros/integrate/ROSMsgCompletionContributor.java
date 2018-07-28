package com.perfetto.ros.integrate;

import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.util.ProcessingContext;
import com.perfetto.ros.integrate.psi.ROSMsgTypes;
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
                                .withTypeText("float/double data-type"));
                        resultSet.addElement(LookupElementBuilder.create("int").bold()
                                .withTypeText("integer data-type"));
                        resultSet.addElement(LookupElementBuilder.create("uint").bold()
                                .withTypeText("unsigned integer data-type"));
                    }
                }
        );
    }
}