package edu.ntnu.idatt2003.gruppe50.ui.view.components;

import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

/** Simple application logo component. */
public class Logo extends HBox {

  /** Creates the logo component. */
  public Logo() {
    Label appName = new Label("Millions");
    appName.getStyleClass().add("logo-label");

    this.getChildren().add(appName);
    this.setAlignment(Pos.CENTER_LEFT);
  }
}
