package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.command.StartGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.infrastructure.CSVFileHandler;
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

  public NewGameController(StartGameSessionUseCase startGameSession) {
    this.startGameSession = startGameSession;
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
   */
  public UUID onStartGame(String playerName, String capital, File stockFile, Difficulty difficulty) {
    BigDecimal startingCapital = Parse.parseBigDecimal(capital);
    Validate.validateInput(playerName, startingCapital, stockFile);

    List<Stock> stocks = loadStocks(stockFile);
    Player player = createPlayer(playerName, startingCapital);
    Exchange exchange = createExchange(stocks, difficulty);

    return startGameSession.execute(
        new StartGameSessionUseCase.Request(player, exchange, difficulty)).gameId();
  }

  private List<Stock> loadStocks(File stockFile) {
    return CSVFileHandler.readLines(stockFile.toPath());
  }

  private Player createPlayer(String name, BigDecimal startingCapital) {
    return new Player(name, startingCapital);
  }

  private Exchange createExchange(List<Stock> stocks, Difficulty difficulty) {
    return new Exchange("Stock exchange", stocks, new TransactionFactory(), difficulty.toVolatilityProfile());
  }
}
