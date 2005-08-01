/*
 * $Header:
 * /home/weber/cvsRepos/highlighting/src/de/marw/javax/swing/text/highlight/Category.java,v
 * 1.13 2005/01/22 18:00:33 weber Exp $
 */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight;



/**
 * Represents a lexical token category used to classify a piece of text for
 * syntax highlighting. Note that the identiers of the enumerated constants only
 * serve as a hint to programmers, the actual meaning of a category depends on
 * the <code>HighlightingKit</code>'s implementation.
 * 
 * @see de.marw.javax.swing.text.highlight.categoriser.Token
 */
public enum Category {

  /**
   * Comment 1 category. This can be used to mark a comment. (eg, Java and C
   * modes use this to mark a multiple line comment.)
   */
  COMMENT_1,

  /**
   * Comment 2 category. This can be used to mark a comment.(eg, Java and C++
   * modes use this to mark a single line comment.)
   */
  COMMENT_2,

  /**
   * Literal string category. This can be used to mark a string literal (eg,
   * Java and C modes use this to mark "..." literals.)
   */
  STRINGVAL,

  /**
   * Number category. Used to mark literal numbers.
   */
  NUMERICVAL,

  /**
   * Literal predefined value category. This can be used to mark an object
   * literal (eg, Java mode uses this to mark <code>true</code>,
   * <code>false</code>,<code>null</code>.)
   */
  PREDEFVAL,

  /**
   * Alternative keyword category. This can be used to mark a keyword. (C mode
   * uses this for preprocessor directives.)
   */
  KEYWORD,

  /**
   * Statement Keyword category. This can be used to mark a keyword for
   * statement. (eg, for, if, while)
   */
  KEYWORD_STATEMENT,

  /**
   * Type keyword category. This can be used to mark a keyword for data types.
   */
  KEYWORD_TYPE,

  /**
   * Operator keyword category. This can be used to mark an operator. (eg, C++
   * mode marks 'new' and 'sizeof' with this token type)
   */
  KEYWORD_OPERATOR,

  /**
   * Operator category. This can be used to mark an operator other than those
   * marked as <code>KEYWORD_OPERATOR</code>. (eg, Java mode marks +, -, etc
   * with this token type)
   */
  OPERATOR,

  /**
   * Label category. This can be used to mark labels.
   */
  LABEL,

  /**
   * Documentation category. Used to mark special documentation (eg Javadoc
   * comments, Python's autodoc)
   */
  DOC,

  /**
   * Custom identifier 1 category. This can be used to mark identifiers of
   * interest, so the user can easily spot them.
   */
  IDENTIFIER_1,

  /**
   * Custom identifier 2 category. This can be used to mark identifiers of
   * interest, so the user can easily spot them.
   */
  IDENTIFIER_2
  ;
}