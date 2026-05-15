package edu.ntnu.idatt2003.gruppe50.ui.view;

import javafx.scene.Scene;

public class ThemeManager {

  private String currentTheme = "teal";

  public void apply(Scene scene) {
    scene.getStylesheets().clear();
    scene.getStylesheets().addAll(
        resource("/css/themes/" + currentTheme + "/theme.css"),
        resource("/css/base.css")
    );
  }

  public void setTheme(String themeName) {
    this.currentTheme = themeName;
  }

  private String resource(String path) {
    var url = getClass().getResource(path);
    if (url == null) {
      throw new IllegalStateException("CSS resource not found:" + path);
    }
    return url.toExternalForm();
  }
}