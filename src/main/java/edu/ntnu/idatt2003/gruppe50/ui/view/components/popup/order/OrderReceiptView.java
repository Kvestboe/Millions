package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.order;

import edu.ntnu.idatt2003.gruppe50.application.query.dto.OrderType;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.model.OrderReceipt;
import edu.ntnu.idatt2003.gruppe50.ui.model.OrderReceipt.ExecutedReceipt;
import edu.ntnu.idatt2003.gruppe50.ui.model.OrderReceipt.PendingReceipt;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import java.math.BigDecimal;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Receipt popup shown after an order has been placed.
 *
 * <p>Adapts its content to whether the order was executed immediately
 * or registered as pending.
 */
public class OrderReceiptView extends VBox {

  private final OrderReceipt receipt;
  private final Runnable onClose;

  public OrderReceiptView(OrderReceipt receipt, Runnable onClose) {
    this.receipt = receipt;
    this.onClose = onClose;

    setSpacing(18);
    getStyleClass().add("receipt");

    getChildren().setAll(
        buildHeader(),
        buildSummary(),
        buildDetails(),
        buildActions()
    );
  }

  private HBox buildHeader() {
    Region badge = new Region();
    badge.getStyleClass().addAll("receipt-badge", badgeStyle());
    badge.setMinSize(10, 10);
    badge.setMaxSize(10, 10);

    Label title = new Label(headerText());
    title.getStyleClass().add("popup-title");

    HBox header = new HBox(10, badge, title);
    header.setAlignment(Pos.CENTER_LEFT);
    return header;
  }

  private VBox buildSummary() {
    VBox box = new VBox(4);

    if (receipt instanceof ExecutedReceipt r) {
      String verb = (r.side() == OrderSide.BUY) ? "Bought" : "Sold";

      Label action = new Label(verb + " " + formatQty(r.quantity()) + " " + r.symbol());
      action.getStyleClass().add("receipt-action");

      Label amount = new Label(
          ((r.side() == OrderSide.BUY) ? "Paid " : "Received ")
              + MoneyFormat.formatCurrency(r.totalAmount())
      );
      amount.getStyleClass().add("receipt-amount");

      box.getChildren().setAll(action, amount);
    } else if (receipt instanceof PendingReceipt r) {
      String verb = (r.side() == OrderSide.BUY) ? "Buy" : "Sell";

      Label action = new Label(verb + " " + formatQty(r.quantity()) + " " + r.symbol());
      action.getStyleClass().add("receipt-action");

      Label trigger = new Label(triggerText(r));
      trigger.getStyleClass().add("receipt-trigger");
      trigger.setWrapText(true);

      box.getChildren().setAll(action, trigger);
    }

    return box;
  }

  private VBox buildDetails() {
    Region separator = new Region();
    separator.getStyleClass().add("receipt-separator");
    separator.setMinHeight(1);
    separator.setMaxHeight(1);

    VBox details = new VBox(8);

    if (receipt instanceof ExecutedReceipt r) {
      details.getChildren().addAll(
          row("Holdings (" + r.symbol() + ")", formatQty(r.newHoldingQuantity())),
          row("Week", String.valueOf(r.week()))
      );
    } else if (receipt instanceof PendingReceipt r) {
      details.getChildren().addAll(
          row("Placed",  "Week " + r.placedAtWeek()),
          row("Expires", "Week " + r.expiresAtWeek())
      );
    }

    return new VBox(12, separator, details);
  }

  private HBox buildActions() {
    Button okBtn = ButtonFactory.styled("OK", "primary", onClose);
    okBtn.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(okBtn, Priority.ALWAYS);

    HBox actions = new HBox(okBtn);
    actions.setAlignment(Pos.CENTER);
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

  private String headerText() {
    if (receipt instanceof ExecutedReceipt r) {
      return (r.side() == OrderSide.BUY) ? "Purchase completed" : "Sale completed";
    }
    return "Order placed";
  }

  private String badgeStyle() {
    return (receipt instanceof ExecutedReceipt) ? "badge-success" : "badge-pending";
  }

  private String triggerText(PendingReceipt r) {
    String price = MoneyFormat.formatCurrency(r.targetPrice());
    if (r.orderType() == OrderType.STOP_LOSS) {
      return "Triggers if price drops to " + price;
    }
    String direction = (r.side() == OrderSide.BUY) ? "drops to " : "rises to ";
    return "Triggers when price " + direction + price;
  }

  private String formatQty(BigDecimal qty) {
    return qty.stripTrailingZeros().toPlainString();
  }
}