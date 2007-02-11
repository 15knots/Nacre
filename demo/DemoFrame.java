// $Id$
/*
 * Copyright 2006 by Martin Weber
 */

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JEditorPane;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.text.Document;


/**
 * The JFrame used for all demo applications.
 * 
 * @author weber
 */
public abstract class DemoFrame extends JFrame
{

  /** the editor component that shows the highlighted text */
  private JEditorPane editor;

  public DemoFrame()
  {
    super();
    setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE);
    getContentPane().setLayout( new BorderLayout());
    JScrollPane scroller= new JScrollPane();
    scroller.setViewportView( getEditor());

    getContentPane().add( "Center", scroller);
  }

  /**
   * Gets the first part of the text for the frame title.
   */
  protected abstract String getDemoName();

  /**
   * Gets the editor component that shows the highlighted text.
   */
  protected JEditorPane getEditor()
  {
    if (editor == null) {
      editor= new JEditorPane();
      // change the frame's title
      PropertyChangeListener updater= new TitleUpdater();
      editor.addPropertyChangeListener( "document", updater);
      editor.addPropertyChangeListener( "editorKit", updater);
    }
    return editor;
  }

  // //////////////////////////////////////////////////////////////////
  private class TitleUpdater implements PropertyChangeListener
  {
    private String docName;

    private String contentType;

    public void propertyChange( PropertyChangeEvent evt)
    {
      String propertyName= evt.getPropertyName();
      if (propertyName == "document") {
        Document doc= (Document) evt.getNewValue();
        Object docDesc= doc.getProperty( Document.StreamDescriptionProperty);
        if (docDesc != null) {
          docName= docDesc.toString();
        }
      }
      else if (propertyName == "editorKit") {
        String conType= editor.getContentType();
        if (conType != null) {
          contentType= conType;
        }
      }
      setTitle( getDemoName() + (docName != null
        ? "- " + docName : "") + (contentType != null
        ? (" (" + contentType + ")") : ""));
    }
  }
}