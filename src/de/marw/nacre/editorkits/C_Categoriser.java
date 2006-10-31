/* $Id$ */

// Copyright � 2004 Martin Weber

package de.marw.javax.swing.text.editorkits;

import java.text.CharacterIterator;

import javax.swing.text.Document;

import de.marw.javax.swing.text.highlight.categoriser.C_likeCategoriser;
import de.marw.javax.swing.text.highlight.categoriser.Category;
import de.marw.javax.swing.text.highlight.categoriser.Token;


/**
 * A source code scanner and token categoriser for the C programming language.
 * In addition to the ANSI standard, this scanner recognizes single line
 * comments as well.
 * <p>
 * In detail, this categoriser maps lexical elements to a
 * {@link de.marw.javax.swing.text.highlight.categoriser.Category Category}</code>
 * as follows: <table border="1">
 * <tr>
 * <th>Category</th>
 * <th>Lexical element</th>
 * </tr>
 * <tr>
 * <td>COMMENT_1</td>
 * <td>Multi-line comments terminated by <code>&#x2f;*</code> and <code>
 * *&#x2f;</code>.</td>
 * </tr>
 * <tr>
 * <td>COMMENT_2</td>
 * <td>Single-line comments starting with <code>&#x2f;&#x2f;</code> and
 * terminated by end of line.</td>
 * </tr>
 * <tr>
 * <td>STRINGVAL</td>
 * <td>String literals enclosed in double quotes as well as character literals
 * enclosed in single quotes.</td>
 * </tr>
 * <tr>
 * <td>NUMERICVAL</td>
 * <td>Number literals: decimal, hexadecimal and decimal floating point
 * numbers. Hexadecimal floating point numbers <code>0x1.fp3</code> currently
 * not supported.</td>
 * </tr>
 * <tr>
 * <td>PREDEFVAL</td>
 * <td>Literal predefined values. These are <code>true</code>, <code>false</code>,
 * <code>__DATE__</code>, <code>__FILE__</code>, <code>__LINE__ </code>,
 * <code>__TIME__</code>.</td>
 * </tr>
 * <tr>
 * <td>KEYWORD</td>
 * <td>Any preprocessor directive.</td>
 * </tr>
 * <tr>
 * <td>KEYWORD_STATEMENT</td>
 * <td>Statement keywords: <code>asm</code>, <code>break</code>, <code>
 * case</code>, <code>continue</code>, <code>default</code>, <code>do
 * </code>, <code>else</code>, <code>for</code>, <code>goto</code>,
 * <code>if</code>, <code>return</code>, <code>switch</code>, <code>
 * while<code>
 * </td>
 * </tr>
 <tr>
 <td>KEYWORD_TYPE</td> <td>Keywords for data types: <code>auto</code>,  
 <code>char</code>, <code>const</code>, <code>double</code>, 
 <code>enum</code>, <code>extern</code>, <code>far</code>, 
 <code>float</code>, <code>huge</code>, <code>inline</code>, 
 <code>int</code>, <code>long</code>, <code>near</code>, 
 <code>pascal</code>,  <code>register</code>, <code>short</code>, 
 <code>signed</code>, <code>static</code>, <code>struct</code>, 
 <code>typedef</code>, <code>union</code>, <code>unsigned</code>, 
 <code>void</code>, <code>volatile</code>.
 </td>
 </tr>
 <tr>
 <td>KEYWORD_OPERATOR</td> <td>Operator keywords: <code>sizeof</code></td>.
 </tr>
 <tr>
 <td>OPERATOR</td> <td>Any punctuation characters except semicolon, parentheses,
 brackets and character or string delemiters.
 </td>
 </tr>
 <tr> <td>LABEL</td> <td>Unused</td> </tr>
 <tr> <td>DOC</td> <td>Unused</td> </tr>
 </table>
 
 * </p>
 * 
 * @author Martin Weber
 */
public class C_Categoriser extends C_likeCategoriser
{

