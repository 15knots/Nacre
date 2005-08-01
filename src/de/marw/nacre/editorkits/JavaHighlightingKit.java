/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight;

import java.awt.Color;
import java.util.Map;

import de.marw.javax.swing.text.highlight.categoriser.Categoriser;
import de.marw.javax.swing.text.highlight.categoriser.JavaCategoriser;


/**
 * This kit supports a fairly minimal handling of editing Java text content. It
 * supports syntax highlighting and produces the lexical structure of the
 * document as best it can.
 * 
 * @author Martin Weber
 */
public class JavaHighlightingKit extends HighlightingKit
{

  /**
   * The styles representing the actual categories.
   */
  private static CategoryStyles categoryStyles;

  public JavaHighlightingKit() {
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
    return new JavaCategoriser();
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

  /** 
   * @see de.marw.javax.swing.text.highlight.HighlightingKit#getCategoryDescriptions()
   */
  public Map getCategoryDescriptions()
  {
    // TODO Auto-generated method stub
    return null;
  }

}
