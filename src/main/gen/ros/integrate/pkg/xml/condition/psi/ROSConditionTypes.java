// This is a generated file. Not intended for manual editing.
package ros.integrate.pkg.xml.condition.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import ros.integrate.pkg.xml.condition.psi.impl.*;

public interface ROSConditionTypes {

  IElementType ITEM = new ROSConditionElementType("ITEM");
  IElementType LOGIC = new ROSConditionElementType("LOGIC");
  IElementType ORDER = new ROSConditionElementType("ORDER");

  IElementType COMPARISON = new ROSConditionTokenType("COMPARISON");
  IElementType LITERAL = new ROSConditionTokenType("LITERAL");
  IElementType LOGIC_OPERATOR = new ROSConditionTokenType("LOGIC_OPERATOR");
  IElementType LPARENTHESIS = new ROSConditionTokenType("LPARENTHESIS");
  IElementType RPARENTHESIS = new ROSConditionTokenType("RPARENTHESIS");
  IElementType VARIABLE = new ROSConditionTokenType("VARIABLE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
      if (type == ITEM) {
        return new ROSConditionItemImpl(node);
      }
      else if (type == LOGIC) {
        return new ROSConditionLogicImpl(node);
      }
      else if (type == ORDER) {
        return new ROSConditionOrderImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
