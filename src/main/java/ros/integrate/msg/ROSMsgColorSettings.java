package ros.integrate.msg;

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

public class ROSMsgColorSettings implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Builtin Types", ROSMsgSyntaxHighlighter.KEYTYPE),
            new AttributesDescriptor("Custom Types", ROSMsgSyntaxHighlighter.TYPE),
            new AttributesDescriptor("Field Names", ROSMsgSyntaxHighlighter.NAME),
            new AttributesDescriptor("Numbers", ROSMsgSyntaxHighlighter.NUMBER),
            new AttributesDescriptor("Strings", ROSMsgSyntaxHighlighter.STRING),
            new AttributesDescriptor("Comments", ROSMsgSyntaxHighlighter.COMMENT),
            new AttributesDescriptor("Service Separator", ROSMsgSyntaxHighlighter.SERVICE),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return ROSIcons.MSG_FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new ROSMsgSyntaxHighlighter();
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
        return "ROS Message / Service";
    }
}
