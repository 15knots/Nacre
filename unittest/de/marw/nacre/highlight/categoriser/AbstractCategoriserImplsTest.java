// $Id$
/*
 * Copyright 2004 by Martin Weber
 */

package de.marw.javax.swing.text.highlight.categoriser;

import javax.swing.text.Document;
import javax.swing.text.Segment;

import junit.framework.TestCase;


/**
 * Test for AbstractCategoriser. This tests the function implemented in
 * AbstractCategoriser.
 * 
 * @author Martin Weber
 */
public class AbstractCategoriserImplsTest extends TestCase
{
  /** der Categoriser im Test */
  protected AbstractCategoriser testee;

  /**
   * @param arg0
   */
  public AbstractCategoriserImplsTest( String arg0)
  {
    super( arg0);
    testee = new AbstractCategoriser() {

      protected int matchFloatSuffix( int lookahead)
      {
        // no suffixes available
        return 0;
      }

      protected int matchIntSuffix( int lookahead)
      {
        // no suffixes available
        return 0;
      }

      public void nextToken( Document doc, Token tokenBuf)
      {
        throw new UnsupportedOperationException( "method not implemented");
      }

    };
  }

  /*
   * @see TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
  }

  /*
   * @see TestCase#tearDown()
   */
  protected void tearDown() throws Exception
  {
    super.tearDown();
  }

  /**
   * Sets the input stream for test methods.
   * 
   * @param text
   */
  protected void input( String text)
  {
    char[] ca = text.toCharArray();
    testee.openInput( new Segment( ca, 0, ca.length));
  }

  public final void testLA()
  {
    String text;
    char[] ctext;
    char c;

    text = "0123456789abcdefghijklmn";
    input( text);
    ctext = text.toCharArray();
    for (int i = 0; i < ctext.length; i++) {
      c = testee.LA( i);
      assertEquals( ctext[i], c);
    }

    // check if CharacterIterator methods do not confuse us
    testee.input.first();
    testee.input.next();
    testee.input.next();
    for (int i = 0; i < ctext.length - 2; i++) {
      c = testee.LA( i);
      assertEquals( ctext[i + 2], c);
    }

    testee.closeInput();
  }

  public final void testConsumeChars()
  {
    String text;
    char c;

    text = "0123456789abcdefghijklmn";
    input( text);
    testee.consumeChars( 3);
    c = testee.LA( 0);
    assertEquals( '3', c);

    // check if CharacterIterator methods do not confuse us
    testee.input.next();
    testee.consumeChars( 3);
    c = testee.LA( 0);
    assertEquals( '7', c);
    testee.consumeChars( 0);
    c = testee.LA( 0);
    assertEquals( '7', c);

    try {
      testee.consumeChars( 33);
      fail( "expected IllegalArgumentException");
    }
    catch (IllegalArgumentException ex) {
    }

    testee.closeInput();
  }

  public final void testMatchWhitespace()
  {
    int len;
    String text;

    text = "";
    input( text);
    len = testee.matchWhitespace();
    assertEquals( "zero length", 0, len);
    testee.closeInput();

    text = "bla";
    input( text);
    len = testee.matchWhitespace();
    assertEquals( "zero length", 0, len);
    testee.closeInput();

    text = " ";
    input( text);
    len = testee.matchWhitespace();
    assertEquals( 1, len);
    testee.closeInput();

    text = " bla";
    input( text);
    len = testee.matchWhitespace();
    assertEquals( 1, len);
    testee.closeInput();

    text = " \r\n" + "\u0009" + "\u000B" + "\u000C" + "\u001C" + "\u001D"
        + "\u001E" + "\u001F" + "bla";
    input( text);
    len = testee.matchWhitespace();
    assertEquals( 10, len);
    testee.closeInput();
  }

  public final void testMatchDecimal()
  {
    int len;
    String text;

    text = "";
    input( text);
    len = testee.matchDecimal( 0);
    assertEquals( "zero length", 0, len);
    testee.closeInput();

    text = "....";
    input( text);
    for (int i = 0; i < text.length(); i++) {
      len = testee.matchDecimal( i);
      assertEquals( "no match", 0, len);
    }
    testee.closeInput();

    text = "01234567890123456789.0123456789abcdefxyz";
    input( text);
    int i;
    for (i = 0; i < 20; i++) {
      len = testee.matchDecimal( i);
      assertEquals( "match len", 20 - i, len);
    }
    i = 20;
    len = testee.matchDecimal( i);
    assertEquals( "no match", 0, len);
    for (i = 21; i < 31; i++) {
      len = testee.matchDecimal( i);
      assertEquals( "match len", 31 - i, len);
    }
    testee.closeInput();
  }

  public final void testMatchHexDecimal()
  {
    int len;
    String text;

    text = "";
    input( text);
    len = testee.matchHexDecimal( 0);
    assertEquals( "zero length", 0, len);
    testee.closeInput();

    text = "....";
    input( text);
    int i;
    for (i = 0; i < text.length(); i++) {
      len = testee.matchHexDecimal( i);
      assertEquals( "no match", 0, len);
    }
    testee.closeInput();

    text = "0123456789abcdefABCDEF0123456789.ABCDEFabcdef0123456789xyz";
    input( text);
    for (i = 0; i < 20 + 12; i++) {
      len = testee.matchHexDecimal( i);
      assertEquals( "match len", 20 + 12 - i, len);
    }
    i = 20 + 12;
    len = testee.matchHexDecimal( i);
    assertEquals( "no match", 0, len);
    for (i = 21 + 12; i < 31 + 12 + 12; i++) {
      len = testee.matchHexDecimal( i);
      assertEquals( "match len", 31 + 12 + 12 - i, len);
    }
    testee.closeInput();
  }

