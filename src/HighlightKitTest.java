/* $Id$ */

// Copyright © 2004 Martin Weber

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.io.File;
import java.io.FileReader;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.Document;

import de.marw.javax.swing.text.highlight.CHighlightingKit;
import de.marw.javax.swing.text.highlight.Category;
import de.marw.javax.swing.text.highlight.CategoryStyles;
import de.marw.javax.swing.text.highlight.HighlightingKit;


/**
 * Simple wrapper around JEditorPane to browse java text using the
 * HighlightingKit plug-in. Run with
 * 
 * <pre>
 * 
 *       java HighlightKitTest filename
 *  
 * </pre>
 */
public class HighlightKitTest
{

  /**
   * This is used to test for appropriate handling of tokens that span multiple
   * lines. <code>true</code>, if the first line of text to categorise is not
   * at offset zero.
   */
  private static final boolean TEST_MULTILINE_TOKEN_PROOFNESS = true;

  public static void main( String[] args)
  {
    if (args.length != 1) {
      System.err.println( "need filename argument");
      System.exit( 1);
    }
    try {
      if (false) {
        GraphicsEnvironment env = GraphicsEnvironment
            .getLocalGraphicsEnvironment();
        String names[] = env.getAvailableFontFamilyNames();
        System.out.println( "## available fonts...");
        for (int i = 0; i < names.length; i++) {
          System.out.println( " - " + i + ": " + names[i]);
        }
      }
      JEditorPane editor = new JEditorPane();

      HighlightingKit kit = new CHighlightingKit();
      editor.setEditorKitForContentType( kit.getContentType(), kit);
      //     kit = new JavaHighlightingKit();
      //     editor.setEditorKitForContentType( kit.getContentType(), kit);
      // add more EditorKits to support different content types here...

      // 
      editor.setContentType( "text/x-c-src");
      //      editor.setContentType( "text/x-java");
      editor.setBackground( Color.white);
      editor.setFont( new Font( "Monospaced", Font.PLAIN, 12));
      editor.setFont( new Font( "Lucida Sans Typewriter", Font.PLAIN, 12));
      //editor.setFont( new Font( "Courier 10 Pitch", Font.PLAIN, 14));
      //editor.setFont( new Font( "Luxi Serif", Font.ITALIC, 30));
      //editor.setEditable( false);

      CategoryStyles styles = kit.getCategoryStyles();
      styles.setColor(Category.COMMENT_2, Color.YELLOW);

      File file = new File( args[0]);
      editor.read( new FileReader( file), file);
      JScrollPane scroller = new JScrollPane();
      scroller.setViewportView( editor);
      
      Document doc=editor.getDocument();
      Object docDesc= doc.getProperty(Document.StreamDescriptionProperty);
      JFrame f = new JFrame( "HighlightingKit: " + docDesc.toString() + " ("
          + editor.getContentType() + ")");
      f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
      f.getContentPane().setLayout( new BorderLayout());
      f.getContentPane().add( "Center", scroller);

      f.pack();
      f.setSize( 211, 701);
      if (TEST_MULTILINE_TOKEN_PROOFNESS) {
        // caret in letzte Zeile (test, ob repaint() immer in erster Zeile
        // anfängt)
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

}