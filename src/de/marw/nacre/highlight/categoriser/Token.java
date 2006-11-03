/* $Id$ */

// Copyright � 2004 Martin Weber

package de.marw.nacre.highlight.categoriser;

import javax.swing.text.Document;
import javax.swing.text.Segment;


/**
 * Represents a portion of categorised text from a {@link Segment}. These are
 * the tokens returned by Categoriser objects from a
 * {@link Categoriser#nextToken(Document, Token)} method implementation. Only
 * for performance reasons, access to fields is public and not handled via
 * method invocation.
 * 
 * @see Categoriser#nextToken(Document, Token)
 * @author Martin Weber
 */
public abstract class Token
{
  /**
   * the start position of the token, relative to the
   * {@link Segment#getBeginIndex() begin index} of the segment.
   */
  public int start;

  /**
   * the length of the categorised toxt, which must be greater or equal than
   * zero. A zero value indicates the end of the current text (the
   * <code>Segment</code>) is reached; no more tokens will be requested then.
   */
  public int length;

  /**
   * the token's Category. This is <code>null</code> to indicate an
   * undefined/unrecognised text category. The rendering mechanism will treat a
   * <code>null</code> value as normal text without any highlighting.
   */
  public Category category;

  /**
   * <code>true</code> if this Token might span multiple lines (e.g.
   * C-comments). The rendering mechanism uses this to determine safe positions
   * to rescan a document partiallly.
   */
  public boolean multiline;

  /**
   * Resets this Token to its newly constructed state
   */
  public final void reset()
  {
    start = length = 0;
    category = null;
    multiline = false;
  }
}