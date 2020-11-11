package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * Visitor implementation to visit a Value.
 */
public class ValueVisitor implements CellVisitor<IReadableCell> {

  /**
   * Process a boolean Cell.
   *
   * @param value the BooleanCell
   * @return the desired result
   */
  @Override
  public IReadableCell visitBooleanCell(boolean value) {
    return new BooleanCell(value);
  }

  /**
   * Process a number Cell.
   *
   * @param value the NumberCell
   * @return the desired result
   */
  @Override
  public IReadableCell visitNumberCell(double value) {
    return new NumberCell(value);
  }

  /**
   * Process a list Cell.
   *
   * @param value the ListCell
   * @return the desired result
   */
  @Override
  public IReadableCell visitListCell(List<ICell> value) {
    return new Error("Exposed list");
  }

  /**
   * Process a string Cell.
   *
   * @param value the StringCell
   * @return the desired result
   */
  @Override
  public IReadableCell visitStringCell(String value) {
    return new StringCell(value);
  }

  /**
   * Process a formula Cell.
   *
   * @param value the FormulaCell
   * @return the desired result
   */
  @Override
  public IReadableCell visitEvaluatableCell(EvaluatableCell value) {
    return value.getValue().accept(this);
  }

  /**
   * Process an empty cell.
   *
   * @return the desired result
   */
  @Override
  public IReadableCell visitEmptyCell() {
    return new EmptyCell();
  }

  /**
   * Process an error cell.
   *
   * @param message the error's message
   * @return the desired result
   */
  @Override
  public IReadableCell visitErrorCell(String message) {
    return new Error(message);
  }
}
