package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * Read-only interface for Spreadsheets.
 */
public interface ReadableSpreadsheet {
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
   * Gets a list of non-empty coords within the Spreadsheet.
   * @return the list of Coords
   */
  List<Coord> getNonEmptyCoords();

  /**
   * Checks to see if there are cell values at the given location Coord.
   * @param location the coord to be checked
   * @return whether or not the coord has a value
   */
  boolean hasContents(Coord location);
}
