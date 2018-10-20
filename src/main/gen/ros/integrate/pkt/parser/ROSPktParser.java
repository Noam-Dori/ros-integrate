// This is a generated file. Not intended for manual editing.
package ros.integrate.pkt.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static ros.integrate.pkt.psi.ROSPktTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ROSPktParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    if (t == COMMENT) {
      r = comment(b, 0);
    }
    else if (t == CONST) {
      r = const_$(b, 0);
    }
    else if (t == FIELD) {
      r = field(b, 0);
    }
    else if (t == FIELD_FRAG) {
      r = field_frag(b, 0);
    }
    else if (t == LABEL) {
      r = label(b, 0);
    }
    else if (t == SEPARATOR) {
      r = separator(b, 0);
    }
    else if (t == TYPE) {
      r = type(b, 0);
    }
    else if (t == TYPE_FRAG) {
      r = type_frag(b, 0);
    }
    else {
      r = parse_root_(t, b, 0);
    }
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return rosPktFile(b, l + 1);
  }

  /* ********************************************************** */
  // LINE_COMMENT
  public static boolean comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "comment")) return false;
    if (!nextTokenIs(b, LINE_COMMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LINE_COMMENT);
    exit_section_(b, m, COMMENT, r);
    return r;
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
  // type label
  public static boolean field(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field")) return false;
    if (!nextTokenIs(b, "<field>", CUSTOM_TYPE, KEYTYPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELD, "<field>");
    r = type(b, l + 1);
    r = r && label(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // field_const|field_frag_const|field|field_frag
  static boolean field_component_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_component_")) return false;
    if (!nextTokenIs(b, "", CUSTOM_TYPE, KEYTYPE)) return false;
    boolean r;
    r = field_const(b, l + 1);
    if (!r) r = field_frag_const(b, l + 1);
    if (!r) r = field(b, l + 1);
    if (!r) r = field_frag(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // type label CONST_ASSIGNER const
  public static boolean field_const(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_const")) return false;
    if (!nextTokenIs(b, "<field const>", CUSTOM_TYPE, KEYTYPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELD, "<field const>");
    r = type(b, l + 1);
    r = r && label(b, l + 1);
    r = r && consumeToken(b, CONST_ASSIGNER);
    r = r && const_$(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // type|type_frag
  public static boolean field_frag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_frag")) return false;
    if (!nextTokenIs(b, "<field frag>", CUSTOM_TYPE, KEYTYPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELD_FRAG, "<field frag>");
    r = type(b, l + 1);
    if (!r) r = type_frag(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // (type|type_frag) label (CONST_ASSIGNER | const)
  public static boolean field_frag_const(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_frag_const")) return false;
    if (!nextTokenIs(b, "<field frag const>", CUSTOM_TYPE, KEYTYPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FIELD_FRAG, "<field frag const>");
    r = field_frag_const_0(b, l + 1);
    r = r && label(b, l + 1);
    r = r && field_frag_const_2(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // type|type_frag
  private static boolean field_frag_const_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_frag_const_0")) return false;
    boolean r;
    r = type(b, l + 1);
    if (!r) r = type_frag(b, l + 1);
    return r;
  }

  // CONST_ASSIGNER | const
  private static boolean field_frag_const_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "field_frag_const_2")) return false;
    boolean r;
    r = consumeToken(b, CONST_ASSIGNER);
    if (!r) r = const_$(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // separator|field_component_|comment|CRLF
  static boolean item_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item_")) return false;
    boolean r;
    r = separator(b, l + 1);
    if (!r) r = field_component_(b, l + 1);
    if (!r) r = comment(b, l + 1);
    if (!r) r = consumeToken(b, CRLF);
    return r;
  }

  /* ********************************************************** */
  // NAME
  public static boolean label(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "label")) return false;
    if (!nextTokenIs(b, NAME)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, NAME);
    exit_section_(b, m, LABEL, r);
    return r;
  }

  /* ********************************************************** */
  // item_*
  static boolean rosPktFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "rosPktFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!item_(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "rosPktFile", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // SERVICE_SEPARATOR
  public static boolean separator(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "separator")) return false;
    if (!nextTokenIs(b, SERVICE_SEPARATOR)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, SERVICE_SEPARATOR);
    exit_section_(b, m, SEPARATOR, r);
    return r;
  }

  /* ********************************************************** */
  // type_ (LBRACKET NUMBER? RBRACKET)?
  public static boolean type(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type")) return false;
    if (!nextTokenIs(b, "<type>", CUSTOM_TYPE, KEYTYPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE, "<type>");
    r = type_(b, l + 1);
    r = r && type_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (LBRACKET NUMBER? RBRACKET)?
  private static boolean type_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_1")) return false;
    type_1_0(b, l + 1);
    return true;
  }

  // LBRACKET NUMBER? RBRACKET
  private static boolean type_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && type_1_0_1(b, l + 1);
    r = r && consumeToken(b, RBRACKET);
    exit_section_(b, m, null, r);
    return r;
  }

  // NUMBER?
  private static boolean type_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_1_0_1")) return false;
    consumeToken(b, NUMBER);
    return true;
  }

  /* ********************************************************** */
  // CUSTOM_TYPE|KEYTYPE
  static boolean type_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_")) return false;
    if (!nextTokenIs(b, "", CUSTOM_TYPE, KEYTYPE)) return false;
    boolean r;
    r = consumeToken(b, CUSTOM_TYPE);
    if (!r) r = consumeToken(b, KEYTYPE);
    return r;
  }

  /* ********************************************************** */
  // type_ (LBRACKET NUMBER? RBRACKET?)?
  public static boolean type_frag(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_frag")) return false;
    if (!nextTokenIs(b, "<type frag>", CUSTOM_TYPE, KEYTYPE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, TYPE_FRAG, "<type frag>");
    r = type_(b, l + 1);
    r = r && type_frag_1(b, l + 1);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // (LBRACKET NUMBER? RBRACKET?)?
  private static boolean type_frag_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_frag_1")) return false;
    type_frag_1_0(b, l + 1);
    return true;
  }

  // LBRACKET NUMBER? RBRACKET?
  private static boolean type_frag_1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_frag_1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LBRACKET);
    r = r && type_frag_1_0_1(b, l + 1);
    r = r && type_frag_1_0_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // NUMBER?
  private static boolean type_frag_1_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_frag_1_0_1")) return false;
    consumeToken(b, NUMBER);
    return true;
  }

  // RBRACKET?
  private static boolean type_frag_1_0_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "type_frag_1_0_2")) return false;
    consumeToken(b, RBRACKET);
    return true;
  }

}
