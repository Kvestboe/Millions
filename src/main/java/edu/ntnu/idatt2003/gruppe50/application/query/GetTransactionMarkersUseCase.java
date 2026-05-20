package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Purchase;
import edu.ntnu.idatt2003.gruppe50.domain.trade.Transaction;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class GetTransactionMarkersUseCase {

  private final GameSessionRepository repository;

  public GetTransactionMarkersUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  public Response execute(Request request) {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    List<Transaction> all = session.getPlayer().getTransactionArchive().getTransactions().stream()
        .filter(t -> t.getShare().getStock().getSymbol().equals(request.symbol()))
        .toList();

    Set<Integer> buyWeeks  = all.stream().filter(t ->  t instanceof Purchase).map(Transaction::getWeek).collect(Collectors.toSet());
    Set<Integer> sellWeeks = all.stream().filter(t -> !(t instanceof Purchase)).map(Transaction::getWeek).collect(Collectors.toSet());

    return new Response(buyWeeks, sellWeeks);
  }

  public record Request(UUID gameId, String symbol) {}
  public record Response(Set<Integer> buyWeeks, Set<Integer> sellWeeks) {}
}
