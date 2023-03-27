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
            new AttributesDescriptor("Bracket string markers", CMakeSyntaxHighlighter.BRACKETS),
            new AttributesDescriptor("Strings", CMakeSyntaxHighlighter.STRING),
            new AttributesDescriptor("Quoted string markers", CMakeSyntaxHighlighter.QUOTE),
            new AttributesDescriptor("Command/function declaration", CMakeSyntaxHighlighter.COMMAND_DECLARATION),
            new AttributesDescriptor("Command/function call", CMakeSyntaxHighlighter.COMMAND_CALL),
            new AttributesDescriptor("Variable", CMakeSyntaxHighlighter.VARIABLE),
            new AttributesDescriptor("Variable braces", CMakeSyntaxHighlighter.VARIABLE_BRACES),
            new AttributesDescriptor("Parameter", CMakeSyntaxHighlighter.PARAMETER),
            new AttributesDescriptor("Keywords", CMakeSyntaxHighlighter.KEYWORD),
            new AttributesDescriptor("Argument name", CMakeSyntaxHighlighter.ARGUMENT_NAME),
            new AttributesDescriptor("Line comment", CMakeSyntaxHighlighter.LINE_COMMENT),
            new AttributesDescriptor("Block comment", CMakeSyntaxHighlighter.BLOCK_COMMENT)
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
        return """
                <command>cmake_minimum_required</command>(<arg>VERSION</arg> 2.8.11)
                <command>project</command>(HELLO)
                <line_comment># sub-directory command</line_comment>
                <command>add_subdirectory</command>(Hello)
                <command>add_subdirectory</command>(Demo)

                <command>add_executable</command>(helloDemo demo.cxx demo_b.cxx)
                <command>target_link_libraries</command> (helloDemo <arg>LINK_PUBLIC</arg> Hello)
                <key>set</key>(<var>var</var> VALUE) <block_comment>#[[This is a block
                                  comment]]</block_comment>
                <key>function</key>(<command_def>greet</command_def> <par>name_arg</par>)
                   <command>message</command>("Hello" <var_b>${</var_b><par>name_arg</par><var_b>}</var_b>)
                <key>endfunction</key>()

                <command>greet</command>([=[John J. McKenzie]=]])
                <command>greet</command>("mr. \\"Line\\" \\
                       break")""";
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

    @Override
    public AttributesDescriptor @NotNull [] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @Override
    public ColorDescriptor @NotNull [] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @Override
    public @NotNull String getDisplayName() {
        return "CMake";
    }
}
