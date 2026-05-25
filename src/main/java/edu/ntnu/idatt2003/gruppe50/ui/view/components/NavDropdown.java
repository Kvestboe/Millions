package edu.ntnu.idatt2003.gruppe50.ui.view.components;

import javafx.scene.Node;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;

public class NavDropdown extends MenuButton {

  public NavDropdown(String text) {
    setText(text);
    init();
  }

  public NavDropdown(Node trigger) {
    setGraphic(trigger);
    init();
  }

  private void init() {
    getStyleClass().addAll("nav-button", "nav-menu-button");
  }

  public void addItem(String label, Runnable action) {
    MenuItem item = new MenuItem(label);
    item.setOnAction(e -> action.run());
    getItems().add(item);
  }

  public void addSeparator() {
    getItems().add(new SeparatorMenuItem());
  }

  public void hideArrow() {
    getStyleClass().add("nav-dropdown-no-arrow");
  }

  /** Adds a greyed-out, non-clickable item (placeholder for a future feature). */
  public void addDisabledItem(String label) {
    MenuItem item = new MenuItem(label);
    item.setDisable(true);
    getItems().add(item);
  }
}
