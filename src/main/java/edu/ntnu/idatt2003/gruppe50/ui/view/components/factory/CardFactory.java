package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

/**
 * Factory for easy and customizable card creation for different UI views.
 *
 */
public class CardFactory {


  public static VBox createCard(Node... content) {
    VBox card = new VBox();
    card.getStyleClass().add("card");

    card.getChildren().addAll(content);
    return card;
  }

  /**
   * Creates a card with a title label.
   *
   * @param title title of the card
   * @return a {@link VBox} containing the title label
   */
  public static VBox createCard(String title) {
    VBox card = new VBox();
    card.getStyleClass().add("card");

    Label titleLabel = new Label(title);
    titleLabel.getStyleClass().add("card-title");

    card.getChildren().add(titleLabel);
    return card;
  }

  /**
   * Creates a card with a title and a single content node.
   *
   * @param title title of the card
   * @param content the node to place in the card
   * @return a styled {@link VBox} containing the title and content
   */
  public static VBox createCard(String title, Node content) {
    VBox card = createCard(title);
    card.getChildren().add(content);
    return card;
  }

  /**
   * Creates a card with a title and multiple content nodes.
   *
   * @param title title of the card
   * @param content the nodes to place in the card
   * @return a styled {@link VBox} containing the title and all content nodes
   */
  public static VBox createCard(String title, Node... content) {
    VBox card = createCard(title);
    card.getChildren().addAll(content);
    return card;
  }

}
