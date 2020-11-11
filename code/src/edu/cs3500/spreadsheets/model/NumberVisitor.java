package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * Visitor implementation to visit a NumberCell.
 */
public class NumberVisitor implements CellVisitor<Double> {
  private final double defaultValue;
  private int numsEncountered;

  public NumberVisitor(double defaultValue) {
    this.defaultValue = defaultValue;
    this.numsEncountered = 0;
  }

  /**
   * Process a boolean Cell.
   *
   * @param value the BooleanCell
   * @return the desired result
   */
  @Override
  public Double visitBooleanCell(boolean value) {
    return defaultValue;
  }

  /**
   * Process a number Cell.
   *
   * @param value the NumberCell
   * @return the desired result
   */
  @Override
  public Double visitNumberCell(double value) {
    numsEncountered++;
    return value;
  }

  /**
   * Process a list Cell.
   *
   * @param value the ListCell
   * @return the desired result
   */
  @Override
  public Double visitListCell(List<ICell> value) {
    return defaultValue;
  }

  /**
   * Process a string Cell.
   *
   * @param value the StringCell
   * @return the desired result
   */
  @Override
  public Double visitStringCell(String value) {
    return defaultValue;
  }

  /**
   * Process a formula Cell.
   *
   * @param value the FormulaCell
   * @return the desired result
   */
  @Override
  public Double visitEvaluatableCell(EvaluatableCell value) {
    return defaultValue;
  }

  /**
   * Process an empty cell.
   *
   * @return the desired result
   */
  @Override
  public Double visitEmptyCell() {
    return defaultValue;
  }

  /**
   * Process an error cell.
   *
   * @param message String representing error
   * @return the desired result
   */
  @Override
  public Double visitErrorCell(String message) {
    throw new IllegalArgumentException(message);
  }

  public int getNumsEncountered() {
    return this.numsEncountered;
  }
}
