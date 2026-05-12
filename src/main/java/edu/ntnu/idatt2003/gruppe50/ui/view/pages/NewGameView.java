package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.shared.Parse;
import edu.ntnu.idatt2003.gruppe50.ui.controller.NewGameController;
import java.io.File;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * View for creating a new game in the Millions stock simulator.
 * Provides input fields for player name, starting capital, and stock data file.
 */
public class NewGameView {

  private final Stage stage;
  private final NewGameController controller;
  private final Label errorLabel = new Label();
  private final Runnable onBack;
  private File selectedFile;
  private TextField nameField;
  private TextField capitalField;
  private Button startBtn;

  /**
   * Constructs a new NewGameView.
   *
   * @param stage the primary stage used for displaying file dialogs
   * @param controller the controller handling game startup logic
   */
  public NewGameView(Stage stage, NewGameController controller, Runnable onBack) {
    this.stage = stage;
    this.controller = controller;
    this.onBack = onBack;
    loadDefaultFile();
  }

  /**
   * Builds and returns the scene for the new game screen.
   *
   * @return the configured JavaFX scene
   */
  public Scene getScene() {
    BorderPane root = new BorderPane();
    root.getStyleClass().add("root-bg");

    Button backBtn = new Button("Back");
    backBtn.setOnAction(_ -> onBack.run());

    StackPane center = new StackPane(createCardBox());
    root.setTop(backBtn);
    root.setCenter(center);

    Scene scene = new Scene(root, 1280, 900);
    scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
    return scene;
  }

  private VBox createCardBox() {
    VBox cardBox = new VBox();
    cardBox.setPrefWidth(860);
    cardBox.setMaxWidth(860);
    cardBox.setPrefHeight(420);
    cardBox.setMaxHeight(420);
    cardBox.getStyleClass().add("card-box");
    cardBox.setAlignment(Pos.CENTER);
    cardBox.setPadding(new Insets(40, 32, 40, 32));
    cardBox.setSpacing(12);

    nameField = createTextField("Enter name...");
    capitalField = createTextField("10 000kr");
    startBtn = createStartButton();
    errorLabel.getStyleClass().add("error-label");
    errorLabel.setVisible(false);

    cardBox.getChildren().addAll(
        createTitleBox(),
        createInputBox("Player name", nameField),
        createInputBox("Starting Capital", capitalField),
        createFilePickerField(),
        startBtn,
        errorLabel
    );
    return cardBox;
  }

  private VBox createTitleBox() {
    // Millions title
    Label millionsLabel = new Label("Millions");
    millionsLabel.setAlignment(Pos.TOP_CENTER);
    millionsLabel.getStyleClass().add("title-label");

    // Trading Game subtitle
    Label tradingGameLabel = new Label("Trading Game");
    tradingGameLabel.setAlignment(Pos.TOP_CENTER);
    tradingGameLabel.getStyleClass().add("subtitle-label");

    // Title box
    VBox titleBox = new VBox(2);
    titleBox.setAlignment(Pos.CENTER);
    titleBox.getChildren().addAll(millionsLabel, tradingGameLabel);

    return titleBox;
  }

  private TextField createTextField(String placeholder) {
    TextField textField = new TextField();
    textField.setPromptText(placeholder);
    textField.getStyleClass().add("input-field");
    textField.textProperty().addListener((_, _, _) -> updateStartButton());
    return textField;
  }

  private VBox createInputBox(String label, TextField field) {
    VBox box = new VBox(4);

    Label fieldLabel = new Label(label);
    fieldLabel.getStyleClass().add("field-label");

    box.getChildren().addAll(fieldLabel, field);
    return box;
  }

  private VBox createFilePickerField() {
    VBox box = new VBox(4);
    box.setMaxWidth(Double.MAX_VALUE);

    Label fieldLabel = new Label("Stock data file");
    fieldLabel.getStyleClass().add("field-label");

    String defaultName = selectedFile != null ? "stocks.csv" : "Choose .csv file";
    Label fileNameLabel = new Label(defaultName);
    fileNameLabel.getStyleClass().add("file-name-label");

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox filePickerRow = new HBox(8);
    filePickerRow.getStyleClass().add("file-picker-row");
    filePickerRow.setAlignment(Pos.CENTER_LEFT);
    filePickerRow.setMaxWidth(Double.MAX_VALUE);
    filePickerRow.getChildren().addAll(fileNameLabel, spacer, createAddButton(fileNameLabel));

    box.getChildren().addAll(fieldLabel, filePickerRow);
    return box;
  }

  private Button createStartButton() {
    Button btn = new Button("Start Game");
    btn.getStyleClass().add("button-disabled");
    btn.setMaxWidth(Double.MAX_VALUE);
    btn.setOnAction(_ -> {
          String error = validate();
          if (error != null) {
            showError(error);
          } else {
            errorLabel.setVisible(false);
            controller.onStartGame(nameField.getText(), capitalField.getText(), selectedFile);
          }
        });
    return btn;
  }

  private Button createAddButton(Label fileNameLabel) {
    Button addButton = new Button("+");
    addButton.getStyleClass().add("add-button");
    addButton.setOnAction(_ -> {
          File file = handleFilePicker();
          if (file != null) {
            selectedFile = file;
            fileNameLabel.setText(file.getName());
            updateStartButton();
          }
        });
    return addButton;
  }

  private File handleFilePicker() {
    FileChooser fileChooser = new FileChooser();
    fileChooser.getExtensionFilters().add(
        new FileChooser.ExtensionFilter("CSV-filer", "*.csv")
    );
    return fileChooser.showOpenDialog(stage);
  }

  private void updateStartButton() {
    if (startBtn == null) {
      return;
    }

    boolean allFilled =
        !nameField.getText().isBlank()
        && isValidCapital(capitalField.getText())
        && selectedFile != null;

    startBtn.getStyleClass().removeAll("button-active", "button-disabled");

    if (allFilled) {
      startBtn.getStyleClass().add("button-active");
      errorLabel.setVisible(false);
    } else {
      startBtn.getStyleClass().add("button-disabled");
    }
  }

  private void showError(String message) {
    errorLabel.setText(message);
    errorLabel.setVisible(true);
  }

  private String validate() {
    if (nameField.getText().isBlank()) {
      return "Please enter your player name";
    }
    if (capitalField.getText().isBlank()) {
      return "Please enter starting capital";
    }
    if (!isValidCapital(capitalField.getText())) {
      return "Starting capital has to be a number";
    }
    if (selectedFile == null) {
      return "You must enter a file";
    }
    return null;
  }

  private boolean isValidCapital(String input) {
    try {
      Parse.parseBigDecimal(input);
      return true;
    } catch (IllegalArgumentException e) {
      return false;
    }
  }

  private void loadDefaultFile() {
    try (var inputStream = getClass().getResourceAsStream("/data/sp500.csv")) {
      if (inputStream == null) {
        return;
      }
      File tempFile = File.createTempFile("stocks", ".csv");
      tempFile.deleteOnExit();
      java.nio.file.Files
          .copy(inputStream, tempFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
      selectedFile = tempFile;
    } catch (Exception e) {
      selectedFile = null;
    }
  }
}
