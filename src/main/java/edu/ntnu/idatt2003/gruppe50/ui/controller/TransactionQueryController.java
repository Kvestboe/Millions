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

/** Controller for handling transactionview and transactiondata. */
public final class TransactionQueryController implements Observer {

  private final UUID gameId;
  private final GetTransactionsUseCase getTransactions;
  private final GetTradingLogUseCase getTradingLog;
  private final ObservableList<TransactionData> transactions = FXCollections.observableArrayList();
  private final SimpleObjectProperty<TradingLogData> tradingLog = new SimpleObjectProperty<>();

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

  public ObservableList<TransactionData> getTransactions() {
    return transactions;
  }

  public SimpleObjectProperty<TradingLogData> getTradingLog() {
    return tradingLog;
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
