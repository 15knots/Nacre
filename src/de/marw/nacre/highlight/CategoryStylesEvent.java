// $Header$
/*
 * Copyright 2005 by Martin Weber
 */

package de.marw.javax.swing.text.highlight;

import java.util.EventObject;


/**
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
  private Category category;

  /**
   * @param source
   * @param category
   */
  public CategoryStylesEvent( Object source, Category category) {
    super( source);
    this.category = category;
  }

  /**
   * Return the Category where the style change occured.
   * 
   * @return The category.
   */
  public Category getCategory()
  {
    return this.category;
  }
}
