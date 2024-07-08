package com.bngy.trackit.controllers.logged;

import com.bngy.trackit.controllers.BaseController;
import com.bngy.trackit.models.Account;
import com.bngy.trackit.models.DatabaseDriver;
import com.bngy.trackit.models.Model;
import com.bngy.trackit.utils.ModeManager;
import com.bngy.trackit.views.TransactionCellFactory;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.ResourceBundle;

public class DashboardController extends BaseController {

    public FontAwesomeIconView settings_btn;
    public Label greet_lbl;
    public Label timedate_lbl;
    public Button addAccount_btn;
    public ImageView card2_img;
    public ImageView card1_img;
    public ChoiceBox<String> summaryTime_box;
    public Label expensesSummary_lbl;
    public Label incomeSummary_lbl;
    public StackPane pop_pane;
    public ChoiceBox<String> mode_box;
    public Button logout_btn;
    public Button addTrans_btn;
    public StackPane addTrans_pane;
    public TextField amount_fld;
    public TextField external_fld;
    public ChoiceBox<String> acc_box;
    public ChoiceBox<String> transType_box;
    public Button addFinalTrans_btn;
    public FontAwesomeIconView backTop_btn;
    public Button backBot_btn;
    public StackPane addAcc_pane;
    public FontAwesomeIconView backTop_btn1;
    public ChoiceBox<String> accType_box;
    public TextField accName_fld;
    public TextField accCash_fld;
    public Button addFinalAcc_btn;
    public Button backBot_btn1;
    public Label card2Blnc_lbl;
    public Label card2Name_lbl;
    public Label card1Blnc_lbl;
    public Label card1Name_lbl;
    public ListView trans_listView;
    public StackPane windowCtrl_pane;
    public FontAwesomeIconView mnmz_btn;
    public FontAwesomeIconView exit_btn;
    public AnchorPane background_pane;
    public Button exportCSV_btn;
    public Button exportJSON_btn;
    public StackPane errorMsg_pane;
    public FontAwesomeIconView ErrorClose_btn;
    public Label ErrorMsg_lbl;
    public Label errorMsgBody_lbl;
    public Button ErrorReturn_btn;
    public BorderPane parentdash_pane;
    public StackPane accDetails_pane;
    public Label accDetTitle_lbl;
    public Label accDetName_lbl;
    public Label accDetType_lbl;
    public Label accDetBal_lbl;
    public ImageView accDetailsCard_img;
    public FontAwesomeIconView exitDetails_btn;
    public Button accDetExit_btn;
    public ListView accDetTrns_listView;
    public ChoiceBox<String> accDet_box;
    public Button accDetExportCSV_btn;
    public Button accDetExportJSON_btn;
    public FontAwesomeIconView accDets_btn;


    double xOffset;
    double yOffset;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        loadFonts();

        try {
            initTransactions();
        } catch (Exception e) {
            showError("Error", "Failed to load transactions, please try again later.");
        }
        trans_listView.setItems(Model.getInstance().getTransactions());
        trans_listView.setCellFactory(e -> new TransactionCellFactory());

        windowCtrl_pane.setOnMousePressed(e -> {
            xOffset = (e.getSceneX());
            yOffset = (e.getSceneY());
        });

        windowCtrl_pane.setOnMouseDragged(e -> {
            Stage stage = (Stage) windowCtrl_pane.getScene().getWindow();
            stage.setX(e.getScreenX() - xOffset);
            stage.setY(e.getScreenY() - yOffset);
        });

        mnmz_btn.setOnMouseClicked(e -> {
            Stage stage = (Stage) mnmz_btn.getScene().getWindow();
            stage.setIconified(true);
        });

        exit_btn.setOnMouseClicked(e -> {
            Platform.exit();
        });

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd");
        LocalDateTime now = LocalDateTime.now();
        timedate_lbl.setText(dtf.format(now));

        String username = Model.getInstance().getUser().getUsername();
        username = username.substring(0, 1).toUpperCase() + username.substring(1);
        greet_lbl.setText("Welcome back, " + username + "!");

        summaryTime_box.getItems().addAll("Today", "Last Month", "Last 6 Months", "Last Year", "Total");
        summaryTime_box.setValue("Today");

        mode_box.getItems().addAll("Light", "Dark" , "Colorblind");
        mode_box.setValue("Light");

