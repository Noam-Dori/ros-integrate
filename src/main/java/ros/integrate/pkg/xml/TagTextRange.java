package ros.integrate.pkg.xml;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class TagTextRange extends TextRange {
    private static final String VALUE = "=", NAME = "\"";

    private Map<String, TextRange> trCache = new HashMap<>();

    public enum Prefix {
        VALUE('='),
        NAME('\"');

        private char text;
        Prefix(char text) {
            this.text = text;
        }

        char get() {
            return text;
        }
    }

    public TagTextRange(@NotNull XmlTag tag) {
        super(tag.getTextOffset(), tag.getTextOffset() + tag.getTextLength());
        for (XmlAttribute attr : tag.getAttributes()) {
            trCache.put(attr.getName(), attr.getTextRange());
            if (attr.getValue() != null) {
                trCache.put(VALUE + attr.getName(), attr.getValueTextRange());
            }
            trCache.put(NAME + attr.getName(), new TextRange(attr.getTextOffset(),
                    attr.getTextOffset() + attr.getName().length()));
        }
        trCache.put(NAME, new TextRange(tag.getTextOffset() + 1, tag.getTextOffset() + tag.getName().length() + 1));
        if (!tag.getValue().getText().isEmpty()) {
            trCache.put(VALUE, tag.getValue().getTextRange());
        }
    }

    public TagTextRange(@NotNull TextRange simpleTr) {
        super(simpleTr.getStartOffset(), simpleTr.getEndOffset());
    }

    public TagTextRange(int startOffset, int endOffset) {
        super(startOffset, endOffset);
    }

    @NotNull
    public TextRange attr(String attrName) {
        return trCache.getOrDefault(attrName, this);
    }

    @NotNull
    public TextRange attrName(String attrName) {
        return trCache.getOrDefault(VALUE + attrName, attr(attrName));
    }

    @NotNull
    public TextRange attrValue(String attrName) {
        return trCache.getOrDefault(NAME + attrName, attr(attrName));
    }

    @NotNull
    public TextRange value() {
        return trCache.getOrDefault(VALUE, this);
    }

    public TextRange name() {
        return trCache.getOrDefault(NAME, this);
    }

    @NotNull
    public List<TextRange> attrQuery(@Nullable Prefix code, @NotNull String... attrNames) {
        List<TextRange> ret = new ArrayList<>(attrNames.length);
        for (String name : attrNames) {
            Optional.ofNullable(trCache.get(code == null ? name : code.get() + name)).ifPresent(ret::add);
        }
        return ret;
    }
}
