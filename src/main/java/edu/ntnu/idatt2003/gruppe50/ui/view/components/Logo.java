package edu.ntnu.idatt2003.gruppe50.ui.view.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class Logo extends HBox {

  public Logo() {
    Label appName = new Label("Millions");
    appName.getStyleClass().add("logo-label");

    this.getStyleClass().add("nav-logo");
    this.getChildren().add(appName);
  }
}
