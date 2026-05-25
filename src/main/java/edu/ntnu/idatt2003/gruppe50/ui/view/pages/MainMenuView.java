package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

public class MainMenuView extends StackPane{

  private final Runnable onNewGame;
  private final Runnable onQuit;
  private final Runnable onSettings;
  private Runnable onLoadGame;
  private Runnable onLeaderboard;
  private Runnable onContinueGame;

  public MainMenuView(Runnable onNewGame, Runnable onSettings, Runnable onQuit) {
    this.onNewGame = onNewGame;
    this.onSettings = onSettings;
    this.onQuit = onQuit;
    build();
  }

  public void setOnLoadGame(Runnable onLoadGame) {
    this.onLoadGame = onLoadGame;
  }

  public void setOnLeaderboard(Runnable onLeaderboard) {
    this.onLeaderboard = onLeaderboard;
  }

  public void setOnContinueGame(Runnable onContinueGame) {
    this.onContinueGame = onContinueGame;
  }

  private void build() {
    getStylesheets().add(
        getClass().getResource("/css/views/mainMenu.css").toExternalForm()
    );

    Canvas chart = buildChart();
    VBox content = buildContent();

    getStyleClass().add("main-menu");
    StackPane.setAlignment(chart, Pos.BOTTOM_CENTER);
    StackPane.setAlignment(content, Pos.CENTER);
    getChildren().addAll(chart, content);
  }

  private Canvas buildChart() {
    Canvas canvas = new Canvas();
    canvas.setHeight(100);
    canvas.widthProperty().bind(widthProperty());

    canvas.widthProperty().addListener((obs, oldVal, newVal) -> {
      drawChart(canvas.getGraphicsContext2D(), newVal.doubleValue());
    });

    return canvas;
  }

  private void drawChart(GraphicsContext gc, double width) {
    gc.clearRect(0, 0, width, 100);

    double[] xPoints = {0, 80, 160, 250, 340, 420, 500, 580, 640, 720, 800, 900, 1000, 1100, 1200, 1350, 1500, 1650, 1800, width};
    double[] yPoints = {110, 88, 95, 65, 70, 42, 48, 22, 28, 18, 24, 12, 20, 8, 15, 5, 18, 8, 12, 5};

    double[] xFill = {0, 80, 160, 250, 340, 420, 500, 580, 640, 720, 800, 900, 1000, 1100, 1200, 1350, 1500, 1650, 1800, width, width, 0};
    double[] yFill = {110, 88, 95, 65, 70, 42, 48, 22, 28, 18, 24, 12, 20, 8, 15, 5, 18, 8, 12, 5, 100, 100};

    gc.setFill(Color.web("#FFD166", 0.08));
    gc.fillPolygon(xFill, yFill, xFill.length);

    gc.setStroke(Color.web("#FFD166", 0.3));
    gc.setLineWidth(1.5);
    gc.strokePolyline(xPoints, yPoints, xPoints.length);
  }

  private VBox buildContent() {
    Separator separator = new Separator();
    separator.getStyleClass().add("menu-separator");
    separator.setMaxWidth(340);

    Label versionLabel = new Label("v1.0.0");
    versionLabel.getStyleClass().add("version-label");

    VBox content = new VBox(14);
    content.getChildren().addAll(buildHeader(),
        buildPrimaryButton(),
        buildSecondaryButtons(), separator,
        buildSystemButtons(), versionLabel
    );
    content.setAlignment(Pos.CENTER);
    content.setPrefWidth(340);

    return content;
  }

  private VBox buildHeader() {
    Label topLabel = new Label("IDATT2003");
    topLabel.getStyleClass().add("menu-top-label");

    Label title = new Label("MILLIONS");
    title.getStyleClass().add("header-title");

    Label subtitle = new Label("A stock market game");
    subtitle.getStyleClass().add("menu-subtitle");

    VBox header = new VBox();
    header.getChildren().addAll(topLabel, title, subtitle);
    header.setAlignment(Pos.CENTER);

    return header;
  }

  private Button buildPrimaryButton() {
    Label icon = new Label("▶");

    Label text = new Label("Continue game");

    Label subtitle = new Label("Week 12");
    subtitle.getStyleClass().add("button-subtitle");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox content = new HBox(8, icon, text, spacer, subtitle);
    content.setAlignment(Pos.CENTER_LEFT);

    Button continueBtn = ButtonFactory.styled("", "btn-accent",() -> {
      if (onContinueGame != null) {
        onContinueGame.run();
      }
    });
    continueBtn.setGraphic(content);
    continueBtn.setMaxWidth(340);

    return continueBtn;
  }

  private HBox buildSecondaryButtons() {
    // De tre ikonknappene: Nytt spill, Last inn, Ledertavle
    // Returnerer HBox med tre knapper

    Button newGameBtn    = ButtonFactory.iconButton("✚", "New Game",    onNewGame);
    Button loadGameBtn   = ButtonFactory.iconButton("⬆", "Load Game",   () -> { if (onLoadGame != null) onLoadGame.run(); });
    Button leaderboardBtn = ButtonFactory.iconButton("★", "Leaderboard",() -> { if (onLeaderboard != null) onLeaderboard.run(); });
    leaderboardBtn.setOnAction(e -> { if (onLeaderboard != null) onLeaderboard.run();});

    HBox.setHgrow(newGameBtn, Priority.ALWAYS);
    HBox.setHgrow(loadGameBtn, Priority.ALWAYS);
    HBox.setHgrow(leaderboardBtn, Priority.ALWAYS);

    HBox secondaryButtonsBox = new HBox(10);
    secondaryButtonsBox.getChildren().addAll(newGameBtn, loadGameBtn, leaderboardBtn);
    secondaryButtonsBox.setMaxWidth(340);

    return secondaryButtonsBox;
  }

  private HBox buildSystemButtons() {
    Button settings = ButtonFactory.styled("⛭ Settings", "system-button", onSettings);
    Button quit = ButtonFactory.styled("✕ Quit", "system-button-danger", onQuit);

    HBox.setHgrow(settings, Priority.ALWAYS);
    HBox.setHgrow(quit, Priority.ALWAYS);
    settings.setMaxWidth(Double.MAX_VALUE);
    quit.setMaxWidth(Double.MAX_VALUE);

    HBox systemBox = new HBox(10);
    systemBox.getChildren().addAll(settings, quit);
    systemBox.setMaxWidth(340);

    return systemBox;
  }
}
