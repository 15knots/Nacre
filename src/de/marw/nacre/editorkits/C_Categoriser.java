// $Id$

package swing.text.highlight.categoriser;

import java.text.CharacterIterator;
import java.util.Comparator;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Segment;

import swing.text.highlight.Category;
import swing.text.highlight.HighlightedDocument;


public class C_Categoriser extends AbstractCategoriser
{
  public static final class RevStringByLengthComparator implements Comparator
  {
    public int compare( Object o1, Object o2)
    {
      return ((String) o2).length() - ((String) o1).length();
    }
  }

  private static final String[] kwPredefVal = { "true", "false" };

  private static final String[] kwType = { "char", "double", "enum", "float",
      "int", "long", "short", "signed", "struct", "typedef", "union",
      "unsigned", "void", "auto", "const", "extern", "register", "static",
      "volatile", "far", "huge", "inline", "near", "pascal" };

  private static final String[] kwStmt = { "asm", "break", "case", "continue",
      "default", "do", "else", "for", "goto", "if", "return", "switch", "while" };

  private static final String[] kwOperator = { "sizeof" };

  /**
   * 
   */
  public C_Categoriser()
  {
  }

  public void openInput( HighlightedDocument doc, Segment lexerInput)
      throws BadLocationException
  {
    super.openInput( doc, lexerInput);

    if (debug) {
      System.out.println( "setInput() char[0]='"
          + lexerInput.array[lexerInput.offset] + "', offset="
          + lexerInput.offset + ", count=" + lexerInput.count);
    }
    lexerInput.first(); // initialize CharIterator
  }

  /**
   * Überschrieben, um
   */
  public Token nextToken( HighlightedDocument doc, Token token)
  {
    if (token == null) {
      token = new Token();
    }

    getToken( doc, token);
    if (debug) {
      // print current token
      System.out.print( "tok=" + token);
      String txt = new String( input.array, token.start, token.length);
      System.out.println( ", '" + txt + "'");
    }
    return token;
  }

  /**
   * Überschrieben, um
   */
  public void insertUpdate( Element elem)
  {
    // TODO Auto-generated method stub
    // update multiline token marks
    Element root = elem.getDocument().getDefaultRootElement();
  }

  /**
   * Überschrieben, um
   */
  public void removeUpdate( Element line)
  {
    // TODO Auto-generated method stub
    //throw new java.lang.UnsupportedOperationException("removeUpdate not
    // implemented");

  }

