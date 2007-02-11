/* $Id$ */

// Copyright 2005-2007 Martin Weber
import java.awt.Color;
import java.awt.Font;
import java.io.File;
import java.io.FileReader;

import javax.swing.JEditorPane;

import de.marw.nacre.editorkits.C_HighlightingKit;
import de.marw.nacre.editorkits.JavaHighlightingKit;
import de.marw.nacre.highlight.CategoryStyles;
import de.marw.nacre.highlight.HighlightingKit;
import de.marw.nacre.highlight.categoriser.Category;


/**
 * This demo shows how to prepare a <code>JEditorPane</code> to support
 * highlighting of several programming languages (in this case: <em>Java</em>
 * and <em>C</em>). The selection of the highlighting style is deferred until
 * the content-type of the text to display is known is chosen. <br>
 * This demo selects the content-type depending on the input file's filename
 * extension. <br>
 * Run with <code>
 * <pre>
 *          Usage:
 *            java HighlightKitTest filename
 * </pre>
 * </code>
 */
public class HighlightKitDemo extends DemoFrame
{

  @Override
  protected String getDemoName()
  {
    return "Several Languages Demo";
  }

  // //////////////////////////////////////////////////////////////////
  // static methods
  // //////////////////////////////////////////////////////////////////
  /**
   */
  public static void main( String[] args)
  {
    if (args.length != 1) {
      System.err.println( "need Java or C filename argument");
      System.exit( 1);
    }

    // GUI setup
    HighlightKitDemo frame= new HighlightKitDemo();
    JEditorPane editor= frame.getEditor();
    configureEditor( editor);

    try {
      // read file
      File file= new File( args[0]);
      editor.read( new FileReader( file), file);

      // select highlighting depending on filename extension
      if (args[0].endsWith( ".c")) {
        editor.setContentType( "text/x-c-src");
      }
      else if (args[0].endsWith( ".java")) {
        editor.setContentType( "text/x-java");
      }

      frame.pack();

      frame.setVisible( true);
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }

  /**
   * Sets a font suitable for syntax highlighting. <br>
   * Installs editor kits for <em>Java</em> and <em>C</em> syntax
   * highlighting.
   * <p>
   * NOTE: Highlighting with Nacre requires a font that has the same width,
   * regardless whether the font is rendered PLAIN, BOLD or ITALIC. Use the
   * {@link de.marw.nacre.tool.FontFetcher FontFetcher} tool to find the
   * appropriate fonts for your system.
   * </p>
   * 
   * @param editor
   */
  protected static void configureEditor( JEditorPane editor)
  {
    // add C highlighting
    HighlightingKit kit= new C_HighlightingKit();
    editor.setEditorKitForContentType( kit.getContentType(), kit);
    // add more EditorKits to support different content types here...
    // add Java highlighting
    kit= new JavaHighlightingKit();
    editor.setEditorKitForContentType( kit.getContentType(), kit);

    // highlighting requires a font that has the same width, regardless
    // whether the font is rendered PLAIN, BOLD or ITALIC.

    editor.setFont( new Font( "Courier New", Font.PLAIN, 13));
  }

}