package edu.cs3500.spreadsheets.model;

import java.util.Objects;

/**
 * An identifier as to which worksheet T resides in.
 * @param <T> the reference relating to this WorksheetId
 */
public class WorksheetId<T> {
  public final String sheet;
  public final T ref;

  /**
   * Initializes a new WorksheetId.
   * @param sheet the identifier of this WorksheetId
   * @param ref   the reference this WorksheetId refers to
   */
  public WorksheetId(String sheet, T ref) {
    if (sheet == null || sheet.isEmpty() || ref == null) {
      throw new IllegalArgumentException("ERROR: ID can not be empty/null");
    }
    else {
      this.sheet = sheet;
      this.ref = ref;
    }
  }

  @Override
  public String toString() {
    return this.sheet + "!" + this.ref.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof WorksheetId)) {
      return false;
    }
    WorksheetId<?> that = (WorksheetId<?>) o;
    return sheet.equals(that.sheet)
            && ref.equals(that.ref);
  }

  @Override
  public int hashCode() {
    return Objects.hash(sheet, ref);
  }
}
