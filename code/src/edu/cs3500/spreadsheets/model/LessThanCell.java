package edu.cs3500.spreadsheets.model;

import java.util.ArrayList;
import java.util.List;

/**
 * FormulaCell representing a LessThan function.
 */
public class LessThanCell extends FormulaCell {


  public LessThanCell(List<ICell> args) {
    super(args, 2);
  }

  /**
   * Gets the name of the function as a prefix.
   *
   * @return
   */
  @Override
  protected String getName() {
    return "<";
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
    ArrayList<Double> valid = new ArrayList<>();
    for (ICell cell : args) {
      valid.add(cell.accept(visitor));
    }
    if (valid.size() != 2) {
      return new Error("Expected 2 arguments; got " + valid.size());
    }
    return new BooleanCell(valid.get(0) < valid.get(1));
  }
}
