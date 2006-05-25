/* $Id$ */

// Copyright � 2004-2006 Martin Weber

package de.marw.javax.swing.text.highlight;

import java.util.Locale;
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
 * structure of the document as best it can. <br>
 * A <code>HighlightingKit</code> or one of its subclasses can be used with
 * any JTextcomponent.
 * <p>
 * Using a HighlightingKit to have syntax highlighting in a JEditorPane is
 * straitforward:
 * <ul>
 * <li>First instantiate a subclass of <code>HighlightingKit</code> according
 * to the programming language you want to highlight, </li>
 * <li>then
 * {@link javax.swing.JEditorPane#setEditorKitForContentType(java.lang.String, javax.swing.text.EditorKit)
 * register the editor kit(s) to use} for the content type of the programming
 * language.</li>
 * <li>After that,
 * {@link javax.swing.JEditorPane#setContentType(java.lang.String) set the selected content type}
 * to activate highlighting.</li>
 * </ul>
 * </p>
 * <code><pre>
 * JEditorPane editor = new JEditorPane();
 * // ...
 * // add highlighting for Java code
 * HighlightingKit kit = new JavaHighlightingKit();
 * String contentTyoe = kit.getContentType();
 * editor.setEditorKitForContentType( contentType, kit);
 * //...
 * // activate highlighting 
 * editor.setContentType( contentType);
 * </pre></code>
 * <p>
 * It is also possible to register multiple editor kits, each for a different
 * programming language and later to select the style of highlighting on demand.
 * </p>
 * <code><pre>
 * JEditorPane editor = new JEditorPane();
 * // ...
 * HighlightingKit kit1 = new JavaHighlightingKit();
 * HighlightingKit kit2 = new CHighlightingKit();
 * 
 * editor.setEditorKitForContentType( kit1.getContentType(), kit1);
 * editor.setEditorKitForContentType( kit2.getContentType(), kit2);
 * // more EditorKits to support different content types here...
 * //...
 * 
 * // activate highlighting for Java code
 * editor.setContentType( &quot;text/x-java&quot;);
 * </pre></code>
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
   * returned here are designed to be shared with any
   * <code>[@linkplain View}</code> that gets created by this view factory.
   * Subclasses are expected to return a <strong>static variable</strong>
   * (class variable) here.
   * </p>
   */
  public abstract CategoryStyles getCategoryStyles();

  /**
   * Returns a Map that specifies each category as a <strong>localized</strong>
   * string that can be used as a label.
   * <p>
   * If the returned Map does not contain a specific <code>Category</code>,
   * this means the editor kit implementation does not highlight any text of
   * that category.
   * 
   * @param locale
   *        the locale for which the localised strings are desired or
   *        <code>null</code> if the default locale should be used.
   * @see Category
   */
  public abstract Map<Category, String> getCategoryDescriptions( Locale locale);

  /**
   * Creates the Categoriser used for highlighting text of this document or
   * <code>null</code> if no highlighting is to be done.
   */
  protected abstract Categoriser createCategoriser();

  /**
   * Fetches the Categoriser used for highlighting text of this document or
   * <code>null</code> if no highlighting is to be done.
   */
  private Categoriser getCategoriser()
  {
    if (categoriser == null) {
      categoriser = createCategoriser();
    }

    return categoriser;
  }

  /**
   * Fetches a factory that is suitable for producing views of any models that
   * are produced by this kit.
   * 
   * @return the view factory
   */
  public final ViewFactory getViewFactory()
  {
    return this;
  }

}