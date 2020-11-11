package edu.cs3500.spreadsheets.model;

/**
 * Interface for user-exposed cells.
 */
public interface IReadableCell extends ICell {
  /**
   * Accept a Cell Reader visitor.
   *
   * @param reader the reader
   * @param <T>    the type to return
   * @return the resulting value
   */
  <T> T accept(ICellReader<T> reader);
}
