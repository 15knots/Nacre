/* $Header$ */

// Copyright © 2004 Martin Weber

package swing.text.highlight;

import swing.text.highlight.categoriser.C_Categoriser;
import swing.text.highlight.categoriser.Categoriser;


/**
 * This kit supports a handling of editing C text content. It supports syntax
 * highlighting and produces the lexical structure of the document as best it
 * can.
 * 
 * @author Martin Weber
 * @version $Revision$
 */
public class CHighlightingKit extends HighlightingKit
{

  public CHighlightingKit()
  {
    super();
  }

  /**
   * Gets the MIME type of the data that this kit represents support for. This
   * kit supports the type <code>text/x-c-src</code>.
   */
  public String getContentType()
  {
    // TODO check whether this MIME type is apprpriate
    return "text/x-c-src";
  }

  /**
   * Creates a Categoriser used for highlighting text of this document or
   * <code>null</code>.
   */
  protected Categoriser createCategoriser()
  {
    return new C_Categoriser();
  }

}

