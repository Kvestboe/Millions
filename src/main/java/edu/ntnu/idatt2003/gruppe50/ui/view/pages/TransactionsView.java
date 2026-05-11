package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.ui.controller.TransactionQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.model.TransactionData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnDefinition;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.BorderPane;

import java.util.List;

public class TransactionsView extends BorderPane implements Page {

  private final TransactionQueryController queryController;
  private final ObservableList<TransactionData> transactions = FXCollections.observableArrayList();
  private final TableView<TransactionData> table;

  public TransactionsView(TransactionQueryController queryController) {
    this.queryController = queryController;
    this.table = makeTransactionTable();
    this.table.setItems(transactions);
    setCenter(table);
  }

  @Override
  public Parent getView() {
    refreshTransactions();
    return this;
  }

  private TableView<TransactionData> makeTransactionTable() {
    TableView<TransactionData> transactionTable = TableFactory.createTable(List.of(
        new ColumnDefinition<>("Symbol", t -> new ReadOnlyStringWrapper(t.share().symbol())),
        new ColumnDefinition<>("Company", t -> new ReadOnlyStringWrapper(t.share().stock())),
        new ColumnDefinition<>("Type", t -> new ReadOnlyStringWrapper(t.type().name())),
        new ColumnDefinition<>("Week", t -> new ReadOnlyObjectWrapper<>(t.week())),
        new ColumnDefinition<>("Commission", t -> new ReadOnlyStringWrapper(t.commissionFee().toString())),
        new ColumnDefinition<>("Tax", t -> new ReadOnlyStringWrapper(t.taxFee().toString())),
        new ColumnDefinition<>("Total", t -> new ReadOnlyStringWrapper(t.total().toString()))
    ));
    transactionTable.setPlaceholder(new Label("No transactions yet."));
    return transactionTable;
  }

  private void refreshTransactions() {
    transactions.setAll(queryController.getTransactions());
  }
}
