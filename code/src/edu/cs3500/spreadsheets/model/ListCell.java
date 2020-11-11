package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * A value cell that contains a String.
 */
public class ListCell implements ICell {
  public final List<ICell> value;

  /**
   * Instantiates a new String cell with the given value. Assumes the value is immutable.
   *
   * @param value the value of the cell
   */
  public ListCell(List<ICell> value) {
    this.value = value;
  }

  /**
   * Get this cell's value as a list.
   *
   * @return
   */
  public List<ICell> getValue() {
    return this.value;
  }

  @Override
  public <T> T accept(CellVisitor<T> visitor) {
    return visitor.visitListCell(this.value);
  }
}