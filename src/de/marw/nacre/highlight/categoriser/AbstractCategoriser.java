// $Header:
// /home/weber/cvsRepos/highlighting/swing/text/highlight/categoriser/AbstractCategoriser.java,v
// 1.1 2004/09/22 19:05:12 weber Exp $

// Copyright © 2004 Razorcat Development GmbH

package swing.text.highlight.categoriser;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.Segment;

import swing.text.highlight.HighlightedDocument;


/**
 * 
 */
public abstract class AbstractCategoriser implements Categoriser
{

  /**
   * Key to be used on lines that contain multiline Tokens.
   */
  protected static final Object CategorizerAttribute = new AttributeKey();

  protected Segment             input;

  /**
   */
  public AbstractCategoriser()
  {
    super();
    this.input = new Segment();
  }

  /**
   * Überschrieben, um
   */
  public void setInput( Segment input)
  {
    this.input = input;
  }

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
  public int getAdjustedStart( HighlightedDocument doc, int offset)
  {
    Element rootElement = doc.getDefaultRootElement();
    int lineNum = rootElement.getElementIndex( offset);
    Element line = rootElement.getElement( lineNum);
    AttributeSet a = line.getAttributes();
    // walk backwards until we get an untagged line...
    while (a.isDefined( CategorizerAttribute) && lineNum > 0) {
      lineNum -= 1;
      line = rootElement.getElement( lineNum);
      a = line.getAttributes();
    }
    return line.getStartOffset();
  }

  /** Used as a key on lines that contain multiline Tokens. */
  protected static class AttributeKey
  {

    private AttributeKey()
    {
    }

    public String toString()
    {
      return "multiline token";
    }

  }
}