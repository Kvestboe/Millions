package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.domain.game.Difficulty;
import edu.ntnu.idatt2003.gruppe50.domain.game.GameOutcome;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import java.math.BigDecimal;

public class GameOverView extends VBox implements Page {

  private final GameOutcome gameOutcome;
  private final BigDecimal finalNetWorth;
  private final int weeksPlayed;
  private final Difficulty difficulty;
  private final Runnable onPlayAgain;
  private final Runnable onMainMenu;

  public GameOverView(GameOutcome gameOutcome,
                      BigDecimal finalNetWorth,
                      int weeksPlayed,
                      Difficulty difficulty,
                      Runnable onPlayAgain,
                      Runnable onMainMenu)
  {
    this.gameOutcome = gameOutcome;
    this.finalNetWorth = finalNetWorth;
    this.weeksPlayed = weeksPlayed;
    this.difficulty = difficulty;
    this.onPlayAgain = onPlayAgain;
    this.onMainMenu = onMainMenu;

    buildUI();
  }

  private void buildUI() {
    Label heading = new Label(gameOutcome == GameOutcome.WON ? "You win!" : "Game Over");

    Label subtitle = new Label(gameOutcome == GameOutcome.WON
        ? "You reached millionaire status,"
        + "\nfinished your rocket and reached THE MOON!!!"
        : "Your portfolio collapsed");

    Label netWorth = new Label("Final net worth: " + MoneyFormat.format(finalNetWorth));

    Label weeks = new Label("Weeks played: " + weeksPlayed);

    Label diff = new Label("Difficulty: " + difficulty.name());

    Button playAgain = ButtonFactory.primary("Play Again", onPlayAgain);
    Button mainMenu = ButtonFactory.secondary("Main Menu", onMainMenu);

    getChildren().addAll(heading, subtitle, netWorth, weeks, diff, playAgain, mainMenu);
  }

  @Override
  public Parent getView() {
    return this;
  }
}
