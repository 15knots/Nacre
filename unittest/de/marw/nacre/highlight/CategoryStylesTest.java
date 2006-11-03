// $Id$
/*
 * Copyright 2005 by Martin Weber
 */

package de.marw.nacre.highlight;

import java.awt.Color;
import java.awt.Font;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;
import de.marw.nacre.highlight.categoriser.Category;


/**
 * @author weber
 */
public class CategoryStylesTest extends TestCase
{
  private CategoryStyles testee;

  /**
   * @see junit.framework.TestCase#setUp()
   */
  protected void setUp() throws Exception
  {
    super.setUp();
    testee = new CategoryStyles();
  }

  public final void testEquals()
  {
    CategoryStyles.applyDefaultStyles( testee);
    CategoryStyles testee2 = new CategoryStyles();
    assertFalse( testee.equals( testee2));
    assertFalse( testee2.equals( testee));

    CategoryStyles.applyDefaultStyles( testee2);
    assertTrue( testee.equals( testee2));
    assertTrue( testee2.equals( testee));
  }

  public final void testGetStyle()
  {
    for (Category cat : Category.values()) {
      assertEquals( "style: " + cat, Font.PLAIN, testee.getStyle( cat));
    }

    Category cat2 = Category.OPERATOR;
    testee.setBold( cat2, true);
    assertEquals( "style: " + cat2, Font.BOLD, testee.getStyle( cat2));
    testee.setItalic( cat2, true);
    assertEquals( "style: " + cat2, Font.BOLD | Font.ITALIC, testee
        .getStyle( cat2));
    testee.setBold( cat2, false);
    assertEquals( "style: " + cat2, Font.ITALIC, testee.getStyle( cat2));
    testee.setItalic( cat2, false);
    for (Category cat : Category.values()) {
      assertEquals( "style: " + cat, Font.PLAIN, testee.getStyle( cat));
    }
  }

  public final void testGetColor()
  {
    for (Category cat : Category.values()) {
      assertNull( "uninitialized: " + cat, testee.getColor( cat));
    }
  }

  public final void testIsBold()
  {
    for (Category cat : Category.values()) {
      assertEquals( "bold: " + cat, false, testee.isBold( cat));
    }
  }

  public final void testIsItalic()
  {
    for (Category cat : Category.values()) {
      assertEquals( "italic: " + cat, false, testee.isItalic( cat));
    }
  }

  public final void testSetColor()
  {
    final Color commentColor = new Color( 63, 127, 95);

    assertEquals( "color is null", null, testee
        .getColor( Category.KEYWORD_STATEMENT));

    testee.setColor( Category.KEYWORD_STATEMENT, commentColor);
    assertEquals( "color", commentColor, testee
        .getColor( Category.KEYWORD_STATEMENT));

    testee.setColor( Category.KEYWORD_STATEMENT, null);
    assertEquals( "color is null", null, testee
        .getColor( Category.KEYWORD_STATEMENT));
  }

  public final void testSetBold()
  {
    assertEquals( "bold", false, testee.isBold( Category.KEYWORD_STATEMENT));

    testee.setBold( Category.KEYWORD_STATEMENT, true);
    assertEquals( "bold", true, testee.isBold( Category.KEYWORD_STATEMENT));

    testee.setBold( Category.KEYWORD_STATEMENT, false);
    assertEquals( "bold", false, testee.isBold( Category.KEYWORD_STATEMENT));

    testee.setBold( Category.KEYWORD_STATEMENT, true);
    assertEquals( "bold", true, testee.isBold( Category.KEYWORD_STATEMENT));
  }

  public final void testSetItalic()
  {
    assertEquals( "italic", false, testee.isItalic( Category.KEYWORD));

    testee.setItalic( Category.KEYWORD, true);
    assertEquals( "italic", true, testee.isItalic( Category.KEYWORD));

    testee.setItalic( Category.KEYWORD, false);
    assertEquals( "italic", false, testee.isItalic( Category.KEYWORD));

    testee.setItalic( Category.KEYWORD, true);
    assertEquals( "italic", true, testee.isItalic( Category.KEYWORD));
  }

