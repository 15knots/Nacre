/* $Id$ */

// Copyright 2004-2006 Martin Weber
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.FileReader;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.Document;

import de.marw.nacre.editorkits.C_HighlightingKit;
import de.marw.nacre.editorkits.JavaHighlightingKit;
import de.marw.nacre.highlight.CategoryStyles;
import de.marw.nacre.highlight.HighlightingKit;
import de.marw.nacre.highlight.categoriser.Category;


/**
 * Simple wrapper around JEditorPane to browse program code using the
 * HighlightingKit plug-in. Run with
 * 
 * <pre>
 *   Usage:
 *        java HighlightKitTest filename
 * </pre>
 */
public class HighlightKitDemo
{

  /**
   * This is used to test for appropriate handling of tokens that span multiple
   * lines. <code>true</code>, if the first line of text to categorise is not
   * at offset zero.
   */
  private static final boolean TEST_MULTILINE_TOKEN_PROOFNESS= false;

  public static void main( String[] args)
  {
    if (args.length != 1) {
      System.err.println( "need filename argument");
      System.exit( 1);
    }
    // create an editor pane
    JEditorPane editor= new JEditorPane();
    // install an editor kit that does syntax highlighting
    configureEditor( editor);

    try {
      // read file
      File file= new File( args[0]);
      // select highlighting
      if (args[0].endsWith( ".c")) {
        editor.setContentType( "text/x-c-src");
      }
      else if (args[0].endsWith( ".java")) {
        editor.setContentType( "text/x-java");
      }
      editor.read( new FileReader( file), file);
      // GUI setup
      JScrollPane scroller= new JScrollPane();
      scroller.setViewportView( editor);

      Document doc= editor.getDocument();
      Object docDesc= doc.getProperty( Document.StreamDescriptionProperty);
      JFrame f=
          new JFrame( "HighlightingKit: " + docDesc.toString() + " ("
              + editor.getContentType() + ")");
      f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
      f.getContentPane().setLayout( new BorderLayout());
      f.getContentPane().add( "Center", scroller);

      f.pack();
      f.setSize( 211, 701);
      if (TEST_MULTILINE_TOKEN_PROOFNESS) {
        // caret in letzte Zeile (test, ob repaint() immer in erster Zeile
        // anf�ngt)
        f.setSize( 211, 301);
        editor.setCaretPosition( editor.getDocument().getLength() - 1);
        scroller.getViewport().setViewPosition( new Point( 0, 9999));
      }

      f.setVisible( true);
    }
    catch (Throwable e) {
      e.printStackTrace();
    }
  }

  /**
   * @param editor
   */
  protected static void configureEditor( JEditorPane editor)
  {
    // editor.setBackground( Color.white);
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
    editor.setContentType( "text/x-c-src");
    // editor.setContentType( "text/x-java");

    // customise colour and font style of hightlighting
    kit=
        (HighlightingKit) editor.getEditorKitForContentType( editor
            .getContentType());
    CategoryStyles styles= kit.getCategoryStyles();
    styles.setColor( Category.COMMENT_2, Color.YELLOW);
  }

}