package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * Read-only interface for Workbooks.
 */
public interface ReadableWorkbook {
  /**
   * Evaluate and return the cell's value at the given position. Formulas and references are
   * evaluated.
   *
   * @param location the WorksheetId referring to the cell to query
   * @return the value of the cell
   */
  IReadableCell getValue(WorksheetId<Coord> location);

  /**
   * Retrieve the unevaluated value at the given location. Formulas and references are shown as-is.
   *
   * @param location the WorksheetId referring to the cell location
   * @return the raw value of the cell
   */
  String getRaw(WorksheetId<Coord> location);

  /**
   * Gets a list of non-empty coords within the Spreadsheet.
   *
   * @param sheet the Spreadsheet to look in
   * @return the list of Coords
   */
  List<Coord> getNonEmptyCoords(String sheet);

  /**
   * Gets a list of all Spreadsheet names within the Workbook.
   * @return the list of names in Strings
   */
  List<String> getSpreadsheets();
}
