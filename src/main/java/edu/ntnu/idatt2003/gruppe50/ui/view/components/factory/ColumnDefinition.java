package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import java.util.function.BiConsumer;
import java.util.function.Function;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;

/**
 * Defines how a table column should be built.
 *
 * @param <T> row item type
 * @param <V> displayed value type
 * @param title column title
 * @param getter function that extracts an observable value from a row
 * @param styler optional function that styles a cell based on its row item
 */
public record ColumnDefinition<T, V>(
    String title,
    Function<T, ObservableValue<V>> getter,
    BiConsumer<TableCell<T, V>, T> styler
) {

  /**
   * Creates a column definition without custom cell styling.
   *
   * @param title column title
   * @param getter function that extracts an observable value from a row
   */
  public ColumnDefinition(
      String title,
      Function<T, ObservableValue<V>> getter
  ) {
    this(title, getter, null);
  }
}