  public final void testIsDefined()
  {
    for (Category cat : Category.values()) {
      assertEquals( "defined: " + cat, false, testee.isDefined( cat));
    }
    testee.setBold( Category.NUMERICVAL, true);
    assertEquals( "bold: " + Category.NUMERICVAL, true, testee
        .isBold( Category.NUMERICVAL));
    for (Category cat : Category.values()) {
      assertEquals( "defined: " + cat, cat == Category.NUMERICVAL, testee
          .isDefined( cat));
    }
    testee.setBold( Category.NUMERICVAL, false);
    assertEquals( "bold: " + Category.NUMERICVAL, false, testee
        .isBold( Category.NUMERICVAL));
    for (Category cat : Category.values()) {
      assertEquals( "defined: " + cat, false, testee.isDefined( cat));
    }

    final Color commentColor = new Color( 63, 127, 95);
    testee.setBold( Category.OPERATOR, true);
    testee.setColor( Category.OPERATOR, commentColor);
    testee.setItalic( Category.OPERATOR, true);
    assertEquals( "defined: " + Category.OPERATOR, true, testee
        .isDefined( Category.OPERATOR));
    testee.setColor( Category.OPERATOR, null);
    assertEquals( "defined: " + Category.OPERATOR, true, testee
        .isDefined( Category.OPERATOR));
    testee.setBold( Category.OPERATOR, false);
    assertEquals( "defined: " + Category.OPERATOR, true, testee
        .isDefined( Category.OPERATOR));
    testee.setItalic( Category.OPERATOR, false);
    assertEquals( "defined: " + Category.OPERATOR, false, testee
        .isDefined( Category.OPERATOR));
  }

  /*
   * Test method for
   * 'de.marw.nacre.highlight.CategoryStyles.undefine(Category)'
   */
  public void testUndefine()
  {
    for (Category cat : Category.values()) {
      assertEquals( "defined: " + cat, false, testee.isDefined( cat));
    }
    final Color aColor = new Color( 63, 127, 95);
    testee.setBold( Category.OPERATOR, true);
    testee.setColor( Category.OPERATOR, aColor);
    testee.setItalic( Category.OPERATOR, true);
    assertEquals( "defined: " + Category.OPERATOR, true, testee
        .isDefined( Category.OPERATOR));
    testee.undefine( Category.OPERATOR);
    assertEquals( "defined: " + Category.OPERATOR, false, testee
        .isDefined( Category.OPERATOR));
    assertEquals( "color", null, testee.getColor( Category.OPERATOR));
    assertEquals( "style: ", Font.PLAIN, testee.getStyle( Category.OPERATOR));
    assertEquals( "bold", false, testee.isBold( Category.OPERATOR));
    assertEquals( "italic", false, testee.isItalic( Category.OPERATOR));

  }

  /*
   * Test method for
   * 'de.marw.nacre.highlight.CategoryStyles.replaceWith(CategoryStyles)'
   */
  public void testReplaceWith()
  {
    final Color aColor = new Color( 63, 127, 95);
    testee.setBold( Category.OPERATOR, true);
    testee.setColor( Category.OPERATOR, aColor);
    testee.setItalic( Category.OPERATOR, true);

    CategoryStyles testee2 = new CategoryStyles();
    CategoryStyles.applyDefaultStyles( testee2);
    testee.undefine(Category.OPERATOR);
    assertFalse( testee2.equals( testee));
    testee.replaceWith(testee2);
    assertTrue( testee.equals( testee2));
  }

  public final void testSerialization() throws IOException,
      ClassNotFoundException
  {
    final Color keywordCol = new Color( 127, 0, 85);
    final Color literalColor = new Color( 42, 0, 255);
    final Color commentColor = new Color( 63, 127, 95);

    testee.setColor( Category.COMMENT_1, commentColor);
    testee.setColor( Category.COMMENT_2, commentColor);
    testee.setColor( Category.STRINGVAL, literalColor);
    testee.setItalic( Category.STRINGVAL, true);
    testee.setColor( Category.NUMERICVAL, literalColor);
    testee.setColor( Category.PREDEFVAL, literalColor);
    testee.setBold( Category.PREDEFVAL, true);
    testee.setItalic( Category.PREDEFVAL, true);
    testee.setColor( Category.KEYWORD_STATEMENT, keywordCol);
    testee.setBold( Category.KEYWORD_STATEMENT, true);

    ByteArrayOutputStream os = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream( os);
    oos.writeObject( testee);
    oos.close();

    InputStream is = new ByteArrayInputStream( os.toByteArray());
    ObjectInputStream ois = new ObjectInputStream( is);
    CategoryStyles cats = (CategoryStyles) ois.readObject();
    ois.close();

    for (Category cat : Category.values()) {
      assertEquals( "de-serialized " + cat, testee.isBold( cat), cats
          .isBold( cat));
      assertEquals( "de-serialized " + cat, testee.isItalic( cat), cats
          .isItalic( cat));
      assertEquals( "de-serialized " + cat, testee.getColor( cat), cats
          .getColor( cat));
      assertEquals( "de-serialized " + cat, testee.isDefined( cat), cats
          .isDefined( cat));
    }
    assertTrue( "de-serialized ", testee.equals(cats));
  }
}
