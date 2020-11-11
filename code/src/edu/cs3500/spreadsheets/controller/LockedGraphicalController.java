package edu.cs3500.spreadsheets.controller;

import edu.cs3500.spreadsheets.model.Workbook;
import edu.cs3500.spreadsheets.view.GraphicalView;
import edu.cs3500.spreadsheets.view.SimpleGraphicalView;

/**
 * Creates and controls a locked graphical UI for the given spreadsheet.
 * Mouse-based selection works as usual, but any requested edits do not go through.
 */
public class LockedGraphicalController {
  GraphicalView view;

  /**
   * Instantiates a LockedGraphicalController for the given Spreadsheet.
   * @param wb the Spreadsheet to view
   */
  public LockedGraphicalController(Workbook wb) {
    view = new SimpleGraphicalView(wb);
  }
}
