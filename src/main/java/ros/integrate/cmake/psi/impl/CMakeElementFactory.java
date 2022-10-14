package ros.integrate.cmake.psi.impl;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import ros.integrate.cmake.lang.CMakeLanguage;
import ros.integrate.cmake.psi.CMakeCommand;
import ros.integrate.cmake.psi.CMakeFile;

public class CMakeElementFactory {
    public static CMakeCommand createCommand(Project project, String cmdName, String... cmdArgs) {
        CMakeFile file = createFile(project, cmdName + "(" + String.join(" ", cmdArgs));
        return (CMakeCommand) file.getFirstChild();
    }

    private static CMakeFile createFile(Project project, String text) {
        String name = "dummy.cmake";
        return (CMakeFile) PsiFileFactory.getInstance(project).createFileFromText(name, CMakeLanguage.INSTANCE, text);
    }


}
