package edu.cs3500.spreadsheets.model;

/**
 * Interface to represent a Cell in a spreadsheet. A cell is one of: A BooleanCell A SymbolCell A
 * StringCell A ListCell A NumberCell A FormulaCell A cell is a spreadsheet representation of a
 * particular data type. Processing of each cell is done through the visitor pattern, so that each
 * data type may be handled separately.
 */
interface ICell {
  /**
   * Accept a cell visitor.
   *
   * @param visitor the cell reader to accept
   * @param <T>     the return type
   * @return the result
   */
  public <T> T accept(CellVisitor<T> visitor);
}
