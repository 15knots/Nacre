/*
 * @(#)CContext.java 1.2 99/05/27 Copyright (c) 1998 Sun Microsystems, Inc. All
 * Rights Reserved. This software is the confidential and proprietary
 * information of Sun Microsystems, Inc. ("Confidential Information"). You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Sun.
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package swing.text.highlight;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Shape;

import javax.swing.event.DocumentEvent;
import javax.swing.text.BadLocationException;
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
   * Constructs a set of styles to represent lexical token categories. By
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
        f = getFont( s);
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
     * used for text runs that spans multiple line (eg Javadoc comments). Set to
     * <code>false</code> if the categorizer needs to adjust it's starting
     * point.
     */
    private boolean lexerValid;

    /**
     * the category we are painting. Used to determine color and font.
     */
    private int     categoryPainting;

    /**
     * Construct a simple colorized view of java text.
     */
    HiliteView( Element elem)
    {
      super( elem);
      lexerValid = false;
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
     * @see View#paint
     */
    public void paint( Graphics g, Shape a)
    {
      super.paint( g, a);
    }

    protected void drawLine( int lineIndex, Graphics g, int x, int y)
    {
      lexerValid = false;
      categoryPainting = CategoryConstants.NORMAL;
      //      System.out.println( "# invalidate lexer ---------------------------");
      System.out.println( "# drawLine() " + lineIndex + " -------");
      super.drawLine( lineIndex, g, x, y);
      // notify lexer
      //TODO lexer.closeInput();
    }

    /**
     * Renders the given range in the model as normal unselected text. This is
     * implemented to paint colors based upon the category-to-color
     * translations. To reduce the number of calls to the Graphics object, text
     * is batched up until a color change is detected or the entire requested
     * range has been reached.
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
      HighlightedDocument doc = (HighlightedDocument) getDocument();
      Categoriser cato = doc.getCategoriser();
      Segment text = getLineBuffer();

      Token token = new Token();
      Segment catoInput = new Segment();

      Color lastColor = g.getColor();
      Font lastFont = g.getFont();
      //      System.out.println("paintloop ---------------------------------");

      // get token
      token = adjustScanner( doc, p0, p1, cato, catoInput, token);
      while (p0 < p1 && token.length > 0) {

        if (false) {
          // print current token
          Segment txt = new Segment();
          System.out.print( "tok= " + token);
          doc.getText( token.start, token.length, txt);
          System.out.println( ", '" + txt + "'");
        }
        int tokenEnd = token.start + token.length;

        if (token.start == p0) {
          // draw current token
          doc.getText( p0, Math.min( tokenEnd, p1) - p0, text);
          x = drawHighlightedText( token.categoryId, text, x, y, g, p0);
          categoryPainting = token.categoryId;
          p0 = tokenEnd;
          token = cato.nextToken( doc, token);
          continue;
        }
        if (token.start > p0) {
          // draw what we have
          doc.getText( p0, Math.min( token.start, p1) - p0, text);
          x = drawHighlightedText( categoryPainting, text, x, y, g, p0);
          p0 = token.start;
          continue; // with 'token.start == p0'
        }
        if (token.start < p0) {
          if (tokenEnd >= p1) {
            // draw complete line
            doc.getText( p0, Math.min( tokenEnd, p1) - p0, text);
            x = drawHighlightedText( token.categoryId, text, x, y, g, p0);
            categoryPainting = token.categoryId;
            p0 = p1;
            break; // we're done
          }
          else {
            categoryPainting = token.categoryId;
            token = cato.nextToken( doc, token);
            continue; // with 'token.start > p0'
          }
        }
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
    private int drawHighlightedText( int categoryCode, Segment text, int x,
        int y, Graphics g, int startOffset)
    {
      if (text.count > 0) {
        Color fg = getForeground( categoryCode);
        Font font = getFont( categoryCode);
        g.setColor( fg);
        g.setFont( font);

        if (true) {
          System.out.print( "painting '" + text + "', offs=" + startOffset
              + ", cat=" + categoryCode);
          System.out.println( ", color=" + fg + ", font=" + font);
        }
        x = Utilities.drawTabbedText( text, x, y, g, this, startOffset);
      }
      return x;
    }

    /**
     * Renders the given range in the model as normal unselected text. This is
     * implemented to paint colors based upon the token-to-color translations.
     * To reduce the number of calls to the Graphics object, text is batched up
     * until a color change is detected or the entire requested range has been
     * reached.
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
    protected int drawUnselectedTextX( Graphics g, int x, int y, int p0, int p1)
        throws BadLocationException
    {
      HighlightedDocument doc = (HighlightedDocument) getDocument();
      Categoriser cato = doc.getCategoriser();
      Segment text = getLineBuffer();

      Token token = null;
      Segment catoInput = new Segment();

      Color lastColor = getForeground( categoryPainting);
      Font lastFont = getFont( categoryPainting);
      int flushedIndex = p0;
      //      System.out.println("paintloop ---------------------------------");
      while (p0 < p1) {
        // get token
        token = adjustScanner( doc, p0, p1, cato, catoInput, token);
        if (true) {
          // print current token
          Segment txt = new Segment();
          System.out.print( "tok=" + token);
          try {
            doc.getText( token.start, token.length, txt);
            System.out.println( ", '" + txt + "'");
          }
          catch (BadLocationException ex) {
            // ex.printStackTrace();
            System.err.println( ex);
          }
        }

        int flushMaxIndex = Math.min( token.start + token.length, p1);
        flushMaxIndex = (flushMaxIndex <= p0) ? p1 : flushMaxIndex;
        // determine color and font
        if (token.categoryId != categoryPainting) {
          Color fg = getForeground( token.categoryId);
          Font font = getFont( token.categoryId);
          // category changed, maybe highlighting changes, too
          if (!fg.equals( lastColor) || !font.equals( lastFont)) {
            // highlighting changed, flush what we have

            if (p0 != flushedIndex) {
              g.setColor( lastColor);
              g.setFont( lastFont);

              doc.getText( p0, p0 - flushedIndex, text);
              if (true) {
                System.out.print( "painting '" + text + "'");
                System.out.println( " color=" + fg + ", font=" + font);
              }
              x = Utilities.drawTabbedText( text, x, y, g, this, p0);
              flushedIndex = p0;
            }
            lastColor = fg;
            lastFont = font;
          }
          categoryPainting = token.categoryId;
        }
        p0 = flushMaxIndex;
      }
      // notify lexer
      cato.closeInput();
      // flush remaining
      if (p1 != p0) {
        g.setColor( lastColor);
        g.setFont( lastFont);
        if (true) {
          System.out.print( "painting '" + text + "'");
          System.out.println( " color=" + lastColor + ", font=" + lastFont);
        }
        doc.getText( p0, p1 - p0, text);
        x = Utilities.drawTabbedText( text, x, y, g, this, p0);
      }
      return x;
    }

    /**
     * Renders the given range in the model as normal unselected text. This is
     * implemented to paint colors based upon the token-to-color translations.
     * To reduce the number of calls to the Graphics object, text is batched up
     * until a color change is detected or the entire requested range has been
     * reached.
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
    protected int drawUnselectedTextXXX( Graphics g, int x, int y, int p0,
        int p1) throws BadLocationException
    {
      HighlightedDocument doc = (HighlightedDocument) getDocument();
      Categoriser cato = doc.getCategoriser();
      Segment text = getLineBuffer();

      System.out.println();
      Token token = null;
      Segment catoInput = new Segment();

      Color lastColor = g.getColor();
      Font lastFont = g.getFont();
      int flushIndex = p0;
      //      System.out.println("paintloop ---------------------------------");
      for (; p0 < p1;) {
        // get token
        token = adjustScanner( doc, p0, p1, cato, catoInput, token);
        //TODO hier wird mit brute force COlor und FOnt gesetzt
        int pt1 = Math.min( token.start + token.length, p1);
        pt1 = (pt1 <= p0) ? p1 : pt1;
        // determine color and font
        Color fg = getForeground( categoryPainting);
        Font font = getFont( categoryPainting);
        if (true || flushIndex != p0) {
          g.setColor( fg);
          g.setFont( font);
          if (true) {
            // print current token
            Segment txt = new Segment();
            System.out.print( "tok=" + token);
            try {
              doc.getText( token.start, token.length, txt);
              System.out.println( ", '" + txt + "'");
            }
            catch (BadLocationException ex) {
              // ex.printStackTrace();
              System.err.println( ex);
            }
          }
          doc.getText( p0, pt1 - p0, text);
          if (true) {
            System.out.print( "painting '" + text + "'");
            System.out.println( " color=" + fg + ", font=" + font);
          }
          x = Utilities.drawTabbedText( text, x, y, g, this, p0);
          flushIndex = p0;
        }
        p0 = pt1;
      }
      // notify lexer
      cato.closeInput();
      g.setColor( lastColor);
      g.setFont( lastFont);
      categoryPainting = token.categoryId;

      return x;
    }

    /**
     * Update the scanner (if necessary) to point to the appropriate token for
     * the given start position needed for rendering.
     * 
     * @param doc
     * @param p0
     *          the beginning position in the model >= 0
     * @param lexer
     * @param lexerInput
     * @param token
     * @return
     */
    private Token adjustScanner( HighlightedDocument doc, int p0, int p1,
        Categoriser lexer, Segment lexerInput, Token token)
    {
      try {
        int p = p0;
        if (!lexerValid) {
          //System.out.println( "# lexer invalid");
          // adjust categorizer's starting point (to start of line)
          p = lexer.getAdjustedStart( doc, p0);
          doc.getText( p, p1 - p, lexerInput);
          /*
           * Bug in 1.41? Wenn das erste Zeichen gelöscht wird, kommt im
           * doc.getText() ein falscher Segmentoffset zu Stande.
           */
          if (p == 0 && lexerInput.offset > doc.getLength()) {
            if (token == null)
              token = new Token();
            token.start = doc.getLength();
            return token;
          }

          //          System.err.println("scanning \n'"+lexerInput+"'");
          lexer.setInput( lexerInput);
          lexerValid = true;
          //System.out.println( "# validated lexer");
        }
        do {
          token = lexer.nextToken( doc, token);
          if (false) {
            // print current token
            Segment txt = new Segment();
            doc.getText( token.start, token.length, txt);
            System.out.println( "tok=" + token + ", '" + txt + "'");
          }
          p = token.start + token.length;
        } while (p <= p0 && token.length > 0);
      }
      catch (Throwable e) {
        // can't adjust scanner... calling logic
        // will simply render the remaining text.
        e.printStackTrace();
      }
      assert token.start + token.length > p0 : "end of token not at required position > "
          + p0;

      return token;
    }

    /**
     * @see javax.swing.text.View#insertUpdate(javax.swing.event.DocumentEvent,
     *      java.awt.Shape, javax.swing.text.ViewFactory)
     */
    public void XXXinsertUpdate( DocumentEvent e, Shape a, ViewFactory f)
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
  }

}