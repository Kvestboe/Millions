package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class DashBoardView extends BorderPane implements Page {

  public DashBoardView() {
    this.getStyleClass().add("dashboard");

  }

  @Override
  public Parent getView() {
    return this;
  }

}
