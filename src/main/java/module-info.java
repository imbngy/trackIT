module com.bngy.trackit {
    requires javafx.controls;
    requires javafx.fxml;
    requires de.jensd.fx.glyphs.fontawesome;
    requires java.sql;
    requires org.xerial.sqlitejdbc;
    requires jakarta.mail;
    requires com.google.gson;

    opens com.bngy.trackit to javafx.fxml;
    opens com.bngy.trackit.controllers to javafx.fxml;
    opens com.bngy.trackit.controllers.logged to javafx.fxml;
    opens com.bngy.trackit.models to com.google.gson;
    opens com.bngy.trackit.views to javafx.fxml;

    exports com.bngy.trackit;
    exports com.bngy.trackit.controllers;
    exports com.bngy.trackit.controllers.logged;
    exports com.bngy.trackit.models;
    exports com.bngy.trackit.views;
}