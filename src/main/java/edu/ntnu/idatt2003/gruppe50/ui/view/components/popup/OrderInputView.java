package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup;

import edu.ntnu.idatt2003.gruppe50.application.query.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitOrder;
import edu.ntnu.idatt2003.gruppe50.ui.model.DraftOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.application.query.OrderType;
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

  private final OrderSide side;
  private final StockDto stock;
  private final Consumer<DraftOrder> onNext;
  private final Runnable onCancel;

  private final ChoiceBox<OrderType> orderTypeBox = new ChoiceBox<>();
  private final TextField quantityField = new TextField();
  private final TextField targetPriceField = new TextField();
  private final ChoiceBox<Integer> durationBox = new ChoiceBox<>();
  private final VBox limitFieldsBox = new VBox(10);
  private final Label errorLabel = new Label();

  public OrderInputView(OrderSide side, StockDto stock, Consumer<DraftOrder> onNext, Runnable onCancel) {
    this.side = side;
    this.stock = stock;
    this.onNext = onNext;
    this.onCancel = onCancel;

    setSpacing(15);

    Label title = new Label((side == OrderSide.BUY ? "Buy" : "Sell") + " " + stock.company());
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
    if (side == OrderSide.BUY) {
      orderTypeBox.setItems(FXCollections.observableArrayList(
          OrderType.MARKET,
          OrderType.TARGET_PRICE
      ));
    } else {
      orderTypeBox.setItems(FXCollections.observableArrayList(
          OrderType.MARKET,
          OrderType.TARGET_PRICE,
          OrderType.STOP_LOSS
      ));
    }

    orderTypeBox.setValue(OrderType.MARKET);

    orderTypeBox.setMaxWidth(Double.MAX_VALUE);
    orderTypeBox.valueProperty().addListener(
        (obs, oldVal, newVal) -> updateTargetFieldsVisibility()
    );
    orderTypeBox.setConverter(new StringConverter<>() {
      @Override
      public String toString(OrderType orderType) {
        if (orderType == null) {
          return "";
        }

        return DraftOrder.label(side, orderType);
      }

      @Override
      public OrderType fromString(String string) {
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
        orderTypeBox.getValue() != OrderType.MARKET;

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

      if (orderTypeBox.getValue() == OrderType.MARKET) {
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

}
