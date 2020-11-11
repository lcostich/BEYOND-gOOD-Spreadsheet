package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * A cell visitor with a default return value.
 * @param <T> the return type
 */
public class DefaultCellVisitor<T> implements CellVisitor<T> {
  private final T defaultItem;

  /**
   * Initializes a new DefaultCellVisitor.
   * @param defaultItem the default
   */
  public DefaultCellVisitor(T defaultItem) {
    this.defaultItem = defaultItem;
  }

  /**
   * Process a boolean Cell.
   *
   * @param value the BooleanCell
   * @return the desired result
   */
  @Override
  public T visitBooleanCell(boolean value) {
    return defaultItem;
  }

  /**
   * Process a number Cell.
   *
   * @param value the NumberCell
   * @return the desired result
   */
  @Override
  public T visitNumberCell(double value) {
    return defaultItem;
  }

  /**
   * Process a string Cell.
   *
   * @param value the StringCell
   * @return the desired result
   */
  @Override
  public T visitStringCell(String value) {
    return defaultItem;
  }

  /**
   * Process a formula Cell.
   *
   * @param value the FormulaCell
   * @return the desired result
   */
  @Override
  public T visitEvaluatableCell(EvaluatableCell value) {
    return defaultItem;
  }

  /**
   * Process an empty cell.
   *
   * @return the desired result
   */
  @Override
  public T visitEmptyCell() {
    return defaultItem;
  }

  /**
   * Process an error cell.
   *
   * @param message the error message
   * @return the desired result
   */
  @Override
  public T visitErrorCell(String message) {
    return defaultItem;
  }

  /**
   * Process a list Cell.
   *
   * @param value the ListCell
   * @return the desired result
   */
  @Override
  public T visitListCell(List value) {
    return defaultItem;
  }
}
