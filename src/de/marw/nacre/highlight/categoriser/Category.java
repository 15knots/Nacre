//$Id$

package swing.text.highlight;

import java.io.Serializable;

import swing.text.highlight.categoriser.CategoryConstants;


/**
 * Represents a lexical token category. This wraps the constants used by the
 * categoriser to provide a convenient class that can be stored as a attribute
 * value.
 * 
 * @author weber
 */
public class Category implements Serializable
{

  private static Category[]  all               = { new Comment1(),
      new Comment2(), new Stringval(), new PredefVal(), new NumericVal(),
      new Label(), new Keyword1(), new Keyword2(), new Type(), new Operator(),
      new Identifier1(), new Identifier2(), new Doc() };

  /**
   * Key to be used in AttributeSet's holding a value of Category type.
   */
  public static final Object CategoryAttribute = new AttributeKey();

  /**
   * Numeric value of this Category.
   */
  private int                categoryId;

  /**
   * @param categoryId
   */
  protected Category( int categoryId)
  {
    this.categoryId = categoryId;
  }

  /**
   * Returns the numeric value of this Category. These are the values returned
   * by the categoriser.
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

    protected AttributeKey()
    {
    }

    public String toString()
    {
      return "category";
    }

  }

}