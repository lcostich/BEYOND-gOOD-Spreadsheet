package edu.cs3500.spreadsheets.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

/**
 * Isolates the representation of a grid of cells.
 * @param <T> the type of data to store
 */
public class CellGrid<T> {

  private HashMap<Coord, T> grid;


  /**
   * Initialize a CellGrid.
   */
  public CellGrid() {
    this.grid = new HashMap<>();
  }

  /**
   * Puts a value in the given location.
   *
   * @param loc   the location to place the value.
   * @param value the value to place in the grid.
   * @return the previous value.
   */
  public T putCell(Coord loc, T value) {
    return grid.put(loc, value);
  }

  /**
   * Clears the given location.
   *
   * @param loc the location to clear.
   * @return the previous value.
   */
  public T removeCell(Coord loc) {
    return grid.remove(loc);
  }

  /**
   * Gets the value at the given coordinate.
   *
   * @param loc the location to query
   * @return the value at the given coordinate.
   */
  public T getValueAt(Coord loc) {
    return grid.get(loc);
  }

  /**
   * Gets the value at the given coordinate, or the given default value.
   *
   * @param loc the location to query
   * @return the value at the given coordinate.
   */
  public T getOrDefault(Coord loc, T defaultValue) {
    return grid.getOrDefault(loc, defaultValue);
  }

  /**
   * Gets the concrete values in a given row.
   *
   * @param row the row to query
   * @return the values in the given row.
   */
  public Set<T> getRow(int row) {
    HashSet<T> rowContents = new HashSet<>();
    for (Coord c : this.grid.keySet()) {
      if (c.row == row) {
        rowContents.add(this.grid.get(c));
      }
    }
    return rowContents;
  }

  /**
   * Gets the concrete values in a given column.
   *
   * @param col the column to query
   * @return the values in the given column.
   */
  public Set<T> getCol(int col) {
    HashSet<T> rowContents = new HashSet<>();
    for (Coord c : this.grid.keySet()) {
      if (c.col == col) {
        rowContents.add(this.grid.get(c));
      }
    }
    return rowContents;
  }

  /**
   * Returns a set of all filled coordinates.
   *
   * @return the set of all filled coordinates
   */
  public Set<Coord> getLocationSet() {
    return this.grid.keySet();
  }

  /**
   * Returns a set of all existing values.
   *
   * @return the set of all existing values
   */
  public Set<T> getValueSet() {
    return this.getValueSet();
  }

  /**
   * Returns if there is a value corresponding to the given location.
   * @param loc the location to query
   * @return if the location has a value
   */
  public boolean hasContents(Coord loc) {
    return this.grid.containsKey(loc);
  }

}
