/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight;

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
 * 
 * @author Martin Weber
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
   * Returns the set of color and font style informations used used to render
   * highlighted text in the document for a specific content type. If a client
   * application changes one of the default styles, the change is automatically
   * reflected by any view.
   * <p>
   * NOTE: The color and font style informations returned here are designed to
   * be shared with any <code>View</code> that gets created by this view
   * factory. Subclasses are expected to return a static variable (
   * <strong>class variable </strong>) here.
   * </p>
   */
  public abstract CategoryStyles getCategoryStyles();

  /**
   * Creates the Categoriser used for highlighting text of this document or
   * <code>null</code> if no highlighting is to be done.
   */
  protected abstract Categoriser createCategoriser();

  /**
   * Fetches the Categoriser used for highlighting text of this document or
   * <code>null</code> if no highlighting is to be done.
   */
  protected Categoriser getCategoriser()
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

  /**
   * Create a copy of the editor kit. This allows an implementation to serve as
   * a prototype for others, so that they can be quickly created.
   */
  // public Object clone()
  // {
  // HighlightingKit kit = new HighlightingKit();
  // kit.preferences = preferences;
  // return kit;
  // }
}