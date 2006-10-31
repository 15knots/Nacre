/* $Id$ */

// Copyright � 2004 Martin Weber

package de.marw.javax.swing.text.highlight.categoriser;

import javax.swing.text.Document;
import javax.swing.text.Segment;


/**
 * A source code scanner and token categoriser.
 * 
 * @author Martin Weber
 */
public interface Categoriser
{

  /**
   * (Re-)Initialises the categoriser to scan the specified <code>Segment</code>.
   * This gets called when the View is going to be rendered. <br>
   * 
   * @param lexerInput
   *        the run of text to categorise.
   */
  public void openInput( Segment lexerInput);

  /**
   * This gets called when the View is rendered. The start position of the
   * returned token is expected to be relative to the start of the <strong>input
   * segment </strong> set with <code>{@link #openInput(Segment)}</code>
   * earlier.
   * 
   * @param doc
   * @param tokenBuf
   *        The buffer where the token is returned.
   * @see #openInput(Segment)
   */
  public void nextToken( Document doc, Token tokenBuf);

  /**
   * Notifies the categoriser of the end of the current scanninng process.
   * 
   * @see #openInput(Segment)
   */
  public void closeInput();

}