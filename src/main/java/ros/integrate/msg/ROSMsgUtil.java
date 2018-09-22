package ros.integrate.msg;

import com.google.common.primitives.UnsignedLong;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ROSMsgUtil {
    /**
     * checks of this comment is an annotation.
     * @param comment the comment to check
     * @return {@param comment} if it is an annotation <code>null</code> otherwise.
     */
    @Nullable
    @Contract("null -> null")
    public static ROSMsgComment checkAnnotation(@Nullable PsiElement comment) {
        if(comment instanceof ROSMsgComment && comment.getText().startsWith(ROSMsgElementFactory.ANNOTATION_PREFIX)) {
            return (ROSMsgComment) comment;
        }
        return null;
    }

    /**
     * determines how many service separators are present in the provided file
     * @param file the PsiFile to check
     * @return the number of valid service separators in the file
     */
    public static int countServiceSeparators(@Nullable PsiFile file) {
        ROSMsgFile rosMsgFile = (ROSMsgFile) file;
        if (rosMsgFile != null) {
            ROSMsgSeparator[] fields = PsiTreeUtil.getChildrenOfType(rosMsgFile, ROSMsgSeparator.class);
            if (fields != null) {
                return fields.length;
            }
        }
        return 0;
    }

    /**
     * counts how many times the field name {@param name} appear in the file
     * @param file the file to test
     * @param name the name to search for. should be a non-empty string
     * @return the number of times the field name {@param name} appears in the file.
     */
    public static int countNameInFile(@Nullable PsiFile file, @NotNull String name) {
        ROSMsgFile rosMsgFile = (ROSMsgFile) file;
        int count = 0;
        if (rosMsgFile != null) {
            ROSMsgField[] fields = PsiTreeUtil.getChildrenOfType(rosMsgFile, ROSMsgField.class);
            if (fields != null) {
                for (ROSMsgField field : fields) {
                    if (name.equals(field.getLabel().getText())) {
                        count++;
                    }
                }
            }
        }
        return count;
    }

    /**
     * checks whether of not the field provided is the first definition in the provided file.
     * Note that this can also be used to check whether a field belongs to a msg file.
     * @param file the msg file to use as reference
     * @param field the field to test
     * @return <code>true</code> if {@param field} is the first first defined field in file {@param file},
     *         <code>false</code> otherwise.
     */
    public static boolean isFirstDefinition(@Nullable PsiFile file, @NotNull ROSMsgField field) {
        ROSMsgLabel name = field.getLabel();
        return name.equals(getFirstNameInFile(file, name.getText()));
    }

    /**
     * fetches the first name provided in the file with the name {@param name}
     * @param file the file to check against
     * @param name the field name to search for
     * @return <code>null</code> if a field labeled {@param name} does not exist in the file, otherwise,
     *         the first psi label in the file holding that name.
     */
    @Nullable
    private static ROSMsgLabel getFirstNameInFile(@Nullable PsiFile file, @NotNull String name) {
        ROSMsgField[] fields = PsiTreeUtil.getChildrenOfType(file, ROSMsgField.class);
        if (fields != null) {
            for (ROSMsgField field : fields) {
                if (name.equals(field.getLabel().getText())) {
                    return field.getLabel();
                }
            }
        }
        return null;
    }

    /**
     * fetches all the message file names in the provided project with additional options for filtering
     * @param project the project where to search for all messages
     * @param key if not null, only finds message files that have this name,
     *            otherwise, finds all messages regardless of their name.
     * @param file if null, will search for all files in the project. If not null, the provided file will be excluded from the search.
     * @return a non-null list of strings containing all the message name found with the query.
     */
    @NotNull
    public static List<String> findProjectMsgNames(@NotNull Project project, @Nullable String key, @Nullable VirtualFile file) {
        List<String> result = new ArrayList<>();
        findProjectMsgLocations(project, key, file).forEach(
                location->result.add(location.getName())
        );
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    /**
     * see {@link ROSMsgUtil#findProjectMsgNames(Project,String,VirtualFile)}
     * in it, key and file are provided as null.
     */
    static List<String> findProjectMsgNames(Project project) {
        return findProjectMsgNames(project,null,null);
    }

    /**
     * finds all the message files in the provided project with additional options for filtering
     * @param project the project where to search for all messages
     * @param key if not null, only finds message files that have this name,
     *            otherwise, finds all messages regardless of their name.
     * @param file if null, will search for all files in the project. If not null, the provided file will be excluded from the search.
     * @return a non-null list containing all message files found via the query.
     */
    static List<ROSMsgFile> findProjectMsgLocations(@NotNull Project project, @Nullable String key, @Nullable VirtualFile file) {
        List<ROSMsgFile> result = null;
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(ROSMsgFileType.INSTANCE, GlobalSearchScope.allScope(project));
        if( file != null) {
            virtualFiles.remove(file);
        }
        for (VirtualFile virtualFile : virtualFiles) {
            ROSMsgFile rosMsgFile = (ROSMsgFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (rosMsgFile != null) {
                if (key == null || key.equals(rosMsgFile.getName())) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(rosMsgFile);
                }
            }
        }
        return result != null ? result : Collections.emptyList();
    }

    /**
     * a useful utility function for trimming the .msg or .srv from the message file name.
     * @param name the string holding the message/service file name.
     * @return the trimmed version of the provided string.
     */
    @NotNull
    public static String trimMsgFileName(@NotNull String name) {
        return name.substring(0,name.length() - 4);
    }

    public static List<ROSMsgField> findFields(Project project, String key) {
        List<ROSMsgField> result = null;
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(ROSMsgFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            ROSMsgFile rosMsgFile = (ROSMsgFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (rosMsgFile != null) {
                ROSMsgField[] fields = PsiTreeUtil.getChildrenOfType(rosMsgFile, ROSMsgField.class);
                if (fields != null) {
                    for (ROSMsgField field : fields) {
                        if (key.equals(field.getText())) {
                            if (result == null) {
                                result = new ArrayList<>();
                            }
                            result.add(field);
                        }
                    }
                }
            }
        }
        return result != null ? result : Collections.emptyList();
    }

    public static List<ROSMsgField> findFields(Project project) {
        List<ROSMsgField> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileTypeIndex.getFiles(ROSMsgFileType.INSTANCE, GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            ROSMsgFile rosMsgFile = (ROSMsgFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (rosMsgFile != null) {
                ROSMsgField[] fields = PsiTreeUtil.getChildrenOfType(rosMsgFile, ROSMsgField.class);
                if (fields != null) {
                    Collections.addAll(result, fields);
                }
            }
        }
        return result;
    }
}
