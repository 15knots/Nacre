/* $Id$ */

// Copyright © 2004 Martin Weber

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.io.FileReader;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;
import javax.swing.text.StyleContext;

import de.marw.javax.swing.text.highlight.CHighlightingKit;
import de.marw.javax.swing.text.highlight.Category;
import de.marw.javax.swing.text.highlight.HighlightingContext;
import de.marw.javax.swing.text.highlight.HighlightingKit;



/**
 * Simple wrapper around JEditorPane to browse java text using the JavaEditorKit
 * plug-in. java HighlightKitTest filename
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
      //      GraphicsEnvironment env =
      //        GraphicsEnvironment.getLocalGraphicsEnvironment();
      //      String names[]=env.getAvailableFontFamilyNames();
      //      for (int i = 0; i < names.length; i++ ) {
      //        System.out.println("- "+i+": "+names[i]);
      //      }

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
      //      editor.setFont( new Font( "Luxi Serif", Font.ITALIC, 30));
      //editor.setEditable( false);

      // PENDING(prinz) This should have a customizer and
      // be serialized. This is a bogus initialization.
      HighlightingContext styles = kit.getStylePreferences();
      Style root = styles.getStyle( StyleContext.DEFAULT_STYLE);
      StyleConstants.setFontFamily( root, "Monospaced");
      //      StyleConstants.setFontSize( root, 30);
      //      editor.setFont( new Font( "Monospaced", Font.PLAIN, 30));

      Style s;
      s = styles.getStyleForCategory( Category.COMMENT_1);
      StyleConstants.setForeground( s, new Color( 0, 128, 0));
      s = styles.getStyleForCategory( Category.COMMENT_2);
      StyleConstants.setForeground( s, new Color( 0, 153, 153));

      s = styles.getStyleForCategory( Category.IDENTIFIER_1);
      StyleConstants.setForeground( s, Color.cyan.darker());
      s = styles.getStyleForCategory( Category.STRINGVAL);
      StyleConstants.setForeground( s, new Color( 102, 153, 102));
      StyleConstants.setItalic( s, true);
      StyleConstants.setStrikeThrough( s, true);
      StyleConstants.setUnderline( s, true);

      s = styles.getStyleForCategory( Category.NUMERICVAL);
      StyleConstants.setForeground( s, new Color( 255, 0, 70));

      s = styles.getStyleForCategory( Category.OPERATOR);
      StyleConstants.setForeground( s, Color.CYAN);
      //      StyleConstants.setUnderline(s, true);

      Color keyword = new Color( 102, 102, 255);
      s = styles.getStyleForCategory( Category.KEYWORD_STATEMENT);
      StyleConstants.setForeground( s, keyword);
      //      StyleConstants.setBold( s, true);
      s = styles.getStyleForCategory( Category.KEYWORD);
      StyleConstants.setForeground( s, keyword);
      s = styles.getStyleForCategory( Category.KEYWORD_TYPE);
      //      StyleConstants.setStrikeThrough(s, true);
      StyleConstants.setForeground( s, keyword.brighter());
      StyleConstants.setBold( s, true);

      s = styles.getStyleForCategory( Category.PREDEFVAL);
      StyleConstants.setForeground( s, keyword.darker());
      StyleConstants.setBold( s, true);

      File file = new File( args[0]);
      editor.read( new FileReader( file), file);
      JScrollPane scroller = new JScrollPane();
      scroller.setViewportView( editor);

      JFrame f = new JFrame( "JavaEditorKit: " + args[0]);
      f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
      f.getContentPane().setLayout( new BorderLayout());
      f.getContentPane().add( "Center", scroller);

      f.pack();
      f.setSize( 200, 300);
      if (TEST_MULTILINE_TOKEN_PROOFNESS) {
        // caret in letzte Zeile (test, ob repaint() immer in erster Zeile
        // anfängt)
        editor.setCaretPosition( editor.getDocument().getLength());
        scroller.getViewport().setViewPosition( new Point( 10, 962));
      }

      /*
       * NOTE: scrollRectToVisible() funktioniert nur, wenn der Frame visible
       * ist. // caret in letzte Zeile (test, ob repaint() immer in erster Zeile
       * anfängt) Rectangle rect=editor.getUI().modelToView(editor,
       * editor.getDocument().getLength()); editor.scrollRectToVisible(rect );
       */
      f.setVisible( true);
    }
    catch (Throwable e) {
      e.printStackTrace();
      System.exit( 1);
    }
  }

}