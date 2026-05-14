package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup;

import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitOrder;
import javafx.collections.FXCollections;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.math.BigDecimal;
import java.util.function.Consumer;
import java.util.stream.IntStream;

public class OrderInputView extends VBox{

  private final OrderFormView.Side side;
  private final Stock stock;
  private final Consumer<DraftOrder> onNext;
  private final Runnable onCancel;

  private final ChoiceBox<OrderFormView.OrderType> orderTypeBox = new ChoiceBox<>();
  private final TextField quantityField = new TextField();
  private final TextField targetPriceField = new TextField();
  private final ChoiceBox<Integer> durationBox = new ChoiceBox<>();
  private final VBox limitFieldsBox = new VBox(10);
  private final Label errorLabel = new Label();

  public OrderInputView(OrderFormView.Side side, Stock stock, Consumer<DraftOrder> onNext, Runnable onCancel) {
    this.side = side;
    this.stock = stock;
    this.onNext = onNext;
    this.onCancel = onCancel;

    setSpacing(15);

    Label title = new Label((side == OrderFormView.Side.BUY ? "Buy" : "Sell") + " " + stock.getCompany());
    title.getStyleClass().add("popup-title");

    errorLabel.getStyleClass().add("popup-error");
    errorLabel.setVisible(false);
    errorLabel.setManaged(false);

    getChildren().setAll(
        title,
        buildForm(),
        errorLabel,
        buildFormActions()
    );
  }

  private VBox buildForm() {
    if (side == OrderFormView.Side.BUY) {
      orderTypeBox.setItems(FXCollections.observableArrayList(
          OrderFormView.OrderType.MARKET,
          OrderFormView.OrderType.TARGET_PRICE
      ));
    } else {
      orderTypeBox.setItems(FXCollections.observableArrayList(
          OrderFormView.OrderType.MARKET,
          OrderFormView.OrderType.TARGET_PRICE,
          OrderFormView.OrderType.STOP_LOSS
      ));
    }

    orderTypeBox.setValue(OrderFormView.OrderType.MARKET);

    orderTypeBox.setMaxWidth(Double.MAX_VALUE);
    orderTypeBox.valueProperty().addListener(
        (obs, oldVal, newVal) -> updateTargetFieldsVisibility()
    );
    orderTypeBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(OrderFormView.OrderType orderType) {
        if (orderType == null) {
          return "";
        }

        return getOrderTypeLabel(orderType);
      }

      @Override
      public OrderFormView.OrderType fromString(String string) {
        return null;
      }
    });

    quantityField.setPromptText("Quantity");
    targetPriceField.setPromptText("Target price (kr)");

    durationBox.setItems(FXCollections.observableArrayList(
        IntStream.rangeClosed(1, LimitOrder.MAX_DURATION_WEEKS).boxed().toList()));
    durationBox.setValue(LimitOrder.DEFAULT_DURATION_WEEKS);
    durationBox.setMaxWidth(Double.MAX_VALUE);

    limitFieldsBox.getChildren().setAll(
        new Label("Target price"), targetPriceField,
        new Label("Duration (weeks)"), durationBox
    );
    updateTargetFieldsVisibility();

    return new VBox(10,
        new Label("Order type"), orderTypeBox,
        new Label("Quantity"), quantityField,
        limitFieldsBox
    );
  }

  private HBox buildFormActions() {
    Button cancelBtn = new Button("Cancel");
    cancelBtn.setOnAction(e -> onCancel.run());

    Button nextBtn = new Button("Next");
    nextBtn.getStyleClass().add("primary");
    nextBtn.setOnAction(e -> handleNext());

    HBox actions = new HBox(10, cancelBtn, nextBtn);
    actions.setAlignment(Pos.CENTER_RIGHT);
    return actions;
  }

  private void updateTargetFieldsVisibility() {
    boolean showTargetFields =
        orderTypeBox.getValue() != OrderFormView.OrderType.MARKET;

    limitFieldsBox.setVisible(showTargetFields);
    limitFieldsBox.setManaged(showTargetFields);
  }

  private BigDecimal parseQuantity() {
    String text = quantityField.getText().trim();
    if (text.isEmpty()) {
      throw new IllegalArgumentException("Quantity is required");
    }
    try {
      BigDecimal quantity = new BigDecimal(text);
      if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Quantity must be positive");
      }
      return quantity;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Quantity must be a number");
    }
  }

  private BigDecimal parseTargetPrice() {
    String text = targetPriceField.getText().trim();
    if (text.isEmpty()) {
      throw new IllegalArgumentException("Target price is required");
    }
    try {
      BigDecimal price = new BigDecimal(text);
      if (price.compareTo(BigDecimal.ZERO) <= 0) {
        throw new IllegalArgumentException("Target price must be positive");
      }
      return price;
    } catch (NumberFormatException e) {
      throw new IllegalArgumentException("Target price must be a number");
    }
  }

  private void handleNext() {
    try {
      BigDecimal quantity = parseQuantity();

      DraftOrder draftOrder;

      if (orderTypeBox.getValue() == OrderFormView.OrderType.MARKET) {
        draftOrder = new DraftOrder(
            side,
            orderTypeBox.getValue(),
            stock,
            quantity,
            null,
            null
        );
      } else {
        BigDecimal targetPrice = parseTargetPrice();
        Integer duration = durationBox.getValue();

        draftOrder = new DraftOrder(
            side,
            orderTypeBox.getValue(),
            stock,
            quantity,
            targetPrice,
            duration
        );
      }

      onNext.accept(draftOrder);

    } catch (IllegalArgumentException ex) {
      showError(ex.getMessage());
    }
  }

  private void showError(String message) {
    errorLabel.setText(message);
    errorLabel.setVisible(true);
    errorLabel.setManaged(true);
  }

  private String getOrderTypeLabel(OrderFormView.OrderType orderType) {
    if (side == OrderFormView.Side.BUY) {
      if (orderType == OrderFormView.OrderType.MARKET) {
        return "Buy now";
      }

      if (orderType == OrderFormView.OrderType.TARGET_PRICE) {
        return "Buy at target price";
      }
    }

    if (side == OrderFormView.Side.SELL) {
      if (orderType == OrderFormView.OrderType.MARKET) {
        return "Sell now";
      }

      if (orderType == OrderFormView.OrderType.TARGET_PRICE) {
        return "Sell at target price";
      }

      if (orderType == OrderFormView.OrderType.STOP_LOSS) {
        return "Stop loss";
      }
    }

    return orderType.toString();
  }
}
