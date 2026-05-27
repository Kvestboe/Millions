package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.OrderSide;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.PurchaseCalculator;
import edu.ntnu.idatt2003.gruppe50.domain.trade.calculator.SaleCalculator;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Comparator;
import java.util.UUID;

/**
 * Calculates a preview of an order before it is placed or executed.
 */
public final class PreviewOrderUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load game sessions
   */
  public PreviewOrderUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Calculates estimated order values for a buy or sell order.
   *
   * @param request input with game id, symbol, quantity, side and optional target price
   * @return response containing price, fees, tax, total and available cash
   * @throws GameSessionNotFoundException if the session does not exist
   * @throws IllegalStateException if a sell preview requests more shares than the player owns
   */
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

    return new Response(price, subTotal, commission, tax, total, week,
        session.getPlayer().getMoney());
  }

  /**
   * Input for previewing an order.
   *
   * @param gameId      id of the game session
   * @param symbol      stock symbol for the order
   * @param quantity    order quantity
   * @param side        whether the order is a buy or sell order
   * @param targetPrice optional target price, or null to use the current market price
   */
  public record Request(
      UUID gameId,
      String symbol,
      BigDecimal quantity,
      OrderSide side,
      BigDecimal targetPrice
  ) {
  }

  /**
   * Output from previewing an order.
   *
   * @param price         price used in the preview
   * @param subtotal      price multiplied by quantity before fees and tax
   * @param commission    estimated commission
   * @param tax           estimated tax
   * @param total         estimated final order total
   * @param week          current market week
   * @param availableCash player's available cash
   */
  public record Response(
      BigDecimal price,
      BigDecimal subtotal,
      BigDecimal commission,
      BigDecimal tax,
      BigDecimal total,
      int week,
      BigDecimal availableCash
  ) {
  }

}
