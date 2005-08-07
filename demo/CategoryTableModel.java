// $Id$
/*
 * Copyright 2005 by Martin Weber
 */

import java.awt.Color;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import de.marw.javax.swing.text.highlight.Category;
import de.marw.javax.swing.text.highlight.CategoryStyles;


/**
 * @author Martin Weber
 */
public class CategoryTableModel extends AbstractTableModel
{

  /**
   * 
   */
  private static final long serialVersionUID = -7437682496492287789L;

  private final String[] columnNames = new String[] { "Category", "Color", "Bold",
      "Italic" };

  private final CategoryStyles styles;

  private final Map<Category, String> descriptions;

  private final Category[] rowToDescriptionMap;

  /**
   * 
   */
  public CategoryTableModel( Map<Category, String> descriptions,
      CategoryStyles styles) {
    super();
    if (styles == null) {
      throw new NullPointerException( "styles");
    }
    this.styles = styles;
    this.descriptions = descriptions;
    this.rowToDescriptionMap = descriptions.keySet().toArray(
        new Category[getRowCount()]);
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
          return descriptions.get( cat);
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

    if (rowIndex < rowToDescriptionMap.length) {
      return rowToDescriptionMap[rowIndex];
    }
    return null;
  }

}