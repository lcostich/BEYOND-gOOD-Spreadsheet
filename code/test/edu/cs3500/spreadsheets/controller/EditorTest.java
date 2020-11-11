package edu.cs3500.spreadsheets.controller;

import org.junit.Test;

import java.io.StringReader;

import edu.cs3500.spreadsheets.BeyondGood;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.Workbook;
import edu.cs3500.spreadsheets.model.WorksheetId;
import edu.cs3500.spreadsheets.view.CommandContext;
import edu.cs3500.spreadsheets.view.GraphicalMapper;
import edu.cs3500.spreadsheets.view.MockGraphicalView;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

//import static junit.framework.TestCase.assertEquals;
//import static junit.framework.TestCase.assertTrue;

/**
 * Class to test the editor GUI (containing a read-only only) of a Spreadsheet.
 */
public class EditorTest {
  MockGraphicalView mock;
  EditableGraphicalController egc;
  GraphicalMapper mapper;
  Workbook model;

  private void initMock() {
    String in = "A1 2\n"
            + "B1 false\n"
            + "C1 5\n"
            + "A2 =(< A1 C1)";
    this.model = BeyondGood.initSpreadsheet(new StringReader(in));
    this.mock = new MockGraphicalView(new StringBuilder());
    this.mapper = new GraphicalMapper(this.mock);
    this.egc = new EditableGraphicalController(model, this.mock, mapper);
  }

  @Test
  public void testMapper() {
    initMock();
    ActionLogger confirmLogger = new ActionLogger();
    this.mapper.addAction("confirm", confirmLogger);
    ActionLogger cancelLogger = new ActionLogger();
    this.mapper.addAction("cancel", cancelLogger);

    this.mock.triggerCancel();
    assertTrue(cancelLogger.getWasTriggered());
    cancelLogger.reset();

    this.mock.triggerConfirm();
    assertTrue(confirmLogger.getWasTriggered());
    confirmLogger.reset();

    testKeyCommand("ENTER", CommandContext.EDIT, confirmLogger);
    testKeyCommand("ESCAPE", CommandContext.EDIT, cancelLogger);
    testKeyCommand("TAB", CommandContext.EDIT, confirmLogger);
  }

  @Test
  public void testController() {
    initMock();
    this.mock.setEditText("\"ABC\"");
    this.mock.triggerConfirm();
    assertEquals("\"ABC\"", this.model.getRaw(mock.getSelection()));
    this.mock.select(new WorksheetId<>("Spreadsheet", new Coord(5, 7)));
    String initialValue = this.model.getRaw(this.mock.getSelection());
    this.mock.setEditText("\"This should be discarded\"");
    this.mock.triggerCancel();
    assertEquals(initialValue, this.model.getRaw(mock.getSelection()));

  }

  private void testKeyCommand(String command, CommandContext context, ActionLogger logger) {
    this.mock.triggerKeyEvent(command, context);
    assertTrue(logger.getWasTriggered());
    logger.reset();
  }

  private class ActionLogger implements CommandListener {
    private boolean wasTriggered;

    public ActionLogger() {
      this.wasTriggered = false;
    }

    @Override
    public void onCommand(String command) {
      this.wasTriggered = true;
    }

    public void reset() {
      this.wasTriggered = false;
    }

    public boolean getWasTriggered() {
      return wasTriggered;
    }
  }
}
