/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight;

import java.awt.Color;
import java.io.IOException;

import javax.swing.text.Document;
import javax.swing.text.Segment;

import sun.tools.java.Constants;
import de.marw.javax.swing.text.highlight.categoriser.Categoriser;
import de.marw.javax.swing.text.highlight.categoriser.Token;


/**
 * This kit supports a fairly minimal handling of editing Java text content. It
 * supports syntax highlighting and produces the lexical structure of the
 * document as best it can.
 * 
 * @author Martin Weber
 */
public class JavaHighlightingKit extends HighlightingKit
{

  /**
   * The styles representing the actual categories.
   */
  private static CategoryStyles categoryStyles;

  public JavaHighlightingKit() {
    super();
  }

  /**
   * Gets the MIME type of the data that this kit represents support for. This
   * kit supports the type <code>text/x-java</code>.
   */
  public String getContentType()
  {
    return "text/x-java";
  }

  /**
   * Creates a Categoriser used for highlighting text of this document of
   * <code>null</code>.
   */
  protected Categoriser createCategoriser()
  {
    return new Java_Tokeniser();
  }

  /**
   * @see de.marw.javax.swing.text.highlight.HighlightingKit#getCategoryStyles()
   */
  public CategoryStyles getCategoryStyles()
  {
    if (categoryStyles == null) {
      categoryStyles = createDefaultStyles();
    }
    return categoryStyles;
  }

  /**
   * Creates a built-in set of color and font style informations used used to
   * render highlighted text written in the C programming language.
   */
  private CategoryStyles createDefaultStyles()
  {
    final Color keywordCol = new Color( 127, 0, 85);
    final Color literalColor = new Color( 42, 0, 255);
    final Color commentColor = new Color( 63, 127, 95);

    CategoryStyles styleDefaults = new CategoryStyles();

    styleDefaults.setColor( Category.COMMENT_1, commentColor);
    styleDefaults.setColor( Category.COMMENT_2, commentColor);
    styleDefaults.setColor( Category.STRINGVAL, literalColor);
    styleDefaults.setItalic( Category.STRINGVAL, true);
    styleDefaults.setColor( Category.NUMERICVAL, literalColor);
    styleDefaults.setColor( Category.PREDEFVAL, literalColor);
    styleDefaults.setBold( Category.PREDEFVAL, true);
    styleDefaults.setColor( Category.KEYWORD_STATEMENT, keywordCol);
    styleDefaults.setBold( Category.KEYWORD_STATEMENT, true);
    styleDefaults.setColor( Category.KEYWORD_OPERATOR, keywordCol);
    styleDefaults.setBold( Category.KEYWORD_OPERATOR, true);
    styleDefaults.setColor( Category.KEYWORD_TYPE, new Color( 181, 0, 121));
    styleDefaults.setBold( Category.KEYWORD_TYPE, true);
    styleDefaults.setColor( Category.KEYWORD, new Color( 109, 137, 164));
    styleDefaults.setBold( Category.KEYWORD, true);
    styleDefaults.setColor( Category.DOC, new Color( 6, 40, 143));
    styleDefaults.setColor( Category.IDENTIFIER_1, Color.cyan.darker());

    return styleDefaults;
  }

