package edu.cs3500.spreadsheets.model;

/**
 * A value cell that contains a boolean.
 */
public class BooleanCell extends AValue<Boolean> {

  /**
   * Instantiates a new boolean cell with the given value. Assumes the value is immutable.
   *
   * @param value the value of the cell
   */
  public BooleanCell(boolean value) {
    this.value = value;
  }

  /**
   * Returns This BooleanCell's value as a String.
   *
   * @return String
   */
  public String toString() {
    if (value) {
      return "true";
    }
    return "false";
  }

  @Override
  public Boolean getValue() {
    return this.value;
  }

  /**
   * Accept a Cell Reader visitor.
   *
   * @param reader the reader
   * @return the resulting value
   */
  @Override
  public <T> T accept(ICellReader<T> reader) {
    return reader.visitBooleanCell(this.value);
  }

  @Override
  public <T> T accept(CellVisitor<T> visitor) {
    return visitor.visitBooleanCell(this.value);
  }
}