        mode_box.setOnAction(e -> {
            String mode = mode_box.getValue();
            ModeManager.getInstance().setMode(mode);
            switch (mode) {
                case "Light":
                    setupLightMode();
                    Model.getInstance().getViewFactory().setLightMode();
                    break;
                case "Dark":
                    setupDarkMode();
                    Model.getInstance().getViewFactory().setDarkMode();
                    break;
                case "Colorblind":
                    setupColorblindMode();
                    Model.getInstance().getViewFactory().setColorblindMode();
                    break;
                default:
                    break;
            }

            notifyModeChange(mode);
        });

        transType_box.getItems().addAll("IN", "OUT");

        //Account card1_img setup
        try {
            Model.getInstance().checkAccounts(username);
        } catch (Exception e) {
            showError("Error", e.getMessage());
        }

        List<Account> accounts = Model.getInstance().getAccounts();
        if (accounts != null && !accounts.isEmpty()) {
            setAccounts(accounts);
            setAccBox(accounts);
        }

        logout_btn.setOnAction(e -> {
            Model.getInstance().logout();
            Stage stage = (Stage) logout_btn.getScene().getWindow();
            Model.getInstance().getViewFactory().closeStage(stage);
            Model.getInstance().getViewFactory().showLoginWindow();
        });

        settings_btn.setOnMouseClicked(e -> {
            if (pop_pane.isVisible()) {
                FadeTransition ft = new FadeTransition(Duration.millis(300), pop_pane);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                ft.play();
                ft.setOnFinished(e1 -> {
                    pop_pane.setVisible(false);
                });
            } else {
                FadeTransition ft = new FadeTransition(Duration.millis(300), pop_pane);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
                pop_pane.setVisible(true);
            }
        });

        addTrans_btn.setOnAction(e -> {
            FadeTransition ft = new FadeTransition();
            ft.setNode(addTrans_pane);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
            addTrans_pane.setVisible(true);
        });

        backTop_btn.setOnMouseClicked(e -> {
            onBackClickedTrans();
        });

        backBot_btn.setOnAction(e -> {
            onBackClickedTrans();
        });

        addAccount_btn.setOnAction(e -> {
            FadeTransition ft = new FadeTransition();
            ft.setNode(addAcc_pane);
            ft.setFromValue(0.0);
            ft.setToValue(1.0);
            ft.play();
            addAcc_pane.setVisible(true);
        });

        exitDetails_btn.setOnMouseClicked(e -> {
            FadeTransition ft = new FadeTransition();
            ft.setNode(accDetails_pane);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.play();
            ft.setOnFinished(e1 -> {
                accDetails_pane.setVisible(false);
            });
        });

        accDetExit_btn.setOnAction(e -> {

            FadeTransition ft = new FadeTransition();
            ft.setNode(accDetails_pane);
            ft.setFromValue(1.0);
            ft.setToValue(0.0);
            ft.play();
            ft.setOnFinished(e1 -> {
            accDetails_pane.setVisible(false);
                });
        });

        accType_box.getItems().addAll("Mastercard", "Visa", "Paypal");
        accType_box.setValue("Mastercard");

        backTop_btn1.setOnMouseClicked(e -> {
            onBackClickedAcc();
        });

        backBot_btn1.setOnAction(e -> {
            onBackClickedAcc();
        });

        addFinalAcc_btn.setOnAction(e -> {
            handleAccount();
        });

        addFinalTrans_btn.setOnAction(e -> {
            try {
                handleTransaction();
            } catch (Exception ex) {
                showError("Error", ex.getMessage());
            }
        });

        summaryTime_box.setOnAction(e -> {
            updateSummary();
        });

        exportCSV_btn.setOnAction(e -> {
            try {
                Model.getInstance().getDatabaseDriver().exportCSV(Model.getInstance().getUser().getUsername());
                showError("Success", "Exported to CSV");
            } catch (Exception ex) {
                showError("Error", ex.getMessage());
            }
        });

        exportJSON_btn.setOnAction(e -> {
            try {
                Model.getInstance().getDatabaseDriver().exportJSON(Model.getInstance().getUser().getUsername());
                showError("Success", "Exported to JSON");
            } catch (Exception ex) {
                showError("Error", ex.getMessage());
            }
        });

