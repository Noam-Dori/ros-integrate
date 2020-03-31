package ros.integrate.pkg.xml.completion;

import com.intellij.codeInsight.completion.InsertHandler;
import com.intellij.codeInsight.completion.InsertionContext;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.util.TextRange;
import org.jetbrains.annotations.NotNull;

public class EmptyTagHandler implements InsertHandler<LookupElement> {
    @Override
    public void handleInsert(@NotNull InsertionContext context, @NotNull LookupElement item) {
        CaretModel model = context.getEditor().getCaretModel();
        Document doc = context.getDocument();
        int offset = model.getOffset();

        String text = doc.getTextLength() == offset ? "" : doc.getText(new TextRange(offset, offset + 1)),
                insert = "/>";
        if ("\r\n".contains(text)) {
            doc.insertString(offset, insert);
        }

        model.moveCaretRelatively(2, 0, false, false, false);
    }
}
