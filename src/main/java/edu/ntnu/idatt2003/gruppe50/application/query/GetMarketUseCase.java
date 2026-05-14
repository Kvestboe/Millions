package edu.ntnu.idatt2003.gruppe50.application.query;

import edu.ntnu.idatt2003.gruppe50.application.GameSessionNotFoundException;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.repository.GameSessionRepository;
import java.util.List;
import java.util.UUID;

public class GetMarketUseCase {

  private final GameSessionRepository repository;

   public GetMarketUseCase(GameSessionRepository repository) {
     this.repository = repository;
   }

   public Response execute(Request request) {
     GameSession session =
         repository.findById(request.gameId()).orElseThrow(GameSessionNotFoundException::new);

     List<Stock> stocks = request.query() == null || request.query().isBlank()
         ? session.getExchange().findStocks(request.query())
         : session.getExchange().getStocks();

     List<StockDto> stockDtos = stocks.stream()
         .map(DtoMapper::createStockDto)
         .toList();

     return new Response(stockDtos);
   }


  public record Request(UUID gameId, String query) {}

  public record Response(List<StockDto> stocks) {}
}
