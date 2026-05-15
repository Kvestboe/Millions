package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.PurchaseCalculator;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.SaleCalculator;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
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


    BigDecimal price = request.targetPrice() != null
        ? request.targetPrice()
        : session.getExchange().getStock(request.symbol()).getSalesPrice();


    BigDecimal subTotal = price.multiply(request.quantity());
    BigDecimal commission;
    BigDecimal tax;
    BigDecimal total;

    if (request.side() == OrderSide.BUY) {
      PurchaseCalculator calculator = new PurchaseCalculator(price, request.quantity());
      commission = calculator.calculateCommission();
      tax = calculator.calculateTax();
      total = calculator.calculateTotal();
    } else {
      BigDecimal remaining = request.quantity();
      BigDecimal costBasis = BigDecimal.ZERO;

      for (Share lot : session.getPlayer().getPortfolio().getShares(request.symbol()).stream()
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

      BigDecimal purchasePrice = costBasis.divide(request.quantity(), 10, RoundingMode.HALF_UP);
      SaleCalculator calculator = new SaleCalculator(purchasePrice, price, request.quantity());
      commission = calculator.calculateCommission();
      tax = calculator.calculateTax();
      total = calculator.calculateTotal();
    }

    int week = session.getExchange().getWeek();

    return new Response(price, subTotal, commission, tax, total, week);
  }


  public record Request(
      UUID gameId,
      String symbol,
      BigDecimal quantity,
      OrderSide side,
      BigDecimal targetPrice
  ) {}

  public record Response(
      BigDecimal price,
      BigDecimal subtotal,
      BigDecimal commission,
      BigDecimal tax,
      BigDecimal total,
      int week
  ) {}

}
