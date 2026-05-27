package edu.ntnu.idatt2003.gruppe50.infrastructure.persistence;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSessionState;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.notification.NotificationLog;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Portfolio;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.shop.CoinExchange;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Purchase;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Sale;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionArchive;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitBuyOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.LimitSellOrder;
import edu.ntnu.idatt2003.gruppe50.domain.trade.order.StopLossOrder;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.CoinExchangeDto;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.ExchangeDto;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.GameSaveDto;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.LimitOrderDto;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.PlayerDto;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.ShareDto;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.infrastructure.persistence.dto.TransactionDto;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

/** Maps game sessions to and from persistence DTOs used for JSON saves. */
public class GameSaveMapper {

  /**
   * Converts a game session into a serializable save DTO.
   *
   * @param session game session to convert
   * @return DTO containing the complete save state
   */
  public static GameSaveDto toDto(GameSession session) {
    Player player = session.getPlayer();
    Exchange exchange = session.getExchange();

    List<ShareDto> shares = player.getPortfolio().getShares().stream()
        .map(s -> new ShareDto(
            s.getStock().getSymbol(),
            s.getQuantity(),
            s.getPurchasePrice(),
            s.getPurchaseWeek()
        )).toList();

    List<TransactionDto> transactions = player.getTransactionArchive().getTransactions().stream()
        .map(t -> new TransactionDto(
            t instanceof Purchase ? "Purchase" : "Sale",
            t.getShare().getStock().getSymbol(),
            t.getShare().getQuantity(),
            t.getShare().getPurchasePrice(),
            t.getWeek(),
            t.getShare().getPurchaseWeek(),
            t.getBatchId().toString()
        )).toList();

    List<StockDto> stocks = exchange.getStocks().stream()
        .map(s -> new StockDto(
            s.getSymbol(),
            s.getCompany(),
            s.getHistoricalPrices()
        )).toList();

    List<LimitOrderDto> orders = exchange.getPendingOrders().stream()
        .map(o -> new LimitOrderDto(
            o.label(),
            o.getStock().getSymbol(),
            o.getTargetPrice(),
            o.getQuantity(),
            o.getCreatedWeek(),
            o.getExpiryWeek()
        )).toList();

    return new GameSaveDto(
        session.getGameId().toString(),
        session.getState().name(),
        session.getDifficulty().name(),
        session.getRunStartedAt().toString(),
        session.getLastPlayed().toString(),
        session.getNetWorthHistory(),
        new PlayerDto(
            player.getName(),
            player.getStartingMoney(),
            player.getMoney(),
            player.getCoins(),
            player.getActiveTheme(),
            List.copyOf(player.getOwnedThemes()),
            shares,
            transactions
        ),
        new ExchangeDto(exchange.getName(), exchange.getWeek(), stocks, orders),
        new CoinExchangeDto(session.getCoinExchange().getPriceHistory())
    );
  }

  /**
   * Reconstructs a game session from a saved DTO.
   *
   * @param dto saved game-session data
   * @param factory transaction factory used by the rehydrated exchange
   * @return reconstructed game session
   * @throws IllegalArgumentException if saved enum values or ids are invalid
   */
  public static GameSession fromDto(GameSaveDto dto, TransactionFactory factory) {
    Map<String, Stock> stockMap = dto.exchange().stocks().stream()
        .map(s -> Stock.rehydrate(s.symbol(), s.company(), s.prices()))
        .collect(Collectors.toMap(Stock::getSymbol, s -> s));

    Map<UUID, Share> shareMap = dto.player().shares().stream()
        .map(s -> new Share(stockMap.get(s.stockSymbol()), s.quantity(), s.purchasePrice(), s.purchaseWeek()))
        .collect(Collectors.toMap(Share::getShareId, s -> s));

    TransactionArchive archive = new TransactionArchive();
    for (TransactionDto t : dto.player().transactions()) {
      Stock stock = stockMap.get(t.stockSymbol());
      Share share = new Share(stock, t.quantity(), t.purchasePrice(), t.purchaseWeek());
      UUID batchId = UUID.fromString(t.batchId());
      archive.add(t.type().equals("Purchase")
          ? new Purchase(share, t.week(), batchId)
          : new Sale(share, t.week(), batchId));
    }

    Player player = Player.rehydrate(
        dto.player().name(),
        dto.player().startingMoney(),
        dto.player().money(),
        dto.player().coins(),
        dto.player().activeTheme(),
        dto.player().ownedThemes(),
        Portfolio.rehydrate(shareMap),
        archive
    );

    List<LimitOrder> orders = dto.exchange().pendingOrders().stream()
        .map(o -> buildOrder(o, stockMap, player))
        .toList();

    Difficulty difficulty = Difficulty.valueOf(dto.difficulty());

    Exchange exchange = Exchange.rehydrate(
        dto.exchange().name(),
        stockMap,
        factory,
        dto.exchange().week(),
        orders,
        difficulty.toVolatilityProfile(),
        new NotificationLog()
    );

    CoinExchange coinExchange = null;
    if (dto.coinExchange() != null && dto.coinExchange().priceHistory() != null
        && !dto.coinExchange().priceHistory().isEmpty()) {
      coinExchange = CoinExchange.rehydrate(
          player.getStartingMoney(),
          dto.coinExchange().priceHistory()
      );
    }

    return GameSession.rehydrate(
        UUID.fromString(dto.gameId()),
        player,
        exchange,
        dto.difficulty() != null ? Difficulty.valueOf(dto.difficulty()) : Difficulty.EASY,
        GameSessionState.valueOf(dto.state()),
        parseDateTime(dto.runStartedAt()),
        parseDateTime(dto.lastPlayed()),
        dto.netWorthHistory(),
        coinExchange
    );
  }

  /**
   * Parses a date string that may be either ISO LocalDateTime ("2024-01-15T10:30:00")
   * or a legacy ISO LocalDate ("2024-01-15") from older save files.
   */
  private static LocalDateTime parseDateTime(String value) {
    try {
      return LocalDateTime.parse(value);
    } catch (Exception e) {
      return LocalDate.parse(value).atStartOfDay();
    }
  }

  private static LimitOrder buildOrder(LimitOrderDto o, Map<String, Stock> stockMap, Player player) {
    Stock stock = stockMap.get(o.stockSymbol());
    return switch (o.type()) {
      case "Buy at target price" -> new LimitBuyOrder(stock, player, o.targetPrice(), o.quantity(), o.createdWeek(), o.expiryWeek());
      case "Sell at target price" -> new LimitSellOrder(stock, player, o.targetPrice(), o.quantity(), o.createdWeek(), o.expiryWeek());
      case "Stop loss" -> new StopLossOrder(stock, player, o.targetPrice(), o.quantity(), o.createdWeek(), o.expiryWeek());
      default -> throw new IllegalArgumentException("Unknown order type: " + o.type());
    };
  }
}
