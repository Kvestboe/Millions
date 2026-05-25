package edu.ntnu.idatt2003.gruppe50.domain.game;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultExchange;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultPlayer;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.market.VolatilityProfile;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

public class GameSessionTest {

  private GameSession session;

  @BeforeEach
  void setUp() {
    session = GameSession.createNew(createDefaultPlayer(), createDefaultExchange(), Difficulty.EASY);
  }

  @Test
  void createNew_validInput_createsActiveSession() {
    GameSession newSession = GameSession.createNew(createDefaultPlayer(), createDefaultExchange(), Difficulty.EASY);

    assertNotNull(newSession.getGameId());
    assertEquals(GameSessionState.ACTIVE, newSession.getState());
    assertEquals(LocalDate.now(), newSession.getRunStartedAt().toLocalDate());
    assertEquals(LocalDate.now(), newSession.getLastPlayed().toLocalDate());
  }

  @Test
  void createNew_nullPlayer_throwsException() {
    assertThrows(
        IllegalArgumentException.class, () -> GameSession.createNew(null, createDefaultExchange(), Difficulty.EASY));
  }

  @Test
  void createNew_nullExchange_throwsException() {
    assertThrows(
        IllegalArgumentException.class, () -> GameSession.createNew(createDefaultPlayer(), null, Difficulty.EASY));
  }

  @Nested
  class RehydrateGameSessionTests {

    @Test
    void rehydrate_validInput_restoresFields() {
      UUID gameId = UUID.randomUUID();
      LocalDateTime runStartedAt = LocalDateTime.now().minusDays(10);
      LocalDateTime lastPlayed = LocalDateTime.now().minusDays(2);

      GameSession rehydrated =
          GameSession.rehydrate(
              gameId,
              createDefaultPlayer(),
              createDefaultExchange(),
              Difficulty.EASY,
              GameSessionState.FINISHED,
              runStartedAt,
              lastPlayed,
              List.of(bd(20)));

      assertEquals(gameId, rehydrated.getGameId());
      assertEquals(GameSessionState.FINISHED, rehydrated.getState());
      assertEquals(runStartedAt, rehydrated.getRunStartedAt());
      assertEquals(lastPlayed, rehydrated.getLastPlayed());
    }

    @Test
    void rehydrate_nullGameId_throwsException() {
      assertThrows(
          IllegalArgumentException.class,
          () ->
              GameSession.rehydrate(
                  null,
                  createDefaultPlayer(),
                  createDefaultExchange(),
                  Difficulty.EASY,
                  GameSessionState.ACTIVE,
                  LocalDateTime.now(),
                  LocalDateTime.now(),
                  List.of(bd(20))));
    }

    @Test
    void rehydrate_nullLastPlayed_throwsException() {
      assertThrows(
          IllegalArgumentException.class,
          () ->
              GameSession.rehydrate(
                  UUID.randomUUID(),
                  createDefaultPlayer(),
                  createDefaultExchange(),
                  Difficulty.EASY,
                  GameSessionState.ACTIVE,
                  LocalDateTime.now(),
                  null,
                  List.of(bd(20))));
    }
  }

  @Test
  void finish_setsStateToFinished() {
    session.finish();

    assertEquals(GameSessionState.FINISHED, session.getState());
  }

  @Test
  void markOpened_updatesLastPlayedToToday() {
    GameSession rehydrated =
        GameSession.rehydrate(
            UUID.randomUUID(),
            createDefaultPlayer(),
            createDefaultExchange(),
            Difficulty.EASY,
            GameSessionState.ACTIVE,
            LocalDateTime.now().minusDays(10),
            LocalDateTime.now().minusDays(1),
            List.of(bd(20)));

    rehydrated.markOpened();

    assertEquals(LocalDate.now(), rehydrated.getLastPlayed().toLocalDate());
  }

  @Test
  void buy_whenFinished_throwsException() {
    session.finish();

    assertThrows(GameSessionFinishedException.class, () -> session.buy("AAPL", bd(1)));
  }

  @Test
  void sell_whenFinished_throwsException() {
    session.buy("AAPL", bd(1));
    Share boughtShare = session.getPlayer().getPortfolio().getShares().getFirst();
    session.finish();

    assertThrows(GameSessionFinishedException.class, () -> session.sell(boughtShare.getStock().getSymbol(), boughtShare.getQuantity()));
  }

  @Test
  void advanceWeek_whenFinished_throwsException() {
    session.finish();

    assertThrows(GameSessionFinishedException.class, session::advanceWeek);
  }

  @Nested
  class EvaluateOutcomeTests {

    @Test
    void evaluateOutcome_netWorthAboveOneMillion_returnsWon() {
      Player richPlayer = new Player("Rich", new BigDecimal("2000000"));
      GameSession richSession = GameSession.createNew(richPlayer, createDefaultExchange(),
          Difficulty.EASY);

      assertEquals(GameOutcome.WON, richSession.evaluateOutcome());
    }

