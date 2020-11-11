package edu.cs3500.spreadsheets.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.function.Function;

import edu.cs3500.spreadsheets.sexp.Sexp;


/**
 * Special type of CellFactory for parsing the inside of a list.
 */
public class ListParser extends CellFactory {
  private final HashMap<String, Function<List<ICell>, FormulaCell>> types;
  private List<ICell> args;
  private boolean functionSpot;

  /**
   * Initialize the list parser.
   *
   * @param context the CellContext to be parsed
   */
  public ListParser(Workbook context) {
    super(context);
    this.types = new HashMap<>();

    this.types.put("SUM", new MakeSum());
    this.types.put("PRODUCT", new MakeProduct());
    this.types.put("<", new MakeLessThan());
    this.types.put("CONCAT", new MakeConcat());
  }

  /**
   * Parse the given list of Sexp's.
   *
   * @param list the list to parse
   * @return the resulting ListCell or FormulaCell
   */
  public ICell parse(List<Sexp> list, String sheet) {
    this.currentSheet = sheet;
    functionSpot = false;
    if (list.size() > 0) {
      // Start at 1, because we don't know if it's a function yet
      this.args = new ArrayList<>();
      for (int i = 1; i < list.size(); i++) {
        this.args.add(list.get(i).accept(this));
      }
      functionSpot = true;
      // Last (first) cell!
      return list.get(0).accept(this);
    }
    // empty list
    return new ListCell(new ArrayList<>());
  }

  // Override relevant methods

  /**
   * Custom symbol behavior.
   *
   * @param s the value
   * @return
   */
  @Override
  public ICell visitSymbol(String s) {
    if (functionSpot) {
      // We should be closing the list here. Check for formulas.
      if (isFormula(s)) {
        // It's a formula!
        return types.get(s).apply(this.args);
      } else {
        // It's a list that starts with some other symbol.
        args.add(super.visitSymbol(s));
        return new Error("Improper cell syntax");
      }
    } else {
      // It's a symbol in the middle of the list.
      return super.visitSymbol(s);
    }
  }

  private boolean isFormula(String s) {
    return types.containsKey(s);
  }

  //====================================== Private classes =========================================

  private class MakeSum implements Function<List<ICell>, FormulaCell> {
    /**
     * Applies this function to the given argument.
     *
     * @param args the function arguments
     * @return the function object
     */
    @Override
    public FormulaCell apply(List<ICell> args) {
      return new SumCell(args);
    }
  }

  private class MakeProduct implements Function<List<ICell>, FormulaCell> {

    /**
     * Applies this function to the given argument.
     *
     * @param args the function arguments
     * @return the function object
     */
    @Override
    public FormulaCell apply(List<ICell> args) {
      return new ProductCell(args);
    }
  }

  private class MakeLessThan implements Function<List<ICell>, FormulaCell> {

    /**
     * Applies this function to the given argument.
     *
     * @param args the function arguments
     * @return the function object
     */
    @Override
    public FormulaCell apply(List<ICell> args) {
      return new LessThanCell(args);
    }
  }

  private class MakeConcat implements Function<List<ICell>, FormulaCell> {

    /**
     * Applies this function to the given argument.
     *
     * @param args the function arguments
     * @return the function object
     */
    @Override
    public FormulaCell apply(List<ICell> args) {
      return new ConcatCell(args);
    }
  }
}
