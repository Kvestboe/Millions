package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.PurchaseCalculator;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.SaleCalculator;
import java.math.BigDecimal;

public record OrderPreview(DraftOrder draftOrder, BigDecimal price, BigDecimal subtotal, BigDecimal commission, BigDecimal tax, BigDecimal total) {

  private static final int PREVIEW_WEEK = 1;

  public static OrderPreview from(DraftOrder draftOrder) {
    BigDecimal price = draftOrder.isLimit()
        ? draftOrder.targetPrice()
        : draftOrder.stock().getSalesPrice();

    Share previewShare = new Share(
        draftOrder.stock(),
        draftOrder.quantity(),
        price,
        PREVIEW_WEEK
    );

    BigDecimal subtotal = price.multiply(draftOrder.quantity());
    BigDecimal commission;
    BigDecimal tax = BigDecimal.ZERO;
    BigDecimal total;

    if (draftOrder.side() == OrderFormView.Side.BUY) {
      PurchaseCalculator calculator = new PurchaseCalculator(previewShare);

      commission = calculator.calculateCommission();
      total = calculator.calculateTotal();

    } else {
      SaleCalculator calculator = new SaleCalculator(previewShare);

      commission = calculator.calculateCommission();
      tax = calculator.calculateTax();
      total = calculator.calculateTotal();
    }

    return new OrderPreview(
        draftOrder,
        price,
        subtotal,
        commission,
        tax,
        total
    );
  }

  public boolean isLimit() {
    return draftOrder().isLimit();
  }
}