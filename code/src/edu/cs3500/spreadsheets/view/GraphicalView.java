package edu.cs3500.spreadsheets.view;

import java.awt.event.ActionListener;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.WorksheetId;

/**
 * The interface representing all GraphicalViews of a Spreadsheet.
 */
public interface GraphicalView extends SpreadsheetView {
  void addKeyboardAction(String shortcut, ActionListener command, CommandContext context);

  /**
   * Returns the edited text of the selected cell.
   * @return the edited cell contents.
   */
  String getEditText();

  /**
   * Cancels an edit, and moves focus back to the grid.
   */
  void cancelEdit();

  /**
   * Adds an object listening to the confirm button.
   * @param listener the listening object
   */
  void registerConfirmListener(ActionListener listener);

  /**
   * Adds an object listening to the cancel button.
   * @param listener the listening object
   */
  void registerCancelListener(ActionListener listener);

  /**
   * Change the selected coordinates in the given direction.
   * @param d the direction to move
   */
  void moveSelect(Direction d);

  /**
   * Moves the focus to the selected cell.
   */
  void focusOnGrid();

  /**
   * Begins editing the selected cell.
   */
  void editMode();

  /**
   * Returns the coordinate of the selected cell.
   * @return the selected coordinates
   */
  WorksheetId<Coord> getSelection();

  /**
   * Returns the mock as a String, never used outside of testing.
   * @return the appendable as a String.
   */
  String returnAsString();
}
