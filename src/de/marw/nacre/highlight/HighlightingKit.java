/* $Id$ */

// Copyright © 2004 Martin Weber

package de.marw.javax.swing.text.highlight;

import javax.swing.text.DefaultEditorKit;
import javax.swing.text.Document;
import javax.swing.text.ViewFactory;

import de.marw.javax.swing.text.highlight.categoriser.Categoriser;



/**
 * This kit supports a fairly minimal handling of editing a programming language
 * text content. It supports syntax highlighting and produces the lexical
 * structure of the document as best it can. 
 * 
 * @author Martin Weber
 */
public abstract class HighlightingKit extends DefaultEditorKit
{

  /**
   * Default constructor used by subclasses.
   */
  protected HighlightingKit()
  {
  }

  /**
   * Creates the Categoriser used for highlighting text of this document or
   * <code>null</code> if no highlighting is to be done.
   */
  protected abstract Categoriser createCategoriser();

  public HighlightingContext getStylePreferences()
  {
    if (preferences == null) {
      preferences = new HighlightingContext( createCategoriser());
    }
    return preferences;
  }

  public void setStylePreferences( HighlightingContext prefs)
  {
    preferences = prefs;
  }

  // --- EditorKit methods -------------------------

  /**
   * Creates an uninitialized text storage model that is appropriate for this
   * type of editor.
   * 
   * @return the model
   */
  public Document createDefaultDocument()
  {
    return new HighlightedDocument();
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
    return getStylePreferences();
  }

  /**
   * Create a copy of the editor kit. This allows an implementation to serve as
   * a prototype for others, so that they can be quickly created.
   */
  //  public Object clone()
  //  {
  //    HighlightingKit kit = new HighlightingKit();
  //    kit.preferences = preferences;
  //    return kit;
  //  }
  protected HighlightingContext preferences;

}