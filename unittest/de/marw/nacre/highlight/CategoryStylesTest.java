// $Header$
/*
 * Copyright 2005 by Martin Weber
 */

package de.marw.javax.swing.text.highlight;

import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import junit.framework.TestCase;


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

  public final void testIsDefined()
  {
    // TODO Implement isDefined().
  }

  public final void testGetStyle()
  {
    // TODO Implement getStyle().
  }

  public final void testGetColor()
  {
    // TODO Implement getColor().
  }

  public final void testIsBold()
  {
    // TODO Implement isBold().
  }

  public final void testIsItalic()
  {
    // TODO Implement isItalic().
  }

  public final void testSetColor()
  {
    // TODO Implement setColor().
  }

  public final void testSetBold()
  {
    // TODO Implement setBold().
  }

  public final void testSetItalic()
  {
    // TODO Implement setItalic().
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
    testee.setColor( Category.KEYWORD_STATEMENT, keywordCol);
    testee.setBold( Category.KEYWORD_STATEMENT, true);

    File f = File.createTempFile( "test", null);
    f.deleteOnExit();
    FileOutputStream fos = new FileOutputStream( f);
    ObjectOutputStream oos = new ObjectOutputStream( fos);
    oos.writeObject( testee);
    oos.close();

    FileInputStream fi = new FileInputStream( f);
    ObjectInputStream ois = new ObjectInputStream( fi);
    CategoryStyles cats = (CategoryStyles) ois.readObject();
    ois.close();
  }
}
