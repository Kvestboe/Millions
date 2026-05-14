package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.PurchaseCalculator;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.SaleCalculator;
import edu.ntnu.idatt2003.gruppe50.ui.model.DraftOrder;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.OrderFormView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.UUID;

public final class PreviewOrderUseCase {

  private final GameSessionRepository repository;

  public PreviewOrderUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public Response execute(Request request) {

    GameSession session =
        repository.findById(request.gameId).orElseThrow(GameSessionNotFoundException::new);

    DraftOrder draftOrder = request.draftOrder();

    BigDecimal price = draftOrder.isLimit()
        ? draftOrder.targetPrice()
        : draftOrder.stock().salesPrice();


    BigDecimal subTotal = price.multiply(draftOrder.quantity());
    BigDecimal commission;
    BigDecimal tax;
    BigDecimal total;

    if (draftOrder.side() == OrderFormView.Side.BUY) {
      PurchaseCalculator calculator = new PurchaseCalculator(price, draftOrder.quantity());
      commission = calculator.calculateCommission();
      tax = calculator.calculateTax();
      total = calculator.calculateTotal();
    } else {
      BigDecimal remaining = draftOrder.quantity();
      BigDecimal costBasis = BigDecimal.ZERO;

      for (Share lot : session.getPlayer().getPortfolio().getShares(draftOrder.stock().symbol()).stream()
          .sorted(Comparator.comparingInt(Share::getPurchaseWeek))
          .toList()) {
        if (remaining.compareTo(BigDecimal.ZERO) <= 0) {
          break;
        }
        BigDecimal consumed = lot.getQuantity().min(remaining);
        costBasis = costBasis.add(consumed.multiply(lot.getPurchasePrice()));
        remaining = remaining.subtract(consumed);
      }

      if (remaining.compareTo(BigDecimal.ZERO) > 0) {
        throw new IllegalStateException("Player does not own enough shares");
      }

      BigDecimal purchasePrice = costBasis.divide(draftOrder.quantity(), 10, RoundingMode.HALF_UP);
      SaleCalculator calculator = new SaleCalculator(purchasePrice, price, draftOrder.quantity());
      commission = calculator.calculateCommission();
      tax = calculator.calculateTax();
      total = calculator.calculateTotal();
    }

    int week = session.getExchange().getWeek();

    return new Response(price, subTotal, commission, tax, total, week);
  }

  public record Request(UUID gameId, DraftOrder draftOrder) {}

  public record Response(
      BigDecimal price,
      BigDecimal subtotal,
      BigDecimal commission,
      BigDecimal tax,
      BigDecimal total,
      int week
  ) {}

}
