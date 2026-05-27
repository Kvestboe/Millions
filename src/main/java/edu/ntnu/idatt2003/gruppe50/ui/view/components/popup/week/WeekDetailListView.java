package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.week;

import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import java.util.List;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * Detail list view for week summary news or notifications.
 */
public class WeekDetailListView extends VBox {

  /**
   * Creates a detail list view.
   *
   * @param title   title shown above the list
   * @param items   text items to display
   * @param onBack  action run when navigating back to the summary
   * @param onClose action run when closing the popup
   */
  public WeekDetailListView(
      String title,
      List<String> items,
      Runnable onBack,
      Runnable onClose
  ) {
    setSpacing(15);

    Button backBtn = ButtonFactory.secondary("Back", onBack);

    Label header = new Label(title);
    header.getStyleClass().add("popup-title");

    HBox headerRow = new HBox(12, backBtn, header);
    headerRow.setAlignment(Pos.CENTER_LEFT);

    VBox rows = new VBox(8);
    items.forEach(t -> {
      Label l = new Label(t);
      l.setWrapText(true);
      l.getStyleClass().add("detail-value");
      rows.getChildren().add(l);
    });

    ScrollPane scroll = new ScrollPane(rows);
    scroll.setFitToWidth(true);
    scroll.setMaxHeight(280);

    Button continueBtn = ButtonFactory.styled("Continue", "primary", onClose);
    HBox actions = new HBox(continueBtn);
    actions.setAlignment(Pos.CENTER_RIGHT);

    getChildren().setAll(headerRow, scroll, actions);
  }
}
