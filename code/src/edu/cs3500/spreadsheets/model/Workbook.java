package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * Interface to represent all functionality of a Workbook.
 */
public interface Workbook extends ReadableWorkbook {
  /**
   * Returns the concrete cell at the given location.
   *
   * @param coordinate the location to query
   * @return the cell at the given location
   */
  ICell getCellAt(WorksheetId<Coord> coordinate);

  /**
   * Retrieves all the cells in a given column.
   * @param column the column to look in
   * @return a list of all non-empty cells in the column.
   */
  List<ICell> getColCells(WorksheetId<Integer> column);

  /**
   * Creates a new cell in the given location representing the given String.
   *
   * @param location where to put the cell
   * @param value    the value to put in the cell
   * @return any WorksheetIds whose value needs to be refreshed
   */
  List<WorksheetId<Coord>> putCell(WorksheetId<Coord> location, String value);

  /**
   * Return if the given cell is invalid.
   *
   * @param location the WorksheetId referring to the location to check
   * @return if the cell is in error
   */
  boolean hasError(WorksheetId<Coord> location);

  /**
   * Removes a cell from a given location.
   *
   * @param location the WorksheetId referring to the location to clear
   * @return the list of cells to be updated
   */
  List<WorksheetId<Coord>> removeCell(WorksheetId<Coord> location);

  /**
   * Creates a new Spreadsheet with the given name, if it doesn't exist.
   * @param sheet the name of the spreadsheet to add
   * @return true if added successfully, false if caused conflict
   */
  boolean addSpreadsheet(String sheet);
}
