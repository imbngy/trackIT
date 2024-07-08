package com.bngy.trackit.controllers;

import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

import java.util.Objects;

public abstract class BaseController implements Initializable {

    protected StackPane errorMsgPane;
    protected Label errorMsgLbl;
    protected Label errorMsgBodyLbl;

    public static String[] fonts = {
            "/com/bngy/trackit/fonts/Roboto-Regular.ttf",
            "/com/bngy/trackit/fonts/Roboto-Bold.ttf",
            "/com/bngy/trackit/fonts/Roboto-Medium.ttf",
            "/com/bngy/trackit/fonts/Roboto-Light.ttf",
            "/com/bngy/trackit/fonts/Roboto-Thin.ttf",
            "/com/bngy/trackit/fonts/Roboto-Black.ttf"
    };


    public abstract void showError(String title, String message);

    public void loadFonts(){
        for (String font : fonts) {
            Font.loadFont(Objects.requireNonNull(getClass().getResource(font)).toExternalForm(), 12);
        }
    }
}
