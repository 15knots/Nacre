/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight;

import java.io.Serializable;

import de.marw.javax.swing.text.highlight.categoriser.Token;


/**
 * Represents a lexical token category used to classify a piece of text for
 * syntax highlighting.
 * 
 * @see de.marw.javax.swing.text.highlight.categoriser.Token
 * @see de.marw.javax.swing.text.highlight.categoriser.Categoriser#nextToken(HighlightedDocument,
 *      Token) .
 * @author Martin Weber TODO unter Java v. 5 in ein enum konvertieren
 */
public class Category implements Serializable
{

  /**
   * Normal text category. This should be used to mark normal text without any
   * highlighting.
   */
  public static final Category NORMAL = new Category( "normal", 0);

  /**
   * Comment 1 category. This can be used to mark a comment. (eg, Java and C
   * modes use this to mark a multiple comment.)
   */
  public static final Category COMMENT_1 = new Category( "comment1", 1);

  /**
   * Comment 2 category. This can be used to mark a comment.(eg, Java and C++
   * modes use this to mark a single line comment.)
   */
  public static final Category COMMENT_2 = new Category( "comment2", 2);

  /**
   * Literal string category. This can be used to mark a string literal (eg,
   * Java and C modes use this to mark "..." literals.)
   */
  public static final Category STRINGVAL = new Category( "stringVal", 3);

  /**
   * Literal predefined value category. This can be used to mark an object
   * literal (eg, Java mode uses this to mark <code>true</code>,
   * <code>false</code>,<code>null</code>.)
   */
  public static final Category PREDEFVAL = new Category( "predefVal", 4);

  /**
   * Number category. Used to mark literal numbers.
   */
  public static final Category NUMERICVAL = new Category( "numericVal", 5);

  /**
   * Label category. This can be used to mark labels.
   */
  public static final Category LABEL = new Category( "label", 6);

  /**
   * Alternative keyword category. This can be used to mark a keyword. (C mode
   * uses this for preprocessor directives.)
   */
  public static final Category KEYWORD = new Category( "keyword", 7);

  /**
   * Statement Keyword category. This can be used to mark a keyword for
   * statement. (eg, for, if, while)
   */
  public static final Category KEYWORD_STATEMENT = new Category(
      "keywordStatement", 8);

  /**
   * Type keyword category. This can be used to mark a keyword for data types.
   */
  public static final Category KEYWORD_TYPE = new Category( "type", 9);

  /**
   * Operator keyword category. This can be used to mark an operator. (eg, C++
   * mode marks 'new' and 'sizeof' with this token type)
   */
  public static final Category KEYWORD_OPERATOR = new Category(
      "keywordOperator", 10);

  /**
   * Operator category. This can be used to mark an operator other than those
   * marked as <code>KEYWORD_OPERATOR</code>. (eg, Java mode marks +, -, etc
   * with this token type)
   */
  public static final Category OPERATOR = new Category( "operator", 11);

  /**
   * Documentation category. Used to mark special documentation (eg Javadoc
   * comments, Python's autodoc)
   */
  public static final Category DOC = new Category( "doc", 12);

  /**
   * Custom identifier 1 category. This can be used to mark identifiers of
   * interest, so the user can easily spot them.
   */
  public static final Category IDENTIFIER_1 = new Category( "identifier1", 13);

  /**
   * Custom identifier 2 category. This can be used to mark identifiers of
   * interest, so the user can easily spot them.
   */
  public static final Category IDENTIFIER_2 = new Category( "identifier2", 14);

  //----------------------------------------------------------
  private static final Category[] all = { NORMAL, COMMENT_1, COMMENT_2,
      STRINGVAL, PREDEFVAL, NUMERICVAL, LABEL, KEYWORD, KEYWORD_STATEMENT,
      KEYWORD_TYPE, KEYWORD_OPERATOR, OPERATOR, DOC, IDENTIFIER_1, IDENTIFIER_2 };

  /**
   * Numeric value of this Category.
   */
  private final int categoryId;

  /**
   * Sting representation of this Category.
   */
  private final String repr;

  /**
   * @param repr
   *        The name of this enum constant, as declared in the enum declaration.
   * @param category
   *        the numeric value of the category.
   */
  private Category( String repr, int categoryId)
  {
    this.categoryId = categoryId;
    this.repr = repr;
  }

  /**
   * Returns the numeric value of this Category.
   */
  public int ordinal()
  {
    return categoryId;
  }

  /**
   * Specifies the category as a string that can be used as a label.
   */
  public String getName()
  {
    return repr;
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
   * and is a <code>Category</code> object with the same numeric value.
   * 
   * @param obj
   *        the object to compare this category with.
   * @return <code>true</code> if the objects are equal; <code>false</code>
   *         otherwise.
   */
  public final boolean equals( Object obj)
  {
    if (obj instanceof Category) {
      return (categoryId == ((Category) obj).categoryId);
    }
    return false;
  }

  public String toString()
  {
    return repr;
  }

  public static Category[] values()
  {
    return all;
  }

}