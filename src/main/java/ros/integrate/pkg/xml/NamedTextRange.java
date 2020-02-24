package ros.integrate.pkg.xml;

import com.intellij.openapi.util.TextRange;

public class NamedTextRange extends TextRange {
    private final String content;

    public NamedTextRange(int startOffset, int endOffset, String content) {
        super(startOffset, endOffset);
        this.content = content;
    }

    public String getContent() {
        return content;
    }
}
