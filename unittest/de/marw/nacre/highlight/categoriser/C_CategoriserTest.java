// $Id$
/*
 * Copyright 2004 by Martin Weber
 */

package de.marw.nacre.highlight.categoriser;

import de.marw.nacre.editorkits.C_Categoriser;



/**
 * @author Martin Weber
 */
public class C_CategoriserTest extends AbstractCategoriserImplsTest
{

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
   * Constructor for C_CategoriserTest.
   * 
   * @param arg0
   */
  public C_CategoriserTest( String arg0)
  {
    super( arg0);
    testee = new C_Categoriser();
  }

  public final void testMatchFloatSuffix()
  {
    int len, i;

    input( "");
    len = testee.matchFloatSuffix( 0);
    assertEquals( "zero length", 0, len);
    testee.closeInput();

    String text;
    text = "1234567890abcdeghijkmnopqrstuvwxyzABCDEGHIJKMNOPQRSTUVWXYZ"
        + "<>|,;.:-_#'+*~^�!\"�$%&/{([)])]0=}�?\\�`";
    input( text);
    for (i = 0; i < text.length(); i++) {
      len = testee.matchFloatSuffix( i);
      assertEquals( "no match '" + text.charAt( i) + "'", 0, len);
    }
    testee.closeInput();

    input( "ff FF ll LL123456789");
    for (i = 0; i < 4; i++) {
      len = testee.matchFloatSuffix( i * 3);
      assertEquals( "match len ["+i+"]", 1, len);
    }
    testee.closeInput();

    input( "FL LF fl lf123456789");
    for (i = 0; i < 4; i++) {
      len = testee.matchFloatSuffix( i * 3);
      assertEquals( "match len ["+i+"]", 2, len);
    }
    testee.closeInput();

  }

  public final void testMatchIntSuffix()
  {
    int len, i;

    input( "");
    len = testee.matchIntSuffix( 0);
    assertEquals( "zero length", 0, len);
    testee.closeInput();

    String text;
    text = "1234567890abcdefghijkmnopqrstvwxyzABCDEFGHIJKMNOPQRSTVWXYZ"
        + "<>|,;.:-_#'+*~^�!\"�$%&/{([)])]0=}�?\\�`";
    input( text);
    for (i = 0; i < text.length(); i++) {
      len = testee.matchIntSuffix( i);
      assertEquals( "no match '" + text.charAt( i) + "'", 0, len);
    }
    testee.closeInput();

    input( "uu UU ll LL123456789");
    for (i = 0; i < 4; i++) {
      len = testee.matchIntSuffix( i * 3);
      assertEquals( "match len ["+i+"]", 1, len);
    }
    testee.closeInput();

    input( "UL LU ul lu123456789");
    for (i = 0; i < 4; i++) {
      len = testee.matchIntSuffix( i * 3);
      assertEquals( "match len ["+i+"]", 2, len);
    }
    testee.closeInput();
  }

  public final void testMatchNumber()
  {
    int len, i;

    /* ints or floats */
    input( "1l 2L 3u 4U 5f 6F ");
    for (i = 0; i < 6; i++) {
      len = testee.matchNumber();
      assertEquals( "match len ["+i+"]", 2, len);
      testee.consumeChars( 3);
    }
    testee.closeInput();

    input( "1lu 2LU 3ul 4UL 5lf 6LF ");
    for (i = 0; i < 6; i++) {
      len = testee.matchNumber();
      assertEquals( "match len ["+i+"]", 3, len);
      testee.consumeChars( 4);
    }
    testee.closeInput();

    input( "lu LU ul UL lf LF ");
    for (i = 0; i < 6; i++) {
      len = testee.matchNumber();
      assertEquals( "no match ["+i+"]", 0, len);
      testee.consumeChars( 3);
    }
    testee.closeInput();

    /* hex ints */
    input( "0x1l 0x2L 0x3u 0x4U ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match len ["+i+"]", 4, len);
      testee.consumeChars( 5);
    }
    testee.closeInput();

