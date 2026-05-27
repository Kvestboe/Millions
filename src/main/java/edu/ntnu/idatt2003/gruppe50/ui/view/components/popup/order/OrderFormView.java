package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.order;

import edu.ntnu.idatt2003.gruppe50.application.query.PreviewOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.PreviewOrderUseCase.Request;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.ui.model.DraftOrder;
import edu.ntnu.idatt2003.gruppe50.ui.model.OrderReceipt;
import java.util.UUID;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Modal flow for entering, previewing and submitting an order.
 */
public class OrderFormView extends StackPane {

  private static final Logger LOG = Logger.getLogger(OrderFormView.class.getName());

  private final UUID gameId;
  private final OrderSide side;
  private final StockDto stock;
  private final Runnable onClose;
  private final Function<DraftOrder, OrderReceipt> onConfirmOrder;
  private final VBox card;
  private final PreviewOrderUseCase previewOrder;

  /**
   * Creates an order form modal for a stock.
   *
   * @param gameId         id of the game session
   * @param side           whether the order buys or sells
   * @param stock          stock being ordered
   * @param onClose        action run when the modal closes
   * @param onConfirmOrder function that places the order and returns a receipt
   * @param previewOrder   use case used to preview the order before confirmation
   */
  public OrderFormView(
      UUID gameId,
      OrderSide side,
      StockDto stock,
      Runnable onClose,
      Function<DraftOrder, OrderReceipt> onConfirmOrder,
      PreviewOrderUseCase previewOrder
  ) {
    this.gameId = gameId;
    this.side = side;
    this.stock = stock;
    this.onClose = onClose;
    this.onConfirmOrder = onConfirmOrder;
    this.previewOrder = previewOrder;

    Region backdrop = new Region();
    backdrop.getStyleClass().add("modal-backdrop");
    backdrop.setOnMouseClicked(e -> onClose.run());

    this.card = new VBox(15);
    card.getStyleClass().add("place-order-popup");
    card.setMaxWidth(420);
    card.setMaxHeight(Region.USE_PREF_SIZE);

    getChildren().addAll(backdrop, card);

    StackPane.setAlignment(card, Pos.TOP_CENTER);
    StackPane.setMargin(card, new Insets(80, 0, 0, 0));

    showInput();
  }

  private void showInput() {
    OrderInputView inputView = new OrderInputView(
        side,
        stock,
        this::showConfirmation,
        onClose
    );

    card.getChildren().setAll(inputView);
  }

  private void showConfirmation(DraftOrder draftOrder) {
    PreviewOrderUseCase.Response preview = previewOrder.execute(new Request(
        gameId,
        draftOrder.stock().symbol(),
        draftOrder.quantity(),
        draftOrder.side(),
        draftOrder.targetPrice()
    ));

    OrderConfirmationView confirmationView = new OrderConfirmationView(
        draftOrder,
        preview,
        this::showInput,
        this::confirmOrder
    );

    card.getChildren().setAll(confirmationView);
  }

  private void confirmOrder(DraftOrder draftOrder) {
    try {
      OrderReceipt receipt = onConfirmOrder.apply(draftOrder);
      showReceipt(receipt);
    } catch (RuntimeException ex) {
      LOG.log(Level.WARNING, "Failed to confirm order", ex);
    }
  }

  private void showReceipt(OrderReceipt receipt) {
    OrderReceiptView receiptView = new OrderReceiptView(receipt, onClose);
    card.getChildren().setAll(receiptView);
  }
}