// $Header:
// /home/weber/cvsRepos/highlighting/swing/text/highlight/categoriser/Categoriser.java,v
// 1.1 2004/09/22 19:05:12 weber Exp $

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
   * (Re-)Initialises the categoriser to scan the specified <code>Segment</code>. This
   * gets called when the View is going to be rendered. <br>
   * bedeutet implizit, dass ein Scanabschnitt beginnt.
   * @param lexerInput
   *          the run of text to categorise.
   */
  public void openInput( Segment lexerInput);

  /**
   * This gets called when the View is rendered. The start position of the
   * returned token is expected to be relative to the start of the <strong>input
   * segment </strong> set with <code>setInput()</code>.
   * 
   * @param doc
   * @param tokenBuf
   *          The buffer where the token is returned or <code>null</code> if a
   *          new buffer should be allocated.
   * @return the categorized token.
   * @see #openInput(int)
   */
  public Token nextToken( HighlightedDocument doc, Token tokenBuf);

  /**
   * Notifies the categoriser of the end of the current scanninng process.
   * 
   * @see #openInput(int)
   */
  public void closeInput();

  /**
   * @param elem
   */
  public void insertUpdate( Element line);

  /**
   * @param elem
   */
  public void removeUpdate( Element line);

}