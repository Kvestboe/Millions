package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.StartGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.market.StockDataSource;
import edu.ntnu.idatt2003.gruppe50.domain.notification.NotificationLog;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.infrastructure.csv.InvalidStockDataException;
import edu.ntnu.idatt2003.gruppe50.shared.Parse;
import edu.ntnu.idatt2003.gruppe50.shared.Validate;
import java.io.File;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Controller responsible for initializing a new game session.
 * Parses and validates user input before creating the necessary
 * game objects ({@link Player} and {@link Exchange}).
 */
public final class NewGameController {

  private final StartGameSessionUseCase startGameSession;
  private final StockDataSource stockDataSource;

  /**
   * Creates a new controller.
   *
   * @param startGameSession use case used to start the session
   * @param stockDataSource source used to load stock data from file
   */
  public NewGameController(
      StartGameSessionUseCase startGameSession,
      StockDataSource stockDataSource
  ) {
    this.startGameSession = startGameSession;
    this.stockDataSource = stockDataSource;
  }

  /**
   * Initializes and starts a new game with the provided input.
   *
   * <p>Parses the capital string, validates all input,
   * loads stocks from file, and creates the player and exchange.
   *
   * @param playerName the name of the player
   * @param capital the starting capital as a string, e.g. "10000" or "10000kr"
   * @param stockFile the CSV file containing stock data
   * @param difficulty the chosen difficulty level
   * @return the UUID of the newly created game session
   * @throws IllegalArgumentException if any input is invalid
   * @throws InvalidStockDataException if the stock file cannot be read or its
   *     contents are malformed
   */
  public UUID onStartGame(
      String playerName,
      String capital,
      File stockFile,
      Difficulty difficulty
  ) throws InvalidStockDataException {
    BigDecimal startingCapital = Parse.parseBigDecimal(capital);
    Validate.validateInput(playerName, startingCapital, stockFile);

    List<Stock> stocks = stockDataSource.readStocks(stockFile.toPath());
    Player player = createPlayer(playerName, startingCapital);
    Exchange exchange = createExchange(stocks, difficulty);

    return startGameSession.execute(
        new StartGameSessionUseCase.Request(player, exchange, difficulty)).gameId();
  }

  private Player createPlayer(String name, BigDecimal startingCapital) {
    return new Player(name, startingCapital);
  }

  private Exchange createExchange(List<Stock> stocks, Difficulty difficulty) {
    return new Exchange("Stock exchange", stocks, new TransactionFactory(), difficulty.toVolatilityProfile(), new NotificationLog());
  }
}
