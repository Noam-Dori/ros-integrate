// This is a generated file. Not intended for manual editing.
package com.perfetto.ros.integrate.psi;

import com.intellij.psi.tree.IElementType;
import com.intellij.psi.PsiElement;
import com.intellij.lang.ASTNode;
import com.perfetto.ros.integrate.psi.impl.*;

public interface ROSMsgTypes {

  IElementType CONST = new ROSMsgElementType("CONST");
  IElementType PROPERTY = new ROSMsgElementType("PROPERTY");
  IElementType SEPARATOR = new ROSMsgElementType("SEPARATOR");

  IElementType COMMENT = new ROSMsgTokenType("COMMENT");
  IElementType CONST_ASSIGNER = new ROSMsgTokenType("CONST_ASSIGNER");
  IElementType CRLF = new ROSMsgTokenType("CRLF");
  IElementType KEYTYPE = new ROSMsgTokenType("KEYTYPE");
  IElementType LBRACKET = new ROSMsgTokenType("LBRACKET");
  IElementType NAME = new ROSMsgTokenType("NAME");
  IElementType NEG_OPERATOR = new ROSMsgTokenType("NEG_OPERATOR");
  IElementType NUMBER = new ROSMsgTokenType("NUMBER");
  IElementType RBRACKET = new ROSMsgTokenType("RBRACKET");
  IElementType SERVICE_SEPERATOR = new ROSMsgTokenType("SERVICE_SEPERATOR");
  IElementType STRING = new ROSMsgTokenType("STRING");
  IElementType TYPE = new ROSMsgTokenType("TYPE");

  class Factory {
    public static PsiElement createElement(ASTNode node) {
      IElementType type = node.getElementType();
       if (type == CONST) {
        return new ROSMsgConstImpl(node);
      }
      else if (type == PROPERTY) {
        return new ROSMsgPropertyImpl(node);
      }
      else if (type == SEPARATOR) {
        return new ROSMsgSeparatorImpl(node);
      }
      throw new AssertionError("Unknown element type: " + type);
    }
  }
}
