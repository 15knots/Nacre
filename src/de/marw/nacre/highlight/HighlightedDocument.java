/* $Header$ */

// Copyright © 2004 Martin Weber

package swing.text.highlight;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.GapContent;
import javax.swing.text.PlainDocument;


/**
 * A document to represent text in the form of the a programming language. This
 * is quite primitive in that it simply provides support for lexically analyzing
 * the text.
 * 
 * @author Martin Weber
 */
public class HighlightedDocument extends PlainDocument
{

  /**
   * Constructs a plain text document. A default model using
   * <code>GapContent</code> is constructed and set.
   */
  public HighlightedDocument()
  {
    this( new GapContent());
  }

  /**
   * Constructs a plain text document. A default root element is created, and
   * the tab size set to 8.
   * 
   * @param c
   *        the container for the content
   */
  public HighlightedDocument( Content c)
  {
    super( c);
  }

  /**
   * @see javax.swing.text.Document#insertString(int, java.lang.String,
   *      javax.swing.text.AttributeSet)
   */
  public void insertString( int offs, String str, AttributeSet a)
      throws BadLocationException
  {
    // TODO Auto-generated method stub
    super.insertString( offs, str, a);
  }

}

