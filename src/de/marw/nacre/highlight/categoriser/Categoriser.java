// $Header:
// /home/weber/cvsRepos/highlighting/swing/text/highlight/categoriser/Categoriser.java,v
// 1.1 2004/09/22 19:05:12 weber Exp $

//Copyright © 2004 Razorcat Development GmbH

package swing.text.highlight.categoriser;

import javax.swing.text.Element;
import javax.swing.text.Segment;

import swing.text.highlight.HighlightedDocument;


/**
 * A source code scanner and token categoriser.
 * 
 * @author Martin Weber
 */
public interface Categoriser
{

  /**
   * <br>
   * bedeutet implizit, dass ein Scanabschnitt beginnt.
   * 
   * @param input
   */
  public void setInput( Segment input);

  /**
   * Notifies the categoriser of the end of the current scanninng process.
   * 
   * @see #setInput(Segment)
   */
  public void closeInput();

  /**
   * This gets called when the View is rendered. The start position of the
   * returned token is expected to be relative to the start of the <strong>input
   * segment </strong>.
   * 
   * @param doc
   * @param tokenBuf
   *          The buffer where the token is returned or <code>null</code> if a
   *          new buffer should be allocated.
   * @return the categorized token.
   * @see #setInput(Segment)
   */
  public Token nextToken( HighlightedDocument doc, Token tokenBuf);

  /**
   * Fetch a reasonable location to start scanning given the desired start
   * location. This allows for adjustments needed to accommodate multiline
   * comments.
   * 
   * @param doc
   *          The document holding the text.
   * @param offset
   *          The offset relative to the beginning of the document.
   * @return adjusted start position which is greater or equal than zero.
   */
  public int getAdjustedStart( HighlightedDocument doc, int offset);

  /**
   * @param elem
   */
  public void insertUpdate( Element line);

  /**
   * @param elem
   */
  public void removeUpdate( Element line);

}