package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup;

import java.util.function.Consumer;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class OrderConfirmationView extends VBox {

  private final OrderPreview preview;
  private final Runnable onBack;
  private final Consumer<DraftOrder> onConfirm;

  public OrderConfirmationView(OrderPreview preview, Runnable onBack, Consumer<DraftOrder> onConfirm) {
    this.preview = preview;
    this.onBack = onBack;
    this.onConfirm = onConfirm;

    setSpacing(15);

    getChildren().setAll(buildTitle(), buildDetails(), buildTotal(), buildActions());
  }

  private Label buildTitle() {
    String action = preview.draftOrder().side() == OrderFormView.Side.BUY ? "buy" : "sell";

    Label title = new Label("Confirm " + action);
    title.getStyleClass().add("popup-title");
    return title;
  }

  private VBox buildDetails() {
    DraftOrder draftOrder = preview.draftOrder();

    String action = draftOrder.side() == OrderFormView.Side.BUY ? "buy" : "sell";
    String orderTypeLabel = draftOrder.isLimit()
        ? "Limit " + action
        : "Market " + action;

    VBox details = new VBox(8,
        row("Stock", draftOrder.stock().getCompany()),
        row("Order type", orderTypeLabel),
        row("Quantity", draftOrder.quantity().toString()),
        row("Price", MoneyFormat.formatCurrency(preview.price())),
        row("Subtotal", MoneyFormat.formatCurrency(preview.subtotal())),
        row("Commission", MoneyFormat.formatCurrency(preview.commission()))
    );

    if (draftOrder.side() == OrderFormView.Side.SELL) {
      details.getChildren().add(row("Tax", MoneyFormat.formatCurrency(preview.tax())));
    }

    if (preview.isLimit()) {
      details.getChildren().add(row("Duration", draftOrder.duration() + " weeks")
//                                row("Expires, Week " + preview.expirationWeek()
                                  );
    }

    return details;
  }

  private Label buildTotal() {
    Label totalLabel = new Label("Estimated total: " + MoneyFormat.formatCurrency(preview.total()));
    totalLabel.getStyleClass().add("popup-total");
    return totalLabel;
  }

  private HBox buildActions() {
    Button backBtn = new Button("Back");
    backBtn.setOnAction(e -> onBack.run());

    Button confirmBtn = new Button("Confirm");
    confirmBtn.getStyleClass().add("primary");
    confirmBtn.setOnAction(e -> onConfirm.accept(preview.draftOrder()));

    HBox actions = new HBox(10, backBtn, confirmBtn);
    actions.setAlignment(Pos.CENTER_RIGHT);
    return actions;
  }

  private HBox row(String label, String value) {
    Label l = new Label(label);
    l.getStyleClass().add("detail-label");

    Label v = new Label(value);
    v.getStyleClass().add("detail-value");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    return new HBox(l, spacer, v);
  }

}