// $Header:
// /home/weber/cvsRepos/highlighting/swing/text/highlight/C_Categoriser.java,v
// 1.1
// 2004/09/26 13:36:30 weber Exp $

package swing.text.highlight.categoriser;

import java.text.CharacterIterator;
import java.util.Comparator;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Segment;

import swing.text.highlight.HighlightedDocument;


public class C_Categoriser extends AbstractCategoriser
{
  private static final boolean debug = true;

  public static final class RevStringByLengthComparator implements Comparator
  {
    public int compare( Object o1, Object o2)
    {
      return ((String) o2).length() - ((String) o1).length();
    }
  }

  private static final String[]              kwPredefVal        = { "true",
      "false"                                                  };

  private static final String[]              kwType             = { "char",
      "double", "enum", "float", "int", "long", "short", "signed", "struct",
      "typedef", "union", "unsigned", "void", "auto", "const", "extern",
      "register", "static", "volatile", "far", "huge", "inline", "near",
      "pascal"                                                 };

  private static final String[]              kwStmt             = { "asm",
      "break", "case", "continue", "default", "do", "else", "for", "goto",
      "if", "return", "switch", "while"                        };

  private static final String[]              kwOperator         = { "sizeof" };

  private static RevStringByLengthComparator byLenghtDescending = new RevStringByLengthComparator();

  //  static {
  //    // sort keyword arrays by keyword length (descending)
  //    Arrays.sort( kwPredefVal, byLenghtDescending);
  //    Arrays.sort( kwType, byLenghtDescending);
  //    Arrays.sort( kwStmt, byLenghtDescending);
  //    Arrays.sort( kwOperator, byLenghtDescending);
  //  }

  public C_Categoriser()
  {
  }

  public int getAdjustedStart( HighlightedDocument doc, int offset)
  {
    return offset;
    //return 0;
  }

  public void setInput( Segment input)
  {
    super.setInput( input);
    if (debug) {
      System.out.println( "setInput() char[0]='" + input.array[input.offset]
          + "', offset=" + input.offset + ", count=" + input.count);
    }
    input.first(); // initialize CharIterator
  }

  /**
   * Überschrieben, um
   */
  public Token nextToken( HighlightedDocument doc, Token token)
  {
    if (token == null) {
      token = new Token();
    }

    getToken( token);
    if (debug) {
      // print current token
      Segment txt = new Segment();
      System.out.print( "tok=" + token);
      try {
        doc.getText( token.start, token.length, txt);
        System.out.println( ", '" + txt + "'");
      }
      catch (BadLocationException ex) {
        System.err.println( ex);
      }
    }
    return token;
  }

  /*
   * (non-Javadoc)
   * 
   * @see swing.text.highlight.categoriser.Categoriser#closeInput()
   */
  public void closeInput()
  {
    if (debug) {
      System.out.println( "closeInput ---------------------------");
    }
    input = null;
  }

  /**
   * Überschrieben, um
   */
  public void insertUpdate( Element elem)
  {
    // TODO Auto-generated method stub
    //throw new java.lang.UnsupportedOperationException("insertUpdate not
    // implemented");

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
  private void getToken( Token token)
  {
    token.categoryId = CategoryConstants.NORMAL;
    int matchLen = matchWhitespace();
    consumeChars( matchLen);
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
        token.categoryId = CategoryConstants.STRINGVAL;
      break;

      case '\'':
        // char const. we allow constants of arbitraty length here
        readCharConst();
        token.categoryId = CategoryConstants.STRINGVAL;
      break;

      case '/':
        // comments or operator?
        switch (LA( 1)) {
          case '*':
            boolean isMultiline = readMLComment();
            // TODO makr as multiline
            token.categoryId = CategoryConstants.COMMENT1;
          break;
          case '/':
            readEOLComment();
            token.categoryId = CategoryConstants.COMMENT2;
          break;
          default:
            input.next(); // consume '/'
            token.categoryId = CategoryConstants.OPERATOR;
          break;
        }
      break;
      case '#': // preprocessor directive
        input.next(); // consume '#'
        matchLen = matchWhitespace();
        matchLen += matchWord();
        consumeChars( matchLen);
        token.categoryId = CategoryConstants.KEYWORD2;
      break;

      case ' ':
      case '\t':
      break;
      default:
        //        if ((matchLen = matchWhitespace()) > 0) {
        //          token.categoryId = CategoryConstants.NORMAL;
        //          consumeChars( matchLen);
        //        }
        //        else
        if ((matchLen = matchNumber()) > 0) {
          token.categoryId = CategoryConstants.NUMERICVAL;
          consumeChars( matchLen);
        }
        else if ((matchLen = matchOperator()) > 0) {
          token.categoryId = CategoryConstants.OPERATOR;
          consumeChars( matchLen);
        }
        else {
          // now try to match a keyword
          matchLen = matchWord();
          if (matchLen == 0) {
            /*
             * if we dont get a keyword here, it is any character not handled
             * somewhere above
             */
            // treat as normal text
            token.categoryId = CategoryConstants.NORMAL;
            input.next(); // consume char
            break;
          }

          assert matchLen > 0 : "unrecognized char in scanner: " + LA( 0);
          if (isKW_PredefVal( matchLen)) {
            token.categoryId = CategoryConstants.PREDEFVAL;
            consumeChars( matchLen);
          }
          else if (isKW_Type( matchLen)) {
            token.categoryId = CategoryConstants.TYPE;
            consumeChars( matchLen);
          }
          else if (isKW_stmt( matchLen)) {
            token.categoryId = CategoryConstants.KEYWORD1;
            consumeChars( matchLen);
          }
          else if (isIdentifier1( matchLen)) {
            token.categoryId = CategoryConstants.IDENTIFIER1;
            consumeChars( matchLen);
          }
          else if (isIdentifier2( matchLen)) {
            token.categoryId = CategoryConstants.IDENTIFIER2;
            consumeChars( matchLen);
          }
          else {
            // still no category found...
            // treat matched word as normal text
            token.categoryId = CategoryConstants.NORMAL;
            consumeChars( matchLen);
          }
        }
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
   * *
   * 
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
  // helper methods
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
    input.setIndex( len + input.getIndex());// consume match
  }

}