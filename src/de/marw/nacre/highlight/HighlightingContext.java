// $Id$

package swing.text.highlight;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import swing.text.highlight.categoriser.Categoriser;
import swing.text.highlight.categoriser.CategoryConstants;
import swing.text.highlight.categoriser.Token;


/**
 * A collection of styles used to render highlighted text. This class also acts
 * as a factory for the views used to represent the documents. Since the
 * rendering styles are based upon view preferences, the views need a way to
 * gain access to the style settings which is facilitated by implementing the
 * factory in the style storage. Both functionalities can be widely shared
 * across the document views.
 * 
 * @author Timothy Prinzing (1.2 05/27/99)
 * @author Martin Weber
 */
public class HighlightingContext extends StyleContext implements ViewFactory
{

  /**
   * Constructs a set of styles to represent lexical tokenBuf categories. By
   * default there are no colors or fonts specified.
   */
  public HighlightingContext()
  {
    super();
    Style root = getStyle( DEFAULT_STYLE);
    Category[] categories = Category.getCategories();
    categoryStyles = new Style[categories.length];
    for (int i = 0; i < categories.length; i++ ) {
      Category t = categories[i];
      Style parent = getStyle( t.getName());
      if (parent == null) {
        parent = addStyle( t.getName(), root);
      }
      Style s = addStyle( null, parent);
      s.addAttribute( Category.CategoryAttribute, t);
      categoryStyles[i] = s;
    }
  }

  /**
   * Fetch the foreground color to use for a text run with the given category
   * id.
   */
  public Color getForeground( int categoryCode)
  {
    if (categoryColors == null) {
      categoryColors = new Color[CategoryConstants.MaximumId + 1];
    }
    //code--; // no mapping for Category.NORMAL
    if ((categoryCode >= 0) && (categoryCode < categoryColors.length)) {
      Color c = categoryColors[categoryCode];
      if (c == null) {
        Style s = categoryStyles[categoryCode];
        c = StyleConstants.getForeground( s);
        categoryColors[categoryCode] = c;
      }
      return c;
    }
    return Color.black;
  }

  /**
   * Fetch the font to use for a text run with the given category id.
   */
  public Font getFont( int categoryCode)
  {
    if (categoryFonts == null) {
      categoryFonts = new Font[CategoryConstants.MaximumId + 1];
    }
    //code--; // no mapping for Category.NORMAL
    if (categoryCode >= 0 && categoryCode < categoryFonts.length) {
      Font f = categoryFonts[categoryCode];
      if (f == null) {
        Style s = categoryStyles[categoryCode];
        f = super.getFont( s);
        categoryFonts[categoryCode] = f;
      }
      return f;
    }
    return null;
  }

  /**
   * Fetches the attribute set to use for the given category code. The set is
   * stored in a table to facilitate relatively fast access to use in
   * conjunction with the scanner.
   */
  public Style getStyleForCategory( int categoryCode)
  {
    if (categoryCode < categoryStyles.length) {
      return categoryStyles[categoryCode];
    }
    return null;
  }

  // --- ViewFactory methods -------------------------------------

  public View create( Element elem)
  {
    return new HiliteView( elem);
  }

  // --- variables -----------------------------------------------

  /**
   * The styles representing the actual categories.
   */
  private Style[]           categoryStyles;

  /**
   * Cache of foreground colors to represent the various categories.
   */
  private transient Color[] categoryColors;

  /**
   * Cache of fonts to represent the various categories.
   */
  private transient Font[]  categoryFonts;

  /**
   * View that uses the lexical information to determine the style
   * characteristics of the text that it renders. This simply colorizes the
   * various categories and assumes a constant font family and size.
   */
  private class HiliteView extends PlainView
  {

    /**
     * 
     */
    private TokenQueue tokenQueue;

    /**
     * Construct a simple colorized view of java text.
     */
    HiliteView( Element elem)
    {
      super( elem);
      tokenQueue = new TokenQueue( (HighlightedDocument) elem.getDocument());
    }

    /**
     * Renders using the given rendering surface and area on that surface. This
     * is implemented to invalidate the lexical scanner after rendering so that
     * the next request to drawUnselectedText will set a new range for the
     * scanner.
     * 
     * @param g
     *          the rendering surface to use
     * @param a
     *          the allocated region to render into
     * @see View#paint(java.awt.Graphics, java.awt.Shape)
     */
    public void paint( Graphics g, Shape a)
    {
      System.out.println( "# paint() -------");
      super.paint( g, a);
    }

