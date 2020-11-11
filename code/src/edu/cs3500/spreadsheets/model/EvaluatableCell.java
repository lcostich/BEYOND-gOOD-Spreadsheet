package edu.cs3500.spreadsheets.model;

import java.util.List;

/**
 * An abstract class representing cells that need to be evaluated.
 */
public abstract class EvaluatableCell implements ICell {
  private ICell cache;

  public abstract String getRaw();

  /**
   * Accepts a CellVisitor.
   *
   * @param visitor the visitor
   * @param <T>     the type that the visitor returns
   * @return whatever the visitor returns
   */
  public <T> T accept(CellVisitor<T> visitor) {
    return visitor.visitEvaluatableCell(this);
  }

  /**
   * Returns any external coordinates that given evaluable references.
   *
   * @return the list of dependency coordinates
   */
  public abstract List<WorksheetId<Coord>> getDependencies();

  /**
   * Returns any columns the given evaluable references.
   *
   * @return the list of column dependencies
   */
  public abstract List<WorksheetId<Integer>> getColDependencies();

  /**
   * Gets the effective value of the cell.
   *
   * @return the cell's effective value
   */
  public final ICell getValue() {
    return this.getValue(this);
  }

  /**
   * Gets the effective value of the cell.
   *
   * @param origin the original cell (for checking circular dependency)
   * @return the cell's effective value
   */
  public final ICell getValue(EvaluatableCell origin) {
    if (this.cache != null) {
      return this.cache;
    }
    try {
      this.cache = this.evaluate(origin);
      return this.cache;
    } catch (Exception e) {
      return new Error(e.getMessage());
    }
  }

  /**
   * Evaluate the cell's value. Dependencies should be resolved by calling evaluate(origin) instead
   * of getValue().
   *
   * @param origin Source cells
   * @return the cell's value
   */
  protected abstract ICell evaluate(EvaluatableCell origin);

  public void clearCache() {
    this.cache = null;
  }
}
