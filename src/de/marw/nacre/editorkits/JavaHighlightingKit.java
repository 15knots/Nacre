//$Id$

package swing.text.highlight;

import java.io.IOException;

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Segment;

import sun.tools.java.Constants;
import swing.text.highlight.categoriser.Categoriser;
import swing.text.highlight.categoriser.Token;


/**
 * This kit supports a fairly minimal handling of editing Java text content. It
 * supports syntax highlighting and produces the lexical structure of the
 * document as best it can.
 * 
 * @author Timothy Prinzing
 * @author Martin Weber
 */
public class JavaHighlightingKit extends HighlightingKit
{

  public JavaHighlightingKit()
  {
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

  public static class Java_Tokeniser extends sun.tools.java.Scanner implements
      Categoriser
  {

    private static boolean debug = false;

    private Segment lexerInput;

    private int seg2docOffset;

    Java_Tokeniser()
    {
      super( new LocalEnvironment());
      scanComments = true;
    }

    /**
     * @see Categoriser#openInput(HighlightedDocument, int)
     * @throws BadLocationException
     */
    public void openInput( HighlightedDocument doc, Segment lexerInput)
        throws BadLocationException
    {
      if (debug) {
        System.out.println( "setInput() char[0]='"
            + lexerInput.array[lexerInput.offset] + "', offset="
            + lexerInput.offset + ", count=" + lexerInput.count);
      }
      this.lexerInput = lexerInput;
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
     * @see Categoriser#nextToken(HighlightedDocument, Token)
     */
    public Token nextToken( HighlightedDocument doc, Token token)
    {
      if (token == null) {
        token = new Token();
      }
      boolean locationOK = true;

      token.start = getStartOffset() - seg2docOffset;
      token.length = getEndOffset() - token.start;
      token.category = Category.NORMAL;
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

        //  values
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
          token.category = Category.NORMAL;
        break;

        // specials = {
        case Constants.COMMENT:
          // mark elem for starting location of next scan
          token.category = Category.COMMENT_1;
          if (debug)
            System.out.println( "cat=" + token.category + ", '<comment>'");
          locationOK = false;
        break;
        case Constants.ERROR:
          locationOK = false;
        // fall thru
        case Constants.TYPE:
        case Constants.LENGTH:
        case Constants.INLINERETURN:
        case Constants.INLINEMETHOD:
        case Constants.INLINENEWINSTANCE:
          token.category = Category.NORMAL;
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
     * @see swing.text.highlight.categoriser.Categoriser#closeInput()
     */
    public void closeInput()
    {
      this.lexerInput = null;
    }

    /**
     * @see Categoriser#insertUpdate(Element)
     */
    public void insertUpdate( Element line)
    {
    }

    /**
     * @see Categoriser#removeUpdate(Element)
     */
    public void removeUpdate( Element line)
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

  /** Used as a key on lines that contain multiline Tokens. */
  protected static class AttributeKey
  {

    private AttributeKey()
    {
    }

    public String toString()
    {
      return "scanner start pos";
    }

  }

  /**
   * Key to be used on lines that contain multiline Tokens.
   */
  protected static final Object CategorizerAttribute = new AttributeKey();

}

