
import java.awt.Color;
import java.awt.Component;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTable;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.TableCellRenderer;


/**
 * @author weber
 */
public class ColorRenderer extends JPanel implements TableCellRenderer
{

  protected Border noFocusBorder = new EmptyBorder( 1, 1, 1, 1);

  // We need a place to store the color the JLabel should be returned
  // to after its foreground and background colors have been set
  // to the selection background color.
  // These ivars will be made protected when their names are finalized.
  private Color unselectedForeground;

  private Color unselectedBackground;

  private JLabel label;

  JComponent colorLabel;

  /**
   * 
   */
  public ColorRenderer() {
    super(false);
    setLayout(new BoxLayout(this,BoxLayout.X_AXIS));
    setOpaque( true);
    setBorder( noFocusBorder);
    label = new JLabel();
    label.setFont(null);
    colorLabel=new JButton();
    colorLabel.setFont(null);
    add( label);
    add(Box.createHorizontalStrut(3));
    add(Box.createGlue());
    add( colorLabel);
    // TODO Auto-generated constructor stub
  }

  // implements javax.swing.table.TableCellRenderer
  /**
   * Returns the default table cell renderer.
   * 
   * @param table
   *        the <code>JTable</code>
   * @param value
   *        the value to assign to the cell at <code>[row, column]</code>
   * @param isSelected
   *        true if cell is selected
   * @param hasFocus
   *        true if cell has focus
   * @param row
   *        the row of the cell to render
   * @param column
   *        the column of the cell to render
   * @return the default table cell renderer
   */
  public Component getTableCellRendererComponent( JTable table, Object value,
      boolean isSelected, boolean hasFocus, int row, int column)
  {

    if (isSelected) {
      super.setForeground( table.getSelectionForeground());
      super.setBackground( table.getSelectionBackground());
    }
    else {
      super
          .setForeground( (unselectedForeground != null) ? unselectedForeground
              : table.getForeground());
      super
          .setBackground( (unselectedBackground != null) ? unselectedBackground
              : table.getBackground());
    }

    setFont( table.getFont());

    if (hasFocus) {
      setBorder( UIManager.getBorder( "Table.focusCellHighlightBorder"));
      if ( !isSelected && table.isCellEditable( row, column)) {
        Color col;
        col = UIManager.getColor( "Table.focusCellForeground");
        if (col != null) {
          super.setForeground( col);
        }
        col = UIManager.getColor( "Table.focusCellBackground");
        if (col != null) {
          super.setBackground( col);
        }
      }
    }
    else {
      setBorder( noFocusBorder);
    }

    setValue( (Color) value);

    return this;
  }

  /**
   * Sets the <code>Color</code> object for the cell being rendered to
   * <code>value</code>.
   * 
   * @param value
   *        the string value for this cell; if value is <code>null</code> it
   *        sets the text value to an empty string
   * @see JLabel#setText
   */
  protected void setValue( Color value)
  {
    if(value == null){
      label.setText("<default>");
      colorLabel.setVisible(false);
    }else {
    label.setText( colorToHex( value));
    colorLabel.setBackground(value);
    colorLabel.setVisible(true);
    }
  }

  /**
   * Converts a type Color to a hex string in the format "#RRGGBB" <br>
   * from CSS.class
   */
  static String colorToHex( Color color)
  {

    StringBuffer colorstr = new StringBuffer( "#");

    // Red
    String str = Integer.toHexString( color.getRed());
    if (str.length() > 2)
      str = str.substring( 0, 2);
    else if (str.length() < 2)
      colorstr.append( "0");
    colorstr.append( str);

    // Green
    str = Integer.toHexString( color.getGreen());
    if (str.length() > 2)
      str = str.substring( 0, 2);
    else if (str.length() < 2)
      colorstr.append( "0");
    colorstr.append( str);

    // Blue
    str = Integer.toHexString( color.getBlue());
    if (str.length() > 2)
      str = str.substring( 0, 2);
    else if (str.length() < 2)
      colorstr.append( "0");
    colorstr.append( str);

    return colorstr.toString();
  }

  /**
   * Convert a "#FFFFFF" hex string to a Color. If the color specification is
   * bad, an attempt will be made to fix it up.
   */
  static final Color hexToColor( String value)
  {
    String digits;
    int n = value.length();
    if (value.startsWith( "#")) {
      digits = value.substring( 1, Math.min( value.length(), 7));
    }
    else {
      digits = value;
    }
    String hstr = "0x" + digits;
    Color c;
    try {
      c = Color.decode( hstr);
    }
    catch (NumberFormatException nfe) {
      c = null;
    }
    return c;
  }

}