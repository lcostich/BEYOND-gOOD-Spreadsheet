package edu.cs3500.spreadsheets.model;

/**
 * A value cell that contains a String.
 */
public class StringCell extends AValue<String> {

  /**
   * Instantiates a new String cell with the given value. Assumes the value is immutable.
   *
   * @param value the value of the cell
   */
  public StringCell(String value) {
    this.value = value;
  }

  @Override
  public String getValue() {
    return this.value;
  }

  @Override
  public <T> T accept(CellVisitor<T> visitor) {
    return visitor.visitStringCell(this.value);
  }

  /**
   * Accept a Cell Reader visitor.
   *
   * @param reader the reader
   * @return the resulting value
   */
  @Override
  public <T> T accept(ICellReader<T> reader) {
    return reader.visitStringCell(this.value);
  }
}