  public final void testMatchExponent()
  {
    int len;
    String text;

    text = "";
    input( text);
    len = testee.matchHexDecimal( 0);
    assertEquals( "zero length", 0, len);
    testee.closeInput();

    text = "....";
    input( text);
    int i;
    for (i = 0; i < text.length(); i++) {
      len = testee.matchHexDecimal( i);
      assertEquals( "no match", 0, len);
    }
    testee.closeInput();

    text = "56789e01234567890123456789xyz";
    input( text);
    for (i = 0; i < 5; i++) {
      len = testee.matchExponent( i);
      assertEquals( "no match ", 0, len);
    }
    len = testee.matchExponent( 5);
    assertEquals( "match len", 21, len);
    testee.closeInput();

    text = "1E0123456789xyz";
    input( text);
    len = testee.matchExponent( 1);
    assertEquals( "match len", 11, len);
    testee.closeInput();

    text = "2e-0123456789xyz";
    input( text);
    len = testee.matchExponent( 1);
    assertEquals( "match len", 12, len);
    testee.closeInput();

    text = "3E+0123456789xyz";
    input( text);
    len = testee.matchExponent( 1);
    assertEquals( "match len", 12, len);
    testee.closeInput();

  }

  public void testMatchNumber()
  {
    int len;

    input( "01234abcdefxyz");
    len = testee.matchNumber();
    assertEquals( "match len", 5, len);
    testee.closeInput();

    input( "0x01234abcdefxyz");
    len = testee.matchNumber();
    assertEquals( "match len", 13, len);
    testee.closeInput();

    input( "0x-01234abcdefxyz");
    len = testee.matchNumber();
    assertEquals( "match '0'", 1, len);
    testee.closeInput();

    input( "0xyz");
    len = testee.matchNumber();
    assertEquals( "match '0'", 1, len);
    testee.closeInput();

    input( ".e12345");
    len = testee.matchNumber();
    assertEquals( "no match", 0, len);
    testee.closeInput();

    input( ".e-12345");
    len = testee.matchNumber();
    assertEquals( "no match", 0, len);
    testee.closeInput();

    input( ".01234abcdefxyz");
    len = testee.matchNumber();
    assertEquals( "match len", 6, len);
    testee.closeInput();

    input( ".01234e56789abcdefxyz");
    len = testee.matchNumber();
    assertEquals( "match len", 12, len);
    testee.closeInput();

    input( "56789.01234abcdefxyz");
    len = testee.matchNumber();
    assertEquals( "match len", 11, len);
    testee.closeInput();

    input( "56789.01234E-56789abcdefxyz");
    len = testee.matchNumber();
    assertEquals( "match len", 18, len);
    testee.closeInput();

    input( "56789.e56789abcdefxyz");
    len = testee.matchNumber();
    assertEquals( "match len", 5, len);
    testee.closeInput();

    input( "56789.E56789abcdefxyz");
    len = testee.matchNumber();
    assertEquals( "match len", 5, len);
    testee.closeInput();

  }

  public final void testMatchOneOfStrings()
  {
    boolean matched;
    String[] matches;
    String text = "hhhhhhhhhhh01234";
    input( text);

    matches = new String[] {};
    matched = testee.matchOneOfStrings( 0, matches);
    assertEquals( "match empty", false, matched);
    matched = testee.matchOneOfStrings( 100, matches);
    assertEquals( "match empty", false, matched);

    matches = new String[] { "", "x", " " };
    matched = testee.matchOneOfStrings( 0, matches);
    assertEquals( "match empty", false, matched);
    matched = testee.matchOneOfStrings( 1, matches);
    assertEquals( "match empty", false, matched);

    matches = new String[text.length() - 1];
    for (int i = 0; i < text.length() - 1; i++) {
      matches[i] = text.substring( 0, i + 1);
    }
    for (int i = 0; i < matches.length; i++) {
      matched = testee.matchOneOfStrings( matches[i].length(), matches);
      assertEquals( "match \"" + matches[i] + "\"", true, matched);
    }

    testee.closeInput();
  }

  public final void testRegionMatches()
  {
    int len;
    char[] ca = "01234567890123456789".toCharArray();
    Segment seg = new Segment( ca, 0, ca.length);

    len = AbstractCategoriser.regionMatches( false, seg, 0, "0123456789");
    assertEquals( "match len", 10, len);
    len = AbstractCategoriser.regionMatches( false, seg, 3, "3456789012");
    assertEquals( "match len", 10, len);
    len = AbstractCategoriser.regionMatches( false, seg, 3, "345678901 ");
    assertEquals( "no match", 0, len);

    ca = "abcdefghijabcdefghij".toCharArray();
    seg = new Segment( ca, 0, ca.length);
    len = AbstractCategoriser.regionMatches( true, seg, 0, "ABCDEFGHIJ");
    assertEquals( "match len", 10, len);
    len = AbstractCategoriser.regionMatches( true, seg, 3, "DEFGHIJABC");
    assertEquals( "match len", 10, len);
    len = AbstractCategoriser.regionMatches( true, seg, 3, "DEFGHIJABC ");
    assertEquals( "no match", 0, len);

    ca = "a�o�u�".toCharArray();
    seg = new Segment( ca, 0, ca.length);
    len = AbstractCategoriser.regionMatches( true, seg, 0, "A�O�U�");
    assertEquals( "match len", 6, len);
  }

}