package com.perfetto.ros.integrate;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.indexing.FileBasedIndex;
import com.perfetto.ros.integrate.psi.ROSMsgFile;
import com.perfetto.ros.integrate.psi.ROSMsgProperty;
import com.perfetto.ros.integrate.psi.ROSMsgSeparator;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ROSMsgUtil {

    public static int countServiceSeparators(PsiFile file) {
        ROSMsgFile rosMsgFile = (ROSMsgFile) file;
        if (rosMsgFile != null) {
            ROSMsgSeparator[] properties = PsiTreeUtil.getChildrenOfType(rosMsgFile, ROSMsgSeparator.class);
            if (properties != null) {
                return properties.length;
            }
        }
        return 0;
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

    public static List<ROSMsgProperty> findProperties(Project project, String key) {
        List<ROSMsgProperty> result = null;
        Collection<VirtualFile> virtualFiles =
                FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, ROSMsgFileType.INSTANCE,
                        GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            ROSMsgFile rosMsgFile = (ROSMsgFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (rosMsgFile != null) {
                ROSMsgProperty[] properties = PsiTreeUtil.getChildrenOfType(rosMsgFile, ROSMsgProperty.class);
                if (properties != null) {
                    for (ROSMsgProperty property : properties) {
                        if (key.equals(property.getText())) {
                            if (result == null) {
                                result = new ArrayList<>();
                            }
                            result.add(property);
                        }
                    }
                }
            }
        }
        return result != null ? result : Collections.emptyList();
    }

    public static List<ROSMsgProperty> findProperties(Project project) {
        List<ROSMsgProperty> result = new ArrayList<>();
        Collection<VirtualFile> virtualFiles =
                FileBasedIndex.getInstance().getContainingFiles(FileTypeIndex.NAME, ROSMsgFileType.INSTANCE,
                        GlobalSearchScope.allScope(project));
        for (VirtualFile virtualFile : virtualFiles) {
            ROSMsgFile rosMsgFile = (ROSMsgFile) PsiManager.getInstance(project).findFile(virtualFile);
            if (rosMsgFile != null) {
                ROSMsgProperty[] properties = PsiTreeUtil.getChildrenOfType(rosMsgFile, ROSMsgProperty.class);
                if (properties != null) {
                    Collections.addAll(result, properties);
                }
            }
        }
        return result;
    }
}
