package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Factory for compact statistic card components.
 */
public final class StatCardFactory {

  private StatCardFactory() {
  }

  /**
   * Creates a statistic tile.
   *
   * @param label  tile label
   * @param values value labels shown in the tile
   * @return statistic tile container
   */
  public static VBox tile(String label, Label... values) {
    return build(label, "stat-card", "stat-label", "stat-value", values);
  }

  /**
   * Creates a compact statistic display.
   *
   * @param label      statistic label
   * @param value      value label
   * @param valueClass CSS class applied to the value
   * @return compact statistic container
   */
  public static VBox compact(String label, Label value, String valueClass) {
    return build(label.toUpperCase(), "bottom-bar-stat", "bottom-bar-label", valueClass, value);
  }

  private static VBox build(
      String label,
      String boxClass,
      String labelClass,
      String valueClass,
      Label... values
  ) {
    Label header = new Label(label);
    header.getStyleClass().add(labelClass);
    for (Label v : values) {
      v.getStyleClass().add(valueClass);
    }

    VBox box = new VBox(header);
    box.getChildren().addAll(values);
    box.getStyleClass().add(boxClass);
    return box;
  }
}
