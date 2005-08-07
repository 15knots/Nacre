// $Id$
/*
 * Copyright 2005 by Martin Weber
 */

package de.marw.javax.swing.text.highlight;

import java.awt.Color;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;


/**
 * A set of color and font style informations used to render highlighted text of
 * specific categories.
 * 
 * @author Martin Weber
 */
public class CategoryStyles implements Serializable
{

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3978148737866741304L;

  /**
   * the categories and the styles managed by this object.
   */
  private EnumMap<Category, StyleEntry> categoryStyles = new EnumMap<Category, StyleEntry>(
      Category.class);

  /**
   * Listeners, lazily created
   */
  private transient List<CategoryStylesListener> listeners;

  /**
   * Constructs an empty set of color and font style informations.
   */
  public CategoryStyles() {
    super();
  }

  /**
   * Checks whether the category has a color or font style value specified in
   * the set.
   * 
   * @param category
   *        the category to check
   * @return <code>true</code> if the category has a value specified
   * @see #undefine(Category)
   */
  public boolean isDefined( Category category)
  {
    return categoryStyles.containsKey( category);
  }

  /**
   * Fetches the font style to use for the given category.
   * 
   * @param category
   *        the category to get the font style for.
   * @return The style constant for the <code>Font</code> to render the
   *         specified category. The style is an integer bitmask that may be
   *         Font.PLAIN, or a bitwise union of Font.BOLD and/or Font.ITALIC (for
   *         example, Font.ITALIC or Font.BOLD|Font.ITALIC).
   */
  public int getStyle( Category category)
  {
    StyleEntry style = categoryStyles.get( category);
    if (style != null) {
      return style.style;
    }
    return Font.PLAIN;
  }

  /**
   * Fetches the color to use for the given category.
   * 
   * @param category
   *        the category to get the color for.
   * @return The <code>Color</code> to render the specified category or
   *         <code>null</code> if the default text component's color is to be
   *         used.
   */
  public Color getColor( Category category)
  {
    StyleEntry style = categoryStyles.get( category);
    if (style != null) {
      return style.color;
    }
    return null;
  }

  /**
   * Indicates whether or not the <code>Font</code> object's style to use for
   * the given category is BOLD.
   * 
   * @param category
   *        the category to query.
   * @return <code>true</code> if the <code>Font</code> object's style is
   *         BOLD; <code>false</code> otherwise.
   */
  public boolean isBold( Category category)
  {
    StyleEntry style = categoryStyles.get( category);
    if (style != null) {
      return style.isBold();
    }
    return false;
  }

  /**
   * Indicates whether or not the <code>Font</code> object's style to use for
   * the given category is ITALIC.
   * 
   * @param category
   *        the category to query.
   * @return <code>true</code> if the <code>Font</code> object's style is
   *         ITALIC; <code>false</code> otherwise.
   */
  public boolean isItalic( Category category)
  {
    StyleEntry style = categoryStyles.get( category);
    if (style != null) {
      return style.isItalic();
    }
    return false;
  }

  /**
   * Sets the specified category to its default rendering style. This is
   * equivalent to calling <code>
   * <pre>
   * setColor( category, null);
   * setBold( category, false);
   * setItalic( category, false);
   * </pre></code>.
   * 
   * @param category
   *        the category to undefine.
   */
  public void undefine( Category category)
  {
    boolean changed = isDefined( category);
    if (changed) {
      categoryStyles.remove( category);
      fireCategoryStylesChanged( category);
    }
  }

  /**
   * Sets the color to use for the given category.
   * 
   * @param category
   *        the category to set the new color for.
   * @param newColor
   *        The <code>Color</code> to render the specified category or
   *        <code>null</code> if the default text component's color is to be
   *        used.
   * @throws IllegalArgumentException
   *         if the category is <code>null</code>.
   */
  public void setColor( Category category, Color newColor)
  {
    StyleEntry style = getOrCreateStyle( category);
    Color oldColor = style.getColor();
    boolean changed = !(newColor == null ? oldColor == null : newColor
        .equals( oldColor));
    style.setColor( newColor);
    if (style.isDefault()) {
      categoryStyles.remove( category);
    }
    if (changed) {
      fireCategoryStylesChanged( category);
    }
  }

