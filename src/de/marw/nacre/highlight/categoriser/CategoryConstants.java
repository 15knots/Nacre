/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight.categoriser;

import de.marw.javax.swing.text.highlight.HighlightedDocument;


/**
 * Constants used to classify a piece of text for syntax highlighting.
 * 
 * @see de.marw.javax.swing.text.highlight.categoriser.Token
 * @see de.marw.javax.swing.text.highlight.categoriser.Categoriser#nextToken(HighlightedDocument,
 *      Token)
 * @author Martin Weber
 * @deprecated wird nicht mehr benötigt, Category ist jetzt eine typesafe
 *             enumeration.
 */
public interface CategoryConstants
{

  //  /**
  //   * Normal text category id. This should be used to mark normal text without
  //   * any highlighting.
  //   */
  //  public static final int NORMAL = 0;
  //
  //  /**
  //   * Comment 1 category id. This can be used to mark a comment.
  //   */
  //  public static final int COMMENT1 = 1;
  //
  //  /**
  //   * Comment 2 category id. This can be used to mark a comment.
  //   */
  //  public static final int COMMENT2 = 2;
  //
  //  /**
  //   * Literal string category id. This can be used to mark a string literal
  // (eg,
  //   * C mode uses this to mark "..." literals)
  //   */
  //  public static final int STRINGVAL = 3;
  //
  //  /**
  //   * Literal predefined value category id. This can be used to mark an object
  //   * literal (eg, Java mode uses this to mark true, false, none, super, this,
  //   * etc)
  //   */
  //  public static final int PREDEFVAL = 4;
  //
  //  /**
  //   * Number category id. Used to mark number values.
  //   */
  //  public static final int NUMERICVAL = 5;
  //
  //  /**
  //   * Label category id. This can be used to mark labels.
  //   */
  //  public static final int LABEL = 6;
  //
  //  /**
  //   * Keyword 1 category id. This can be used to mark a keyword. This should be
  //   * used for statements.
  //   */
  //  public static final int KEYWORD1 = 7;
  //
  //  /**
  //   * Keyword 2 category id. This can be used to mark a keyword. This should be
  //   * used for preprocessor directives.
  //   */
  //  public static final int KEYWORD2 = 8;
  //
  //  /**
  //   * Type keyword category id. This can be used to mark a keyword. This should
  //   * be used for data types.
  //   */
  //  public static final int TYPE = 9;
  //
  //  /**
  //   * Operator category id. This can be used to mark an operator. (eg, SQL mode
  //   * marks +, -, etc with this token type)
  //   */
  //  public static final int OPERATOR = 10;
  //
  //  /**
  //   * Identifier1 category id. This can be used to mark identifers of interest,
  //   * so the user can easily spot them.
  //   */
  //  public static final int IDENTIFIER1 = 11;
  //
  //  /**
  //   * Identifier1 category id. This can be used to mark identifers of interest,
  //   * so the user can easily spot them.
  //   */
  //  public static final int IDENTIFIER2 = 12;
  //
  //  /**
  //   * Documentation category id. Used to mark special documentation (eg Javadoc
  //   * comments, Python's autodoc)
  //   */
  //  public static final int DOC = 13;
  //
  //  public static final int MaximumId = DOC;

}