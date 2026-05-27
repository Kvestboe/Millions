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

/**
 * Retrieves buy and sell week markers for a stock chart.
 */
public class GetTransactionMarkersUseCase {

  private final GameSessionRepository repository;

  /**
   * Creates the use case with a game-session repository.
   *
   * @param repository repository used to load game sessions
   */
  public GetTransactionMarkersUseCase(GameSessionRepository repository) {
    this.repository = repository;
  }

  /**
   * Finds the weeks when the selected stock was bought or sold.
   *
   * @param request input with game id and stock symbol
   * @return response containing buy weeks and sell weeks
   * @throws GameSessionNotFoundException if the session does not exist
   */
  public Response execute(Request request) {
    GameSession session =
        repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

    List<Transaction> all = session.getPlayer().getTransactionArchive().getTransactions().stream()
        .filter(t -> t.getShare().getStock().getSymbol().equals(request.symbol()))
        .toList();

    Set<Integer> buyWeeks =
        all.stream().filter(t -> t instanceof Purchase).map(Transaction::getWeek)
            .collect(Collectors.toSet());
    Set<Integer> sellWeeks =
        all.stream().filter(t -> !(t instanceof Purchase)).map(Transaction::getWeek)
            .collect(Collectors.toSet());

    return new Response(buyWeeks, sellWeeks);
  }

  /**
   * Input for retrieving transaction markers.
   *
   * @param gameId id of the game session
   * @param symbol stock symbol to find markers for
   */
  public record Request(UUID gameId, String symbol) {
  }

  /**
   * Output containing transaction marker weeks.
   *
   * @param buyWeeks  weeks when purchases occurred
   * @param sellWeeks weeks when sales occurred
   */
  public record Response(Set<Integer> buyWeeks, Set<Integer> sellWeeks) {
  }
}
