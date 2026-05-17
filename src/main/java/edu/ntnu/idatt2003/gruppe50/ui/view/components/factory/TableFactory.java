package edu.ntnu.idatt2003.gruppe50.ui.view.components.factory;

import java.util.List;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

public final class TableFactory {

  private TableFactory() {}

  public static <T> TableView<T> createTable(List<ColumnDefinition<T, ?>> definitionList) {
    TableView<T> table = new TableView<>();

    for (ColumnDefinition<T, ?> definition : definitionList) {
      table.getColumns().add(createColumn(definition));
    }

    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_FLEX_LAST_COLUMN);
    return table;
  }

  private static <T, V> TableColumn<T, V> createColumn(ColumnDefinition<T, V> definition) {
    TableColumn<T, V> column = new TableColumn<>(definition.title());
    column.setCellValueFactory(cell -> definition.getter().apply(cell.getValue()));

    if (definition.styler() != null) {
      column.setCellFactory(col ->  new TableCell<>() {
        @Override
        protected void updateItem(V item, boolean empty) {
          super.updateItem(item, empty);

          if (empty || item== null) {
            setText(null);
            setStyle("");
            return;
          }

          setText(item.toString());
          T rowItem = getTableRow().getItem();
          if (rowItem != null) {
            definition.styler().accept(this, rowItem);
          }
        }
      });
    }

    return column;
  }
}
