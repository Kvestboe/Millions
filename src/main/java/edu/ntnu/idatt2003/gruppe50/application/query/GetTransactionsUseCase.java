package edu.ntnu.idatt2003.gruppe50.application.query;

import static edu.ntnu.idatt2003.gruppe50.application.query.dto.DtoMapper.createShareDto;
import static edu.ntnu.idatt2003.gruppe50.application.query.dto.DtoMapper.defineTransactionType;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.TransactionDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionArchive;
import java.util.List;
import java.util.UUID;

/** Retrieves the full transaction history for a game session. */
public final class GetTransactionsUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load game sessions
   */
  public GetTransactionsUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Builds a transaction history response for the requested game session.
   *
   * @param request input containing game id
   * @return response containing all transaction DTOs
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public Response execute(Request request) {
    GameSession session =
        repository.findById(request.gameId).orElseThrow(GameSessionNotFoundException::new);

    TransactionArchive archive = session.getPlayer().getTransactionArchive();

    List<TransactionDto> transactionDtoArchive = archive.getTransactions().stream()
        .map(transaction -> new TransactionDto(
            createShareDto(transaction.getShare()),
            transaction.getWeek(),
            defineTransactionType(transaction),
            transaction.isCommitted(),
            transaction.getCalculator().calculateTax(),
            transaction.getCalculator().calculateCommission(),
            transaction.getCalculator().calculateTotal()
        )).toList();

    return new Response(transactionDtoArchive);
  }

  /**
   * Input for retrieving transactions.
   *
   * @param gameId id of the game session
   */
  public record Request(UUID gameId) {}

  /**
   * Output containing the transaction history.
   *
   * @param transactionDtoArchive all transactions as DTOs
   */
  public record Response(List<TransactionDto> transactionDtoArchive) {}
}
