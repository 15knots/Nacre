/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight.categoriser;

import java.text.CharacterIterator;

import javax.swing.text.Segment;


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
   */
  public void openInput( Segment lexerInput)
  {
    if (debug) {
      System.out.println( "# AbstractCategoriser.openInput() char[0]='"
          + lexerInput.array[lexerInput.offset] + "', offset="
          + lexerInput.offset + ", count=" + lexerInput.count);
    }
    this.input = lexerInput;
    lexerInput.first(); // initialize CharIterator
  }

  public void closeInput()
  {
    if (debug) {
      System.out
          .println( "# AbstractCategoriser.closeInput() ---------------------------");
    }
    input = null;
  }

  /**
   * @return The input segment.
   */
  protected final Segment getInput()
  {
    return this.input;
  }

  ///////////////////////////////////////////////////////////
  // categoriser helper methods
  ///////////////////////////////////////////////////////////

  /**
   * Fetches the lookahead character at the specified position.
   * 
   * @return the lookahead character or <code>CharacterIterator.DONE</code> at
   *         end of input is reached.
   */
  protected final char LA( int lookAhead)
  {
    int offset = input.getIndex();
    if (offset + lookAhead >= input.offset + input.count) {
      return CharacterIterator.DONE;
    }
    return input.array[lookAhead + offset];
  }

  /**
   * Consumes the specified number of character from the input.
   * 
   * @param len
   *        the number of character to consume.
   */
  protected final void consumeChars( int len)
  {
    input.setIndex( len + input.getIndex());
  }

  ///////////////////////////////////////////////////////////
  // other helper methods
  ///////////////////////////////////////////////////////////

  /**
   * Looks if a subregion in the <code>input</code> starting at the current
   * scanner input position is equal to one of the Strings in
   * <code>wordlist</code>.
   * 
   * @see AbstractCategoriser#input
   * @param lenght
   *        the length of the region that must match.
   * @param wordlist
   *        the strings that may match.
   * @return <code>true</code> if a match was found, otherwise
   *         <code>false</code>.
   */
  protected final boolean matchInWordlist( int length, final String[] wordlist)
  {
    for (int i = 0; i < wordlist.length; i++ ) {
      if (wordlist[i].length() == length
          && AbstractCategoriser.regionMatches( false, input, input.getIndex(),
              wordlist[i]) > 0)
        return true;
    }
    return false; // no match

  }

  /**
   * Checks if a subregion of a <code>Segment</code> is equal to a string.
   * 
   * @param ignoreCase
   *        True if case should be ignored, false otherwise.
   * @param text
   *        The source of the text.
   * @param offset
   *        The offset into the segment to start the matching.
   * @param match
   *        The string to match.
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