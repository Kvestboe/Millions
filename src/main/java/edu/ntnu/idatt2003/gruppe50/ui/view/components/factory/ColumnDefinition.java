package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import java.util.function.BiConsumer;
import java.util.function.Function;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TableCell;

/**
 * @param <T>
 * @param <V>
 */
public record ColumnDefinition<T, V>(
    String title,
    Function<T, ObservableValue<V>> getter,
    BiConsumer<TableCell<T, V>, T> styler
) {
  public ColumnDefinition(
      String title,
      Function<T, ObservableValue<V>> getter
  ) {
    this(title, getter, null);
  }
}
