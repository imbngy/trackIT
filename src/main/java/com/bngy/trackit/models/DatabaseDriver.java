package com.bngy.trackit.models;

import com.bngy.trackit.views.ViewFactory;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.FileWriter;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class DatabaseDriver {
    private static Connection conn;
    private static ViewFactory viewFactory = null;
    public DatabaseDriver(ViewFactory viewFactory) {
        try {
            this.conn = DriverManager.getConnection("jdbc:sqlite:trackit.db");
        } catch (SQLException e) {
            viewFactory.getCurrentController().showError("Error", "Error connecting to database");
        }
    }

    public static Double getBalanceByName(String accName, String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double balance = 0.0;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("SELECT balance FROM account WHERE acc_name = '" + accName + "' AND owner = '" + username + "'");
            balance = rs.getDouble("balance");
        } catch (SQLException e) {
            throw new Exception("Error getting balance");
        }
        return balance;
    }

    public ResultSet getUserData(String username, String password) throws Exception {
        Statement statement;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("SELECT * FROM user WHERE username ='" + username + "' AND password ='" + password + "'");
        } catch (SQLException e) {
            throw new Exception("Error getting user data");
        }
        return rs;
    }

    public void createUser(String username, String email, String password) throws Exception {
        Statement statement;
        try {
            statement = conn.createStatement();
            username = username.toLowerCase();
            statement.executeUpdate("INSERT INTO user (username, email, password) VALUES ('" + username + "','" + email + "','" + password + "')");
        } catch (SQLException e) {
            throw new Exception("Error creating user");
        }
    }

    public boolean checkUsername(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("SELECT * FROM user WHERE username ='" + username + "'");
        } catch (SQLException e) {
            throw new Exception("Error checking username");
        }

        return rs.isBeforeFirst();
    }

    public ResultSet getAccountsData(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            username = username.toLowerCase();
            rs = statement.executeQuery("SELECT * FROM account where owner = '" + username + "'");
        } catch (SQLException e) {
            throw new Exception("Error getting accounts data");
        }
        return rs;
    }

    public void createAccount(String name, String type, String owner, Double balance) throws Exception {
        Statement statement;
        try {
            statement = conn.createStatement();
            statement.executeUpdate("INSERT INTO account (acc_name, acc_type, owner, balance) VALUES ('" + name + "','" + type + "','" + owner + "'," + balance + ")");
        } catch (SQLException e) {
            throw new Exception("Error creating account");
        }
    }

    public void createTransaction(String external, Integer acc_id, Double amount, String type) throws Exception {
        Statement statement;
        try {
            statement = conn.createStatement();
            LocalDate date = LocalDate.now();
            statement.executeUpdate("INSERT INTO transactions (external, account, amount, dates, type) VALUES ('" + external + "'," + acc_id + "," + amount + ",'" + date + "','" + type + "')");
        } catch (SQLException e) {
            throw new Exception("Error creating transaction");
        }
    }

    public Double getBalance(Integer acc_id) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double balance = 0.0;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("SELECT balance FROM account WHERE id = " + acc_id);
            balance = rs.getDouble("balance");
        } catch (SQLException e) {
            throw new Exception("Error getting balance");
        }
        return balance;
    }

    public void updateBalance(Integer acc_id, Double amount, String type) throws Exception {
        Statement statement;
        Double newBalance = this.getBalance(acc_id);
        if (type.equals("OUT")) {
            amount = amount * -1;
        }
        newBalance += amount;
        try {
            statement = conn.createStatement();
            statement.executeUpdate("UPDATE account SET balance = " + newBalance + " WHERE id = " + acc_id);
        } catch (SQLException e) {
            throw new Exception("Error updating balance");
        }
    }

    public ResultSet getAccountsbyUsername(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("SELECT id FROM account WHERE owner = '" + username + "'");
        } catch (SQLException e) {
            throw new Exception("Error getting accounts");
        }
        return rs;
    }

    public Double getTodayExpenses(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double total = 0.0;

        ResultSet accounts = getAccountsbyUsername(username);
        while (accounts.next()) {
            try {
                statement = conn.createStatement();
                LocalDate date = LocalDate.now();
                rs = statement.executeQuery("SELECT amount FROM transactions WHERE account = " + accounts.getInt("id") + " AND dates = '" + date + "' AND type = 'OUT'");
                while (rs.next()) {
                    total += rs.getDouble("amount");
                }
            } catch (SQLException e) {
                throw new Exception("Error getting today expenses");
            }
        }
        return total;
    }

    public Double getTodayIncome(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double total = 0.0;

        ResultSet accounts = getAccountsbyUsername(username);
        while (accounts.next()) {
            try {
                statement = conn.createStatement();
                LocalDate date = LocalDate.now();
                rs = statement.executeQuery("SELECT amount FROM transactions WHERE account = " + accounts.getInt("id") + " AND dates = '" + date + "' AND type = 'IN'");
                while (rs.next()) {
                    total += rs.getDouble("amount");
                }
            } catch (SQLException e) {
                throw new Exception("Error getting today income");
            }
        }
        return total;
    }

    public Double getLastMonthExpenses(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double total = 0.0;
        ResultSet accounts = getAccountsbyUsername(username);
        while (accounts.next()) {
            try {
                statement = conn.createStatement();
                //get year and month
                LocalDate date = LocalDate.now();
                String month = date.getYear() + "-" + date.getMonthValue() + "%";
                if (date.getMonthValue() < 10) {
                    month = date.getYear() + "-" + "0" + date.getMonthValue() + "%";
                }
                rs = statement.executeQuery("SELECT amount FROM transactions WHERE account = " + accounts.getInt("id") + " AND dates like '" + month + "' AND type = 'OUT'");
                while (rs.next()) {
                    total += rs.getDouble("amount");
                }
            } catch (SQLException e) {
                throw new Exception("Error getting last month expenses");
            }
        }
        return total;
    }

    public Double getLastMonthIncome(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double total = 0.0;
        ResultSet accounts = getAccountsbyUsername(username);
        while (accounts.next()) {
            try {
                statement = conn.createStatement();
                //get year and month
                LocalDate date = LocalDate.now();
                String month = date.getYear() + "-" + date.getMonthValue() + "%";
                if (date.getMonthValue() < 10) {
                    month = date.getYear() + "-" + "0" + date.getMonthValue() + "%";
                }
                rs = statement.executeQuery("SELECT amount FROM transactions WHERE account = " + accounts.getInt("id") + " AND dates like '" + month + "' AND type = 'IN'");
                while (rs.next()) {
                    total += rs.getDouble("amount");
                }
            } catch (SQLException e) {
                throw new Exception("Error getting last month income");
            }
        }
        return total;
    }


    public Double getLast6MonthsExpenses(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double total = 0.0;
        ResultSet accounts = getAccountsbyUsername(username);
        while (accounts.next()) {
            try {
                statement = conn.createStatement();
                //get year and month
                LocalDate date = LocalDate.now();
                for (int i = 0; i < 6; i++) {
                    String month = date.getYear() + "-" + date.getMonthValue() + "%";
                    if (date.getMonthValue() < 10) {
                        month = date.getYear() + "-" + "0" + date.getMonthValue() + "%";
                    }
                    rs = statement.executeQuery("SELECT amount FROM transactions WHERE account = " + accounts.getInt("id") + " AND dates like '" + month + "' AND type = 'OUT'");
                    while (rs.next()) {
                        total += rs.getDouble("amount");
                    }
                    date = date.minusMonths(1);
                }
            } catch (SQLException e) {
                throw new Exception("Error getting last 6 months expenses");
            }
        }
        return total;
    }

    public Double getLast6MonthsIncome(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double total = 0.0;
        ResultSet accounts = getAccountsbyUsername(username);
        while (accounts.next()) {
            try {
                statement = conn.createStatement();
                //get year and month
                LocalDate date = LocalDate.now();
                for (int i = 0; i < 6; i++) {
                    String month = date.getYear() + "-" + date.getMonthValue() + "%";
                    if (date.getMonthValue() < 10) {
                        month = date.getYear() + "-" + "0" + date.getMonthValue() + "%";
                    }
                    rs = statement.executeQuery("SELECT amount FROM transactions WHERE account = " + accounts.getInt("id") + " AND dates like '" + month + "' AND type = 'IN'");
                    while (rs.next()) {
                        total += rs.getDouble("amount");
                    }
                    date = date.minusMonths(1);
                }
            } catch (SQLException e) {
                throw new Exception("Error getting last 6 months income");
            }
        }
        return total;
    }

    public Double getLastYearExpenses(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double total = 0.0;
        ResultSet accounts = getAccountsbyUsername(username);
        while (accounts.next()) {
            try {
                statement = conn.createStatement();
                //get year and month
                LocalDate date = LocalDate.now();
                for (int i = 0; i < 12; i++) {
                    String month = date.getYear() + "-" + date.getMonthValue() + "%";
                    if (date.getMonthValue() < 10) {
                        month = date.getYear() + "-" + "0" + date.getMonthValue() + "%";
                    }
                    rs = statement.executeQuery("SELECT amount FROM transactions WHERE account = " + accounts.getInt("id") + " AND dates like '" + month + "' AND type = 'OUT'");
                    while (rs.next()) {
                        total += rs.getDouble("amount");
                    }
                    date = date.minusMonths(1);
                }
            } catch (SQLException e) {
                throw new Exception("Error getting last year expenses");
            }
        }
        return total;
    }

    public Double getLastYearIncome(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double total = 0.0;
        ResultSet accounts = getAccountsbyUsername(username);
        while (accounts.next()) {
            try {
                statement = conn.createStatement();
                //get year and month
                LocalDate date = LocalDate.now();

                for (int i = 0; i < 12; i++) {
                    String month = date.getYear() + "-" + date.getMonthValue() + "%";
                    if (date.getMonthValue() < 10) {
                        month = date.getYear() + "-" + "0" + date.getMonthValue() + "%";
                    }
                    rs = statement.executeQuery("SELECT amount FROM transactions WHERE account = " + accounts.getInt("id") + " AND dates like '" + month + "' AND type = 'IN'");
                    while (rs.next()) {
                        total += rs.getDouble("amount");
                    }
                    date = date.minusMonths(1);
                }
            } catch (SQLException e) {
                throw new Exception("Error getting last year income");
            }
        }
        return total;
    }


    public Double getTotalExpenses(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double total = 0.0;
        ResultSet accounts = getAccountsbyUsername(username);
        while (accounts.next()) {
            try {
                statement = conn.createStatement();
                rs = statement.executeQuery("SELECT amount FROM transactions WHERE account = " + accounts.getInt("id") + " AND type = 'OUT'");
                while (rs.next()) {
                    total += rs.getDouble("amount");
                }
            } catch (SQLException e) {
                throw new Exception("Error getting total expenses");
            }
        }
        return total;
    }

    public Double getTotalIncome(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        Double total = 0.0;
        ResultSet accounts = getAccountsbyUsername(username);
        while (accounts.next()) {
            try {
                statement = conn.createStatement();
                rs = statement.executeQuery("SELECT amount FROM transactions WHERE account = " + accounts.getInt("id") + " AND type = 'IN'");
                while (rs.next()) {
                    total += rs.getDouble("amount");
                }
            } catch (SQLException e) {
                throw new Exception("Error getting total income");
            }
        }
        return total;
    }

    public ObservableList<Transaction> getTransactions(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        ResultSet accounts = getAccountsbyUsername(username);
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        while (accounts.next()) {
            try {
                statement = conn.createStatement();
                rs = statement.executeQuery("SELECT * FROM transactions WHERE account = " + accounts.getInt("id"));
                while (rs.next()) {
                    String external = rs.getString("external");
                    int acc_id = rs.getInt("account");
                    double amount = rs.getDouble("amount");
                    String date = rs.getString("dates");
                    String type = rs.getString("type");
                    transactions.add(new Transaction(external, acc_id, amount, date, type));
                }
            } catch (SQLException e) {
                throw new SQLException("Error getting transactions");
            }
        }
        return transactions;
    }

    public String getAccountName(int acc_id) throws SQLException {
        Statement statement;
        ResultSet rs = null;
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("SELECT acc_name FROM account WHERE id = " + acc_id);
            return rs.getString("acc_name");
        } catch (SQLException e) {
            throw new SQLException("Error getting account name");
        }
    }

    public void exportCSV(String username) throws Exception {
        ObservableList<Transaction> transactions = getTransactions(username);

        if (transactions.isEmpty()) {
            throw new Exception("No transactions to export");
        }

        try (FileWriter writer = new FileWriter("transactions.csv")) {
            writer.write("External,Account,Amount,Date,Type\n");

            for (Transaction t : transactions) {
                String external = t.getExternal().replace("\"", "\"\"");
                String accountName = getAccountName(t.getAcc_id()).replace("\"", "\"\"");
                String amount = String.valueOf(t.getAmount()).replace(",", ".");
                String date = t.getDate().toString();
                String type = t.getType().replace("\"", "\"\"");

                writer.write(String.format("\"%s\",\"%s\",%s,%s,\"%s\"\n",
                        external, accountName, amount, date, type));
            }

        } catch (IOException e) {
            throw new Exception("Error exporting to CSV");
        }
    }

    public void exportJSON(String username) throws Exception {
        Statement statement;
        ResultSet rs = null;
        ResultSet accounts = getAccountsbyUsername(username);
        JsonArray jsonArray = new JsonArray();
        if (!accounts.isBeforeFirst()) {
            throw new Exception("No transactions to export");
        }
        while(accounts.next()){
            try{
                statement = conn.createStatement();
                rs = statement.executeQuery("SELECT * FROM transactions WHERE account = " + accounts.getInt("id"));

                while(rs.next()) {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("external", rs.getString("external"));
                    jsonObject.addProperty("account", rs.getInt("account"));
                    jsonObject.addProperty("amount", rs.getDouble("amount"));
                    jsonObject.addProperty("date", rs.getString("dates"));
                    jsonObject.addProperty("type", rs.getString("type"));
                    jsonArray.add(jsonObject);
                }
            } catch (SQLException e) {
                throw new Exception("Error exporting to JSON");
            }

            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            if (jsonArray.isEmpty()) {
                throw new Exception("No transactions to export");
            }
            try(FileWriter writer = new FileWriter("transactions.json")){
                gson.toJson(jsonArray, writer);
            } catch (IOException e) {
                throw new Exception("Error exporting to JSON");
            }
        }
    }

    public ObservableList getTransactionsByAccId(Integer accId) throws SQLException {
        Statement statement;
        ResultSet rs = null;
        ObservableList<Transaction> transactions = FXCollections.observableArrayList();
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("SELECT * FROM transactions WHERE account = " + accId);
            while (rs.next()) {
                String external = rs.getString("external");
                int acc_id = rs.getInt("account");
                double amount = rs.getDouble("amount");
                String date = rs.getString("dates");
                String type = rs.getString("type");
                transactions.add(new Transaction(external, acc_id, amount, date, type));
            }
        } catch (SQLException e) {
            throw new SQLException("Error getting transactions by account id");
        }
        return transactions;
    }

    public void exportSingleAccJSON(Integer accId) throws Exception {
        Statement statement;
        ResultSet rs = null;
        JsonArray jsonArray = new JsonArray();
        try {
            statement = conn.createStatement();
            rs = statement.executeQuery("SELECT * FROM transactions WHERE account = " + accId);
            if (!rs.isBeforeFirst()) {
                throw new Exception("No transactions to export");
            }
            while (rs.next()) {
                JsonObject jsonObject = new JsonObject();
                jsonObject.addProperty("external", rs.getString("external"));
                jsonObject.addProperty("account", rs.getInt("account"));
                jsonObject.addProperty("amount", rs.getDouble("amount"));
                jsonObject.addProperty("date", rs.getString("dates"));
                jsonObject.addProperty("type", rs.getString("type"));
                jsonArray.add(jsonObject);
            }
        } catch (SQLException e) {
            throw new SQLException("Error exporting to JSON");
        }

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        String accName = getAccountName(accId);
        try (FileWriter writer = new FileWriter(accName + "Transactions.json")) {
            gson.toJson(jsonArray, writer);
        } catch (IOException e) {
            throw new Exception("Error exporting to JSON");
        }
    }

    public void exportSingleAccCSV(Integer accId) throws Exception {
        ObservableList<Transaction> transactions = getTransactionsByAccId(accId);
        String accName = getAccountName(accId);
        if (transactions.isEmpty()) {
            throw new Exception("No transactions to export");
        }
        try (FileWriter writer = new FileWriter(accName + "Transactions.csv")) {
            writer.write("External,Account,Amount,Date,Type\n");
            for (Transaction t : transactions) {
                String external = t.getExternal().replace("\"", "\"\"");
                String accountName = getAccountName(t.getAcc_id()).replace("\"", "\"\"");
                String amount = String.valueOf(t.getAmount()).replace(",", ".");
                String date = t.getDate().toString();
                String type = t.getType().replace("\"", "\"\"");

                writer.write(String.format("\"%s\",\"%s\",%s,%s,\"%s\"\n",
                        external, accountName, amount, date, type));
            }

        } catch (IOException e) {
            throw new Exception("Error exporting to CSV");
        }
    }
}
