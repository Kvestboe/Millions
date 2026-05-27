package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.ui.view.SoundManager;
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

/**
 * Settings page where the player can adjust display and sound options.
 *
 * <p>Includes a fullscreen toggle and master/music/sound effects toggles
 * with a master volume slider. Sound state is delegated to the
 * {@link SoundManager}.
 */
public class SettingsView extends VBox {

  private final Runnable onBack;
  private final Consumer<Boolean> onFullscreen;
  private final boolean initialFullscreen;
  private final SoundManager soundManager;

  private Button masterToggle;
  private Slider masterSlider;
  private Button musicToggle;
  private Button sfxToggle;

  /**
   * Constructs the settings view.
   *
   * @param onBack            action triggered when the player clicks "Back"
   * @param onFullscreen      called with the new fullscreen state when toggled
   * @param initialFullscreen the initial fullscreen state when the view is built
   * @param soundManager      the sound manager controlling master, music and SFX state
   */
  public SettingsView(
      Runnable onBack,
      Consumer<Boolean> onFullscreen,
      boolean initialFullscreen,
      SoundManager soundManager
  ) {
    this.onBack = onBack;
    this.onFullscreen = onFullscreen;
    this.initialFullscreen = initialFullscreen;
    this.soundManager = soundManager;
    build();
  }

  private void build() {
    getStyleClass().add("settings-view");
    setSpacing(8);
    setPadding(new Insets(40));
    setAlignment(Pos.TOP_LEFT);

    HBox musicRow = buildMusicRow();
    HBox sfxRow = buildSfxRow();
    HBox masterRow = buildMasterVolumeRow();

    getChildren().addAll(
        buildHeader(),
        buildSectionLabel("GENERAL"),
        buildFullscreenRow(),
        buildSectionLabel("SOUND"),
        masterRow,
        musicRow,
        sfxRow
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

    Button toggle = createToggle(initialFullscreen);
    if (initialFullscreen) {
      ((Label) toggle.getGraphic()).setTranslateX(10);
    }

    toggle.setOnAction(e -> {
      boolean isOn = toggle.getStyleClass().contains("toggle-on");
      setToggleState(toggle, !isOn);
      onFullscreen.accept(!isOn);
    });

    HBox row = new HBox(text, spacer, toggle);
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().add("settings-row");
    return row;
  }

  private HBox buildMasterVolumeRow() {
    Label title = new Label("Master volume");
    title.getStyleClass().add("settings-row-title");

    Label subtitle = new Label("Overall sound level");
    subtitle.getStyleClass().add("settings-row-subtitle");

    VBox text = new VBox(2, title, subtitle);

    int initialPercent = (int) Math.round(soundManager.getMasterVolume() * 100);

    masterSlider = new Slider(0, 100, initialPercent);
    masterSlider.getStyleClass().add("volume-slider");
    masterSlider.setPrefWidth(400);
    masterSlider.setMaxWidth(400);

    Label valueLabel = new Label(initialPercent + "%");
    valueLabel.getStyleClass().add("settings-row-subtitle");

    masterSlider.valueProperty().addListener((obs, oldVal, newVal) -> {
      int p = newVal.intValue();
      valueLabel.setText(p + "%");
      soundManager.setMasterVolume(p / 100.0);
    });

    HBox sliderRow = new HBox(8, masterSlider, valueLabel);
    HBox.setHgrow(masterSlider, Priority.ALWAYS);

    VBox content = new VBox(10, text, sliderRow);
    HBox.setHgrow(content, Priority.ALWAYS);

    Region spacer = new Region();

    // Master toggle
    masterToggle = createToggle(soundManager.isMasterEnabled());
    if (soundManager.isMasterEnabled()) {
      ((Label) masterToggle.getGraphic()).setTranslateX(10);
    }
    masterToggle.setOnAction(e -> {
      boolean isOn = masterToggle.getStyleClass().contains("toggle-on");
      boolean newState = !isOn;

      setToggleState(masterToggle, newState);
      soundManager.setMasterEnabled(newState);
      masterSlider.setDisable(!newState);

      // Snu music + sfx visuelt og oppdater SoundManager
      setToggleState(musicToggle, newState);
      setToggleState(sfxToggle, newState);
      soundManager.setMusicEnabled(newState);
      soundManager.setSfxEnabled(newState);
    });

    masterSlider.setDisable(!soundManager.isMasterEnabled());

    HBox row = new HBox(content, spacer, masterToggle);
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().add("settings-row");
    return row;
  }

  private HBox buildMusicRow() {
    return buildToggleRow(
        "Music",
        "Play background music",
        soundManager.isMusicEnabled(),
        newState -> {
          soundManager.setMusicEnabled(newState);
          if (newState) {
            ensureMasterOn();
          }
        },
        btn -> musicToggle = btn
    );
  }

  private HBox buildSfxRow() {
    return buildToggleRow(
        "Sound effects",
        "Play UI clicks and game sounds",
        soundManager.isSfxEnabled(),
        newState -> {
          soundManager.setSfxEnabled(newState);
          if (newState) {
            ensureMasterOn();
          }
        },
        btn -> sfxToggle = btn
    );
  }

  private HBox buildToggleRow(String titleText, String subtitleText, boolean initial,
                              Consumer<Boolean> onChange,
                              Consumer<Button> toggleRef) {
    Label title = new Label(titleText);
    title.getStyleClass().add("settings-row-title");

    Label subtitle = new Label(subtitleText);
    subtitle.getStyleClass().add("settings-row-subtitle");

    VBox text = new VBox(2, title, subtitle);

    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    Button toggle = createToggle(initial);
    toggleRef.accept(toggle);
    if (initial) {
      ((Label) toggle.getGraphic()).setTranslateX(10);
    }

    toggle.setOnAction(e -> {
      boolean isOn = toggle.getStyleClass().contains("toggle-on");
      toggle.getStyleClass().remove(isOn ? "toggle-on" : "toggle-off");
      toggle.getStyleClass().add(isOn ? "toggle-off" : "toggle-on");

      TranslateTransition t = new TranslateTransition(
          Duration.millis(150), (Label) toggle.getGraphic());
      t.setToX(isOn ? -10 : 10);
      t.play();

      onChange.accept(!isOn);
    });

    HBox row = new HBox(text, spacer, toggle);
    row.setAlignment(Pos.CENTER_LEFT);
    row.getStyleClass().add("settings-row");
    return row;
  }

  private void setToggleState(Button toggle, boolean on) {
    boolean currentlyOn = toggle.getStyleClass().contains("toggle-on");
    if (currentlyOn == on) {
      return;
    }

    toggle.getStyleClass().remove(currentlyOn ? "toggle-on" : "toggle-off");
    toggle.getStyleClass().add(on ? "toggle-on" : "toggle-off");

    TranslateTransition t = new TranslateTransition(
        Duration.millis(150), (Label) toggle.getGraphic());
    t.setToX(on ? 10 : -10);
    t.play();
  }

  private void ensureMasterOn() {
    if (soundManager.isMasterEnabled()) {
      return;
    }
    setToggleState(masterToggle, true);
    soundManager.setMasterEnabled(true);
    masterSlider.setDisable(false);
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
