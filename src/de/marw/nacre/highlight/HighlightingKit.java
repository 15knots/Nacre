/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight;

import java.util.Map;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Element;
import javax.swing.text.PlainView;
import javax.swing.text.View;
import javax.swing.text.ViewFactory;

import de.marw.javax.swing.text.highlight.categoriser.Categoriser;


/**
 * This kit supports a fairly minimal handling of editing a programming language
 * text content. It supports syntax highlighting and produces the lexical
 * structure of the document as best it can.
 * </p>
 * Using a HighlightingKit to obtain syntax highlighting in a JEditorPane ist
 * straitforward: Simply instantiate a subclass of <code>HighlightingKit</code>,
 * then
 * {@link javax.swing.JEditorPane#setEditorKitForContentType(java.lang.String, javax.swing.text.EditorKit)
 * set the editor kit(s) to use} for the given content type. It is possible to
 * set multiple editor kits, each for a different content type. After that,
 * {@link javax.swing.JEditorPane#setContentType(java.lang.String) set the content type}
 * to install the appropriate subclass of <code>HighlightingKit</code>, thus
 * activating the kind of highlighting You want.
 * 
 * <pre>
 * JEditorPane editor = new JEditorPane();
 * // ...
 * HighlightingKit kit1 = new JavaHighlightingKit();
 * HighlightingKit kit2 = new CHighlightingKit();
 * 
 * editor.setEditorKitForContentType( kit1.getContentType(), kit);
 * editor.setEditorKitForContentType( kit2.getContentType(), kit);
 * // add more EditorKits to support different content types here...
 * //...
 * 
 * //  activate the kind of highlighting for Java code
 * editor.setContentType( &quot;text/x-java&quot;);
 * </pre>
 * 
 * </p>
 * 
 * @author Martin Weber
 * @version $Revision$
 * @see javax.swing.text.DefaultEditorKit#getContentType()
 */
public abstract class HighlightingKit extends DefaultEditorKit implements
    ViewFactory
{

  /**
   * the Categoriser used for highlighting text of this document or
   * <code>null</code> if no highlighting is to be done.
   */
  private Categoriser categoriser;

  /**
   * Default constructor used by subclasses.
   */
  protected HighlightingKit() {
  }

  // interface ViewFactory
  public View create( Element elem)
  {
    CategoryStyles styles = getCategoryStyles();
    Categoriser categoriser = getCategoriser();
    return (categoriser != null && styles != null) ? new HiliteView( elem,
        categoriser, styles) : new PlainView( elem);
  }

  /**
   * Returns the set of color and font style informations used to render
   * highlighted text in the document for a specific content type. <br>
   * If a client application changes one of the styles contained in the set
   * returned here, the change will be automatically reflected by any
   * <code>JEditorPane</code> in the application that has a subclass of
   * <code>HighlightingKit</code> installed.
   * <p>
   * NOTE for subclass implementors: The color and font style informations
   * returned here are designed to be shared with any <code>View</code> that
   * gets created by this view factory. Subclasses are expected to return a
   * <strong>static variable</strong> (class variable) here.
   * </p>
   */
  public abstract CategoryStyles getCategoryStyles();

  /**
   * Returns a Map that specifies each category as a <strong>localized </strong>
   * string that can be used as a label. If the returned Map yields a
   * <code>null</code> -string for a Category, the editor kit does not
   * highlight any text as the queried category.
   */
  public abstract Map<Category, String> getCategoryDescriptions();

  /**
   * Creates the Categoriser used for highlighting text of this document or
   * <code>null</code> if no highlighting is to be done.
   */
  protected abstract Categoriser createCategoriser();

  /**
   * Fetches the Categoriser used for highlighting text of this document or
   * <code>null</code> if no highlighting is to be done.
   */
  protected final Categoriser getCategoriser()
  {
    if (categoriser == null) {
      categoriser = createCategoriser();
    }

    return categoriser;
  }

  /**
   * Fetches a factory that is suitable for producing views of any models that
   * are produced by this kit. The default is to have the UI produce the
   * factory, so this method has no implementation.
   * 
   * @return the view factory
   */
  public final ViewFactory getViewFactory()
  {
    return this;
  }

}