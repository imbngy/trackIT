package com.bngy.trackit.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class User {
    private final StringProperty username;
    private final StringProperty password;
    private final StringProperty email;

    public User(String username, String password, String email){
        this.username = new SimpleStringProperty(this, "Username", username);
        this.password = new SimpleStringProperty(this, "Password", password);
        this.email = new SimpleStringProperty(this, "Email", email);
    }

    public StringProperty usernameProperty(){
        return this.username;
    }

    public StringProperty emailProperty(){
        return this.email;
    }

    public String getUsername() {
        return username.get();
    }

    public String getEmail() {
        return email.get();
    }
}
