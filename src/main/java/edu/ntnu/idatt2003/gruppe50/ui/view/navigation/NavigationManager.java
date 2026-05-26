package edu.ntnu.idatt2003.gruppe50.ui.view.navigation;

import edu.ntnu.idatt2003.gruppe50.ui.view.pages.Page;
import java.util.EnumMap;
import java.util.Map;
import javafx.scene.layout.StackPane;

/** Manages page navigation by swapping the visible content in a shared content area. */
public final class NavigationManager {

  private final StackPane contentArea = new StackPane();
  private final Map<PageId, Page> pages;

  /**
   * Creates a navigation manager with the available pages.
   *
   * @param pages pages registered by page id
   */
  public NavigationManager(Map<PageId, Page> pages) {
    this.pages = new EnumMap<>(pages);
  }

  /**
   * Navigates to a registered page.
   *
   * @param id id of the page to show
   * @throws IllegalArgumentException if no page is registered for the id
   */
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

  /**
   * Returns the content area where pages are displayed.
   *
   * @return shared content area
   */
  public StackPane getContentArea() {
    return contentArea;
  }
}
