package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.shared.observer.Observer;
import edu.ntnu.idatt2003.gruppe50.ui.controller.GameController;
import edu.ntnu.idatt2003.gruppe50.ui.controller.PortfolioQueryController;
import edu.ntnu.idatt2003.gruppe50.ui.model.PortfolioData;
import edu.ntnu.idatt2003.gruppe50.ui.model.ShareData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.AreaChartView;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.function.Function;
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

public class PortfolioView extends VBox implements Page, Observer {

  private final PortfolioQueryController queryController;
  private final Label netWorthLabel = new Label();
  private final Label portfolioValueLabel = new Label();
  private final Label playerCashLabel = new Label();
  private final TableView<ShareData> table = new TableView<>();
  private final AreaChartView netWorthChart = new AreaChartView("Week", "Net Worth");

  public PortfolioView(PortfolioQueryController queryController, GameController gameController) {
    this.queryController = queryController;

    Label title = new Label("Portfolio");
    Label holdingsTitle = new Label("My holdings");

    VBox cardContainer = createCardContainer();      // bruker feltene
    AreaChart<Number, Number> chart = createNetWorthChart(queryController.getPortfolio());
    createHoldingsTable();                     // setter kolonner én gang

    HBox topSection = new HBox(16, cardContainer, chart);
    HBox.setHgrow(chart, Priority.ALWAYS);

    title.getStyleClass().add("page-title");
    holdingsTitle.getStyleClass().add("section-title");
    this.getStyleClass().add("portfolio-view");
    this.getChildren().addAll(title, topSection, holdingsTitle, table);
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

  private void createHoldingsTable() {
    table.setPlaceholder(new Label("You don't own any shares yet. Go to the Market to buy!"));
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    table.getColumns().addAll(
        createColumn("Symbol",         ShareData::symbol),
        createColumn("Company",        ShareData::stock),
        createColumn("Quantity",       s -> MoneyFormat.format(s.quantity())),
        createColumn("Purchase price", s -> MoneyFormat.formatCurrency(s.purchasePrice())),
        createColumn("Current value",  s -> MoneyFormat.formatCurrency(s.currentShareValue())),
        createGainColumn(),
        createPercentColumn()
    );
  }

  private TableColumn<ShareData, String> createColumn(
      String title,
      Function<ShareData, String> extractor
  ) {
    TableColumn<ShareData, String> col = new TableColumn<>(title);
    col.setCellValueFactory(
        data -> new SimpleStringProperty(extractor.apply(data.getValue()))
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
          setText(MoneyFormat.formatSignedCurrency(value));
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
          setText(MoneyFormat.formatSignedPercent(value));
          setStyle(gainStyle(value));
        }
      }
    });
    return col;
  }

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
    if (value.compareTo(BigDecimal.ZERO) > 0) {
      return "-fx-text-fill: #4CAF50;";
    } else if (value.compareTo(BigDecimal.ZERO) < 0) {
      return "-fx-text-fill: #E24B4A;";
    }
    return "-fx-text-fill: #E0E0E0;";
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
    table.getItems().setAll(p.shares());
    netWorthChart.display("Net Worth", p.netWorthHistory());
  }
}
