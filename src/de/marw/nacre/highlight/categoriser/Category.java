/*
 * $Header:
 * /home/weber/cvsRepos/highlighting/src/de/marw/javax/swing/text/highlight/Category.java,v
 * 1.13 2005/01/22 18:00:33 weber Exp $
 */

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
 */
public enum Category {

  /**
   * Comment 1 category. This can be used to mark a comment. (eg, Java and C
   * modes use this to mark a multiple comment.)
   */
  COMMENT_1("Multi-line comment"),

  /**
   * Comment 2 category. This can be used to mark a comment.(eg, Java and C++
   * modes use this to mark a single line comment.)
   */
  COMMENT_2("Single-line comment"),

  /**
   * Literal string category. This can be used to mark a string literal (eg,
   * Java and C modes use this to mark "..." literals.)
   */
  STRINGVAL("Strings"),

  /**
   * Number category. Used to mark literal numbers.
   */
  NUMERICVAL("Numbers"),

  /**
   * Literal predefined value category. This can be used to mark an object
   * literal (eg, Java mode uses this to mark <code>true</code>,
   * <code>false</code>,<code>null</code>.)
   */
  PREDEFVAL("Predefined values"),

  /**
   * Alternative keyword category. This can be used to mark a keyword. (C mode
   * uses this for preprocessor directives.)
   */
  KEYWORD("Keywords"),

  /**
   * Statement Keyword category. This can be used to mark a keyword for
   * statement. (eg, for, if, while)
   */
  KEYWORD_STATEMENT("Statement keywords"),

  /**
   * Type keyword category. This can be used to mark a keyword for data types.
   */
  KEYWORD_TYPE("Type keywords"),

  /**
   * Operator keyword category. This can be used to mark an operator. (eg, C++
   * mode marks 'new' and 'sizeof' with this token type)
   */
  KEYWORD_OPERATOR("Operator keywords"),

  /**
   * Operator category. This can be used to mark an operator other than those
   * marked as <code>KEYWORD_OPERATOR</code>. (eg, Java mode marks +, -, etc
   * with this token type)
   */
  OPERATOR("Operators"),

  /**
   * Label category. This can be used to mark labels.
   */
  LABEL("Labels"),

  /**
   * Documentation category. Used to mark special documentation (eg Javadoc
   * comments, Python's autodoc)
   */
  DOC("Documentation"),

  /**
   * Custom identifier 1 category. This can be used to mark identifiers of
   * interest, so the user can easily spot them.
   */
  IDENTIFIER_1("Custom identifier 1"),

  /**
   * Custom identifier 2 category. This can be used to mark identifiers of
   * interest, so the user can easily spot them.
   */
  IDENTIFIER_2("Custom identifier 2")
  ;

  // ----------------------------------------------------------
  /**
   * String representation of this Category.
   */
  private final String description;

  /**
   * Sole Constructor.
   * 
   * @param description
   *        The String representation of this enum constant, as declared in the
   *        enum declaration.
   */
  private Category( String description) {
    this.description = description;
  }

  /**
   * Specifies the category as a string that can be used as a label. 
   */
  public String getDescription()
  {
    return description;
  }

}