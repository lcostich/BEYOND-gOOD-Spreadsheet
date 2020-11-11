package edu.cs3500.spreadsheets.model;

import java.util.List;

import edu.cs3500.spreadsheets.sexp.Sexp;
import edu.cs3500.spreadsheets.sexp.SexpVisitor;

/**
 * Factory pattern for instantiating ICells.
 */
public class CellFactory implements SexpVisitor<ICell> {
  private final Workbook context;
  protected String currentSheet;

  public CellFactory(Workbook context) {
    this.context = context;
  }

  /**
   * Creates a cell based off of the given input and sheet.
   * @param input the Sexp to be parsed
   * @param sheet the sheet to be edited
   * @return the new Cell
   */
  public ICell parse(Sexp input, String sheet) {
    if (sheet == null) {
      throw new IllegalArgumentException("Null sheet");
    }
    this.currentSheet = sheet;
    return input.accept(this);
  }


  /**
   * Process a boolean value.
   *
   * @param b the value
   * @return the desired result
   */
  @Override
  public ICell visitBoolean(boolean b) {
    return new BooleanCell(b);
  }

  /**
   * Process a numeric value.
   *
   * @param d the value
   * @return the desired result
   */
  @Override
  public ICell visitNumber(double d) {
    return new NumberCell(d);
  }

  /**
   * Process a list value.
   *
   * @param l the contents of the list (not yet visited)
   * @return the desired result
   */
  @Override
  public ICell visitSList(List<Sexp> l) {
    return new ListParser(this.context).parse(l, currentSheet);
  }

  /**
   * Process a symbol.
   *
   * @param s the value
   * @return the desired result
   */
  @Override
  public ICell visitSymbol(String s) {
    try {
      if (SingleReference.isSingleReference(s)) {
        return new SingleReference(context, s, currentSheet);
      } else if (MultiReference.isMultiReference(s)) {
        return new MultiReference(context, s, currentSheet);
      } else if (ColumnReference.isColumnReference(s)) {
        return new ColumnReference(context, s, currentSheet);
      }
    } catch (Exception e) {
      return new Error(e.getMessage());
    }
    return new Error("Unexpected symbol: " + s);
  }

  /**
   * Process a string value.
   *
   * @param s the value
   * @return the desired result
   */
  @Override
  public ICell visitString(String s) {
    return new StringCell(s);
  }
}