  /**
   * @author weber
   */
  public static class Java_Tokeniser extends sun.tools.java.Scanner implements
      Categoriser
  {

    private static boolean debug = false;

    Java_Tokeniser() {
      super( new LocalEnvironment());
      scanComments = true;
    }

    /**
     * @see Categoriser#openInput(Segment)
     */
    public void openInput( Segment lexerInput)
    {
      if (debug) {
        System.out.println( "setInput() char[0]='"
            + lexerInput.array[lexerInput.offset] + "', offset="
            + lexerInput.offset + ", count=" + lexerInput.count);
      }
      try {
        /*
         * Note: This call will retrieve the first token too!
         */
        super.useInputStream( new SegmentInputStream( lexerInput));
      }
      catch (IOException e) {
        // can't adjust scanner... calling logic
        // will simply render the remaining text.
        e.printStackTrace();
      }
    }

    /**
     * @see Categoriser#nextToken(Document, Token)
     */
    public Token nextToken( Document doc, Token token)
    {
      if (token == null) {
        token = new Token();
      }

      token.start = getStartOffset();
      token.length = getEndOffset() - token.start;
      token.category = null;
      switch (super.token) {
        default:
        break;
        // operators
        case Constants.COMMA:
        case Constants.ASSIGN:
        case Constants.ASGMUL:
        case Constants.ASGDIV:
        case Constants.ASGREM:
        case Constants.ASGADD:
        case Constants.ASGSUB:
        case Constants.ASGLSHIFT:
        case Constants.ASGRSHIFT:
        case Constants.ASGURSHIFT:
        case Constants.ASGBITAND:
        case Constants.ASGBITOR:
        case Constants.ASGBITXOR:
        case Constants.COND:
        case Constants.OR:
        case Constants.AND:
        case Constants.BITOR:
        case Constants.BITXOR:
        case Constants.BITAND:
        case Constants.NE:
        case Constants.EQ:
        case Constants.GE:
        case Constants.GT:
        case Constants.LE:
        case Constants.LT:
        case Constants.INSTANCEOF:
        case Constants.LSHIFT:
        case Constants.RSHIFT:
        case Constants.URSHIFT:
        case Constants.ADD:
        case Constants.SUB:
        case Constants.DIV:
        case Constants.REM:
        case Constants.MUL:
        case Constants.CAST:
        case Constants.POS:
        case Constants.NEG:
        case Constants.NOT:
        case Constants.BITNOT:
        case Constants.PREINC:
        case Constants.PREDEC:
        case Constants.NEWARRAY:
        case Constants.NEWINSTANCE:
        case Constants.NEWFROMNAME:
        case Constants.POSTINC:
        case Constants.POSTDEC:
        case Constants.FIELD:
        case Constants.METHOD:
        case Constants.ARRAYACCESS:
        case Constants.NEW:
        case Constants.INC:
        case Constants.DEC:
        case Constants.CONVERT:
        case Constants.EXPR:
        case Constants.ARRAY:
        case Constants.GOTO:
          token.category = Category.OPERATOR;
          if (debug)
            System.out.println( "cat=" + token.category + ", '<op>'");
        break;

        // values
        case Constants.IDENT:
          token.category = Category.IDENTIFIER_1;
          if (debug)
            System.out.println( "cat=" + token.category + ", '" + super.idValue
                + "'");
        break;
        case Constants.BOOLEANVAL:
        case Constants.BYTEVAL:
        case Constants.SHORTVAL:
        case Constants.INTVAL:
        case Constants.LONGVAL:
        case Constants.FLOATVAL:
        case Constants.DOUBLEVAL:
          token.category = Category.NUMERICVAL;
          if (debug)
            System.out.println( "cat=" + token.category + ", '<numval>'");
        break;
        case Constants.CHARVAL:
        case Constants.STRINGVAL:
          token.category = Category.STRINGVAL;
          if (debug)
            System.out.println( "cat=" + token.category + ", '<STRINGVAL>'");
        break;

        // types
        case Constants.BYTE:
        case Constants.CHAR:
        case Constants.SHORT:
        case Constants.INT:
        case Constants.LONG:
        case Constants.FLOAT:
        case Constants.DOUBLE:
        case Constants.VOID:
        case Constants.BOOLEAN:
          token.category = Category.KEYWORD_TYPE;
        break;
        // expressions
        case Constants.TRUE:
        case Constants.FALSE:
        case Constants.THIS:
        case Constants.SUPER:
        case Constants.NULL:
          token.category = Category.PREDEFVAL;
        break;
        // statements
        case Constants.IF:
        case Constants.ELSE:
        case Constants.FOR:
        case Constants.WHILE:
        case Constants.DO:
        case Constants.SWITCH:
        case Constants.CASE:
        case Constants.DEFAULT:
        case Constants.BREAK:
        case Constants.CONTINUE:
        case Constants.RETURN:
        case Constants.TRY:
        case Constants.CATCH:
        case Constants.FINALLY:
        case Constants.THROW:
        case Constants.STAT:
        case Constants.EXPRESSION:
        case Constants.DECLARATION:
        case Constants.VARDECLARATION:
          token.category = Category.KEYWORD_STATEMENT;
        break;

        // declarations
        case Constants.IMPORT:
        case Constants.CLASS:
        case Constants.EXTENDS:
        case Constants.IMPLEMENTS:
        case Constants.INTERFACE:
        case Constants.PACKAGE:
          token.category = Category.KEYWORD;
        break;

        // modifiers = {
        case Constants.PRIVATE:
        case Constants.PUBLIC:
        case Constants.PROTECTED:
        case Constants.CONST:
        case Constants.STATIC:
        case Constants.TRANSIENT:
        case Constants.SYNCHRONIZED:
        case Constants.NATIVE:
        case Constants.FINAL:
        case Constants.VOLATILE:
        case Constants.ABSTRACT:
          token.category = Category.KEYWORD_TYPE;
        break;

        // punctuations = {
        case Constants.SEMICOLON:
        case Constants.COLON:
        case Constants.QUESTIONMARK:
        case Constants.LBRACE:
        case Constants.RBRACE:
        case Constants.LPAREN:
        case Constants.RPAREN:
        case Constants.LSQBRACKET:
        case Constants.RSQBRACKET:
        case Constants.THROWS:
          token.category = null;
        break;

        // specials = {
        case Constants.COMMENT:
          // mark elem for starting location of next scan
          token.category = Category.COMMENT_1;
          if (debug)
            System.out.println( "cat=" + token.category + ", '<comment>'");
        break;
        case Constants.ERROR:
        // fall thru
        case Constants.TYPE:
        case Constants.LENGTH:
        case Constants.INLINERETURN:
        case Constants.INLINEMETHOD:
        case Constants.INLINENEWINSTANCE:
          token.category = null;
        break;
      }

      try {
        super.scan();
      }
      catch (IOException e) {
        // can't adjust scanner... calling logic
        // will simply render the remaining text.
        throw new RuntimeException( e);
      }
      return token;
    }

    /**
     * @see de.marw.javax.swing.text.highlight.categoriser.Categoriser#closeInput()
     */
    public void closeInput()
    {
    }

    /**
     * This fetches the starting location of the current token in the segment.
     */
    private int getStartOffset()
    {
      return (int) (super.pos & MAXFILESIZE);
    }

    /**
     * This fetches the ending location of the current token in the segment.
     */
    private int getEndOffset()
    {
      return (int) (getEndPos() & MAXFILESIZE);
    }

  }

  static class LocalEnvironment extends sun.tools.java.Environment
  {

    public void error( Object source, long where, String err, Object arg1,
        Object arg2, Object arg3)
    {
      // should do something useful...
      System.err.println( err);
      System.err.println( "location: " + (where & MAXFILESIZE));
    }
  }

}
