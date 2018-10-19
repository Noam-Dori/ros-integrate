package ros.integrate.pkt;

import com.intellij.codeInspection.SuppressionUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.inspections.CommentSplitter;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import org.jetbrains.annotations.NotNull;
import ros.integrate.pkt.psi.ROSPktComment;
import ros.integrate.pkt.psi.ROSPktType;

/**
 * a class enabling the spellchecker in ROS messages.
 */
public class ROSPktSpellcheckingStrategy extends SpellcheckingStrategy {
    private final Tokenizer<ROSPktComment> myROSCommentTokenizer = new ROSCommentTokenizer();

    @NotNull
    @Override
    public Tokenizer getTokenizer(PsiElement element) {
        if(element instanceof ROSPktType) {
            return EMPTY_TOKENIZER;
        }
        if (element instanceof ROSPktComment) {
            if (SuppressionUtil.isSuppressionComment(element)) {
                return EMPTY_TOKENIZER;
            }
            return myROSCommentTokenizer;
        }
        return super.getTokenizer(element);
    }

    public static class ROSCommentTokenizer extends Tokenizer<ROSPktComment> {

        @Override
        public void tokenize(@NotNull ROSPktComment element, TokenConsumer consumer) {
            // doc-comment chameleon expands as PsiComment inside PsiComment, avoid duplication
            if (element.getParent() instanceof ROSPktComment) return;

            // remove commenting part
            ROSPktCommenter commenter = new ROSPktCommenter();
            String commentPrefix = commenter.getLineCommentPrefix();
            String textToCheck = element.getText();
            int offset = 0;
            if (commentPrefix != null && textToCheck.matches(commentPrefix + ".*")) { //line comment setup
                textToCheck = textToCheck.substring(commentPrefix.length());
                offset = commentPrefix.length();
            } else { // block comment setup
                commentPrefix = commenter.getBlockCommentPrefix();
                String commentPostfix = commenter.getBlockCommentSuffix();
                if (commentPrefix != null && commentPostfix != null &&
                        textToCheck.matches(commentPrefix + "([^a]|a)*" + commentPostfix)) { // there must be a better regex
                    textToCheck = textToCheck.substring(commentPrefix.length(),textToCheck.length() - commentPostfix.length());
                    offset = commentPrefix.length();
                }
            }


            consumer.consumeToken(element, textToCheck, false, offset, TextRange.allOf(textToCheck), CommentSplitter.getInstance());
        }
    }
}
