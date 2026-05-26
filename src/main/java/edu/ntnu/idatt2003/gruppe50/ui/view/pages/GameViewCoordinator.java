package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.GameSessionControllerBundle;
import edu.ntnu.idatt2003.gruppe50.application.query.dto.StockDto;
import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameOutcome;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameResult;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSession;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameSessionState;
import edu.ntnu.idatt2003.gruppe50.domain.leaderboard.Leaderboard;
import edu.ntnu.idatt2003.gruppe50.domain.leaderboard.LeaderboardEntry;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.LevelGap;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Status;
import edu.ntnu.idatt2003.gruppe50.infrastructure.repository.LeaderboardFileHandler;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.model.WeekHolding;
import edu.ntnu.idatt2003.gruppe50.ui.model.WeekSummary;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.NavBar;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.StatCardFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.week.WeekSummaryPopup;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.popup.week.InsufficientCashPopup;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.NavigationManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import java.util.stream.Collectors;

import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class GameViewCoordinator {

  private final GameSessionControllerBundle bundle;
  private final Runnable onMainMenu;
  private final Runnable onPlayAgain;
  private final Runnable onSettings;
  private final Runnable onLeaderboard;
  private final Consumer<String> onThemeChanged;
  private final Leaderboard leaderboard;
  private final LeaderboardFileHandler leaderboardFile;
  private NavigationManager navManager;
  private BorderPane root;
  private ShopView shopView;
  private StackPane popupHost;
  private NavBar navBar;

  private HBox bottomBar;
  private Label weekValue;
  private Label netWorthValue;
  private Label deltaValue;
  private Label cashValue;
  private Button advanceButton;

  private BigDecimal lastWeeklyDelta = BigDecimal.ZERO;
  private static final BigDecimal DANGER_ZONE_MULTIPLIER = new BigDecimal("1.15");

  public GameViewCoordinator(
      GameSessionControllerBundle bundle,
      Runnable onMainMenu,
      Runnable onPlayAgain, Runnable onSettings, Runnable onLeaderboard,
      Consumer<String> onThemeChanged,
      Leaderboard leaderboard,
      LeaderboardFileHandler leaderboardFile

  ) {
    this.bundle = bundle;
    this.onMainMenu = onMainMenu;
    this.onPlayAgain = onPlayAgain;
    this.onSettings = onSettings;
    this.onLeaderboard = onLeaderboard;
    this.onThemeChanged = onThemeChanged;
    this.leaderboard = leaderboard;
    this.leaderboardFile = leaderboardFile;
  }

  public Scene getScene() {
    navManager = new NavigationManager(buildPages());
    navBar = new NavBar(navManager::navigateTo, new NavBar.AccountMenuListener() {
      @Override public void onSettings()    { onSettings.run(); }
      @Override public void onLeaderboard() { onLeaderboard.run(); }
      @Override public void onMainMenu()    { onMainMenu.run(); }
      @Override public void onSaveAndQuit() { Platform.exit(); }
    });
    refreshNavBar();

    root = new BorderPane();
    root.setTop(navBar);
    root.setCenter(navManager.getContentArea());
    bottomBar = buildBottomBar();
    root.setBottom(bottomBar);
    refreshBottomBar();
    bundle.session().getExchange().addObserver(this::refreshBottomBar);

    navManager.navigateTo(PageId.DASHBOARD);
    bundle.market().setOnStockSelected(stock -> navigateToStockDetail(stock, PageId.MARKET));

    bundle.game().setOutcomeListener(outcome -> {
      GameSession session = bundle.session();
      BigDecimal finalNetWorth = session.getPlayer().getNetWorth();
      int weeksPlayed = session.getExchange().getWeek();
      Difficulty difficulty = session.getDifficulty();

      GameResult result = new GameResult(
          outcome == GameOutcome.WON,
          finalNetWorth,
          session.getPlayer().getStartingMoney(),
          weeksPlayed,
          difficulty
      );

      if (outcome == GameOutcome.WON) {
        LeaderboardEntry entry = new LeaderboardEntry(
            session.getPlayer().getName(),
            LeaderboardEntry.calculateScore(weeksPlayed, result.startingCapital()),
            weeksPlayed,
            finalNetWorth,
            result.startingCapital(),
            difficulty,
            LocalDate.now()
        );
        leaderboard.add(entry);
        leaderboardFile.save(leaderboard);
      }

      root.setTop(null);
      root.setBottom(null);
      navManager.show(new GameOverView(result, onPlayAgain, onMainMenu, onLeaderboard));
    });

    popupHost = new StackPane(root);
    Scene scene = new Scene(popupHost);
    return scene;
  }

  private Map<PageId, Page> buildPages() {
    Map<PageId, Page> pages = new EnumMap<>(PageId.class);

    pages.put(PageId.DASHBOARD, new DashboardView(bundle.dashboard()));
    pages.put(PageId.MARKET, new MarketView(
        bundle.market(),
        stock -> navigateToStockDetail(stock, PageId.MARKET)
    ));
    pages.put(PageId.PORTFOLIO, new PortfolioView(
        bundle.portfolio(),
        shareDto -> bundle.market().findBySymbol(shareDto.symbol())
            .ifPresent(stock -> navigateToStockDetail(stock, PageId.PORTFOLIO))
    ));
    pages.put(PageId.SHOP, shopView = new ShopView(
        bundle.shop(),
        onThemeChanged,
        () -> {
          bundle.portfolio().update();
          refreshBottomBar();
        }
    ));
    pages.put(PageId.TRANSACTIONS, new TransactionsView(bundle.transactions(),
        t -> bundle.market().findBySymbol(t.share().symbol())
            .ifPresent(stock -> navigateToStockDetail(stock, PageId.TRANSACTIONS))));
    pages.put(PageId.ORDERS, new OrdersView(bundle.ordersController()));

    return pages;
  }

  private void navigateToStockDetail(StockDto stock, PageId backTo) {
    StockDetailView view = new StockDetailView(
        stock,
        bundle.stockQuery(),
        bundle.orderPlacement(),
        () -> navManager.navigateTo(backTo));
    navManager.show(view);
  }

  private HBox buildBottomBar() {
    weekValue     = new Label();
    netWorthValue = new Label();
    deltaValue    = new Label();
    cashValue     = new Label();

    VBox week = StatCardFactory.compact("Week", weekValue, "bottom-bar-value");
    VBox netWorth = StatCardFactory.compact("Net worth", netWorthValue, "bottom-bar-value-gold");
    VBox delta = StatCardFactory.compact("This week", deltaValue, "bottom-bar-value");
    VBox cash = StatCardFactory.compact("Cash", cashValue, "bottom-bar-value");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    advanceButton = ButtonFactory.styled("", "advance-button", this::onAdvanceWeek);

    HBox bar = new HBox(week, netWorth, delta, cash, spacer, advanceButton);
    bar.getStyleClass().add("bottom-bar");
    return bar;
  }

  private void onAdvanceWeek() {
    GameSession session = bundle.session();
    BigDecimal hangarCost = session.getUpcomingHangarCost();

    if (session.getPlayer().getMoney().compareTo(hangarCost) < 0) {
      showInsufficientCashPopup(hangarCost);
      return;
    }

    int prevWeek = session.getExchange().getWeek();
    BigDecimal before = session.getPlayer().getNetWorth();
    bundle.game().advanceWeek();

    if (session.getState() == GameSessionState.FINISHED) {
      refreshBottomBar();
      return;
    }

    BigDecimal after = session.getPlayer().getNetWorth();
    lastWeeklyDelta = after.subtract(before);

    bundle.shop().advanceCoinExchange();
    if (shopView != null) {
      shopView.refresh();
    }

    refreshBottomBar();

    WeekSummary summary = buildWeekSummary(prevWeek, before, after);

    Node[] holder = new Node[1];
    WeekSummaryPopup popup = new WeekSummaryPopup(summary, () -> closePopup(holder[0]));
    holder[0] = popup;
    showPopup(popup);
  }

  private WeekSummary buildWeekSummary(int prevWeek, BigDecimal before, BigDecimal after) {
    Player player = bundle.session().getPlayer();

    List<WeekHolding> holdings = player.getPortfolio().getShares().stream()
        .collect(Collectors.groupingBy(s -> s.getStock().getSymbol()))
        .entrySet().stream()
        .map(e -> {
          Stock stock = e.getValue().get(0).getStock();
          BigDecimal qty = e.getValue().stream()
              .map(Share::getQuantity)
              .reduce(BigDecimal.ZERO, BigDecimal::add);
          BigDecimal delta = stock.getLatestPriceChange().multiply(qty);
          BigDecimal percent = stock.getLatestPriceChangePercent();
          return new WeekHolding(stock.getSymbol(), qty, delta, percent);
        })
        .sorted((a, b) -> b.weeklyDelta().abs().compareTo(a.weeklyDelta().abs()))
        .toList();

    BigDecimal hangarCost = bundle.session().getPlayer().getStartingMoney()
        .multiply(BigDecimal.valueOf(bundle.session().getDifficulty().getHangarCostRate()))
        .setScale(2, RoundingMode.HALF_UP);

    return new WeekSummary(
        prevWeek,
        bundle.session().getExchange().getWeek(),
        before,
        after,
        bundle.session().getPlayer().getMoney(),
        hangarCost,
        holdings,
        List.of(),
        List.of()
    );
  }

  private void refreshBottomBar() {
    Player player         = bundle.session().getPlayer();
    Exchange exchange     = bundle.session().getExchange();
    Difficulty difficulty = bundle.session().getDifficulty();

    BigDecimal netWorth = player.getNetWorth();
    BigDecimal threshold = player.getStartingMoney()
        .multiply(BigDecimal.valueOf(difficulty.getGameOverThreshold()));
    BigDecimal warningBand = threshold.multiply(DANGER_ZONE_MULTIPLIER);
    boolean danger = netWorth.compareTo(warningBand) < 0;

    weekValue.setText("Week " + exchange.getWeek());
    netWorthValue.setText(MoneyFormat.formatCurrency(netWorth));

    deltaValue.setText(MoneyFormat.formatSignedCurrency(lastWeeklyDelta));
    deltaValue.getStyleClass().removeAll("gain", "loss");
    if (lastWeeklyDelta.signum() > 0) deltaValue.getStyleClass().add("gain");
    if (lastWeeklyDelta.signum() < 0) deltaValue.getStyleClass().add("loss");

    if (danger) {
      cashValue.setText("Game over at " + MoneyFormat.formatCurrency(threshold));
    } else {
      cashValue.setText(MoneyFormat.formatCurrency(player.getMoney()));
    }

    advanceButton.setText("Advance to Week " + (exchange.getWeek() + 1) + " →");

    bottomBar.getStyleClass().remove("bottom-bar-danger");
    if (danger) bottomBar.getStyleClass().add("bottom-bar-danger");

    refreshNavBar();
  }

  private void refreshNavBar() {
    Player player = bundle.session().getPlayer();
    navBar.updatePlayerInfo(player.getName(), player.getStatus());
  }

  /**
   * Builds a hint describing what the player still needs to reach the next status.
   *
   * @param next           name of the next status level
   * @param equityProgress net-worth progress toward that level, 0.0–1.0
   * @param tradedWeeks    number of distinct weeks the player has traded
   * @param weeksNeeded    weeks required for the next status
   * @param netWorthTarget net worth required for the next status
   * @return a human-readable hint
   */
  private String nextStatusTip(String next, double equityProgress,
                               int tradedWeeks, int weeksNeeded, BigDecimal netWorthTarget) {
    boolean equityDone = equityProgress >= 1.0;
    int weeksLeft = Math.max(0, weeksNeeded - tradedWeeks);

    if (equityDone && weeksLeft > 0) {
      return "Net worth is high enough for " + next + " — keep trading "
          + weeksLeft + " more " + (weeksLeft == 1 ? "week" : "weeks") + ".";
    }
    if (!equityDone && weeksLeft == 0) {
      return "You've traded enough — reach "
          + MoneyFormat.formatCurrency(netWorthTarget) + " net worth for " + next + ".";
    }
    return "To reach " + next + ": "
        + MoneyFormat.formatCurrency(netWorthTarget) + " net worth and "
        + weeksLeft + " more " + (weeksLeft == 1 ? "week" : "weeks") + " of trading.";
  }

  private void showPopup(Node popup) {
    popupHost.getChildren().add(popup);
  }

  private void closePopup(Node popup) {
    popupHost.getChildren().remove(popup);
  }

  private String formatGap(Status next, LevelGap gap) {
    boolean weeks = gap.weeksRemaining() > 0;
    boolean money = gap.moneyRemaining().signum() > 0;
    String weekText = gap.weeksRemaining()
        + (gap.weeksRemaining() == 1 ? " more week" : " more weeks") + " of trading";
    String moneyText = MoneyFormat.formatCurrency(gap.moneyRemaining()) + " more net worth";

    if (weeks && money) {
      return "To reach " + next.displayName() + ":\n" + weekText + " and " + moneyText + ".";
    }
    if (weeks) {
      return "To reach " + next.displayName() + ":\n" + weekText + ".";
    }
    if (money) {
      return "To reach " + next.displayName() + ":\n" + moneyText + ".";
    }
    return "Ready to advance to " + next.displayName() + "!";
  }

  /**
   * Shows a popup telling the player they don't have enough cash
   * to pay the hangar rent, and that they need to sell shares first.
   *
   * @param hangarCost the rent the player must pay next week
   */
  private void showInsufficientCashPopup(BigDecimal hangarCost) {
    BigDecimal playerCash = bundle.session().getPlayer().getMoney();
    Node[] holder = new Node[1];
    InsufficientCashPopup popup = new InsufficientCashPopup(
        hangarCost,
        playerCash,
        () -> closePopup(holder[0])
    );
    holder[0] = popup;
    showPopup(popup);
  }
}
