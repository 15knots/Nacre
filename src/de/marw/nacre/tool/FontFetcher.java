// $Id$
/*
 * Copyright 2006 by Martin Weber
 */

package de.marw.nacre.tool;

import java.awt.Font;
import java.awt.GraphicsEnvironment;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import javax.swing.JComponent;
import javax.swing.JEditorPane;

import de.marw.nacre.highlight.HighlightingKit;


/**
 * Utility class to determine the fonts suitable for highlighting of programming
 * language code.
 * 
 * @author weber
 */
public class FontFetcher
{

  /**
   * Returns a set containing the names of all font families in the local
   * <code>GraphicsEnvironment</code> that can be used with a
   * {@link HighlightingKit}.
   * <p>
   * Highlighting programming language's code requires a font that has the same
   * width, regardless whether the font is rendered PLAIN, BOLD or ITALIC.
   * </p>
   * 
   * @return a Set of <code>String</code>s containing font family names
   *         localized for the default <code>Locale</code>, or a suitable
   *         alternative name if no name exists for the specified locale.
   * @see GraphicsEnvironment#getAvailableFontFamilyNames()
   * @since 1.2
   */
  public static SortedSet<String> getSuitableFonts()
  {
    GraphicsEnvironment env= GraphicsEnvironment.getLocalGraphicsEnvironment();

    SortedSet<String> names= new TreeSet<String>();
    JComponent dummy= new JEditorPane();
    final String pattern= "TaTeTiTuToIWaWeWiWoWuW|M_";
    for (String name : env.getAvailableFontFamilyNames()) {
      Font plainFont= new Font( name, Font.PLAIN, 100);

      int plainW= dummy.getFontMetrics( plainFont).stringWidth( pattern);
      int boldW=
          dummy.getFontMetrics( plainFont.deriveFont( Font.BOLD)).stringWidth(
              pattern);
      int italW=
          dummy.getFontMetrics( plainFont.deriveFont( Font.ITALIC))
              .stringWidth( pattern);
      int boldItalW=
          dummy.getFontMetrics( plainFont.deriveFont( Font.BOLD | Font.ITALIC))
              .stringWidth( pattern);
      // System.out.println( "Font: plain=" + plainW + ", bold=" + boldW
      // + ", italic=" + italW +", bold+italic=" + boldItalW + "\t Name="
      // +
      // font.getFamily());

      /*
       * highlighting requires a font that has the same width, regardless
       * whether the font is rendered PLAIN, BOLD or ITALIC.
       */
      if (plainW == boldW && plainW == italW && plainW == boldItalW) {
        names.add( name);
      }
    }
    return names;
  }

  /**
   * @param args unused
   */
  public static void main( String[] args)
  {
    System.out.println( "## Family names of fonts available for use with "
        + "HighlightingKit(s) on this system");
    final Set<String> fonts= getSuitableFonts();
    for (String font : fonts) {
      System.out.println( font);
    }
    System.out.println( "\n## Found " + fonts.size() + " fonts.");
  }
}
