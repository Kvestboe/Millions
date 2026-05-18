package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.domain.game.GameResult;
import edu.ntnu.idatt2003.gruppe50.shared.MoneyFormat;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextAlignment;

// ui/view/pages/GameOverView.java
public class GameOverView extends VBox implements Page {

  private final GameResult result;
  private final Runnable onPlayAgain;
  private final Runnable onMainMenu;

  public GameOverView(GameResult result, Runnable onPlayAgain, Runnable onMainMenu) {
    this.result = result;
    this.onPlayAgain = onPlayAgain;
    this.onMainMenu = onMainMenu;
    getStyleClass().add("root-bg");
    buildUI();
  }

  private void buildUI() {
    // Sentrert innhold-container, maks 560px bred
    VBox inner = new VBox(20);
    inner.setAlignment(Pos.CENTER);
    inner.setPadding(new Insets(40, 80, 40, 80));
    inner.setMaxWidth(560);

    inner.getChildren().addAll(
        buildIcon(),
        buildTitle(),
        buildSubtitle(),
        buildNetWorthCard(),   // 1. Full bredde, gull
        buildStatsRow(),       // 2. Tre kort side om side
        buildScoreCard(),      // 3. Full bredde, score til høyre
        buildNavBar()
    );

    setAlignment(Pos.CENTER);
    VBox.setVgrow(inner, Priority.ALWAYS);
    getChildren().add(inner);
  }

  private Label buildIcon() {
    Label icon = new Label(result.won() ? "🚀" : "📉");
    icon.getStyleClass().add("story-icon"); // 32px, samme som StoryStep
    return icon;
  }

  private Label buildTitle() {
    Label title = new Label(result.won() ? "You reached the Moon." : "Mission failed.");
    title.getStyleClass().add("game-title");
    if (!result.won()) {
      title.setStyle("-fx-text-fill: #F87171;"); // -loss farge
    }
    return title;
  }

  private Label buildSubtitle() {
    String text = result.won()
        ? "Rocket launched. Mission complete.\nYou earned your place among the stars."
        : "Grounded. The market won this round —\nyour portfolio couldn't keep up with the costs.";

    Label subtitle = new Label(text);
    subtitle.getStyleClass().add("label-muted");
    subtitle.setWrapText(true);
    subtitle.setTextAlignment(TextAlignment.CENTER);
    return subtitle;
  }

  private VBox buildNetWorthCard() {
    Label overline = new Label("FINAL NET WORTH");
    overline.getStyleClass().add("label-overline");
    overline.setStyle("-fx-text-fill: -text-secondary;"); // overstyr gull fra info-card-accent

    Label value = new Label(MoneyFormat.format(result.finalNetWorth()));
    value.getStyleClass().add("net-worth-value"); // 24px bold gull — bare tallet
    value.setStyle("-fx-font-size: 28px;");       // litt større enn default 24px

    VBox card = new VBox(4, overline, value);
    card.getStyleClass().add(result.won() ? "info-card-accent" : "info-card");
    if (!result.won()) card.setStyle("-fx-border-color: #F87171;");
    return card;
  }

  private HBox buildStatsRow() {
    VBox weeks = buildStatCard("📅 Weeks played", result.weeksPlayed() + " weeks");
    VBox diff = buildStatCard("⚡ Difficulty", result.difficulty().name());
    VBox third = result.won()
        ? buildStatCard("🏁 Starting capital", MoneyFormat.format(result.startingCapital()))
        : buildStatCard("🎯 Target", "1 000 000 kr");

    HBox row = new HBox(12, weeks, diff, third);
    HBox.setHgrow(weeks, Priority.ALWAYS);
    HBox.setHgrow(diff, Priority.ALWAYS);
    HBox.setHgrow(third, Priority.ALWAYS);
    return row;
  }

  private VBox buildStatCard(String overlineText, String valueText) {
    Label overline = new Label(overlineText);
    overline.getStyleClass().add("label-overline");

    Label value = new Label(valueText);
    value.getStyleClass().add("section-title"); // 16px i stedet for 14px field-label
    value.setStyle("-fx-font-weight: 600;");

    VBox card = new VBox(6, overline, value);
    card.getStyleClass().add("info-card");
    return card;
  }

  private HBox buildScoreCard() {
    Label overline = new Label("🏆 LEADERBOARD SCORE");
    overline.getStyleClass().add("label-overline");

    Label hint = new Label("Rank #3 on " + result.difficulty().name());
    hint.getStyleClass().add("label-muted");

    VBox left = new VBox(4, overline, hint);

    Label score = new Label(String.valueOf(result.calculateScore()));
    score.getStyleClass().add("net-worth-value");
    score.setStyle("-fx-font-size: 20px;");

    VBox scoreBox = new VBox(score);
    scoreBox.setAlignment(Pos.CENTER_RIGHT);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox card = new HBox(left, spacer, scoreBox);
    card.setAlignment(Pos.CENTER_LEFT);
    card.getStyleClass().add("info-card-accent");
    card.setPadding(new Insets(14, 16, 14, 16));
    return card;
  }

  private VBox buildLoseReasonBox() {
    String text = switch (result.difficulty()) {
      case EASY   -> "You've lost too much to continue. The math is simple: "
          + "not enough capital left to keep the rocket ready.";
      case MEDIUM -> "Below this point, the costs outweigh the options. "
          + "There's nothing left to work with.";
      case HARD   -> "The margin for error on Hard is razor thin — "
          + "and you used it all up.";
    };

    Label label = new Label(text);
    label.getStyleClass().add("label-muted");
    label.setWrapText(true);

    VBox box = new VBox(label);
    box.getStyleClass().add("info-card");
    return box;
  }

  private HBox buildNavBar() {
    Button mainMenu = new Button("← Main Menu");
    mainMenu.getStyleClass().add("btn-secondary");
    mainMenu.setPrefWidth(150);
    mainMenu.setOnAction(_ -> onMainMenu.run());

    Button playAgain = new Button(result.won() ? "Play Again →" : "Try Again →");
    playAgain.getStyleClass().add("btn-primary");
    playAgain.setPrefWidth(150);
    playAgain.setOnAction(_ -> onPlayAgain.run());

    HBox bar = new HBox(15, mainMenu);

    if (result.won()) {
      Button leaderboard = new Button("🏆 Leaderboard");
      leaderboard.getStyleClass().add("btn-accent");
      leaderboard.setPrefWidth(150);
      leaderboard.setOnAction(_ -> { /* TODO: koble til leaderboard */ });
      bar.getChildren().add(leaderboard);
    }

    bar.getChildren().add(playAgain);
    bar.setAlignment(Pos.CENTER);
    bar.setPadding(new Insets(16, 48, 24, 48));
    bar.getStyleClass().add("navbar");
    return bar;
  }

  @Override
  public Parent getView() {
    return this;
  }
}
