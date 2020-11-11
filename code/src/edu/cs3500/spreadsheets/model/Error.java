package edu.cs3500.spreadsheets.model;

/**
 * A cell representing some sort of error.
 */
public class Error implements IReadableCell {
  private final String message;

  /**
   * Instantiates a new Error cell with the given error message.
   *
   * @param message a String representing the error message
   */
  public Error(String message) {
    this.message = message;
  }

  @Override
  public <T> T accept(CellVisitor<T> visitor) {
    return visitor.visitErrorCell(message);
  }

  /**
   * Accept a Cell Reader visitor.
   *
   * @param reader the reader
   * @return the resulting value
   */
  @Override
  public <T> T accept(ICellReader<T> reader) {
    return reader.visitErrorCell(this.message);
  }
}
