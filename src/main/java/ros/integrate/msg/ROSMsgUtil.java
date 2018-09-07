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
import com.intellij.util.indexing.FileBasedIndex;
import ros.integrate.msg.psi.*;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;

public class ROSMsgUtil {
    @Contract("null -> null")
    public static ROSMsgComment checkAnnotation(PsiElement field) {
        if(field instanceof ROSMsgComment && field.getText().startsWith(ROSMsgElementFactory.ANNOTATION_PREFIX)) {
            return (ROSMsgComment) field;
        }
        return null;
    }

    public static int countServiceSeparators(PsiFile file) {
        ROSMsgFile rosMsgFile = (ROSMsgFile) file;
        if (rosMsgFile != null) {
            ROSMsgSeparator[] fields = PsiTreeUtil.getChildrenOfType(rosMsgFile, ROSMsgSeparator.class);
            if (fields != null) {
                return fields.length;
            }
        }
        return 0;
    }

    public static int countNameInFile(PsiFile file, String name) {
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


    public static boolean isFirstDefinition(PsiFile file, @NotNull ROSMsgField field) {
        ROSMsgLabel name = field.getLabel();
        return name.equals(Objects.requireNonNull(getFirstNameInFile(file, name.getText())));
    }

    @Nullable
    private static PsiElement getFirstNameInFile(PsiFile file, String name) {
        ROSMsgField[] fields = PsiTreeUtil.getChildrenOfType(file, ROSMsgField.class);
        if (fields != null) {
            for (ROSMsgField field : fields) {
                if (name.equals(field.getLabel().getText())) {
                    field.getLabel();
                }
            }
        }
        return null;
    }

    public static List<String> findProjectMsgNames(Project project, @Nullable String key, @Nullable VirtualFile file) {
        List<String> result = new ArrayList<>();
        findProjectMsgLocations(project, key, file).forEach(
                location->result.add(trimMsgFileName(location.getVirtualFile().getName()))
        );
        return result.isEmpty() ? Collections.emptyList() : result;
    }

    public static List<String> findProjectMsgNames(Project project) {
        return findProjectMsgNames(project,null,null);
    }

    public static List<ROSMsgFile> findProjectMsgLocations(Project project, @Nullable String key, @Nullable VirtualFile file) {
        List<ROSMsgFile> result = null;
        Collection<VirtualFile> virtualFiles =
                FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, ROSMsgFileType.INSTANCE,
                        GlobalSearchScope.allScope(project));
        if( file != null) {
            virtualFiles.remove(file);
        }
        for (VirtualFile virtualFile : virtualFiles) {
            ROSMsgFile rosMsgFile = (ROSMsgFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (rosMsgFile != null) {
                if (key == null || key.equals(trimMsgFileName(virtualFile.getName()))) {
                    if (result == null) {
                        result = new ArrayList<>();
                    }
                    result.add(rosMsgFile);
                }
            }
        }
        return result != null ? result : Collections.emptyList();
    }

    @NotNull
    public static String trimMsgFileName(@NotNull String name) {
        return name.substring(0,name.length() - 4);
    }

    public static List<ROSMsgField> findFields(Project project, String key) {
        List<ROSMsgField> result = null;
        Collection<VirtualFile> virtualFiles =
                FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, ROSMsgFileType.INSTANCE,
                        GlobalSearchScope.allScope(project));
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
                FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, ROSMsgFileType.INSTANCE,
                        GlobalSearchScope.allScope(project));
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


    @Contract("null -> null")
    public static PsiElement getBestFit(ROSMsgConst msgConst) {
        if (msgConst == null) {
            return null;
        }
        String num = msgConst.getText();
        try {
            if (num.contains(".")) { // floating-point
                double floaty = Double.parseDouble(num);
                if ((double) (float) floaty == floaty) {
                    return ROSMsgElementFactory.createType(msgConst.getProject(), "float32");
                } else {
                    return ROSMsgElementFactory.createType(msgConst.getProject(), "float64");
                }
            } else { // integral
                if (num.contains("-")) { // int
                    long integral = Long.parseLong(num);
                    if ((long) (byte) integral == integral) {
                        return ROSMsgElementFactory.createType(msgConst.getProject(), "int8");
                    }
                    if ((long) (short) integral == integral) {
                        return ROSMsgElementFactory.createType(msgConst.getProject(), "int16");
                    }
                    if ((long) (int) integral == integral) {
                        return ROSMsgElementFactory.createType(msgConst.getProject(), "int32");
                    }
                    return ROSMsgElementFactory.createType(msgConst.getProject(), "int64");
                } else { // uint
                    UnsignedLong integral = UnsignedLong.valueOf(num);
                    if (integral.byteValue() == 0 || integral.byteValue() == 1) {
                        return ROSMsgElementFactory.createType(msgConst.getProject(), "bool");
                    }
                    if (integral.compareTo(UnsignedLong.valueOf((long)Byte.MAX_VALUE - Byte.MIN_VALUE)) < 0) {
                        return ROSMsgElementFactory.createType(msgConst.getProject(), "uint8");
                    }
                    if (integral.compareTo(UnsignedLong.valueOf((long)Short.MAX_VALUE - Short.MIN_VALUE)) < 0) {
                        return ROSMsgElementFactory.createType(msgConst.getProject(), "uint16");
                    }
                    if (integral.compareTo(UnsignedLong.valueOf((long)Integer.MAX_VALUE - Integer.MIN_VALUE)) < 0) {
                        return ROSMsgElementFactory.createType(msgConst.getProject(), "uint32");
                    }
                    return ROSMsgElementFactory.createType(msgConst.getProject(), "uint64");
                }
            }
        } catch (NumberFormatException e) {
            return ROSMsgElementFactory.createType(msgConst.getProject(),"string");
        }
    }
}
