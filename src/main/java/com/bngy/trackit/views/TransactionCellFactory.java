package com.bngy.trackit.views;

import com.bngy.trackit.controllers.logged.ItemController;
import com.bngy.trackit.models.Transaction;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ListCell;

public class TransactionCellFactory extends ListCell<Transaction> {
    private ViewFactory viewFactory;

    @Override
    protected void updateItem(Transaction item, boolean empty) {
        super.updateItem(item, empty);
        if (empty){
            setText(null);
            setGraphic(null);
        }
        else {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bngy/trackit/fxml/logged/Item.fxml"));
            ItemController controller = new ItemController(item);
            loader.setController(controller);
            setText(null);
            try {
                setGraphic(loader.load());
            } catch (Exception e) {
                // viewfactory error
                viewFactory.getCurrentController().showError("Error", "Error loading item view.");
            }
        }
    }
}
