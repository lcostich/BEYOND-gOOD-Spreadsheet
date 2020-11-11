package edu.cs3500.spreadsheets.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A representation of a Spreadsheet, serving as a context for cells.
 */
public class CellContext implements Spreadsheet {
  private CellGrid<ICell> cells;
  private Map<Coord, String> rawValues;
  private ValueVisitor effective;

  /**
   * Instantiate a new CellContext.
   */
  public CellContext() {
    this.cells = new CellGrid<>();
    this.rawValues = new HashMap<>();
    this.effective = new ValueVisitor();
    RawValueVisitor raw = new RawValueVisitor();
  }

  /**
   * Returns the concrete cell at the given location.
   * @param coordinate the location to query
   * @return the cell at the given location
   */
  @Override
  public ICell getCellAt(Coord coordinate) {
    return cells.getOrDefault(coordinate, new EmptyCell());
  }

  @Override
  public List<ICell> getColCells(Integer col) {
    return new ArrayList<>(this.cells.getCol(col));
  }

  @Override
  public void putCell(Coord location, ICell toAdd, String rawString) {
    // Put the cell in its place.
    this.cells.putCell(location, toAdd);
    this.rawValues.put(location, rawString);
  }

  /**
   * Return the cell's value at the given position. Formulas and references are evaluated.
   *
   * @param location the location of the requested cell
   * @return the value of the cell
   */
  @Override
  public IReadableCell getValue(Coord location) {
    return this.cells.getOrDefault(location, new EmptyCell())
            .accept(this.effective);
  }

  /**
   * Retrieve the unevaluated value at the given location. Formulas and references are shown as-is.
   *
   * @param location the location of the cell
   * @return the raw value of the cell
   */
  @Override
  public String getRaw(Coord location) {
    return this.rawValues.get(location);
  }

  /**
   * Return if the given cell is invalid.
   *
   * @param location the location to check
   * @return if the cell is in error
   */
  @Override
  public boolean hasError(Coord location) {
    return this.getValue(location) instanceof Error;
  }

  @Override
  public List<Coord> getNonEmptyCoords() {
    return new ArrayList<>(this.cells.getLocationSet());
  }

  /**
   * Removes a cell from a given location.
   *
   * @param location the location to clear
   */
  @Override
  public void removeCell(Coord location) {
    if (this.cells.hasContents(location)) {
      this.cells.removeCell(location);
      this.rawValues.remove(location);
    }
  }

  /**
   * Checks to see if there are cell values at the given location Coord.
   * @param location the coord to be checked
   * @return whether or not the coord has a value
   */
  @Override
  public boolean hasContents(Coord location) {
    return this.cells.hasContents(location);
  }
}
