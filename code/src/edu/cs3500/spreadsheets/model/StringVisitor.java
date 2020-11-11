package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * Converts a Cell into a String.
 */
public class StringVisitor implements CellVisitor<String>, ICellReader<String> {
  /**
   * Process a boolean Cell.
   *
   * @param value the BooleanCell
   * @return the desired result
   */
  @Override
  public String visitBooleanCell(boolean value) {
    if (value) {
      return "true";
    }
    return "false";
  }

  /**
   * Process a number Cell.
   *
   * @param value the NumberCell
   * @return the desired result
   */
  @Override
  public String visitNumberCell(double value) {
    return String.format("%f", value);
  }

  /**
   * Process a list Cell.
   *
   * @param value the ListCell
   * @return the desired result
   */
  @Override
  public String visitListCell(List<ICell> value) {
    StringBuilder builder = new StringBuilder("(");
    for (ICell cell : value) {
      if (!(cell instanceof EmptyCell)) {
        // Ignore empty cells
        builder.append(cell.accept(this));
        builder.append(" ");
      }
    }
    builder.deleteCharAt(builder.length() - 1);
    builder.append(")");
    return builder.toString();
  }

  /**
   * Process a string Cell.
   *
   * @param value the StringCell
   * @return the desired result
   */
  @Override
  public String visitStringCell(String value) {
    return "\"" + value + "\"";
  }

  /**
   * Process a formula Cell.
   *
   * @param value the FormulaCell
   * @return the desired result
   */
  @Override
  public String visitEvaluatableCell(EvaluatableCell value) {
    return value.getValue().accept(this);
  }

  /**
   * Process an empty cell.
   *
   * @return the desired result
   */
  @Override
  public String visitEmptyCell() {
    return "";
  }

  /**
   * Process an error cell.
   *
   * @param message the error message
   * @return the desired result
   */
  @Override
  public String visitErrorCell(String message) {
    return "ERROR: " + message;
  }
}
