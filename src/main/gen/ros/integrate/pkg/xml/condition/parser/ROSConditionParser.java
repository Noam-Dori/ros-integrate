// This is a generated file. Not intended for manual editing.
package ros.integrate.pkg.xml.condition.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static ros.integrate.pkg.xml.condition.psi.ROSConditionTypes.*;
import static com.intellij.lang.parser.GeneratedParserUtilBase.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class ROSConditionParser implements PsiParser, LightPsiParser {

  public ASTNode parse(IElementType t, PsiBuilder b) {
    parseLight(t, b);
    return b.getTreeBuilt();
  }

  public void parseLight(IElementType t, PsiBuilder b) {
    boolean r;
    b = adapt_builder_(t, b, this, null);
    Marker m = enter_section_(b, 0, _COLLAPSE_, null);
    r = parse_root_(t, b);
    exit_section_(b, 0, m, t, r, true, TRUE_CONDITION);
  }

  protected boolean parse_root_(IElementType t, PsiBuilder b) {
    return parse_root_(t, b, 0);
  }

  static boolean parse_root_(IElementType t, PsiBuilder b, int l) {
    return rosCondition(b, l + 1);
  }

  /* ********************************************************** */
  // order | item
  static boolean entry_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entry_")) return false;
    boolean r;
    r = order(b, l + 1);
    if (!r) r = item(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // entry_ (logic? entry_)*
  static boolean entry_series_(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entry_series_")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = entry_(b, l + 1);
    r = r && entry_series__1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // (logic? entry_)*
  private static boolean entry_series__1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entry_series__1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!entry_series__1_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "entry_series__1", c)) break;
    }
    return true;
  }

  // logic? entry_
  private static boolean entry_series__1_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entry_series__1_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = entry_series__1_0_0(b, l + 1);
    r = r && entry_(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // logic?
  private static boolean entry_series__1_0_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "entry_series__1_0_0")) return false;
    logic(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // VARIABLE | LITERAL
  public static boolean item(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "item")) return false;
    if (!nextTokenIs(b, "<item>", LITERAL, VARIABLE)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, ITEM, "<item>");
    r = consumeToken(b, VARIABLE);
    if (!r) r = consumeToken(b, LITERAL);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COMPARISON | LOGIC_OPERATOR
  public static boolean logic(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "logic")) return false;
    if (!nextTokenIs(b, "<logic>", COMPARISON, LOGIC_OPERATOR)) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, LOGIC, "<logic>");
    r = consumeToken(b, COMPARISON);
    if (!r) r = consumeToken(b, LOGIC_OPERATOR);
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // LPARENTHESIS entry_series_ RPARENTHESIS
  public static boolean order(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "order")) return false;
    if (!nextTokenIs(b, LPARENTHESIS)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, LPARENTHESIS);
    r = r && entry_series_(b, l + 1);
    r = r && consumeToken(b, RPARENTHESIS);
    exit_section_(b, m, ORDER, r);
    return r;
  }

  /* ********************************************************** */
  // entry_series_
  static boolean rosCondition(PsiBuilder b, int l) {
    return entry_series_(b, l + 1);
  }

}
