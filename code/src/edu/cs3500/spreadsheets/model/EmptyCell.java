package edu.cs3500.spreadsheets.model;

/**
 * To represent an empty cell.
 */
public class EmptyCell implements IReadableCell {
  @Override
  public <T> T accept(CellVisitor<T> visitor) {
    return visitor.visitEmptyCell();
  }

  /**
   * Accept a Cell Reader visitor.
   *
   * @param reader the reader
   * @return the resulting value
   */
  @Override
  public <T> T accept(ICellReader<T> reader) {
    return reader.visitEmptyCell();
  }
}
