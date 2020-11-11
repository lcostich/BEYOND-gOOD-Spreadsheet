package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * FormulaCell representing a Product function.
 */
public class ProductCell extends FormulaCell {


  public ProductCell(List<ICell> args) {
    super(args);
  }

  /**
   * Gets the name of the function as a prefix.
   * @return
   */
  @Override
  protected String getName() {
    return "PRODUCT";
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
    NumberVisitor visitor = new NumberVisitor(1);
    double sum = 1;
    for (ICell cell: args) {
      sum *= cell.accept(visitor);
    }
    if (visitor.getNumsEncountered() == 0) {
      return new NumberCell(0);
    }
    return new NumberCell(sum);
  }
}
