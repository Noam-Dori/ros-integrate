package ros.integrate.cmake.folding;

import com.intellij.lang.ASTNode;
import com.intellij.lang.folding.FoldingBuilderEx;
import com.intellij.lang.folding.FoldingDescriptor;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.FoldingGroup;
import com.intellij.openapi.project.DumbAware;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.psi.*;

import java.util.*;

public class CMakeFoldingBuilder extends FoldingBuilderEx implements DumbAware {
    private static final Map<String, List<String>> NEXT_MAP = createNext();

    @NotNull
    private static Map<String, List<String>> createNext() {
        Map<String, List<String>> ret = new HashMap<>();
        ret.put("if", Arrays.asList("endif", "else", "elseif"));
        ret.put("elseif", Arrays.asList("endif", "else", "elseif"));
        ret.put("else", Collections.singletonList("endif"));
        ret.put("endif", Collections.emptyList());
        return ret;
    }

    @Override
    public FoldingDescriptor @NotNull [] buildFoldRegions(@NotNull PsiElement root, @NotNull Document document, boolean quick) {
        // Initialize the list of folding regions
        List<FoldingDescriptor> descriptors = new ArrayList<>();
        // Evaluate the collection
        addFoldingRegion(descriptors, root, FoldingGroup.newGroup("function"), CMakeFunction.class);
        addFoldingRegion(descriptors, root, FoldingGroup.newGroup("macro"), CMakeMacro.class);
        addFoldingRegion(descriptors, root, FoldingGroup.newGroup("for"), CMakeForBlock.class);
        addFoldingRegion(descriptors, root, FoldingGroup.newGroup("while"), CMakeWhileBlock.class);
        for (final CMakeIfBlock block : PsiTreeUtil.findChildrenOfType(root, CMakeIfBlock.class)) {
            List<String> next = NEXT_MAP.get("if");
            CMakeOperation start = block.getStartCommand();
            List<CMakeOperation> ops = block.getCommandList();

            ops.add(block.getEndCommand());
            for (CMakeOperation end : ops) {
                if (end instanceof CMakeCommand && next.contains(end.getFirstChild().getText().toLowerCase())) {
                    descriptors.add(new FoldingDescriptor(block, start.getTextRange().getEndOffset(), end.getTextOffset(), FoldingGroup.newGroup("if"), " ... "));
                    start = end;
                    next = NEXT_MAP.get(end.getFirstChild().getText().toLowerCase());
                }
            }
        }
        return descriptors.toArray(new FoldingDescriptor[0]);
    }

    private static void addFoldingRegion(List<FoldingDescriptor> descriptors, PsiElement root, FoldingGroup group, Class<? extends CMakeBlock> clazz) {
        for (final CMakeBlock block : PsiTreeUtil.findChildrenOfType(root, clazz)) {
            CMakeCommand start = block.getStartCommand();
            CMakeCommand end = block.getEndCommand();
            descriptors.add(new FoldingDescriptor(block, start.getTextRange().getEndOffset(), end.getTextOffset(), group, " ... "));
        }
    }

    @Override
    public @Nullable String getPlaceholderText(@NotNull ASTNode node) {
        return "...";
    }

    @Override
    public boolean isCollapsedByDefault(@NotNull ASTNode node) {
        return false;
    }
}