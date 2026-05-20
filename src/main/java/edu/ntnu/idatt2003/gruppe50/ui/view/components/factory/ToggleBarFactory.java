package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import java.util.ArrayList;
import java.util.List;
import java.util.function.IntConsumer;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;

public final class ToggleBarFactory {

  /**
   * Row of mutually-exclusive toggle buttons. The selected one carries the
   * .segmented-btn-active class.
   *
   * @param labels       button labels in display order
   * @param initialIndex which button starts active
   * @param onSelected   fired with the index of the newly selected button
   */
  public static HBox of(List<String> labels, int initialIndex, IntConsumer onSelected) {
    HBox container = new HBox();
    container.getStyleClass().add("segmented-control");

    List<Button> buttons = new ArrayList<>();
    for (int i = 0; i < labels.size(); i++) {
      int index = i;
      Button btn = ButtonFactory.styled(labels.get(i), "segmented-btn", () -> {
        buttons.forEach(b -> b.getStyleClass().remove("segmented-btn-active"));
        buttons.get(index).getStyleClass().add("segmented-btn-active");
        onSelected.accept(index);
      });
      buttons.add(btn);
      container.getChildren().add(btn);
    }

    if (initialIndex >= 0 && initialIndex < buttons.size()) {
      buttons.get(initialIndex).getStyleClass().add("segmented-btn-active");
    }
    return container;
  }
}
