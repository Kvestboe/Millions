package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.order;

import edu.ntnu.idatt2003.gruppe50.application.query.PreviewOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.model.DraftOrder;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import java.util.function.Consumer;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Confirmation step shown before an order is placed.
 */
public class OrderConfirmationView extends VBox {

  private final DraftOrder draftOrder;
  private final PreviewOrderUseCase.Response preview;
  private final Runnable onBack;
  private final Consumer<DraftOrder> onConfirm;

  /**
   * Creates an order confirmation view.
   *
   * @param draftOrder draft order being confirmed
   * @param preview    calculated order preview
   * @param onBack     action run when the user goes back
   * @param onConfirm  callback receiving the confirmed draft order
   */
  public OrderConfirmationView(DraftOrder draftOrder, PreviewOrderUseCase.Response preview,
                               Runnable onBack, Consumer<DraftOrder> onConfirm) {
    this.draftOrder = draftOrder;
    this.preview = preview;
    this.onBack = onBack;
    this.onConfirm = onConfirm;

    setSpacing(15);

    getChildren().setAll(
        buildTitle(),
        buildDetails(),
        buildTotal(),
        buildValidationMessage(),
        buildActions()
    );
  }

  private Label buildTitle() {
    String action = draftOrder.side() == OrderSide.BUY ? "buy" : "sell";

    Label title = new Label("Confirm " + action);
    title.getStyleClass().add("popup-title");
    return title;
  }

  private VBox buildDetails() {

    String orderTypeLabel = draftOrder.label();

    VBox details = new VBox(8,
        row("Stock", draftOrder.stock().company()),
        row("Order type", orderTypeLabel),
        row("Quantity", draftOrder.quantity().toString()),
        row("Price", MoneyFormat.formatCurrency(preview.price())),
        row("Subtotal", MoneyFormat.formatCurrency(preview.subtotal())),
        row("Commission", MoneyFormat.formatCurrency(preview.commission()))
    );

    if (draftOrder.side() == OrderSide.SELL) {
      details.getChildren().add(row("Tax", MoneyFormat.formatCurrency(preview.tax())));
    }

    if (draftOrder.isLimit()) {
      details.getChildren().add(row("Duration", draftOrder.duration() + " weeks")
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
    Button backBtn = ButtonFactory.secondary("Back", onBack);
    Button confirmBtn = ButtonFactory.styled(
        "Confirm",
        "primary",
        () -> onConfirm.accept(draftOrder)
    );

    confirmBtn.setDisable(hasInsufficientCash());

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

  /**
   * Returns true if the player cannot afford this market buy order.
   *
   * @return true when the order is a market buy and total cost exceeds available cash
   */
  private boolean hasInsufficientCash() {
    return draftOrder.side() == OrderSide.BUY
        && !draftOrder.isLimit()
        && preview.total().compareTo(preview.availableCash()) > 0;
  }

  /**
   * Builds a validation label for order confirmation problems.
   *
   * @return a visible error label when the order cannot be confirmed,
   *     otherwise a hidden unmanaged label
   */
  private Label buildValidationMessage() {
    Label label = new Label();

    label.getStyleClass().add("popup-error");

    if (hasInsufficientCash()) {
      label.setText("Not enough cash. Available: "
          + MoneyFormat.formatCurrency(preview.availableCash()));
      label.setVisible(true);
      label.setManaged(true);
    } else {
      label.setVisible(false);
      label.setManaged(false);
    }

    return label;
  }
}