// $Header$

package swing.text.highlight;

import java.text.CharacterIterator;

import javax.swing.text.Element;
import javax.swing.text.Segment;

import swing.text.highlight.categoriser.AbstractCategoriser;
import swing.text.highlight.categoriser.CategoryConstants;
import swing.text.highlight.categoriser.Token;


public class C_Tokeniser extends AbstractCategoriser
{

  private static final String[] kwPredefVal = { "true", "false" };

  private static final String[] kwType      = { "char", "double", "enum",
      "float", "int", "long", "short", "signed", "struct", "typedef", "union",
      "unsigned", "void", "auto", "const", "extern", "register", "static",
      "volatile", "far", "huge", "inline", "near", "pascal" };

  private static final String[] kwStmt      = { "asm", "break", "case",
      "continue", "default", "do", "else", "for", "goto", "if", "return",
      "switch", "while"                    };

  private static final String[] kwOperator  = { "sizeof" };

  C_Tokeniser()
  {
  }

  public void setInput( Segment input)
  {
    super.setInput( input);
    input.setIndex( input.getBeginIndex()); // initialize CharIterator
  }

  /**
   * Überschrieben, um
   */
  public Token nextToken( HighlightedDocument doc, Token token)
  {
    if (token == null) {
      token = new Token();
    }

    token.categoryId = CategoryConstants.NORMAL;
    char c;
    matchToken: for (; true; c = input.next(), token.start = input.getIndex()) {

      consumeChars( matchWhitespace());
      token.start = input.getIndex();
      c = input.current();
      int matchLen;

      switch (c) {
        case CharacterIterator.DONE:
        // will return a zero length token
        break matchToken;
        case '\"':
          // String
          readString();
          token.categoryId = CategoryConstants.STRINGVAL;
        break matchToken;

        case '\'':
          // char const. we allow constants of arbitraty length here
          readCharConst();
          token.categoryId = CategoryConstants.STRINGVAL;
        break matchToken;

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
        break matchToken;
        case '#': // preprocessor directive
          input.next(); // consume '#'
          matchLen = matchWhitespace();
          matchLen += matchWord();
          consumeChars( matchLen);
          token.categoryId = CategoryConstants.KEYWORD2;
        break matchToken;

        default:
          if ((matchLen = matchNumber()) > 0) {
            token.categoryId = CategoryConstants.NUMERICVAL;
            consumeChars( matchLen);
            break matchToken;
          }
          else if ((matchLen = matchKW_PredefVal()) > 0) {
            token.categoryId = CategoryConstants.PREDEFVAL;
            consumeChars( matchLen);
            break matchToken;
          }
          else if ((matchLen = matchOperator()) > 0) {
            token.categoryId = CategoryConstants.OPERATOR;
            consumeChars( matchLen);
            break matchToken;
          }
          else if ((matchLen = matchKW_Type()) > 0) {
            token.categoryId = CategoryConstants.TYPE;
            consumeChars( matchLen);
            break matchToken;
          }
          else if ((matchLen = matchKW_stmt()) > 0) {
            token.categoryId = CategoryConstants.IDENTIFIER1;
            consumeChars( matchLen);
            break matchToken;
          }
          else if ((matchLen = matchIdentifier1()) > 0) {
            token.categoryId = CategoryConstants.IDENTIFIER1;
            consumeChars( matchLen);
            break matchToken;
          }
          else if ((matchLen = matchIdentifier1()) > 0) {
            token.categoryId = CategoryConstants.IDENTIFIER1;
            consumeChars( matchLen);
            break matchToken;
          }
          else {
            // still no category found...
            int kk = 0;
            // TODO
          }
      } // switch
    }// matchToken

    token.length = input.getIndex() - token.start;
    return token;
  }

  /**
   * @return
   */
  private int matchKW_stmt()
  {
    return matchInWordlist( kwStmt);
  }

  /**
   * @return the length of the match or <code>0</code> if no match was found.
   */
  private int matchNumber()
  {
    // TODO
    return 0; // no match
  }

  /**
   * @return the length of the match or <code>0</code> if no match was found.
   */
  private int matchOperator()
  {
    // TODO greedy loop implementieren für '+=sizeof--' o.ä.
    int len = matchInWordlist( kwOperator);
    // non keyword operators
    // match anything that looks like a sequence of operators...
    for (char c = LA( len); c != CharacterIterator.DONE; c = LA( ++len)) {
      if (Character.isJavaIdentifierPart( c) || Character.isWhitespace( c)
          || ";()[]{}'".indexOf( c) >= 0) {
        break;
      }
    }
    return len;
  }

  /**
   * @return the length of the match or <code>0</code> if no match was found.
   */
  private int matchKW_Type()
  {
    return matchInWordlist( kwType);
  }

  /**
   * @return the length of the match or <code>0</code> if no match was found.
   */
  protected int matchKW_PredefVal()
  {
    /* C++ kennt true, false */
    return matchInWordlist( kwPredefVal);
  }

  /**
   * Looks if a region starting at the current scanner input position matches a
   * String in <code>wordlist</code>.
   * 
   * @param wordlist
   *          the string that may match.
   * @return the length of the match or <code>0</code> if no match was found.
   */
  private int matchInWordlist( final String[] wordlist)
  {
    for (int i = 0; i < wordlist.length; i++ ) {
      int len;
      if ((len = AbstractCategoriser.regionMatches( false, input, input
          .getIndex(), wordlist[i])) > 0)
        return len;
    }
    return 0; // no match

  }

  /**
   * @return the length of the match or <code>0</code> if no match was found.
   */
  private int matchIdentifier1()
  {
    // TODO Auto-generated method stub
    /*
     * mittels vom Anwender gefüllter Worttabelle (auc Document-Attribute?)
     * matchen
     */
    return 0; // no match
  }

  /**
   * 
   */
  private void readWord()
  {
    char c = input.current();
    if (Character.isJavaIdentifierStart( c)) {
      c = input.next();
      while (Character.isJavaIdentifierPart( c)) {
        c = input.next();
      }
    }
  }

  /**
   * Matches an Identifer or a keyword.
   * 
   * @return the length of the match or <code>0</code> if no match was found.
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
   * @return the length of the match or <code>0</code> if no match was found.
   */
  private int matchWhitespace()
  {
    int len = 0;
    char c = LA( len);
    while (Character.isWhitespace( c)) {
      c = LA( ++len);
    }
    return len;
  }

  /**
   * 
   */
  private void readCharConst()
  {
    // TODO Auto-generated method stub
    throw new java.lang.UnsupportedOperationException(
        "readCharCOnst not implemented");

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

  /*
   * (non-Javadoc)
   * 
   * @see swing.text.highlight.categoriser.Categoriser#closeInput()
   */
  public void closeInput()
  {
    // TODO Auto-generated method stub
  }

  ///////////////////////////////////////////////////////////
  // helper methods
  ///////////////////////////////////////////////////////////

  private char LA( int lookAhead)
  {
    int offset = input.getIndex();
    if (offset + lookAhead > input.offset + input.count) {
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