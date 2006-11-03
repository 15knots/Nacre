// $Id$
/*
 * Copyright 2005-2006 by Martin Weber
 */

package de.marw.nacre.highlight;

import java.util.EventObject;

import de.marw.nacre.highlight.categoriser.Category;


/**
 * The <code>CategoryStylesEvent</code> class encapsulates information that a
 * {@link de.marw.nacre.highlight.CategoryStyles} object sends its
 * listeners whenever the visual representation of a
 * {@link de.marw.nacre.highlight.categoriser.Category} is changed.
 * 
 * @author weber
 */
public class CategoryStylesEvent extends EventObject
{

  /**
   * Comment for <code>serialVersionUID</code>
   */
  private static final long serialVersionUID = 3256445806675834423L;

  /**
   * the Category where the style change occured.
   */
  private final Category category;

  /**
   * @param source
   *        The object on which the Event initially occurred.
   * @param category
   *        The Category where the style change occured.
   * @throws IllegalArgumentException
   *         if source or category is <code>null</code>.
   */
  public CategoryStylesEvent( Object source, Category category) {
    super( source);
    if (category == null) {
      throw new IllegalArgumentException( "category is null");
    }
    this.category = category;
  }

  /**
   * Returns the Category where the style change occured.
   * 
   * @return The category.
   */
  public Category getCategory()
  {
    return this.category;
  }
}
