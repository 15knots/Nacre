
import java.awt.Color;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.marw.javax.swing.text.highlight.Category;
import de.marw.javax.swing.text.highlight.CategoryStyles;


/**
 * @author weber
 */
public class CategoryTableModel extends AbstractTableModel
{

  private String[] columnNames = new String[] { "Category", "Color", "Bold",
      "Italic" };

  private CategoryStyles styles;

  private Map descriptions;

  /**
   * 
   */
  public CategoryTableModel( Map descriptions, CategoryStyles styles) {
    super();
    if (styles == null) {
      throw new NullPointerException( "styles");
    }
    this.styles = styles;
    this.descriptions = descriptions;
  }

  /**
   * @see javax.swing.table.TableModel#getRowCount()
   */
  public int getRowCount()
  {
    return descriptions.size();
  }

  /**
   * @see javax.swing.table.TableModel#getColumnCount()
   */
  public int getColumnCount()
  {
    return columnNames.length;
  }

  /**
   * @see javax.swing.table.TableModel#getColumnClass(int)
   */
  public Class< ? > getColumnClass( int columnIndex)
  {
    switch (columnIndex) {
      case 0:
        return String.class;
      case 1:
        return Color.class;
      case 2:
      case 3:
        return Boolean.class;
    }
    return super.getColumnClass( columnIndex);
  }

  /**
   * @see javax.swing.table.TableModel#getColumnName(int)
   */
  public String getColumnName( int column)
  {
    if (column <= columnNames.length) {
      return columnNames[column];
    }
    return super.getColumnName( column);
  }

  /**
   * @see javax.swing.table.TableModel#isCellEditable(int, int)
   */
  public boolean isCellEditable( int rowIndex, int columnIndex)
  {
    if (columnIndex == 0)
      return false;
    return true;
  }

  /**
   * @see javax.swing.table.TableModel#setValueAt(java.lang.Object, int, int)
   */
  public void setValueAt( Object aValue, int rowIndex, int columnIndex)
  {
    Category cat = getCategoryAt( rowIndex);
    if (cat != null) {
      switch (columnIndex) {
        case 0:
          return;
        case 1:
          styles.setColor( cat, (Color) aValue);
        break;
        case 2:
          styles.setBold( cat, ((Boolean) aValue).booleanValue());
        break;
        case 3:
          styles.setItalic( cat, ((Boolean) aValue).booleanValue());
        break;
      }
    }
  }

  /**
   * @see javax.swing.table.TableModel#getValueAt(int, int)
   */
  public Object getValueAt( int rowIndex, int columnIndex)
  {
    Category cat = getCategoryAt( rowIndex);
    if (cat != null) {
      switch (columnIndex) {
        case 0:
          return descriptions.get(cat);
        case 1:
          return styles.getColor( cat);
        case 2:
          return Boolean.valueOf( styles.isBold( cat));
        case 3:
          return Boolean.valueOf( styles.isItalic( cat));
      }
    }
    return null;
  }

  /**
   * @param rowIndex
   * @return
   */
  private Category getCategoryAt( int rowIndex)
  {
    Category cat = null;
    Category[] cats = Category.values();
    for (int i = 0; i < cats.length; i++) {
      if (rowIndex == cats[i].ordinal()) {
        cat = cats[i];
        break;
      }
    }
    return cat;
  }

}