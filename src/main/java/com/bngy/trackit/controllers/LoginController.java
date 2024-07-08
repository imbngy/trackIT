package com.bngy.trackit.controllers;

import com.bngy.trackit.models.Model;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.net.URL;
import java.sql.SQLException;
import java.util.Objects;
import java.util.ResourceBundle;

public class LoginController extends BaseController{

    public TextField username_fld;
    public PasswordField psw_fld;
    public Button login_btn;
    public Button createAcc_btn;
    public Label error_lbl;
    public StackPane windowCtrl_pane;
    public FontAwesomeIconView mnmz_btn;
    public FontAwesomeIconView exit_btn;
    public StackPane errorMsg_pane;
    public FontAwesomeIconView ErrorClose_btn;
    public Label ErrorMsg_lbl;
    public Label errorMsgBody_lbl;
    public Button ErrorReturn_btn;

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

        login_btn.setOnAction(e -> {
            try {
                onLogin();
            } catch (SQLException ex) {
                showError("Database Error", "An error occurred while trying to connect to the database. Please try again later.");
            }
        });

        createAcc_btn.setOnAction(e -> {
            onRegister();
        });

        ErrorClose_btn.setOnMouseClicked(e -> {
            errorMsg_pane.setVisible(false);
        });

        ErrorReturn_btn.setOnAction(e -> {
            errorMsg_pane.setVisible(false);
        });

        ErrorMsg_lbl.setText("Error");
    }

    private void onLogin() throws SQLException {
        //Check login
        try {
            Model.getInstance().checkLogin(username_fld.getText().trim().toLowerCase(), psw_fld.getText());
        } catch (Exception e) {
            showError("Error", "Error connecting to the database.");
        }
        if(Model.getInstance().getLoginSuccess()){
            Stage stage = (Stage) login_btn.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showDashboardWindow();
        } else {
            username_fld.clear();
            psw_fld.clear();
            error_lbl.setText("Invalid username or password");
        }

    }

    private void onRegister() {
        Stage stage = (Stage) createAcc_btn.getScene().getWindow();
        Model.getInstance().getViewFactory().closeStage(stage);
        Model.getInstance().getViewFactory().showRegisterWindow();
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
