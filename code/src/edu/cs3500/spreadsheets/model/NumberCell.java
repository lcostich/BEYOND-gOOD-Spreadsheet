package edu.cs3500.spreadsheets.model;

/**
 * A value cell that contains a double.
 */
public class NumberCell extends AValue<Double> {

  /**
   * Instantiates a new number cell with the given value. Assumes the value is immutable.
   *
   * @param value the value of the cell
   */
  public NumberCell(double value) {
    this.value = value;
  }

  @Override
  public Double getValue() {
    return this.value;
  }

  @Override
  public <T> T accept(CellVisitor<T> visitor) {
    return visitor.visitNumberCell(this.value);
  }

  /**
   * Accept a Cell Reader visitor.
   *
   * @param reader the reader
   * @return the resulting value
   */
  @Override
  public <T> T accept(ICellReader<T> reader) {
    return reader.visitNumberCell(this.value);
  }
}