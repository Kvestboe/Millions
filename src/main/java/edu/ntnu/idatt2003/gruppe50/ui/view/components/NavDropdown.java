package edu.ntnu.idatt2003.gruppe50.ui.view.components;

import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

/**
 * Dropdown menu styled for use in the navigation bar.
 */
public class NavDropdown extends MenuButton {

  /**
   * Creates a dropdown with text as the trigger.
   *
   * @param text trigger text
   */
  public NavDropdown(String text) {
    setText(text);
    init();
  }

  /**
   * Creates a dropdown with a custom graphic trigger.
   *
   * @param trigger node used as the dropdown trigger
   */
  public NavDropdown(Node trigger) {
    setGraphic(trigger);
    init();
  }

  private void init() {
    getStyleClass().addAll("nav-button", "nav-menu-button");
  }

  /**
   * Adds a clickable item to the dropdown.
   *
   * @param label  item label
   * @param action action to run when the item is selected
   */
  public void addItem(String label, Runnable action) {
    MenuItem item = new MenuItem(label);
    item.setOnAction(e -> action.run());
    getItems().add(item);
  }

  /**
   * Adds a separator line to the dropdown.
   */
  public void addSeparator() {
    getItems().add(new SeparatorMenuItem());
  }

  /**
   * Hides the default dropdown arrow.
   */
  public void hideArrow() {
    getStyleClass().add("nav-dropdown-no-arrow");
  }

  /**
   * Adds a greyed-out, non-clickable item.
   *
   * @param label disabled item label
   */
  public void addDisabledItem(String label) {
    MenuItem item = new MenuItem(label);
    item.setDisable(true);
    getItems().add(item);
  }
}
