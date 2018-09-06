package ros.integrate.msg.inspection;

import com.intellij.codeInspection.InspectionManager;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.util.SmartList;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.msg.annotate.ROSMsgTypeAnnotator;
import ros.integrate.msg.psi.ROSMsgFile;
import ros.integrate.msg.psi.ROSMsgProperty;

import java.util.List;

public class CamelCaseInspection extends AbstractROSMsgInspection {

    @Contract(pure = true)
    @Nullable
    public static String getUnorthodoxTypeMessage(@NotNull String fieldType, boolean inProject) {
        String camelCase = "([A-Za-z]|([0-9]([A-Z]|$)))*";
        String regex = inProject ? "[A-Z]" + camelCase
                : "([a-zA-Z][a-zA-Z0-9_]*/)?[A-Z]" + camelCase;
        if(!fieldType.matches(regex)) {
            return "Field type is not written in CamelCase";
        }
        return null;
    }

    public ProblemDescriptor[] checkFile(@NotNull PsiFile file, @NotNull final InspectionManager manager, final boolean isOnTheFly) {
        if (!(file instanceof ROSMsgFile)) return null;
        final List<ROSMsgProperty> properties = ((ROSMsgFile)file).getProperties();
        final List<ProblemDescriptor> descriptors = new SmartList<>();
        for (ROSMsgProperty property : properties) {
            if(isSuppressedFor(property)) {continue;}
            ProgressManager.checkCanceled();
            PsiElement custom = property.getType().custom();
            if (custom != null && ROSMsgTypeAnnotator.getIllegalTypeMessage(custom.getText(),false) == null) {
                String message = getUnorthodoxTypeMessage(custom.getText(),false);
                if (message != null) {
                    ProblemDescriptor descriptor = manager.createProblemDescriptor(custom, custom, message,
                            ProblemHighlightType.GENERIC_ERROR_OR_WARNING, isOnTheFly);
                    descriptors.add(descriptor); // TODO add CamelCaser
                }
            }
        }
        return descriptors.toArray(new ProblemDescriptor[0]);
    }

//    private static class DerpQuickFix implements LocalQuickFix {
//
//        @Nls(capitalization = Nls.Capitalization.Sentence)
//        @NotNull
//        @Override
//        public String getFamilyName() {
//            return "Derp";
//        }
//
//        @Override
//        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
//
//        }
//    }

    /*private static class RemoveTrailingSpacesFix implements LocalQuickFix {
        private final boolean myIgnoreVisibleSpaces;

        private RemoveTrailingSpacesFix(boolean ignoreVisibleSpaces) {
            myIgnoreVisibleSpaces = ignoreVisibleSpaces;
        }

        @NotNull
        public String getFamilyName() {
            return "Remove Trailing Spaces";
        }

        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            PsiElement element = descriptor.getPsiElement();
            PsiElement parent = element == null ? null : element.getParent();
            if (!(parent instanceof PropertyImpl)) return;
            TextRange textRange = getTrailingSpaces(element, myIgnoreVisibleSpaces);
            if (textRange != null) {
                Document document = PsiDocumentManager.getInstance(project).getDocument(element.getContainingFile());
                TextRange docRange = textRange.shiftRight(element.getTextRange().getStartOffset());
                document.deleteString(docRange.getStartOffset(), docRange.getEndOffset());
            }
        }
    }*/
}
