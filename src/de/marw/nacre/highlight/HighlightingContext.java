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

import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.Segment;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;
import javax.swing.text.Utilities;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;
import javax.swing.text.WrappedPlainView;

import swing.text.highlight.categoriser.Categoriser;
import swing.text.highlight.categoriser.CategoryConstants;
import swing.text.highlight.categoriser.Token;


/**
 * A collection of styles used to render java text. This class also acts as a
 * factory for the views used to represent the java documents. Since the
 * rendering styles are based upon view preferences, the views need a way to
 * gain access to the style settings which is facilitated by implementing the
 * factory in the style storage. Both functionalities can be widely shared
 * across java document views.
 * 
 * @author Timothy Prinzing (1.2 05/27/99)
 * @author Martin Weber
 */
public class HighlightingContext extends StyleContext implements ViewFactory
{

  /**
   * Constructs a set of styles to represent java lexical tokens. By default
   * there are no colors or fonts specified.
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
  public Color getForeground( int code)
  {
    if (tokenColors == null) {
      tokenColors = new Color[CategoryConstants.MaximumId + 1];
    }
    //code--; // no mapping for Category.NORMAL
    if ((code >= 0) && (code < tokenColors.length)) {
      Color c = tokenColors[code];
      if (c == null) {
        Style s = categoryStyles[code];
        c = StyleConstants.getForeground( s);
      }
      return c;
    }
    return Color.black;
  }

  /**
   * Fetch the font to use for a text run with the given category id.
   */
  public Font getFont( int code)
  {
    if (tokenFonts == null) {
      tokenFonts = new Font[CategoryConstants.MaximumId + 1];
    }
    //code--; // no mapping for Category.NORMAL
    if (code >= 0 && code < tokenFonts.length) {
      Font f = tokenFonts[code];
      if (f == null) {
        Style s = categoryStyles[code];
        f = getFont( s);
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
  public Style getStyleForCategory( int code)
  {
    if (code < categoryStyles.length) { return categoryStyles[code]; }
    return null;
  }

  // --- ViewFactory methods -------------------------------------

  public View create( Element elem)
  {
    return new HiliteView( elem);
  }

  // --- variables -----------------------------------------------

  /**
   * The styles representing the actual token types.
   */
  private Style[]           categoryStyles;

  /**
   * Cache of foreground colors to represent the various tokens.
   */
  private transient Color[] tokenColors;

  /**
   * Cache of fonts to represent the various tokens.
   */
  private transient Font[]  tokenFonts;

  /**
   * View that uses the lexical information to determine the style
   * characteristics of the text that it renders. This simply colorizes the
   * various tokens and assumes a constant font family and size.
   */
  private class HiliteView extends WrappedPlainView
  {

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
      lexerValid = false;
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
    protected int drawUnselectedText( Graphics g, int x, int y, int p0, int p1)
        throws BadLocationException
    {
      HighlightedDocument doc = (HighlightedDocument) getDocument();
      Categoriser cato = doc.getCategoriser();
      Segment text = getLineBuffer();

      Token token = null;
      Segment catoInput = new Segment();

      Color lastColor = null;
      Font lastFont = null;
      int mark = p0;
      for (; p0 < p1;) {
        // get token
        token = adjustScanner( doc, p0, cato, catoInput, token);

        int p = Math.min( token.start + token.length, p1);
        p = (p <= p0) ? p1 : p;

        // determine color and font
        Color fg = getForeground( token.categoryId);
        Font font = getFont( token.categoryId);
        if ((fg != lastColor && lastColor != null)
            || (font != lastFont && font != null)) {
          // highlighting change, flush what we have
          g.setColor( lastColor);
          g.setFont( lastFont);
          doc.getText( mark, p0 - mark, text);
          //          System.out.println("-> cat="+token.categoryId+", '"+text+"'");
          x = Utilities.drawTabbedText( text, x, y, g, this, mark);
          mark = p0;
        }
        lastColor = fg;
        lastFont = font;
        p0 = p;
      }
      // notify lexer
      cato.closeInput();
      // flush remaining
      g.setColor( lastColor);
      g.setFont( lastFont);
      doc.getText( mark, p1 - mark, text);
      //      System.out.println("-> flush, cat="+token.categoryId+", '"+text+"'");
      x = Utilities.drawTabbedText( text, x, y, g, this, mark);
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
    private Token adjustScanner( HighlightedDocument doc, int p0,
        Categoriser lexer, Segment lexerInput, Token token)
    {
      try {
        int p = p0;
        if (!lexerValid) {
          // adjust categorizer's starting point (to start of line)
          p = lexer.getAdjustedStart( doc, p0);
          doc.getText( p, doc.getLength() - p, lexerInput);
          //System.err.println("scanning \n'"+lexerInput+"'");
          lexer.setInput( lexerInput);
          lexerValid = true;
        }
        while (p <= p0) {
          token = lexer.nextToken( doc, token);
          if (true) {
            // print current token
            Segment txt = new Segment();
            doc.getText( token.start, token.length, txt);
            System.out.println( "cat=" + token.categoryId + ", '" + txt + "'");
          }
          p += token.start + token.length;
        }
      }
      catch (Throwable e) {
        // can't adjust scanner... calling logic
        // will simply render the remaining text.
        e.printStackTrace();
      }
      return token;
    }

    /**
     * used for text runs that span multiple line (eg Javadoc comments). Set to
     * <code>false</code> if the categorizer needs to adjust it's starting
     * point.
     */
    private boolean lexerValid;
  }

}