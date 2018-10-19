package ros.integrate.msg.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.file.ROSMsgFileType;

/**
 * a utility factory class to generate PSI objects within ROS messages.
 */
public class ROSMsgElementFactory {
    public static final String ANNOTATION_PREFIX = "# noinspection ";

    /**
     * creates a dummy msg file
     * @param project the project the field/file belongs to
     * @param text text to parse into PSI
     * @return a msg file containing the provided text
     */
    @NotNull
    private static ROSMsgFile createFile(Project project, String text) {
        String name = "dummy.msg";
        return (ROSMsgFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, ROSMsgFileType.INSTANCE, text);
    }

    /**
     * creates an actual message file
     * @param fileName the name of the file without extension
     * @param directory the directory this file should be placed in
     * @return the instance of the file created.
     */
    @NotNull
    public static ROSMsgFile createFile(String fileName, @NotNull PsiDirectory directory) {
        return (ROSMsgFile) directory.createFile(fileName + ".msg");
    }

    /**
     * creates an annotation comment
     * @param project the project this belongs to.
     * @param annotationText the text included within the annotation.
     * @return an annotation type comment.
     */
    public static ROSMsgComment createAnnotation(Project project, String annotationText) {
        final ROSMsgFile file = createFile(project, ANNOTATION_PREFIX + annotationText);
        return (ROSMsgComment) file.getFirstChild();
    }

    /**
     * creates a Carriage Return; Line Feed.
     * @param project the project this belongs to.
     * @return a psi element holding the CRLF
     */
    public static PsiElement createCRLF(Project project) {
        final ROSMsgFile file = createFile(project, "\n");
        return file.getFirstChild();
    }

    /**
     * creates an instance of a message type.
     * @param project the project this belongs to.
     * @param typeName the name of the type to reference.
     * @return a psi element holding the properties and name of the type provided.
     */
    @NotNull
    public static ROSMsgType createType(Project project, String typeName) {
        final ROSMsgFile file = createFile(project, typeName + " dummyName");
        return (ROSMsgType) file.getFirstChild().getFirstChild();
    }

    public static ROSMsgLabel createLabel(Project project, String labelName) {
        final ROSMsgFile file = createFile(project, "dummyName " + labelName);
        return (ROSMsgLabel) file.getFirstChild().getLastChild();
    }
}
