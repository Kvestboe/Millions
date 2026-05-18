package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.NavigationManager;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.SnapshotResult;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.BorderPane;
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

  public MainMenuView(Runnable onNewGame, Runnable onSettings, Runnable onQuit) {
    this.onNewGame = onNewGame;
    this.onSettings = onSettings;
    this.onQuit = onQuit;
    build();
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

    Button continueBtn = new Button();
    continueBtn.setGraphic(content);
    continueBtn.setMaxWidth(340);
    continueBtn.getStyleClass().add("btn-accent");

    return continueBtn;
  }

  private HBox buildSecondaryButtons() {
    // De tre ikonknappene: Nytt spill, Last inn, Ledertavle
    // Returnerer HBox med tre knapper

    Button newGameBtn = createSecondaryButton("✚", "New Game");
    Button loadGameBtn = createSecondaryButton("⬆", "Load Game");
    Button leaderboardBtn = createSecondaryButton("★", "Leaderboard");

    newGameBtn.setOnAction(e -> onNewGame.run());

    HBox.setHgrow(newGameBtn, Priority.ALWAYS);
    HBox.setHgrow(loadGameBtn, Priority.ALWAYS);
    HBox.setHgrow(leaderboardBtn, Priority.ALWAYS);

    HBox secondaryButtonsBox = new HBox(10);
    secondaryButtonsBox.getChildren().addAll(newGameBtn, loadGameBtn, leaderboardBtn);
    secondaryButtonsBox.setMaxWidth(Double.MAX_VALUE);
    secondaryButtonsBox.setMaxWidth(340);

    return secondaryButtonsBox;
  }

  private HBox buildSystemButtons() {
    // Innstillinger + Avslutt
    // Avslutt kobler onQuit til onAction
    Button settings = new Button("⛭ Settings");
    settings.getStyleClass().add("system-button");
    settings.setOnAction(e -> onSettings.run());

    Button quit = new Button("✕ Quit");
    quit.getStyleClass().add("system-button-danger");
    quit.setOnAction(e -> onQuit.run());

    HBox.setHgrow(settings, Priority.ALWAYS);
    HBox.setHgrow(quit, Priority.ALWAYS);
    settings.setMaxWidth(Double.MAX_VALUE);
    quit.setMaxWidth(Double.MAX_VALUE);

    HBox systemBox = new HBox(10);
    systemBox.getChildren().addAll(settings, quit);
    systemBox.setMaxWidth(340);

    return systemBox;
  }

  private Button createSecondaryButton(String icon, String text) {
    Label iconLabel = new Label(icon);
    iconLabel.getStyleClass().add("secondary-button-icon");

    Label textLabel = new Label(text);
    textLabel.getStyleClass().add("secondary-button-text");

    VBox content = new VBox(5, iconLabel, textLabel);
    content.setAlignment(Pos.CENTER);

    Button btn = new Button();
    btn.setGraphic(content);
    btn.setMaxWidth(Double.MAX_VALUE);
    btn.getStyleClass().add("btn-secondary");
    return btn;
  }
}
