package com.bngy.trackit.views;

import com.bngy.trackit.controllers.BaseController;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;

public class ViewFactory {

    private BorderPane dashboardView;
    private AnchorPane registerView;
    private BaseController currentController;

    public ViewFactory() {
    }

    public void setCurrentController(BaseController controller) {
        this.currentController = controller;
    }

    public BaseController getCurrentController() {
        return currentController;
    }

    private BorderPane getDashboardView() {
        dashboardView = null;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bngy/trackit/fxml/logged/Dashboard.fxml"));
            dashboardView = loader.load();
            setCurrentController(loader.getController());
        } catch (Exception e) {
            this.getCurrentController().showError("Error", "Error loading dashboard view");
            return null;
        }
        return dashboardView;
    }
    public void showLoginWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bngy/trackit/fxml/Login.fxml"));
        createStage(loader);
        setCurrentController(loader.getController());
    }

    public void showDashboardWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bngy/trackit/fxml/logged/Dashboard.fxml"));
        BorderPane dashboardView = null;
        try {
            dashboardView = loader.load();
        } catch (IOException e) {
            this.getCurrentController().showError("Error", "Error loading dashboard view");
            return;
        }

        if (dashboardView != null) {
            Scene scene = new Scene(dashboardView);
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.initStyle(StageStyle.UNDECORATED);
            stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/com/bngy/trackit/imgs/logo.png"))));
            stage.setResizable(false);
            stage.setTitle("TrackIt");
            stage.show();
            scene.getRoot().requestFocus();
            setCurrentController(loader.getController());
        }
    }

    public void showRegisterWindow() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/bngy/trackit/fxml/Register.fxml"));
        createStage(loader);
        setCurrentController(loader.getController());
    }

    private void createStage(FXMLLoader loader){
        Scene scene = null;
        try {
            scene = new Scene(loader.load());
        } catch (Exception e) {
            this.getCurrentController().showError("Error", "Error loading view");
            return;
        }
        Stage stage = new Stage();
        stage.setScene(scene);
        stage.getIcons().add(new Image(String.valueOf(getClass().getResource("/com/bngy/trackit/imgs/logo.png"))));
        stage.setResizable(false);
        stage.initStyle(StageStyle.UNDECORATED);
        stage.setTitle("TrackIt");
        stage.show();
        scene.getRoot().requestFocus();
    }

    public void closeStage(Stage stage) {
        stage.close();
    }

    public void setLightMode() {
        if (dashboardView == null) {
            dashboardView = getDashboardView();
        }
        dashboardView.getStylesheets().clear();
        dashboardView.getStylesheets().add(getClass().getResource("/com/bngy/trackit/css/light.css").toExternalForm());
    }

    public void setDarkMode() {
        if (dashboardView == null) {
            dashboardView = getDashboardView();
        }
        dashboardView.getStylesheets().clear();
        dashboardView.getStylesheets().add(getClass().getResource("/com/bngy/trackit/css/dark.css").toExternalForm());
    }

    public void setColorblindMode() {
        if (dashboardView == null) {
            dashboardView = getDashboardView();
        }
        dashboardView.getStylesheets().clear();
        dashboardView.getStylesheets().add(getClass().getResource("/com/bngy/trackit/css/colorblind.css").toExternalForm());
    }


}