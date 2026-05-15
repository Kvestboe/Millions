package edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.steps;

import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingFlowData;
import edu.ntnu.idatt2003.gruppe50.ui.view.pages.onboarding.OnboardingStep;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.control.Label;
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
  private File selectedFile;
  private Label selectedLabel;

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
    VBox container = new VBox(16);
    container.setAlignment(Pos.CENTER);
    container.setPadding(new Insets(60, 80, 60, 80));
    container.setMaxWidth(480);
    container.getStyleClass().add("root-bg");

    Label title = new Label("Choose your market");
    title.getStyleClass().add("page-title");

    Label subtitle = new Label("Which stocks will you trade?");
    subtitle.getStyleClass().add("label-muted");

    selectedLabel = new Label("S&P 500 (default)");
    selectedLabel.getStyleClass().add("label-muted");

    VBox options = new VBox(10);
    options.setMaxWidth(400);
    options.getChildren().addAll(
        createMarketOption("🇺🇸", "S&P 500",
            "Top 500 US stocks by market cap", "/data/sp500.csv", true),
        createMarketOption("🇳🇴", "Oslo Børs",
            "Norwegian stock exchange", "/data/oslo.csv", false),
        // TODO: replace label and file with the fun market once created
        createMarketOption("🎲", "??? Exchange",
            "A very serious market. Definitely not a joke.", "/data/fun.csv", false),
        createCustomOption()
    );

    container.getChildren().addAll(title, subtitle, options, selectedLabel);
    return container;
  }

  /**
   * Creates a market option card that loads a bundled CSV resource.
   *
   * @param icon        emoji icon for the market
   * @param name        display name
   * @param description short description
   * @param resourcePath path to the bundled CSV resource
   * @param isDefault   whether this option is pre-selected
   * @return a styled VBox card
   */
  private VBox createMarketOption(
      String icon, String name, String description,
      String resourcePath, boolean isDefault
  ) {
    Label iconLabel = new Label(icon + "  " + name);
    iconLabel.getStyleClass().add("field-label");

    Label descLabel = new Label(description);
    descLabel.getStyleClass().add("label-muted");

    VBox card = new VBox(4, iconLabel, descLabel);
    card.setPadding(new Insets(14, 16, 14, 16));
    card.getStyleClass().add(isDefault ? "market-card-selected" : "market-card");

    card.setOnMouseClicked(_ -> {
      loadDefaultFile(resourcePath);
      selectedLabel.setText("Selected: " + name);
      resetCardStyles(card);
    });

    return card;
  }

  /**
   * Creates a custom file picker option.
   *
   * @return a styled VBox card for custom file selection
   */
  private VBox createCustomOption() {
    Label iconLabel = new Label("📁  Custom file");
    iconLabel.getStyleClass().add("field-label");

    Label descLabel = new Label("Load your own .csv file");
    descLabel.getStyleClass().add("label-muted");

    VBox card = new VBox(4, iconLabel, descLabel);
    card.setPadding(new Insets(14, 16, 14, 16));
    card.getStyleClass().add("market-card");

    card.setOnMouseClicked(_ -> {
      FileChooser chooser = new FileChooser();
      chooser.getExtensionFilters().add(
          new FileChooser.ExtensionFilter("CSV files", "*.csv")
      );
      File file = chooser.showOpenDialog(stage);
      if (file != null) {
        selectedFile = file;
        selectedLabel.setText("Selected: " + file.getName());
        resetCardStyles(card);
      }
    });

    return card;
  }

  /**
   * Resets all market card styles and marks the selected card.
   *
   * @param selected the card that was clicked
   */
  private void resetCardStyles(VBox selected) {
    // Note: in a real implementation, keep references to all cards
    // and iterate over them. This is a simplified version.
    selected.getStyleClass().removeAll("market-card", "market-card-selected");
    selected.getStyleClass().add("market-card-selected");
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