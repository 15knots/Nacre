// $Header$

// Copyright © 2004 Razorcat Development GmbH

package swing.text.highlight;

import java.io.IOException;
import java.io.InputStream;
import java.text.CharacterIterator;

import javax.swing.text.Segment;


/**
 * Class to provide InputStream functionality from a Segment. This really
 * should be a Reader, but not enough things use it yet.
 */
class SegmentInputStream extends InputStream
{
  private char c;
  public SegmentInputStream( Segment segment)
  {
    this.segment= segment;
    c= segment.first();
  }

  /**
   * Reads the next byte of data from this input stream. The value byte is
   * returned as an <code>int</code> in the range <code>0</code> to
   * <code>255</code>. If no byte is available because the end of the
   * stream has been reached, the value <code>-1</code> is returned. This
   * method blocks until input data is available, the end of the stream is
   * detected, or an exception is thrown.
   * <p>
   * A subclass must provide an implementation of this method.
   * 
   * @return the next byte of data, or <code>-1</code> if the end of the
   *         stream is reached.
   * @exception IOException
   *            if an I/O error occurs.
   * @since JDK1.0
   */
  public int read() throws IOException
  {
    //      if (index >= segment.offset + segment.count) {
    //        // no more data
    //        return -1;
    //      }
    //      return segment.array[index++];
    int ret=c == CharacterIterator.DONE ? -1 : c;
    c= segment.next();
    return ret;
  }

  Segment segment;

  int     index;  // index into array of the segment
}