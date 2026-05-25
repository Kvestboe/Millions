package edu.ntnu.idatt2003.gruppe50.ui.view.components;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Status;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;
import java.util.EnumMap;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

/**
 * Top navigation bar for the application.
 *
 * <p>Contains the logo, navigation buttons for each {@link PageId}, and a
 * player identity section showing the player's name, status, and progress
 * toward the next status level.
 */
public class NavBar extends HBox {

  private final Map<PageId, Button> buttons = new EnumMap<>(PageId.class);

  private final Label nameLabel;
  private final Label statusLabel;
  private final ProgressBar levelProgress;
  private final Tooltip levelTip = new Tooltip();

  /**
   * Listener interface for navigation events triggered by the nav bar.
   */
  public interface NavListener {
    /**
     * Called when the user clicks a navigation button.
     *
     * @param item the {@link PageId} of the selected page
     */
    void onNavSelectedItem(PageId item);
  }

  /**
   * Listener for actions triggered from the account dropdown menu.
   */
  public interface AccountMenuListener {

    /** Called when the user selects "Settings". */
    void onSettings();

    /** Called when the user selects "Leaderboard". */
    void onLeaderboard();

    /** Called when the user selects "Main Menu". */
    void onMainMenu();

    /** Called when the user selects "Save & Quit". */
    void onSaveAndQuit();
  }

  /**
   * Constructs the navigation bar and registers the given listener for navigation events.
   *
   * @param listener the listener to notify when a navigation button is clicked
   */
  public NavBar(NavListener listener, NavBar.AccountMenuListener accountListener) {
    this.getStyleClass().add("navbar");

    Logo logo = new Logo();

    HBox navLinks = new HBox(6);
    navLinks.getStyleClass().add("nav-links");
    navLinks.setMaxWidth(Region.USE_PREF_SIZE);
    navLinks.setAlignment(Pos.CENTER);
    navLinks.setMinWidth(Region.USE_PREF_SIZE);
    navLinks.setMaxWidth(Region.USE_PREF_SIZE);   // den du har fra før

    for (PageId id : PageId.values()) {
      Button btn = createNavButton(id, listener);
      buttons.put(id, btn);
      navLinks.getChildren().add(btn);
    }

    levelProgress = new ProgressBar(0);
    levelProgress.getStyleClass().add("level-progress");
    levelProgress.setMinHeight(6);
    levelProgress.setPrefHeight(6);
    levelProgress.setMaxHeight(6);
    levelProgress.prefWidthProperty().bind(
        Bindings.max(90, Bindings.min(170, widthProperty().multiply(0.1))));
    levelTip.setShowDelay(Duration.millis(200));
    levelTip.getStyleClass().add("level-tooltip");
    levelProgress.setTooltip(levelTip);

    nameLabel = new Label("");
    nameLabel.getStyleClass().add("player-name");
    nameLabel.setAlignment(Pos.CENTER_RIGHT);
    nameLabel.prefWidthProperty().bind(levelProgress.prefWidthProperty());

    statusLabel = new Label("");
    statusLabel.getStyleClass().add("player-status");
    statusLabel.setAlignment(Pos.CENTER_RIGHT);
    statusLabel.prefWidthProperty().bind(levelProgress.prefWidthProperty());

    Label progressHelp = new Label("?");
    progressHelp.getStyleClass().add("progress-help");
    Tooltip.install(progressHelp, levelTip);     // samme tooltip som baren

    HBox progressRow = new HBox(4, levelProgress, progressHelp);
    progressRow.setAlignment(Pos.CENTER_RIGHT);

    VBox playerInfo = new VBox(2, nameLabel, statusLabel, progressRow);
    playerInfo.setAlignment(Pos.CENTER_RIGHT);

    Label avatarIcon = new Label("");
    avatarIcon.getStyleClass().add("avatar-circle");

    HBox playerSection = new HBox(10, playerInfo, avatarIcon);
    playerSection.setAlignment(Pos.CENTER_RIGHT);

    NavDropdown account = new NavDropdown(playerSection);
    account.hideArrow();
    account.addItem("Settings",     accountListener::onSettings);
    account.addDisabledItem("Achievements");        // TODO: koble når feature finnes
    account.addItem("Leaderboard",  accountListener::onLeaderboard);
    account.addSeparator();
    account.addItem("Save & Main Menu",    accountListener::onMainMenu);
    account.addItem("Save & Quit",  accountListener::onSaveAndQuit);

    HBox leftZone = new HBox(logo);
    leftZone.setAlignment(Pos.CENTER_LEFT);

    HBox rightZone = new HBox(account);
    rightZone.setAlignment(Pos.CENTER_RIGHT);

    NumberBinding sideWidth =
        Bindings.max(logo.widthProperty(), account.widthProperty());
    leftZone.prefWidthProperty().bind(sideWidth);
    rightZone.prefWidthProperty().bind(sideWidth);

    HBox.setHgrow(leftZone, Priority.ALWAYS);
    HBox.setHgrow(rightZone, Priority.ALWAYS);

    this.getChildren().setAll(leftZone, navLinks, rightZone);

    setActive(PageId.DASHBOARD);
  }

  /**
   * Updates the player's name and status display.
   */
  public void updatePlayerInfo(String name, Status status) {
    nameLabel.setText(name);
    statusLabel.setText(status.displayName());

    levelProgress.getStyleClass().removeIf(c -> c.startsWith("level-progress-"));
    levelProgress.getStyleClass().add("level-progress-" + status.name().toLowerCase());
  }

  /**
   * Sets the level progress bar value, expected in [0.0, 1.0].
   */
  public void setProgress(double progress) {
    levelProgress.setProgress(progress);
  }

  public void setProgressTooltip(String text) {
    levelTip.setText(text);
  }

  /**
   * Shows or hides the level progress bar (hidden at top status).
   */
  public void setProgressVisible(boolean visible) {
    levelProgress.setVisible(visible);
    levelProgress.setManaged(visible);
  }
  /**
   * Marks the given page's navigation button as active and deactivates all others.
   *
   * @param activePage the {@link PageId} of the currently active page
   */
  public void setActive(PageId activePage) {
    buttons.forEach((id, btn) -> {
      btn.getStyleClass().remove("nav-button-active");
      if (id == activePage) {
        btn.getStyleClass().add("nav-button-active");
      }
    });
  }

  /**
   * Creates a styled navigation button for the given page.
   *
   * @param pageId   the page this button navigates to
   * @param listener the listener to notify on click
   * @return the configured {@link Button}
   */
  private Button createNavButton(PageId pageId, NavListener listener) {
    Button btn = ButtonFactory.styled(pageId.getLabel(), "nav-button");
    btn.setOnMousePressed(_ -> {
      setActive(pageId);
      listener.onNavSelectedItem(pageId);
    });
    return btn;
  }
}