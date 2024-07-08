package com.bngy.trackit.utils;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class ModeManager {
    private static ModeManager instance;
    private final StringProperty mode;

    private ModeManager() {
        this.mode = new SimpleStringProperty("Light");
    }

    public static ModeManager getInstance() {
        if (instance == null) {
            instance = new ModeManager();
        }
        return instance;
    }

    public String getMode() {
        return mode.get();
    }

    public void setMode(String mode) {
        this.mode.set(mode);
    }

    public StringProperty modeProperty() {
        return mode;
    }
}
