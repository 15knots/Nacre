/* $Id$ */

// Copyright � 2004 Martin Weber

package de.marw.javax.swing.text.highlight.categoriser;

import javax.swing.text.Document;


/**
 * The tokens returned by Categoriser objects.
 * 
 * @see Categoriser#nextToken(Document, Token)
 * @author Martin Weber
 */
public class Token
{
  /** the start position of the token */
  public int start = 0;

  /**
   * the length of the token, which must be greater or equal tha zero. A zero
   * value indicates the end of the current portion of text (the
   * <code>Segment</code>) to scan.
   */
  public int length = 0;

  /**
   * the token's Category. This is <code>null</code> to indicate an undefined
   * text category. Categorisers will use this to mark their unititialized
   * state. The rendering mechanism will treat a <code>null</code> values as
   * normal text without any highlighting.
   */
  public Category category = null;

  /**
   * <code>true</code> if this Token might span multiple lines (e.g.
   * C-comments). This is used in conjunction of <code>category</code> to
   * determine safe positions to rescan a document.
   */
  public boolean multiline = false;

  public String toString()
  {
    return "start=" + start + ", length=" + length + ", cat=" + category
        + (multiline ? ", multiline" : ", single line");
  }
}