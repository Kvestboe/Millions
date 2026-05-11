package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import com.sun.glass.ui.HeaderButtonOverlay;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.ui.model.PortfolioData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.AreaChartView;
import javafx.scene.Parent;
import javafx.scene.chart.AreaChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;
import java.util.List;

public class StockDetailView extends VBox implements Page {

  private Stock stock;

  public StockDetailView(Stock stock) {
    this.stock = stock;

    Button backBtn = new Button("Back");


    getChildren().addAll(
        backBtn,
        createHeader(),
        createStockChart(),
        createStatTiles(),
        createButtonRow());
  }

  @Override
  public Parent getView() {
    return this;
  }

  @Override
  public String getTitle() {
    return "Stock Detail";
  }

  private VBox createHeader() {
    VBox header = new VBox(10);
    HBox nameBox = new HBox(10);
    HBox priceBox = new HBox(10);

    Label symbol = new Label("Symbol");
    Label company = new Label("Company");
    nameBox.getChildren().addAll(symbol, company);

    Label price = new Label("Price");
    Label priceChangeNumber = new Label("Price Change");
    Label priceChangePercent = new Label("Percent Change");
    priceBox.getChildren().addAll(price, priceChangeNumber, priceChangePercent);

    header.getChildren().addAll(nameBox, priceBox);
    return header;
  }

  private AreaChart<Number, Number> createStockChart() {
    AreaChartView stockChart = new AreaChartView("Week", "Price");

    List<BigDecimal> history = List.of(BigDecimal.ONE, BigDecimal.TWO, BigDecimal.TEN);
    stockChart.display("price development", history);
    stockChart.getChart().setLegendVisible(false);
    return stockChart.getChart();
  }

  private VBox createStatTiles() {
    VBox myHoldingBox = new VBox(10);
    Label myHolding = new Label("My holding");

    HBox numbersBox = new HBox(10);

    VBox myQuantityBox = new VBox(10);
    Label quantity = new Label("Quantity");
    Label quantityNumber = new Label("quantity number");
    myQuantityBox.getChildren().addAll(quantity, quantityNumber);

    VBox gavBox = new VBox(10);
    Label gav = new Label("GAV");
    Label gavNumber = new Label("Gav number");
    gavBox.getChildren().addAll(gav, gavNumber);

    VBox profitBox = new VBox(10);
    Label profit = new Label("Profit");
    Label profitNumber = new Label("Profit number");
    Label profitNumberPrecent = new Label("Profit number precent");
    profitBox.getChildren().addAll(profit, profitNumber, profitNumberPrecent);

    numbersBox.getChildren().addAll(myQuantityBox, gavBox, profitBox);

    myHoldingBox.getChildren().addAll(myHolding, numbersBox);
    return myHoldingBox;
  }

  private HBox createButtonRow() {
    HBox buttonRow = new HBox(10);

    Button buy = new Button("Buy");
    buy.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(buy, Priority.ALWAYS);

    Button sell = new Button("Sell");
    sell.setMaxWidth(Double.MAX_VALUE);
    HBox.setHgrow(sell, Priority.ALWAYS);

    buttonRow.getChildren().addAll(buy, sell);
    return buttonRow;
  }

}
