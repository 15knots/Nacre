// $Id$
/*
 * Copyright 2005 by Martin Weber
 */

package de.marw.javax.swing.text.highlight;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.HashMap;
import java.util.Map;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
import javax.swing.text.Caret;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.JTextComponent;
import javax.swing.text.PlainView;
import javax.swing.text.Segment;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import de.marw.javax.swing.text.highlight.categoriser.Categoriser;
import de.marw.javax.swing.text.highlight.categoriser.Token;


/**
 * A View that uses the lexical information to determine the style
 * characteristics of the text that it renders. This simply colorizes the
 * various categories and assumes a constant font family and size. <br>
 * The view represents each child element as a line of text.
 */
public class HiliteView extends PlainView
{

  /**
   * the Categoriser used for highlighting text of this document or
   * <code>null</code> if no highlighting is to be done.
   */
  private final Categoriser categoriser;

  /**
   * The styles representing the actual categories.
   */
  private transient CategoryStyles categoryStyles;

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
   * Responsible for invalidateng the font and color caches upon style changes.
   */
  private transient final CacheInvalidator cacheInvalidator;

  /**
   * 
   */
  private final transient TokenQueue tokenQueue;

  /**
   * the last known host's (the text component's) color used to render selected
   * text.
   */
  private transient Color selectedColor;

  /**
   * the last known host's (the text component's) color used to render
   * unselected text.
   */
  private transient Color normalColor;

  /**
   * last known host's (the text component's) font.
   */
  private transient Font hostFont;

  /**
   * number of the line we have to start scanning. This will be updated due to
   * character insertions or deletions.
   */
  private transient int requiredScanStart;

  /**
   * Used to communicate between <code>drawLine()</code> and
   * <code>paint()</code> about lines that need to be rendered due to
   * multiline tokens determined by drawLine().
   */
  private transient int forceRepaintTo;

  /**
   * Construct a simple colorized view of java text.
   * 
   * @param categoriser
   *        the Categoriser used for highlighting text of this document.
   * @param styles
   *        the set of color and font style informations used used to render
   *        highlighted text.
   */
  public HiliteView( Element elem, Categoriser categoriser,
      CategoryStyles styles) {
    super( elem);
    if (categoriser == null) {
      throw new NullPointerException( "categoriser");
    }
    this.categoriser = categoriser;
    cacheInvalidator = new CacheInvalidator();
    setCategoryStyles( styles);
    this.tokenQueue = new TokenQueue( elem.getDocument());
    requiredScanStart = 0;
    // System.out.println( "Ctor: " + this);
  }

  /**
   * Sets a new set of color and font style informations used to render
   * highlighted text.
   * 
   * @param styles
   *        the set of color and font style informations used used to render
   *        highlighted text.
   */
  public void setCategoryStyles( CategoryStyles styles)
  {
    if (styles == null) {
      throw new NullPointerException( "styles");
    }
    if (categoryStyles != null) {
      categoryStyles.removeCategoryStylesListener( cacheInvalidator);
    }
    categoryStyles = styles;
    categoryStyles.addCategoryStylesListener( cacheInvalidator);

    Container host = getContainer();
    if (host != null)
      host.repaint();
  }

