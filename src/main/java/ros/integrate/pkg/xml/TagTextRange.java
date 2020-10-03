package ros.integrate.pkg.xml;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.xml.XmlAttribute;
import com.intellij.psi.xml.XmlTag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * an advanced text range specifically tackling the many text ranges tags have.
 * Provides easy access to the tag, attribute, etc.
 * this class is a text range on its own, and represents the text range of the entire tag
 * @author Noam Dori
 */
public class TagTextRange extends TextRange {
    private static final String VALUE = "=", NAME = "\"";

    private final Map<String, TextRange> trCache = new HashMap<>();

    /**
     * a useful class that helps both index things in the cache effectively and allows for a neat language for lookups.
     */
    public enum Prefix {
        VALUE('='),
        NAME('\"');

        private final char text;

        /**
         * construct a new prefix
         * @param text the char used as prefix
         */
        Prefix(char text) {
            this.text = text;
        }

        /**
         * @return the prefix itself
         */
        char get() {
            return text;
        }
    }

    /**
     * construct a new tag text range from an actual tag
     * @param tag the tag to analyze and extract a bunch of text ranges from
     */
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

    /**
     * convert a text range to a tag text range.
     * It does not have all the fancy features since it was not able to analyze any tag.
     * @param simpleTr the text range to convert
     */
    public TagTextRange(@NotNull TextRange simpleTr) {
        super(simpleTr.getStartOffset(), simpleTr.getEndOffset());
    }

    /**
     * construct a simplified text range within the tag text range.
     * It does not have all the fancy features since it was not able to analyze any tag.
     * @param startOffset the starting offset of the range
     * @param endOffset the ending offset of the range
     */
    public TagTextRange(int startOffset, int endOffset) {
        super(startOffset, endOffset);
    }

    /**
     * gets the text range of an entire attribute, queried by the name of said attribute
     * @param attrName the name of the attribute to find
     * @return the text range of the attribute queried
     */
    @NotNull
    public TextRange attr(String attrName) {
        return trCache.getOrDefault(attrName, this);
    }

    /**
     * gets the text range of the name part of an attribute, queried by the name of said attribute
     * @param attrName the name of the attribute to find
     * @return the text range of the name of the attribute queried
     */
    @NotNull
    public TextRange attrName(String attrName) {
        return trCache.getOrDefault(NAME + attrName, attr(attrName));
    }

    /**
     * gets the text range of the value of an attribute, queried by the name of said attribute
     * @param attrName the name of the attribute to find
     * @return the text range of the the value of the attribute queried
     */
    @NotNull
    public TextRange attrValue(String attrName) {
        return trCache.getOrDefault(VALUE + attrName, attr(attrName));
    }

    /**
     * @return the text range of the value of the tag
     */
    @NotNull
    public TextRange value() {
        return trCache.getOrDefault(VALUE, this);
    }

    /**
     * @return the text range of the name of the tag
     */
    public TextRange name() {
        return trCache.getOrDefault(NAME, this);
    }

    /**
     * allows you to get multiple text ranges of multiple attributes
     * @param code the property of the attribute you want to get the text ranges for. Prefix.VALUE for the value part,
     *             Prefix.NAME for the name part, null for the entire attribute
     * @param attrNames a list of the attribute names to search for
     * @return a list of text ranges over a specific property of all successfully found attributes
     */
    @NotNull
    public List<TextRange> attrQuery(@Nullable Prefix code, @NotNull String... attrNames) {
        List<TextRange> ret = new ArrayList<>(attrNames.length);
        for (String name : attrNames) {
            Optional.ofNullable(trCache.get(code == null ? name : code.get() + name)).ifPresent(ret::add);
        }
        return ret;
    }
}
