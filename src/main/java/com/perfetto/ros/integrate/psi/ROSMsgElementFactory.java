package com.perfetto.ros.integrate.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import com.perfetto.ros.integrate.ROSMsgFileType;

public class ROSMsgElementFactory {
    public static ROSMsgProperty createProperty(Project project, String name, String value) {
        final ROSMsgFile file = createFile(project, name + " " + value);
        return (ROSMsgProperty) file.getFirstChild();
    }

    public static ROSMsgProperty createProperty(Project project, String name) {
        final ROSMsgFile file = createFile(project, name);
        return (ROSMsgProperty) file.getFirstChild();
    }

    public static ROSMsgProperty createSeperator(Project project) {
        final ROSMsgFile file = createFile(project, "---");
        return (ROSMsgProperty) file.getFirstChild();
    }

    public static PsiElement createCRLF(Project project) {
        System.out.println("in CRLF");
        final ROSMsgFile file = createFile(project, "\n");
        return file.getFirstChild();
    }

    public static ROSMsgFile createFile(Project project, String text) {
        String name = "dummy.msg";
        return (ROSMsgFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, ROSMsgFileType.INSTANCE, text);
    }
}