    input( "0x1lu 0x2LU 0x3ul 0x4UL ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match len ["+i+"]", 5, len);
      testee.consumeChars( 6);
    }
    testee.closeInput();

    input( "0x1ll 0x2LL 0x3uu 0x4UU ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match no duplicates", 4, len);
      testee.consumeChars( 6);
    }
    testee.closeInput();

    input( "0xl 0xL 0xu 0xU ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match '0'", 1, len);
      testee.consumeChars( 4);
    }
    testee.closeInput();

    input( "0xLU 0xlu 0xUL 0xul ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match '0'", 1, len);
      testee.consumeChars( 5);
    }
    testee.closeInput();

    input( "0x-1l 0x-2L 0x-3u 0x-4U ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match '0'", 1, len);
      testee.consumeChars( 6);
    }
    testee.closeInput();

    /* floats */
    input( ".1e1l .2e2L .3e3f .4e4F ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match len ["+i+"]", 5, len);
      testee.consumeChars( 6);
    }
    testee.closeInput();

    input( ".1e1lf .2e2LF .3e3fl .4e4FL ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match len ["+i+"]", 6, len);
      testee.consumeChars( 7);
    }
    testee.closeInput();

    input( ".1e-1lf .2e-2LF .3e-3fl .4e-4FL ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match len ["+i+"]", 7, len);
      testee.consumeChars( 8);
    }
    testee.closeInput();

    input( ".1e+1lf .2e+2LF .3e+3fl .4e+4FL ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match len ["+i+"]", 7, len);
      testee.consumeChars( 8);
    }
    testee.closeInput();

    input( ".1e1ll .2e2LL .3e3ff .4e4FF ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match no duplicates", 5, len);
      testee.consumeChars( 7);
    }
    testee.closeInput();

    input( ".e1l .e2L .e3f .e4F ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "no match ["+i+"]", 0, len);
      testee.consumeChars( 5);
    }
    testee.closeInput();

    input( ".e1lf .e2LF .e3fl .e4FL ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "no match ["+i+"]", 0, len);
      testee.consumeChars( 6);
    }
    testee.closeInput();

    input( "1234.1e1l 1234.2e2L 1234.3e3f 1234.4e4F ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match len ["+i+"]", 9, len);
      testee.consumeChars( 10);
    }
    testee.closeInput();

    input( "1234.1e1lf 1234.2e2LF 1234.3e3fl 1234.4e4FL ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match len ["+i+"]", 10, len);
      testee.consumeChars( 11);
    }
    testee.closeInput();

    input( "1234.1e-1lf 1234.2e-2LF 1234.3e-3fl 1234.4e-4FL ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match len ["+i+"]", 11, len);
      testee.consumeChars( 12);
    }
    testee.closeInput();

    input( "1234.1e+1lf 1234.2e+2LF 1234.3e+3fl 1234.4e+4FL ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match len [" + i + "]", 11, len);
      testee.consumeChars( 12);
    }
    testee.closeInput();

    input( "1234.1e1ll 1234.2e2LL 1234.3e3ff 1234.4e4FF ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "match no duplicates", 9, len);
      testee.consumeChars( 11);
    }
    testee.closeInput();

    input( "1234.e1l 1234.e2L 1234.e3f 1234.e4F ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "no match ["+i+"]", 4, len);
      testee.consumeChars( 9);
    }
    testee.closeInput();

    input( "1234.e1lf 1234.e2LF 1234.e3fl 1234.e4FL ");
    for (i = 0; i < 4; i++) {
      len = testee.matchNumber();
      assertEquals( "no match ["+i+"]", 4, len);
      testee.consumeChars( 10);
    }
    testee.closeInput();

  }

  public final void testNextToken()
  {
    //TODO Implement nextToken().
  }

}