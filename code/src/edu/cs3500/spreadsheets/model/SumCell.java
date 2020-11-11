package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * FormulaCell representing a Sum function.
 */
public class SumCell extends FormulaCell {


  public SumCell(List<ICell> args) {
    super(args);
  }

  /**
   * Gets the name of the function as a prefix.
   *
   * @return
   */
  @Override
  protected String getName() {
    return "SUM";
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
    NumberVisitor visitor = new NumberVisitor(0);
    double sum = 0;
    for (ICell cell : args) {
      sum += cell.accept(visitor);
    }
    return new NumberCell(sum);
  }
}
