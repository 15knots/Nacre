// $Id$
/*
 * Copyright 2005 by Martin Weber
 */

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.util.Map;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

import de.marw.nacre.editorkits.C_HighlightingKit;
import de.marw.nacre.highlight.CategoryStyles;
import de.marw.nacre.highlight.HighlightingKit;
import de.marw.nacre.highlight.categoriser.Category;


/**
 * @author weber
 */
public class CustomizerFrame extends JFrame
{

  private javax.swing.JPanel jContentPane= null; // @jve:decl-index=0:visual-constraint="24,15"

  private JScrollPane jScrollPane_preview= null;

  private JEditorPane jPreviewEditorPane= null;

  private JLabel jLabel= null;

  private JPanel jDialogButtonsPanel= null;

  private JButton jButton_Apply= null;

  private JButton jButton_Close= null;

  private JButton jButton_OK= null;

  private JPanel jCustomizerPanel= null;

  private JLabel jLabel1= null;

  private JTable jCategoryTable= null;

  private JScrollPane jScrollPane_table= null;

  /**
   * This is the default constructor
   */
  public CustomizerFrame()
  {
    super();
    initialize();
  }

  /**
   * This method initializes this
   */
  private void initialize()
  {
    this.setContentPane( getJContentPane()); // Generated
    this.pack();
    this.setSize( 400, 500);
    HighlightingKit kit= ((HighlightingKit) jPreviewEditorPane.getEditorKit());
    CategoryStyles styles= kit.getCategoryStyles();

    setTitle( "HighlightingKit: " + kit.getContentType());
    Map<Category, String> descriptions= kit.getCategoryDescriptions( null);
    System.out.println( "[initialize] value of descriptions: " + descriptions);
    this.jCategoryTable
      .setModel( new CategoryTableModel( descriptions, styles));
    this.jCategoryTable.setDefaultRenderer( Color.class, new ColorRenderer());
  }

  /**
   * This method initializes jContentPane
   * 
   * @return javax.swing.JPanel
   */
  private javax.swing.JPanel getJContentPane()
  {
    if (jContentPane == null) {
      jContentPane= new javax.swing.JPanel();
      jContentPane.setLayout( new java.awt.BorderLayout());
      jContentPane.add( getJCustomizerPanel(), java.awt.BorderLayout.CENTER); // Generated
      jContentPane.add( getJDialogButtonsPanel(), java.awt.BorderLayout.SOUTH); // Generated
    }
    return jContentPane;
  }

  /**
   * This method initializes jScrollPane_preview
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getJScrollPane_preview()
  {
    if (jScrollPane_preview == null) {
      jScrollPane_preview= new JScrollPane();
      jScrollPane_preview.setViewportView( getPreview()); // Generated
    }
    return jScrollPane_preview;
  }

  /**
   * This method initializes jPreviewEditorPane
   * 
   * @return javax.swing.JEditorPane
   */
  private JEditorPane getPreview()
  {
    if (jPreviewEditorPane == null) {
      jPreviewEditorPane= new JEditorPane();
      // HighlightingKit kit = new JavaHighlightingKit();
      // jPreviewEditorPane.setEditorKitForContentType( kit.getContentType(),
      // kit);
      HighlightingKit kit= new C_HighlightingKit();
      jPreviewEditorPane.setEditorKitForContentType( kit.getContentType(), kit);
      // 
      jPreviewEditorPane.setContentType( kit.getContentType());
      jPreviewEditorPane.setBackground( Color.white);
      jPreviewEditorPane.setFont( new Font( "Monospaced", Font.PLAIN, 12));
      jPreviewEditorPane.setText( getPreviewText());
    }
    return jPreviewEditorPane;
  }

  /**
   * @return
   */
  private String getPreviewText()
  {
    String text=
      "/* text for preview\n" + "*/\n" + "\n"
        + "# define falze (\'X\'==\'U\')  \n" + "#include <stdio.h>\n" + "\n"
        + "int pain( int orkc, char** orks){\n"
        + "   print( \"date= \" __DATE__ \"time=\" __TIME__);\n"
        + "   int foo= 47.11;\n" + "   while(1) {\n"
        + "       foo += sizeof(\'23\');\n"
        + "       char* txt= \"bla bla\";\n" + "   }\n"
        + "   if( *txt == \'a\')\n" + "       return 1;\n"
        + "   return 0;   // success\n" + "}";
    return text;
  }

