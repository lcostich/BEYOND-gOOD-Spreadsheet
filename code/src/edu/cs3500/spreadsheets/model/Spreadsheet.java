package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * Interface to represent all functionality of a Spreadsheet.
 */
public interface Spreadsheet extends ReadableSpreadsheet {
  /**
   * Returns the concrete cell at the given location.
   *
   * @param coordinate the location to query
   * @return the cell at the given location
   */
  ICell getCellAt(Coord coordinate);

  /**
   * Returns the list of non-empty cells within a given column.
   *
   * @param col the column to be retrieved
   * @return the list of cells in that column
   */
  List<ICell> getColCells(Integer col);

  /**
   * Creates a new cell in the given location representing the given String.
   *
   * @param location where to put the cell
   * @param value    the value to put in the cell
   * @param rawString the raw value of the cell
   */
  void putCell(Coord location, ICell value, String rawString);

  /**
   * Evaluate and return the cell's value at the given position. Formulas and references are
   * evaluated.
   *
   * @param location the cell to query
   * @return the value of the cell
   */
  IReadableCell getValue(Coord location);

  /**
   * Retrieve the unevaluated value at the given location. Formulas and references are shown as-is.
   *
   * @param location the cell location
   * @return the raw value of the cell
   */
  String getRaw(Coord location);

  /**
   * Return if the given cell is invalid.
   *
   * @param location the location to check
   * @return if the cell is in error
   */
  boolean hasError(Coord location);

  /**
   * Gets a list of non-empty coords within the Spreadsheet.
   *
   * @return the list of Coords
   */
  List<Coord> getNonEmptyCoords();

  /**
   * Removes a cell from a given location.
   */
  void removeCell(Coord location);
}
