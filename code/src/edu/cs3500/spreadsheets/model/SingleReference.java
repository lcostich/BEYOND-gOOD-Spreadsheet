package edu.cs3500.spreadsheets.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Class representing a single Reference.
 */
final class SingleReference extends Reference {
  private final WorksheetId<Coord> link;

  /**
   * Instantiate a new SingleReference referencing the given coordinate.
   *
   * @param context the CellContext containing the reference
   * @param cell    represents the coordinates of the referenced cell
   */
  public SingleReference(Workbook context, String cell, String sheet) {
    super(context);
    if (!isSingleReference(cell)) {
      throw new IllegalArgumentException("Expected a reference to a single cell. Got " + cell);
    }
    this.link = super.parse(cell, sheet);
  }

  /**
   * Determines whether a string references a single cell.
   *
   * @param s the string to check
   * @return
   */
  public static boolean isSingleReference(String s) {
    String coordPart = parseSheet(s, "")[0];
    // two single references, separated by ":"
    return isReference(coordPart);
  }


  @Override
  public String getRaw() {
    return link.toString();
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
    try {
      ICell thing = super.retrieve(this.link, origin);
      return thing;
    } catch (IllegalArgumentException e) {
      return new Error(e.getMessage());
    }
  }

  @Override
  public List<WorksheetId<Coord>> getDependencies() {
    ArrayList<WorksheetId<Coord>> coords = new ArrayList<>();
    coords.add(link);
    return coords;
  }

  @Override
  public String toString() {
    return this.getRaw();
  }
}
