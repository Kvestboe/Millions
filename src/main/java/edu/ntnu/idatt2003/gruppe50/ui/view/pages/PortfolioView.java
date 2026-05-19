package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import static edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.TableFactory.createTable;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.PortfolioDto;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.ShareDto;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.PortfolioQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.AreaChartView;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ColumnPresets;
import java.util.List;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

public class PortfolioView extends VBox implements Page {

  private final Label netWorthLabel = new Label();
  private final Label portfolioValueLabel = new Label();
  private final Label playerCashLabel = new Label();
  private final TableView<ShareDto> holdingsTable;
  private final AreaChartView netWorthChart = new AreaChartView("Week", "Net Worth");

  public PortfolioView(PortfolioQueryController queryController, GameController gameController) {
    Label title = new Label("Portfolio");
    Label holdingsTitle = new Label("My holdings");

    SimpleObjectProperty<PortfolioDto> p = queryController.getPortfolio();

    VBox cardContainer = createCardContainer(p);
    AreaChart<Number, Number> chart = createNetWorthChart(p.get());
    holdingsTable = createHoldingsTable();

    holdingsTable.setItems(queryController.getShares());

    p.addListener((_, _, portfolio) ->
        netWorthChart.display("Net Worth", portfolio.netWorthHistory())
    );

    HBox topSection = new HBox(16, cardContainer, chart);
    HBox.setHgrow(chart, Priority.ALWAYS);

    title.getStyleClass().add("page-title");
    holdingsTitle.getStyleClass().add("section-title");
    this.getStyleClass().add("portfolio-view");
    this.getChildren().addAll(title, topSection, holdingsTitle, holdingsTable);
    this.getStylesheets().add(getClass().getResource("/css/views/portfolio.css").toExternalForm());
  }

  @Override
  public Parent getView() {
    return this;
  }

  private VBox createCardContainer(SimpleObjectProperty<PortfolioDto> p) {
    netWorthLabel.textProperty().bind(
        Bindings.createStringBinding(() -> MoneyFormat.formatCurrency(p.get().netWorth()), p));
    portfolioValueLabel.textProperty().bind(
        Bindings.createStringBinding(() -> MoneyFormat.formatCurrency(p.get().portfolioValue()), p));
    playerCashLabel.textProperty().bind(
        Bindings.createStringBinding(() -> MoneyFormat.formatCurrency(p.get().cash()), p));

    VBox netWorthCard       = new VBox(new Label("Net worth:"),       netWorthLabel);
    VBox portfolioValueCard = new VBox(new Label("Portfolio value:"), portfolioValueLabel);
    VBox cashBalanceCard    = new VBox(new Label("Cash balance:"),    playerCashLabel);

    VBox.setVgrow(portfolioValueCard, Priority.ALWAYS);
    VBox.setVgrow(cashBalanceCard, Priority.ALWAYS);
    VBox.setVgrow(netWorthCard, Priority.ALWAYS);

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

  private AreaChart<Number, Number> createNetWorthChart(PortfolioDto portfolio) {
    netWorthChart.display("Net Worth Chart", portfolio.netWorthHistory());
    netWorthChart.getChart().setLegendVisible(false);
    return netWorthChart.getChart();
  }

  private TableView<ShareDto> createHoldingsTable() {
    TableView<ShareDto> holdingsTable = createTable(List.of(
        ColumnPresets.text("Symbol", ShareDto::symbol),
        ColumnPresets.text("Company", ShareDto::stock),
        ColumnPresets.bigDecimal("Quantity", ShareDto::quantity),
        ColumnPresets.currency("Purchase price", ShareDto::purchasePrice),
        ColumnPresets.currency("Current value", ShareDto::currentShareValue),
        ColumnPresets.signedCurrency("Gain/Loss", ShareDto::gain),
        ColumnPresets.signedPercent("Change %", ShareDto::percentageGain)

    ));
    holdingsTable.setPlaceholder(new Label("You don't own any shares yet. Go to the Market to buy!"));
    return holdingsTable;
  }

}
