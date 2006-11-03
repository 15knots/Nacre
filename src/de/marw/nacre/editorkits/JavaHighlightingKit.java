/* $Id$ */

// Copyright � 2004 Martin Weber

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
 * This kit supports a fairly minimal handling of editing Java text content. It
 * supports syntax highlighting and produces the lexical structure of the
 * document as best it can.
 * 
 * @author Martin Weber
 */
public class JavaHighlightingKit extends HighlightingKit
{

  /**
   * 
   */
  private static final long serialVersionUID = 5360252654985043336L;

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
   * @see de.marw.nacre.highlight.HighlightingKit#getCategoryStyles()
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
    CategoryStyles.applyDefaultStyles( styleDefaults);
    styleDefaults.setColor( Category.IDENTIFIER_1, Color.cyan.darker());
    styleDefaults.setColor( Category.IDENTIFIER_2, Color.cyan.darker());
    return styleDefaults;
  }

  /**
   * @see de.marw.nacre.highlight.HighlightingKit#getCategoryDescriptions(Locale)
   */
  public Map<Category, String> getCategoryDescriptions( Locale locale)
  {
    String bundle_name = JavaHighlightingKit.class.getName();
    ResourceBundle bundle = ResourceBundle.getBundle( bundle_name,
        locale == null ? Locale.getDefault() : locale);

    Map<Category, String> catDescriptions = new EnumMap<Category, String>(
        Category.class);

    catDescriptions.put( Category.COMMENT_1, getString( bundle,
        Category.COMMENT_1.name()));
    catDescriptions.put( Category.COMMENT_2, getString( bundle,
        Category.COMMENT_2.name()));
    catDescriptions.put( Category.DOC, getString( bundle, Category.DOC.name()));
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
