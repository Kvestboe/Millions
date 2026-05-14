package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup;

import edu.ntnu.idatt2003.gruppe50.application.query.PreviewOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.StockDto;
import edu.ntnu.idatt2003.gruppe50.ui.model.DraftOrder;
import java.util.UUID;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.util.function.Consumer;

public class OrderFormView extends StackPane {

  public enum Side { BUY, SELL }

  public enum OrderType {
    MARKET,
    TARGET_PRICE,
    STOP_LOSS
  }

  private final UUID gameId;
  private final Side side;
//  private final Stock stock;
  private final StockDto stock;
  private final Runnable onClose;
  private final Consumer<DraftOrder> onConfirmOrder;
  private final VBox card;
  private final PreviewOrderUseCase previewOrder;

//  public OrderFormView(Side side, Stock stock, Runnable onClose, Consumer<DraftOrder> onConfirmOrder) {
  public OrderFormView(
      UUID gameId,
      Side side,
      StockDto stock,
      Runnable onClose,
      Consumer<DraftOrder> onConfirmOrder,
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
//    OrderPreview preview = OrderPreview.from(draftOrder);
    OrderPreview preview = OrderPreview.getOrderPreview(gameId, draftOrder, previewOrder);

    OrderConfirmationView confirmationView = new OrderConfirmationView(
        preview,
        this::showInput,
        this::confirmOrder
    );

    card.getChildren().setAll(confirmationView);
  }

  private void confirmOrder(DraftOrder draftOrder) {
    onConfirmOrder.accept(draftOrder);
    onClose.run();
  }
}