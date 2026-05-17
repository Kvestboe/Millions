package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import java.util.function.Consumer;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class SettingsView extends VBox {

  private final Runnable onBack;
  private final Consumer<Boolean> onFullscreen;
  private Slider volumeSlider;

  public SettingsView(Runnable onBack, Consumer<Boolean> onFullscreen) {
    this.onBack = onBack;
    this.onFullscreen = onFullscreen;
    build();
  }

  private void build() {
    getStylesheets().addAll(
        getClass().getResource("/css/views/settings.css").toExternalForm(),
        getClass().getResource("/css/base.css").toExternalForm()
    );
    getStyleClass().add("settings-view");
    setSpacing(8);
    setPadding(new Insets(40));
    setAlignment(Pos.TOP_LEFT);

    getChildren().addAll(
        buildHeader(),
        buildSectionLabel("GENERAL"),
        buildFullscreenRow(),
        buildSoundRow(),
        buildSectionLabel("LANGUAGE"),
        buildLanguageRow()
    );
  }

  private HBox buildHeader() {
    Button backBtn = new Button("← Back");
    backBtn.getStyleClass().add("btn-secondary");
    backBtn.setOnAction(e -> onBack.run());

    Label title = new Label("Settings");
    title.getStyleClass().add("page-title");

    HBox header = new HBox(12, backBtn, title);
    header.setAlignment(Pos.CENTER_LEFT);
    header.setPadding(new Insets(0, 0, 16, 0));
    return header;
  }

  private Label buildSectionLabel(String text) {
    Label label = new Label(text);
    label.getStyleClass().add("settings-section-label");
    VBox.setMargin(label, new Insets(10, 0, 4, 0));
    return label;
  }

  private HBox buildFullscreenRow() {
    Label title = new Label("Fullscreen");
    title.getStyleClass().add("settings-row-title");

    Label subtitle = new Label("Run the game in fullscreen mode");
    subtitle.getStyleClass().add("settings-row-subtitle");

    VBox text = new VBox(2, title, subtitle);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button toggle = createToggle(false);
    toggle.setOnAction(e -> {
      boolean isOn = toggle.getStyleClass().contains("toggle-on");
      toggle.getStyleClass().remove(isOn ? "toggle-on" : "toggle-off");
      toggle.getStyleClass().add(isOn ? "toggle-off" : "toggle-on");

      TranslateTransition t = new TranslateTransition(
          Duration.millis(150), (Label) toggle.getGraphic());
      t.setToX(isOn ? -10 : 10);
      t.play();

      onFullscreen.accept(!isOn);
    });

    HBox row = new HBox(text, spacer, toggle);
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().add("settings-row");
    return row;
  }

  private HBox buildSoundRow() {
    Label title = new Label("Sound");
    title.getStyleClass().add("settings-row-title");

    Label subtitle = new Label("Enable sound effects and music");
    subtitle.getStyleClass().add("settings-row-subtitle");

    VBox text = new VBox(2, title, subtitle);

    volumeSlider = new Slider(0, 100, 65);
    volumeSlider.getStyleClass().add("volume-slider");
    volumeSlider.setPrefWidth(400);
    volumeSlider.setMaxWidth(400);

    Label volumeLabel = new Label("65%");
    volumeLabel.getStyleClass().add("settings-row-subtitle");

    volumeSlider.valueProperty().addListener((obs, oldVal, newVal) ->
        volumeLabel.setText((int) newVal.doubleValue() + "%")
    );

    HBox sliderRow = new HBox(8, volumeSlider, volumeLabel);
    HBox.setHgrow(volumeSlider, Priority.ALWAYS);
    sliderRow.setAlignment(Pos.CENTER_LEFT);

    VBox soundContent = new VBox(10, text, sliderRow);
    HBox.setHgrow(soundContent, Priority.ALWAYS);

    Region spacer = new Region();

    Button toggle = createToggle(true);

    // Felles metoder for å styre tilstand
    Runnable turnOn = () -> {
      if (toggle.getStyleClass().contains("toggle-off")) {
        toggle.getStyleClass().remove("toggle-off");
        toggle.getStyleClass().add("toggle-on");
        TranslateTransition t = new TranslateTransition(
            Duration.millis(500), (Label) toggle.getGraphic());
        t.setToX(10);
        t.play();
        if (volumeSlider.getValue() == 0) {
          volumeSlider.setValue(65);
        }
      }
    };

    Runnable turnOff = () -> {
      if (toggle.getStyleClass().contains("toggle-on")) {
        toggle.getStyleClass().remove("toggle-on");
        toggle.getStyleClass().add("toggle-off");
        TranslateTransition t = new TranslateTransition(
            Duration.millis(500), (Label) toggle.getGraphic());
        t.setToX(-10);
        t.play();
        volumeSlider.setValue(0);
      }
    };

    toggle.setOnAction(e -> {
      if (toggle.getStyleClass().contains("toggle-on")) {
        turnOff.run();
      } else {
        turnOn.run();
      }
    });

    volumeSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
      if (newVal.doubleValue() > 0) {
        turnOn.run();
      } else {
        turnOff.run();
      }
    });

    HBox row = new HBox(soundContent, spacer, toggle);
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().add("settings-row");
    return row;
  }

  private HBox buildLanguageRow() {
    Label title = new Label("Language");
    title.getStyleClass().add("settings-row-title");

    Label subtitle = new Label("Select your preferred language");
    subtitle.getStyleClass().add("settings-row-subtitle");

    VBox text = new VBox(2, title, subtitle);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    // TODO: Implement language system
    Button enBtn = new Button("EN");
    enBtn.getStyleClass().add("lang-btn-active");

    Button noBtn = new Button("NO");
    noBtn.getStyleClass().add("lang-btn");

    enBtn.setOnAction(e -> {
      enBtn.getStyleClass().setAll("lang-btn-active");
      noBtn.getStyleClass().setAll("lang-btn");
    });

    noBtn.setOnAction(e -> {
      noBtn.getStyleClass().setAll("lang-btn-active");
      enBtn.getStyleClass().setAll("lang-btn");
    });

    HBox langBtns = new HBox(6, enBtn, noBtn);
    langBtns.setAlignment(Pos.CENTER_RIGHT);

    HBox row = new HBox(text, spacer, langBtns);
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().add("settings-row");
    return row;
  }

  private Button createToggle(boolean initialState) {
    Label thumb = new Label();
    thumb.getStyleClass().add("toggle-thumb");

    Button toggle = new Button();
    toggle.setGraphic(thumb);
    toggle.getStyleClass().add(initialState ? "toggle-on" : "toggle-off");
    return toggle;
  }
}