package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import edu.ntnu.idatt2003.gruppe50.domain.portfolio.Share;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class ShareCardFactory {
  public static VBox createShareCard(Share share) {
    // Create share labels
    Label stockCompanyLabel = new Label(share.getStock().getCompany());
    Label quantityLabel = new Label(share.getQuantity().toString());
    Label purchasePriceLabel = new Label(share.getPurchasePrice().toString());
    Label currentPriceLabel = new Label(share.getStock().getSalesPrice().toString());

    // Create label containers
    VBox quantityContainer = new VBox(new Label("Quantity:"), quantityLabel);
    VBox purchasePriceContainer = new VBox(new Label("Purchase price:"), purchasePriceLabel);
    VBox currentPriceContainer = new VBox(new Label("Current price:", currentPriceLabel));

    HBox informationContainer =
        new HBox(quantityContainer, purchasePriceContainer, currentPriceContainer);

    // Create the share card
    return new VBox(stockCompanyLabel, informationContainer);

  }
}