  /**
   * Renders using the given rendering surface and area on that surface. This is
   * implemented to first initialise the lexical scanner, then having the
   * superclass doing the rendering and finally invalidating the scanner.
   * 
   * @param g
   *        the rendering surface to use
   * @param a
   *        the allocated region to render into
   * @see View#paint(java.awt.Graphics, java.awt.Shape)
   */
  @Override public void paint( Graphics g, Shape a)
  {
    JTextComponent host = (JTextComponent) getContainer();
    {
      normalColor = (host.isEnabled()) ? host.getForeground() : host
          .getDisabledTextColor();
      Caret c = host.getCaret();
      selectedColor = c.isSelectionVisible() ? host.getSelectedTextColor()
          : normalColor;
      // host font changes invalidate the font cache
      Font f = host.getFont();
      metrics = host.getFontMetrics( f);
      if (hostFont != f) {
        categoryFonts = null; // invalidate cache
        hostFont = f;
      }
    }

    // If the lines are clipped then we don't expend the effort to
    // try and paint them. Since all of the lines are the same height
    // with this object, determination of what lines need to be repainted
    // is quick.
    int linesBelow, linesAbove, linesTotal;
    {
      Rectangle alloc = (Rectangle) a;
      Rectangle clip = g.getClipBounds();
      int fontHeight = metrics.getHeight();
      int heightBelow = (alloc.y + alloc.height) - (clip.y + clip.height);
      linesBelow = Math.max( 0, heightBelow / fontHeight);
      int heightAbove = clip.y - alloc.y;
      linesAbove = Math.max( 0, heightAbove / fontHeight);
      linesTotal = alloc.height / fontHeight;

      if (alloc.height % fontHeight != 0) {
        // linesTotal++;
      }
    }
    // update the visible lines
    Element map = getElement();
    int lineCount = map.getElementCount();
    int startLine = Math.min( requiredScanStart, linesAbove);
    startLine = Math.min( startLine, lineCount - 1);
    int endLine = Math.min( lineCount, linesTotal - linesBelow);
    endLine = Math.max( endLine - 1, 0);

    if (false)
      System.out.println( "# paint() lines: " + startLine + ", " + linesAbove
          + ".." + (endLine) + " -------");
    Document doc = map.getDocument();
    Element line1 = map.getElement( startLine);
    // Element line2 = map.getElement( endLine);
    // bisschen mehr Text scannen für besseres forcedRepaint..
    Element line2 = map.getElement( Math.min( endLine + 5, lineCount - 1));
    int p0 = line1.getStartOffset();
    int p1 = Math.min( doc.getLength(), line2.getEndOffset());
    /*
     * (Re-)Initialise the categoriser to point to the appropriate token for the
     * given start position needed for rendering. The start position adjustment
     * is required by text runs that span multiple lines (eg '/*'-comments).
     */
    try {
      int p0Adj = p0;
      if (startLine > 0) {
        p0Adj = Math.min( getAdjustedStart( startLine), p0Adj);
      }
      Segment lexerInput = new Segment();
      doc.getText( p0Adj, p1 - p0Adj, lexerInput);
      tokenQueue.open( p0, lexerInput, lexerInput.offset - p0Adj);

      // mark lines without rendering...
      while (requiredScanStart < linesAbove && requiredScanStart < lineCount) {
        // scan current line...
        Element line = map.getElement( requiredScanStart);
        p0 = line.getStartOffset();
        p1 = Math.min( doc.getLength(), line.getEndOffset());
        consumeTokens( p0, p1);
        // line is scanned, determine marks
        requiredScanStart = addMarks( map, requiredScanStart) + 1;
      }
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
      if (false)
        System.out.println( "# forced repaint of " + (endLine + 1) + ".."
            + forceRepaintTo);
      damageLineRange( endLine + 1, forceRepaintTo, a, host);
    }
  }

  /**
   * Consumes the Tokens by scanning the text between the specified positions.
   * 
   * @param p0
   *        the beginning position in the model
   * @param p1
   *        the ending position in the model
   */
  private void consumeTokens( int p0, int p1)
  {
    if ( !tokenQueue.isEmpty()) {
      Token token = null;
      // scan current line...
      while (p0 < p1) {
        // get token
        token = tokenQueue.peek();
        if (token.start > p0) {
          // gap between tokens
          p0 = Math.min( token.start, p1);
        }
        if (token.start <= p0) {
          p0 = Math.min( token.start + token.length, p1);
          if (token.start + token.length < p1) {
            // token was completely consumed: remove from queue
            tokenQueue.remove();
          }
        }
      }
    }
  }

