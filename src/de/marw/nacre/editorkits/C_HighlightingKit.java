/**
 */

package swing.text.highlight;

import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;

import javax.swing.text.Element;
import javax.swing.text.Segment;

import swing.text.highlight.categoriser.AbstractCategoriser;
import swing.text.highlight.categoriser.Categoriser;
import swing.text.highlight.categoriser.CategoryConstants;
import swing.text.highlight.categoriser.LexerC;
import swing.text.highlight.categoriser.LexerCTokenTypes;
import swing.text.highlight.categoriser.Token;
import antlr.TokenStreamException;

/**
 * This kit supports a fairly minimal handling of editing C text content. It
 * supports syntax highlighting and produces the lexical structure of the
 * document as best it can.
 * 
 * @author Timothy Prinzing
 * @author Martin Weber
 * @version 1.2 05/27/99
 */
public class CHighlightingKit extends HighlightingKit
{

  public CHighlightingKit()
  {
    super();
  }

  /**
   * Gets the MIME type of the data that this kit represents support for. This
   * kit supports the type <code>text/x-c-src</code>.
   */
  public String getContentType()
  {
    // check whether MIME type is apprpriate
    return "text/x-c-src";
  }

  /**
   * Creates a Categoriser used for highlighting text of this document of
   * <code>null</code>.
   */
  protected Categoriser createCategoriser()
  {
    return new C_Tokeniser();
  }

  public class C_Tokeniser extends AbstractCategoriser
  {

    private LexerC lexer;

    C_Tokeniser()
    {}

    public void setInput( Segment input)
    {
      super.setInput( input);
      //      lexer= new LexerC( new SegmentInputStream( input));
    }

    /**
     * Überschrieben, um
     */
    public Token nextToken( HighlightedDocument doc, Token token)
    {
      if (token == null) {
        token= new Token();
      }

      if (true) {
        scan( token);
      }
      else {
        token.start= input.getIndex();
        antlr.Token lToken= null;
        tokenLoop: for (;;) {
          try {
            lToken= lexer.nextToken();
            switch (lToken.getType()) {
              case LexerCTokenTypes.Whitespace:
              break;
              case LexerCTokenTypes.Comment:
                // TODO mark elem for starting location of next scan
                token.categoryId= CategoryConstants.COMMENT1;
              break tokenLoop;
              case LexerCTokenTypes.CPPComment:
                token.categoryId= CategoryConstants.COMMENT2;
              break tokenLoop;
              case LexerCTokenTypes.Number:
                token.categoryId= CategoryConstants.NUMERICVAL;
              break tokenLoop;
              // TODO
              default:
                token.categoryId= CategoryConstants.NORMAL;
              break tokenLoop;
            }
          }
          catch (TokenStreamException e) {
            // can't adjust scanner... calling logic
            // will simply render the remaining text.
            //e.printStackTrace();
            System.out.println( e);
            token.length= input.getIndex() - token.start;
            token.categoryId= CategoryConstants.NORMAL;
            input.next();
          }
        }
        token.length= lToken.getText().length();
        //      token.categoryId= bunter++ % CategoryConstants.MaximumId + 1;
      }
      return token;
    }

    private void scan( Token token)
    {
      tokenLoop: for (;;) {
        skipWS();
        token.start= input.getIndex();
        char c= nextChar();

        switch (c) {
          case '\"':
            readString();
            token.categoryId= CategoryConstants.STRINGVAL;
          break tokenLoop;

          case '\'':
            readCharConst();
            token.categoryId= CategoryConstants.STRINGVAL;
          break tokenLoop;

          case '/':
            if (input.array[input.getIndex()] == '*') {
              readMLComment();
              token.categoryId= CategoryConstants.COMMENT1;
            }
            else {
              token.categoryId= CategoryConstants.OPERATOR;
            }
          break tokenLoop;
          case '#':
            skipWS();
            readWord();
            token.categoryId= CategoryConstants.KEYWORD2;
          break tokenLoop;
          default:
            readWord();
          token.categoryId= CategoryConstants.NORMAL;
          break tokenLoop;
        }
      }
      token.length= input.getIndex() - token.start;
    }

    /**
     * 
     */
    private void readWord()
    {
      for (char c= input.current(); Character.isLetterOrDigit( c) || (c == '_');) {
        c= nextChar();
      }
    }

    void skipWS()
    {
      while (Character.isWhitespace( input.current())) {
        input.next();
      }
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
     * 
     */
    private void readMLComment()
    {
      char c= nextChar();
      for (;;) {
        switch (c) {
          case '*':
            if (input.current() == '/') {
              nextChar();
              return;
            }
            c= nextChar();
          break;
          case CharacterIterator.DONE:
            return;
          default:
            c= nextChar();
        }
      }
    }

    /**
     * 
     */
    private void readString()
    {
      char c= nextChar();
      for (;;) {
        switch (c) {
          case '\\':
            nextChar();
          break;
          case '\"':
          case '\n':
            nextChar();
            return;
          case CharacterIterator.DONE:
            return;
          default:
            c= nextChar();
        }
      }
    }

    private char nextChar()
    {
      char ret= input.current();
      input.next();
      return ret;
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
  }

}