  /**
   * Type keywords.
   */
  private static final String[] kwType = { "auto", "cdecl", "char", "const",
      "double", "enum", "extern", "far", "float", "huge", "inline", "int",
      "long", "near", "pascal", "register", "short", "signed", "static",
      "struct", "typedef", "union", "unsigned", "void", "volatile", };

  /**
   * statement keywords.
   */
  private static final String[] kwStmt = { "asm", "break", "case", "continue",
      "default", "do", "else", "for", "goto", "if", "return", "switch", "while" };

  /**
   * operator keywords.
   */
  private static final String[] kwOperator = { "sizeof" };

  /**
   * predefined constants value keywords.
   */
  private static final String[] kwPredefVal = { "__DATE__", "__FILE__",
      "__LINE__", "__TIME__", "true", "false" };

  /**
   * 
   */
  public C_Categoriser() {
  }

  /*
   * interface Categoriser
   */
  public void nextToken( Document doc, Token token)
  {

    getToken( doc, token);
    if (false) {
      // print current token
      System.out.print( "tok=" + token);
      String txt = new String( input.array, token.start, token.length);
      System.out.println( ", '" + txt + "'");
    }
  }

  // /////////////////////////////////////////////////////////
  // categoriser methods
  // /////////////////////////////////////////////////////////
  /**
   * @param token
   *        the token to initialise.
   */
  private void getToken( Document doc, Token token)
  {
    consumeChars( matchWhitespace());

    token.start = input.getIndex();
    char c = input.current();
    switch (c) {
      case CharacterIterator.DONE:
        // will return a zero length token
        token.length = 0;
        return;
      case '\"':
        // String
        consumeString();
        token.category = Category.STRINGVAL;
      break;

      case '\'':
        // char const. we allow constants of arbitraty length here
        consumeCharConst();
        token.category = Category.STRINGVAL;
      break;

      case '/':
        // comments or operator?
        switch (LA( 1)) {
          case '*': // '/*' comment
            consumeMLComment();
            token.category = Category.COMMENT_1;
            token.multiline = true; // mark as multiline token
          break;
          case '/': // '//' comment
            consumeEOLComment();
            token.category = Category.COMMENT_2;
          break;
          default: // division operator
            input.next(); // consume '/'
            token.category = Category.OPERATOR;
          break;
        }
      break;
      case '#': // preprocessor directive
        input.next(); // consume '#'
        consumeChars( matchWhitespaceNoNL());
        consumeChars( matchWord());
        token.category = Category.KEYWORD;
      break;

      default:
        int matchLen = 0;

        if ((matchLen = matchNumber()) > 0) {
          token.category = Category.NUMERICVAL;
        }
        else if ((matchLen = matchOperator()) > 0) {
          token.category = Category.OPERATOR;
        }
        else {
          // now try to match a keyword
          matchLen = matchWord();
          if (matchLen > 0) {
            if (isKW_PredefVal( matchLen)) {
              token.category = Category.PREDEFVAL;
            }
            else if (isKW_Type( matchLen)) {
              token.category = Category.KEYWORD_TYPE;
            }
            else if (isKW_Statement( matchLen)) {
              token.category = Category.KEYWORD_STATEMENT;
            }
            else if (isKW_Operator( matchLen)) {
              token.category = Category.KEYWORD_OPERATOR;
            }
            else if (isIdentifier1( matchLen)) {
              token.category = Category.IDENTIFIER_1;
            }
            else if (isIdentifier2( matchLen)) {
              token.category = Category.IDENTIFIER_2;
            }
            else {
              // still no category found...
              // treat matched word as normal text
              token.category = null;
            }
          } // matchlen >0
          else {
            /*
             * if we didn't get a keyword here, it is any character not handled
             * somewhere above: treat as normal text
             */
            matchLen = 1; // consume char
            token.category = null;
          }
        }
        consumeChars( matchLen);
      break; // default
    } // switch

    token.length = input.getIndex() - token.start;
  }

