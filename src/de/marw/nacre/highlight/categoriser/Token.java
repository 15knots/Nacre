// $Header:
// /home/weber/cvsRepos/highlighting/swing/text/highlight/categoriser/Token.java,v
// 1.1 2004/09/22 19:05:12 weber Exp $

// Copyright © 2004 Razorcat Development GmbH

package swing.text.highlight.categoriser;

/**
 * The tokens returned by Categoriser objects.
 * 
 * @see Categoriser#nextToken(HighlightedDocument, Token)
 * @author Martin Weber
 */
public class Token implements Cloneable
{
  /** the start position of the token */
  public int start      = 0;

  /** the length of the token */
  public int length     = 0;

  /**
   * Numeric value of the token's Category.
   */
  public int categoryId = CategoryConstants.NORMAL;

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
    return "start=" + start + ", length=" + length + ", cat=" + categoryId;
  }
}