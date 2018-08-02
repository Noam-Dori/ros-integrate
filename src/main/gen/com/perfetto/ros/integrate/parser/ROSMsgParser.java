// This is a generated file. Not intended for manual editing.
package com.perfetto.ros.integrate.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static com.perfetto.ros.integrate.psi.ROSMsgTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ROSMsgParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == CONST) {
      r = const_$(b, 0);
    }
    else if (t == PROPERTY) {
      r = property(b, 0);
    }
    else if (t == SEPARATOR) {
      r = separator(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return rosMsgFile(b, l + 1);
  }

  /* ********************************************************** */
  // NEG_OPERATOR? NUMBER | STRING
  public static boolean const_$(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_$")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, CONST, "<const $>");
    r = const_0(b, l + 1);
    if (!r) r = consumeToken(b, STRING);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // NEG_OPERATOR? NUMBER
  private static boolean const_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = const_0_0(b, l + 1);
    r = r && consumeToken(b, NUMBER);
    exit_section_(b, m, null, r);
    return r;
  }

  // NEG_OPERATOR?
  private static boolean const_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "const_0_0")) return false;
    consumeToken(b, NEG_OPERATOR);
    return true;
  }

  /* ********************************************************** */
  // property|COMMENT|CRLF|separator
  static boolean item_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item_")) return false;
    boolean r;
    r = property(b, l + 1);
    if (!r) r = consumeToken(b, COMMENT);
    if (!r) r = consumeToken(b, CRLF);
    if (!r) r = separator(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // type_ (LBRACKET NUMBER? RBRACKET)? NAME (CONST_ASSIGNER const)?
  public static boolean property(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property")) return false;
    if (!nextTokenIs(b, "<property>", KEYTYPE, TYPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, PROPERTY, "<property>");
    r = type_(b, l + 1);
    r = r && property_1(b, l + 1);
    r = r && consumeToken(b, NAME);
    r = r && property_3(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (LBRACKET NUMBER? RBRACKET)?
  private static boolean property_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_1")) return false;
    property_1_0(b, l + 1);
    return true;
  }

  // LBRACKET NUMBER? RBRACKET
  private static boolean property_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && property_1_0_1(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // NUMBER?
  private static boolean property_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_1_0_1")) return false;
    consumeToken(b, NUMBER);
    return true;
  }

  // (CONST_ASSIGNER const)?
  private static boolean property_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_3")) return false;
    property_3_0(b, l + 1);
    return true;
  }

  // CONST_ASSIGNER const
  private static boolean property_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "property_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, CONST_ASSIGNER);
    r = r && const_$(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // item_*
  static boolean rosMsgFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rosMsgFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!item_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "rosMsgFile", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // SERVICE_SEPERATOR
  public static boolean separator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "separator")) return false;
    if (!nextTokenIs(b, SERVICE_SEPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SERVICE_SEPERATOR);
    exit_section_(b, m, SEPARATOR, r);
    return r;
  }

  /* ********************************************************** */
  // TYPE|KEYTYPE
  static boolean type_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_")) return false;
    if (!nextTokenIs(b, "", KEYTYPE, TYPE)) return false;
    boolean r;
    r = consumeToken(b, TYPE);
    if (!r) r = consumeToken(b, KEYTYPE);
    return r;
  }

}
