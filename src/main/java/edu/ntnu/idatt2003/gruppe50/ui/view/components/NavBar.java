package edu.ntnu.idatt2003.gruppe50.ui.view.components;

import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;
import java.util.EnumMap;
import java.util.Map;
import javafx.css.PseudoClass;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Top navigation bar for the application.
 *
 * <p>Contains the logo, navigation buttons for each {@link PageId}, and a
 * player identity section showing the player's name, status, and progress
 * toward the next status level.
 */
public class NavBar extends HBox {

  private static final PseudoClass ACTIVE = PseudoClass.getPseudoClass("active-nav");
  private final Map<PageId, Button> buttons = new EnumMap<>(PageId.class);

  private final Label nameLabel;
  private final Label statusLabel;
  private final ProgressBar levelProgress;

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
   * Constructs the navigation bar and registers the given listener for navigation events.
   *
   * @param listener the listener to notify when a navigation button is clicked
   */
  public NavBar(NavListener listener) {
    this.getStyleClass().add("navbar");

    Logo logo = new Logo();
    Region spacer = new Region();
    HBox.setHgrow(spacer, Priority.ALWAYS);

    HBox navLinks = new HBox(4);
    navLinks.getStyleClass().add("nav-links");

    for (PageId id : PageId.values()) {
      Button btn = createNavButton(id, listener);
      buttons.put(id, btn);
      navLinks.getChildren().add(btn);
    }

    nameLabel = new Label("");
    nameLabel.getStyleClass().add("player-name");

    statusLabel = new Label("");
    statusLabel.getStyleClass().add("player-status");

    levelProgress = new ProgressBar(0);
    levelProgress.getStyleClass().add("level-progress");
    levelProgress.setPrefWidth(120);
    levelProgress.setPrefHeight(6);

    VBox playerInfo = new VBox(2, nameLabel, statusLabel, levelProgress);
    playerInfo.setAlignment(Pos.CENTER_RIGHT);

    Label avatarIcon = new Label("🚀");
    avatarIcon.getStyleClass().add("avatar-circle");

    HBox playerSection = new HBox(10, playerInfo, avatarIcon);
    playerSection.setAlignment(Pos.CENTER_RIGHT);

    this.getChildren().addAll(logo, spacer, navLinks, playerSection);

    setActive(PageId.DASHBOARD);
  }

  /**
   * Updates the player identity section with current player data.
   *
   * <p>Also swaps the progress bar CSS class to match the player's current status,
   * allowing per-status color theming via {@code .level-progress-novice},
   * {@code .level-progress-investor}, and {@code .level-progress-speculator}.
   *
   * @param name     the player's name
   * @param status   the player's current status label (e.g. "Novice", "Investor", "Speculator")
   * @param progress progress toward the next status level, between {@code 0.0} and {@code 1.0}
   */
  public void updatePlayerInfo(String name, String status, double progress) {
    nameLabel.setText(name);
    statusLabel.setText(status);
    levelProgress.setProgress(progress);

    levelProgress.getStyleClass().removeIf(c -> c.startsWith("level-progress-"));
    levelProgress.getStyleClass().add("level-progress-" + status.toLowerCase());
  }

  /**
   * Marks the given page's navigation button as active and deactivates all others.
   *
   * @param activePage the {@link PageId} of the currently active page
   */
  public void setActive(PageId activePage) {
    buttons.forEach((id, btn) -> btn.pseudoClassStateChanged(ACTIVE, id == activePage));
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