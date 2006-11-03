// $Id$
/*
 * Copyright 2005 by Martin Weber
 */

package de.marw.nacre.highlight;

/**
 * Defines the requirements of an object that is notified when the visual
 * representation of a
 * {@link de.marw.nacre.highlight.categoriser.Category} (the style
 * for highlighting) is changed.
 * 
 * @author weber
 */
public interface CategoryStylesListener
{
  /**
   * Gets notified when the visual representation (the style for highlighting)
   * of a {@link de.marw.nacre.highlight.categoriser.Category} is
   * changed.
   */
  public void styleChanged( CategoryStylesEvent evt);
}
