/*
 * @(#)HighlightKitTest.java 1.2 99/05/27 Copyright (c) 1998 Sun Microsystems,
 * Inc. All Rights Reserved. This software is the confidential and proprietary
 * information of Sun Microsystems, Inc. ("Confidential Information"). You shall
 * not disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into with Sun.
 * SUN MAKES NO REPRESENTATIONS OR WARRANTIES ABOUT THE SUITABILITY OF THE
 * SOFTWARE, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE, OR
 * NON-INFRINGEMENT. SUN SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY
 * LICENSEE AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES.
 */

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.FileReader;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import swing.text.highlight.CHighlightingKit;
import swing.text.highlight.Category;
import swing.text.highlight.HighlightingContext;
import swing.text.highlight.HighlightingKit;


/**
 * Simple wrapper around JEditorPane to browse java text using the JavaEditorKit
 * plug-in. java HighlightKitTest filename
 */
public class HighlightKitTest
{

  public static void main( String[] args)
  {
    if (args.length != 1) {
      System.err.println( "need filename argument");
      System.exit( 1);
    }
    try {
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
      //     editor.setFont( new Font( "Monospaced", 0, 12));
      //      editor.setEditable( true);

      // PENDING(prinz) This should have a customizer and
      // be serialized. This is a bogus initialization.
      HighlightingContext styles = kit.getStylePreferences();
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
      JViewport vp = scroller.getViewport();
      vp.add( editor);

      JFrame f = new JFrame( "JavaEditorKit: " + args[0]);
      f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
      f.getContentPane().setLayout( new BorderLayout());
      f.getContentPane().add( "Center", scroller);
      f.pack();
      f.setSize( 200, 751);
      f.setVisible( true);
    }
    catch (Throwable e) {
      e.printStackTrace();
      System.exit( 1);
    }
  }

}