  /**
   * This method initializes jPanel1
   * 
   * @return javax.swing.JPanel
   */
  private JPanel getJDialogButtonsPanel()
  {
    if (jDialogButtonsPanel == null) {
      jDialogButtonsPanel= new JPanel();
      jDialogButtonsPanel.setLayout( new BoxLayout( jDialogButtonsPanel,
        BoxLayout.X_AXIS)); // Generated
      jDialogButtonsPanel.setBorder( javax.swing.BorderFactory
        .createLineBorder( Color.gray, 1)); // Generated
      jDialogButtonsPanel.add( getJButton_OK(), null); // Generated
      jDialogButtonsPanel.add( getJButton_Close(), null); // Generated
      jDialogButtonsPanel.add( getJButton_Apply(), null); // Generated
    }
    return jDialogButtonsPanel;
  }

  /**
   * This method initializes jPanel2
   * 
   * @return javax.swing.JPanel
   */
  private JComponent getJCustomizerPanel()
  {
    if (jCustomizerPanel == null) {
      jLabel= new JLabel();
      jLabel.setText( "Foreground:"); // Generated
      jLabel1= new JLabel();
      jLabel1.setText( "Preview:"); // Generated
      jCustomizerPanel= new JPanel();
      jCustomizerPanel.setBorder( javax.swing.BorderFactory.createEmptyBorder(
        4, 4, 4, 4)); // Generated
      jCustomizerPanel.setLayout( new BoxLayout( jCustomizerPanel,
        BoxLayout.Y_AXIS)); // Generated
      jCustomizerPanel.add( jLabel, null); // Generated
      Box tBox= new Box( BoxLayout.X_AXIS);
      tBox.add( getJScrollPane_table(), null); // Generated
      tBox.add( Box.createGlue());
      jCustomizerPanel.add( tBox);
      // jCustomizerPanel.add( getJScrollPane_table(), null); // Generated
      jCustomizerPanel.add( jLabel1, null); // Generated
      jCustomizerPanel.add( getJScrollPane_preview(), null); // Generated

      jLabel.setAlignmentX( 0.0F);
      jLabel1.setAlignmentX( 0.0F);
      tBox.setAlignmentX( 0.0F);
      jScrollPane_table.setAlignmentX( 0.0F);
      jScrollPane_preview.setAlignmentX( 0.0F);
    }
    return jCustomizerPanel;
  }

  /**
   * This method initializes jTable
   * 
   * @return javax.swing.JTable
   */
  private JTable getJCategoryTable()
  {
    if (jCategoryTable == null) {
      jCategoryTable= new JTable() {
        public Dimension getPreferredScrollableViewportSize()
        {
          return getPreferredSize();
        }
      };
      jCategoryTable.setShowHorizontalLines( true); // Generated
      jCategoryTable.setAutoResizeMode( JTable.AUTO_RESIZE_OFF); // Generated
      jCategoryTable.setShowGrid( false); // Generated
      jCategoryTable.setSelectionMode( ListSelectionModel.SINGLE_SELECTION); // Generated
    }
    return jCategoryTable;
  }

  /**
   * This method initializes jScrollPane_table
   * 
   * @return javax.swing.JScrollPane
   */
  private JScrollPane getJScrollPane_table()
  {
    if (jScrollPane_table == null) {
      jScrollPane_table= new JScrollPane();
      jScrollPane_table.setViewportView( getJCategoryTable()); // Generated
    }
    return jScrollPane_table;
  }

  /**
   * This method initializes jButton_Apply
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButton_Apply()
  {
    if (jButton_Apply == null) {
      jButton_Apply= new JButton();
      jButton_Apply.setText( "Apply"); // Generated
    }
    return jButton_Apply;
  }

  /**
   * This method initializes jButton_Close
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButton_Close()
  {
    if (jButton_Close == null) {
      jButton_Close= new JButton();
      jButton_Close.setText( "Close"); // Generated
    }
    return jButton_Close;
  }

  /**
   * This method initializes jButton3
   * 
   * @return javax.swing.JButton
   */
  private JButton getJButton_OK()
  {
    if (jButton_OK == null) {
      jButton_OK= new JButton();
      jButton_OK.setText( "OK"); // Generated
    }
    return jButton_OK;
  }

  public static void main( String[] args)
  {
    try {
      JFrame f= new CustomizerFrame();
      f.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
      f.setVisible( true);
    }
    catch (Throwable e) {
      e.printStackTrace();
    }

  }
} // @jve:decl-index=0:visual-constraint="7,6"