  /**
   * Sets whether or not the <code>Font</code> object's style to use for the
   * given category is BOLD.
   * 
   * @param category
   *        the category to set a bold font style for.
   * @param bold
   *        <code>true</code> if the <code>Font</code> object's style is
   *        BOLD; <code>false</code> otherwise.
   * @throws IllegalArgumentException
   *         if the category is <code>null</code>.
   */
  public void setBold( Category category, boolean bold)
  {
    StyleEntry style = getOrCreateStyle( category);
    boolean changed = (style.isBold() != bold);
    style.setBold( bold);
    if (style.isDefault()) {
      categoryStyles.remove( category);
    }
    if (changed) {
      fireCategoryStylesChanged( category);
    }
  }

  /**
   * Sets whether or not the <code>Font</code> object's style to use for the
   * given category is ITALIC.
   * 
   * @param category
   *        the category to set an italic font style for.
   * @param italic
   *        <code>true</code> if the <code>Font</code> object's style is
   *        ITALIC; <code>false</code> otherwise.
   * @throws IllegalArgumentException
   *         if the category is <code>null</code>.
   */
  public void setItalic( Category category, boolean italic)
  {
    StyleEntry style = getOrCreateStyle( category);
    boolean changed = (style.isItalic() != italic);
    style.setItalic( italic);
    if (style.isDefault()) {
      categoryStyles.remove( category);
    }
    if (changed) {
      fireCategoryStylesChanged( category);
    }
  }

  /**
   * Removes all styles in this <code>CategoryStyles</code> object and then
   * adds the styles contained in <code>newStyles</code>.<br>
   * This can be used in conjunction with
   * {@link HighlightingKit#getCategoryStyles() getCategoryStyles()}to apply a
   * (persistent) set of color and font style informations and automatically
   * reflect the changes to any <code>JEditorPane</code> in the application.
   * 
   * @param newStyles
   *        the new styles to add.
   */
  public void replaceWith( CategoryStyles newStyles)
  {
    for (Category cat : Category.values()) {
      StyleEntry oldStyle = this.categoryStyles.get( cat);
      StyleEntry newStyle = newStyles.categoryStyles.get( cat);

      boolean changed = false;
      if (oldStyle != null) {
        if (newStyle == null) {
          categoryStyles.remove( cat);
          changed = true;
        }
        else {
          if ( !oldStyle.equals( newStyle)) {
            categoryStyles.put( cat, newStyle);
            changed = true;
          }
        }
      }
      else { // oldstyle is null
        if (newStyle != null) {
          categoryStyles.put( cat, newStyle);
          changed = true;
        }
      }

      if (changed) {
        fireCategoryStylesChanged( cat);
      }
    } // for
  }

  /**
   * Applies a built-in set of color and font style informations to the
   * specified <code>CategoryStyles</code> object. Convenience funtion.
   * 
   * @return the specified <code>CategoryStyles</code> object
   */
  public static CategoryStyles setDefaults( CategoryStyles target)
  {
    final Color keywordCol_any = new Color( 109, 137, 164);
    final Color keywordCol = new Color( 127, 0, 85);
    final Color keywordCol_type = new Color( 181, 0, 121);
    final Color literalColor = new Color( 42, 0, 255);
    final Color commentColor = new Color( 63, 127, 95);

    target.setColor( Category.COMMENT_1, commentColor);
    target.setColor( Category.COMMENT_2, commentColor);
    target.setColor( Category.STRINGVAL, literalColor);
    target.setItalic( Category.STRINGVAL, true);
    target.setColor( Category.NUMERICVAL, literalColor);
    target.setColor( Category.PREDEFVAL, literalColor);
    target.setBold( Category.PREDEFVAL, true);
    target.setColor( Category.KEYWORD_STATEMENT, keywordCol);
    target.setBold( Category.KEYWORD_STATEMENT, true);
    target.setColor( Category.KEYWORD_OPERATOR, keywordCol);
    target.setBold( Category.KEYWORD_OPERATOR, true);
    target.setColor( Category.KEYWORD_TYPE, keywordCol_type);
    target.setBold( Category.KEYWORD_TYPE, true);
    target.setColor( Category.KEYWORD, keywordCol_any);
    target.setBold( Category.KEYWORD, true);
    target.setColor( Category.DOC, new Color( 6, 40, 143));

    return target;
  }

  public void addCategoryStylesListener( CategoryStylesListener listener)
  {
    if (listeners == null) {
      listeners = new ArrayList<CategoryStylesListener>();
    }
    listeners.add( listener);
  }

  public void removeCategoryStylesListener( CategoryStylesListener listener)
  {
    if (listeners != null) {
      listeners.remove( listener);
    }
  }

  protected void fireCategoryStylesChanged( Category cat)
  {
    if (listeners != null) {
      CategoryStylesEvent evt = new CategoryStylesEvent( this, cat);
      for (CategoryStylesListener listener : listeners) {
        listener.styleChanged( evt);
      }
    }
  }

