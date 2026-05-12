package edu.ntnu.idatt2003.gruppe50.ui.view.components;

import edu.ntnu.idatt2003.gruppe50.ui.view.navigation.PageId;
import java.util.EnumMap;
import java.util.Map;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;

public class NavBar extends HBox {

  private static final PseudoClass ACTIVE = PseudoClass.getPseudoClass("active-nav");
  private final Map<PageId, Button> buttons = new EnumMap<>(PageId.class);

  /**
   * Inner interface to listen for nav-bar clicks.
   */
  public interface NavListener {
    void onNavSelectedItem(PageId item);
  }

  // I don't know how the wireframe looks yet but this is a good starter layout.
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

    this.getChildren().addAll(logo, spacer, navLinks);

    setActive(PageId.DASHBOARD);
  }

  public void setActive(PageId activePage) {
    buttons.forEach((id, btn) -> {
      boolean shouldBeActive = id == activePage;
      // if the shouldBeActive is true the pseudo class is set to active, or else false
      btn.pseudoClassStateChanged(ACTIVE, shouldBeActive);
    });
  }

  private Button createNavButton(PageId pageId, NavListener listener) {
    Button btn = new Button(pageId.getLabel());
    btn.getStyleClass().add("nav-button");

    btn.setOnMousePressed(_ -> {
      setActive(pageId);
      listener.onNavSelectedItem(pageId);
    });

    return btn;
  }
}
