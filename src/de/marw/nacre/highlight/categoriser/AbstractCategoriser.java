// $Header:
// /home/weber/cvsRepos/highlighting/swing/text/highlight/categoriser/AbstractCategoriser.java,v
// 1.1 2004/09/22 19:05:12 weber Exp $

// Copyright © 2004 Martin Weber

package swing.text.highlight.categoriser;

import javax.swing.text.BadLocationException;
import javax.swing.text.Segment;

import swing.text.highlight.HighlightedDocument;


/**
 * 
 */
public abstract class AbstractCategoriser implements Categoriser
{

  protected static final boolean debug = false;

  protected Segment input;

  /**
   */
  public AbstractCategoriser()
  {
    super();
  }

  /**
   * Überschrieben, um
   * 
   * @throws BadLocationException
   */
  public void openInput( HighlightedDocument doc, int lineIndex)
      throws BadLocationException
  {
    if (debug) {
      System.out
          .println( "# in AbstractCategoriser.openInput() ---------------------------");
    }
    setInput( new Segment());
  }

  public void closeInput()
  {
    if (debug) {
      System.out
          .println( "# in AbstractCategoriser.closeInput() ---------------------------");
    }
    input = null;
  }

  protected void setInput( Segment input)
  {
    this.input = input;
  }

  /**
   * @return The input.
   */
  protected final Segment getInput()
  {
    return this.input;
  }

  /**
   * Checks if a subregion of a <code>Segment</code> is equal to a string.
   * 
   * @param ignoreCase
   *          True if case should be ignored, false otherwise
   * @param text
   *          The segment
   * @param offset
   *          The offset into the segment
   * @param match
   *          The string to match
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  public static int regionMatches( boolean ignoreCase, Segment text,
      int offset, String match)
  {
    int endpos = offset + match.length();
    char[] textArray = text.array;

    if (endpos > (text.offset + text.count)) {
      return 0; // no match
    }
    int j = 0;
    for (int i = offset; i < endpos; i++ , j++ ) {
      char c1 = textArray[i];
      char c2 = match.charAt( j);

      if (ignoreCase) {
        c1 = Character.toUpperCase( c1);
        c2 = Character.toUpperCase( c2);
      }

      if (c1 != c2) {
        return 0; // no match
      }
    }

    return j;
  }

}