    /**
     * Renders a line of text, suppressing whitespace at the end and expanding
     * any tabs.
     * 
     * @param lineIndex
     *          the line to draw >= 0
     * @param g
     *          the <code>Graphics</code> context
     * @param x
     *          the starting X position >= 0
     * @param y
     *          the starting Y position >= 0
     * @see PlainView#drawUnselectedText
     * @see PlainView#drawSelectedText
     */
    protected void drawLine( int lineIndex, Graphics g, int x, int y)
    {
      //lexerValid = false;
      //      System.out.println( "# invalidate lexer ---------------------------");
      System.out.println( "# drawLine() " + lineIndex + " -------");
      try {
        tokenQueue.initialise( lineIndex);
        super.drawLine( lineIndex, g, x, y);
        // notify lexer
        //TODO lexer.closeInput();
      }
      catch (BadLocationException ex) {
        throw new /* StateInvariantError */Error( "Can't render line: "
            + lineIndex);
      }
    }

    /**
     * Renders the given range in the model as unselected text. This is
     * implemented to paint colors based upon the category-to-color
     * translations.
     * 
     * @param g
     *          the graphics context
     * @param x
     *          the starting X coordinate
     * @param y
     *          the starting Y coordinate
     * @param p0
     *          the beginning position in the model
     * @param p1
     *          the ending position in the model
     * @returns the location of the end of the range
     * @exception BadLocationException
     *              if the range is invalid
     */
    protected int drawUnselectedText( Graphics g, int x, int y, int p0, int p1)
        throws BadLocationException
    {
      return drawText( g, x, y, p0, p1, false);
    }

    /**
     * Renders the given range in the model as selected text. This is
     * implemented to render the text in the background color specified in the
     * hosting component. It assumes the highlighter will render the selected
     * background.
     * 
     * @param g
     *          the graphics context
     * @param x
     *          the starting X coordinate >= 0
     * @param y
     *          the starting Y coordinate >= 0
     * @param p0
     *          the beginning position in the model >= 0
     * @param p1
     *          the ending position in the model >= 0
     * @return the location of the end of the range
     * @exception BadLocationException
     *              if the range is invalid
     */
    protected int drawSelectedText( Graphics g, int x, int y, int p0, int p1)
        throws BadLocationException
    {
      {
        return drawText( g, x, y, p0, p1, true);
      }
    }

    /**
     * Renders the given range in the model as selected or unselected text. This
     * is implemented to paint colors based upon the category-to-color
     * translations.
     * 
     * @param g
     *          the graphics context
     * @param x
     *          the starting X coordinate
     * @param y
     *          the starting Y coordinate
     * @param p0
     *          the beginning position in the model
     * @param p1
     *          the ending position in the model
     * @returns the X coordinate of the end of the range
     * @exception BadLocationException
     *              if the range is invalid
     */
    protected int drawText( Graphics g, int x, int y, int p0, int p1,
        boolean selected) throws BadLocationException
    {
      HighlightedDocument doc = (HighlightedDocument) getDocument();

      Color lastColor = g.getColor();
      Font lastFont = g.getFont();

      Segment text = getLineBuffer();
      Token token = null;
      while (p0 < p1) {
        int category = CategoryConstants.NORMAL;
        int flushTo = p1;
        if (!tokenQueue.isEmpty()) {
          // get token
          token = tokenQueue.peek();
          if (false) {
            // print current token
            Segment txt = new Segment();
            System.out.print( "tok= " + token);
            doc.getText( token.start, token.length, txt);
            System.out.println( ", '" + txt + "'");
          }
          if (token.start > p0) {
            // gap between tokens: draw as normal text
            category = CategoryConstants.NORMAL;
            flushTo = token.start;
            doc.getText( p0, flushTo - p0, text);
            x = drawHighlightedText( category, selected, text, x, y, g, p0);
            p0 = flushTo;
          }
          if (token.start <= p0) {
            // draw current token
            category = token.categoryId;
            flushTo = Math.min( token.start + token.length, p1);
            if (token.start + token.length < p1) {
              // token was completely consumed: remove from queue
              tokenQueue.remove();
            }
          }
        }
        doc.getText( p0, flushTo - p0, text);
        x = drawHighlightedText( category, selected, text, x, y, g, p0);
        p0 = flushTo;
      }

      g.setColor( lastColor);
      g.setFont( lastFont);
      return x;
    }

