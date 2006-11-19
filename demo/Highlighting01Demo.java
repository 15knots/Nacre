/* $Id$ */
// Copyright 2006 Martin Weber
import java.awt.Font;
import java.awt.HeadlessException;

import javax.swing.JEditorPane;
import javax.swing.text.EditorKit;

import de.marw.nacre.editorkits.C_HighlightingKit;


/**
 * This basic demo shows how to set up a single <code>JEditorPane</code> to
 * support highlighting of <em>C</em> program code.<br>
 */
public class Highlighting01Demo extends DemoFrame
{

  private static final String sampleText=
    "/* Sample C code.\n"
      + "  Sorry for the typos in here, just fix them and see how it works.\n"
      + "* /\n\n" + " #  include <stdio.h>\n\n" + "#ifndef HELLO\n"
      + " #define HELLO \"Hello\"\n" + "#endif\n\n"
      + "int main( int argc, char ** argv)\n" + "{\n" + "  int i;\n"
      + "  for( i=0; i< argc; i++)\n" + "    printf( HELLO \" world!\");\n\n"
      + "  return 0 * sizeof(int);\n" + "}\n";

  /**
   */
  public Highlighting01Demo() throws HeadlessException
  {
    // install an editor kit that does syntax highlighting
    configureEditor( getEditor());
  }

  /**
   * Installs editor kit for C syntax highlighting. <br>
   * Sets a font suitable for syntax highlighting. <br>
   */
  protected void configureEditor( JEditorPane editor)
  {
    /**
     * Create an EditorKit for C syntax highlighting and install it at the
     * editor component.
     */
    EditorKit kit= new C_HighlightingKit();
    editor.setEditorKit( kit);

    /**
     * Set the font of the editor component.<br>
     * Highlighting requires a font that has the same width, regardless whether
     * the font is rendered PLAIN, BOLD or ITALIC.
     */
    editor.setFont( new Font( "Courier New", Font.PLAIN, 13));
    /*
     * below is a list of suitable fonts I found on my system. Use the
     * FontFetcher tool to find the fonts on your system.
     */
    // editor.setFont( new Font( "Courier", Font.PLAIN, 12));
    // editor.setFont( new Font( "Courier 10 Pitch", Font.PLAIN, 12));
    // editor.setFont( new Font( "Courier New", Font.PLAIN, 13));
    // # editor.setFont( new Font( "Cumberland AMT", Font.PLAIN, 13));
    // editor.setFont( new Font( "DialogInput", Font.PLAIN, 12));
    // # editor.setFont( new Font( "Lucida Sans Typewriter", Font.PLAIN, 12));
    // editor.setFont( new Font( "Luxi Mono", Font.PLAIN, 12));
    // editor.setFont( new Font( "Monospaced", Font.PLAIN, 12));
    // editor.setFont( new Font( "Nimbus Mono L", Font.PLAIN, 14));
    // editor.setFont( new Font( "SUSE Sans Mono", Font.PLAIN, 12));
    // editor.setFont( new Font( "SansSerif", Font.PLAIN, 13));
  }

  @Override
  protected String getDemoName()
  {
    return "Basic Highlighting Demo";
  }

  // //////////////////////////////////////////////////////////////////
  /**
   */
  public static void main( String[] args)
  {

    // set up the GUI
    DemoFrame frame= new Highlighting01Demo();
    // put text into the editor
    JEditorPane editor= frame.getEditor();
    editor.setText( sampleText);

    frame.pack();
    // frame.setSize( 211, 701);

    frame.setVisible( true);
  }

}