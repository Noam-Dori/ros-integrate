// This is a generated file. Not intended for manual editing.
package ros.integrate.cmake.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import ros.integrate.cmake.psi.impl.*;

public interface CMakeTypes {

  IElementType ARGUMENT = new CMakeElementType("ARGUMENT");
  IElementType BRACKET_COMMENT = new CMakeElementType("BRACKET_COMMENT");
  IElementType COMMAND = new CMakeElementType("COMMAND");
  IElementType FOR_BLOCK = new CMakeElementType("FOR_BLOCK");
  IElementType FUNCTION = new CMakeElementType("FUNCTION");
  IElementType IF_BLOCK = new CMakeElementType("IF_BLOCK");
  IElementType JUNK = new CMakeElementType("JUNK");
  IElementType LINE_COMMENT = new CMakeElementType("LINE_COMMENT");
  IElementType MACRO = new CMakeElementType("MACRO");
  IElementType WHILE_BLOCK = new CMakeElementType("WHILE_BLOCK");

  IElementType BRACKET_CLOSE = new CMakeTokenType("BRACKET_CLOSE");
  IElementType BRACKET_OPEN = new CMakeTokenType("BRACKET_OPEN");
  IElementType COMMENT_START = new CMakeTokenType("COMMENT_START");
  IElementType CONTINUATION = new CMakeTokenType("CONTINUATION");
  IElementType ESCAPE_SEQUENCE = new CMakeTokenType("ESCAPE_SEQUENCE");
  IElementType NEXTLINE = new CMakeTokenType("NEXTLINE");
  IElementType PAREN_CLOSE = new CMakeTokenType("PAREN_CLOSE");
  IElementType PAREN_OPEN = new CMakeTokenType("PAREN_OPEN");
  IElementType QUOTE = new CMakeTokenType("QUOTE");
  IElementType TEXT_ELEMENT = new CMakeTokenType("TEXT_ELEMENT");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ARGUMENT) {
        return new CMakeArgumentImpl(node);
      }
      else if (type == BRACKET_COMMENT) {
        return new CMakeBracketCommentImpl(node);
      }
      else if (type == COMMAND) {
        return new CMakeCommandImpl(node);
      }
      else if (type == FOR_BLOCK) {
        return new CMakeForBlockImpl(node);
      }
      else if (type == FUNCTION) {
        return new CMakeFunctionImpl(node);
      }
      else if (type == IF_BLOCK) {
        return new CMakeIfBlockImpl(node);
      }
      else if (type == JUNK) {
        return new CMakeJunkImpl(node);
      }
      else if (type == LINE_COMMENT) {
        return new CMakeLineCommentImpl(node);
      }
      else if (type == MACRO) {
        return new CMakeMacroImpl(node);
      }
      else if (type == WHILE_BLOCK) {
        return new CMakeWhileBlockImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
