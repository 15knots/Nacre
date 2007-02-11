/* $Id$ */
// Copyright Martin Weber
import java.awt.Color;
import java.awt.Font;

import javax.swing.JEditorPane;

import de.marw.nacre.editorkits.C_HighlightingKit;
import de.marw.nacre.highlight.CategoryStyles;
import de.marw.nacre.highlight.HighlightingKit;
import de.marw.nacre.highlight.categoriser.Category;


/**
 * This demo shows how to customise the colour and font style of highlighting of
 * program code.
 */
public class Highlighting02Demo extends DemoFrame
{

  @Override
  protected String getDemoName()
  {
    return "Customised Styles Highlighting Demo";
  }

  // //////////////////////////////////////////////////////////////////
  // static methods
  // //////////////////////////////////////////////////////////////////

  private static final String sampleText=
    "/* Sample C code.\n*/\n\n" + " #  include <stdio.h>\n\n"
      + "#ifndef HELLO\n" + " #define HELLO \"Hello\"\n" + "#endif\n\n"
      + "int main( int argc, char ** argv)\n" + "{\n" + "  int i;\n"
      + "  for( i=0; i< argc; i++)\n" + "    printf( HELLO \" world!\");\n\n"
      + "  return 0 * sizeof(int);\n" + "}\n";

  /**
   */
  public static void main( String[] args)
  {

    // set up the GUI
    DemoFrame frame= new Highlighting02Demo();
    // put text into the editor
    JEditorPane editor= frame.getEditor();
    configureEditor( editor);

    editor.setText( sampleText);

    frame.pack();

    frame.setVisible( true);
  }

  /**
   * Sets a font suitable for syntax highlighting. <br>
   * Installs editor kit for C syntax highlighting. <br>
   * Sets a non-default colour for the rendering of comments and keywords.
   * Keywords are also rendered as italic.
   * <p>
   * NOTE: Highlighting with Nacre requires a font that has the same width,
   * regardless whether the font is rendered PLAIN, BOLD or ITALIC. Use the
   * {@link de.marw.nacre.tool.FontFetcher FontFetcher} tool to find the
   * appropriate fonts for your system.
   * </p>
   * 
   * @param editor
   *        the editor component that shows the highlighted text.
   */
  private static void configureEditor( JEditorPane editor)
  {
    /**
     * Create an EditorKit for C syntax highlighting and install it at the
     * editor component.
     */
    HighlightingKit kit= new C_HighlightingKit();
    editor.setEditorKit( kit);

    /**
     * Set the font of the editor component.
     */
    editor.setFont( new Font( "Courier New", Font.PLAIN, 13));

    /*
     * customise colour and font style of hightlighting
     */
    CategoryStyles styles= kit.getCategoryStyles();
    // comments are yellow..
    styles.setColor( Category.COMMENT_2, Color.YELLOW);
    styles.setColor( Category.KEYWORD, Color.ORANGE);
    styles.setItalic( Category.KEYWORD, true);
  }

}