  ///////////////////////////////////////////////////////////
  // categoriser methods
  ///////////////////////////////////////////////////////////
  /**
   * @param token
   *          the token to initialise.
   */
  private void getToken( HighlightedDocument doc, Token token)
  {
    token.category = Category.NORMAL;
    consumeChars( matchWhitespace());

    int matchLen = 0;
    token.multiline = false;
    token.start = input.getIndex();
    char c = input.current();
    switch (c) {
      case CharacterIterator.DONE:
        // will return a zero length token
        token.length = 0;
        return;
      case '\"':
        // String
        readString();
        token.category = Category.STRINGVAL;
      break;

      case '\'':
        // char const. we allow constants of arbitraty length here
        readCharConst();
        token.category = Category.STRINGVAL;
      break;

      case '/':
        // comments or operator?
        switch (LA( 1)) {
          case '*':
            readMLComment();
            token.category = Category.COMMENT_1;
            token.multiline = true; // mark as multiline token
          break;
          case '/':
            readEOLComment();
            token.category = Category.COMMENT_2;
          break;
          default:
            input.next(); // consume '/'
            token.category = Category.OPERATOR;
          break;
        }
      break;
      case '#': // preprocessor directive
        input.next(); // consume '#'
        consumeChars( matchWhitespaceNoNL());
        matchLen = matchWord();
        consumeChars( matchLen);
        token.category = Category.KEYWORD;
      break;

      default:
        //        if ((matchLen = matchWhitespace()) > 0) {
        //          token.category = Category.NORMAL;
        //          consumeChars( matchLen);
        //        }
        //        else
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
            else if (isKW_stmt( matchLen)) {
              token.category = Category.KEYWORD_STATEMENT;
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
              token.category = Category.NORMAL;
            }
          } // matchlen >0
          else {
            /*
             * if we didn't get a keyword here, it is any character not handled
             * somewhere above: treat as normal text
             */
            matchLen = 1; // consume char
            token.category = Category.NORMAL;
          }
        }
        consumeChars( matchLen);
      break; // default
    } // switch

    token.length = input.getIndex() - token.start;
  }

  /**
   * @param lenght
   *          the length of the region that must match.
   * @return <code>true</code> if a match was found, otherwise
   *         <code>false</code>.
   */
  private boolean isKW_stmt( int length)
  {
    return matchInWordlist( length, kwStmt);
  }

  /**
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  private int matchNumber()
  {
    // TODO
    return 0; // no match
  }

  /**
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  private int matchOperator()
  {
    int len = 0;
    int kwLen = matchWord();
    if (kwLen > 0) {
      if (matchInWordlist( kwLen, kwOperator)) {
        // got a keyword operator
        len += kwLen;
      }
      else {
        // got a word, but not an operator keyword
        return len;
      }
    }
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
   * @param lenght
   *          the length of the region that must match.
   * @return <code>true</code> if a match was found, otherwise
   *         <code>false</code>.
   */
  private boolean isKW_Type( int length)
  {
    return matchInWordlist( length, kwType);
  }

  /**
   * @param lenght
   *          the length of the region that must match.
   * @return <code>true</code> if a match was found, otherwise
   *         <code>false</code>.
   */
  protected boolean isKW_PredefVal( int length)
  {
    /* C++ kennt true, false */
    return matchInWordlist( length, kwPredefVal);
  }

  /**
   * Looks if a region starting at the current scanner input position equals a
   * String in <code>wordlist</code>.
   * 
   * @param lenght
   *          the length of the region that must match.
   * @param wordlist
   *          the strings that may match.
   * @return <code>true</code> if a match was found, otherwise
   *         <code>false</code>.
   */
  private boolean matchInWordlist( int length, final String[] wordlist)
  {
    for (int i = 0; i < wordlist.length; i++ ) {
      if (wordlist[i].length() == length
          && AbstractCategoriser.regionMatches( false, input, input.getIndex(),
              wordlist[i]) > 0)
        return true;
    }
    return false; // no match

  }

  /**
   * @param identifierLen
   *          the length of the region that must match.
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  private boolean isIdentifier1( int identifierLen)
  {
    // TODO Auto-generated method stub
    /*
     * mittels vom Anwender gefüllter Worttabelle (auc Document-Attribute?)
     * matchen
     */
    return false; // no match
  }

  /**
   * @param identifierLen
   *          the length of the region that must match.
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  private boolean isIdentifier2( int identifierLen)
  {
    // TODO Auto-generated method stub
    /*
     * mittels vom Anwender gefüllter Worttabelle (auc Document-Attribute?)
     * matchen
     */
    return false; // no match
  }

  /**
   * Matches an Identifer or a keyword.
   * 
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  private int matchWord()
  {
    int len = 0;
    char c = LA( len);
    if (Character.isJavaIdentifierStart( c)) {
      c = LA( ++len);
      while (Character.isJavaIdentifierPart( c)) {
        c = LA( ++len);
      }
    }
    return len;
  }

  /**
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  private int matchWhitespace()
  {
    int len = 0;
    char c = LA( len);
    // match WS until end of line..
    while (Character.isWhitespace( c) /* && c !='\n' */) {
      c = LA( ++len);
    }
    return len;
  }

  /**
   * Matches WS until end of line.
   * 
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  private int matchWhitespaceNoNL()
  {
    int len;
    // match WS until end of line..
    for (len = 0; Character.isWhitespace( LA( len)); len++ ) {
    }
    return len;
  }

  /**
   * 
   */
  private void readCharConst()
  {
    char c = input.next();
    for (; c != CharacterIterator.DONE; c = input.next()) {
      switch (c) {
        case '\\':
          input.next();
        break;
        case '\'':
        case '\n':
          input.next();
          return;
      }
    }
  }

  /**
   * @return <code>true</code> if the comment spans multiple lines.
   */
  private boolean readMLComment()
  {
    boolean isMultiline = false;

    input.next(); // consume '/'
    char c = input.next(); // consume '*'
    for (; c != CharacterIterator.DONE; c = input.next()) {
      switch (c) {
        case '*':
          if (LA( 1) == '/') {
            input.next(); // consume '*'
            input.next(); // consume '/'
            return isMultiline;
          }
        break;
        case '\n':
          isMultiline = true;
        break;
      }
    }
    return isMultiline;
  }

  /**
   */
  private void readEOLComment()
  {

    input.next(); // consume '/'
    char c = input.next(); // consume '/'
    for (; c != CharacterIterator.DONE; c = input.next()) {
      switch (c) {
        case '\n':
          input.next(); // consume '/'
          return;
      }
    }
    return;
  }

  /**
   * 
   */
  private void readString()
  {
    char c = input.next();
    for (; c != CharacterIterator.DONE; c = input.next()) {
      switch (c) {
        case '\\':
          input.next();
        break;
        case '\"':
        case '\n':
          input.next();
          return;
      }
    }
  }

  ///////////////////////////////////////////////////////////
  // categoriser helper methods
  ///////////////////////////////////////////////////////////

  private char LA( int lookAhead)
  {
    int offset = input.getIndex();
    if (offset + lookAhead >= input.offset + input.count) {
      return CharacterIterator.DONE;
    }
    return input.array[lookAhead + offset];
  }

  /**
   * @param len
   */
  private void consumeChars( int len)
  {
    input.setIndex( len + input.getIndex());
  }

  ///////////////////////////////////////////////////////////
  // other helper methods
  ///////////////////////////////////////////////////////////
}