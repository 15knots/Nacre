// $Id$
/*
 * Copyright 2005 by Martin Weber
 */

package de.marw.javax.swing.text.editorkits;

/**
 * A source code scanner and token categoriser for the Cplusplus programming
 * language.
 * 
 * @author Martin Weber
 */
public class Cpp_Categoriser extends C_Categoriser
{

  /**
   * Type keywords.
   */
  private static final String[] kwType = { "bool", "char", "class", "double",
      "enum", "float", "int", "long", "short", "signed", "struct", "typedef",
      "typename", "union", "unsigned", "void", "virtual", "wchar_t", "auto",
      "const", "mutable", "extern", "register", "static", "volatile", "friend",
      "operator", "inline", "explicit", "export", "protected", "private",
      "public", "far", "huge", "near", "pascal", "template" };

  /**
   * statement keywords.
   */
  private static final String[] kwStmt = { "asm", "break", "case", "catch",
      "continue", "default", "do", "else", "for", "goto", "if", "namespace",
      "return", "switch", "while", "throw", "try", "using" };

  /**
   * operator keywords.
   */
  private static final String[] kwOperator = { "and", "and_eq", "bitand",
      "bitor", "compl", "const_cast", "delete", "dynamic_cast", "new", "not",
      "not_eq", "or", "or_eq", "reinterpret_cast", "sizeof", "static_cast",
      "typeid", "xor", "xor_eq" };

  /**
   * predefined constants value keywords.
   */
  private static final String[] kwPredefVal = { "__DATE__", "__FILE__",
      "__LINE__", "__TIME__", "false", "true", "this" };

  /**
   * 
   */
  public Cpp_Categoriser() {
    super();
  }

  /**
   * Checks whether a subregion in the <code>input</code> starting at the
   * current scanner input position is a keyword used in statements.
   * 
   * @see de.marw.javax.swing.text.highlight.categoriser.Category#KEYWORD_STATEMENT
   * @param length
   *        the length of the region that must match.
   * @return <code>true</code> if the subregion is one of the keywords,
   *         otherwise <code>false</code>.
   */
  protected boolean isKW_Statement( int length)
  {
    return matchOneOfStrings( length, kwStmt);
  }

  /**
   * Checks whether a subregion in the <code>input</code> starting at the
   * current scanner input position is a keyword used for types.
   * 
   * @see de.marw.javax.swing.text.highlight.categoriser.Category#KEYWORD_TYPE
   * @param length
   *        the length of the region that must match.
   * @return <code>true</code> if the subregion is one of the keywords,
   *         otherwise <code>false</code>.
   */
  protected boolean isKW_Type( int length)
  {
    return matchOneOfStrings( length, kwType);
  }

  /**
   * Checks whether a subregion in the <code>input</code> starting at the
   * current scanner input position is a keyword used as operator.
   * 
   * @see de.marw.javax.swing.text.highlight.categoriser.Category#KEYWORD_OPERATOR
   * @param length
   *        the length of the region that must match.
   * @return <code>true</code> if the subregion is one of the keywords,
   *         otherwise <code>false</code>.
   */
  protected boolean isKW_Operator( int length)
  {
    return matchOneOfStrings( length, kwOperator);
  }

  /**
   * Checks whether a subregion in the <code>input</code> starting at the
   * current scanner input position is a keyword used for predefined value
   * literals.
   * 
   * @see de.marw.javax.swing.text.highlight.categoriser.Category#PREDEFVAL
   * @param length
   *        the length of the region that must match.
   * @return <code>true</code> if the subregion is one of the keywords,
   *         otherwise <code>false</code>.
   */
  protected boolean isKW_PredefVal( int length)
  {
    return matchOneOfStrings( length, kwPredefVal);
  }

}
