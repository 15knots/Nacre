/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight;

import java.awt.Color;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.marw.javax.swing.text.highlight.categoriser.C_Categoriser;
import de.marw.javax.swing.text.highlight.categoriser.Categoriser;


/**
 * This kit supports a handling of editing text content written in the C
 * programming language. It supports syntax highlighting and produces the
 * lexical structure of the document as best it can.
 * 
 * @author Martin Weber
 * @see de.marw.javax.swing.text.highlight.HighlightingKit for sample usage
 *      code.
 * @version $Revision$
 */
public class CHighlightingKit extends HighlightingKit
{

  /**
   * The styles representing the actual categories.
   * 
   * @see HighlightingKit#getCategoryStyles()
   */
  private static CategoryStyles categoryStyles;

  /**
   * localized descriptions of categories, lazily instantiated.
   */
  private static Map<Category, String> catDescriptions;

  public CHighlightingKit() {
    super();
  }

  /**
   * Gets the MIME type of the data that this kit represents support for. This
   * kit supports the type <strong>text/x-c-src </strong>.
   */
  public String getContentType()
  {
    return "text/x-c-src"; //$NON-NLS-1$
  }

  /**
   * Creates a <code>C_Categoriser</code> for highlighting text of the C
   * programming language.
   * 
   * @return An object of type {@link C_Categoriser C_Categoriser}to map
   *         lexical elements to categories.
   * @see C_Categoriser
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
   * Returns a Map that specifies each category as a <strong>localized </strong>
   * string that can be used as a label. If the returned Map yields a
   * <code>null</code> -value for a Category, this editor kit does not
   * highlight any text as the queried category.
   */
  public Map<Category, String> getCategoryDescriptions()
  {
    if (catDescriptions == null) {
      Map<Category, String> map = new EnumMap<Category, String>( Category.class);
      map.put( Category.COMMENT_1, getString( "CHighlightingKit.Comment_1")); //$NON-NLS-1$
      map.put( Category.COMMENT_2, getString( "CHighlightingKit.Comment_2")); //$NON-NLS-1$
      map.put( Category.STRINGVAL, getString( "CHighlightingKit.StringVal")); //$NON-NLS-1$
      map.put( Category.NUMERICVAL, getString( "CHighlightingKit.NumericVal")); //$NON-NLS-1$
      map.put( Category.PREDEFVAL, getString( "CHighlightingKit.PredefVal")); //$NON-NLS-1$
      map.put( Category.KEYWORD_STATEMENT,
          getString( "CHighlightingKit.KeywordStatement")); //$NON-NLS-1$
      map.put( Category.KEYWORD_OPERATOR,
          getString( "CHighlightingKit.KeywordOperator")); //$NON-NLS-1$
      map.put( Category.KEYWORD_TYPE,
          getString( "CHighlightingKit.KeywordType")); //$NON-NLS-1$
      map.put( Category.KEYWORD, getString( "CHighlightingKit.Keyword")); //$NON-NLS-1$
      map.put( Category.OPERATOR, getString( "CHighlightingKit.Operator")); //$NON-NLS-1$
      catDescriptions = Collections.unmodifiableMap( map);
    }
    return catDescriptions;
  }

  /**
   * Creates a built-in set of color and font style informations used to render
   * highlighted text written in the C programming language.
   */
  private CategoryStyles createDefaultStyles()
  {
    CategoryStyles styleDefaults = new CategoryStyles();
    CategoryStyles.setDefaults( styleDefaults);
    styleDefaults.setColor( Category.IDENTIFIER_1, Color.cyan.darker());
    styleDefaults.setColor( Category.IDENTIFIER_2, Color.cyan.darker());
    return styleDefaults;
  }

  // localization issues...
  private static final String BUNDLE_NAME = CHighlightingKit.class.getName();

  private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle
      .getBundle( BUNDLE_NAME);

  private static String getString( String key)
  {
    try {
      return RESOURCE_BUNDLE.getString( key);
    }
    catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
}
