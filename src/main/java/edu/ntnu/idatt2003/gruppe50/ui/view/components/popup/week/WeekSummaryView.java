package edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.week;

import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.model.WeekHolding;
import edu.ntnu.idatt2003.gruppe50.ui.model.WeekSummary;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import java.math.BigDecimal;
import java.math.RoundingMode;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Main content view for the week summary popup.
 */
public class WeekSummaryView extends VBox {

  private final WeekSummary summary;
  private final Runnable onClose;

  /**
   * Creates a week summary view.
   *
   * @param summary week summary data to display
   * @param onClose action run when closing the summary
   */
  public WeekSummaryView(
      WeekSummary summary,
      Runnable onClose
  ) {
    this.summary = summary;
    this.onClose = onClose;

    setSpacing(15);
    getChildren().setAll(
        buildHero(),
        buildStatRow(),
        buildWeeklyBreakdown(),
        buildHoldingsBlock(),
        buildActions()
    );
  }

  private VBox buildHero() {
    Label title = new Label("Week " + summary.previousWeek() + " → Week " + summary.newWeek());
    title.getStyleClass().add("hero-title");

    BigDecimal delta = summary.weeklyDelta();
    String arrow = delta.signum() >= 0 ? "▲" : "▼";
    Label deltaLabel = new Label(arrow + "  " + MoneyFormat.formatSignedCurrency(delta));
    deltaLabel.getStyleClass().addAll("hero-delta", delta.signum() >= 0 ? "gain" : "loss");

    VBox box = new VBox(8, title, deltaLabel);
    box.setAlignment(Pos.CENTER);
    return box;
  }

  private HBox buildStatRow() {
    HBox row = new HBox(12,
        statTile("Net worth", MoneyFormat.formatCurrency(summary.netWorthAfter())),
        statTile("Cash", MoneyFormat.formatCurrency(summary.cash())));
    row.setAlignment(Pos.CENTER);
    return row;
  }

  private VBox statTile(String label, String value) {
    Label l = new Label(label.toUpperCase());
    l.getStyleClass().add("bottom-bar-label");
    Label v = new Label(value);
    v.getStyleClass().add("bottom-bar-value");
    VBox tile = new VBox(2, l, v);
    tile.getStyleClass().add("stat-tile");
    HBox.setHgrow(tile, Priority.ALWAYS);
    return tile;
  }

  /**
   * Builds a breakdown of the weekly result.
   *
   * <p>The portfolio movement shows the net worth change before hangar cost,
   * while weekly result includes the hangar cost deduction.
   *
   * @return a VBox containing the weekly breakdown rows
   */
  private VBox buildWeeklyBreakdown() {
    Label header = new Label("Weekly breakdown");
    header.getStyleClass().add("popup-title");

    BigDecimal taxEffect = summary.feesAndTaxImpact();
    String taxEffectLabel = taxEffect.signum() >= 0
        ? "Reduced tax liability"
        : "Tax and fee drag";

    VBox rows = new VBox(6,
        row("Holdings movement", MoneyFormat.formatSignedCurrency(summary.holdingsMovement())),
        row(taxEffectLabel, MoneyFormat.formatSignedCurrency(taxEffect)),
        row("Hangar cost", MoneyFormat.formatSignedCurrency(summary.hangarCost().negate())),
        row("Weekly result", MoneyFormat.formatSignedCurrency(summary.weeklyDelta()))
    );

    VBox block = new VBox(8, header, rows);
    block.getStyleClass().add("card");
    return block;
  }

  private VBox buildHoldingsBlock() {
    Label header = new Label("Your holdings this week");
    header.getStyleClass().add("popup-title");

    VBox rows = new VBox(6);
    summary.holdings().forEach(h -> rows.getChildren().add(holdingRow(h)));

    ScrollPane scroll = new ScrollPane(rows);
    scroll.setFitToWidth(true);
    scroll.setMaxHeight(180);

    VBox block = new VBox(8, header, scroll);
    if (summary.holdings().isEmpty()) {
      block.setVisible(false);
      block.setManaged(false);
    }
    return block;
  }

  private HBox holdingRow(WeekHolding h) {
    Label sym = new Label(h.symbol());
    Label qty = new Label("x " + h.quantity().setScale(0, RoundingMode.HALF_UP));
    Label delta = new Label(MoneyFormat.formatSignedCurrency(h.weeklyDelta())
        + "  (" + MoneyFormat.formatSignedPercent(h.weeklyPercent()) + ")");
    delta.getStyleClass().add(h.weeklyDelta().signum() >= 0 ? "gain" : "loss");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox row = new HBox(12, sym, qty, spacer, delta);
    row.getStyleClass().add("holding-row");
    return row;
  }

  private HBox buildActions() {
    Button cont = ButtonFactory.styled("Continue →", "primary", onClose);
    HBox row = new HBox(cont);
    row.setAlignment(Pos.CENTER_RIGHT);
    return row;
  }

  private HBox row(String label, String value) {
    Label l = new Label(label);
    l.getStyleClass().add("detail-label");
    Label v = new Label(value);
    v.getStyleClass().add("detail-value");
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);
    return new HBox(l, spacer, v);
  }
}