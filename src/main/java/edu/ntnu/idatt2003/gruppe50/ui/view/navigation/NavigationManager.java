package edu.ntnu.idatt2003.gruppe50.ui.view.navigation;

import edu.ntnu.idatt2003.gruppe50.ui.view.pages.Page;
import javafx.scene.layout.StackPane;

import java.util.*;

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

  /**
   * Shows a page directly, without registering it under a {@link PageId}.
   * Used for dynamic pages such as stock detail views where one instance
   * is created per piece of data.
   *
   * @param page the page to show
   * @throws IllegalArgumentException if {@code page} is null
   */
  public void show(Page page) {
    if (page == null) {
      throw new IllegalArgumentException("Page cannot be null");
    }
    contentArea.getChildren().setAll(page.getView());
  }

  public StackPane getContentArea() {
    return contentArea;
  }

}
