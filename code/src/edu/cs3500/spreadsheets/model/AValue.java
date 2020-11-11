package edu.cs3500.spreadsheets.model;

/**
 * Represents any value that can be placed in a cell.
 *
 * @param <T> generic value contained within this ICell
 */
public abstract class AValue<T> implements IReadableCell {
  public T value;

  public abstract T getValue();
}
