package edu.ntnu.idatt2003.gruppe50.ui.view.components;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Status;
import edu.ntnu.idatt2003.gruppe50.ui.view.components.factory.ButtonFactory;
import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;
import java.util.EnumMap;
import java.util.Map;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.NumberBinding;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

/**
 * Top navigation bar for the application.
 *
 * <p>Contains the logo, navigation buttons for each {@link PageId}, and a
 * player identity section showing the player's name and status.
 */
public class NavBar extends HBox {

  private final Map<PageId, Button> buttons = new EnumMap<>(PageId.class);

  private final Label nameLabel;
  private final Label statusLabel;

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

    /** Called when the user selects "Save &amp; Quit". */
    void onSaveAndQuit();
  }

  /**
   * Constructs the navigation bar and registers listeners for navigation and account actions.
   *
   * @param listener listener to notify when a navigation button is clicked
   * @param accountListener listener to notify when an account menu item is selected
   */
  public NavBar(NavListener listener, NavBar.AccountMenuListener accountListener) {
    this.getStyleClass().add("navbar");

    HBox navLinks = new HBox(6);
    navLinks.getStyleClass().add("nav-links");
    navLinks.setMaxWidth(Region.USE_PREF_SIZE);
    navLinks.setAlignment(Pos.CENTER);
    navLinks.setMinWidth(Region.USE_PREF_SIZE);

    for (PageId id : PageId.values()) {
      Button btn = createNavButton(id, listener);
      buttons.put(id, btn);
      navLinks.getChildren().add(btn);
    }
    nameLabel = new Label("");
    nameLabel.getStyleClass().add("player-name");
    nameLabel.setAlignment(Pos.CENTER_RIGHT);

    statusLabel = new Label("");
    statusLabel.getStyleClass().add("player-status");
    statusLabel.setAlignment(Pos.CENTER_RIGHT);

    VBox playerInfo = new VBox(2, nameLabel, statusLabel);
    playerInfo.setAlignment(Pos.CENTER_RIGHT);

    VBox menuIcon = new VBox(4);
    menuIcon.getStyleClass().add("menu-icon");
    menuIcon.setAlignment(Pos.CENTER);
    for (int i = 0; i < 3; i++) {
      Region line = new Region();
      line.getStyleClass().add("menu-icon-line");
      menuIcon.getChildren().add(line);
    }

    HBox playerSection = new HBox(12, playerInfo, menuIcon);
    playerSection.setAlignment(Pos.CENTER_RIGHT);

    NavDropdown account = new NavDropdown(playerSection);
    account.hideArrow();
    account.setTooltip(new Tooltip("Open menu"));
    account.setAccessibleText("Open menu");
    account.addItem("Settings",     accountListener::onSettings);
    account.addItem("Leaderboard",  accountListener::onLeaderboard);
    account.addSeparator();
    account.addItem("Save & Main Menu",    accountListener::onMainMenu);
    account.addItem("Save & Quit",  accountListener::onSaveAndQuit);

    Logo logo = new Logo();

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
   *
   * @param name player name to display
   * @param status current player status
   */
  public void updatePlayerInfo(String name, Status status) {
    nameLabel.setText(name);
    statusLabel.setText(status.displayName());
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
