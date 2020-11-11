package edu.cs3500.spreadsheets.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A cell representing a range of columns.
 */
public class ColumnReference extends Reference {
  private final String referenceSheet;
  private final String min;
  private final String max;
  private final int minIndex;
  private final int maxIndex;

  /**
   * Instantiate a new ColumnReference referencing the given range.
   *
   * @param context     the CellContext that the reference is within
   * @param coordinates represents the coordinate range of referenced columns
   */
  public ColumnReference(Workbook context, String coordinates, String sheet) {
    super(context);
    if (!isColumnReference(coordinates)) {
      throw new IllegalArgumentException("Improperly formatted coordinate: " + coordinates);
    }
    String[] parts = parseSheet(coordinates, sheet);
    this.referenceSheet = parts[1];
    String[] columns = parts[0].split(":");
    if (columns.length != 2) {
      throw new IllegalArgumentException("This should not be possible.");
    }
    this.min = columns[0];
    this.max = columns[1];
    this.minIndex = Coord.colNameToIndex(min);
    this.maxIndex = Coord.colNameToIndex(max);
    if (this.minIndex > this.maxIndex) {
      throw new IllegalArgumentException("Minimum column greater than maximum column");
    }
  }

  /**
   * Determines whether a string references a range of columns.
   *
   * @param s the string to check
   * @return
   */
  public static boolean isColumnReference(String s) {
    String coordPart = parseSheet(s, "")[0];
    // two column references, separated by ":"
    return coordPart.matches("[A-Z]+:[A-Z]+");
  }

  @Override
  public String getRaw() {
    return min + ":" + max;
  }

  @Override
  protected ICell evaluate(EvaluatableCell origin) {
    ArrayList<ICell> values = new ArrayList<>();
    for (WorksheetId<Integer> col : getColDependencies()) {
      for (ICell cell : context.getColCells(col)) {
        if (cell == origin) {
          return new Error("Circular dependency");
        }
        if (cell instanceof EvaluatableCell) {
          cell = ((EvaluatableCell) cell).getValue(origin);
        }
        if (cell instanceof Error) {
          return cell;
        }
        values.add(cell);
      }
    }
    return new ListCell(values);
  }

  @Override
  public List<WorksheetId<Integer>> getColDependencies() {
    List<WorksheetId<Integer>> colDependencies = new ArrayList<>();
    for (int i = minIndex; i < maxIndex + 1; i++) {
      colDependencies.add(new WorksheetId<>(referenceSheet, i));
    }
    return colDependencies;
  }

  @Override
  public String toString() {
    return this.getRaw();
  }
}
