package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import java.math.BigDecimal;
import java.util.function.Function;
import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.scene.control.TableCell;

/** Factory for common table column definitions. */
public class ColumnPresets {

  /** CSS style for positive numeric values. */
  public static final String POSITIVE = "-fx-text-fill: #4CAF50;";

  /** CSS style for negative numeric values. */
  public static final String NEGATIVE = "-fx-text-fill: #EF5350;";

  /** CSS style for neutral numeric values. */
  public static final String NEUTRAL = "-fx-text-fill: #E0E0E0;";

  /**
   * Creates a text column.
   *
   * @param title column title
   * @param getter function that extracts text from a row
   * @param <T> row item type
   * @return text column definition
   */
  public static <T> ColumnDefinition<T, String> text(String title, Function<T, String> getter) {
    return new ColumnDefinition<>(title, row -> new ReadOnlyStringWrapper(getter.apply(row)));
  }

  /**
   * Creates an integer column.
   *
   * @param title column title
   * @param getter function that extracts text from a row
   * @param <T> row item type
   * @return text column definition
   */
  public static <T> ColumnDefinition<T, Integer> integer(
      String title,
      Function<T, Integer> getter
  ) {
    return new ColumnDefinition<>(
        title,
        row -> new ReadOnlyIntegerWrapper(getter.apply(row)).asObject());
  }

  /**
   * Creates a BigDecimal text column.
   *
   * @param title column title
   * @param getter function that extracts text from a row
   * @param <T> row item type
   * @return text column definition
   */
  public static <T> ColumnDefinition<T, String> bigDecimal(
      String title,
      Function<T, BigDecimal> getter
  ) {
    return new ColumnDefinition<>(
        title,
        row -> new ReadOnlyStringWrapper(getter.apply(row).toString()));
  }

  /**
   * Creates a formatted currency column.
   *
   * @param title column title
   * @param getter function that extracts text from a row
   * @param <T> row item type
   * @return text column definition
   */
  public static <T> ColumnDefinition<T, String> currency(
      String title,
      Function<T, BigDecimal> getter
  ) {
    return new ColumnDefinition<>(
        title,
        row -> new ReadOnlyStringWrapper(MoneyFormat.formatCurrency(getter.apply(row))));
  }

  /**
   * Creates a signed currency column with gain/loss coloring.
   *
   * @param title column title
   * @param getter function that extracts text from a row
   * @param <T> row item type
   * @return text column definition
   */
  public static <T> ColumnDefinition<T, String> signedCurrency(
      String title,
      Function<T, BigDecimal> getter
  ) {
    return new ColumnDefinition<>(
        title,
        row -> new ReadOnlyStringWrapper(MoneyFormat.formatSignedCurrency(getter.apply(row))),
        (TableCell<T, String> cell, T row) -> cell.setStyle(gainColor(getter.apply(row)))
    );
  }

  /**
   * Creates a signed percentage column with gain/loss coloring.
   *
   * @param title column title
   * @param getter function that extracts text from a row
   * @param <T> row item type
   * @return text column definition
   */
  public static <T> ColumnDefinition<T, String> signedPercent(
      String title,
      Function<T, BigDecimal> getter
  ) {
    return new ColumnDefinition<>(
        title,
        row -> new ReadOnlyStringWrapper(MoneyFormat.formatSignedPercent(getter.apply(row))),
        (TableCell<T, String> cell, T row) -> cell.setStyle(gainColor(getter.apply(row)))
    );
  }

  private static String gainColor(BigDecimal value) {
    if (value.compareTo(BigDecimal.ZERO) > 0) {
      return POSITIVE;
    } else if (value.compareTo(BigDecimal.ZERO) < 0) {
      return NEGATIVE;
    } else {
      return NEUTRAL;
    }
  }

}
