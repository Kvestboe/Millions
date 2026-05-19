package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

public final class ButtonFactory {

  public static Button primary(String text, Runnable action) {
    Button btn = new Button(text);
    btn.getStyleClass().add("btn-primary");
    btn.setOnAction(_ -> action.run());
    return btn;
  }

  public static Button secondary(String text, Runnable action) {
    Button btn = new Button(text);
    btn.getStyleClass().add("btn-secondary");
    btn.setOnAction(_ -> action.run());
    return btn;
  }

  public static Button secondary(String text) {
    Button btn = new Button(text);
    btn.getStyleClass().add("btn-secondary");
    return btn;
  }

  public static Button iconButton(String icon, String text, Runnable action) {
    Label iconLabel = new Label(icon);
    iconLabel.getStyleClass().add("secondary-button-icon");

    Label textLabel = new Label(text);
    textLabel.getStyleClass().add("secondary-button-text");

    VBox content = new VBox(5, iconLabel, textLabel);
    content.setAlignment(Pos.CENTER);

    Button btn = new Button();
    btn.setGraphic(content);
    btn.setMaxWidth(Double.MAX_VALUE);
    btn.getStyleClass().add("btn-secondary");
    btn.setOnAction(_ -> action.run());
    return btn;
  }

  public static Button danger(String text, Runnable action) {
    Button btn = new Button(text);
    btn.getStyleClass().add("btn-danger");
    btn.setOnAction(_ -> action.run());
    return btn;
  }

  public static Button styled(String text, String cssClass, Runnable action) {
    Button btn = new Button(text);
    btn.getStyleClass().add(cssClass);
    btn.setOnAction(_ -> action.run());
    return btn;
  }

  public static Button styled(String text, String cssClass) {
    Button btn = new Button(text);
    btn.getStyleClass().add(cssClass);
    return btn;
  }

  public static Button toggle(boolean initialState) {
    Label thumb = new Label();
    thumb.getStyleClass().add("toggle-thumb");

    Button btn = new Button();
    btn.setGraphic(thumb);
    btn.getStyleClass().add(initialState ? "toggle-on" : "toggle-off");
    return btn;
  }
}
