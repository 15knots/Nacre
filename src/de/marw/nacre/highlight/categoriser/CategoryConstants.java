// $Header:
// /home/weber/cvsRepos/highlighting/swing/text/highlight/categoriser/CategoryConstants.java,v
// 1.1 2004/09/22 19:05:12 weber Exp $

// Copyright © 2004 Razorcat Development GmbH

package swing.text.highlight.categoriser;

import swing.text.highlight.HighlightedDocument;


/**
 * Constants used to classify a piece of text for syntax highlighting.
 * 
 * @see swing.text.highlight.categoriser.Token
 * @see swing.text.highlight.categoriser.Categoriser#nextToken(HighlightedDocument,
 *      Token)
 * @author Martin Weber
 */
public interface CategoryConstants
{

  /**
   * Normal text category id. This should be used to mark normal text without
   * any highlighting.
   */
  public static final int NORMAL      = 0;

  /**
   * Comment 1 category id. This can be used to mark a comment.
   */
  public static final int COMMENT1    = 1;

  /**
   * Comment 2 category id. This can be used to mark a comment.
   */
  public static final int COMMENT2    = 2;

  /**
   * Literal 1 category id. This can be used to mark a string literal (eg, C
   * mode uses this to mark "..." literals)
   */
  public static final int STRINGVAL   = 3;

  /**
   * Literal 2 category id. This can be used to mark an object literal (eg, Java
   * mode uses this to mark true, false, none, super, this, etc)
   */
  public static final int PREDEFVAL   = 4;

  /**
   * Number category id. Used to mark number values.
   */
  public static final int NUMERICVAL  = 5;

  /**
   * Label category id. This can be used to mark labels (eg, C mode uses this to
   * mark ...: sequences)
   */
  public static final int LABEL       = 6;

  /**
   * Keyword 1 category id. This can be used to mark a keyword. This should be
   * used for general language constructs.
   */
  public static final int KEYWORD1    = 7;

  /**
   * Keyword 2 category id. This can be used to mark a keyword. This should be
   * used for preprocessor commands, or variables.
   */
  public static final int KEYWORD2    = 8;

  /**
   * Keyword 3 category id. This can be used to mark a keyword. This should be
   * used for data types.
   */
  public static final int TYPE        = 9;

  /**
   * Operator category id. This can be used to mark an operator. (eg, SQL mode
   * marks +, -, etc with this token type)
   */
  public static final int OPERATOR    = 10;

  /**
   * Identifier1 category id. This can be used to mark identifers of interest,
   * so the user can easily spot them.
   */
  public static final int IDENTIFIER1 = 11;

  /**
   * Identifier1 category id. This can be used to mark identifers of interest,
   * so the user can easily spot them.
   */
  public static final int IDENTIFIER2 = 12;

  /**
   * Documentation category id. Used to mark special documentation (eg Javadoc
   * comments, Python's autodoc)
   */
  public static final int DOC         = 13;

  public static final int MaximumId   = DOC;

}