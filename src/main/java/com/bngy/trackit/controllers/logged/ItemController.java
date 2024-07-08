package com.bngy.trackit.controllers.logged;

import com.bngy.trackit.models.Model;
import com.bngy.trackit.models.Transaction;
import com.bngy.trackit.utils.ModeManager;
import com.bngy.trackit.views.ViewFactory;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class ItemController implements Initializable {
    public Label externalSource_lbl;
    public Label account_lbl;
    public Label date_lbl;
    public Label amount_lbl;
    public ImageView trans_imgView;

    private ViewFactory viewFactory;
    private final Transaction transaction;

    public ItemController(Transaction transaction) {
        this.transaction = transaction;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        ModeManager.getInstance().modeProperty().addListener((observable, oldValue, newValue) -> updateArrowColor(newValue));
        externalSource_lbl.setText(transaction.externalProperty().get());
        try {
            account_lbl.setText(Model.getInstance().getAccountName(transaction.accountProperty().get()));
        } catch (SQLException e) {
            viewFactory.getCurrentController().showError("Error", e.getMessage());
        }
        date_lbl.setText(transaction.dateProperty().get());
        amount_lbl.setText("€ " + String.valueOf(transaction.amountProperty().get()));
        if (transaction.typeProperty().get().equals("IN")) {
            trans_imgView.setImage(Model.getInstance().getIn());
        } else {
            trans_imgView.setImage(Model.getInstance().getOut());
        }
        updateArrowColor(ModeManager.getInstance().getMode());
    }

    public void updateArrowColor(String mode) {
        if (transaction.typeProperty().get().equals("IN")) {
            switch (mode) {
                case "Light":
                    trans_imgView.setImage(Model.getInstance().getIn());
                    break;
                case "Dark":
                    trans_imgView.setImage(Model.getInstance().getIn());
                    break;
                case "Colorblind":
                    trans_imgView.setImage(Model.getInstance().getInCB());
                    break;
                default:
                    trans_imgView.setImage(Model.getInstance().getIn());
                    break;
            }
        } else {
            switch (mode) {
                case "Light":
                    trans_imgView.setImage(Model.getInstance().getOut());
                    break;
                case "Dark":
                    trans_imgView.setImage(Model.getInstance().getOut());
                    break;
                case "Colorblind":
                    trans_imgView.setImage(Model.getInstance().getOutCB());
                    break;
                default:
                    trans_imgView.setImage(Model.getInstance().getOut());
                    break;
            }
        }
    }

}
