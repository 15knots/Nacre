/* $Header$ */

// Copyright © 2004 Martin Weber

package swing.text.highlight;

import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;

import javax.swing.text.Segment;


/**
 * Class to provide InputStream functionality from a Segment. This really should
 * be a Reader.
 */
class SegmentInputStream extends InputStream
{
  private Segment segment;

  private char c;

  public SegmentInputStream( Segment segment)
  {
    this.segment = segment;
    c = segment.first(); // initialize CharIterator
  }

  /**
   * Reads the next byte of data from this input stream. The value byte is
   * returned as an <code>int</code> in the range <code>0</code> to
   * <code>255</code>. If no byte is available because the end of the stream
   * has been reached, the value <code>-1</code> is returned. This method
   * blocks until input data is available, the end of the stream is detected, or
   * an exception is thrown.
   * <p>
   * 
   * @return the next byte of data, or <code>-1</code> if the end of the
   *         stream is reached.
   * @exception IOException
   *            if an I/O error occurs.
   */
  public int read() throws IOException
  {
    if (c == CharacterIterator.DONE) {
      return -1;
    }
    int ret = c;
    c = segment.next();
    return ret;
  }

}