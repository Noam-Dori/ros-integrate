package ros.integrate.msg;

import com.intellij.codeInspection.SuppressionUtil;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.spellchecker.inspections.CommentSplitter;
import com.intellij.spellchecker.tokenizer.SpellcheckingStrategy;
import com.intellij.spellchecker.tokenizer.TokenConsumer;
import com.intellij.spellchecker.tokenizer.Tokenizer;
import org.jetbrains.annotations.NotNull;
import ros.integrate.msg.psi.ROSMsgComment;

public class ROSMsgSpellcheckingStrategy extends SpellcheckingStrategy {
    protected final Tokenizer<ROSMsgComment> myROSCommentTokenizer = new ROSCommentTokenizer();

    @NotNull
    @Override
    public Tokenizer getTokenizer(PsiElement element) {
        if (element instanceof ROSMsgComment) {
            if (SuppressionUtil.isSuppressionComment(element)) {
                return EMPTY_TOKENIZER;
            }
            return myROSCommentTokenizer;
        }
        return super.getTokenizer(element);
    }

    public static class ROSCommentTokenizer extends Tokenizer<ROSMsgComment> {

        @Override
        public void tokenize(@NotNull ROSMsgComment element, TokenConsumer consumer) {
            // doc-comment chameleon expands as PsiComment inside PsiComment, avoid duplication
            if (element.getParent() instanceof ROSMsgComment) return;

            // remove commenting part
            ROSMsgCommenter commenter = new ROSMsgCommenter();
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
