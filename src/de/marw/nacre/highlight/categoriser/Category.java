/*
 * @(#)Category.java 1.2 99/05/27 Copyright (c) 1998 Sun Microsystems, Inc. All
 * Rights Reserved. This software is the confidential and proprietary
 * information of Sun Microsystems, Inc. ("Confidential Information"). You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Sun.
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

package swing.text.highlight;

import java.io.Serializable;

import swing.text.highlight.categoriser.CategoryConstants;


/**
 * Simple class to represent a lexical token. This wraps the Constants used by
 * the scanner to provide a convenient class that can be stored as a attribute
 * value.
 * 
 * @author Timothy Prinzing
 * @version 1.2 05/27/99
 */
public class Category implements Serializable
{

  private Category( int categoryId)
  {
    this.categoryId = categoryId;
  }

  /**
   * Numeric value of this Category. This is the value returned by the
   * categorizer.
   */
  public int getId()
  {
    return categoryId;
  }

  /**
   * Specifies the category as a string that can be used as a label.
   */
  public String getName()
  {
    String nm = getClass().getName();
    int nmStart = nm.lastIndexOf( '.') + 1; // not found results in 0
    return nm.substring( nmStart, nm.length());
  }

  /**
   * Returns a hashcode for this set of attributes.
   * 
   * @return a hashcode value for this set of attributes.
   */
  public final int hashCode()
  {
    return categoryId;
  }

  /**
   * Compares this object to the specifed object. The result is
   * <code>true</code> if and only if the argument is not <code>null</code>
   * and is a <code>Font</code> object with the same name, style, and point
   * size as this font.
   * 
   * @param obj
   *          the object to compare this font with.
   * @return <code>true</code> if the objects are equal; <code>false</code>
   *         otherwise.
   */
  public final boolean equals( Object obj)
  {
    if (obj instanceof Category) {
      Category t = (Category) obj;
      return (categoryId == t.categoryId);
    }
    return false;
  }

  public static Category[] getCategories()
  {
    return all;
  }

  // --- variables -------------------------------------

  /**
   * Key to be used in AttributeSet's holding a value of Category.
   */
  public static final Object CategoryAttribute = new AttributeKey();

  private int                categoryId;

  //----------------------------------------------------------
  public static class Comment1 extends Category
  {

    Comment1()
    {
      super( CategoryConstants.COMMENT1);
    }

  }

  public static class Comment2 extends Category
  {

    Comment2()
    {
      super( CategoryConstants.COMMENT2);
    }
  }

  public static class Stringval extends Category
  {

    Stringval()
    {
      super( CategoryConstants.STRINGVAL);
    }
  }

  public static class PredefVal extends Category
  {

    PredefVal()
    {
      super( CategoryConstants.PREDEFVAL);
    }
  }

  public static class NumericVal extends Category
  {

    NumericVal()
    {
      super( CategoryConstants.NUMERICVAL);
    }
  }

  public static class Label extends Category
  {

    Label()
    {
      super( CategoryConstants.LABEL);
    }
  }

  public static class Keyword1 extends Category
  {

    Keyword1()
    {
      super( CategoryConstants.KEYWORD1);
    }
  }

  public static class Keyword2 extends Category
  {

    Keyword2()
    {
      super( CategoryConstants.KEYWORD2);
    }
  }

  public static class Type extends Category
  {

    Type()
    {
      super( CategoryConstants.TYPE);
    }
  }

  public static class Operator extends Category
  {

    Operator()
    {
      super( CategoryConstants.OPERATOR);
    }
  }

  public static class Identifier1 extends Category
  {

    Identifier1()
    {
      super( CategoryConstants.IDENTIFIER1);
    }
  }

  public static class Identifier2 extends Category
  {

    Identifier2()
    {
      super( CategoryConstants.IDENTIFIER2);
    }
  }

  public static class Doc extends Category
  {

    Doc()
    {
      super( CategoryConstants.DOC);
    }
  }

  //  public static class Special extends Category
  //  {
  //
  //    Special( )
  //    {
  //      super( representation, categoryId);
  //    }
  //  }

  static class AttributeKey
  {

    private AttributeKey()
    {
    }

    public String toString()
    {
      return "category";
    }

  }

  private static Category[] all = { new Comment1(), new Comment2(),
      new Stringval(), new PredefVal(), new NumericVal(), new Label(),
      new Keyword1(), new Keyword2(), new Type(), new Operator(),
      new Identifier1(), new Identifier2(), new Doc() };

}