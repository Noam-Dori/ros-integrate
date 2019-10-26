package ros.integrate.pkt.psi;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileFactory;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.file.ROSMsgFileType;
import ros.integrate.pkt.file.ROSPktFileType;

/**
 * a utility factory class to generate PSI objects within ROS messages.
 */
public class ROSPktElementFactory {
    public static final String ANNOTATION_PREFIX = "# noinspection ";

    /**
     * creates a dummy packet file
     * @param project the project the field/file belongs to
     * @param text text to parse into PSI
     * @return a pkt file containing the provided text
     */
    @NotNull
    private static ROSMsgFile createFile(Project project, String text) {
        String name = "dummy." + ROSMsgFileType.INSTANCE.getDefaultExtension();
        return (ROSMsgFile) PsiFileFactory.getInstance(project).
                createFileFromText(name, ROSMsgFileType.INSTANCE, text);
    }

    /**
     * creates an actual packet file
     * @param fileName the name of the file without extension
     * @param directory the directory this file should be placed in
     * @param fileType the file type to use. If it does not matter, use {@link ROSMsgFileType#INSTANCE}
     * @return the instance of the file created.
     */
    @NotNull
    public static ROSPktFile createFile(String fileName, @NotNull PsiDirectory directory, @NotNull ROSPktFileType fileType) {
        return (ROSPktFile) directory.createFile(fileName + "." + fileType.getDefaultExtension());
    }

    /**
     * creates an annotation comment
     * @param project the project this belongs to.
     * @param annotationText the text included within the annotation.
     * @return an annotation type comment.
     */
    public static ROSPktComment createAnnotation(Project project, String annotationText) {
        final ROSMsgFile file = createFile(project, ANNOTATION_PREFIX + annotationText);
        return (ROSPktComment) file.getFirstChild().getFirstChild();
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
    public static ROSPktType createType(Project project, String typeName) {
        final ROSMsgFile file = createFile(project, typeName + " dummyName");
        return (ROSPktType) file.getFirstChild().getFirstChild().getFirstChild();
    }

    public static ROSPktLabel createLabel(Project project, String labelName) {
        final ROSMsgFile file = createFile(project, "dummyName " + labelName);
        return (ROSPktLabel) file.getFirstChild().getFirstChild().getLastChild();
    }
}
