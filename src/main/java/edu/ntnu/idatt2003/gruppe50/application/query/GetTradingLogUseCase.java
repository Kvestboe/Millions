package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.application.query.dto.DtoMapper.createShareDto;
import static edu.ntnu.idatt2003.gruppe50.application.query.dto.DtoMapper.defineTransactionType;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.TradingLogDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.TransactionDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionArchive;
import java.util.List;
import java.util.UUID;

/** Retrieves trading statistics and recent activity for a game session. */
public final class GetTradingLogUseCase {

  private static final int RECENT_ACTIVITY_LIMIT = 5;

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load game sessions
   */
  public GetTradingLogUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Builds a trading log summary for the requested game session.
   *
   * @param gameId id of the game session
   * @return trading log DTO with totals and recent transactions
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public TradingLogDto execute(UUID gameId) {
    GameSession session =
        repository.findById(gameId).orElseThrow(GameSessionNotFoundException::new);

    TransactionArchive archive = session.getPlayer().getTransactionArchive();

    List<TransactionDto> recent = archive.recent(RECENT_ACTIVITY_LIMIT).stream()
        .map(t -> new TransactionDto(
            createShareDto(t.getShare()),
            t.getWeek(),
            defineTransactionType(t),
            t.isCommitted(),
            t.getCalculator().calculateTax(),
            t.getCalculator().calculateCommission(),
            t.getCalculator().calculateTotal()
        ))
        .toList();

    return new TradingLogDto(
        archive.size(),
        archive.countPurchases(),
        archive.countSales(),
        archive.totalCommission(),
        archive.totalTax(),
        archive.realizedProfitLoss(),
        recent
    );
  }
}
