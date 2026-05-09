package edu.ntnu.idatt2003.gruppe50.ui.view.pages;


import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.PortfolioQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.model.PortfolioData;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.AreaChartView;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;

public class PortfolioView extends VBox implements Page {

  public PortfolioView(PortfolioQueryController queryController, GameController gameController) {
    PortfolioData portfolio = queryController.getPortfolio();

    // ─── Lag komponenter ─────────────────────────────────
    Label title = new Label("Portfolio");
    VBox cardContainer = createCardContainer(portfolio);
    AreaChart<Number,Number> chart = createNetWorthChart(portfolio);
    Label holdingsTitle = new Label("My holdings");
    TableView<ShareData> table = createHoldingsTable(portfolio.shares());

    // ─── Bygg layout ─────────────────────────────────────
    HBox topSection = new HBox(16, cardContainer, chart);
    HBox.setHgrow(chart, Priority.ALWAYS);

    // ─── Style og CSS ────────────────────────────────────
    title.getStyleClass().add("page-title");
    holdingsTitle.getStyleClass().add("section-title");
    this.getStyleClass().add("portfolio-view");
    this.getChildren().addAll(title, topSection, holdingsTitle, table);
    this.getStylesheets().add(getClass().getResource("/css/portfolio.css").toExternalForm());
  }

  @Override
  public Parent getView() {
    return this;
  }

  private VBox createCardContainer(PortfolioData portfolio) {
    // Make label for the different player values
    Label netWorthLabel = new Label(portfolio.netWorth().toString());
    Label portfolioValueLabel = new Label(portfolio.portfolioValue().toString());
    Label playerCashLabel = new Label(portfolio.cash().toString());

    // Make cards for the player information
    VBox netWorthCard = new VBox(new Label("Net worth:"), netWorthLabel);
    VBox portfolioValueCard = new VBox(new Label("Portfolio value:"), portfolioValueLabel);
    VBox cashBalanceCard = new VBox(new Label("Cash balance:"), playerCashLabel);

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


  private AreaChart<Number,Number> createNetWorthChart(PortfolioData portfolio) {
    // Add temporary net worth line chart
    AreaChartView netWorthChart = new AreaChartView("Week", "Net Worth");
    List<BigDecimal> history = List.of(BigDecimal.ONE, BigDecimal.TWO, BigDecimal.TEN);
    netWorthChart.display("Net Worth Chart", history);
    netWorthChart.getChart().setLegendVisible(false);
    return netWorthChart.getChart();

    //ACTUAL LOGIC
//    LineChartView netWorthChart = new LineChartView("Week", "Net Worth");
//    netWorthChart.display("Net Worth Chart", portfolio.netWorthHistory());
//    return netWorthChart.getChart();
  }

  private TableView<ShareData> createHoldingsTable(List<ShareData> shares) {
    TableView<ShareData> table = new TableView<>();
    table.setPlaceholder(new Label("You don't own any shares yet. Go to the Market to buy!"));
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    table.getColumns().addAll(
        createColumn("Symbol", ShareData::symbol),
        createColumn("Company", ShareData::stock),
        createColumn("Quantity", shareData -> shareData.quantity().toString()),
        createColumn("Purchase price", shareData -> shareData.purchasePrice().toString()),
        createColumn("Current value", shareData -> shareData.currentShareValue().toString()),
        createGainColumn(),
        createPercentColumn()
    );
    table.getItems().addAll(shares);
    return table;
  }

  private TableColumn<ShareData, String> createColumn(String title, Function<ShareData, String> extractor) {
    TableColumn<ShareData, String> col = new TableColumn<>(title);
    col.setCellValueFactory(data -> new SimpleStringProperty(extractor.apply(data.getValue()))
    );
    return col;
  }

  private TableColumn<ShareData, BigDecimal> createGainColumn() {
    TableColumn<ShareData, BigDecimal> col = new TableColumn<>("Gain/Loss");
      col.setCellValueFactory(data ->
          new SimpleObjectProperty<>(calculateGain(data.getValue())));
      col.setCellFactory(column -> new TableCell<>() {
        @Override
        protected void updateItem(BigDecimal value, boolean empty) {
          super.updateItem(value, empty);
          if (empty || value == null) {
            setText(null);
            setStyle("");
          } else {
            setText((value.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + value + " kr");
            setStyle(gainStyle(value));
          }
        }
      });
      return col;
  }

  private TableColumn<ShareData, BigDecimal> createPercentColumn() {
    TableColumn<ShareData, BigDecimal> col = new TableColumn<>("Change %");
    col.setCellValueFactory(data ->
        new SimpleObjectProperty<>(calculatePercent(data.getValue())));
    col.setCellFactory(column -> new TableCell<>() {
      @Override
      protected void updateItem(BigDecimal value, boolean empty) {
        super.updateItem(value, empty);
        if (empty || value == null) {
          setText(null);
          setStyle("");
        } else {
          setText((value.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "") + value + "%");
          setStyle(gainStyle(value));
        }
      }
    });
    return col;
  }

//  private TableColumn<ShareData, Void> createActionColumn() {
//  }

  private BigDecimal calculateGain(ShareData shareData) {
    return shareData.currentShareValue()
        .subtract(shareData.purchasePrice()
            .multiply(shareData.quantity()));
  }

  private BigDecimal calculatePercent(ShareData shareData) {
    return shareData.currentPrice()
        .subtract(shareData.purchasePrice())
        .divide(shareData.purchasePrice(), 2, RoundingMode.HALF_UP)
        .multiply(BigDecimal.valueOf(100));
  }

  private String gainStyle(BigDecimal value) {
    return value.compareTo(BigDecimal.ZERO) >= 0
        ? "-fx-text-fill: #4CAF50;"
        : "-fx-text-fill: #E24B4A;";
  }
}