    @Test
    void evaluateOutcome_netWorthAtOneMillion_returnsWon() {
      Player richPlayer = new Player("Rich", new BigDecimal("1000000"));
      GameSession richSession = GameSession.createNew(richPlayer, createDefaultExchange(),
          Difficulty.EASY);

      assertEquals(GameOutcome.WON, richSession.evaluateOutcome());
    }

    @Test
    void evaluateOutcome_netWorthBelowGameOverThreshold_returnsLost() {
      session.getPlayer().withdrawMoney(new BigDecimal("6500"));

      assertEquals(GameOutcome.LOST, session.evaluateOutcome());
    }

    @Test
    void evaluateOutcome_netWorthBetweenThresholdAndMillion_returnsOngoing() {
      assertEquals(GameOutcome.ONGOING, session.evaluateOutcome());
    }

    @Test
    void advanceWeek_outcomeIsLost_finishesSession() {
      session.getPlayer().withdrawMoney(new BigDecimal("6500"));

      session.advanceWeek();

      assertEquals(GameSessionState.FINISHED, session.getState());
    }
  }

  @Nested
  class PlaceOrderTests {

    @Test
    void placeBuyLimitOrder_validInput_addsOrderToPending() {
      session.placeBuyLimitOrder("AAPL", bd(1), bd(100), 3);

      assertEquals(1, session.getPendingOrders().size());
    }

    @Test
    void placeSellLimitOrder_validInput_addsOrderToPending() {
      session.placeSellLimitOrder("AAPL", bd(1), bd(500), 3);

      assertEquals(1, session.getPendingOrders().size());
    }

    @Test
    void placeStopLossOrder_validInput_addsOrderToPending() {
      session.placeStopLossOrder("AAPL", bd(1), bd(100), 3);

      assertEquals(1, session.getPendingOrders().size());
    }

    @Test
    void placeBuyLimitOrder_blankSymbol_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> session.placeBuyLimitOrder("", bd(1), bd(100), 3));
    }

    @Test
    void placeBuyLimitOrder_zeroQuantity_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> session.placeBuyLimitOrder("AAPL", bd(0), bd(100), 3));
    }

    @Test
    void placeBuyLimitOrder_zeroTargetPrice_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> session.placeBuyLimitOrder("AAPL", bd(1), bd(0), 3));
    }

    @Test
    void placeBuyLimitOrder_zeroDuration_throwsException() {
      assertThrows(IllegalArgumentException.class,
          () -> session.placeBuyLimitOrder("AAPL", bd(1), bd(100), 0));
    }
  }

  @Nested
  class GameResultTests {

    @Test
    void calculateScore_notWon_returnsZero() {
      GameResult result = new GameResult(false, bd(5000), bd(10000), 10, Difficulty.EASY);

      assertEquals(0, result.calculateScore());
    }

    @Test
    void calculateScore_won_returnsPositiveScore() {
      GameResult result = new GameResult(true, bd(2_000_000), bd(10000), 10, Difficulty.EASY);

      assertTrue(result.calculateScore() > 0);
    }

    @Test
    void calculateScore_fewerWeeks_givesHigherScore() {
      GameResult fast = new GameResult(true, bd(2_000_000), bd(10000), 5, Difficulty.EASY);
      GameResult slow = new GameResult(true, bd(2_000_000), bd(10000), 50, Difficulty.EASY);

      assertTrue(fast.calculateScore() > slow.calculateScore());
    }
  }

  @Nested
  class DifficultyTests {

    @Test
    void getMaxStartingCapital_easy_returnsEmpty() {
      assertTrue(Difficulty.EASY.getMaxStartingCapital().isEmpty());
    }

    @Test
    void getMaxStartingCapital_medium_returnsPresent() {
      assertTrue(Difficulty.MEDIUM.getMaxStartingCapital().isPresent());
      assertEquals(0,
          new BigDecimal("25000").compareTo(Difficulty.MEDIUM.getMaxStartingCapital().get()));
    }

    @Test
    void getMaxStartingCapital_hard_returnsPresent() {
      assertTrue(Difficulty.HARD.getMaxStartingCapital().isPresent());
      assertEquals(0,
          new BigDecimal("5000").compareTo(Difficulty.HARD.getMaxStartingCapital().get()));
    }

    @Test
    void toVolatilityProfile_returnsProfileWithMatchingValues() {
      VolatilityProfile profile = Difficulty.EASY.toVolatilityProfile();

      assertEquals(Difficulty.EASY.getUpChance(), profile.upChance());
      assertEquals(Difficulty.EASY.getMaxGain(), profile.maxGain());
      assertEquals(Difficulty.EASY.getMaxLoss(), profile.maxLoss());
    }

    @Test
    void getGameOverThreshold_easy_returnsCorrectValue() {
      assertEquals(0.40, Difficulty.EASY.getGameOverThreshold(), 0.001);
    }

    @Test
    void getHangarCostRate_hard_isHigherThanEasy() {
      assertTrue(Difficulty.HARD.getHangarCostRate() > Difficulty.EASY.getHangarCostRate());
    }
  }
}
