package ros.integrate.pkt;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.refactoring.move.moveFilesOrDirectories.MoveFileHandler;
import com.intellij.refactoring.util.MoveRenameUsageInfo;
import com.intellij.refactoring.util.TextOccurrencesUtil;
import com.intellij.usageView.UsageInfo;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.ROSPktFieldBase;
import ros.integrate.pkt.psi.ROSPktFile;
import ros.integrate.pkt.psi.ROSPktTypeBase;
import ros.integrate.pkg.ROSPackageManager;
import ros.integrate.pkg.psi.ROSPackage;

import java.util.*;

public class MovePktFileHandler extends MoveFileHandler {
    @Override
    public boolean canProcessElement(PsiFile element) {
        return element instanceof ROSPktFile;
    }

    @Override
    public void prepareMovedFile(PsiFile file, PsiDirectory moveDestination, Map<PsiElement, PsiElement> oldToNewMap) {
        // add mapping from old pkg to new pkg
        ROSPackage oldPkg = pkt(file).getPackage();
        ROSPackage newPkg = file.getProject().getService(ROSPackageManager.class).findPackage(moveDestination);
        oldToNewMap.put(oldPkg, newPkg);
        // trigger references, auto-updating them.
        for (ROSPktFieldBase field : pkt(file).getFields(ROSPktFieldBase.class, true)) {
            ROSPktTypeBase type = field.getTypeBase();
            if (type.custom() != null) {
                PsiElement resolution = type.getReference().resolve();
                if (resolution != null) {
                    pkt(file).setPackage(newPkg);
                    type.getReference().bindToElement(resolution);
                    pkt(file).setPackage(oldPkg);
                }
            }
        }
    }

    @Nullable
    @Override
    public List<UsageInfo> findUsages(PsiFile psiFile, PsiDirectory newParent, boolean searchInComments, boolean searchInNonJavaFiles) {
        List<UsageInfo> ret = new ArrayList<>();
        Set<PsiReference> foundReferences = new HashSet<>();

        GlobalSearchScope projectScope = GlobalSearchScope.projectScope(psiFile.getProject());
        for (PsiReference reference : ReferencesSearch.search(psiFile, projectScope, false)) {
            TextRange range = reference.getRangeInElement();
            if (foundReferences.contains(reference)) {
                continue;
            }
            ret.add(new MoveRenameUsageInfo(reference.getElement(), reference, range.getStartOffset(), range.getEndOffset(), psiFile, false));
            foundReferences.add(reference);
        }

        findNonCodeUsages(searchInComments, searchInNonJavaFiles, psiFile, ret);
        return ret;
    }

    private void findNonCodeUsages(boolean searchInComments, boolean searchInNonCodeFiles,
                                   PsiFile psiFile, List<UsageInfo> results) {
        String qName = pkt(psiFile).getQualifiedName(), name = pkt(psiFile).getPacketName();
        GlobalSearchScope scope = GlobalSearchScope.projectScope(psiFile.getProject());
        TextOccurrencesUtil.findNonCodeUsages(psiFile, scope, qName, searchInComments, searchInNonCodeFiles, qName, results);
        TextOccurrencesUtil.findNonCodeUsages(psiFile, scope, name, searchInComments, searchInNonCodeFiles, qName, results);
    }

    @Override
    public void retargetUsages(List<UsageInfo> usageInfos, Map<PsiElement, PsiElement> oldToNewMap) {
        usageInfos.parallelStream().forEach(use -> {
            PsiElement element = use.getElement();
            PsiReference ref = use.getReference();
            if(element != null && ref != null) {
                ref.handleElementRename(element.getText().replaceAll(".*/", "").replaceAll("\\[]",""));
            }
        });
    }

    @Override
    public void updateMovedFile(PsiFile file) {}

    @Contract(pure = true)
    private static ROSPktFile pkt(PsiFile file) {
        return (ROSPktFile)file;
    }
}
