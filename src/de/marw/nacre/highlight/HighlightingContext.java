/* $Header$ */

// Copyright � 2004 Martin Weber

package swing.text.highlight;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
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
   * the Categoriser used for highlighting text of this document or
   * <code>null</code> if no highlighting is to be done.
   */
  private final Categoriser categoriser;

  /**
   * The styles representing the actual categories.
   */
  private Style[] categoryStyles;

  /**
   * Cache of foreground colors to represent the various categories.
   * 
   * @see #getForeground(Category)
   */
  private transient Color[] categoryColors;

  /**
   * Cache of fonts to represent the various categories.
   * 
   * @see #getFont(Category)
   */
  private transient Font[] categoryFonts;

  /**
   * Constructs a set of styles to represent lexical tokenBuf categories. By
   * default there are no colors or fonts specified.
   * 
   * @param categoriser
   *        the Categoriser used for highlighting text of this document or
   *        <code>null</code> if no highlighting is to be done.
   */
  public HighlightingContext( Categoriser categoriser)
  {
    super();
    this.categoriser = categoriser;
    ChangeListener cacheInvalidator = new CacheInvalidator();

    Style root = getStyle( DEFAULT_STYLE);
    // configure the default style
    StyleConstants.setFontFamily( root, "Monospaced");
    StyleConstants.setFontSize( root, 12);
    root.addChangeListener( cacheInvalidator);

    Category[] categories = Category.values();
    categoryStyles = new Style[Category.values().length];
    for (int i = 0; i < categories.length; i++ ) {
      Category cat = categories[i];
      Style parent = getStyle( cat.getName());
      if (parent == null) {
        // add style for category
        parent = addStyle( cat.getName(), root);
      }
      // add attributes for category style...
      Style style = addStyle( null, parent);
      categoryStyles[cat.ordinal()] = style;
      style.addAttribute( Category.CategoryAttribute, cat);
      style.addChangeListener( cacheInvalidator);
    }

  }

  /**
   * Fetch the foreground color to use for a text run with the given category .
   */
  public final Color getForeground( Category category)
  {
    if (categoryColors == null) {
      categoryColors = new Color[Category.values().length];
    }
    Color c = null;
    int categoryCode = category.ordinal();
    if ((categoryCode >= 0) && (categoryCode < categoryColors.length)) {
      c = categoryColors[categoryCode];
      if (c == null) {
        Style s = categoryStyles[categoryCode];
        c = super.getForeground( s);
        categoryColors[categoryCode] = c;
      }
    }
    return c;
  }

  /**
   * Fetch the font to use for a text run with the given category.
   */
  public final Font getFont( Category category)
  {
    if (categoryFonts == null) {
      categoryFonts = new Font[Category.values().length];
    }
    Font f = null;
    int categoryCode = category.ordinal();
    if (categoryCode >= 0 && categoryCode < categoryFonts.length) {
      f = categoryFonts[categoryCode];
      if (f == null) {
        Style s = categoryStyles[categoryCode];
        f = super.getFont( s);
        categoryFonts[categoryCode] = f;
      }
    }
    return f;
  }

  /**
   * Fetches the attribute set to use for the given category. The set is stored
   * in a table to facilitate relatively fast access to use in conjunction with
   * the scanner.
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

  public View create( Element elem)
  {
    //    String kind = elem.getName();
    return categoriser != null ? new HiliteView( elem) : new PlainView( elem);
  }

  // --- classes -----------------------------------------------

  private class CacheInvalidator implements ChangeListener
  {
    public void stateChanged( ChangeEvent e)
    {
      // invalidate caches
      HighlightingContext.this.categoryColors = null;
      HighlightingContext.this.categoryFonts = null;
    }
  };

  /**
   * View that uses the lexical information to determine the style
   * characteristics of the text that it renders. This simply colorizes the
   * various categories and assumes a constant font family and size.
   * 
   * @todo super.updateMetrics verwendet den FOnt aus der JTextComponent statt
   *       der Style-Attribute
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
     *        the rendering surface to use
     * @param a
     *        the allocated region to render into
     * @see View#paint(java.awt.Graphics, java.awt.Shape)
     */
    public void paint( Graphics g, Shape a)
    {
      System.out.println( "# paint() -------");
      JTextComponent host = (JTextComponent) getContainer();
      Color normalColor = (host.isEnabled()) ? host.getForeground() : host
          .getDisabledTextColor();
      Caret c = host.getCaret();
      selectedColor = c.isSelectionVisible() ? host.getSelectedTextColor()
          : normalColor;
      super.paint( g, a);
    }

    /**
     * Renders a line of text, suppressing whitespace at the end and expanding
     * any tabs.
     * 
     * @param lineIndex
     *        the line to draw >= 0
     * @param g
     *        the <code>Graphics</code> context
     * @param x
     *        the starting X position >= 0
     * @param y
     *        the starting Y position >= 0
     * @see PlainView#drawUnselectedText
     * @see PlainView#drawSelectedText
     */
    protected void drawLine( int lineIndex, Graphics g, int x, int y)
    {
      System.out.println( "# drawLine() " + lineIndex + " -------");
      try {
        Element rootElement = getElement();
        HighlightedDocument doc = (HighlightedDocument) rootElement
            .getDocument();
        Element line = rootElement.getElement( lineIndex);
        int p0 = line.getStartOffset();
        int p1 = Math.min( doc.getLength(), line.getEndOffset());
        /*
         * (Re-)Initialise the categoriser to point to the appropriate token for
         * the given start position needed for rendering. The start position
         * adjustment is required by text runs that span multiple line (eg
         * '/*'-comments).
         */
        int p0Adj = getAdjustedStart( lineIndex);
        Segment lexerInput = new Segment();
        doc.getText( p0Adj, p1 - p0Adj, lexerInput);
        tokenQueue.initialise( p0, lexerInput, lexerInput.offset - p0Adj);

        super.drawLine( lineIndex, g, x, y);

        // drawing complete, notify categoriser
        HighlightingContext.this.categoriser.closeInput();

        // check and mark whether the next line is unsafe to restart scanning
        Token token = null;
        if (!tokenQueue.isEmpty()) {
          // get token
          token = tokenQueue.peek();
          if (token.multiline && token.start < p1) {
            do {
              line = rootElement.getElement( ++lineIndex);
              // the current line is unsafe to restart scanning
              if (line != null) {
                putMark( line, null);
                /*
                 * force the component to repaint the lines, thus supporting
                 * highlighting tokens that span mutliple lines.
                 */
                if (metrics != null) {
                  System.out.println( "# force repaint of " + lineIndex);
                  Component host = getContainer();
                  host.repaint( x, y + (lineIndex * metrics.getHeight()), g
                      .getClipBounds().width, metrics.getHeight());
                }

              }
            } while (token.start + token.length > p1);
          }
        }
      }
      catch (BadLocationException ex) {
        throw new /* StateInvariantError */Error( "Can't render line: "
            + lineIndex);
      }
    }

    /**
     * Fetch a reasonable location to start scanning given the desired start
     * location. This allows for adjustments needed to accommodate multiline
     * comments.
     * 
     * @param lineIndex
     *        The number of the line to render.
     * @return the adjusted start position in the document model which is
     *         greater or equal than zero.
     */
    private int getAdjustedStart( int lineIndex)
    {
      Element element = getElement();
      // walk backwards until we get an untagged line...
      //System.out.print( "# find start in line " + lineIndex);
      for (; lineIndex > 0; lineIndex-- ) {
        Element line = element.getElement( lineIndex);
        if (!hasMark( line)) {
          //  System.out.println( " found start in " + lineIndex);
          return line.getStartOffset();
        }
        //System.out.print( "," + lineIndex);
      }
      //System.out.println( " not found (0)");
      return 0;
    }

    /**
     * Renders the given range in the model as unselected text. This is
     * implemented to paint colors based upon the category-to-color
     * translations.
     * 
     * @param g
     *        the graphics context
     * @param x
     *        the starting X coordinate
     * @param y
     *        the starting Y coordinate
     * @param p0
     *        the beginning position in the model
     * @param p1
     *        the ending position in the model
     * @return the location of the end of the range
     * @throws BadLocationException
     *         if the range is invalid
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
     *        the graphics context
     * @param x
     *        the starting X coordinate >= 0
     * @param y
     *        the starting Y coordinate >= 0
     * @param p0
     *        the beginning position in the model >= 0
     * @param p1
     *        the ending position in the model >= 0
     * @return the location of the end of the range
     * @throws BadLocationException
     *         if the range is invalid
     */
    protected int drawSelectedText( Graphics g, int x, int y, int p0, int p1)
        throws BadLocationException
    {
      return drawText( g, x, y, p0, p1, true);
    }

    /**
     * Renders the given range in the model as selected or unselected text. This
     * is implemented to paint colors and fonts based upon the category-to-color
     * translations.
     * 
     * @param g
     *        the graphics context
     * @param x
     *        the starting X coordinate
     * @param y
     *        the starting Y coordinate
     * @param p0
     *        the beginning position in the model
     * @param p1
     *        the ending position in the model
     * @return the X coordinate of the end of the range
     * @throws BadLocationException
     *         if the range is invalid
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
        // flush what we have..
        doc.getText( p0, flushTo - p0, text);
        x = drawHighlightedText( category, selected, text, x, y, g, p0);
        p0 = flushTo;
      }

      return x;
    }

    /**
     * Draws the given text, using the color and font indicated by the given
     * <code>Category</code>.
     * 
     * @param category
     *        the category we are painting. Used to determine color and font.
     * @param text
     *        the source of the text to draw
     * @param x
     *        the X origin >= 0
     * @param y
     *        the Y origin >= 0
     * @param g
     *        the graphics context
     * @param startOffset
     *        starting offset of the text in the document >= 0
     * @return the X location at the end of the rendered text
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
     * Repaint the region of change covered by the given document event. If
     * lines are added or removed, damages the whole view. Overridden to update
     * the line marks.
     */
    protected void updateDamage( DocumentEvent changes, Shape a, ViewFactory f)
    {
      Element elem = getElement();
      DocumentEvent.ElementChange ec = changes.getChange( elem);

      Element[] added = (ec != null) ? ec.getChildrenAdded() : null;
      Element[] removed = (ec != null) ? ec.getChildrenRemoved() : null;
      if (((added != null) && (added.length > 0))
          || ((removed != null) && (removed.length > 0))) {
        // lines were added or removed...
        if (added != null) {
          for (int i = 0; i < added.length; i++ ) {
          }
        }
        if (removed != null) {
          for (int i = 0; i < removed.length; i++ ) {
            removeMark( removed[i]);
          }
        }
      }
      else {
        if (changes.getType() == DocumentEvent.EventType.INSERT) {
          int i = 0;
        }
        else if (changes.getType() == DocumentEvent.EventType.REMOVE) {
          // TODO wenn man in einem multiline comment das erster Sternchen
          // l�scht, klappts nich mit den folgezeilen
          int i = 0;
        }

      }
      super.updateDamage( changes, a, f);
    }

    ///////////////////////////////////////////////////////////////////
    // unsafe line marking
    ///////////////////////////////////////////////////////////////////
    /**
     * Holds the marks for lines that are unsafe to restart scanning.
     */
    private Map unsafeLineMarks = null;

    private Object getMark( Element line)
    {
      if (unsafeLineMarks == null) {
        return null;
      }
      return unsafeLineMarks.get( line);
    }

    /**
     * @param line
     *        TODO
     * @return
     */
    private boolean hasMark( Element line)
    {
      if (unsafeLineMarks == null) {
        return false;
      }
      return this.unsafeLineMarks.containsKey( line);
    }

    /**
     * Adds a mark that specifies a line as a position where to start the
     * scanning is <strong>unsafe </strong>.
     * 
     * @param line
     * @param value
     */
    private void putMark( Element line, Object value)
    {
      if (line == null) {
        throw new NullPointerException( "line");
      }
      // lazy creation
      if (unsafeLineMarks == null) {
        unsafeLineMarks = new HashMap();
      }
      unsafeLineMarks.put( line, value);
      //      System.out.println( "unsafeLineMarks put()=" + unsafeLineMarks);
    }

    private Object removeMark( Element line)
    {
      if (unsafeLineMarks == null) {
        return null;
      }
      return unsafeLineMarks.remove( line);
    }

    /**
     * Eine Queue f�r <code>Token</code>s.
     * 
     * @author Martin Weber
     */
    private final class TokenQueue
    {
      private final HighlightedDocument doc;

      private transient Token tokenBuf;

      private transient int seg2docOffset;

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
       * @param p0
       * @param doc
       *        the document model.
       * @param line
       *        the starting line in the model.
       */
      public void initialise( int p0, Segment lexerInput, int seg2docOffset)
      {
        //System.out.println( "# TokenQueue.initialise()");

        this.seg2docOffset = seg2docOffset;
        HighlightingContext.this.categoriser.openInput( lexerInput);

        do {
          remove();
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
          System.out.println( "tok=" + tokenBuf + ", seg2docoffs="
              + seg2docOffset);
          try {
            Segment txt = new Segment();
            doc.getText( tokenBuf.start, tokenBuf.length, txt);
            System.out.println( ", '" + txt + "'");
          }
          catch (BadLocationException ex) {
          }
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
        tokenBuf = HighlightingContext.this.categoriser.nextToken( doc,
            tokenBuf);
        tokenBuf.start -= seg2docOffset;
      }

    } // TokenQueue

  }

}