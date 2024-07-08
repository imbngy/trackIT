package com.bngy.trackit.models;

import com.bngy.trackit.views.ViewFactory;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.image.Image;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Model {
    private static Model model;
    private ViewFactory viewFactory;
    private final DatabaseDriver databaseDriver;
    //User
    private User user;
    private boolean loginSuccess;
    private List<Account> accounts;
    private ObservableList<Transaction> transactions;


    public Model() {
        this.viewFactory = new ViewFactory();
        this.databaseDriver = new DatabaseDriver(viewFactory);
        //Client Data
        this.loginSuccess = false;
        this.user = new User("","","");
        this.accounts = new ArrayList<>();
        this.transactions = FXCollections.observableArrayList();
    }

    public static synchronized Model getInstance() {
        if (model == null) {
            model = new Model();
        }
        return model;
    }

    public ViewFactory getViewFactory() {
        return viewFactory;
    }

    public DatabaseDriver getDatabaseDriver() {
        return databaseDriver;
    }

    public boolean getLoginSuccess() {
        return loginSuccess;
    }

    public void setLoginSuccess(boolean loginSuccess) {
        this.loginSuccess = loginSuccess;
    }

    public User getUser() {
        return user;
    }

    public void checkLogin(String username, String password) throws Exception {
        ResultSet rs = databaseDriver.getUserData(username, password);
        try{
            if (rs == null) {
                this.loginSuccess = false;
                viewFactory.getCurrentController().showError("Database Error", "Error connecting to the database.");
                return;
            }
            if(rs.isBeforeFirst()){
                this.user.usernameProperty().set(rs.getString("username"));
                this.loginSuccess = true;
            }
        } catch (SQLException e) {
            viewFactory.getCurrentController().showError("Database Error", "Error connecting to the database.");
        }
    }

    public void checkAccounts(String username) throws Exception {
        ResultSet rs = databaseDriver.getAccountsData(username);
        accounts = new ArrayList<>(); // Initialize accounts list

        try {
            while (rs.next()) {
                accounts.add(Account.fromResultSet(rs));
            }
        } catch (SQLException e) {
            viewFactory.getCurrentController().showError("Database Error", "Error connecting to the database.");
        }
    }

    public List<Account> getAccounts() {
        return accounts;
    }

    public Image getMastercard() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/bngy/trackit/imgs/mc-card.png")));
    }

    public Image getVisa() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/bngy/trackit/imgs/vs-card.png")));
    }

    public Image getPaypal() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/bngy/trackit/imgs/pp-card.png")));
    }

    public Image getIn() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/bngy/trackit/imgs/in.png")));
    }

    public Image getInCB() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/bngy/trackit/imgs/in-cb.png")));
    }

    public Image getOut() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/bngy/trackit/imgs/out.png")));
    }

    public Image getOutCB() {
        return new Image(Objects.requireNonNull(getClass().getResourceAsStream("/com/bngy/trackit/imgs/out-cb.png")));
    }

    public Account getAccountByName(String accName) {
        for (Account account : accounts) {
            if (account.getAcc_name().equals(accName)) {
                return account;
            }
        }
        return null;
    }

    public Integer getAccountId(String accName) {
        for (Account account : accounts) {
            if (account.getAcc_name().equals(accName)) {
                return account.getAcc_id();
            }
        }
        return null;
    }

    private void prepareTransactions() throws Exception {
        ObservableList<Transaction> dbTrans = databaseDriver.getTransactions(user.getUsername());
        transactions.addAll(dbTrans);
    }

    public void setTransactions() throws Exception {
        transactions.clear();
        prepareTransactions();
    }

    public ObservableList<Transaction> getTransactions() {
        return transactions;
    }

    public void logout() {
        this.loginSuccess = false;
        this.user = new User("","","");
        this.accounts = new ArrayList<>();
        this.transactions = FXCollections.observableArrayList();
    }

    public String getAccountName(int acc_id) throws SQLException {
        return databaseDriver.getAccountName(acc_id);
    }


}
