/* $Id$ */
// Copyright 2006 Martin Weber
import java.awt.Color;
import java.awt.HeadlessException;

import javax.swing.JEditorPane;

import de.marw.nacre.highlight.CategoryStyles;
import de.marw.nacre.highlight.HighlightingKit;
import de.marw.nacre.highlight.categoriser.Category;


/**
 * This basic demo shows how to set up a single <code>JEditorPane</code> to
 * support highlighting of <em>C</em> program code.<br>
 */
public class Highlighting02Demo extends Highlighting01Demo
{

  private static final String sampleText=
    "/* Sample C code.\n*/\n\n" + " #  include <stdio.h>\n\n"
      + "#ifndef HELLO\n" + " #define HELLO \"Hello\"\n" + "#endif\n\n"
      + "int main( int argc, char ** argv)\n" + "{\n" + "  int i;\n"
      + "  for( i=0; i< argc; i++)\n" + "    printf( HELLO \" world!\");\n\n"
      + "  return 0 * sizeof(int);\n" + "}\n";

  /**
   */
  public Highlighting02Demo() throws HeadlessException
  {
    // install an editor kit that does syntax highlighting
    configureEditor( getEditor());
  }

  /**
   * Installs editor kit for C syntax highlighting. <br>
   * Sets a font suitable for syntax highlighting. <br>
   */
  @Override
  protected void configureEditor( JEditorPane editor)
  {
    /**
     * Create an EditorKit for C syntax highlighting and install it at the
     * editor component.<br>
     * Set the font of the editor component.<br>
     * Highlighting requires a font that has the same width, regardless whether
     * the font is rendered PLAIN, BOLD or ITALIC.
     */
    super.configureEditor( editor);
    // customise colour and font style of hightlighting
    HighlightingKit kit= (HighlightingKit) editor.getEditorKit();
    CategoryStyles styles= kit.getCategoryStyles();
    // comments are yellow..
    styles.setColor( Category.COMMENT_2, Color.YELLOW);
    styles.setColor( Category.KEYWORD, Color.ORANGE);
    styles.setItalic( Category.KEYWORD, true);
  }

  @Override
  protected String getDemoName()
  {
    return "Customised Styles Highlighting Demo";
  }

  // //////////////////////////////////////////////////////////////////
  /**
   */
  public static void main( String[] args)
  {

    // set up the GUI
    DemoFrame frame= new Highlighting02Demo();
    // put text into the editor
    JEditorPane editor= frame.getEditor();
    editor.setText( sampleText);

    frame.pack();
    // frame.setSize( 211, 701);

    frame.setVisible( true);
  }

}