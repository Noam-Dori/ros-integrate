package ros.integrate.pkt.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;

import javax.swing.*;
import java.util.Map;

/**
 * implements the color settings tab for packet files (.msg, .srv, .action).
 * It only shows the GUI, the colors are specified in {@link ROSPktSyntaxHighlighter}
 * @author Noam Dori
 */
public class ROSPktColorSettings implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Builtin types", ROSPktSyntaxHighlighter.KEYTYPE),
            new AttributesDescriptor("Custom types", ROSPktSyntaxHighlighter.TYPE),
            new AttributesDescriptor("Field names", ROSPktSyntaxHighlighter.NAME),
            new AttributesDescriptor("Numbers", ROSPktSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Strings", ROSPktSyntaxHighlighter.STRING),
            new AttributesDescriptor("Comments", ROSPktSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Service separator", ROSPktSyntaxHighlighter.SERVICE),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return ROSIcons.MSG_FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new ROSPktSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return """
                Header header
                int8 name
                string[] vector
                int[9] array
                Image image
                int32 const = -100
                string string_const = a really really long constant
                # this is a comment
                time t # This is also a comment.
                ---\s
                # service separator""";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "ROS Packet";
    }
}
