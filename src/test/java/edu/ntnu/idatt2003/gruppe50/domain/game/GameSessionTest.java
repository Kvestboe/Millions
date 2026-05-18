package edu.ntnu.idatt2003.gruppe50.domain.game;

import static edu.ntnu.idatt2003.gruppe50.testutil.BigDecimalTestUtils.bd;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultExchange;
import static edu.ntnu.idatt2003.gruppe50.testutil.TestDataFactory.createDefaultPlayer;
import static org.junit.jupiter.api.Assertions.*;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
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
    assertEquals(LocalDate.now(), newSession.getRunStartedAt());
    assertEquals(LocalDate.now(), newSession.getLastPlayed());
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

  @Test
  void rehydrate_validInput_restoresFields() {
    UUID gameId = UUID.randomUUID();
    LocalDate runStartedAt = LocalDate.now().minusDays(10);
    LocalDate lastPlayed = LocalDate.now().minusDays(2);

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
                LocalDate.now(),
                LocalDate.now(),
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
                LocalDate.now(),
                null,
                List.of(bd(20))));
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
            LocalDate.now().minusDays(10),
            LocalDate.now().minusDays(1),
            List.of(bd(20)));

    rehydrated.markOpened();

    assertEquals(LocalDate.now(), rehydrated.getLastPlayed());
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
}
