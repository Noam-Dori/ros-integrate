package ros.integrate.pkg.xml.condition.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import com.intellij.openapi.util.Pair;
import com.intellij.util.containers.ContainerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.ROSIcons;

import javax.swing.*;
import java.util.Map;

/**
 * implements the color settings tab for ROS conditions. It only shows the GUI, the colors are specified in
 * {@link ROSConditionSyntaxHighlighter}
 * @author Noam Dori
 */
public class ROSConditionColorSettings implements ColorSettingsPage {
    @Nullable
    @Override
    public Icon getIcon() {
        return ROSIcons.CONDITION;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new ROSConditionSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "($ROS_VERSION == 2) or $PYTHON_VERSION >= 3 \n" +
                "<ignored>$IGNORED < 1 and IGNORED != IGNORED</ignored>";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return ContainerUtil.newHashMap(Pair.create("ignored", ROSConditionSyntaxHighlighter.IGNORED));
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return new AttributesDescriptor[] {
                new AttributesDescriptor("Logic Operator", ROSConditionSyntaxHighlighter.LOGIC_OPERATOR),
                new AttributesDescriptor("Comparison Sign", ROSConditionSyntaxHighlighter.COMPARISON),
                new AttributesDescriptor("Parentheses", ROSConditionSyntaxHighlighter.PARENTHESES),
                new AttributesDescriptor("Literal Values", ROSConditionSyntaxHighlighter.LITERAL),
                new AttributesDescriptor("Variables", ROSConditionSyntaxHighlighter.VARIABLE),
                new AttributesDescriptor("Ignored Conditions", ROSConditionSyntaxHighlighter.IGNORED),
        };
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "ROS Condition";
    }
}
