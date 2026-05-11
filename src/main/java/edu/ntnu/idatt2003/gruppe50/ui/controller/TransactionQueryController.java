package edu.ntnu.idatt2003.gruppe50.ui.controller;

import edu.ntnu.idatt2003.gruppe50.application.query.GetTransactionsUseCase;
import edu.ntnu.idatt2003.gruppe50.ui.mapper.TransactionDataMapper;
import edu.ntnu.idatt2003.gruppe50.ui.model.TransactionData;
import edu.ntnu.idatt2003.gruppe50.ui.model.TransactionHistoryData;

import java.util.List;
import java.util.UUID;

public class TransactionQueryController {
  private final UUID gameId;
  private final GetTransactionsUseCase getTransactions;

  public TransactionQueryController(UUID gameId, GetTransactionsUseCase getTransactions) {
    this.gameId = gameId;
    this.getTransactions = getTransactions;
  }

  public TransactionHistoryData getTransactions() {
    GetTransactionsUseCase.Response response = getTransactions.execute(
        new GetTransactionsUseCase.Request(gameId)
    );

    List<TransactionData> transactions = response.transactionDtoArchive().stream()
        .map(TransactionDataMapper::mapTransaction).toList();

    return new TransactionHistoryData(transactions);
  }
}
