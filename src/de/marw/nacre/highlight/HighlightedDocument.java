//$Id$
/*
 * @(#)HighlightedDocument.java 1.2 99/05/27 Copyright (c) 1998 Sun
 * Microsystems, Inc. All Rights Reserved. This software is the confidential and
 * proprietary information of Sun Microsystems, Inc. ("Confidential
 * Information"). You shall not disclose such Confidential Information and shall
 * use it only in accordance with the terms of the license agreement you entered
 * into with Sun. SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE
 * SUITABILITY OF THE SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE IMPLIED WARRANTIES OF MERCHANTABILITY, FITNESS FOR A
 * PARTICULAR PURPOSE, OR NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY
 * DAMAGES SUFFERED BY LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING
 * THIS SOFTWARE OR ITS DERIVATIVES.
 */

package swing.text.highlight;

import java.util.HashMap;
import java.util.Map;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Element;
import javax.swing.text.GapContent;
import javax.swing.text.PlainDocument;

import swing.text.highlight.categoriser.Categoriser;


/**
 * A document to represent text in the form of the a programming language. This
 * is quite primitive in that it simply provides support for lexically analyzing
 * the text.
 * 
 * @author Timothy Prinzing (version 1.2 05/27/99)
 * @author Martin Weber
 */
public class HighlightedDocument extends PlainDocument
{

  /**
   * the Categoriser used for highlighting text of this document.
   */
  private final Categoriser categoriser;

  /**
   * Constructs a plain text document. A default model using
   * <code>GapContent</code> is constructed and set.
   * 
   * @param categoriser
   *          the Categoriser used for highlighting text of this document or
   *          <code>null</code> if no highlighting is to be done.
   */
  public HighlightedDocument( Categoriser categoriser)
  {
    this( categoriser, new GapContent());
  }

  /**
   * Constructs a plain text document. A default root element is created, and
   * the tab size set to 8.
   * 
   * @param categoriser
   *          the Categoriser used for highlighting text of this document or
   *          <code>null</code> if no highlighting is to be done.
   * @param c
   *          the container for the content
   */
  public HighlightedDocument( Categoriser categoriser, Content c)
  {
    super( c);
    this.categoriser = categoriser;
  }

  /**
   * Returns the Categoriser used for highlighting text of this document or
   * <code>null</code> if no highlighting is to be done.
   */
  public final Categoriser getCategoriser()
  {
    return categoriser;
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

  //////////////////////////// stuff for categorisers
  // TODO Zum Interface machen
  private Map marks = null;

  /**
   * Adds a mark that specifies a line as a position to safely start the
   * scanning.
   * 
   * @param line
   * @param value
   */
  public void putMark( Element line, Object value)
  {
    synchronized (this ) {
      // lazy creation
      if (marks == null) {
        marks = new HashMap();
      }
      marks.put( line, value);
      //      System.out.println( "marks put()=" + marks);
    }
  }

  public Object getMark( Element line)
  {
    synchronized (this ) {
      if (marks == null) {
        return null;
      }
      return marks.get( line);
    }
  }

  public Object removeMark( Element line)
  {
    synchronized (this ) {
      if (marks == null) {
        return null;
      }
      return marks.remove( line);
    }
  }
}

