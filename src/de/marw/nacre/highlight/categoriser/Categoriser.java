// $Header$

//Copyright © 2004 Razorcat Development GmbH

package swing.text.highlight.categoriser;

import javax.swing.text.Element;
import javax.swing.text.Segment;

import swing.text.highlight.HighlightedDocument;

/**
 * A source code scanner and token categoriser. (formerly TokenMarker)
 * 
 * @author Martin Weber
 */
public interface Categoriser
{

  /**
   * @param input
   */
  public void setInput( Segment input);

  /**
   * This gets called when the View is rendered.
   * 
   * @param doc
   * @param tokenBuf
   *        The buffer where the token is returned or <code>null</code> if a
   *        new buffer should be allocated.
   * @return the categorized token.
   */
  public Token nextToken( HighlightedDocument doc, Token tokenBuf);

  /**
   * Fetch a reasonable location to start scanning given the desired start
   * location. This allows for adjustments needed to accommodate multiline
   * comments.
   * 
   * @param doc
   *        The document holding the text.
   * @param pos
   * @return adjusted start position which is greater or equal than zero.
   */
  public int getAdjustedStart( HighlightedDocument doc, int pos);

  /**
   * @param elem
   */
  public void insertUpdate( Element line);

  /**
   * @param elem
   */
  public void removeUpdate( Element line);

}