package edu.cs3500.spreadsheets.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.cs3500.spreadsheets.sexp.Parser;

/**
 * A SimpleWorkbook to contain multiple Spreadsheets.
 */
public class SimpleWorkbook implements Workbook {
  private final Map<String, Spreadsheet> spreadsheets;
  private final Map<WorksheetId<Coord>, List<WorksheetId<Coord>>> singleDependencies;
  private final Map<WorksheetId<Integer>, List<WorksheetId<Coord>>> colDependencies;
  private final CellFactory cellFactory;

  /**
   * Instantiate a new SimpleWorkbook.
   */
  public SimpleWorkbook() {
    this.spreadsheets = new HashMap<>();
    this.singleDependencies = new HashMap<>();
    this.colDependencies = new HashMap<>();
    this.cellFactory = new CellFactory(this);
  }

  @Override
  public ICell getCellAt(WorksheetId<Coord> coordinate) {
    return this.spreadsheets.get(coordinate.sheet).getCellAt(coordinate.ref);
  }

  @Override
  public List<ICell> getColCells(WorksheetId<Integer> column) {
    return this.spreadsheets.get(column.sheet).getColCells(column.ref);
  }

  @Override
  public List<WorksheetId<Coord>> putCell(WorksheetId<Coord> location, String value) {
    this.clearDependencies(location);
    String strippedValue = value;
    if (value.startsWith("=")) {
      strippedValue = value.substring(1);
    }
    if (value.isEmpty()) {
      return removeCell(location);
    }
    ICell toAdd = this.cellFactory.parse(Parser.parse(strippedValue), location.sheet);
    if (toAdd instanceof EvaluatableCell) {
      System.out.println("Adding dependencies");
      addDependencies(((EvaluatableCell)toAdd).getDependencies(), singleDependencies, location);
      addDependencies(((EvaluatableCell)toAdd).getColDependencies(), colDependencies, location);
    }
    this.spreadsheets.get(location.sheet).putCell(location.ref, toAdd, value);
    return clearCache(location);
  }

  @Override
  public IReadableCell getValue(WorksheetId<Coord> location) {
    String sheet = location.sheet;
    Coord c = location.ref;
    return this.spreadsheets.get(sheet).getValue(c);
  }

  @Override
  public String getRaw(WorksheetId<Coord> location) {
    return this.spreadsheets.get(location.sheet).getRaw(location.ref);
  }

  @Override
  public boolean hasError(WorksheetId<Coord> location) {
    String sheet = location.sheet;
    Coord c = location.ref;
    return this.spreadsheets.get(sheet).hasError(c);
  }

  @Override
  public List<Coord> getNonEmptyCoords(String sheet) {
    if (!this.spreadsheets.containsKey(sheet)) {
      System.out.println("Spreadsheet does not exist: " + sheet);
      return new ArrayList<>();
    }
    return this.spreadsheets.get(sheet).getNonEmptyCoords();
  }

  @Override
  public List<String> getSpreadsheets() {
    return new ArrayList<>(this.spreadsheets.keySet());
  }

  @Override
  public List<WorksheetId<Coord>> removeCell(WorksheetId<Coord> location) {
    String sheet = location.sheet;
    Coord c = location.ref;
    Spreadsheet ss = spreadsheets.get(sheet);
    if (ss.hasContents(c)) {
      this.clearDependencies(location);
      ss.removeCell(c);
      return this.clearCache(location);
    }
    return new ArrayList<>();
  }

  @Override
  public boolean addSpreadsheet(String sheet) {
    if (this.spreadsheets.containsKey(sheet)) {
      return false;
    }
    this.spreadsheets.put(sheet, new CellContext());

    return true;
  }

  //===================================== PRIVATE METHODS ==========================================

  /**
   * Helper method to register cell dependencies.
   *
   * @param input    the cell's dependencies
   * @param list     the propagation graph
   * @param location the cell location
   * @param <T>      the graph's key type.
   */
  private <T> void addDependencies(Iterable<T> input, Map<T, List<WorksheetId<Coord>>> list,
                                   WorksheetId<Coord> location) {
    for (T dependency : input) {
      if (!list.containsKey(dependency)) {
        list.put(dependency, new ArrayList<>());
      }
      if (!list.get(dependency).contains(location)) {
        list.get(dependency).add(location);
        System.out.println(location + " is watching " + dependency);
      }
    }
  }

  private List<WorksheetId<Coord>> getDependencies(WorksheetId<Coord> root) {
    Set<WorksheetId<Coord>> allDeps = new HashSet<>();
    List<WorksheetId<Coord>> toProcess = new ArrayList<>(this.singleDependencies.getOrDefault(root,
            new ArrayList<>()));
    toProcess.addAll(this.colDependencies.getOrDefault(
            new WorksheetId<>(root.sheet, root.ref.col), new ArrayList<>()));

    while (toProcess.size() > 0) {
      WorksheetId<Coord> c = toProcess.remove(0);
      List<WorksheetId<Coord>> children = this.singleDependencies.getOrDefault(c,
              new ArrayList<>());
      children.addAll(this.colDependencies.getOrDefault(
              new WorksheetId<>(root.sheet, root.ref.col), new ArrayList<>()));
      if (children != null) {
        for (WorksheetId<Coord> child : children) {
          // Avoid infinite loops by refusing to re-process cells
          if (!allDeps.contains(child) && !toProcess.contains(child)) {
            toProcess.add(child);
          }
        }
      }
      allDeps.add(c);
    }
    return new ArrayList<>(allDeps);
  }

  private List<WorksheetId<Coord>> clearCache(WorksheetId<Coord> location) {

    // Clear the cache for relevant items
    if (this.isWatched(location)) {
      System.out.println("Clearing cache from " + location);
      List<WorksheetId<Coord>> deps = this.getDependencies(location);
      for (WorksheetId<Coord> toClear : deps) {
        ICell hold = this.getCellAt(toClear);
        if (hold instanceof EvaluatableCell) {
          System.out.println("Clearing cache: " + toClear.toString());
          ((EvaluatableCell) hold).clearCache();
        } else {
          throw new IllegalStateException("Non-evaluable reference?!");
        }
      }
      return deps;
    }
    return new ArrayList<>();
  }

  private boolean isWatched(WorksheetId<Coord> location) {
    return this.singleDependencies.containsKey(location)
            || this.colDependencies.containsKey(
                    new WorksheetId<>(location.sheet, location.ref.col));
  }

  private void clearDependencies(WorksheetId<Coord> location) {
    ICell cell = this.getCellAt(location);
    if (!(cell instanceof EmptyCell)) {
      // There's a cell here. Check if it's evaluable.
      if (cell instanceof EvaluatableCell) {
        // Cell is evaluable. Look at this cell's dependencies.
        EvaluatableCell evaluable = (EvaluatableCell) cell;
        for (WorksheetId<Coord> dependency : evaluable.getDependencies()) {
          // Go to that cell and remove this from the list of cells to update upon change.
          singleDependencies.get(dependency).remove(location);
        }

        for (WorksheetId<Integer> colDependency : evaluable.getColDependencies()) {
          colDependencies.get(colDependency).remove(location);
        }
      }
    }
  }
}
