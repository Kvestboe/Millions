package edu.ntnu.idatt2003.gruppe50.ui.view.components;

import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.model.TradingLogData;
import edu.ntnu.idatt2003.gruppe50.ui.model.TransactionData;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.StatCardFactory;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.RowConstraints;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

/**
 * Card component showing trading statistics and recent activity.
 */
public class TradingLogCard extends VBox {

  private final Label totalTradesLabel = new Label("0 total trades");
  private final Label realizedPnLValue = new Label("0,00 kr");
  private final Label tradesValue = new Label("0");
  private final Label feesValue = new Label("0,00 kr");
  private final Label taxesValue = new Label("0,00 kr");

  private final Region buyFill = new Region();
  private final Region sellFill = new Region();
  private final Label buyCountLabel = new Label("0B");
  private final Label sellCountLabel = new Label("0S");

  private final VBox activityList = new VBox(8);
  private final Label emptyActivity = new Label(
      "No trades yet, head to the Market to place your first order."
  );

  /**
   * Creates a trading log card bound to an observable data source.
   *
   * @param source observable trading log data
   */
  public TradingLogCard(ObservableValue<TradingLogData> source) {
    getStyleClass().add("trading-log-card");
    setSpacing(14);

    VBox activitySection = buildActivitySection();
    VBox.setVgrow(activitySection, Priority.ALWAYS);

    getChildren().addAll(
        buildHeader(),
        buildStatsGrid(),
        buildMixSection(),
        activitySection
    );

    source.addListener((_, _, data) -> update(data));
    update(source.getValue());
  }

  private HBox buildHeader() {
    Label title = new Label("TRADING LOG");
    title.getStyleClass().add("trading-log-title");

    totalTradesLabel.getStyleClass().add("trading-log-count");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox header = new HBox(title, spacer, totalTradesLabel);
    header.setAlignment(Pos.CENTER_LEFT);
    header.getStyleClass().add("trading-log-header");
    return header;
  }

  private GridPane buildStatsGrid() {
    realizedPnLValue.getStyleClass().add("pnl-value");

    VBox pnl = StatCardFactory.tile("REALIZED P/L", realizedPnLValue);
    VBox trades = StatCardFactory.tile("TRADES", tradesValue);
    VBox fees = StatCardFactory.tile("FEES PAID", feesValue);
    VBox taxes = StatCardFactory.tile("TAXES PAID", taxesValue);

    for (VBox tile : List.of(pnl, trades, fees, taxes)) {
      tile.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
      GridPane.setHgrow(tile, Priority.ALWAYS);
      GridPane.setVgrow(tile, Priority.ALWAYS);
    }

    ColumnConstraints col = new ColumnConstraints();
    col.setPercentWidth(50);
    col.setHgrow(Priority.ALWAYS);

    RowConstraints row = new RowConstraints();
    row.setPercentHeight(50);
    row.setVgrow(Priority.ALWAYS);

    GridPane grid = new GridPane();
    grid.setHgap(8);
    grid.setVgap(8);
    grid.getColumnConstraints().addAll(col, col);
    grid.getRowConstraints().addAll(row, row);

    grid.add(pnl, 0, 0);
    grid.add(trades, 1, 0);
    grid.add(fees, 0, 1);
    grid.add(taxes, 1, 1);

    return grid;
  }

  private VBox buildMixSection() {
    Label overline = new Label("BUY / SELL MIX");
    overline.getStyleClass().add("section-overline");

    buyFill.getStyleClass().add("buy-sell-bar-buy");
    sellFill.getStyleClass().add("buy-sell-bar-sell");
    HBox bar = new HBox(buyFill, sellFill);
    bar.getStyleClass().add("buy-sell-bar");

    buyCountLabel.getStyleClass().add("mix-count-buy");
    sellCountLabel.getStyleClass().add("mix-count-sell");
    Label dot = new Label("·");
    dot.getStyleClass().add("mix-separator");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox counts = new HBox(6, spacer, buyCountLabel, dot, sellCountLabel);
    counts.setAlignment(Pos.CENTER_RIGHT);

    HBox titleRow = new HBox(overline, counts);
    HBox.setHgrow(counts, Priority.ALWAYS);
    titleRow.setAlignment(Pos.CENTER_LEFT);

    return new VBox(6, titleRow, bar);
  }

  private VBox buildActivitySection() {
    Label overline = new Label("RECENT ACTIVITY");
    overline.getStyleClass().add("section-overline");

    StackPane wrapper = new StackPane(activityList, emptyActivity);
    wrapper.getStyleClass().add("activity-wrapper");
    emptyActivity.getStyleClass().add("empty-activity");

    return new VBox(8, overline, wrapper);
  }

  private void update(TradingLogData data) {
    if (data == null) {
      return;
    }

    totalTradesLabel.setText(data.totalTrades() + " total trades");
    tradesValue.setText(String.valueOf(data.totalTrades()));
    realizedPnLValue.setText(MoneyFormat.formatSignedCurrency(data.realizedPnL()));
    feesValue.setText(MoneyFormat.formatCurrency(data.totalFees()));
    taxesValue.setText(MoneyFormat.formatCurrency(data.totalTaxes()));

    realizedPnLValue.getStyleClass().removeAll("gain", "loss");
    int sign = data.realizedPnL().signum();
    if (sign > 0) {
      realizedPnLValue.getStyleClass().add("gain");
    }
    if (sign < 0) {
      realizedPnLValue.getStyleClass().add("loss");
    }

    updateMix(data.purchases(), data.sales());
    updateActivity(data.recentActivity());
  }

  private void updateMix(long buys, long sells) {
    long total = buys + sells;
    double buyPct = total == 0 ? 0.5 : (double) buys / total;
    double sellPct = total == 0 ? 0.5 : (double) sells / total;

    HBox bar = (HBox) buyFill.getParent();
    buyFill.prefWidthProperty().bind(bar.widthProperty().multiply(buyPct));
    sellFill.prefWidthProperty().bind(bar.widthProperty().multiply(sellPct));

    buyCountLabel.setText(buys + "B");
    sellCountLabel.setText(sells + "S");
  }

  private void updateActivity(List<TransactionData> recent) {
    activityList.getChildren().clear();
    boolean empty = recent.isEmpty();
    emptyActivity.setVisible(empty);
    emptyActivity.setManaged(empty);
    activityList.setVisible(!empty);
    activityList.setManaged(!empty);

    for (TransactionData t : recent) {
      activityList.getChildren().add(buildActivityRow(t));
    }
  }

  private HBox buildActivityRow(TransactionData t) {
    Label type = new Label(t.type().name());
    type.getStyleClass().add(t.type().name().equals("PURCHASE") ? "loss" : "gain");

    Label symbol = new Label(t.share().symbol());
    symbol.getStyleClass().add("activity-symbol");

    Label week = new Label("Week " + t.week());
    week.getStyleClass().add("activity-meta");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Label total = new Label(MoneyFormat.formatCurrency(t.total()));
    total.getStyleClass().add("activity-total");

    HBox row = new HBox(10, type, symbol, week, spacer, total);
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().add("activity-row");
    return row;
  }

}
