package edu.ntnu.idatt2003.gruppe50.ui.view.pages;

import edu.ntnu.idatt2003.gruppe50.ui.controller.TransactionQueryController;
import javafx.scene.Parent;
import javafx.scene.layout.BorderPane;

public class TransactionsView extends BorderPane implements Page {

  private final TransactionQueryController queryController;

  public TransactionsView(TransactionQueryController queryController) {
    this.queryController = queryController;
  }

  @Override
  public Parent getView() {
    return this;
  }

}
