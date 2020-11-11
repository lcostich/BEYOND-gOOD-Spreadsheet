package edu.cs3500.spreadsheets.model;

/**
 * Visitor implementation to visit a RawValue.
 */
public class RawValueVisitor extends StringVisitor {
  boolean internal;

  public RawValueVisitor() {
    this(false);
  }

  public RawValueVisitor(boolean internal) {
    this.internal = internal;
  }

  /**
   * Process a formula Cell.
   *
   * @param value the FormulaCell
   * @return the desired result
   */
  @Override
  public String visitEvaluatableCell(EvaluatableCell value) {
    if (internal) {
      return value.toString();
    } else {
      return value.getRaw();
    }
  }
}
