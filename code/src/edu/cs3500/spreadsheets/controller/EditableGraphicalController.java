package edu.cs3500.spreadsheets.controller;

import java.util.List;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.Workbook;
import edu.cs3500.spreadsheets.model.WorksheetId;
import edu.cs3500.spreadsheets.view.GraphicalMapper;
import edu.cs3500.spreadsheets.view.GraphicalView;
import edu.cs3500.spreadsheets.view.SimpleGraphicalView;

/**
 * Creates and controls an editable graphical UI for Spreadsheets. All cells are editable, as
 * expected.
 */
public class EditableGraphicalController {
  private Workbook model;
  private GraphicalView view;

  /**
   * Instantiates an EditableGraphicalController for the given Spreadsheet.
   *
   * @param wb the workbook to edit
   */
  public EditableGraphicalController(Workbook wb) {
    SimpleGraphicalView view = new SimpleGraphicalView(wb);
    init(wb, view, new GraphicalMapper(view));
  }

  /**
   * USED FOR TESTING; Instantiates an EGC with a SpreadSheet and a GraphicalView.
   *
   * @param wb  the Spreadsheet to test over
   * @param gv the GraphicalView mock used for testing
   */
  public EditableGraphicalController(Workbook wb, GraphicalView gv, GraphicalMapper mapper) {
    init(wb, gv, mapper);
  }

  private void init(Workbook wb, GraphicalView gv, GraphicalMapper mapper) {
    this.model = wb;
    this.view = gv;

    mapper.addAction("confirm", c -> {
      WorksheetId<Coord> selection = view.getSelection();
      if (selection == null) {
        System.out.println("Null selection!");
        return;
      }
      update(selection, model.putCell(selection, view.getEditText()));
    });

    mapper.addAction("delete", c -> {
      WorksheetId<Coord> selection = view.getSelection();
      if (selection == null) {
        System.out.println("Null selection!");
        return;
      }
      update(selection, model.removeCell(selection));
    });
  }

  private void update(WorksheetId<Coord> cell, List<WorksheetId<Coord>> dependencies) {
    view.drawCell(cell, model.getValue(cell));
    for (WorksheetId<Coord> c : dependencies) {
      System.out.println("Drawing " + c.toString());
      view.drawCell(c, model.getValue(c));
    }
  }
}