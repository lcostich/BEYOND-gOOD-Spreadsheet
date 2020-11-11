package edu.cs3500.spreadsheets.view;

import java.io.IOException;

import edu.cs3500.spreadsheets.model.Coord;
import edu.cs3500.spreadsheets.model.IReadableCell;
import edu.cs3500.spreadsheets.model.ReadableWorkbook;
import edu.cs3500.spreadsheets.model.StringVisitor;
import edu.cs3500.spreadsheets.model.WorksheetId;

/**
 * Renders a textual representation of a Spreadsheet into an Appendable.
 */
public class TextualView implements SpreadsheetView {
  private final ReadableWorkbook model;
  private Appendable out;

  /**
   * Instantiate a new TextualView.
   *
   * @param model the Spreadsheet to be drawn
   * @param out   the Appendable to be written to
   */
  public TextualView(ReadableWorkbook model, Appendable out) throws IllegalArgumentException {
    if (model == null) {
      throw new IllegalArgumentException("null model");
    }
    if (out == null) {
      throw new IllegalArgumentException("null appendable");
    }
    this.model = model;
    this.out = out;
  }

  @Override
  public void drawCell(WorksheetId<Coord> coord, IReadableCell value) {
    try {
      this.out.append(coord.ref.toString() + " " + value.accept(new StringVisitor()) + "\n");
    } catch (IOException io) {
      throw new IllegalStateException(io.getMessage());
    }
  }

  @Override
  public void drawRaw(WorksheetId<Coord> coord, String raw) {
    try {
      this.out.append(coord.ref.toString() + " " + raw + "\n");
    } catch (IOException io) {
      throw new IllegalStateException(io.getMessage());
    }
  }

  /**
   * Appends beginning syntax for a Spreadsheet to the Appendable.
   * @param sheet the sheet to initialize in Appendable out
   */
  private void beginSheet(String sheet) {
    try {
      this.out.append(sheet + "{\n");
    } catch (IOException io) {
      throw new IllegalStateException(io.getMessage());
    }
  }

  /**
   * Appends ending syntax for a Spreadsheet to the Appendable.
   */
  private void endSheet() {
    try {
      this.out.append("}\n");
    } catch (IOException io) {
      throw new IllegalStateException(io.getMessage());
    }
  }

  /**
   * To write all non-empty Cells to the Appendable this.out.
   */
  public void initView() {
    for (String sheet : this.model.getSpreadsheets()) {
      beginSheet(sheet);
      for (Coord c : this.model.getNonEmptyCoords(sheet)) {
        this.drawRaw(new WorksheetId<>(sheet, c), this.model.getRaw(new WorksheetId<>(sheet, c)));
      }
      endSheet();
    }
  }
}
