package edu.cs3500.spreadsheets.model;

/**
 * Reader for ICells.
 *
 * @param <T> the return type
 */
public interface ICellReader<T> {
  /**
   * Visit a boolean cell.
   *
   * @param value the cell value
   * @return
   */
  T visitBooleanCell(boolean value);

  /**
   * Visit a number cell.
   *
   * @param value the cell value
   * @return
   */
  T visitNumberCell(double value);

  /**
   * Visit a string cell.
   *
   * @param value the cell value
   * @return
   */
  T visitStringCell(String value);

  /**
   * Visit an empty cell.
   *
   * @return
   */
  T visitEmptyCell();

  /**
   * Visit an error cell.
   *
   * @param message the error message
   * @return
   */
  T visitErrorCell(String message);
}
