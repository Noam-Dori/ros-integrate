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
            new AttributesDescriptor("Builtin Types", ROSPktSyntaxHighlighter.KEYTYPE),
            new AttributesDescriptor("Custom Types", ROSPktSyntaxHighlighter.TYPE),
            new AttributesDescriptor("Field Names", ROSPktSyntaxHighlighter.NAME),
            new AttributesDescriptor("Numbers", ROSPktSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Strings", ROSPktSyntaxHighlighter.STRING),
            new AttributesDescriptor("Comments", ROSPktSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Service Separator", ROSPktSyntaxHighlighter.SERVICE),
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
        return "Header header\n" +
                "int8 name\n" +
                "string[] vector\n" +
                "int[9] array\n" +
                "Image image\n" +
                "int32 const = -100\n" +
                "string string_const = a really really long constant\n" +
                "# this is a comment\n" +
                "time t # This is also a comment.\n" +
                "--- \n" +
                "# service seperator";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return null;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "ROS Packet";
    }
}
