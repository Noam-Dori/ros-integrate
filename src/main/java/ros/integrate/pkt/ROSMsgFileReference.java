package ros.integrate.pkt;

import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.resolve.reference.impl.providers.PsiFileReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.pkt.psi.*;
import ros.integrate.workspace.ROSPackageManager;
import ros.integrate.workspace.psi.ROSPackage;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * a class defining the references of {@link ros.integrate.pkt.psi.ROSPktTypeBase} to {@link ROSMsgFile}
 */
public class ROSMsgFileReference extends PsiReferenceBase<PsiElement> implements PsiFileReference {
    // note: myElement is the referencing element, and the result of resolve() is the original element (the file).

    @NotNull
    private String msgName;
    @NotNull
    private final String pkgName;
    private final boolean explicitPackage;

    public ROSMsgFileReference(@NotNull ROSPktTypeBase element, @NotNull TextRange textRange) {
        super(element, textRange);
        msgName = element.raw().getText();
        if (msgName.contains("/")) {
            pkgName = msgName.replaceAll("/.*", "");
            msgName = msgName.replaceAll(".*/", "");
            explicitPackage = true;
        } else {
            pkgName = ((ROSPktFile) element.getContainingFile().getOriginalFile()).getPackage().getName();
            explicitPackage = false;
        }
    }

    @NotNull
    @Override
    public ResolveResult[] multiResolve(boolean incompleteCode) {
        Project project = myElement.getProject();
        ROSMsgFile file = ROSPktUtil.findMessage(project, pkgName, msgName);
        if (file == null && isFirstHeader(null)) {
            return new ResolveResult[]{new PsiElementResolveResult(Objects.requireNonNull(
                    ROSPktUtil.findMessage(project, "std_msgs", msgName)))};
        }
        if (file == null || file.equals(myElement.getContainingFile().getOriginalFile())) {
            return ResolveResult.EMPTY_ARRAY;
        }
        return new ResolveResult[]{new PsiElementResolveResult(file)};
    }

    @Nullable
    @Override
    public ROSMsgFile resolve() {
        ResolveResult[] resolveResults = multiResolve(false);
        return resolveResults.length == 1 ? (ROSMsgFile) resolveResults[0].getElement() : null;
    }

    @NotNull
    @Override
    public Object[] getVariants() {
        Project project = myElement.getProject();
        List<LookupElement> variants = new ArrayList<>();
        addMessageVariants(project, variants, null);
        addPackageVariants(project, variants);
        return variants.toArray();
    }

    private void addPackageVariants(@NotNull Project project, List<LookupElement> variants) {
        if (!explicitPackage) {
            for (final ROSPackage pkg : project.getComponent(ROSPackageManager.class).getAllPackages()) {
                if (pkg.getPackets(GlobalSearchScope.allScope(project)).length > 0) {
                    variants.add(LookupElementBuilder.createWithIcon(pkg)
                            .withInsertHandler((context, item) -> addSlash(context, project, pkg))
                    );
                }
            }
        }
    }

    private void addSlash(@NotNull InsertionContext context, @NotNull Project project, @NotNull ROSPackage pkg) {
        CaretModel model = context.getEditor().getCaretModel();
        Document document = context.getDocument();
        final String prevName;

        if(document.getTextLength() > model.getOffset() && document.getText(new TextRange(model.getOffset(),model.getOffset() + 1)).equals("/")) {
            prevName = document.getText(new TextRange(model.getOffset() + 1, model.getVisualLineEnd())).replaceAll("( .*)?\n?","");
            model.getCurrentCaret().moveCaretRelatively(prevName.length() + 1, 0, false, false);
        } else {
            document.insertString(model.getOffset(), "/");
            model.getCurrentCaret().moveCaretRelatively(1, 0, false, false);
            prevName = "";
        }

        List<LookupElement> variants = new ArrayList<>();
        addMessageVariants(project, variants, pkg.getName());

        LookupManager.getInstance(project).showLookup(context.getEditor(), variants.toArray(LookupElement.EMPTY_ARRAY),prevName);
    }

    private void addMessageVariants(@NotNull Project project, List<LookupElement> variants, @Nullable String forcedPkg) {
        PsiFile containingFile = myElement.getContainingFile().getOriginalFile();
        ROSMsgFile thisAsExclude = containingFile instanceof ROSMsgFile ? (ROSMsgFile) containingFile : null;
        final String lookupPkg = forcedPkg != null ? forcedPkg : explicitPackage ? pkgName : null;
        for (final ROSMsgFile msg : ROSPktUtil.findMessages(project, lookupPkg, thisAsExclude)) {
            if (msg.getPacketName().length() > 0) {
                variants.add(LookupElementBuilder.create(msg.getPacketName())
                        .withIcon(msg.getIcon(0))
                        .withTypeText(msg.getQualifiedName())
                        .withInsertHandler((context, item) -> addPackageName(context, msg))
                );
            }
        }
    }

    /**
     * adds completion context by reference.
     * @param context the insertion context pointing to the original ROS pkt file that references another file.
     * @param msg the file that is being referenced.
     */
    private void addPackageName(@NotNull InsertionContext context, @NotNull ROSMsgFile msg) {
        CaretModel model = context.getEditor().getCaretModel();

        model.getCurrentCaret().moveCaretRelatively(-msg.getPacketName().length(), 0, false, false);
        // remove prev. package references if they exist
        context.getDocument().deleteString(model.getVisualLineStart(), model.getOffset());
        // add new ref.
        if (!msg.getPackage().getName().equals(((ROSPktFile) context.getFile()).getPackage().getName()) &&
            !isFirstHeader(msg)) {
            context.getDocument().insertString(model.getOffset(), msg.getPackage().getName() + "/");
        }
        model.getCurrentCaret().moveCaretRelatively(msg.getQualifiedName().length(), 0, false, false);
    }

    /**
     * checks if this reference element is the first header in the document.
     * @param msg the referenced file. If null, no information about the referenced file is available.
     * @return true if the current element is the first header in the file, false otherwise.
     */
    private boolean isFirstHeader(@Nullable ROSMsgFile msg) {
        if (msg != null && !msg.getQualifiedName().equals("std_msgs/Header")) {
            return false;
        }
        if (msg == null && !msgName.equals("Header")) {
            return false;
        }
        return myElement.getParent().equals(
                ((ROSPktTypeBase)myElement).getContainingSection()
                        .getFields(ROSPktFieldBase.class, false).get(0));
    }

    @Override
    public PsiElement handleElementRename(@NotNull String newElementName) throws IncorrectOperationException {
        // check if reference is in the same package as target.
        ROSPktFile referencer = (ROSPktFile) myElement.getContainingFile().getOriginalFile();
        String pkg, pkt;
        if (newElementName.contains("/")) {
            pkg = newElementName.replaceAll("/.*", "");
            pkt = newElementName.replaceAll(".*/", "");
        } else {
            pkg = pkgName;
            pkt = newElementName;
        }
        if (referencer.getPackage().getName().equals(pkg)) {
            return super.handleElementRename(pkt);
        } else {
            return super.handleElementRename(pkg + "/" + pkt);
        }
    }

    @Override
    public PsiElement bindToElement(@NotNull PsiElement element) throws IncorrectOperationException {
        if (!(element instanceof ROSMsgFile)) {
            throw new IncorrectOperationException("Cannot bind to " + element);
        }
        return handleElementRename(((ROSMsgFile) element).getQualifiedName());
    }
}
