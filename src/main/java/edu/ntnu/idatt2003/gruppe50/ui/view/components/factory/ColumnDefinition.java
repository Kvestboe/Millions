package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import java.util.function.Function;
import javafx.beans.value.ObservableValue;

/**
 * @param <T>
 * @param <V>
 */
public record ColumnDefinition<T, V>(String title, Function<T, ObservableValue<V>> getter) {}
