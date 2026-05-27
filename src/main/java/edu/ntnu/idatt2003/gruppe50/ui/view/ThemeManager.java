package edu.ntnu.idatt2003.gruppe50.ui.view;

import javafx.scene.Scene;

/**
 * Applies CSS themes to JavaFX scenes.
 *
 * <p>Keeps track of the currently selected theme and resolves the matching
 * stylesheet under {@code /css/themes/}. The base stylesheet is always applied
 * alongside the active theme.
 */
public class ThemeManager {

  private static final String DEFAULT_THEME = "default";
  private String currentTheme = DEFAULT_THEME;

  /**
   * Applies the current theme to the given scene by setting its stylesheets.
   *
   * @param scene the scene to style
   */
  public void apply(Scene scene) {
    scene.getStylesheets().setAll(
        resource("/css/base.css"),
        resource("/css/themes/" + currentTheme + "/theme.css")
    );
  }

  /**
   * Sets the active theme used by subsequent {@link #apply(Scene)} calls.
   *
   * @param themeName the id of the theme directory under {@code /css/themes/}
   */
  public void setTheme(String themeName) {
    this.currentTheme = themeName;
  }

  /** Resets the active theme to the default. */
  public void reset() {
    currentTheme = DEFAULT_THEME;
  }

  private String resource(String path) {
    var url = getClass().getResource(path);
    if (url == null) {
      throw new IllegalStateException("CSS resource not found:" + path);
    }
    return url.toExternalForm();
  }
}