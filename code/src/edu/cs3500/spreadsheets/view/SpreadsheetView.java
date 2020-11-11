package edu.cs3500.spreadsheets.view;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IReadableCell;
import edu.cs3500.spreadsheets.model.WorksheetId;

/**
 * Interface to represent views of a Spreadsheet.
 */
public interface SpreadsheetView {
  /**
   * To draw a single cell.
   *
   * @param coord the location of the cell to be drawn
   * @param value the IReadableCell to be drawn
   */
  void drawCell(WorksheetId<Coord> coord, IReadableCell value);

  /**
   * To draw the raw value of a cell.
   *
   * @param coord the location of the cell with raw value to be drawn
   * @param raw   the String to be drawn
   */
  void drawRaw(WorksheetId<Coord> coord, String raw);
}
