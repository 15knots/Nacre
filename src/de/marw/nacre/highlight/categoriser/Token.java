/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight.categoriser;

import de.marw.javax.swing.text.highlight.Category;


/**
 * The tokens returned by Categoriser objects.
 * 
 * @see Categoriser#nextToken(HighlightedDocument, Token)
 * @author Martin Weber
 */
public class Token implements Cloneable
{
  /** the start position of the token */
  public int start = 0;

  /** the length of the token */
  public int length = 0;

  /**
   * the token's Category.
   */
  public Category category = Category.NORMAL;

  /**
   * <code>true</code> if this Token might span multiple lines (e.g.
   * C-comments). This is used in conjunction of <code>category</code> to
   * determine safe positions to rescan a document.
   */
  public boolean multiline = false;

  /**
   * Returns a shallow copy of this <code>Token</code> instance.
   * 
   * @see java.lang.Object#clone()
   */
  public Object clone()
  {
    try {
      return super.clone();
    }
    catch (CloneNotSupportedException ex) {
      // this shouldn't happen, since we are Cloneable
      throw new InternalError();
    }
  }

  public String toString()
  {
    return "start=" + start + ", length=" + length + ", cat=" + category
        + (multiline ? ", multiline" : ", single line");
  }
}