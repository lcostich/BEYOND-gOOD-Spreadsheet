package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * An abstracted function object for processing any Cell Values.
 *
 * @param <T> The return type of this function
 */
public interface CellVisitor<T> extends ICellReader<T> {

  /**
   * Process a boolean Cell.
   *
   * @param value the BooleanCell
   * @return the desired result
   */
  T visitBooleanCell(boolean value);

  /**
   * Process a number Cell.
   *
   * @param value the NumberCell
   * @return the desired result
   */
  T visitNumberCell(double value);

  /**
   * Process a list Cell.
   *
   * @param value the ListCell
   * @return the desired result
   */
  T visitListCell(List<ICell> value);

  /**
   * Process a string Cell.
   *
   * @param value the StringCell
   * @return the desired result
   */
  T visitStringCell(String value);

  /**
   * Process a formula Cell.
   *
   * @param value the FormulaCell
   * @return the desired result
   */
  T visitEvaluatableCell(EvaluatableCell value);

  /**
   * Process an empty cell.
   *
   * @return the desired result
   */
  T visitEmptyCell();

  /**
   * Process an error cell.
   *
   * @return the desired result
   */
  T visitErrorCell(String message);
}
