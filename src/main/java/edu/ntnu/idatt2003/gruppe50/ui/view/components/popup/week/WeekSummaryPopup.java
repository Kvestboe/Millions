package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.week;

import edu.ntnu.idatt2003.gruppe50.ui.model.WeekSummary;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class WeekSummaryPopup extends StackPane {

  private final WeekSummary summary;
  private final Runnable onClose;
  private final VBox card;

  public WeekSummaryPopup(WeekSummary summary, Runnable onClose) {
    this.summary = summary;
    this.onClose = onClose;

    Region backdrop = new Region();
    backdrop.getStyleClass().add("modal-backdrop");
    backdrop.setOnMouseClicked(e -> onClose.run());

    this.card = new VBox(15);
    card.getStyleClass().add("week-summary-popup");
    card.setMaxWidth(560);
    card.setMaxHeight(Region.USE_PREF_SIZE);

    getChildren().addAll(backdrop, card);

    StackPane.setAlignment(card, Pos.TOP_CENTER);
    StackPane.setMargin(card, new Insets(80, 0, 0, 0));

    showSummary();
  }

  private void showSummary() {
    WeekSummaryView view = new WeekSummaryView(
        summary,
        this::showNews,
        this::showNotifications,
        onClose
    );
    card.getChildren().setAll(view);
  }

  private void showNews() {
    WeekDetailListView view = new WeekDetailListView(
        "📰  News",
        summary.news(),
        this::showSummary,
        onClose
    );
    card.getChildren().setAll(view);
  }

  private void showNotifications() {
    WeekDetailListView view = new WeekDetailListView(
        "🔔  Notifications",
        summary.notifications(),
        this::showSummary,
        onClose
    );
    card.getChildren().setAll(view);
  }
}
