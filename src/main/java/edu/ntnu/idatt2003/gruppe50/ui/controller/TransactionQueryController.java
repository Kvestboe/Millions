package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetTradingLogUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.GetTransactionsUseCase;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.TransactionType;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import edu.ntnu.idatt2003.gruppe50.ui.mapper.TradingLogDataMapper;
import edu.ntnu.idatt2003.gruppe50.ui.mapper.TransactionDataMapper;
import edu.ntnu.idatt2003.gruppe50.ui.model.TradingLogData;
import edu.ntnu.idatt2003.gruppe50.ui.model.TransactionData;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Keeps transaction history and trading log data synchronized with the exchange state.
 */
public final class TransactionQueryController implements Observer {

  private final UUID gameId;
  private final GetTransactionsUseCase getTransactions;
  private final GetTradingLogUseCase getTradingLog;
  private final ObservableList<TransactionData> transactions = FXCollections.observableArrayList();
  private final SimpleObjectProperty<TradingLogData> tradingLog = new SimpleObjectProperty<>();

  /**
   * Creates a transaction query controller.
   *
   * @param gameId          id of the game session
   * @param getTransactions use case used to retrieve transactions
   * @param getTradingLog   use case used to retrieve trading log statistics
   * @param exchange        observed exchange that triggers transaction refreshes
   */
  public TransactionQueryController(
      UUID gameId,
      GetTransactionsUseCase getTransactions,
      GetTradingLogUseCase getTradingLog,
      Exchange exchange
  ) {
    this.gameId = gameId;
    this.getTransactions = getTransactions;
    this.getTradingLog = getTradingLog;
    exchange.addObserver(this);
    refresh();
  }

  /**
   * Returns the observable transaction list.
   *
   * @return observable transaction data list
   */
  public ObservableList<TransactionData> getTransactions() {
    return transactions;
  }

  /**
   * Returns the observable trading log summary.
   *
   * @return trading log data property
   */
  public SimpleObjectProperty<TradingLogData> getTradingLog() {
    return tradingLog;
  }

  /**
   * Filters transactions by query text and optional transaction type.
   *
   * @param query search text matched against symbol, stock name and transaction type
   * @param type  transaction type filter, or null to include all types
   * @return filtered transactions
   */

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

  /**
   * Refreshes transaction data when the observed exchange changes.
   */
  @Override
  public void update() {
    refresh();
  }

  /**
   * Reloads transaction history and trading log data from the application layer.
   */
  public void refresh() {
    GetTransactionsUseCase.Response response =
        getTransactions.execute(new GetTransactionsUseCase.Request(gameId));
    List<TransactionData> mapped =
        response.transactionDtoArchive().stream()
            .map(TransactionDataMapper::mapTransaction)
            .toList();
    transactions.setAll(mapped);

    tradingLog.set(TradingLogDataMapper.map(getTradingLog.execute(gameId)));
  }
}