  /**
   * Renders a line of text, suppressing whitespace at the end and expanding any
   * tabs. The field <code>requiredScanStart</code> is updated.
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
  @Override protected void drawLine( int lineIndex, Graphics g, int x, int y)
  {
    // System.out.println( "# drawLine() " + lineIndex + " -------");
    super.drawLine( lineIndex, g, x, y);
    int forceRepaintFrom = lineIndex;

    // Check which line(s) following are unsafe to restart scanning and mark
    // these
    lineIndex = addMarks( getElement(), lineIndex);
    requiredScanStart = lineIndex + 1; // document has been scanned up to here
    /*
     * force the component to repaint the following lines, thus supporting
     * highlighting tokens that span multiple lines.
     */
    if (lineIndex > forceRepaintFrom && lineIndex > forceRepaintTo) {
      // System.out.println( "# forcing repaint up to " + lineIndex);
      forceRepaintTo = lineIndex;
    }
  }

  /**
   * Checks whether the line(s) following the specified index are unsafe to
   * restart scanning and marks these.
   * 
   * @param rootElement
   *        the root element of the view.
   * @param lineIndex
   *        number of the line where to start.
   * @return the number of the last line that was checked.
   */
  private int addMarks( Element rootElement, int lineIndex)
  {
    if ( !tokenQueue.isEmpty()) {
      Document doc = rootElement.getDocument();
      Element line = rootElement.getElement( lineIndex);

      // get last token highlighted
      Token token = tokenQueue.peek();
      int endOffset = Math.min( doc.getLength(), line.getEndOffset());
      if (token.multiline && token.start < endOffset) {
        line = rootElement.getElement( lineIndex);
        synchronized (UNSAFE_LINE_MARKS_LOCK ) {
          // the following line(s) are unsafe to restart scanning
          ScanState mark = getMark( line);
          if (mark != ScanState.UnsafeRestartHere)
            putMark( line, ScanState.UnsafeRestartFollows);
          // mark following lines as unsafe to restart scanning
          for (int numLines = rootElement.getElementCount() - 1; token.start
              + token.length >= endOffset
              && lineIndex < numLines;) {
            lineIndex++;
            line = rootElement.getElement( lineIndex);
            putMark( line, ScanState.UnsafeRestartHere);
            endOffset = Math.min( doc.getLength(), line.getEndOffset());
          } // while
        } // synchronized
      }
    }
    return lineIndex;
  }

  /**
   * Renders the given range in the model as unselected text. This is
   * implemented to paint colors based upon the category-to-color translations.
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
  @Override protected int drawUnselectedText( Graphics g, int x, int y, int p0,
      int p1) throws BadLocationException
  {
    return drawText( g, x, y, p0, p1, false);
  }

  /**
   * Renders the given range in the model as selected text. This is implemented
   * to render the text in the background color specified in the hosting
   * component. It assumes the highlighter will render the selected background.
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
    Document doc = getDocument();

    Segment text = getLineBuffer();
    Token token = null;
    while (p0 < p1) {
      Category category = null;
      int flushTo = p1;

      if ( !tokenQueue.isEmpty()) {
        // get token
        token = tokenQueue.peek();
        // Abschnitte ohne Kategorie zusammenziehen...
        for (; !tokenQueue.isEmpty() && token.category == null; token = tokenQueue
            .peek()) {
          tokenQueue.remove();
        }

        if (token.start > p0) {
          // gap between tokens: draw as normal text
          category = null;
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

      if (false) {
        System.out.print( "painting '" + text + "', offs=" + startOffset
            + ", cat=" + category + ", sel=" + selected);
        System.out.println( ", color=" + fg.getRGB() + ", font="
            + font.getFontName());
      }
      if ( !g.getColor().equals( fg)) {
        g.setColor( fg);
      }
      if ( !g.getFont().equals( font)) {
        g.setFont( font);
      }

      x = Utilities.drawTabbedText( text, x, y, g, this, startOffset);
    }
    return x;
  }

  /**
   * Overwritten to remove listeners when going to be garbage collected.
   */
  @Override public void setParent( View parent)
  {
    if (parent == null) {
      // we are going to be garbage collected
      // System.out.println( "garbage: " + this);
      categoryStyles.removeCategoryStylesListener( cacheInvalidator);
    }
    super.setParent( parent);
  }

  /*
   * @see javax.swing.text.View#insertUpdate(javax.swing.event.DocumentEvent,
   *      java.awt.Shape, javax.swing.text.ViewFactory)
   */
  /*
   * public void insertUpdate( DocumentEvent e, Shape a, ViewFactory f) {
   * System.out.println( "### insertUpdate()--------------------");
   * super.insertUpdate( e, a, f); System.out.println( "### insertUpdate() DONE
   * --------------------"); }
   */
  // /*
  // * @see
  // javax.swing.text.View#removeUpdate(javax.swing.event.DocumentEvent,
  // * java.awt.Shape, javax.swing.text.ViewFactory)
  // */
  // public void removeUpdate( DocumentEvent e, Shape a, ViewFactory f)
  // {
  // System.out.println( "### removeUpdate()--------------------");
  // super.removeUpdate( e, a, f);
  // System.out.println( "### removeUpdate() DONE --------------------");
  // }
  /**
   * Repaint the region of change covered by the given document event. If lines
   * are added or removed, damages the whole view. Overridden to update the line
   * marks. The field <code>requiredScanStart</code> is updated.
   */
  @Override protected void updateDamage( DocumentEvent changes, Shape a,
      ViewFactory f)
  {
    Element elem = getElement();
    DocumentEvent.ElementChange ec = changes.getChange( elem);

    Element[] added = (ec != null) ? ec.getChildrenAdded() : null;
    Element[] removed = (ec != null) ? ec.getChildrenRemoved() : null;
    if (((added != null) && (added.length > 0))
        || ((removed != null) && (removed.length > 0))) {
      // lines were added or removed...
      if (removed != null) {
        for (int i = 0; i < removed.length; i++) {
          removeMark( removed[i]);
        }
      }
    }
    else {
      int lineIdx = elem.getElementIndex( changes.getOffset());
      Element line = elem.getElement( lineIdx);
      synchronized (UNSAFE_LINE_MARKS_LOCK ) {
        ScanState mark = getMark( line);
        if (mark != null) {
          if (mark == ScanState.UnsafeRestartHere) {
            requiredScanStart = Math.min( requiredScanStart, lineIdx - 1);
          }
          else if (mark == ScanState.UnsafeRestartFollows) {
            requiredScanStart = Math.min( requiredScanStart, lineIdx);
          }
          int endline = removeConsecutiveMarks( lineIdx);
          damageLineRange( lineIdx, endline, a, getContainer());
        }
      }
    }
    super.updateDamage( changes, a, f);
  }

  /**
   * Fetch a reasonable location to start scanning given the desired start
   * location. This allows for adjustments needed to accommodate multiline
   * comments.
   * 
   * @param lineIndex
   *        The number of the line to render.
   * @return the adjusted start position in the document model which is greater
   *         or equal than zero.
   */
  private int getAdjustedStart( int lineIndex)
  {
    Element element = getElement();
    // walk backwards until we get an untagged line...
    // System.out.print( "# find start in line " + lineIndex);
    for (; lineIndex > 0; lineIndex--) {
      Element line = element.getElement( lineIndex);
      if ( !hasMark( line)) {
        // System.out.println( " found start in " + lineIndex);
        return line.getStartOffset();
      }
      // System.out.print( "," + lineIndex);
    }
    // System.out.println( " not found (0)");
    return 0;
  }

  /**
   * @param lineIndex
   *        the line number where to start removing line marks.
   * @return the line number where the last line mark was removed.
   */
  private int removeConsecutiveMarks( int lineIndex)
  {
    synchronized (UNSAFE_LINE_MARKS_LOCK ) {
      if (unsafeLineMarks != null) {
        Element element = getElement();
        for (;; lineIndex++) {
          Element line = element.getElement( lineIndex);
          if (line == null || !unsafeLineMarks.containsKey( line)) {
            break;
          }
          removeMark( line);
        }
      }
    }
    return lineIndex;
  }

  // /////////////////////////////////////////////////////////////////
  // unsafe line marking
  // /////////////////////////////////////////////////////////////////
  /**
   * The locking object for atomic operations that rely on
   * <code>unsafeLineMarks</code>.
   */
  private final Object UNSAFE_LINE_MARKS_LOCK = new Object();

  /**
   * Holds the marks for lines that are unsafe to restart scanning. Lazily
   * created.
   */
  private Map<Element, ScanState> unsafeLineMarks = null;

  /**
   * Gets the mark that specifies a line as a position where to start the
   * scanning is <strong>unsafe </strong>.
   * 
   * @param line
   *        The line to get the mark for or <code>null</code>.
   * @return The marking object or <code>null</code> if the line is not
   *         marked.
   */
  private ScanState getMark( Element line)
  {
    if (line != null) {
      synchronized (UNSAFE_LINE_MARKS_LOCK ) {
        if (unsafeLineMarks != null) {
          return unsafeLineMarks.get( line);
        }
      }
    }
    return null;
  }

  /**
   * Returns whether a line is marked as a position where to start the scanning
   * is <strong>unsafe </strong>.
   * 
   * @param line
   *        The line to get the mark for or <code>null</code>.
   * @return <code>true</code> if the line is marked, otherwise
   *         <code>false</code>.
   */
  private boolean hasMark( Element line)
  {
    if (line != null) {
      synchronized (UNSAFE_LINE_MARKS_LOCK ) {
        if (unsafeLineMarks != null) {
          return this.unsafeLineMarks.containsKey( line);
        }
      }
    }
    return false;
  }

  /**
   * Adds a mark that specifies a line as a position where to start the scanning
   * is <strong>unsafe </strong>.
   * 
   * @param line
   *        The line to mark.
   * @param value
   *        The marking object.
   */
  private void putMark( Element line, ScanState value)
  {
    if (line == null) {
      throw new NullPointerException( "line");
    }
    synchronized (UNSAFE_LINE_MARKS_LOCK ) {
      // lazy creation
      if (unsafeLineMarks == null) {
        unsafeLineMarks = new HashMap<Element, ScanState>();
      }
      unsafeLineMarks.put( line, value);
    }
  }

  /**
   * Removes a mark that specifies a line as a position where to start the
   * scanning is <strong>unsafe </strong>.
   * 
   * @param line
   *        The line to unmark or <code>null</code>.
   * @return The marking object or <code>null</code> if the line is not
   *         marked.
   */
  private ScanState removeMark( Element line)
  {
    if (line != null) {
      synchronized (UNSAFE_LINE_MARKS_LOCK ) {
        if (unsafeLineMarks != null) {
          return unsafeLineMarks.remove( line);
        }
      }
    }
    return null;
  }

  /**
   * Fetches the foreground color to use for a text run with the given category.
   * The color is cached in a table to facilitate relatively fast access to use
   * in conjunction with the categoriser.
   */
  private Color getForeground( Category category)
  {
    if (category == null) {
      // treat as normal text, use the JTextComponent's font and color
      return normalColor;
    }

    if (categoryColors == null) {
      categoryColors = new Color[Category.values().length];
    }
    int categoryCode = category.ordinal();
    Color c = categoryColors[categoryCode];
    if (c == null && categoryStyles.isDefined( category)) {
      c = categoryStyles.getColor( category);
      categoryColors[categoryCode] = c;
    }
    if (c == null)
      c = normalColor;
    return c;
  }

  /**
   * Fetches the font to use for a text run with the given category. The font is
   * stored in a table to facilitate relatively fast access to use in
   * conjunction with the categoriser.
   */
  private Font getFont( Category category)
  {
    if (category == null) {
      // treat as normal text, use the JTextComponent's font and color
      return hostFont;
    }

    if (categoryFonts == null) {
      categoryFonts = new Font[Category.values().length];
    }
    int categoryCode = category.ordinal();
    Font font = categoryFonts[categoryCode];
    if (font == null && categoryStyles.isDefined( category)) {
      font = hostFont;
      int style = categoryStyles.getStyle( category);
      if (style != Font.PLAIN) {
        font = font.deriveFont( style);
      }
      categoryFonts[categoryCode] = font;
    }
    if (font == null)
      font = hostFont;
    return font;
  }

  /**
   * States used to look up a reasonable scanner start position.
   * 
   * @see HiliteView#getAdjustedStart(int)
   * @author Martin Weber
   */
  private enum ScanState {
    /**
     * Line mark: this line is unsafe to restart scanning.
     */
    UnsafeRestartHere,
    /**
     * Line mark: this line is safe to restart scanning, the following line(s)
     * are unsafe to restart scanning.
     */
    UnsafeRestartFollows
    ;
  }

  /**
   * Invalidates the cached fonts and colors.
   */
  private class CacheInvalidator implements CategoryStylesListener
  {
    /**
     * @see de.marw.javax.swing.text.highlight.CategoryStylesListener#styleChanged(de.marw.javax.swing.text.highlight.CategoryStylesEvent)
     */
    public void styleChanged( CategoryStylesEvent evt)
    {

      int categoryCode = evt.getCategory().ordinal();
      // invalidate caches
      if (categoryFonts != null) {
        HiliteView.this.categoryFonts[categoryCode] = null;
      }
      if (categoryColors != null) {
        HiliteView.this.categoryColors[categoryCode] = null;
      }
      HiliteView.this.getContainer().repaint();
    }
  }

  /**
   * Eine Queue für <code>Token</code>s.
   * 
   * @author Martin Weber
   */
  private final class TokenQueue
  {
    private final Document doc;

    private transient Token tokenBuf;

    private transient int seg2docOffset;

    /**
     * @param doc
     */
    public TokenQueue( Document doc) {
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
      // System.out.println( "# TokenQueue.initialise()");

      this.seg2docOffset = seg2docOffset;
      HiliteView.this.categoriser.openInput( lexerInput);

      do {
        remove();
      } while ( !isEmpty() && tokenBuf.start + tokenBuf.length <= p0);
    }

    /**
     * Closes this Queue and notifies the categoriser of the end of the current
     * scanninng process.
     */
    public void close()
    {
      HiliteView.this.categoriser.closeInput();
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
        System.out.print( "tok=" + tokenBuf + ", seg2docoffs=" + seg2docOffset);
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
     * Removes the head of this queue and retrieves the next token. Obtains the
     * next token from the categoriser and adjusts the token's start position
     * relative to the document.
     */
    public void remove()
    {
      tokenBuf = HiliteView.this.categoriser.nextToken( doc, tokenBuf);
      tokenBuf.start -= seg2docOffset;
    }

  } // TokenQueue

}
