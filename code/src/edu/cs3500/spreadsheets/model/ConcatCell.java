package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * FormulaCell representing a LessThan function.
 */
public class ConcatCell extends FormulaCell {


  public ConcatCell(List<ICell> args) {
    super(args);
  }

  /**
   * Gets the name of the function as a prefix.
   *
   * @return
   */
  @Override
  protected String getName() {
    return "CONCAT";
  }

  /**
   * Evaluate the formula's value. Dependencies should be resolved by calling evaluate(origin)
   * instead of getValue().
   *
   * @param args The arguments for the item.
   * @return
   */
  @Override
  protected ICell calculate(List<ICell> args) throws IllegalArgumentException {
    StringVisitor visitor = new StringVisitor();
    StringBuilder builder = new StringBuilder();
    for (ICell cell : args) {
      builder.append(cell.accept(visitor).replaceAll("\"", ""));
    }
    return new StringCell(builder.toString());
  }
}
