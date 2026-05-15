package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.CardFactory.createCard;

import edu.ntnu.idatt2003.gruppe50.application.query.TransactionType;
import edu.ntnu.idatt2003.gruppe50.ui.controller.TransactionQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.model.TransactionData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnPresets;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.SearchBarFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory;
import java.util.List;
import javafx.collections.ListChangeListener;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

public class TransactionsView extends BorderPane implements Page {

  private final TransactionQueryController queryController;
  private final TableView<TransactionData> table;
  private final TextField searchField;

  private final RadioButton allFilterButton = new RadioButton("All");
  private final RadioButton purchaseFilterButton = new RadioButton("Purchase");
  private final RadioButton saleFilterButton = new RadioButton("Sell");

  public TransactionsView(TransactionQueryController queryController) {
    this.queryController = queryController;
    this.table = createTransactionTable();

    this.searchField = SearchBarFactory.createSearchField(
        "Search by symbol, company or type...",
        this::applyFilters
    );

    setupTypeFilters();
    // Listen to the master list and apply the filters when new transaction arrives
    queryController.getTransactions()
        .addListener((ListChangeListener<TransactionData>) _ -> applyFilters());

    setTop(createCard(createFilterBar()));
    setCenter(createCard("History", table));
    applyFilters();
  }

  @Override
  public Parent getView() {
    return this;
  }

  private TableView<TransactionData> createTransactionTable() {
    TableView<TransactionData> transactionTable = TableFactory.createTable(List.of(
        ColumnPresets.text("Symbol", t -> t.share().symbol()),
        ColumnPresets.text("Company", t -> t.share().stock()),
        ColumnPresets.text("Type", t -> t.type().name()),
        ColumnPresets.integer("Week", TransactionData::week),
        ColumnPresets.currency("Commission", TransactionData::commissionFee),
        ColumnPresets.currency("Tax", TransactionData::taxFee),
        ColumnPresets.currency("Total", TransactionData::total)
    ));
    transactionTable.setPlaceholder(new Label("No transactions yet."));
    return transactionTable;
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
    TransactionType type = selectedTypeFilter();
    table.getItems().setAll(queryController.onSearch(query, type));
  }

}
