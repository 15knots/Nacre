/* $Id$ */

// Copyright 2004-2006 Martin Weber
import java.awt.Color;
import java.awt.Font;
import java.awt.HeadlessException;
import java.io.File;
import java.io.FileReader;

import javax.swing.JEditorPane;

import de.marw.nacre.editorkits.C_HighlightingKit;
import de.marw.nacre.editorkits.JavaHighlightingKit;
import de.marw.nacre.highlight.CategoryStyles;
import de.marw.nacre.highlight.HighlightingKit;
import de.marw.nacre.highlight.categoriser.Category;


/**
 * This demo shows how to set up a <code>JEditorPane</code> to support both
 * highlighting of <em>Java</em> and <em>C</em> program code. The actual
 * style for highlighting text is chosen depending of the input file's filename
 * extension. <br>
 * Run with
 * 
 * <pre>
 *                Usage:
 *                     java HighlightKitTest filename
 * </pre>
 */
public class HighlightKitDemo extends DemoFrame
{

  /**
   */
  public HighlightKitDemo() throws HeadlessException
  {
    // install an editor kit that does syntax highlighting
    configureEditor( getEditor());
  }

  @Override
  protected String getDemoName()
  {
    return "Java or C Demo";
  }

  /**
   * Installs editor kits for C and Java for syntax highlighting. <br>
   * Sets a font suitable for syntax highlighting. <br>
   * Customises the style for rendering the <code>Category.COMMENT_2</code>.
   * 
   * @param editor
   */
  protected void configureEditor( JEditorPane editor)
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
    
    // editor.setFont( new Font( "Courier", Font.PLAIN, 12));
    // editor.setFont( new Font( "Courier 10 Pitch", Font.PLAIN, 12));
    editor.setFont( new Font( "Courier New", Font.PLAIN, 13));
    // # editor.setFont( new Font( "Cumberland AMT", Font.PLAIN, 13));
    // editor.setFont( new Font( "DialogInput", Font.PLAIN, 12));
    // # editor.setFont( new Font( "Lucida Sans Typewriter", Font.PLAIN, 12));
    // editor.setFont( new Font( "Luxi Mono", Font.PLAIN, 12));
    // editor.setFont( new Font( "Monospaced", Font.PLAIN, 12));
    // editor.setFont( new Font( "Nimbus Mono L", Font.PLAIN, 14));
    // editor.setFont( new Font( "SUSE Sans Mono", Font.PLAIN, 12));
    // editor.setFont( new Font( "SansSerif", Font.PLAIN, 13));

    // select C highlighting
    // editor.setContentType( "text/x-c-src");
    // editor.setContentType( "text/x-java");

    // customise colour and font style of hightlighting
    kit=
        (HighlightingKit) editor.getEditorKitForContentType( editor
            .getContentType());
    CategoryStyles styles= kit.getCategoryStyles();
    styles.setColor( Category.COMMENT_2, Color.YELLOW);
  }

  /**
   */
  public static void main( String[] args)
  {
    if (args.length != 1) {
      System.err.println( "need filename argument");
      System.exit( 1);
    }

    // GUI setup
    HighlightKitDemo frame= new HighlightKitDemo();
    JEditorPane editor= frame.getEditor();
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
      frame.setSize( 211, 701);

      frame.setVisible( true);
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }

}