package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetTransactionsUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.TransactionType;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import edu.ntnu.idatt2003.gruppe50.ui.mapper.TransactionDataMapper;
import edu.ntnu.idatt2003.gruppe50.ui.model.TransactionData;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

// This class should not be a direct observer of exchange, this leads to tighter coupling.
// Need another method for doing this like a game event method which calls to refresh the queries.
/**
 * Controller for handling transactionview and transactiondata
 */
public final class TransactionQueryController implements Observer {
  private final UUID gameId;
  private final GetTransactionsUseCase getTransactions;
  private final ObservableList<TransactionData> transactions = FXCollections.observableArrayList();

  public TransactionQueryController(UUID gameId, GetTransactionsUseCase getTransactions, Exchange exchange) {
    this.gameId = gameId;
    this.getTransactions = getTransactions;
    exchange.addObserver(this);
    refresh();
  }

  public ObservableList<TransactionData> getTransactions() {
    return transactions;
  }

  public List<TransactionData> onSearch(String query, TransactionType type) {
    String term = query.toLowerCase(Locale.ROOT);
    return transactions.stream()
        .filter(transaction ->
            transaction.share().symbol().toLowerCase(Locale.ROOT).contains(term)
                || transaction.share().stock().toLowerCase(Locale.ROOT).contains(term)
                || transaction.type().name().toLowerCase(Locale.ROOT).contains(term)
        )
        .filter(transaction -> type == null || transaction.type() == type)
        .toList();
  }

  @Override
  public void update() {
    refresh();
  }

  public void refresh() {
    GetTransactionsUseCase.Response response = getTransactions.execute(new GetTransactionsUseCase.Request(gameId));
    List<TransactionData> mapped = response.transactionDtoArchive().stream()
        .map(TransactionDataMapper::mapTransaction)
        .toList();
    transactions.setAll(mapped);
  }
}
