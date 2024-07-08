package com.bngy.trackit.models;

import javafx.beans.property.*;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    private final IntegerProperty acc_id;
    private final StringProperty owner;
    private final StringProperty acc_name;
    private final StringProperty acc_type;
    private final DoubleProperty balance;

    public Account(Integer accId, String owner, String name, String type, Double balance){
        this.acc_id = new SimpleIntegerProperty(this, "Id", accId);
        this.owner = new SimpleStringProperty(this, "Owner", owner);
        this.acc_name = new SimpleStringProperty(this, "Name", name);
        this.acc_type = new SimpleStringProperty(this, "Type", type);
        this.balance = new SimpleDoubleProperty(this, "Balance", balance);
    }

    public static Account fromResultSet(ResultSet rs) throws SQLException {
        Integer acc_id = rs.getInt("id");
        String owner = rs.getString("owner");
        String acc_name = rs.getString("acc_name");
        String type = rs.getString("acc_type");
        Double balance = rs.getDouble("balance");
        return new Account(acc_id, owner, acc_name, type, balance);
    }

    public StringProperty ownerProperty(){
        return this.owner;
    }

    public StringProperty acc_nameProperty(){
        return this.acc_name;
    }

    public StringProperty acc_typeProperty(){
        return this.acc_type;
    }

    public DoubleProperty balanceProperty(){
        return this.balance;
    }

    public String getOwner() {
        return owner.get();
    }

    public String getAcc_name() {
        return acc_name.get();
    }

    public String getAcc_type() {
        return acc_type.get();
    }

    public Double getBalance() {
        return balance.get();
    }

    public Integer getAcc_id() {
        return acc_id.get();
    }


}
