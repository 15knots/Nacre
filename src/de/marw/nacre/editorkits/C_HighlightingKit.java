/**
 */

package swing.text.highlight;

import javax.swing.text.Segment;

import swing.text.highlight.categoriser.Categoriser;


/**
 * This kit supports a fairly minimal handling of editing C text content. It
 * supports syntax highlighting and produces the lexical structure of the
 * document as best it can.
 * 
 * @author Timothy Prinzing
 * @author Martin Weber
 * @version 1.2 05/27/99
 */
public class CHighlightingKit extends HighlightingKit
{

  public CHighlightingKit()
  {
    super();
  }

  /**
   * Gets the MIME type of the data that this kit represents support for. This
   * kit supports the type <code>text/x-c-src</code>.
   */
  public String getContentType()
  {
    // check whether MIME type is apprpriate
    return "text/x-c-src";
  }

  /**
   * Creates a Categoriser used for highlighting text of this document of
   * <code>null</code>.
   */
  protected Categoriser createCategoriser()
  {
    return new C_Tokeniser();
  }

  /**
   * Checks if a subregion of a <code>Segment</code> is equal to a character
   * array.
   * 
   * @param ignoreCase
   *          True if case should be ignored, false otherwise
   * @param text
   *          The segment
   * @param offset
   *          The offset into the segment
   * @param match
   *          The character array to match
   */
  private static boolean regionMatches( boolean ignoreCase, Segment text,
      int offset, char[] match)
  {
    int endpos = offset + match.length;
    char[] textArray = text.array;

    if (endpos > (text.offset + text.count)) {
      return false;
    }

    for (int i = offset, j = 0; i < endpos; i++ , j++ ) {
      char c1 = textArray[i];
      char c2 = match[j];

      if (ignoreCase) {
        c1 = Character.toUpperCase( c1);
        c2 = Character.toUpperCase( c2);
      }

      if (c1 != c2) {
        return false;
      }
    }

    return true;
  }

  //public byte markTokensImpl( Segment line, int lineIndex)
  //{
  //  byte lastTokenType;
  //  char[] array= line.array;
  //  int offset= line.offset;
  //  int lastOffset= offset;
  //  int lastKeyword= offset;
  //
  //  int length= line.count + offset;
  //  boolean backslash= false;
  //
  //  loop: for (int i= offset; i < length; i++) {
  //    int i1= (i + 1);
  //
  //    char c= array[i];
  //
  //    if (c == '\\') {
  //      backslash= !backslash;
  //
  //      continue;
  //    }
  //
  //    switch (lastTokenType) {
  //      case CategoryConstants.NORMAL:
  //
  //        switch (c) {
  //          /*
  //           * case '#': if(backslash) backslash = false;
  //           * if(doKeyword(line,i,c)) break; addToken(i - lastOffset,token);
  //           * addToken(length - i,CategoryConstants.KEYWORD2); lastOffset =
  //           * lastKeyword = length; break loop;
  //           */
  //          case '"':
  //            doKeyword( line, i, c);
  //
  //            if (backslash) {
  //              backslash= false;
  //            }
  //            else {
  //              addToken( i - lastOffset, lastTokenType);
  //              lastTokenType= CategoryConstants.STRINGVAL;
  //              lastOffset= lastKeyword= i;
  //            }
  //
  //          break;
  //
  //          case '\'':
  //            doKeyword( line, i, c);
  //
  //            if (backslash) {
  //              backslash= false;
  //            }
  //            else {
  //              addToken( i - lastOffset, lastTokenType);
  //              lastTokenType= CategoryConstants.STRINGVAL;
  //              lastOffset= lastKeyword= i;
  //            }
  //
  //          break;
  //
  //          case ':':
  //
  //            if (lastKeyword == offset) {
  //              if (doKeyword( line, i, c)) {
  //                break;
  //              }
  //
  //              backslash= false;
  //              addToken( i1 - lastOffset, CategoryConstants.LABEL);
  //              lastOffset= lastKeyword= i1;
  //            }
  //            else if (doKeyword( line, i, c)) {
  //              break;
  //            }
  //
  //          break;
  //
  //          case '/':
  //            backslash= false;
  //            doKeyword( line, i, c);
  //
  //            if ((length - i) > 1) {
  //              switch (array[i1]) {
  //                case '*':
  //                  addToken( i - lastOffset, lastTokenType);
  //                  lastOffset= lastKeyword= i;
  //
  //                  if (((length - i) > 2) && (array[i + 2] == '*')) {
  //                    lastTokenType= CategoryConstants.COMMENT2;
  //                  }
  //                  else {
  //                    lastTokenType= CategoryConstants.COMMENT1;
  //                  }
  //
  //                break;
  //
  //                case '/':
  //                  addToken( i - lastOffset, lastTokenType);
  //                  addToken( length - i, CategoryConstants.COMMENT1);
  //                  lastOffset= lastKeyword= length;
  //
  //                break loop;
  //              }
  //            }
  //
  //          break;
  //
  //          default:
  //            backslash= false;
  //
  //            if (!Character.isLetterOrDigit( c) && (c != '_') && (c != '#')) {
  //              doKeyword( line, i, c);
  //            }
  //
  //          break;
  //        }
  //
  //      break;
  //
  //      case CategoryConstants.COMMENT1:
  //      case CategoryConstants.COMMENT2:
  //        backslash= false;
  //
  //        if ((c == '*') && ((length - i) > 1)) {
  //          if (array[i1] == '/') {
  //            i++;
  //            addToken( (i + 1) - lastOffset, lastTokenType);
  //            lastTokenType= CategoryConstants.NORMAL;
  //            lastOffset= lastKeyword= i + 1;
  //          }
  //        }
  //
  //      break;
  //
  //      case CategoryConstants.STRINGVAL:
  //
  //        if (backslash) {
  //          backslash= false;
  //        }
  //        else if (c == '"') {
  //          addToken( i1 - lastOffset, lastTokenType);
  //          lastTokenType= CategoryConstants.NORMAL;
  //          lastOffset= lastKeyword= i1;
  //        }
  //
  //      break;
  //
  //      case CategoryConstants.STRINGVAL:
  //
  //        if (backslash) {
  //          backslash= false;
  //        }
  //        else if (c == '\'') {
  //          addToken( i1 - lastOffset, CategoryConstants.STRINGVAL);
  //          lastTokenType= CategoryConstants.NORMAL;
  //          lastOffset= lastKeyword= i1;
  //        }
  //
  //      break;
  //
  //      default:
  //        throw new InternalError( "Invalid state: " + lastTokenType);
  //    }
  //  }
  //
  //  if (lastTokenType == CategoryConstants.NORMAL) {
  //    doKeyword( line, length, '\0');
  //  }
  //
  //  switch (lastTokenType) {
  //    case CategoryConstants.STRINGVAL:
  //    case CategoryConstants.STRINGVAL:
  //      addToken( length - lastOffset, CategoryConstants.INVALID);
  //      lastTokenType= CategoryConstants.NORMAL;
  //
  //    break;
  //
  //    case CategoryConstants.KEYWORD2:
  //      addToken( length - lastOffset, lastTokenType);
  //
  //      if (!backslash) {
  //        lastTokenType= CategoryConstants.NORMAL;
  //      }
  //
  //    default:
  //      addToken( length - lastOffset, lastTokenType);
  //
  //    break;
  //  }
  //
  //  return lastTokenType;
  //}

}

