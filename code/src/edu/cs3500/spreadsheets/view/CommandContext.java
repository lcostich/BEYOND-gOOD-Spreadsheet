package edu.cs3500.spreadsheets.view;

/**
 * Contexts for keyboard commands in a GraphicalView.
 */
public enum CommandContext {
  /**
   * When the window is in focus.
   */
  WINDOW("WINDOW"),
  /**
   * When editing a cell.
   */
  EDIT("EDIT"),
  /**
   * When navigating the spreadsheet, but not editing a cell.
   */
  GRID("GRID");

  private String asString;

  CommandContext(String asString) {
    this.asString = asString;
  }

  /**
   * Returns the CommandContext as a String.
   *
   * @return
   */
  public String toString() {
    return asString;
  }
}