  /**
   * Matches the suffix that indicates a floating point numeric literal. <br>
   * FloatSuffix: [fF][lL] | [lL][fF]
   * 
   * @todo hexadecimal floats, e.g. 0x1.fp3
   * @param lookahead
   *        the position ahead of the current index of the input segment. *
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected int matchFloatSuffix( int lookahead)
  {
    int len = 0;
    char c = Character.toUpperCase( LA( lookahead));
    char seen = '\0';
    if (c == 'L' || c == 'F') {
      seen = c;
      len++;
      c = Character.toUpperCase( LA( lookahead + 1));
      if ((c == 'L' || c == 'F') && c != seen) {
        len++;
      }
    }
    return len;
  }

  /**
   * Matches the suffix that indicates an integer numeric literal. <br>
   * IntSuffix: [uU][lL] | [lL][uU]
   * 
   * @param lookahead
   *        the position ahead of the current index of the input segment. *
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected int matchIntSuffix( int lookahead)
  {
    int len = 0;
    char c = Character.toUpperCase( LA( lookahead));
    char seen = '\0';
    if (c == 'L' || c == 'U') {
      seen = c;
      len++;
      c = Character.toUpperCase( LA( lookahead + 1));
      if ((c == 'L' || c == 'U') && c != seen) {
        len++;
      }
    }
    return len;
  }

  /**
   * Matches an operator. This implementation greedyly matches anything that
   * looks like a sequence of operators.
   * 
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected int matchOperator()
  {
    int len = 0;
    // non keyword operators
    // greedyly match anything that looks like a sequence of operators...
    for (char c = LA( len); c != CharacterIterator.DONE; c = LA( ++len)) {
      if (Character.isJavaIdentifierPart( c) || Character.isWhitespace( c)
          || "/;(){}'\"".indexOf( c) >= 0) {
        break;
      }
    }
    return len;
  }

  /**
   * Checks whether a subregion in the <code>input</code> starting at the
   * current scanner input position is a keyword used in statements.
   * 
   * @see Category#KEYWORD_STATEMENT
   * @param length
   *        the length of the region that must match.
   * @return <code>true</code> if the subregion is one of the keywords,
   *         otherwise <code>false</code>.
   */
  protected boolean isKW_Statement( int length)
  {
    return matchOneOfStrings( length, kwStmt);
  }

  /**
   * Checks whether a subregion in the <code>input</code> starting at the
   * current scanner input position is a keyword used for types.
   * 
   * @see Category#KEYWORD_TYPE
   * @param length
   *        the length of the region that must match.
   * @return <code>true</code> if the subregion is one of the keywords,
   *         otherwise <code>false</code>.
   */
  protected boolean isKW_Type( int length)
  {
    return matchOneOfStrings( length, kwType);
  }

  /**
   * Checks whether a subregion in the <code>input</code> starting at the
   * current scanner input position is a keyword used as operator.
   * 
   * @see Category#KEYWORD_OPERATOR
   * @param length
   *        the length of the region that must match.
   * @return <code>true</code> if the subregion is one of the keywords,
   *         otherwise <code>false</code>.
   */
  protected boolean isKW_Operator( int length)
  {
    return matchOneOfStrings( length, kwOperator);
  }

  /**
   * Checks whether a subregion in the <code>input</code> starting at the
   * current scanner input position is a keyword used for predefined value
   * literals.
   * 
   * @see Category#PREDEFVAL
   * @param length
   *        the length of the region that must match.
   * @return <code>true</code> if the subregion is one of the keywords,
   *         otherwise <code>false</code>.
   */
  protected boolean isKW_PredefVal( int length)
  {
    /* C++ kennt true, false */
    return matchOneOfStrings( length, kwPredefVal);
  }

  // /////////////////////////////////////////////////////////
  // categoriser helper methods
  // /////////////////////////////////////////////////////////

  // /////////////////////////////////////////////////////////
  // other helper methods
  // /////////////////////////////////////////////////////////

}