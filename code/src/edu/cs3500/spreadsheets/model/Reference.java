package edu.cs3500.spreadsheets.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract Class to represent a Reference to cells.
 */
abstract class Reference extends EvaluatableCell {
  private static final String DELIMIT = "!";
  protected final Workbook context;

  public Reference(Workbook context) {
    this.context = context;
  }

  public String getRaw() {
    return this.toString();
  }

  protected static final WorksheetId<Coord> parse(String str, String defaultSheet) {
    String[] parts = parseSheet(str, defaultSheet);
    String sheet = parts[1];
    if (sheet == null) {
      System.out.println(defaultSheet);
    }
    String location = parts[0];
    return new WorksheetId<>(sheet, parse(location));
  }

  protected static final Coord parse(String str) {
    if (!isReference(str)) {
      throw new IllegalArgumentException("Expected a String representing a coordinate. Got " + str);
    }
    String letterPart = str.replaceAll("[0-9]", "");
    // Get the numeric part
    int numberPart = Integer.parseInt(str.replaceAll("[A-X]", ""));
    return new Coord(Coord.colNameToIndex(letterPart), numberPart);
  }

  /**
   * Separates the coordinate from the Spreadsheet, with default handling.
   * @param str the coordinate string to isolate
   * @param defaultSheet the default worksheet if none is found in the coordinate.
   * @return String[] {coordinate, spreadsheet}
   */
  protected static final String[] parseSheet(String str, String defaultSheet) {
    String[] parts = new String[2];
    parts[0] = str;
    parts[1] = defaultSheet;
    if (str.contains(DELIMIT)) {
      parts[0] = str.substring(str.indexOf(DELIMIT) + 1);
      parts[1] = str.substring(0, str.indexOf(DELIMIT));
    }
    return parts;
  }

  /**
   * Retrieves a cell from the context.
   * @param location the location of the cell to retrieve
   * @param origin the first cell in the chain
   * @return the cell at the given location
   */
  protected final ICell retrieve(WorksheetId<Coord> location, EvaluatableCell origin) {
    ICell cell = this.context.getCellAt(location);
    if (cell == origin) {
      return new Error("Circular dependency");
    }
    if (cell instanceof EvaluatableCell) {
      cell = ((EvaluatableCell) cell).getValue(origin);
    }
    return cell;
  }

  @Override
  public List<WorksheetId<Coord>> getDependencies() {
    return new ArrayList<>();
  }

  @Override
  public List<WorksheetId<Integer>> getColDependencies() {
    return new ArrayList<>();
  }

  /**
   * Determines whether the given symbol represents a Reference.
   *
   * @param s the string to check
   * @return if the String represents a Reference
   */
  public static boolean isReference(String s) {
    String coordPart = parseSheet(s, "")[0];
    return s.matches("[A-Z]+[0-9]+"); // capital letters then decimal digits
  }

}
