package edu.cs3500.spreadsheets.view;

import org.junit.Test;

import java.io.StringReader;

import edu.cs3500.spreadsheets.BeyondGood;
import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.Workbook;
import edu.cs3500.spreadsheets.model.WorksheetId;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Class to test the TextualView of a Spreadsheet.
 */
public class TextualViewTest {

  @Test
  public void testRoundTrip() {
    String in = "A1 2\n" +
            "B1 false\n" +
            "C1 5\n" +
            "A2 =(< A1 C1)";
    Workbook model1 = BeyondGood.initSpreadsheet(new StringReader(in));
    StringBuilder view1 = new StringBuilder();
    new TextualView(model1, view1).initView();
    Workbook model2 = BeyondGood.initSpreadsheet(new StringReader(view1.toString()));

    // Compare models
    assertEquals(model1.getNonEmptyCoords("Spreadsheet").size(),
            model2.getNonEmptyCoords("Spreadsheet").size());
    assertTrue(model1.getNonEmptyCoords("Spreadsheet").containsAll(
            model2.getNonEmptyCoords("Spreadsheet")));
    for (Coord c : model1.getNonEmptyCoords("Spreadsheet")) {
      // Check if corresponding cells are the same.
      assertEquals(model1.getRaw(new WorksheetId<>("Spreadsheet", c)),
              model2.getRaw(new WorksheetId<>("Spreadsheet", c)));
    }
  }

}
