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
import java.util.Iterator;
import java.util.List;
import java.util.Map;


/**
 * A set of color and font style informations used used to render highlighted
 * text of specific categories.
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
   * @param attrName
   *        the attribute name
   * @return true if the category has a value specified
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

    style.setColor( newColor);
    checkDefaultRemove( style);
    fireCategoryStylesChanged( category);
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
    style.setBold( bold);
    checkDefaultRemove( style);
    fireCategoryStylesChanged( category);
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
    style.setItalic( italic);
    checkDefaultRemove( style);
    fireCategoryStylesChanged( category);
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
   * @return
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

  /**
   * Determines whether the specyfied style can be safely replaced by the
   * default style (no color, Font.PLAIN) and removes the style in the latter
   * case.
   * 
   * @param style
   *        the style to check.
   */
  private void checkDefaultRemove( StyleEntry style)
  {
    if (style.isDefault()) {
      categoryStyles.remove( style);
    }
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