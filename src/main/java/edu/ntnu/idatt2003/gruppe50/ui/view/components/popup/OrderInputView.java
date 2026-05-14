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
    orderTypeBox.setItems(FXCollections.observableArrayList(OrderFormView.OrderType.values()));
    orderTypeBox.setValue(OrderFormView.OrderType.MARKET);
    orderTypeBox.setMaxWidth(Double.MAX_VALUE);
    orderTypeBox.valueProperty().addListener((obs, oldVal, newVal) -> updateLimitVisibility());

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
    updateLimitVisibility();

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

  private void updateLimitVisibility() {
    boolean isLimit = orderTypeBox.getValue() == OrderFormView.OrderType.LIMIT;
    limitFieldsBox.setVisible(isLimit);
    limitFieldsBox.setManaged(isLimit);
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
        draftOrder = new DraftOrder(side, stock, quantity, null, null);
      } else {
        BigDecimal targetPrice = parseTargetPrice();
        int duration = durationBox.getValue();
        draftOrder = new DraftOrder(side, stock, quantity, targetPrice, duration);
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
