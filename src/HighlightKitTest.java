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
import java.awt.Font;
import java.io.File;
import java.io.FileReader;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.text.Style;
import javax.swing.text.StyleConstants;

import swing.text.highlight.CHighlightingKit;
import swing.text.highlight.HighlightingContext;
import swing.text.highlight.HighlightingKit;
import swing.text.highlight.JavaHighlightingKit;
import swing.text.highlight.categoriser.CategoryConstants;

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
      JEditorPane editor= new JEditorPane();

      HighlightingKit kit= new CHighlightingKit();
      editor.setEditorKitForContentType( kit.getContentType(), kit);
      kit= new JavaHighlightingKit();
      editor.setEditorKitForContentType( kit.getContentType(), kit);
      // add more EditorKits to support different content types here...

      // 
//      editor.setContentType( "text/x-c-src");
      editor.setContentType( "text/x-java");
      editor.setBackground( Color.white);
      editor.setFont( new Font( "Courier", 0, 12));
      editor.setEditable( true);

      // PENDING(prinz) This should have a customizer and
      // be serialized. This is a bogus initialization.
      HighlightingContext styles= kit.getStylePreferences();
      Style s;
      s= styles.getStyleForCategory( CategoryConstants.COMMENT1);
      StyleConstants.setForeground( s, new Color( 0, 128, 0));
      s= styles.getStyleForCategory( CategoryConstants.COMMENT2);
      StyleConstants.setForeground( s, new Color( 0, 153, 153));
      
      s= styles.getStyleForCategory( CategoryConstants.IDENTIFIER1);
      StyleConstants.setForeground( s, Color.YELLOW.darker());
      s= styles.getStyleForCategory( CategoryConstants.STRINGVAL);
      StyleConstants.setForeground( s, new Color( 102, 153, 102));
//      StyleConstants.setItalic(s, true);
//      StyleConstants.setUnderline(s, true);

      s= styles.getStyleForCategory( CategoryConstants.OPERATOR);
      StyleConstants.setForeground( s, Color.MAGENTA);
//      StyleConstants.setUnderline(s, true);

      Color keyword= new Color( 102, 102, 255);
      s= styles.getStyleForCategory( CategoryConstants.KEYWORD1);
      StyleConstants.setForeground( s, keyword);
//      StyleConstants.setBold( s, true);
      s= styles.getStyleForCategory( CategoryConstants.KEYWORD2);
      StyleConstants.setForeground( s, keyword);
      s= styles.getStyleForCategory( CategoryConstants.TYPE);
//      StyleConstants.setStrikeThrough(s, true);
      StyleConstants.setForeground( s, keyword.brighter());
      StyleConstants.setBold( s, true);
      s= styles.getStyleForCategory( CategoryConstants.PREDEFVAL);
      StyleConstants.setForeground( s, keyword.darker());
      StyleConstants.setBold( s, true);

      File file= new File( args[0]);
      editor.read( new FileReader( file), file);
      JScrollPane scroller= new JScrollPane();
      JViewport vp= scroller.getViewport();
      vp.add( editor);

      JFrame f= new JFrame( "JavaEditorKit: " + args[0]);
      f.getContentPane().setLayout( new BorderLayout());
      f.getContentPane().add( "Center", scroller);
      f.pack();
      f.setSize( 400, 751);
      f.setVisible( true);
    }
    catch (Throwable e) {
      e.printStackTrace();
      System.exit( 1);
    }
  }

}