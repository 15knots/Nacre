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

  private static final boolean debug = false;

  /**
   * The input segment currently in use.
   * 
   * @see #getInput()
   */
  protected Segment input;

  /**
   * Constructor for subclasses.
   */
  protected AbstractCategoriser() {
  }

  /*
   * 
   */
  public void openInput( Segment lexerInput)
  {
    if (debug) {
      System.out.println( "# AbstractCategoriser.openInput(), offset="
          + lexerInput.offset + ", count=" + lexerInput.count);
    }
    this.input = lexerInput;
    lexerInput.first(); // initialize CharIterator
  }

  /*
   * 
   */
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

  // /////////////////////////////////////////////////////////
  // matcher methods for use by subclasses
  // /////////////////////////////////////////////////////////

  /**
   * Matches white space.
   * 
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected int matchWhitespace()
  {
    int len = 0;
    while (Character.isWhitespace( LA( len))) {
      len++;
    }
    return len;
  }

  /**
   * Matches a number. <br>
   * 
   * <pre>
   *   
   *    
   *     
   *      
   *       
   *        
   *          Number 
   *             : ( Decimal )? '.' Decimal ( Exponent )? ( FloatSuffix)?
   *             | Decimal ( Exponent )? ( FloatSuffix)? | Decimal ( IntSuffix )?
   *             | '0' ( 'x' | 'X' ) HexDecimal ( IntSuffix )? 
   *         
   *        
   *       
   *      
   *     
   *    
   * </pre>
   * 
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected int matchNumber()
  {

    int len;
    len = matchDecimal( 0); // matched Decimal
    if (len == 1) {
      // hexadecimal number?
      if (LA( 0) == '0' && Character.toUpperCase( LA( 1)) == 'X'
          && isHexDigit( LA( 2))) {
        len += 2;
        len += matchHexDecimal( 3);
        // match trailing LongSuffix and UnsignedSuffix...
        len += matchIntSuffix( len);
        // matched '0' ( 'x' | 'X' ) HexDecimal ( IntSuffix )?
        return len;
      }
    }
    else {
      if (LA( len) == '.') {
        // fractional number?
        if (Character.isDigit( LA( len + 1))) {
          // fractional number: matched Decimal '.' Digit
          len += 2;
          len += matchDecimal( len); // matched Decimal '.' Decimal
          len += matchExponent( len);
          len += matchFloatSuffix( len);
          // matched ( Decimal )? '.' Decimal ( Exponent )? ( FloatSuffix)?
          return len;
        }
      }
    }

    if (len > 0) {
      // if we ran here, we matched Decimal, either an integer or float
      // match trailing suffixes...
      // try suffixes for float
      int expolen = matchExponent( len);
      // matched Decimal (Exponent)?
      int suflen_f = matchFloatSuffix( len + expolen);
      int suflen_i = 0;
      if (expolen == 0) {
        // no exponent, try suffixes for integer
        // match trailing LongSuffix and UnsignedSuffix...
        suflen_i = matchIntSuffix( len);
      }
      len += Math.max( suflen_i, suflen_f) + expolen;
    }
    return len;
  }

  /**
   * Matches a decimal number. <br>
   * Decimal: [0-9]+
   * 
   * @param lookAhead
   *        the position ahead of the current index of the input segment. *
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected int matchDecimal( int lookAhead)
  {
    int len = 0;
    while (Character.isDigit( LA( lookAhead++))) {
      len++;
    }
    return len;
  }

  /**
   * Matches a hexadecimal number. <br>
   * HexDecimal: [0-9a-fA-F]+
   * 
   * @param lookAhead
   *        the position ahead of the current index of the input segment. *
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected int matchHexDecimal( int lookAhead)
  {
    int len = 0;
    while (isHexDigit( LA( lookAhead++))) {
      len++;
    }
    return len;
  }

  /**
   * Matches an exponent. <br>
   * Exponent : ( 'e' | 'E' ) ( '+' | '-' )? Decimal
   * 
   * @param lookAhead
   *        the position ahead of the current index of the input segment. *
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected int matchExponent( int lookAhead)
  {
    int len = 0;
    char c = LA( lookAhead);
    if ((c == 'e' || c == 'E')) {
      c = LA( lookAhead + 1);
      if (c == '+' || c == '-') {
        len = matchDecimal( lookAhead + 2);
        if (len == 0) {
          // missing trailing number: no match
          return 0;
        }
        len += 2;
      }
      else {
        len = matchDecimal( lookAhead + 1);
        if (len == 0) {
          // missing trailing number: no match
          return 0;
        }
        len += 1;
      }
    }
    return len;
  }

  /**
   * Matches the suffix that indicates a floating point numeric literal. <br>
   * FloatSuffix: [fFlL]
   * 
   * @param lookAhead
   *        the position ahead of the current index of the input segment. *
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected abstract int matchFloatSuffix( int lookAhead);

  /**
   * Matches the suffix that indicates an integer numeric literal. <br>
   * IntSuffix: [lLuU]
   * 
   * @param lookAhead
   *        the position ahead of the current index of the input segment. *
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected abstract int matchIntSuffix( int lookAhead);

  // /////////////////////////////////////////////////////////
  // categoriser helper methods
  // /////////////////////////////////////////////////////////

  /**
   * Fetches the lookahead character at the specified position.
   * 
   * @see #input
   * @param lookAhead
   *        the position ahead of the current index of the input segment. *
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
   * @param num
   *        the positive number of character to consume.
   * @throws IllegalArgumentException
   *         if an invalid value is supplied or if the input segment does not
   *         contain the specified number of characters.
   */
  protected final void consumeChars( int num)
  {
    input.setIndex( num + input.getIndex());
  }

  // /////////////////////////////////////////////////////////
  // other helper methods
  // /////////////////////////////////////////////////////////

  /**
   * Looks if a subregion in the <code>input</code> starting at the current
   * scanner input position is equal to one of the Strings in
   * <code>wordlist</code>.
   * 
   * @see AbstractCategoriser#input
   * @param length
   *        the length of the region that must match.
   * @param matches
   *        the strings that may match.
   * @return <code>true</code> if a match was found, otherwise
   *         <code>false</code>.
   */
  protected final boolean matchOneOfStrings( int length, final String[] matches)
  {
    for (int i = 0; i < matches.length; i++) {
      if (matches[i].length() == length
          && AbstractCategoriser.regionMatches( false, input, input.getIndex(),
              matches[i]) > 0)
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
    for (int i = offset; i < endpos; i++, j++) {
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

  /**
   * Determines if the specified character is a digit.
   * 
   * @param ch
   *        the character to be tested.
   * @return <code>true</code> if the character is a digit; <code>false</code>
   *         otherwise.
   * @see java.lang.Character#isDigit(char)
   */
  public static boolean isHexDigit( char ch)
  {
    ch = Character.toUpperCase( ch);
    switch (ch) {
      case '0':
      case '1':
      case '2':
      case '3':
      case '4':
      case '5':
      case '6':
      case '7':
      case '8':
      case '9':
      case 'A':
      case 'B':
      case 'C':
      case 'D':
      case 'E':
      case 'F':
        return true;
    }
    return false;
  }

  /**
   * Checks whether a subregion in the <code>input</code> starting at the
   * current scanner input position is a custom identifier.
   * 
   * @see de.marw.javax.swing.text.highlight.categoriser.Category#IDENTIFIER_1
   * @param identifierLen
   *        the length of the region that must match.
   * @return <code>true</code> if the subregion is one of the keywords,
   *         otherwise <code>false</code>.
   */
  protected boolean isIdentifier1( int identifierLen)
  {
    // TODO Auto-generated method stub
    /*
     * mittels vom Anwender gefÃ¼llter Worttabelle (auc Document-Attribute?)
     * matchen
     */
    return false; // no match
  }

  /**
   * Checks whether a subregion in the <code>input</code> starting at the
   * current scanner input position is a custom identifier.
   * 
   * @see de.marw.javax.swing.text.highlight.categoriser.Category#IDENTIFIER_2
   * @param identifierLen
   *        the length of the region that must match.
   * @return <code>true</code> if the subregion is one of the keywords,
   *         otherwise <code>false</code>.
   */
  protected boolean isIdentifier2( int identifierLen)
  {
    // TODO Auto-generated method stub
    /*
     * mittels vom Anwender gefÃ¼llter Worttabelle (auc Document-Attribute?)
     * matchen
     */
    return false; // no match
  }

}