  /**
   * @param category
   * @return the StyleEntry for the specified category.
   * @throws IllegalArgumentException
   *         if the category is <code>null</code>.
   */
  private StyleEntry getOrCreateStyle( Category category)
  {
    if (category == null) {
      throw new NullPointerException( "category");
    }
    StyleEntry style = categoryStyles.get( category);
    if (style == null) {
      style = new StyleEntry();
      categoryStyles.put( category, style);
    }
    return style;
  }

  // classes -------------------------------------------------------
  private static class StyleEntry implements Serializable
  {
    /**
     * Comment for <code>serialVersionUID</code>
     */
    private static final long serialVersionUID = 3546358431356237617L;

    /**
     * The foreground color to use. Set to <code>null</code> if the default
     * text component's color is to be used.
     */
    private Color color;

    /**
     * The style constant for the <code>Font</code>. The style value is an
     * integer bitmask that may be Font.PLAIN, or a bitwise union of Font.BOLD
     * and/or Font.ITALIC (for example, Font.ITALIC or Font.BOLD|Font.ITALIC).
     */
    private int style;

    /**
     * Constructs a new StyleEntry object with no color and a plain font style.
     */
    public StyleEntry() {
      this.color = null;
      this.style = Font.PLAIN;
    }

    /**
     * Sets the color to use.
     * 
     * @param newColor
     *        The <code>Color</code> to render.
     */
    protected void setColor( Color newColor)
    {
      this.color = newColor;
    }

    /**
     * Sets whether or not the <code>Font</code> object's style to use is
     * BOLD.
     * 
     * @param bold
     *        <code>true</code> if the <code>Font</code> object's style is
     *        BOLD; <code>false</code> otherwise.
     */
    protected void setBold( boolean bold)
    {
      if (bold) {
        style |= Font.BOLD;
      }
      else {
        style &= ~Font.BOLD;
      }
    }

    /**
     * Sets whether or not the <code>Font</code> object's style to use is
     * ITALIC.
     * 
     * @param italic
     *        <code>true</code> if the <code>Font</code> object's style is
     *        ITALIC; <code>false</code> otherwise.
     */
    protected void setItalic( boolean italic)
    {
      if (italic) {
        style |= Font.ITALIC;
      }
      else {
        style &= ~Font.ITALIC;
      }
    }

    /**
     * @return The color.
     */
    protected final Color getColor()
    {
      return this.color;
    }

    /**
     * Returns the style constant for the <code>Font</code>. The style value
     * is an integer bitmask that may be Font.PLAIN, or a bitwise union of
     * Font.BOLD and/or Font.ITALIC (for example, Font.ITALIC or
     * Font.BOLD|Font.ITALIC).
     * 
     * @return The font style.
     */
    protected final int getStyle()
    {
      return this.style;
    }

    /**
     * Indicates whether or not the <code>Font</code> object's style is BOLD.
     * 
     * @return <code>true</code> if the <code>Font</code> object's style is
     *         BOLD; <code>false</code> otherwise.
     */
    protected final boolean isBold()
    {
      return (style & Font.BOLD) != 0;
    }

    /**
     * Indicates whether or not the <code>Font</code> object's style is
     * ITALIC.
     * 
     * @return <code>true</code> if the <code>Font</code> object's style is
     *         ITALIC; <code>false</code> otherwise.
     */
    protected final boolean isItalic()
    {
      return (style & Font.ITALIC) != 0;
    }

    /**
     * @see java.lang.Object#toString()
     */
    public String toString()
    {
      String ctext = null;
      if (color != null) {
        ctext = "r=" + color.getRed() + ",g=" + color.getGreen() + ",b="
            + color.getBlue();
      }

      return getClass().getName() + "[" + ctext + ",s="
          + (isBold() ? "bold" : "") + (isItalic() ? "italic" : "")
          + ( !isItalic() && !isBold() ? "plain" : "") + "]";
    }

    /**
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals( Object obj)
    {
      if (obj == null || obj.getClass() != StyleEntry.class)
        return false;
      StyleEntry ob = (StyleEntry) obj;
      return ob.style == style
          && (ob.color == null ? color == null : ob.color.equals( color));
    }

    /**
     * Determines whether this style entry can be safely replaced by the default
     * style (no color, Font.PLAIN).
     * 
     * @return <code>true</code> if the style can be safely replaced by the
     *         default style.
     */
    private boolean isDefault()
    {
      return ((color == null) && (style == Font.PLAIN));
    }

  }

}