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
    CategoryStyles styleDefaults = new CategoryStyles();
    CategoryStyles.setDefaults( styleDefaults);
    styleDefaults.setColor( Category.IDENTIFIER_1, Color.cyan.darker());
    styleDefaults.setColor( Category.IDENTIFIER_2, Color.cyan.darker());
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
