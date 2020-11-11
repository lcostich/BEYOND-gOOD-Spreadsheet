package edu.cs3500.spreadsheets.model;

import java.io.StringReader;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A factory for reading inputs and producing Worksheets: given a {@link WorkbookBuilder} to produce
 * the new Worksheet itself, this class can parse the given input and populate the worksheet.
 */
public final class WorkbookReader {
  /**
   * A builder pattern for producing Worksheets.
   *
   * @param <T> the type of Worksheet to produce
   */
  public interface WorkbookBuilder<T> {
    /**
     * Creates a new cell at the given coordinates and fills in its raw contents.
     *
     * @param col      the column of the new cell (1-indexed)
     * @param row      the row of the new cell (1-indexed)
     * @param contents the raw contents of the new cell: may be {@code null}, or any string. Strings
     *                 beginning with an {@code =} character should be treated as formulas; all
     *                 other strings should be treated as number or boolean values if possible, and
     *                 string values otherwise.
     * @return this {@link WorkbookBuilder}
     */
    WorkbookBuilder<T> createCell(int col, int row, String contents);

    /**
     * Puts any following cells into the worksheet of the given name.
     *
     * @param sheet the worksheet to open
     */
    void openSheet(String sheet);

    /**
     * Closes the open worksheet, if any.
     */
    void closeSheet();

    /**
     * Finalizes the construction of the worksheet and returns it.
     *
     * @return the fully-filled-in worksheet
     */
    T createWorkbook();
  }


  private static <T> void readSheet(WorkbookBuilder<T> builder, Readable readable) {
    Scanner scan = new Scanner(readable);
    final Pattern cellRef = Pattern.compile("([A-Za-z]+)([1-9][0-9]*)");
    scan.useDelimiter("\\s+");
    while (scan.hasNext()) {
      int col;
      int row;
      while (scan.hasNext("#.*")) {
        scan.nextLine();
        scan.skip("\\s*");
      }
      String cell = scan.next();
      Matcher m = cellRef.matcher(cell);
      if (m.matches()) {
        col = Coord.colNameToIndex(m.group(1));
        row = Integer.parseInt(m.group(2));
      } else {
        throw new IllegalStateException("Expected cell ref");
      }
      scan.skip("\\s*");
      while (scan.hasNext("#.*")) {
        scan.nextLine();
        scan.skip("\\s*");
      }
      String contents = scan.nextLine();
      builder = builder.createCell(col, row, contents);
    }
  }

  /**
   * <p>A factory for producing Workbooks.  The file format is</p>
   * <pre>
   *   &lt;spreadsheets, in the format name{...}&gt;
   *   &lt;cell coordinates, in A# format&gt; &lt;the raw contents of the cell&gt;
   *   ...
   * </pre>
   * <p>e.g.</p>
   * <pre>
   *   A1 5
   *   A2 6
   *   A3 =(SUM A1 A2:A2)
   * </pre>
   * <p>Line-comments are indicated by <code>#</code>, and are allowed either at the start
   * of a line or between the cell coordinate and its contents.</p>
   * <p>There is no requirement that cells are filled in in order of their dependencies,
   * since no cell evaluation occurs during this creation process.</p>
   *
   * @param builder  The source of the new Worksheet object
   * @param readable the input source for the contents of this Worksheet
   * @param <T>      the type of Worksheet to produce
   * @return the fully-filled-in Worksheet
   */
  public static <T> T read(WorkbookBuilder<T> builder, Readable readable) {
    Scanner scan = new Scanner(readable);
    // Delimit using { } ( ) and "
    scan.useDelimiter("\n");
    String layers = "";
    Pattern markSearch = Pattern.compile("\"\\(\\)");
    String spreadsheet = "";
    StringBuilder spreadsheetContents = new StringBuilder();
    boolean hasSpreadsheet = false;
    boolean hasOutside = false;


    // Go through the Readable line by line
    while (scan.hasNext()) {
      // If this line is a comment, skip
      if (scan.hasNext("#.*")) {
        while (scan.hasNext("#.*")) {
          scan.nextLine();
          scan.skip("\\s*");
        }
      } else {
        String line = scan.next();
        // Only process non-blank lines
        if (!line.isBlank()) {
          // Keep track of other delimiters
          Matcher matcher = markSearch.matcher(line);
          if (!matcher.find()) {
            if (spreadsheet.isEmpty()) {
              // Look to open a Spreadsheet
              if (line.contains("{")) {
                System.out.println("Opening spreadsheet");
                spreadsheet = line.substring(0, line.indexOf("{"));
                // Check for Spreadsheet name validity
                if (spreadsheet.matches("^[a-zA-Z0-9_]*$")) {
                  spreadsheetContents = new StringBuilder();
                  builder.openSheet(spreadsheet);
                  hasSpreadsheet = true;
                } else {
                  throw new IllegalStateException("Improper Spreadsheet name: " + spreadsheet);
                }
              } else {
                hasOutside = true;
                spreadsheetContents.append(line);
                spreadsheetContents.append("\n");
              }
            } else {
              if (line.contains("}")) {
                // Parse spreadsheet contents
                readSheet(builder, new StringReader(spreadsheetContents.toString()));
                // Reset spreadsheet.
                System.out.println("Closing spreadsheet");
                builder.closeSheet();
                spreadsheet = "";
                spreadsheetContents = new StringBuilder();
              } else {
                spreadsheetContents.append(line);
                spreadsheetContents.append("\n");
              }
            }
          }
        }
      }
    }
    if (hasSpreadsheet && hasOutside) {
      throw new IllegalStateException("Found floating cells");
    } else if (!hasSpreadsheet) {
      // Handle legacy format
      System.out.println("Building default spreadsheet");
      builder.openSheet("Spreadsheet");
      readSheet(builder, new StringReader(spreadsheetContents.toString()));
      builder.closeSheet();
    }

    return builder.createWorkbook();
  }
}
