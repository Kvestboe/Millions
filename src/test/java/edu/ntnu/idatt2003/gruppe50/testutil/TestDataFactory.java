package edu.ntnu.idatt2003.gruppe50.testutil;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.market.VolatilityProfile;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import java.util.List;

public class TestDataFactory {

  public static Player createDefaultPlayer() {
    return new Player("Test player", bd(10000));
  }

  public static Stock createKOGStock() {
    return new Stock("KOG", "Kongsberg Gruppen", bd(330));
  }

  public static Stock createAAPLStock() {
    return new Stock("AAPL", "Apple", bd(400));
  }

  public static Exchange createDefaultExchange() {
    return new Exchange(
        "Test exchange", List.of(createKOGStock(), createAAPLStock()), new TransactionFactory(), Difficulty.MEDIUM.toVolatilityProfile());
  }

  public static GameSession createDefaultGameSession() {
    return GameSession.createNew(createDefaultPlayer(), createDefaultExchange(), Difficulty.EASY);
  }
}
