/**
 */

package swing.text.highlight;

import java.io.IOException;

import javax.swing.text.Element;
import javax.swing.text.Segment;

import sun.tools.java.Constants;
import swing.text.highlight.categoriser.Categoriser;
import swing.text.highlight.categoriser.CategoryConstants;
import swing.text.highlight.categoriser.Token;


/**
 * This kit supports a fairly minimal handling of editing Java text content. It
 * supports syntax highlighting and produces the lexical structure of the
 * document as best it can.
 * 
 * @author Timothy Prinzing
 * @author Martin Weber
 * @version 1.2 05/27/99
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

    protected Segment input;

    Java_Tokeniser()
    {
      super( new LocalEnvironment());
      scanComments = true;
      this.input = new Segment();
    }

    /**
     */
    public void setInput( Segment input)
    {
      try {
        this.input = input;
        /*
         * Note: This call will scan the first token too!
         */
        super.useInputStream( new SegmentInputStream( input));
      }
      catch (IOException e) {
        // can't adjust scanner... calling logic
        // will simply render the remaining text.
        e.printStackTrace();
      }
    }

    /**
     * Fetch a reasonable location to start scanning given the desired start
     * location. This allows for adjustments needed to accommodate multiline
     * comments.
     * 
     * @param doc
     *          The document holding the text.
     * @param offset
     *          The offset relative to the beginning of the document.
     * @return adjusted start position which is greater or equal than zero.
     */
    public int getAdjustedStart( HighlightedDocument doc, int offset)
    {
      Element rootElement = doc.getDefaultRootElement();
      int lineNum = rootElement.getElementIndex( offset);
      // walk backwards until we get a tagged line...
      System.out.println( "# find start in " + lineNum + "...");
      Element line = rootElement.getElement( lineNum);
      for (; lineNum > 0; line = rootElement.getElement( lineNum), lineNum-- ) {
        //        System.out.print( " " + lineNum);
        if (null != doc.getMark( line)) break;
      }
      System.out.println( "# found start in " + lineNum);

      return line.getStartOffset();
    }

    /**
     * @param doc
     * @param token
     */
    private void markLines( HighlightedDocument doc, Token token,
        boolean locationOK)
    {
      Element rootElement = doc.getDefaultRootElement();
      int lineNum = rootElement.getElementIndex( token.start);
      if (!locationOK) {
        // token is a multiline token
        if (token.length == 0) {
          return; // empty line
        }
        else {
          int lastLine = rootElement.getElementIndex( token.start
              + token.length);
          do {
            doc
                .putMark( rootElement.getElement( lineNum),
                    CategorizerAttribute);
          } while (lineNum++ <= lastLine);
        }
      }
      else {
        //        int lastLine= rootElement.getElementIndex( token.start +
        // token.length);
        //        do {
        //          doc.removeMark( lineNum);
        //        }
        //        while (lineNum++ <= lastLine);
      }
    }

    private static boolean debug = false;

    /**
     */
    public Token nextToken( HighlightedDocument doc, Token token)
    {
      if (token == null) {
        token = new Token();
      }
      boolean locationOK = true;

      token.start = getStartOffset();
      token.length = getEndOffset() - token.start;
      token.categoryId = CategoryConstants.NORMAL;
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
          token.categoryId = CategoryConstants.OPERATOR;
          if (debug)
              System.out.println( "cat=" + token.categoryId + ", '<op>'");
        break;

        //  values
        case Constants.IDENT:
          token.categoryId = CategoryConstants.IDENTIFIER1;
          if (debug)
              System.out.println( "cat=" + token.categoryId + ", '"
                  + super.idValue + "'");
        break;
        case Constants.BOOLEANVAL:
        case Constants.BYTEVAL:
        case Constants.SHORTVAL:
        case Constants.INTVAL:
        case Constants.LONGVAL:
        case Constants.FLOATVAL:
        case Constants.DOUBLEVAL:
          token.categoryId = CategoryConstants.NUMERICVAL;
          if (debug)
              System.out.println( "cat=" + token.categoryId + ", '<numval>'");
        break;
        case Constants.CHARVAL:
        case Constants.STRINGVAL:
          token.categoryId = CategoryConstants.STRINGVAL;
          if (debug)
              System.out
                  .println( "cat=" + token.categoryId + ", '<Stringval>'");
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
          token.categoryId = CategoryConstants.TYPE;
        break;
        // expressions
        case Constants.TRUE:
        case Constants.FALSE:
        case Constants.THIS:
        case Constants.SUPER:
        case Constants.NULL:
          token.categoryId = CategoryConstants.PREDEFVAL;
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
          token.categoryId = CategoryConstants.KEYWORD1;
        break;

        // declarations
        case Constants.IMPORT:
        case Constants.CLASS:
        case Constants.EXTENDS:
        case Constants.IMPLEMENTS:
        case Constants.INTERFACE:
        case Constants.PACKAGE:
          token.categoryId = CategoryConstants.KEYWORD2;
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
          token.categoryId = CategoryConstants.TYPE;
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
          token.categoryId = CategoryConstants.NORMAL;
        break;

        // specials = {
        case Constants.COMMENT:
          // mark elem for starting location of next scan
          token.categoryId = CategoryConstants.COMMENT1;
          if (debug)
              System.out.println( "cat=" + token.categoryId + ", '<comment>'");
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
          token.categoryId = CategoryConstants.NORMAL;
        break;
      }

      markLines( doc, token, locationOK);

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
     * Überschrieben, um
     */
    public void insertUpdate( Element line)
    {
      Element rootElement = line.getParentElement();
      int lineNum = rootElement.getElementIndex( line.getStartOffset());
      HighlightedDocument doc = (HighlightedDocument) line.getDocument();
      //    TODO doc.putMark( lineNum, CategorizerAttribute);
    }

    /**
     * Überschrieben, um
     */
    public void removeUpdate( Element line)
    {
      Element rootElement = line.getParentElement();
      int lineNum = rootElement.getElementIndex( line.getStartOffset());
      HighlightedDocument doc = (HighlightedDocument) line.getDocument();
      //TODO doc.putMark( lineNum, CategorizerAttribute);
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

    /*
     * (non-Javadoc)
     * 
     * @see swing.text.highlight.categoriser.Categoriser#closeInput()
     */
    public void closeInput()
    {
      // TODO Auto-generated method stub

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

