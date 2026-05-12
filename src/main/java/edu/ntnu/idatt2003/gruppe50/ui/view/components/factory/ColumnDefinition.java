package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import javafx.beans.value.ObservableValue;

import java.util.function.Function;

/**
 *
 * @param <T>
 * @param <V>
 */
public record ColumnDefinition<T, V>(
    String title,
    Function<T, ObservableValue<V>> getter
) { }
