// $Id$

package swing.text.highlight;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import swing.text.highlight.categoriser.Categoriser;
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
    // configure the default style
    StyleConstants.setFontFamily( root, "Monospaced");
    StyleConstants.setFontSize( root, 16);

    Category[] categories = Category.values();
    categoryStyles = new Style[Category.values().length];
    for (int i = 0; i < categories.length; i++ ) {
      Category cat = categories[i];
      Style parent = getStyle( cat.getName());
      if (parent == null) {
        parent = addStyle( cat.getName(), root);
      }
      Style s = addStyle( null, parent);
      s.addAttribute( Category.CategoryAttribute, cat);
      categoryStyles[cat.ordinal()] = s;
    }
  }

  /**
   * Fetch the foreground color to use for a text run with the given category
   * id.
   */
  public Color getForeground( Category category)
  {
    if (categoryColors == null) {
      categoryColors = new Color[Category.values().length];
    }
    Color c = null;
    //code--; // no mapping for Category.NORMAL
    int categoryCode = category.ordinal();
    if ((categoryCode >= 0) && (categoryCode < categoryColors.length)) {
      c = categoryColors[categoryCode];
      if (c == null) {
        Style s = categoryStyles[categoryCode];
        if (s != null) {
          c = super.getForeground( s);
          categoryColors[categoryCode] = c;
        }
      }
    }
    return c;
  }

  /**
   * Fetch the font to use for a text run with the given category id.
   */
  public Font getFont( Category category)
  {
    if (categoryFonts == null) {
      categoryFonts = new Font[Category.values().length];
    }
    //code--; // no mapping for Category.NORMAL
    Font f = null;
    int categoryCode = category.ordinal();
    if (categoryCode >= 0 && categoryCode < categoryFonts.length) {
      f = categoryFonts[categoryCode];
      if (f == null) {
        Style s = categoryStyles[categoryCode];
        if (s != null) {
          f = super.getFont( s);
          categoryFonts[categoryCode] = f;
        }
      }
    }
    return f;
  }

  /**
   * Fetches the attribute set to use for the given category code. The set is
   * stored in a table to facilitate relatively fast access to use in
   * conjunction with the scanner.
   */
  public Style getStyleForCategory( Category category)
  {
    int categoryCode = category.ordinal();
    if (categoryCode >= 0 && categoryCode < categoryStyles.length) {
      Style s = categoryStyles[categoryCode];
      return s;
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
  private Style[] categoryStyles;

  /**
   * Cache of foreground colors to represent the various categories.
   */
  private transient Color[] categoryColors;

  /**
   * Cache of fonts to represent the various categories.
   */
  private transient Font[] categoryFonts;

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
     * the color used to render selected text.
     */
    private Color selectedColor;

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
      JTextComponent host = (JTextComponent) getContainer();
      Color normalColor = (host.isEnabled()) ? host.getForeground() : host
          .getDisabledTextColor();
      Caret c = host.getCaret();
      selectedColor = c.isSelectionVisible() ? host.getSelectedTextColor()
          : normalColor;
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

      Segment text = getLineBuffer();
      Token token = null;
      while (p0 < p1) {
        Category category = Category.NORMAL;
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
            category = Category.NORMAL;
            flushTo = token.start;
            doc.getText( p0, flushTo - p0, text);
            x = drawHighlightedText( category, selected, text, x, y, g, p0);
            p0 = flushTo;
          }
          if (token.start <= p0) {
            // draw current token
            category = token.category;
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

      return x;
    }

    /**
     * Draws the given text, expanding any tabs that are contained using the
     * given tab expansion technique. This is implemented to paint colors based
     * upon the category-to-color translations.
     * 
     * @param category
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
    private int drawHighlightedText( Category category, boolean selected,
        Segment text, int x, int y, Graphics g, int startOffset)
    {
      if (text.count > 0) {
        Color fg = selected ? selectedColor : getForeground( category);
        Font font = getFont( category);

        if (!g.getColor().equals( fg)) {
          g.setColor( fg);
        }
        if (!g.getFont().equals( font)) {
          g.setFont( font);
        }

        if (false) {
          System.out.print( "painting '" + text + "', offs=" + startOffset
              + ", cat=" + category + ", sel=" + selected);
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
     * Repaints the given line range. Overwritten to repaint all lines, thus
     * supporting highlighting tokens that span mutliple lines.
     * 
     * @param host
     *          the component hosting the view (used to call repaint)
     * @param a
     *          the region allocated for the view to render into
     * @param line0
     *          the starting line number to repaint. This must be a valid line
     *          number in the model.
     * @param line1
     *          the ending line number to repaint. This must be a valid line
     *          number in the model.
     */
    protected void damageLineRange( int line0, int line1, Shape a,
        Component host)
    {
      if (a != null) {
        host.repaint();
      }
    }

    /**
     * Eine Queue für <code>Token</code>s.
     * 
     * @author weber
     */
    private final class TokenQueue
    {
      private Categoriser categoriser;

      private Token tokenBuf;

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