    /**
     * Draws the given text, expanding any tabs that are contained using the
     * given tab expansion technique. This is implemented to paint colors based
     * upon the category-to-color translations.
     * 
     * @param categoryCode
     *          the category we are painting. Used to determine color and font.
     * @param text
     *          the source of the text
     * @param x
     *          the X origin >= 0
     * @param y
     *          the Y origin >= 0
     * @param g
     *          the graphics context
     * @param startOffset
     *          starting offset of the text in the document >= 0
     * @return the X location at the end of the rendered text
     * @exception BadLocationException
     *              if the range is invalid
     */
    private int drawHighlightedText( int categoryCode, boolean selected,
        Segment text, int x, int y, Graphics g, int startOffset)
    {
      if (text.count > 0) {
        Color fg = getForeground( categoryCode);
        Font font = getFont( categoryCode);
        if (fg != null)
          g.setColor( fg);
        if (font != null)
          g.setFont( font);

        if (true) {
          System.out.print( "painting '" + text + "', offs=" + startOffset
              + ", cat=" + categoryCode + ", sel=" + selected);
          System.out.println( ", color=" + fg + ", font=" + font);
        }
        x = Utilities.drawTabbedText( text, x, y, g, this, startOffset);
      }
      return x;
    }

    /**
     * @see javax.swing.text.View#insertUpdate(javax.swing.event.DocumentEvent,
     *      java.awt.Shape, javax.swing.text.ViewFactory)
     */
    public void insertUpdate( DocumentEvent e, Shape a, ViewFactory f)
    {
      // TODO Auto-generated method stub TEST
      System.out.println( "### insertUpdate()--------------------");
      super.insertUpdate( e, a, f);

      System.out.println( "### insertUpdate()  DONE --------------------");
    }

    /**
     * @see javax.swing.text.View#removeUpdate(javax.swing.event.DocumentEvent,
     *      java.awt.Shape, javax.swing.text.ViewFactory)
     */
    public void removeUpdate( DocumentEvent e, Shape a, ViewFactory f)
    {
      // TODO Auto-generated method stub TEST
      System.out.println( "### removeUpdate()--------------------");
      super.removeUpdate( e, a, f);
      System.out.println( "### removeUpdate()  DONE --------------------");
    }

    /**
     * @see javax.swing.text.View#changedUpdate(javax.swing.event.DocumentEvent,
     *      java.awt.Shape, javax.swing.text.ViewFactory)
     */
    public void XXXchangedUpdate( DocumentEvent e, Shape a, ViewFactory f)
    {
      // TODO Auto-generated method stub TEST
      System.out.println( "changedUpdate()--------------------");
      super.changedUpdate( e, a, f);
    }

    /**
     * Eine Queue für <code>Token</code>s.
     * 
     * @author weber
     */
    private final class TokenQueue
    {
      private Categoriser         categoriser;

      private Token               tokenBuf;

      private HighlightedDocument doc;

      /**
       * @param doc
       */
      public TokenQueue( HighlightedDocument doc)
      {
        super();
        this.doc = doc;
      }

      /**
       * (Re-)Initialises the categoriser to point to the appropriate token for
       * the given start position needed for rendering. The start position
       * adjustment is required by text runs that span multiple line (eg Javadoc
       * comments).
       * 
       * @param doc
       *          the document model.
       * @param line
       *          the starting line in the model.
       */
      public void initialise( int lineIndex) throws BadLocationException
      {
        //System.out.println( "# TokenQueue.adjustCategoriser()");
        Element rootElement = doc.getDefaultRootElement();

        Element line = rootElement.getElement( lineIndex);
        int p0 = line.getStartOffset();
        categoriser = doc.getCategoriser(); // remember categoriser
        categoriser.openInput( doc, lineIndex);

        do {
          tokenBuf = categoriser.nextToken( doc, tokenBuf);
        } while (!isEmpty() && tokenBuf.start + tokenBuf.length <= p0);

      }

      /**
       * Returns <code>true</code> if this queue contains no elements.
       * 
       * @return <code>true</code> if this queue contains no elements
       */
      public boolean isEmpty()
      {
        return tokenBuf.length <= 0;
      }

      /**
       * Retrieves, but does not remove, the head of this queue.
       */
      public Token peek()
      {
        if (false) {
          // print current tokenBuf
          System.out.println( "tok=" + tokenBuf);
        }
        return tokenBuf;
      }

      /**
       * Removes the head of this queue and retrieves the next token. Obtains
       * the next token from the categoriser and adjusts the token's start
       * position relative to the document.
       */
      public void remove()
      {
        tokenBuf = categoriser.nextToken( doc, tokenBuf);
      }
    }
  }

}