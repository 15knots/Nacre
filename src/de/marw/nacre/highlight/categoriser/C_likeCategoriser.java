// $Id$
/*
 * Copyright 2004 by Martin Weber
 */

package de.marw.javax.swing.text.highlight.categoriser;

import java.text.CharacterIterator;


/**
 * A source code scanner and token categoriser for programming languages which
 * have been derived from the C programming language. This includes basic
 * support for Java, C++ and others.
 * 
 * @author Martin Weber
 */
public abstract class C_likeCategoriser extends AbstractCategoriser
{

  /**
   * This is an abstract class that cannot be instantiated directly.
   */
  protected C_likeCategoriser() {
    super();
  }

  // /////////////////////////////////////////////////////////
  // categoriser helper methods
  // /////////////////////////////////////////////////////////

  /**
   * Matches an Identifer or a keyword.
   * 
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected int matchWord()
  {
    int len = 0;
    char c = LA( len);
    if (Character.isJavaIdentifierStart( c)) {
      c = LA( ++len);
      while (Character.isJavaIdentifierPart( c)) {
        c = LA( ++len);
      }
    }
    return len;
  }

  /**
   * Consumes a character literal until end of line.
   */
  protected void consumeCharConst()
  {
    char c = input.next();
    for (; c != CharacterIterator.DONE; c = input.next()) {
      switch (c) {
        case '\\':
          input.next();
        break;
        case '\'':
        case '\n':
          input.next();
          return;
      }
    }
  }

  /**
   * Consumes a string literal until end of line.
   */
  protected void consumeString()
  {
    char c = input.next();
    for (; c != CharacterIterator.DONE; c = input.next()) {
      switch (c) {
        case '\\':
          input.next();
        break;
        case '\"':
        case '\n':
          input.next();
          return;
      }
    }
  }

  /**
   * Consumes a multiline comment which is started by '/*''.
   */
  void consumeMLComment()
  {

    input.next(); // consume '/'
    char c = input.next(); // consume '*'
    for (; c != CharacterIterator.DONE; c = input.next()) {
      switch (c) {
        case '*':
          if (LA( 1) == '/') {
            consumeChars( 2); // consume '*/'
            return;
          }
        break;
      }
    }
  }

  /**
   * Consumes a multiline comment which is started by '//''.
   */
  protected void consumeEOLComment()
  {

    input.next(); // consume '/'
    char c = input.next(); // consume '/'
    for (; c != CharacterIterator.DONE; c = input.next()) {
      switch (c) {
        case '\n':
          input.next(); // consume '\n'
          return;
      }
    }
    return;
  }

  /**
   * Matches white space until end of line.
   * 
   * @return the length of the matching text or <code>0</code> if no match was
   *         found.
   */
  protected int matchWhitespaceNoNL()
  {
    int len = 0;
    char c = LA( len);
    // match WS until end of line..
    while (Character.isWhitespace( c) && c != '\n') {
      c = LA( ++len);
    }
    return len;
  }

}