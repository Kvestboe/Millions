package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup;

import edu.ntnu.idatt2003.gruppe50.application.query.PreviewOrderUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.PreviewOrderUseCase.Request;
import edu.ntnu.idatt2003.gruppe50.ui.model.DraftOrder;
import java.math.BigDecimal;
import java.util.UUID;

public record OrderPreview(DraftOrder draftOrder, BigDecimal price, BigDecimal subtotal, BigDecimal commission, BigDecimal tax, BigDecimal total) {

  private static final int PREVIEW_WEEK = 1;

  public static OrderPreview getOrderPreview(UUID gameId, DraftOrder draftOrder, PreviewOrderUseCase previewOrder) {
//    BigDecimal price = draftOrder.isLimit()
//        ? draftOrder.targetPrice()
//        : draftOrder.stock().salesPrice();
//
//    Share previewShare = new Share(
//        draftOrder.stock(),
//        draftOrder.quantity(),
//        price,
//        PREVIEW_WEEK
//    );
//
//    BigDecimal subtotal = price.multiply(draftOrder.quantity());
//    BigDecimal commission;
//    BigDecimal tax = BigDecimal.ZERO;
//    BigDecimal total;
//
//    if (draftOrder.side() == OrderFormView.Side.BUY) {
//      PurchaseCalculator calculator = new PurchaseCalculator(previewShare);
//
//      commission = calculator.calculateCommission();
//      total = calculator.calculateTotal();
//
//    } else {
//      SaleCalculator calculator = new SaleCalculator(previewShare);
//
//      commission = calculator.calculateCommission();
//      tax = calculator.calculateTax();
//      total = calculator.calculateTotal();
//    }

    PreviewOrderUseCase.Response response = previewOrder.execute(new Request(gameId, draftOrder));

    return new OrderPreview(
        draftOrder,
        response.price(),
        response.subtotal(),
        response.commission(),
        response.tax(),
        response.total()
    );
  }

  public boolean isLimit() {
    return draftOrder().isLimit();
  }
}