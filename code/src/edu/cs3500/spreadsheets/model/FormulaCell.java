package edu.cs3500.spreadsheets.model;

import java.util.ArrayList;
import java.util.List;

/**
 * An abstract class representing formulas.
 */
abstract class FormulaCell extends EvaluatableCell {
  private final List<ICell> args;
  private final int expectedArgs;

  protected FormulaCell(List<ICell> args) {
    this(args, -1);
  }

  /**
   * Sets up the argument variable and checks if the number of arguments makes sense.
   *
   * @param args         the list of argument cells
   * @param expectedArgs the number of arguments to expect, or -1 for any number of arguments
   */
  protected FormulaCell(List<ICell> args, int expectedArgs) {
    if (args == null) {
      throw new IllegalArgumentException("Null arguments array");
    }
    this.args = new ArrayList<>(args);
    this.expectedArgs = expectedArgs;

  }

  /**
   * Gets the raw formula.
   *
   * @return the raw formula
   */
  public String getRaw() {
    return "=" + this.toString();
  }

  /**
   * Gets the name of the function.
   *
   * @return the name of the function
   */
  protected abstract String getName();

  /**
   * Produces a String to represent the cell.
   *
   * @return the cell as a String
   */
  public String toString() {
    StringBuilder builder = new StringBuilder("(");
    RawValueVisitor rv = new RawValueVisitor(true);
    builder.append(this.getName());
    for (ICell cell : this.args) {
      builder.append(" ");
      builder.append(cell.accept(rv));
    }
    builder.append(")");
    return builder.toString();
  }

  /**
   * Returns any external coordinates that given evaluable references.
   *
   * @return the list of dependency coordinates
   */
  @Override
  public List<WorksheetId<Coord>> getDependencies() {
    DependencyVisitor dv = new DependencyVisitor(this);
    ArrayList<WorksheetId<Coord>> dependencies = new ArrayList<>();
    for (ICell cell : args) {
      dependencies.addAll(cell.accept(dv));
    }
    return dependencies;
  }

  /**
   * Returns any columns the given evaluable references.
   *
   * @return the list of column dependencies
   */
  @Override
  public List<WorksheetId<Integer>> getColDependencies() {
    ColDependencyVisitor dv = new ColDependencyVisitor(this);
    ArrayList<WorksheetId<Integer>> dependencies = new ArrayList<>();
    for (ICell cell : args) {
      dependencies.addAll(cell.accept(dv));
    }
    return dependencies;
  }

  @Override
  public void clearCache() {
    for (ICell cell : args) {
      if (cell instanceof EvaluatableCell) {
        ((EvaluatableCell) cell).clearCache();
      }
    }
    super.clearCache();
  }

  /**
   * Evaluates the cell's value.
   *
   * @param origin Source cells
   * @return the value of the cell
   */
  @Override
  protected final ICell evaluate(EvaluatableCell origin) {
    ArrayList<ICell> primed = new ArrayList<>(args.size());
    ExpanderVisitor expander = new ExpanderVisitor(origin);
    for (ICell cell : this.args) {
      if (cell instanceof ListCell || cell instanceof EvaluatableCell) {
        for (ICell toAdd : cell.accept(expander)) {
          if (cell instanceof Error) {
            return cell;
          }
          primed.add(toAdd);
        }
      } else if (cell instanceof Error) {
        return cell;
      } else {
        primed.add(cell);
      }
    }
    if (this.expectedArgs != -1 && primed.size() != expectedArgs) {
      return new Error("Expected " + expectedArgs + " arguments; got " + primed.size());
    }
    return this.calculate(primed);
  }

  /**
   * Calculates the cell's value, using the given arguments.
   *
   * @param args the arguments to the function
   * @return the resulting value
   */
  protected abstract ICell calculate(List<ICell> args);

  private class DependencyVisitor extends DefaultCellVisitor<List<WorksheetId<Coord>>> {
    private final EvaluatableCell origin;

    DependencyVisitor(EvaluatableCell origin) {
      super(new ArrayList<>());
      this.origin = origin;
    }

    /**
     * Process an evaluable Cell.
     *
     * @param value the FormulaCell
     * @return the desired result
     */
    @Override
    public List<WorksheetId<Coord>> visitEvaluatableCell(EvaluatableCell value) {
      if (value == origin) {
        // Just ignore self-references for this
        return new ArrayList<>();
      }
      return value.getDependencies();
    }
  }

  private class ColDependencyVisitor extends DefaultCellVisitor<List<WorksheetId<Integer>>> {
    private final EvaluatableCell origin;

    ColDependencyVisitor(EvaluatableCell origin) {
      super(new ArrayList<>());
      this.origin = origin;
    }

    /**
     * Process a formula Cell.
     *
     * @param value the FormulaCell
     * @return the desired result
     */
    @Override
    public List<WorksheetId<Integer>> visitEvaluatableCell(EvaluatableCell value) {
      if (value == origin) {
        // Just ignore self-references for this
        return new ArrayList<>();
      }
      return value.getColDependencies();
    }
  }

  private class ExpanderVisitor implements CellVisitor<List<ICell>> {
    private final EvaluatableCell origin;

    ExpanderVisitor(EvaluatableCell origin) {
      this.origin = origin;
    }

    /**
     * Process a boolean Cell.
     *
     * @param value the BooleanCell
     * @return the desired result
     */
    @Override
    public List<ICell> visitBooleanCell(boolean value) {
      return makeList(new BooleanCell(value));
    }

    /**
     * Process a number Cell.
     *
     * @param value the NumberCell
     * @return the desired result
     */
    @Override
    public List<ICell> visitNumberCell(double value) {
      return makeList(new NumberCell(value));

    }

    /**
     * Process a list Cell.
     *
     * @param value the ListCell
     * @return the desired result
     */
    @Override
    public List<ICell> visitListCell(List<ICell> value) {
      return value;
    }

    /**
     * Process a string Cell.
     *
     * @param value the StringCell
     * @return the desired result
     */
    @Override
    public List<ICell> visitStringCell(String value) {
      return makeList(new StringCell(value));
    }

    /**
     * Process a formula Cell.
     *
     * @param value the FormulaCell
     * @return the desired result
     */
    @Override
    public List<ICell> visitEvaluatableCell(EvaluatableCell value) {
      if (value == origin) {
        System.out.println("Evaluated cell to error: Circular Dependency");
        return new Error("Circular dependency").accept(this);
      }
      return value.getValue(origin).accept(this);
    }

    /**
     * Process an empty cell.
     *
     * @return the desired result
     */
    @Override
    public List<ICell> visitEmptyCell() {
      return new ArrayList<>();
    }

    /**
     * Process an error cell.
     *
     * @param message the error message
     * @return the desired result
     */
    @Override
    public List<ICell> visitErrorCell(String message) {
      return makeList(new Error(message));
    }

    private ArrayList<ICell> makeList(ICell value) {
      ArrayList<ICell> list = new ArrayList<>(1);
      list.add(value);
      return list;
    }
  }
}