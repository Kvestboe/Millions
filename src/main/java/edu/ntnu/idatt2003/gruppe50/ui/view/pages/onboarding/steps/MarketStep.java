package edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps;

import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingFlowData;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingStep;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * Onboarding step where the player selects a stock market (CSV file).
 * Offers S&P 500, Oslo Børs, a fun alternative market, or a custom file.
 * Valid when a file has been selected.
 */
public class MarketStep implements OnboardingStep {

  private final Stage stage;
  private final List<VBox> cards = new ArrayList<>();
  private File selectedFile;
  private Label selectedLabel;
  private int selectedIndex = 0;
  private String selectedDisplayName = "S&P 500 (default)";

  /**
   * Constructs a MarketStep.
   *
   * @param stage the primary stage, used for the file chooser dialog
   */
  public MarketStep(Stage stage) {
    this.stage = stage;
    loadDefaultFile("/data/sp500.csv");
  }

  /**
   * Builds and returns the market selection layout.
   *
   * @return the root node of this step's view
   */
  @Override
  public Parent getView() {
    cards.clear();

    VBox container = new VBox(12);
    container.setAlignment(Pos.CENTER);
    container.setPadding(new Insets(24, 80, 24, 80));
    container.getStyleClass().add("root-bg");

    Label title = new Label("Choose your market");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("Which stocks will you trade?");
    subtitle.getStyleClass().add("label-muted");

    selectedLabel = new Label("S&P 500 (default)");
    selectedLabel.getStyleClass().add("label-muted");

    VBox sp500  = createMarketOption("US", "S&P 500",
        "Top 500 US stocks by market cap", "/data/sp500.csv");
    VBox oslo   = createMarketOption("NO", "Oslo Børs",
        "Norwegian stock exchange", "/data/oslo.csv");
    // TODO: replace label and file with the fun market once created
    VBox funMkt = createMarketOption("🎲", "???",
        "A very serious market. Definitely not a joke.", "/data/fun.csv");
    VBox custom = createCustomOption();

    cards.addAll(List.of(sp500, oslo, funMkt, custom));
    cards.get(selectedIndex).getStyleClass().add("market-card-selected");

    selectedLabel.setText(selectedDisplayName);

    VBox options = new VBox(10, sp500, oslo, funMkt, custom);
    options.setMaxWidth(500);
    options.setAlignment(Pos.CENTER);

    container.getChildren().addAll(title, subtitle, options, selectedLabel);
    return container;
  }

  /**
   * Creates a market option card that loads a bundled CSV resource.
   *
   * @param icon         text or emoji icon for the market
   * @param name         display name
   * @param description  short description
   * @param resourcePath path to the bundled CSV resource
   *
   * @return a styled, clickable VBox card
   */
  private VBox createMarketOption(
      String icon, String name, String description, String resourcePath
  ) {
    VBox card = buildCard(icon, name, description);
    card.setOnMouseClicked(_ -> {
      loadDefaultFile(resourcePath);
      selectedDisplayName = "Selected: " + name;
      selectedLabel.setText(selectedDisplayName);
      selectCard(card);
    });
    return card;
  }

  /**
   * Creates a custom file picker option.
   *
   * @return a styled, clickable VBox card for custom file selection
   */
  private VBox createCustomOption() {
    VBox card = buildCard("📁", "Custom file", "Load your own .csv file");

    card.setOnMouseClicked(_ -> {
      FileChooser chooser = new FileChooser();
      chooser.getExtensionFilters().add(
          new FileChooser.ExtensionFilter("CSV files", "*.csv")
      );
      File file = chooser.showOpenDialog(stage);
      if (file != null) {
        selectedFile = file;
        selectedDisplayName = "Selected: " + file.getName();
        selectedLabel.setText(selectedDisplayName);
        selectCard(card);
      }
    });

    return card;
  }

  /**
   * Builds a market card with an icon to the left and stacked
   * name and description labels to the right.
   *
   * @param icon        icon text shown on the left
   * @param name        market name shown as the card title
   * @param description short description shown below the title
   * @return a styled VBox card
   */
  private VBox buildCard(String icon, String name, String description) {
    Label iconLabel = new Label(icon);
    iconLabel.getStyleClass().add("market-icon");
    iconLabel.setMinWidth(32);

    Label nameLabel = new Label(name);
    nameLabel.getStyleClass().add("field-label");

    Label descLabel = new Label(description);
    descLabel.getStyleClass().add("label-muted");

    VBox texts = new VBox(2, nameLabel, descLabel);
    HBox row = new HBox(10, iconLabel, texts);
    row.setAlignment(Pos.CENTER_LEFT);

    VBox card = new VBox(row);
    card.setPadding(new Insets(8, 12, 8, 12));
    card.getStyleClass().add("market-card");
    return card;
  }

  /**
   * Selects the clicked card and deselects all others.
   *
   * @param clicked the card that was clicked
   */
  private void selectCard(VBox clicked) {
    cards.forEach(card -> card.getStyleClass().remove("market-card-selected"));
    clicked.getStyleClass().add("market-card-selected");
    selectedIndex = cards.indexOf(clicked);
  }

  /**
   * Loads a bundled CSV resource as a temporary file.
   *
   * @param resourcePath the classpath resource path
   */
  private void loadDefaultFile(String resourcePath) {
    try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
      if (is == null) {
        return;
      }
      File temp = File.createTempFile("market", ".csv");
      temp.deleteOnExit();
      Files.copy(is, temp.toPath(), StandardCopyOption.REPLACE_EXISTING);
      selectedFile = temp;
    } catch (IOException e) {
      selectedFile = null;
    }
  }

  /**
   * Returns true if a stock file has been selected.
   *
   * @return true if valid
   */
  @Override
  public boolean isValid() {
    return selectedFile != null;
  }

  /**
   * Sets the chosen stock file on the shared onboarding data.
   *
   * @param data the shared mutable onboarding data object
   */
  @Override
  public void contribute(OnboardingFlowData data) {
    data.stockFile = selectedFile;
  }
}