/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight;

import java.awt.Color;

import de.marw.javax.swing.text.highlight.categoriser.C_Categoriser;
import de.marw.javax.swing.text.highlight.categoriser.Categoriser;


/**
 * This kit supports a handling of editing text content wriiten in the C
 * programming language. It supports syntax highlighting and produces the
 * lexical structure of the document as best it can.
 * 
 * @author Martin Weber
 * @version $Revision$
 */
public class CHighlightingKit extends HighlightingKit
{

  /**
   * The styles representing the actual categories.
   */
  private static CategoryStyles categoryStyles;
  
  public CHighlightingKit() {
    super();
  }

  /**
   * Gets the MIME type of the data that this kit represents support for. This
   * kit supports the type <code>text/x-c-src</code>.
   */
  public String getContentType()
  {
    return "text/x-c-src";
  }

  /**
   * Creates a Categoriser used for highlighting text of this document or
   * <code>null</code>.
   */
  protected Categoriser createCategoriser()
  {
    return new C_Categoriser();
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

}