        amount_fld.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([\\.]\\d{0,2})?")) {
                amount_fld.setText(oldValue);
            }
        });

        accCash_fld.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("\\d{0,7}([\\.]\\d{0,2})?")) {
                accCash_fld.setText(oldValue);
            }
        });

        card1_img.setOnMouseClicked(e -> {
            if (accounts != null && !accounts.isEmpty()) {
                try {
                    showAccDetails(accounts.getFirst());
                } catch (SQLException ex) {
                    showError("Error", "Failed to load account details, please try again later.");
                }
            } else {
                showError("Error", "No accounts found");
            }
        });

        card2_img.setOnMouseClicked(e -> {
            if (accounts != null && accounts.size() > 1) {
                try {
                    showAccDetails(accounts.get(1));
                } catch (SQLException ex) {
                    showError("Error", "Failed to load account details, please try again later.");
                }
            } else {
                showError("Error", "No accounts found");
            }
        });

        for (Account acc : Model.getInstance().getAccounts()) {
            accDet_box.getItems().add(acc.getAcc_name());
        }

        accDets_btn.setOnMouseClicked(e -> {
            if (accDetails_pane.isVisible()) {
                FadeTransition ft = new FadeTransition(Duration.millis(300), accDetails_pane);
                ft.setFromValue(1.0);
                ft.setToValue(0.0);
                ft.play();
                ft.setOnFinished(e1 -> {
                    accDetails_pane.setVisible(false);
                });
            } else {
                FadeTransition ft = new FadeTransition(Duration.millis(300), accDetails_pane);
                ft.setFromValue(0.0);
                ft.setToValue(1.0);
                ft.play();
                accDetails_pane.setVisible(true);
                if (accDet_box.getValue() != null) {
                    try {
                        updateAccDetails(Model.getInstance().getAccountByName(accDet_box.getValue()));
                    } catch (SQLException ex) {
                        showError("Error", "Failed to load account details, please try again later.");
                    }
                } else {
                    try {
                        updateAccDetails(null);
                    } catch (SQLException ex) {
                        throw new RuntimeException(ex);
                    }
                }
            }
        });

        accDet_box.setOnAction(e -> {
            try {
                updateAccDetails(Model.getInstance().getAccountByName(accDet_box.getValue()));
            } catch (SQLException ex) {
                showError("Error", "Failed to load account details, please try again later.");
            }
        });

        accDetExportCSV_btn.setOnAction(e -> {
            try {
                //Export transactions for selectedAcc to CSV
                Model.getInstance().getDatabaseDriver().exportSingleAccCSV(Model.getInstance().getAccountId(accDet_box.getValue()));
                showError("Success", "Exported to CSV");
            } catch (Exception e1) {
                showError("Error", "Export to CSV failed");
            }
        });

        accDetExportJSON_btn.setOnAction(e -> {
            try {
                //Export transactions for selectedAcc to JSON
                Model.getInstance().getDatabaseDriver().exportSingleAccJSON(Model.getInstance().getAccountId(accDet_box.getValue()));
                showError("Success", "Exported to JSON");
            } catch (SQLException e1) {
                showError("Error", "Export to JSON failed");
            } catch (Exception ex) {
                showError("Error", ex.getMessage());
            }
        });

        updateSummary();

        Platform.runLater(() -> {
            setupLightMode();
            Model.getInstance().getViewFactory().setLightMode();
        });
    }

    private void updateAccDetails(Account accountByName) throws SQLException {
        if (accountByName == null) {
            accDetTitle_lbl.setText("No account selected");
            accDetName_lbl.setText("Name: ");
            accDetType_lbl.setText("Type: ");
            accDetBal_lbl.setText("Balance: ");
            return;
        }
        accDet_box.setValue(accountByName.getAcc_name());
        accDetTitle_lbl.setText(accountByName.getAcc_name() + " Details");
        accDetName_lbl.setText("Name: " + accountByName.getAcc_name());
        accDetType_lbl.setText("Type: " + accountByName.getAcc_type());
        accDetBal_lbl.setText("Balance: €" + accountByName.getBalance());
        switch (accountByName.getAcc_type()) {
            case "Mastercard":
                accDetailsCard_img.setImage(Model.getInstance().getMastercard());
                break;
            case "Visa":
                accDetailsCard_img.setImage(Model.getInstance().getVisa());
                break;
            case "PayPal":
                accDetailsCard_img.setImage(Model.getInstance().getPaypal());
                break;
        }

        accDetTrns_listView.setItems(Model.getInstance().getDatabaseDriver().getTransactionsByAccId(accountByName.getAcc_id()));
        accDetTrns_listView.setCellFactory(e -> new TransactionCellFactory());
    }

    private void showAccDetails(Account account) throws SQLException {

        updateAccDetails(account);

        FadeTransition ft = new FadeTransition();
        ft.setNode(accDetails_pane);
        ft.setFromValue(0.0);
        ft.setToValue(1.0);
        ft.play();
        accDetails_pane.setVisible(true);
    }

    private void onBackClickedAcc() {
        FadeTransition ft = new FadeTransition();
        ft.setNode(addAcc_pane);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
        ft.setOnFinished(e -> {
            addAcc_pane.setVisible(false);
        });
    }

    private void onBackClickedTrans() {
        FadeTransition ft = new FadeTransition();
        ft.setNode(addTrans_pane);
        ft.setFromValue(1.0);
        ft.setToValue(0.0);
        ft.play();
        ft.setOnFinished(e -> {
            addTrans_pane.setVisible(false);
        });
    }

    private void updateUI() {
        String username = Model.getInstance().getUser().getUsername();
        try {
            Model.getInstance().checkAccounts(username);
        } catch (Exception e) {
            showError("Error", e.getMessage());
        }

        List<Account> accounts = Model.getInstance().getAccounts();
        if (accounts != null && !accounts.isEmpty()) {
            setAccounts(accounts);
            setAccBox(accounts);
        }
    }

    private void setAccounts(List<Account> accounts) {
        DecimalFormat df = new DecimalFormat("#.00");
        Account firstAccount = accounts.getFirst();
        updateAccCard(firstAccount, card1_img, card1Blnc_lbl, card1Name_lbl);


        if (accounts.size() > 1) {
            Account secondAccount = accounts.get(1);
            updateAccCard(secondAccount, card2_img, card2Blnc_lbl, card2Name_lbl);
        }
    }

    private void updateAccCard(Account account, ImageView cardImg, Label cardBlncLbl, Label cardNameLbl){
        DecimalFormat df = new DecimalFormat("0.00");
        switch (account.getAcc_type()) {
            case "Mastercard":
                cardImg.setImage(Model.getInstance().getMastercard());
                break;
            case "Visa":
                cardImg.setImage(Model.getInstance().getVisa());
                break;
            case "PayPal":
                cardImg.setImage(Model.getInstance().getPaypal());
                break;
        }
        cardNameLbl.setText(account.getAcc_name());
        cardBlncLbl.setText("Balance: €" + df.format(account.getBalance()));
    }

    private void setAccBox (List<Account> accounts) {
        ObservableList<String> accNames = FXCollections.observableArrayList();
        for (Account account : accounts) {
            accNames.add(account.getAcc_name());
        }
        acc_box.setItems(accNames);
        acc_box.setValue(accNames.getFirst());
    }

    private void handleTransaction() throws Exception {
        String accName = acc_box.getValue();
        Double amount = Double.parseDouble(amount_fld.getText());
        String external = external_fld.getText();
        String type = transType_box.getValue();
        Integer acc_id = Model.getInstance().getAccountId(accName);

        if (accName.isEmpty() || amount == 0) {
            showError("Error", "Please fill in all fields");
            return;
        }

        if (amount > DatabaseDriver.getBalanceByName(accName, Model.getInstance().getUser().getUsername()) && type.equals("OUT")) {
            showError("Error", "Insufficient funds on " + accName + ", total balance is €" + DatabaseDriver.getBalanceByName(accName, Model.getInstance().getUser().getUsername()));
            return;
        }

        try {
            Model.getInstance().getDatabaseDriver().createTransaction(external, acc_id, amount, type);
            Model.getInstance().getDatabaseDriver().updateBalance(acc_id, amount, type);
            updateTransactions();
        } catch (Exception e) {
            showError("Error", e.getMessage());
        }

        onBackClickedTrans();
        updateSummary();
        updateUI();
    }

    private void handleAccount(){
        String accType = accType_box.getValue();
        String accName = accName_fld.getText();
        Double accCash = Double.parseDouble(accCash_fld.getText());
        if (accName.isEmpty()) {
            return;
        }
        try {
            Model.getInstance().getDatabaseDriver().createAccount(accName, accType, Model.getInstance().getUser().getUsername().toLowerCase(),accCash);
        } catch (Exception e) {
            showError("Error", e.getMessage());
        }

        onBackClickedAcc();
        updateUI();
    }

    private void updateSummary() {

        String time = summaryTime_box.getValue();
        switch (time) {
            case "Today":
                if (!Model.getInstance().getAccounts().isEmpty()) {
                    try {
                        Double expenses = Model.getInstance().getDatabaseDriver().getTodayExpenses(Model.getInstance().getUser().getUsername());
                        Double income = Model.getInstance().getDatabaseDriver().getTodayIncome(Model.getInstance().getUser().getUsername());
                        updateSummaryLbls(expenses, income);
                    } catch (Exception e) {
                        showError("Error", e.getMessage());
                    }
                }
                break;
            case "Last Month":
                try {
                    Double expenses = Model.getInstance().getDatabaseDriver().getLastMonthExpenses(Model.getInstance().getUser().getUsername());
                    Double income = Model.getInstance().getDatabaseDriver().getLastMonthIncome(Model.getInstance().getUser().getUsername());
                    updateSummaryLbls(expenses, income);
                } catch (Exception e) {
                    showError("Error", e.getMessage());
                }
                break;
            case "Last 6 Months":
                try {
                    Double expenses = Model.getInstance().getDatabaseDriver().getLast6MonthsExpenses(Model.getInstance().getUser().getUsername());
                    Double income = Model.getInstance().getDatabaseDriver().getLast6MonthsIncome(Model.getInstance().getUser().getUsername());
                    updateSummaryLbls(expenses, income);
                } catch (Exception e) {
                    showError("Error", e.getMessage());
                }
                break;
            case "Last Year":
                try {
                    Double expenses = Model.getInstance().getDatabaseDriver().getLastYearExpenses(Model.getInstance().getUser().getUsername());
                    Double income = Model.getInstance().getDatabaseDriver().getLastYearIncome(Model.getInstance().getUser().getUsername());
                    updateSummaryLbls(expenses, income);
                } catch (Exception e) {
                    showError("Error", e.getMessage());
                }
                break;
            case "Total":
                try {
                    Double expenses = Model.getInstance().getDatabaseDriver().getTotalExpenses(Model.getInstance().getUser().getUsername());
                    Double income = Model.getInstance().getDatabaseDriver().getTotalIncome(Model.getInstance().getUser().getUsername());
                    updateSummaryLbls(expenses, income);
                }
                catch (Exception e) {
                    showError("Error", e.getMessage());
                }
                break;
            default:
                break;
        }

    }

    private void updateSummaryLbls(Double expenses, Double income){
        DecimalFormat df = new DecimalFormat("0.00");
        expensesSummary_lbl.setText("€" + df.format(expenses));
        incomeSummary_lbl.setText("€" + df.format(income));
    }

    private void initTransactions() throws Exception {
        if(Model.getInstance().getTransactions().isEmpty()){
            Model.getInstance().setTransactions();
        }
    }

    private void updateTransactions() throws Exception {

        Model.getInstance().setTransactions();
    }

    private void setBackgroundColorLight() {
        background_pane.setStyle("-fx-background-color: #fafafc");
    }

    private void setBackgroundColorDark() {
        background_pane.setStyle("-fx-background-color: #494949");
    }

    private void setBackgroundColorColorblind() {
        background_pane.setStyle("-fx-background-color: #f0f0f0");
    }

    private void setAccDetBackgroundLight() {
        accDetails_pane.setStyle("""
                -fx-background-color: #fafafc;
                -fx-border-color: #f0f0f0;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 0);""");
    }

    private void setAccDetBackgroundDark() {
        accDetails_pane.setStyle("""
                -fx-background-color: #2e2e2e;
                -fx-border-color: #4a4a4a;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 0);""");
    }

    private void setAccDetBackgroundColorblind() {
        accDetails_pane.setStyle("""
                -fx-background-color: #f0f0f0;
                -fx-border-color: #f0f0f0;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 0);""");
    }

    private void setSettingsBackgroundLight() {
        pop_pane.setStyle("""
                -fx-background-color: #fafafc;
                    -fx-border-color: #f0f0f0;
                    -fx-border-width: 1px;
                    -fx-border-radius: 30px;
                    -fx-background-radius: 30px;
                    -fx-padding: 10px;
                    -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 0);""");
    }

    private void setSettingsBackgroundDark() {
        pop_pane.setStyle("""
                -fx-background-color: #2e2e2e;
                -fx-border-color: #4a4a4a;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 0);""");
    }

    private void setSettingsBackgroundColorblind() {
        pop_pane.setStyle("""
                -fx-background-color: #f0f0f0;
                -fx-border-color: #f0f0f0;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 0);""");
    }

    private void setTransBackgroundLight() {
        addTrans_pane.setStyle("""
                -fx-background-color: #fafafc;
                -fx-border-color: #f0f0f0;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 0);""");
    }

    private void setTransBackgroundDark() {
        addTrans_pane.setStyle("""
                -fx-background-color: #2e2e2e;
                -fx-border-color: #4a4a4a;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 0);""");
    }

    private void setTransBackgroundColorblind() {
        addTrans_pane.setStyle("""
                -fx-background-color: #f0f0f0;
                -fx-border-color: #f0f0f0;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 0);""");
    }

    private void setAccBackgroundLight() {
        addAcc_pane.setStyle("""
                -fx-background-color: #fafafc;
                -fx-border-color: #f0f0f0;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 0);""");
    }

    private void setAccBackgroundDark() {
        addAcc_pane.setStyle("""
                -fx-background-color: #2e2e2e;
                -fx-border-color: #4a4a4a;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 0);""");
    }

    private void setAccBackgroundColorblind() {
        addAcc_pane.setStyle("""
                -fx-background-color: #f0f0f0;
                -fx-border-color: #f0f0f0;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 0);""");
    }

    private void setErrorBackgroundLight(){
        errorMsg_pane.setStyle("""
                -fx-background-color: #fafafc;
                -fx-border-color: #f0f0f0;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 0);""");
    }

    private void setErrorBackgroundDark(){
        errorMsg_pane.setStyle("""
                -fx-background-color: #2e2e2e;
                -fx-border-color: #4a4a4a;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.3), 10, 0, 0, 0);""");
    }

    private void setErrorBackgroundColorblind(){
        errorMsg_pane.setStyle("""
                -fx-background-color: #f0f0f0;
                -fx-border-color: #f0f0f0;
                -fx-border-width: 1px;
                -fx-border-radius: 30px;
                -fx-background-radius: 30px;
                -fx-padding: 10px;
                -fx-effect: dropshadow(three-pass-box, rgba(0, 0, 0, 0.1), 10, 0, 0, 0);""");
    }

    private void setupLightMode() {
        parentdash_pane.getStylesheets().clear();
        parentdash_pane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/bngy/trackit/css/light.css")).toExternalForm());
        setBackgroundColorLight();
        setSettingsBackgroundLight();
        setTransBackgroundLight();
        setAccBackgroundLight();
        setErrorBackgroundLight();
        setAccDetBackgroundLight();
    }

    private void setupDarkMode() {
        parentdash_pane.getStylesheets().clear();
        parentdash_pane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/bngy/trackit/css/dark.css")).toExternalForm());
        setBackgroundColorDark();
        setSettingsBackgroundDark();
        setTransBackgroundDark();
        setAccBackgroundDark();
        setErrorBackgroundDark();
        setAccDetBackgroundDark();
    }

    private void setupColorblindMode() {
        parentdash_pane.getStylesheets().clear();
        parentdash_pane.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/com/bngy/trackit/css/colorblind.css")).toExternalForm());
        setBackgroundColorColorblind();
        setSettingsBackgroundColorblind();
        setTransBackgroundColorblind();
        setAccBackgroundColorblind();
        setErrorBackgroundColorblind();
        setAccDetBackgroundColorblind();
    }

    private void notifyModeChange(String mode) {
        ModeManager.getInstance().setMode(mode);
    }

    @Override
    public void showError(String title,String message){
        errorMsg_pane.setVisible(true);
        errorMsgBody_lbl.setText(message);
        ErrorMsg_lbl.setText(title);
        ErrorClose_btn.setOnMouseClicked(e1 -> {
            errorMsg_pane.setVisible(false);
        });
        ErrorReturn_btn.setOnAction(e1 -> {
            errorMsg_pane.setVisible(false);
        });
    }
}
