package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.application.query.TransactionType;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.controller.TransactionQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.model.TransactionData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnDefinition;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Parent;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import java.util.List;
import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.CardFactory.createCard;

public class TransactionsView extends BorderPane implements Page {

  private final TransactionQueryController queryController;
  private final ObservableList<TransactionData> transactions = FXCollections.observableArrayList();
  private final TableView<TransactionData> table;
  private final TextField searchField;
  private final RadioButton allFilterButton;
  private final RadioButton purchaseFilterButton;
  private final RadioButton saleFilterButton;

  public TransactionsView(TransactionQueryController queryController) {
    this.queryController = queryController;
    this.table = makeTransactionTable();
    this.searchField = createSearchField();
    this.allFilterButton = new RadioButton("All");
    this.purchaseFilterButton = new RadioButton("Purchase");
    this.saleFilterButton = new RadioButton("Sale");
    setupTypeFilters();
    this.table.setItems(transactions);
    setTop(createCard(createFilterBar()));
    setCenter(createCard("History", table));
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
        new ColumnDefinition<>("Commission", t ->
            new ReadOnlyStringWrapper(MoneyFormat.formatCurrency(t.commissionFee()))
        ),
        new ColumnDefinition<>("Tax", t ->
            new ReadOnlyStringWrapper(MoneyFormat.formatCurrency(t.taxFee()))
        ),
        new ColumnDefinition<>("Total", t ->
            new ReadOnlyStringWrapper(MoneyFormat.formatCurrency(t.total()))
        )
    ));
    transactionTable.setPlaceholder(new Label("No transactions yet."));
    return transactionTable;
  }

  private TextField createSearchField() {
    TextField field = new TextField();
    field.setPromptText("Search by symbol, company or type...");
    field.textProperty().addListener((_, _, _) -> applyFilters());
    field.setMaxWidth(Double.MAX_VALUE);
    return field;
  }

  private HBox createFilterBar() {
    return new HBox(searchField, allFilterButton, purchaseFilterButton, saleFilterButton);
  }

  private void setupTypeFilters() {
    ToggleGroup group = new ToggleGroup();
    allFilterButton.setToggleGroup(group);
    purchaseFilterButton.setToggleGroup(group);
    saleFilterButton.setToggleGroup(group);
    allFilterButton.setSelected(true);
    group.selectedToggleProperty().addListener((_, _, _) -> applyFilters());
  }

  private TransactionType selectedTypeFilter() {
    if (purchaseFilterButton.isSelected()) {
      return TransactionType.PURCHASE;
    }
    if (saleFilterButton.isSelected()) {
      return TransactionType.SALE;
    }
    return null;
  }

  private void applyFilters() {
    String query = searchField.getText();
    TransactionType selectedType = selectedTypeFilter();
    if (query == null || query.isBlank()) {
      if (selectedType == null) {
        table.getItems().setAll(queryController.getTransactions());
      } else {
        table.getItems().setAll(queryController.onSearch("", selectedType));
      }
    } else {
      table.getItems().setAll(queryController.onSearch(query, selectedType));
    }
  }

  private void refreshTransactions() {
    applyFilters();
  }
}
