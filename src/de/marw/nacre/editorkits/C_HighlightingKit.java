/* $Id$ */

// Copyright © 2004-2006 Martin Weber

package de.marw.nacre.editorkits;

import java.awt.Color;
import java.util.EnumMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import java.util.ResourceBundle;

import de.marw.nacre.highlight.CategoryStyles;
import de.marw.nacre.highlight.HighlightingKit;
import de.marw.nacre.highlight.categoriser.Categoriser;
import de.marw.nacre.highlight.categoriser.Category;


/**
 * This kit supports a handling of editing text content written in the C
 * programming language. It supports syntax highlighting and produces the
 * lexical structure of the document as best it can.
 * 
 * @author Martin Weber
 * @see de.marw.nacre.highlight.HighlightingKit for sample usage code.
 * @version $Revision$
 */
public class C_HighlightingKit extends HighlightingKit
{

  /**   */
  private static final long serialVersionUID= -913448696261515702L;

  public C_HighlightingKit()
  {
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
   * Creates a built-in set of color and font style informations used to render
   * highlighted text written in the C programming language.
   */
  protected CategoryStyles createCategoryStyles()
  {
    CategoryStyles styleDefaults= new CategoryStyles();
    styleDefaults.applyDefaultStyles();
    // categoryStyles.undefine( Category.DOC); // not supported by categoriser
    styleDefaults.setColor( Category.IDENTIFIER_1, Color.cyan.darker());
    styleDefaults.setColor( Category.IDENTIFIER_2, Color.cyan.darker());
    return styleDefaults;
  }

  /**
   * Returns a Map that specifies each category as a <strong>localized </strong>
   * string that can be used as a label. If the returned Map yields a
   * <code>null</code> -value for a Category, this editor kit does not
   * highlight any text as the queried category.
   */
  public Map<Category, String> getCategoryDescriptions( Locale locale)
  {
    String bundle_name= C_HighlightingKit.class.getName();
    ResourceBundle bundle=
      ResourceBundle.getBundle( bundle_name, locale == null
        ? Locale.getDefault() : locale);

    Map<Category, String> catDescriptions=
      new EnumMap<Category, String>( Category.class);

    catDescriptions.put( Category.COMMENT_1, getString( bundle,
      Category.COMMENT_1.name()));
    catDescriptions.put( Category.COMMENT_2, getString( bundle,
      Category.COMMENT_2.name()));
    // catDescriptions.put( Category.DOC, getString( bundle,
    // Category.DOC.name()));
    catDescriptions.put( Category.STRINGVAL, getString( bundle,
      Category.STRINGVAL.name()));
    catDescriptions.put( Category.NUMERICVAL, getString( bundle,
      Category.NUMERICVAL.name()));
    catDescriptions.put( Category.PREDEFVAL, getString( bundle,
      Category.PREDEFVAL.name()));
    catDescriptions.put( Category.KEYWORD_STATEMENT, getString( bundle,
      Category.KEYWORD_STATEMENT.name()));
    catDescriptions.put( Category.KEYWORD_OPERATOR, getString( bundle,
      Category.KEYWORD_OPERATOR.name()));
    catDescriptions.put( Category.KEYWORD_TYPE, getString( bundle,
      Category.KEYWORD_TYPE.name()));
    catDescriptions.put( Category.KEYWORD, getString( bundle, Category.KEYWORD
      .name()));
    catDescriptions.put( Category.OPERATOR, getString( bundle,
      Category.OPERATOR.name()));

    return catDescriptions;
  }

  // localization issues...
  private static String getString( final ResourceBundle bundle, String key)
  {
    try {
      return bundle.getString( key);
    }
    catch (MissingResourceException e) {
      return '!' + key + '!';
    }
  }
}
