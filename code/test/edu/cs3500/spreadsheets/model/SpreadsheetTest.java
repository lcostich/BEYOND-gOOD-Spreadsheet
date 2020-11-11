package edu.cs3500.spreadsheets.model;

import org.junit.Test;

import java.io.StringReader;

import edu.cs3500.spreadsheets.BeyondGood;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertTrue;

/**
 * Class to test the Workbook.
 */
public class SpreadsheetTest {
  StringVisitor asString;

  public SpreadsheetTest() {
    this.asString = new StringVisitor();
  }

  private void assertValue(String expected, Workbook sheet, int col, int row) {
    IReadableCell value = sheet.getValue(new WorksheetId<>("Spreadsheet", new Coord(col, row)));
    assertEquals(expected, value.accept(asString));
  }

  @Test
  public void testEmptySpreadsheet() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader(""));
    assertValue("", sheet, 1, 1);
  }

  @Test
  public void testPrimitiveCells() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 true\n" +
            "B2 2.364\n" +
            "F3 \"String\""));
    assertValue("true", sheet, 1, 1);
    assertValue(String.format("%f", 2.364), sheet, 2, 2);
    assertValue("\"String\"", sheet, 6, 3);
  }

  @Test
  public void testSum() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 2\n" +
            "B1 4\n" +
            "C1 5\n" +
            "A2 =(SUM A1:C1)"));
    assertValue(String.format("%f", 11.0), sheet, 1, 2);
  }

  @Test
  public void testMixedValiditySum() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 2\n" +
            "B1 true\n" +
            "C1 5\n" +
            "A2 =(SUM A1:C1)"));
    assertValue(String.format("%f", 7.0), sheet, 1, 2);
  }

  @Test
  public void testProduct() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 2\n" +
            "B1 4\n" +
            "C1 5\n" +
            "A2 =(PRODUCT A1 B1:C1)"));
    assertValue(String.format("%f", 40.0), sheet, 1, 2);
  }

  @Test
  public void testLessThan() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 2\n" +
            "B1 4\n" +
            "C1 5\n" +
            "A2 =(< A1 B1)"));
    assertValue("true", sheet, 1, 2);
  }

  @Test
  public void testLessThanTooManyMixed() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 2\n" +
            "B1 false\n" +
            "C1 5\n" +
            "A2 =(< A1:C1)"));
    assertValue("ERROR: Expected 2 arguments; got 3", sheet, 1, 2);
    assertTrue(sheet.hasError(new WorksheetId<Coord>("Spreadsheet",
            new Coord(1, 2))));
  }

  @Test
  public void testLessThanNotEnough() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 2\n" +
            "B1 false\n" +
            "C1 5\n" +
            "A2 =(< A1)"));
    assertValue("ERROR: Expected 2 arguments; got 1", sheet, 1, 2);
  }

  @Test
  public void testConcatMixedTypes() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 2\n" +
            "B1 false\n" +
            "C1 \"String\"\n" +
            "A2 =(CONCAT A1 B1 C1)"));
    assertValue("\"" + String.format("%f", 2.0) + "falseString" + "\"",
            sheet, 1, 2);
  }

  @Test
  public void testConcat() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 \"A\"\n" +
            "B1 \"B\"\n" +
            "C1 \"C\"\n" +
            "A2 =(CONCAT A1 B1 C1)"));
    assertValue("\"ABC\"", sheet, 1, 2);
  }

  @Test
  public void testProductDefault() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 =C1\n" +
            "B1 false\n" +
            "C1 \"String\"\n" +
            "A2 =(PRODUCT A1 B1:C1)"));
    assertValue(String.format("%f", 0.0), sheet, 1, 2);
  }

  @Test
  public void testCircularReference() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 =A1"));
    assertValue("ERROR: Circular dependency", sheet, 1, 1);
    Workbook sheet2 = BeyondGood.initSpreadsheet(new StringReader("A1 1\n" +
            "A2 false\n" +
            "B1 \"String\"\n" +
            "B2 =(SUM A1:B2)"));
    assertValue("ERROR: Circular dependency", sheet, 1, 1);
    assertValue("ERROR: Circular dependency", sheet2, 2, 2);
  }

  @Test
  public void testCircularFormula() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 1\n" +
            "A2 false\n" +
            "B1 \"String\"\n" +
            "B2 =(SUM A1:B2)"));
    assertValue("ERROR: Circular dependency", sheet, 2, 2);
  }

  @Test
  public void testIndirectCircularReference() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 1\n" +
            "B1 2\n" +
            "C1 3\n" +
            "D1 =A2\n" +
            "A2 =(SUM A1:D1)"));
    assertValue("ERROR: Circular dependency", sheet, 1, 2);
    //assertValue("ERROR: Circular dependency", sheet, 4, 1);
  }

  @Test
  public void testBogusFormula() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 1\n" +
            "B1 2\n" +
            "C1 3\n" +
            "D1 =A2\n" +
            "A2 =(SUN A1:C1)"));
    assertValue("ERROR: Improper cell syntax", sheet, 1, 2);
  }

  @Test
  public void testRedundantFormula() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 1\n" +
            "B1 =(SUM A1 A1)"));
    assertValue(String.format("%f", 2.0), sheet, 2, 1);
  }

  @Test
  public void testExpensiveRedundancy() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 1\n" +
            "B1 =(SUM A1 A1)\n" +
            "C1 =(SUM B1 B1)\n" +
            "D1 =(SUM C1 C1)\n" +
            "E1 =(SUM D1 D1)\n" +
            "F1 =(SUM E1 E1)\n" +
            "G1 =(SUM F1 F1)\n" +
            "H1 =(SUM G1 G1)\n" +
            "I1 =(SUM H1 H1)\n" +
            "J1 =(SUM I1 I1)"));
    assertValue(String.format("%f", 512.0), sheet, 10, 1);
  }

  @Test
  public void testColumnReference() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 1\n" +
            "A2 2\n" +
            "A3 3\n" +
            "A4 =(SUM A1:A3)\n" +
            "B1 =(PRODUCT A:A)\n" +
            "B2 4\n" +
            "B3 1\n" +
            "B4 2\n" +
            "C1 =(SUM A:A)\n" +
            "D1 =(PRODUCT A:C)"));
    assertValue(String.format("%f", 12.0), sheet, 3, 1);
    assertValue(String.format("%f", 124416.0), sheet, 4, 1);
  }

  @Test
  public void testCircularColumnReference() {
    Workbook sheet = BeyondGood.initSpreadsheet(new StringReader("A1 1\n" +
            "A2 2\n" +
            "A3 =(PRODUCT B:B)\n" +
            "A4 =(SUM A1:A3)\n" +
            "B1 =(PRODUCT A:A)\n" +
            "B2 4\n" +
            "B3 1\n" +
            "B4 2\n" +
            "C1 =(SUM A:A)\n" +
            "D1 =(PRODUCT A:C)\n" +
            "E1 =(SUM E:E)"));
    assertValue("ERROR: Circular dependency", sheet, 5, 1);
    assertValue("ERROR: Circular dependency", sheet, 2, 1);
  }
}
