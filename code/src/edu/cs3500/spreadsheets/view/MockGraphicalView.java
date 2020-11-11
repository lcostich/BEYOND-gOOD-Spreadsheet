package edu.cs3500.spreadsheets.view;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashMap;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IReadableCell;
import edu.cs3500.spreadsheets.model.WorksheetId;

/**
 * Mock GraphicalView intended for testing the controller.
 */
public class MockGraphicalView implements GraphicalView {
  private final Appendable ap;
  private final Appendable window;
  private final Appendable editBar;
  private final Appendable grid;
  private final HashMap<String, ArrayList<ActionListener>> eventListeners;
  private WorksheetId<Coord> selected;
  private String editText;

  /**
   * Constructor for a MockGraphicalView.
   *
   * @param ap the Appendable to confirm controller activity
   */
  public MockGraphicalView(Appendable ap) {
    this.ap = ap;
    this.window = new StringBuilder();
    this.editBar = new StringBuilder();
    this.grid = new StringBuilder();
    this.eventListeners = new HashMap<>();
    this.selected = new WorksheetId<>("Spreadsheet", new Coord(1, 1));
    this.editText = "";
  }

  @Override
  public void addKeyboardAction(String shortcut, ActionListener command, CommandContext context) {
    try {
      String key = "";
      switch (context) {
        case WINDOW:
          window.append("Received a window command");
          key = "WINDOW";
          break;
        case EDIT:
          this.editBar.append("Received an edit command");
          key = "EDIT";
          break;
        case GRID:
          this.grid.append("Received a grid command");
          key = "GRID";
          break;
        default:
          // Necessary for well-roundedness, but will never reach the default case
          break;
      }
      addListener(key + ":" + shortcut, command);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public String getEditText() {
    try {
      ap.append("Received getEditText() request");

    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return this.editText;
  }

  @Override
  public void cancelEdit() {
    try {
      this.editText = "";
      ap.append("Received cancelEdit() request");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void registerConfirmListener(ActionListener listener) {
    try {
      ap.append("Received registerConfirmListener() request");
      addListener("confirm", listener);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void registerCancelListener(ActionListener listener) {
    try {
      ap.append("Received registerCancelListener() request");
      addListener("cancel", listener);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void moveSelect(Direction d) {
    try {
      ap.append("Received moveSelect() request for Direction " + d.toString());
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void focusOnGrid() {
    try {
      ap.append("Received focusOnGrid() request");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void editMode() {
    try {
      ap.append("Received editMode() request");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public WorksheetId<Coord> getSelection() {
    try {
      ap.append("Received getSelection() request");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    return this.selected;
  }

  /**
   * Trigger a keyboard event in the given context.
   *
   * @param keypress the key to press
   * @param context  what's in focus
   */
  public void triggerKeyEvent(String keypress, CommandContext context) {
    this.triggerEvent(context.toString() + ":" + keypress);

  }

  /**
   * Simulate pressing the confirm button.
   */
  public void triggerConfirm() {
    this.triggerEvent("confirm");
  }

  /**
   * Simulate pressing the cancel button.
   */
  public void triggerCancel() {
    this.triggerEvent("cancel");
  }

  /**
   * Trigger all ActionListeners associated with the given key.
   *
   * @param key the key to trigger.
   */
  private void triggerEvent(String key) {
    ActionEvent event = new ActionEvent(this, ActionEvent.ACTION_PERFORMED, key);
    for (ActionListener listener : this.eventListeners.getOrDefault(key, new ArrayList<>())) {
      listener.actionPerformed(event);
    }
  }

  @Override
  public String returnAsString() {
    return ap.toString();
  }

  /**
   * Register a command.
   *
   * @param key     the key of the command formatted as [CONTEXT]:[KEY]
   * @param command the action to perform when called
   */
  private void addListener(String key, ActionListener command) {
    if (!eventListeners.containsKey(key)) {
      eventListeners.put(key, new ArrayList<>());
    }
    eventListeners.get(key).add(command);
  }

  /**
   * Select a specific Coord for testing purposes.
   *
   * @param toSelect the Coord to select.
   */
  public void select(WorksheetId<Coord> toSelect) {
    this.selected = toSelect;
  }

  /**
   * Sets the contents of the mock edit bar.
   *
   * @param editText the contents to put in the edit bar.
   */
  public void setEditText(String editText) {
    this.editText = editText;
  }

  @Override
  public void drawCell(WorksheetId<Coord> coord, IReadableCell value) {
    try {
      ap.append("Received drawCell() request");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }

  @Override
  public void drawRaw(WorksheetId<Coord> coord, String raw) {
    try {
      ap.append("Received drawRaw() request");
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }
}
