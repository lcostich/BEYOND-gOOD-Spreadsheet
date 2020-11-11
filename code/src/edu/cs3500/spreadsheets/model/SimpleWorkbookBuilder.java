package edu.cs3500.spreadsheets.model;

/**
 * Class to build a SimpleWorkbook given a String of input.
 */
public class SimpleWorkbookBuilder implements WorkbookReader.WorkbookBuilder<SimpleWorkbook> {
  private String sheet;
  private SimpleWorkbook workbook;

  public SimpleWorkbookBuilder() {
    sheet = "";
    this.workbook = new SimpleWorkbook();
  }

  /**
   * Creates a new cell at the given coordinates and fills in its raw contents.
   *
   * @param col      the column of the new cell (1-indexed)
   * @param row      the row of the new cell (1-indexed)
   * @param contents the raw contents of the new cell: may be {@code null}, or any string. Strings
   *                 beginning with an {@code =} character should be treated as formulas; all other
   *                 strings should be treated as number or boolean values if possible, and string
   *                 values otherwise.
   * @return this {@link WorkbookReader.WorkbookBuilder}
   */
  @Override
  public WorkbookReader.WorkbookBuilder<SimpleWorkbook> createCell(int col,
                                                                   int row, String contents) {
    if (sheet.isBlank()) {
      throw new IllegalStateException("Tried to add cell outside of any Spreadsheet");
    }
    workbook.putCell(new WorksheetId<>(sheet, new Coord(col, row)), contents);
    return this;
  }

  /**
   * Puts any following cells into the worksheet of the given name.
   *
   * @param sheet the worksheet to open
   */
  @Override
  public void openSheet(String sheet) {
    this.workbook.addSpreadsheet(sheet);
    this.sheet = sheet;
  }

  /**
   * Closes the worksheet, if any.
   */
  @Override
  public void closeSheet() {
    this.sheet = "";
  }

  /**
   * Finalizes the construction of the worksheet and returns it.
   *
   * @return the fully-filled-in worksheet
   */
  @Override
  public SimpleWorkbook createWorkbook() {
    return this.workbook;
  }
}
