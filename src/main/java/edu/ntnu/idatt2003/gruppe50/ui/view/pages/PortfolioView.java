package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory.createTable;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.PortfolioQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.model.PortfolioData;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.AreaChartView;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnPresets;
import java.util.List;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PortfolioView extends VBox implements Page, Observer {

  private final PortfolioQueryController queryController;
  private final Label netWorthLabel = new Label();
  private final Label portfolioValueLabel = new Label();
  private final Label playerCashLabel = new Label();
  private final TableView<ShareData> holdingsTable;
  private final AreaChartView netWorthChart = new AreaChartView("Week", "Net Worth");

  public PortfolioView(PortfolioQueryController queryController, GameController gameController) {
    this.queryController = queryController;

    Label title = new Label("Portfolio");
    Label holdingsTitle = new Label("My holdings");

    VBox cardContainer = createCardContainer();      // bruker feltene
    AreaChart<Number, Number> chart = createNetWorthChart(queryController.getPortfolio());
    holdingsTable = createHoldingsTable();                     // setter kolonner én gang

    HBox topSection = new HBox(16, cardContainer, chart);
    HBox.setHgrow(chart, Priority.ALWAYS);

    title.getStyleClass().add("page-title");
    holdingsTitle.getStyleClass().add("section-title");
    this.getStyleClass().add("portfolio-view");
    this.getChildren().addAll(title, topSection, holdingsTitle, holdingsTable);
    this.getStylesheets().add(getClass().getResource("/css/portfolio.css").toExternalForm());

    queryController.addObserver(this);
    refresh();
  }

  @Override
  public Parent getView() {
    return this;
  }

  private VBox createCardContainer() {
    VBox netWorthCard       = new VBox(new Label("Net worth:"),       netWorthLabel);
    VBox portfolioValueCard = new VBox(new Label("Portfolio value:"), portfolioValueLabel);
    VBox cashBalanceCard    = new VBox(new Label("Cash balance:"),    playerCashLabel);

    VBox.setVgrow(portfolioValueCard, Priority.ALWAYS);
    VBox.setVgrow(cashBalanceCard, Priority.ALWAYS);
    VBox.setVgrow(netWorthCard, Priority.ALWAYS);

    // Style
    portfolioValueCard.getStyleClass().add("info-card");
    cashBalanceCard.getStyleClass().add("info-card");
    netWorthCard.getStyleClass().add("info-card");
    netWorthLabel.getStyleClass().add("net-worth-value");

    VBox container = new VBox(portfolioValueCard, cashBalanceCard, netWorthCard);
    HBox.setHgrow(container, Priority.NEVER);
    container.getStyleClass().add("card-container");
    container.setMinWidth(200);
    container.setPrefWidth(200);

    return container;
  }

  private AreaChart<Number, Number> createNetWorthChart(PortfolioData portfolio) {
    netWorthChart.display("Net Worth Chart", portfolio.netWorthHistory());
    netWorthChart.getChart().setLegendVisible(false);
    return netWorthChart.getChart();
  }

  private TableView<ShareData> createHoldingsTable() {
    TableView<ShareData> holdingsTable = createTable(List.of(
        ColumnPresets.text("Symbol", ShareData::symbol),
        ColumnPresets.text("Company", ShareData::stock),
        ColumnPresets.bigDecimal("Quantity", ShareData::quantity),
        ColumnPresets.currency("Purchase price", ShareData::purchasePrice),
        ColumnPresets.currency("Current value", ShareData::currentShareValue),
        ColumnPresets.signedCurrency("Gain/Loss", ShareData::gain),
        ColumnPresets.signedPercent("Change %", ShareData::percentageGain)

    ));
    holdingsTable.setPlaceholder(new Label("You don't own any shares yet. Go to the Market to buy!"));
    return holdingsTable;
  }

  @Override
  public void update() {
    refresh();
  }

  private void refresh() {
    PortfolioData p = queryController.getPortfolio();
    netWorthLabel.setText(MoneyFormat.formatCurrency(p.netWorth()));
    portfolioValueLabel.setText(MoneyFormat.formatCurrency(p.portfolioValue()));
    playerCashLabel.setText(MoneyFormat.formatCurrency(p.cash()));
    holdingsTable.getItems().setAll(p.shares());
    netWorthChart.display("Net Worth", p.netWorthHistory());
  }
}
