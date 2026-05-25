package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.week;

import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import java.math.BigDecimal;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Popup shown when the player tries to advance the week but does not
 * have enough cash to pay the hangar rent.
 */
public class InsufficientCashPopup extends StackPane {

  /**
   * Creates the popup with the upcoming hangar cost and the player's current cash.
   *
   * @param hangarCost the rent the player must pay next week
   * @param playerCash the player's current cash
   * @param onClose called when the user closes the popup
   */
  public InsufficientCashPopup(BigDecimal hangarCost, BigDecimal playerCash, Runnable onClose) {
    Region backdrop = new Region();
    backdrop.getStyleClass().add("modal-backdrop");
    backdrop.setOnMouseClicked(e -> onClose.run());

    BigDecimal missing = hangarCost.subtract(playerCash);

    Label title = new Label("Not enough cash for hangar rent");
    title.getStyleClass().add("popup-title");

    Label explanation = new Label(
        "Every week your hangar costs " + MoneyFormat.formatCurrency(hangarCost) + ". "
            + "You only have " + MoneyFormat.formatCurrency(playerCash) + " in cash, "
            + "because the rest is tied up in shares. "
            + "Sell shares for at least " + MoneyFormat.formatCurrency(missing) + " to continue to the next week.");
    explanation.setWrapText(true);

    Button close = new Button("Got it");

    String baseStyle =
        "-fx-background-color: #FFD166;"   // -accent-gold
            + "-fx-text-fill: #061016;"          // -bg-primary
            + "-fx-background-radius: 8;"
            + "-fx-border-color: transparent;"
            + "-fx-padding: 10 18 10 18;"
            + "-fx-font-size: 14px;"
            + "-fx-font-weight: bold;"
            + "-fx-cursor: hand;";

    close.setStyle(baseStyle);

    close.setOnMouseEntered(e -> {
      close.setStyle(baseStyle);
      close.setScaleX(1.02);
      close.setScaleY(1.02);
    });
    close.setOnMouseExited(e -> {
      close.setScaleX(1.0);
      close.setScaleY(1.0);
    });
    close.setOnMousePressed(e -> {
      close.setScaleX(0.98);
      close.setScaleY(0.98);
    });
    close.setOnMouseReleased(e -> {
      close.setScaleX(1.02);
      close.setScaleY(1.02);
    });

    close.setOnAction(e -> onClose.run());

    VBox card = new VBox(15, title, explanation, close);
    card.getStyleClass().add("week-summary-popup");
    card.setMaxWidth(480);
    card.setMaxHeight(Region.USE_PREF_SIZE);

    getChildren().addAll(backdrop, card);
    StackPane.setAlignment(card, Pos.TOP_CENTER);
    StackPane.setMargin(card, new Insets(80, 0, 0, 0));
  }
}
