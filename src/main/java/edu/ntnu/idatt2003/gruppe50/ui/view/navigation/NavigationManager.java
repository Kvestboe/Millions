package edu.ntnu.idatt2003.gruppe50.ui.view.navigation;

import edu.ntnu.idatt2003.gruppe50.ui.view.pages.Page;
import java.util.EnumMap;
import java.util.Map;
import javafx.scene.layout.StackPane;

public final class NavigationManager {

  private final StackPane contentArea = new StackPane();
  private final Map<PageId, Page> pages;

  public NavigationManager(Map<PageId, Page> pages) {
    this.pages = new EnumMap<>(pages);
  }

  public void navigateTo(PageId id) {
    Page page = pages.get(id);
    if (page == null) {
      throw new IllegalArgumentException("No page registered for: " + id);
    }
    contentArea.getChildren().setAll(page.getView());
  }

  public StackPane getContentArea() {
    return contentArea;
  }

}
