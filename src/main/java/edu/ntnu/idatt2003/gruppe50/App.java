package edu.ntnu.idatt2003.gruppe50;

import edu.ntnu.idatt2003.gruppe50.application.command.LoadGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.application.command.StartGameSessionUseCase;
import edu.ntnu.idatt2003.gruppe50.domain.market.Exchange;
import edu.ntnu.idatt2003.gruppe50.domain.market.Stock;
import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Player;
import edu.ntnu.idatt2003.gruppe50.domain.trade.TransactionFactory;
import edu.ntnu.idatt2003.gruppe50.infrastructure.CSVFileHandler;
import edu.ntnu.idatt2003.gruppe50.ui.model.OnboardingData;
import edu.ntnu.idatt2003.gruppe50.ui.view.ThemeManager;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.GameViewCoordinator;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.MainMenuView;

import java.util.List;
import java.util.UUID;

import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingFlow;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.CapitalStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.DifficultyStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.LaunchStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.MarketStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.NameStep;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps.StoryStep;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Hello world!
 *
 */
public final class App extends Application {

  private final AppModule module = new AppModule();
  private Stage stage;
  private ThemeManager themeManager;


  @Override
  public void start(Stage stage) {
    this.stage = stage;
    this.themeManager = new ThemeManager();
    showMainMenu();
  }

  public void switchToGame(UUID gameId) {
    module.loadGameSession.execute(new LoadGameSessionUseCase.Request(gameId));
    GameSessionControllerBundle bundle = module.gameBundle(gameId);
    Scene scene = new GameViewCoordinator(bundle, this::showMainMenu, this::showNewGame).getScene();
    themeManager.apply(scene);
    stage.setScene(scene);
    stage.show();
  }

  private void showMainMenu() {
    MainMenuView menu = new MainMenuView(
        this::showNewGame,
        Platform::exit
    );
    Scene scene = menu.getScene();
    themeManager.apply(scene);
    stage.setScene(scene);
    stage.show();
  }

  private void showNewGame() {
    CapitalStep capitalStep = new CapitalStep();

    List<OnboardingStep> steps = List.of(
        new StoryStep(),
        new NameStep(),
        new DifficultyStep(),
        capitalStep,
        new MarketStep(stage),
        new LaunchStep()
    );

    OnboardingFlow flow = new OnboardingFlow(
        steps,
        this::startGameFromOnboarding,
        this::showMainMenu
    );

    Scene scene = flow.getScene();
    themeManager.apply(scene);
    stage.setScene(scene);
  }

  private void startGameFromOnboarding(OnboardingData data) {
    List<Stock> stocks = CSVFileHandler.readLines(data.stockFile().toPath());
    Player player = new Player(data.playerName(), data.startingCapital());
    Exchange exchange = new Exchange("Stock exchange", stocks, new TransactionFactory());

    UUID gameId = module.startGameSession.execute(
        new StartGameSessionUseCase.Request(player, exchange, data.difficulty())
    ).gameId();

    switchToGame(gameId);
  }
}
