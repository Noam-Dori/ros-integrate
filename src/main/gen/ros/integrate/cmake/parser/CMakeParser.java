// This is a generated file. Not intended for manual editing.
package ros.integrate.cmake.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import static ros.integrate.cmake.psi.CMakeTypes.*;
import static ros.integrate.cmake.parser.impl.CMakeParserUtil.*;
import com.intellij.psi.tree.IElementType;
import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.TokenSet;
import com.intellij.lang.PsiParser;
import com.intellij.lang.LightPsiParser;

@SuppressWarnings({"SimplifiableIfStatement", "UnusedAssignment"})
public class CMakeParser implements PsiParser, LightPsiParser {

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
    return cmakeFile(b, l + 1);
  }

  /* ********************************************************** */
  // argument_element*
  public static boolean argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument")) return false;
    Marker m = enter_section_(b, l, _NONE_, ARGUMENT, "<argument>");
    while (true) {
      int c = current_position_(b);
      if (!argument_element(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "argument", c)) break;
    }
    exit_section_(b, l, m, true, false, null);
    return true;
  }

  /* ********************************************************** */
  // bracket_argument | quoted_argument | unquoted_argument
  static boolean argument_element(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "argument_element")) return false;
    boolean r;
    r = bracket_argument(b, l + 1);
    if (!r) r = quoted_argument(b, l + 1);
    if (!r) r = unquoted_argument(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // BRACKET_OPEN TEXT_ELEMENT* BRACKET_CLOSE
  static boolean bracket_argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_argument")) return false;
    if (!nextTokenIs(b, BRACKET_OPEN)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, BRACKET_OPEN);
    r = r && bracket_argument_1(b, l + 1);
    r = r && consumeToken(b, BRACKET_CLOSE);
    exit_section_(b, m, null, r);
    return r;
  }

  // TEXT_ELEMENT*
  private static boolean bracket_argument_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_argument_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!consumeToken(b, TEXT_ELEMENT)) break;
      if (!empty_element_parsed_guard_(b, "bracket_argument_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // COMMENT_START bracket_argument
  public static boolean bracket_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "bracket_comment")) return false;
    if (!nextTokenIs(b, COMMENT_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, COMMENT_START);
    r = r && bracket_argument(b, l + 1);
    exit_section_(b, m, BRACKET_COMMENT, r);
    return r;
  }

  /* ********************************************************** */
  // file_element*
  static boolean cmakeFile(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "cmakeFile")) return false;
    while (true) {
      int c = current_position_(b);
      if (!file_element(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "cmakeFile", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TEXT_ELEMENT PAREN_OPEN argument PAREN_CLOSE
  public static boolean command(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "command")) return false;
    if (!nextTokenIs(b, TEXT_ELEMENT)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, TEXT_ELEMENT, PAREN_OPEN);
    r = r && argument(b, l + 1);
    r = r && consumeToken(b, PAREN_CLOSE);
    exit_section_(b, m, COMMAND, r);
    return r;
  }

  /* ********************************************************** */
  // function | macro | if_block | while_block | for_block | command | bracket_comment | line_ending | junk
  static boolean file_element(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "file_element")) return false;
    boolean r;
    r = function(b, l + 1);
    if (!r) r = macro(b, l + 1);
    if (!r) r = if_block(b, l + 1);
    if (!r) r = while_block(b, l + 1);
    if (!r) r = for_block(b, l + 1);
    if (!r) r = command(b, l + 1);
    if (!r) r = bracket_comment(b, l + 1);
    if (!r) r = line_ending(b, l + 1);
    if (!r) r = junk(b, l + 1);
    return r;
  }

  /* ********************************************************** */
  // <<namedCommand "foreach">> in_for* <<namedCommand "endforeach">>
  public static boolean for_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_block")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FOR_BLOCK, "<for block>");
    r = namedCommand(b, l + 1, "foreach");
    r = r && for_block_1(b, l + 1);
    r = r && namedCommand(b, l + 1, "endforeach");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // in_for*
  private static boolean for_block_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "for_block_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!in_for(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "for_block_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // <<namedCommand "function">> in_function* <<namedCommand "endfunction">>
  public static boolean function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, FUNCTION, "<function>");
    r = namedCommand(b, l + 1, "function");
    r = r && function_1(b, l + 1);
    r = r && namedCommand(b, l + 1, "endfunction");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // in_function*
  private static boolean function_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "function_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!in_function(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "function_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // <<namedCommand "if">> in_if*
  //             (<<namedCommand "elseif">> in_if*)*
  //             (<<namedCommand "else">> in_else*)?
  //             <<namedCommand "endif">>
  public static boolean if_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_block")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, IF_BLOCK, "<if block>");
    r = namedCommand(b, l + 1, "if");
    r = r && if_block_1(b, l + 1);
    r = r && if_block_2(b, l + 1);
    r = r && if_block_3(b, l + 1);
    r = r && namedCommand(b, l + 1, "endif");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // in_if*
  private static boolean if_block_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_block_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!in_if(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_block_1", c)) break;
    }
    return true;
  }

  // (<<namedCommand "elseif">> in_if*)*
  private static boolean if_block_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_block_2")) return false;
    while (true) {
      int c = current_position_(b);
      if (!if_block_2_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_block_2", c)) break;
    }
    return true;
  }

  // <<namedCommand "elseif">> in_if*
  private static boolean if_block_2_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_block_2_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namedCommand(b, l + 1, "elseif");
    r = r && if_block_2_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // in_if*
  private static boolean if_block_2_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_block_2_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!in_if(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_block_2_0_1", c)) break;
    }
    return true;
  }

  // (<<namedCommand "else">> in_else*)?
  private static boolean if_block_3(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_block_3")) return false;
    if_block_3_0(b, l + 1);
    return true;
  }

  // <<namedCommand "else">> in_else*
  private static boolean if_block_3_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_block_3_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = namedCommand(b, l + 1, "else");
    r = r && if_block_3_0_1(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // in_else*
  private static boolean if_block_3_0_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "if_block_3_0_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!in_else(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "if_block_3_0_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // if_block | while_block | for_block | <<unnamedCommand "else" "endif">> | bracket_comment | line_ending
  static boolean in_else(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_else")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = if_block(b, l + 1);
    if (!r) r = while_block(b, l + 1);
    if (!r) r = for_block(b, l + 1);
    if (!r) r = unnamedCommand(b, l + 1, "else", "endif");
    if (!r) r = bracket_comment(b, l + 1);
    if (!r) r = line_ending(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // if_block | while_block | for_block | <<unnamedCommand "endforeach">> | bracket_comment | line_ending
  static boolean in_for(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_for")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = if_block(b, l + 1);
    if (!r) r = while_block(b, l + 1);
    if (!r) r = for_block(b, l + 1);
    if (!r) r = unnamedCommand(b, l + 1, "endforeach");
    if (!r) r = bracket_comment(b, l + 1);
    if (!r) r = line_ending(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // if_block | while_block | for_block | <<unnamedCommand "endfunction">> | bracket_comment | line_ending
  static boolean in_function(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_function")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = if_block(b, l + 1);
    if (!r) r = while_block(b, l + 1);
    if (!r) r = for_block(b, l + 1);
    if (!r) r = unnamedCommand(b, l + 1, "endfunction");
    if (!r) r = bracket_comment(b, l + 1);
    if (!r) r = line_ending(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // if_block | while_block | for_block | <<unnamedCommand "elseif" "else" "endif">> | bracket_comment | line_ending
  static boolean in_if(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_if")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = if_block(b, l + 1);
    if (!r) r = while_block(b, l + 1);
    if (!r) r = for_block(b, l + 1);
    if (!r) r = unnamedCommand(b, l + 1, "elseif", "else", "endif");
    if (!r) r = bracket_comment(b, l + 1);
    if (!r) r = line_ending(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // if_block | while_block | for_block | <<unnamedCommand "endwhile">> | bracket_comment | line_ending
  static boolean in_while(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "in_while")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = if_block(b, l + 1);
    if (!r) r = while_block(b, l + 1);
    if (!r) r = for_block(b, l + 1);
    if (!r) r = unnamedCommand(b, l + 1, "endwhile");
    if (!r) r = bracket_comment(b, l + 1);
    if (!r) r = line_ending(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  /* ********************************************************** */
  // argument_element+
  public static boolean junk(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "junk")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, JUNK, "<junk>");
    r = argument_element(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!argument_element(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "junk", c)) break;
    }
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  /* ********************************************************** */
  // COMMENT_START TEXT_ELEMENT
  public static boolean line_comment(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_comment")) return false;
    if (!nextTokenIs(b, COMMENT_START)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeTokens(b, 0, COMMENT_START, TEXT_ELEMENT);
    exit_section_(b, m, LINE_COMMENT, r);
    return r;
  }

  /* ********************************************************** */
  // line_comment? NEXTLINE
  static boolean line_ending(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_ending")) return false;
    if (!nextTokenIs(b, "", COMMENT_START, NEXTLINE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = line_ending_0(b, l + 1);
    r = r && consumeToken(b, NEXTLINE);
    exit_section_(b, m, null, r);
    return r;
  }

  // line_comment?
  private static boolean line_ending_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "line_ending_0")) return false;
    line_comment(b, l + 1);
    return true;
  }

  /* ********************************************************** */
  // <<namedCommand "macro">> in_function* <<namedCommand "endmacro">>
  public static boolean macro(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, MACRO, "<macro>");
    r = namedCommand(b, l + 1, "macro");
    r = r && macro_1(b, l + 1);
    r = r && namedCommand(b, l + 1, "endmacro");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // in_function*
  private static boolean macro_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "macro_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!in_function(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "macro_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // QUOTE quoted_element* QUOTE
  static boolean quoted_argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quoted_argument")) return false;
    if (!nextTokenIs(b, QUOTE)) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, QUOTE);
    r = r && quoted_argument_1(b, l + 1);
    r = r && consumeToken(b, QUOTE);
    exit_section_(b, m, null, r);
    return r;
  }

  // quoted_element*
  private static boolean quoted_argument_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quoted_argument_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!quoted_element(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "quoted_argument_1", c)) break;
    }
    return true;
  }

  /* ********************************************************** */
  // TEXT_ELEMENT | ESCAPE_SEQUENCE | CONTINUATION
  static boolean quoted_element(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "quoted_element")) return false;
    boolean r;
    r = consumeToken(b, TEXT_ELEMENT);
    if (!r) r = consumeToken(b, ESCAPE_SEQUENCE);
    if (!r) r = consumeToken(b, CONTINUATION);
    return r;
  }

  /* ********************************************************** */
  // (bracket_comment | TEXT_ELEMENT | NEXTLINE | ESCAPE_SEQUENCE |
  //                                (PAREN_OPEN argument_element? PAREN_CLOSE?))+
  static boolean unquoted_argument(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unquoted_argument")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = unquoted_argument_0(b, l + 1);
    while (r) {
      int c = current_position_(b);
      if (!unquoted_argument_0(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "unquoted_argument", c)) break;
    }
    exit_section_(b, m, null, r);
    return r;
  }

  // bracket_comment | TEXT_ELEMENT | NEXTLINE | ESCAPE_SEQUENCE |
  //                                (PAREN_OPEN argument_element? PAREN_CLOSE?)
  private static boolean unquoted_argument_0(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unquoted_argument_0")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = bracket_comment(b, l + 1);
    if (!r) r = consumeToken(b, TEXT_ELEMENT);
    if (!r) r = consumeToken(b, NEXTLINE);
    if (!r) r = consumeToken(b, ESCAPE_SEQUENCE);
    if (!r) r = unquoted_argument_0_4(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // PAREN_OPEN argument_element? PAREN_CLOSE?
  private static boolean unquoted_argument_0_4(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unquoted_argument_0_4")) return false;
    boolean r;
    Marker m = enter_section_(b);
    r = consumeToken(b, PAREN_OPEN);
    r = r && unquoted_argument_0_4_1(b, l + 1);
    r = r && unquoted_argument_0_4_2(b, l + 1);
    exit_section_(b, m, null, r);
    return r;
  }

  // argument_element?
  private static boolean unquoted_argument_0_4_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unquoted_argument_0_4_1")) return false;
    argument_element(b, l + 1);
    return true;
  }

  // PAREN_CLOSE?
  private static boolean unquoted_argument_0_4_2(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "unquoted_argument_0_4_2")) return false;
    consumeToken(b, PAREN_CLOSE);
    return true;
  }

  /* ********************************************************** */
  // <<namedCommand "while">> in_while* <<namedCommand "endwhile">>
  public static boolean while_block(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_block")) return false;
    boolean r;
    Marker m = enter_section_(b, l, _NONE_, WHILE_BLOCK, "<while block>");
    r = namedCommand(b, l + 1, "while");
    r = r && while_block_1(b, l + 1);
    r = r && namedCommand(b, l + 1, "endwhile");
    exit_section_(b, l, m, r, false, null);
    return r;
  }

  // in_while*
  private static boolean while_block_1(PsiBuilder b, int l) {
    if (!recursion_guard_(b, l, "while_block_1")) return false;
    while (true) {
      int c = current_position_(b);
      if (!in_while(b, l + 1)) break;
      if (!empty_element_parsed_guard_(b, "while_block_1", c)) break;
    }
    return true;
  }

}
