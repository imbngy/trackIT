package com.bngy.trackit.models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Transaction {
    private final StringProperty external;
    private final IntegerProperty account;
    private final DoubleProperty amount;
    private final StringProperty date;
    private final StringProperty type;

    public Transaction(String external, int account, Double amount, String date, String type){
        this.external = new SimpleStringProperty(this, "External",external);
        this.account = new SimpleIntegerProperty(this, "Account",account);
        this.amount = new SimpleDoubleProperty(this, "Amount",amount);
        this.date = new SimpleStringProperty(this, "Date",date);
        this.type = new SimpleStringProperty(this, "Type",type);
    }

    public StringProperty externalProperty(){
        return this.external;
    }

    public IntegerProperty accountProperty(){
        return this.account;
    }

    public DoubleProperty amountProperty(){
        return this.amount;
    }

    public StringProperty dateProperty(){
        return this.date;
    }

    public StringProperty typeProperty(){
        return this.type;
    }

    public String getExternal() {
        return external.get();
    }

    public int getAcc_id() {
        return account.get();
    }

    public double getAmount() {
        return amount.get();
    }

    public String getDate() {
        return date.get();
    }

    public String getType() {
        return type.get();
    }
}
