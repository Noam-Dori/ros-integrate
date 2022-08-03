package ros.integrate.cmake.highlight;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.apache.groovy.util.Maps;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ros.integrate.cmake.lang.CMakeFileType;

import javax.swing.*;
import java.util.Map;

public class CMakeColorSettings implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Bracket String Markers", CMakeSyntaxHighlighter.BRACKETS),
            new AttributesDescriptor("Strings", CMakeSyntaxHighlighter.STRING),
            new AttributesDescriptor("Quoted String Markers", CMakeSyntaxHighlighter.QUOTE),
            new AttributesDescriptor("Command/Function Declaration", CMakeSyntaxHighlighter.COMMAND_DECLARATION),
            new AttributesDescriptor("Command/Function Call", CMakeSyntaxHighlighter.COMMAND_CALL),
            new AttributesDescriptor("Variable", CMakeSyntaxHighlighter.VARIABLE),
            new AttributesDescriptor("Variable Braces", CMakeSyntaxHighlighter.VARIABLE_BRACES),
            new AttributesDescriptor("Parameter", CMakeSyntaxHighlighter.PARAMETER),
            new AttributesDescriptor("Keywords", CMakeSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Argument Name", CMakeSyntaxHighlighter.ARGUMENT_NAME),
            new AttributesDescriptor("Line Comment", CMakeSyntaxHighlighter.LINE_COMMENT),
            new AttributesDescriptor("Block Comment", CMakeSyntaxHighlighter.BLOCK_COMMENT)
    };

    @Override
    public @Nullable Icon getIcon() {
        return CMakeFileType.INSTANCE.getIcon();
    }

    @Override
    public @NotNull SyntaxHighlighter getHighlighter() {
        return new CMakeSyntaxHighlighter();
    }

    @Override
    public @NotNull String getDemoText() {
        return "<command>cmake_minimum_required</command>(<arg>VERSION</arg> 2.8.11)\n" +
                "<command>project</command>(HELLO)\n" +
                "<line_comment># sub-directory command</line_comment>\n" +
                "<command>add_subdirectory</command>(Hello)\n" +
                "<command>add_subdirectory</command>(Demo)\n" +
                "\n" +
                "<command>add_executable</command>(helloDemo demo.cxx demo_b.cxx)\n" +
                "<command>target_link_libraries</command> (helloDemo <arg>LINK_PUBLIC</arg> Hello)\n" +
                "<key>set</key>(<var>var</var> VALUE) <block_comment>#[[This is a block\n" +
                "                  comment]]</block_comment>\n" +
                "<key>function</key>(<command_def>greet</command_def> <par>name_arg</par>)\n" +
                "   <command>message</command>(\"Hello\" <var_b>${</var_b><par>name_arg</par><var_b>}</var_b>)\n" +
                "<key>endfunction</key>()\n" +
                "\n" +
                "<command>greet</command>([=[John J. McKenzie]=]])\n" +
                "<command>greet</command>(\"mr. \\\"Line\\\" \\\n" +
                "       break\")";
    }

    @Override
    public @Nullable Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        return Maps.of(
                "command_def", CMakeSyntaxHighlighter.COMMAND_DECLARATION,
                "command", CMakeSyntaxHighlighter.COMMAND_CALL,
                "var", CMakeSyntaxHighlighter.VARIABLE,
                "var_b", CMakeSyntaxHighlighter.VARIABLE_BRACES,
                "par", CMakeSyntaxHighlighter.PARAMETER,
                "key", CMakeSyntaxHighlighter.KEYWORD,
                "arg", CMakeSyntaxHighlighter.ARGUMENT_NAME,
                "block_comment", CMakeSyntaxHighlighter.BLOCK_COMMENT,
                "line_comment", CMakeSyntaxHighlighter.LINE_COMMENT);
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

    @Override
    public @NotNull
    String getDisplayName() {
        return "CMake";
    }
}
