package edu.cs3500.spreadsheets.model;

import java.util.ArrayList;
import java.util.List;

/**
 * A cell referencing a range of cells.
 */
public final class MultiReference extends Reference {
  private final String referenceSheet;
  private final Coord min;
  private final Coord max;

  /**
   * Instantiate a new MultiReference referencing the given range.
   *
   * @param context     the CellContext that the reference is within
   * @param coordinates represents the coordinate range of referenced cells
   */
  public MultiReference(Workbook context, String coordinates, String sheet) {
    super(context);
    if (!isMultiReference(coordinates)) {
      throw new IllegalArgumentException("Improperly formatted coordinate: " + coordinates);
    }
    String[] parts = super.parseSheet(coordinates, sheet);
    this.referenceSheet = parts[1];
    String[] cells = parts[0].split(":");
    if (cells.length != 2) {
      throw new IllegalArgumentException("This should not be possible.");
    }
    this.min = super.parse(cells[0]);
    this.max = super.parse(cells[1]);
    if (this.min.col > this.max.col) {
      throw new IllegalArgumentException("Minimum column greater than maximum column");
    }
    if (this.min.row > this.max.row) {
      throw new IllegalArgumentException("Minimum row greater than maximum row");
    }
  }

  /**
   * Determines whether a string references a range of cells.
   *
   * @param s the string to check
   * @return
   */
  public static boolean isMultiReference(String s) {
    String coordPart = parseSheet(s, "")[0];
    // two single references, separated by ":"
    return coordPart.matches("[A-Z]+[0-9]+:[A-Z]+[0-9]+");
  }

  @Override
  public String getRaw() {
    return min.toString() + ":" + max.toString();
  }

  /**
   * Evaluate the formula's value. Dependencies should be resolved by calling evaluate(origin)
   * instead of getValue().
   *
   * @param origin Source cells
   * @return the linked value
   */
  @Override
  protected ICell evaluate(EvaluatableCell origin) {
    ArrayList<ICell> values = new ArrayList<>();
    for (WorksheetId<Coord> c : getDependencies()) {
      try {
        ICell toAdd = super.retrieve(c, origin);
        if (toAdd instanceof Error) {
          return toAdd;
        }
        values.add(toAdd);
      } catch (IllegalArgumentException e) {
        return new Error(e.getMessage());
      }
    }
    return new ListCell(values);
  }

  @Override
  public List<WorksheetId<Coord>> getDependencies() {
    ArrayList<WorksheetId<Coord>> coordinates = new ArrayList<>();
    for (int col = min.col; col <= max.col; col++) {
      for (int row = min.row; row <= max.row; row++) {
        coordinates.add(new WorksheetId<>(this.referenceSheet, new Coord(col, row)));
      }
    }
    return coordinates;
  }

  @Override
  public String toString() {
    return this.getRaw();
  }
}
