/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.text.*;

import de.marw.javax.swing.text.highlight.categoriser.Categoriser;
import de.marw.javax.swing.text.highlight.categoriser.Token;


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
 * @version $Revision$
 */
public class HighlightingContext extends StyleContext implements ViewFactory
{

  /**
   * Key to be used in AttributeSets of styles holding a value of the
   * <code>Category</code> type.
   */
  public static final Object CATEGORY_ATTRIBUTE = new CategoryAttribKey();

  private static final Object unsafeRestartHere = new Object();

  private static final Object unsafeRestartFollows = new Object();

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
    for (int i = 0; i < categories.length; i++) {
      Category cat = categories[i];
      Style parent = getStyle( cat.getName());
      if (parent == null) {
        // add style for category
        parent = addStyle( cat.getName(), root);
      }
      // add attributes for category style...
      Style style = addStyle( null, parent);
      categoryStyles[cat.ordinal()] = style;
      style.addAttribute( CATEGORY_ATTRIBUTE, cat);
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

  /**
   * Instances of this class serve as a key in AttributeSet's. 
   */
  private static class CategoryAttribKey
  {
    public String toString()
    {
      return "category";
    }
  }

  /**
   * Invalidates the cached fonts and colors.
   */
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
   * A View that uses the lexical information to determine the style
   * characteristics of the text that it renders. This simply colorizes the
   * various categories and assumes a constant font family and size. <br>
   * The view represents each child element as a line of text.
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
     * number of the line we have to start scanning. This will be updated due to
     * character insertions or deletions.
     */
    private int requiredScanStart;

    /**
     * Used to communicate between <code>drawLine()</code> and
     * <code>paint()</code> about lines that need to be rendered due to
     * multiline tokens determined by drawLine().
     */
    private int forceRepaintTo;

    /**
     * Construct a simple colorized view of java text.
     */
    HiliteView( Element elem)
    {
      super( elem);
      tokenQueue = new TokenQueue( (HighlightedDocument) elem.getDocument());
      requiredScanStart = 0;
    }

    /**
     * Renders using the given rendering surface and area on that surface. This
     * is implemented to first initialise the lexical scanner, then having the
     * superclass doing the rendering and finally invalidating the scanner.
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
      Rectangle alloc = (Rectangle) a;
      JTextComponent host = (JTextComponent) getContainer();
      Color normalColor = (host.isEnabled()) ? host.getForeground() : host
          .getDisabledTextColor();
      Caret c = host.getCaret();
      selectedColor = c.isSelectionVisible() ? host.getSelectedTextColor()
          : normalColor;
      updateMetrics();

      // If the lines are clipped then we don't expend the effort to
      // try and paint them. Since all of the lines are the same height
      // with this object, determination of what lines need to be repainted
      // is quick.
      Rectangle clip = g.getClipBounds();
      int fontHeight = metrics.getHeight();
      int heightBelow = (alloc.y + alloc.height) - (clip.y + clip.height);
      int linesBelow = Math.max( 0, heightBelow / fontHeight);
      int heightAbove = clip.y - alloc.y;
      int linesAbove = Math.max( 0, heightAbove / fontHeight);
      int linesTotal = alloc.height / fontHeight;

      if (alloc.height % fontHeight != 0) {
        linesTotal++;
      }
      // update the visible lines
      Element map = getElement();
      int lineCount = map.getElementCount();
      int endLine = Math.min( lineCount, linesTotal - linesBelow);
      endLine = Math.max( endLine - 1, 0);
      Document doc = map.getDocument();
      Element line1 = map.getElement( linesAbove);
      Element line2 = map.getElement( endLine);
      int p0 = line1.getStartOffset();
      int p1 = Math.min( doc.getLength(), line2.getEndOffset());
      /*
       * (Re-)Initialise the categoriser to point to the appropriate token for
       * the given start position needed for rendering. The start position
       * adjustment is required by text runs that span multiple line (eg
       * '/*'-comments).
       */
      int p0Adj = requiredScanStart;
      if (p0Adj > 0) {
        p0Adj = Math.min( getAdjustedStart( linesAbove), p0Adj);
      }
      Segment lexerInput = new Segment();
      try {
        doc.getText( p0Adj, p1 - p0Adj, lexerInput);
        tokenQueue.open( p0, lexerInput, lexerInput.offset - p0Adj);
      }
      catch (BadLocationException ex) {
        // we cannot paint highlighted here, so we will render as normal text.
        // the painting logic in PlainView might raise a BadLocationException on
        // its own.
        throw new /* StateInvariantError */Error( "Can't render", ex);
      }
      forceRepaintTo = -1; // gets set by drawLine()
      super.paint( g, a);
      // rendering complete, notify categoriser
      tokenQueue.close();

      /*
       * force the component to repaint the following lines, thus supporting
       * highlighting tokens that span mutliple lines.
       */
      if (forceRepaintTo > endLine) {
        System.out.println( "# forced repaint of " + (endLine + 1) + ".."
            + forceRepaintTo);
        damageLineRange( endLine + 1, forceRepaintTo, a, host);
      }
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
      //System.out.println( "# drawLine() " + lineIndex + " -------");
      super.drawLine( lineIndex, g, x, y);

      /*
       * check whether the following line(s) are unsafe to restart scanning and
       * mark these
       */
      Token token = null;
      if ( !tokenQueue.isEmpty()) {
        Element rootElement = getElement();
        Document doc = (HighlightedDocument) rootElement.getDocument();
        Element line = rootElement.getElement( lineIndex);
        int endOffset = Math.min( doc.getLength(), line.getEndOffset());

        // get last token highlighted
        token = tokenQueue.peek();
        if (token.multiline && token.start < endOffset) {
          line = rootElement.getElement( lineIndex);
          // the following line(s) are unsafe to restart scanning
          Object mark = getMark( line);
          if (mark != HighlightingContext.unsafeRestartHere)
            putMark( line, HighlightingContext.unsafeRestartFollows);
          int forceRepaintFrom = lineIndex;
          // mark following lines as unsafe to restart scanning
          while (token.start + token.length >= endOffset) {
            lineIndex++;
            line = rootElement.getElement( lineIndex);
            if (line == null) {
              break; // end of document
            }
            putMark( line, HighlightingContext.unsafeRestartHere);
            endOffset = Math.min( doc.getLength(), line.getEndOffset());
          } // while

          requiredScanStart = lineIndex + 1; // document has been scanned up to
          // here

          /*
           * force the component to repaint the following lines, thus supporting
           * highlighting tokens that span mutliple lines.
           */
          if (lineIndex > forceRepaintFrom && lineIndex > forceRepaintTo) {
            // System.out.println( "# force repaint of " + forceRepaintFrom +
            // ".." + lineIndex);
            forceRepaintTo = lineIndex;
          }
        }
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
      for (; lineIndex > 0; lineIndex--) {
        Element line = element.getElement( lineIndex);
        if ( !hasMark( line)) {
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
      Document doc = (HighlightedDocument) getDocument();

      Segment text = getLineBuffer();
      Token token = null;
      while (p0 < p1) {
        Category category = Category.NORMAL;
        int flushTo = p1;
        if ( !tokenQueue.isEmpty()) {
          // get token
          token = tokenQueue.peek();
          if (token.start > p0) {
            // gap between tokens: draw as normal text
            category = Category.NORMAL;
            flushTo = Math.min( token.start, p1);
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
        if (flushTo > p0) {
          doc.getText( p0, flushTo - p0, text);
          x = drawHighlightedText( category, selected, text, x, y, g, p0);
          p0 = flushTo;
        }
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

        if ( !g.getColor().equals( fg)) {
          g.setColor( fg);
        }
        if ( !g.getFont().equals( font)) {
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

    /*
     * @see javax.swing.text.View#insertUpdate(javax.swing.event.DocumentEvent,
     *      java.awt.Shape, javax.swing.text.ViewFactory)
     */
    /*
     * public void insertUpdate( DocumentEvent e, Shape a, ViewFactory f) {
     * System.out.println( "### insertUpdate()--------------------");
     * super.insertUpdate( e, a, f); System.out.println( "### insertUpdate()
     * DONE --------------------"); }
     */
    /*
     * @see javax.swing.text.View#removeUpdate(javax.swing.event.DocumentEvent,
     *      java.awt.Shape, javax.swing.text.ViewFactory)
     */
    /*
     * public void removeUpdate( DocumentEvent e, Shape a, ViewFactory f) {
     * System.out.println( "### removeUpdate()--------------------");
     * super.removeUpdate( e, a, f); System.out.println( "### removeUpdate()
     * DONE --------------------"); }
     */

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
          for (int i = 0; i < added.length; i++) {
          }
        }
        if (removed != null) {
          for (int i = 0; i < removed.length; i++) {
            removeMark( removed[i]);
          }
        }
      }
      else {
        int lineIdx = elem.getElementIndex( changes.getOffset());
        Element line = elem.getElement( lineIdx);
        Object mark = getMark( line);
        if (mark != null) {
          if (mark == HighlightingContext.unsafeRestartHere) {
            requiredScanStart = Math.min( requiredScanStart, lineIdx - 1);
          }
          else if (mark == HighlightingContext.unsafeRestartFollows) {
            requiredScanStart = Math.min( requiredScanStart, lineIdx);
          }
          int endline = removeConsecutiveMarks( lineIdx);
          damageLineRange( lineIdx, endline, a, getContainer());
        }
      }
      super.updateDamage( changes, a, f);
    }

    /**
     * @param lineIndex
     *        the line number where to start removing line marks.
     */
    private int removeConsecutiveMarks( int lineIndex)
    {
      Element element = getElement();
      for (;; lineIndex++) {
        Element line = element.getElement( lineIndex);
        if ( !hasMark( line)) {
          break;
        }
        removeMark( line);
      }
      return lineIndex;
    }

    ///////////////////////////////////////////////////////////////////
    // unsafe line marking
    ///////////////////////////////////////////////////////////////////
    /**
     * Holds the marks for lines that are unsafe to restart scanning.
     */
    private Map unsafeLineMarks = null;

    /**
     * Gets the mark that specifies a line as a position where to start the
     * scanning is <strong>unsafe </strong>.
     * 
     * @param line
     *        The line to get the mark for.
     * @return The marking object or <code>null</code> if the line is not
     *         marked.
     */
    private Object getMark( Element line)
    {
      if (unsafeLineMarks == null || line == null) {
        return null;
      }
      return unsafeLineMarks.get( line);
    }

    /**
     * Returns whether a line is marked as a position where to start the
     * scanning is <strong>unsafe </strong>.
     * 
     * @param line
     *        The line to get the mark for.
     * @return <code>true</code> if the line is marked, otherwise
     *         <code>false</code>.
     */
    private boolean hasMark( Element line)
    {
      if (unsafeLineMarks == null || line == null) {
        return false;
      }
      return this.unsafeLineMarks.containsKey( line);
    }

    /**
     * Adds a mark that specifies a line as a position where to start the
     * scanning is <strong>unsafe </strong>.
     * 
     * @param line
     *        The line to mark.
     * @param value
     *        The marking object.
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
    }

    /**
     * Removes a mark that specifies a line as a position where to start the
     * scanning is <strong>unsafe </strong>.
     * 
     * @param line
     *        The line to unmark.
     * @return The marking object or <code>null</code> if the line is not
     *         marked.
     */
    private Object removeMark( Element line)
    {
      if (unsafeLineMarks == null || line == null) {
        return null;
      }
      return unsafeLineMarks.remove( line);
    }

    /**
     * Eine Queue für <code>Token</code>s.
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
       *        the beginning position in the model >= 0
       * @param lexerInput
       *        the input for the categoriser.
       * @param seg2docOffset
       *        the offset of the segment relative to the document the segement
       *        comes from.
       */
      public void open( int p0, Segment lexerInput, int seg2docOffset)
      {
        //System.out.println( "# TokenQueue.initialise()");

        this.seg2docOffset = seg2docOffset;
        HighlightingContext.this.categoriser.openInput( lexerInput);

        do {
          remove();
        } while ( !isEmpty() && tokenBuf.start + tokenBuf.length <= p0);
      }

      /**
       * Closes this Queue and notifies the categoriser of the end of the
       * current scanninng process.
       */
      public void close()
      {
        HighlightingContext.this.categoriser.closeInput();
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