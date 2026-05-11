package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Transaction;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionArchive;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static edu.ntnu.idatt2003.gruppe50.application.query.DtoMapper.createShareDto;
import static edu.ntnu.idatt2003.gruppe50.application.query.DtoMapper.defineTransactionType;

public final class GetTransactionsUseCase {
  private final GameSessionRepository repository;

  public GetTransactionsUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public Response execute(Request request) {
    GameSession session = repository.findById(request.gameId).orElseThrow(
        GameSessionNotFoundException::new
    );

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

  public record Request(UUID gameId) {}

  public record Response(
    List<TransactionDto> transactionDtoArchive
  ) {}

}
