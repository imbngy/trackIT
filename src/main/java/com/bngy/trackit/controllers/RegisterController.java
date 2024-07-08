package com.bngy.trackit.controllers;

import com.bngy.trackit.models.Model;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import jakarta.mail.internet.AddressException;
import jakarta.mail.internet.InternetAddress;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class RegisterController extends BaseController{

    public TextField email_fld;
    public TextField username_fld;
    public PasswordField psw_fld;
    public PasswordField checkPsw_fld;
    public Button register_btn;
    public Label registerError_lbl;
    public StackPane windowCtrl_pane;
    public FontAwesomeIconView mnmz_btn;
    public FontAwesomeIconView exit_btn;
    public StackPane errorMsg_pane;
    public FontAwesomeIconView ErrorClose_btn;
    public Label ErrorMsg_lbl;
    public Label errorMsgBody_lbl;
    public Button ErrorReturn_btn;
    public Button back2log_btn;

    double xOffSet, yOffSet;


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadFonts();

        windowCtrl_pane.setOnMousePressed(e -> {
            xOffSet = e.getSceneX();
            yOffSet = e.getSceneY();
        });

        windowCtrl_pane.setOnMouseDragged(e -> {
            Stage stage = (Stage) windowCtrl_pane.getScene().getWindow();
            stage.setX(e.getScreenX() - xOffSet);
            stage.setY(e.getScreenY() - yOffSet);
        });

        exit_btn.setOnMouseClicked(e -> {
            Stage stage = (Stage) exit_btn.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
        });

        mnmz_btn.setOnMouseClicked(e -> {
            Stage stage = (Stage) mnmz_btn.getScene().getWindow();
            stage.setIconified(true);
        });

        register_btn.setOnAction(e -> {
            try {
                onRegister();
            } catch (Exception ex) {
                showError("Error", "An error occurred while trying to connect to the database. Please try again later.");
            }
        });

        back2log_btn.setOnAction(e -> {
            Stage stage = (Stage) back2log_btn.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        });
    }

    private void onRegister() throws Exception {
        if (email_fld.getText().isEmpty() || username_fld.getText().isEmpty() || psw_fld.getText().isEmpty() || checkPsw_fld.getText().isEmpty()) {
            registerError_lbl.setText("Please fill all fields");
            return;
        }
        //Check if username already exists
        if(Model.getInstance().getDatabaseDriver().checkUsername(username_fld.getText().toLowerCase())){
            registerError_lbl.setText("Username is already taken");
            return;
        }
        //Check if email is valid
        InternetAddress emailAddr = new InternetAddress(email_fld.getText());
        try {
            emailAddr.validate();
        } catch (AddressException e) {
            registerError_lbl.setText("Invalid email");
            return;
        }
        //Check if passwords match
        if(psw_fld.getText().equals(checkPsw_fld.getText())){
            Model.getInstance().getDatabaseDriver().createUser(username_fld.getText().toLowerCase(), email_fld.getText(), psw_fld.getText());
            Stage stage = (Stage) register_btn.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        } else {
            psw_fld.clear();
            checkPsw_fld.clear();
            registerError_lbl.setText("Passwords do not match!");
        }
    }

    @Override
    public void showError(String title, String message){
        ErrorMsg_lbl.setText(title);
        errorMsgBody_lbl.setText(message);
        errorMsg_pane.setVisible(true);
        ErrorClose_btn.setOnMouseClicked(e -> {
            errorMsg_pane.setVisible(false);
        });
        ErrorReturn_btn.setOnAction(e -> {
            errorMsg_pane.setVisible(false);
        });
    }
}
