// Hanze Simon Zhang
// This is the controller for the order and payment stage.
// It will ask for user payment information and validate these information to see if
// they are in correct format.
// Once the order is entered, it will give the user an order ID.
// If the customer is not in the customer table, show a new stage
// for the user to enter the customer to the table.

package com.example.project;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.*;

public class PaymentController {
    @FXML
    private TextField first_name; // The customer's first name
    @FXML
    private TextField last_name; // The customer's last name
    @FXML
    private TextField cvv_num; // The customer's card cvv number
    @FXML
    private TextField exp_date; // The customer's cards expiration date
    @FXML
    private TextField card_num; // The customer's card number
    @FXML
    private TextField total_pay; // The total amount that the customer needs to pay
    @FXML
    private TextField payment_address; // The customer's payment address
    @FXML
    private Label error_msg; // The error message
    @FXML
    private Label status_msg; // The status
    @FXML
    private AnchorPane pane; // The pane of the stage

    private String purchase_list; // List of purchase items
    private boolean isBackOrder; // is there any item that is currently not available

    // Open a connection with the database, read user inputted payment information
    // Do a information format validation, and insert into the order table
    // Throws SQL exception if there is an error in database interaction
    // Throws NumberFormatException if the user enters incorrect data format
    // Throws IO Exception if there is an error in opening a new stage
    @FXML
    public void processPayment() {
        try {
            error_msg.setText("");
            status_msg.setText("");

            String firstName = first_name.getText();
            String lastName = last_name.getText();
            String name = firstName + " " + lastName;
            String cvv = cvv_num.getText();
            String expiration = exp_date.getText();
            String card_number = card_num.getText().replaceAll("\\s","");
            String address = payment_address.getText();
            Date date = new Date(System.currentTimeMillis());

            DatabaseManager db = new DatabaseManager();
            validatePayment(firstName, lastName, cvv, card_number, expiration);
            ResultSet rs = db.checkCustomerExist(name);
            if (!rs.next()) {
                displayAddCustomer(name);
            }
            updateItemAmount(db);
            long orderID = db.insertOrder(purchase_list, card_number, expiration, name, cvv,
                    total_pay.getText(), date, address, isBackOrder);

            db.close();
            if (isBackOrder) {
                status_msg.setText("Payment Succeed! Your order ID is " + orderID);
            } else {
                status_msg.setText("You'll not be charged until we have all your items on hand! " +
                        "Your order ID is " + orderID);
            }
        } catch (SQLException e) {
            status_msg.setText("Payment Failed!");
            e.printStackTrace();
        } catch (NumberFormatException | IOException e) {
            status_msg.setText("Error while processing your order!");
            e.printStackTrace();
        }
    }

    // Set the total amount that the customer needs to pay
    public void initTotalPay(String amount) {
        this.total_pay.setText(amount);
    }

    // Initialize the list of purchase items
    public void initPurchaseListID(String purchaseList) {
        this.purchase_list = purchaseList;
    }

    // Initialize the back order status
    public void initBackOrderStatus(boolean isBackOrder) {
        this.isBackOrder = isBackOrder;
    }

    // After the order is inserted into the order table,
    // update corresponding item's available amount in the item table
    // Throws SQLexception if there is an error in database interaction
    // Throws NumberFormatException if the id is not in correct format
    //
    // @Param con -> Database connection
    private void updateItemAmount(DatabaseManager db) throws SQLException, NumberFormatException {
        String[] items = purchase_list.split(", ");
        for (String item : items) {
            String[] idAndAmount = item.split("x");
            db.updateItemAmount(idAndAmount);
        }
    }

    // Validate payment information that the user enters
    // Throws SQLException if there is an error with database interaction
    //
    // @Param firstName - first name of the customer
    // @Param lastName - last name of the customer
    // @Param cvv - cvv of the customer's card
    // @Param card_number - the customer's card number
    // @Param expiration - expiration data on the customer's card
    private void validatePayment(String firstName, String lastName, String cvv,
                                 String card_number, String expiration) throws SQLException {
        String name = firstName + " " + lastName;
        if (name.trim().isEmpty() || cvv.trim().isEmpty() ||
                expiration.trim().isEmpty() || card_number.trim().isEmpty()) {
            error_msg.setText("Please complete the entire form!");
            throw new SQLException();
        }

        if (!firstName.matches("^[a-zA-Z]*$") || !lastName.matches("^[a-zA-Z]*$")) {
            error_msg.setText("Please enter your name correctly!");
            throw new SQLException();
        }

        if (card_number.length() != 16 || !card_number.matches("[0-9]+")) {
            error_msg.setText("Please enter correct card numbers!");
            throw new SQLException();
        }

        String month = expiration.substring(0, 2);
        String year = expiration.substring(3);
        if (expiration.length() != 5 || !expiration.contains("/") ||
                !month.matches("[0-9]+") || !year.matches("[0-9]+")) {
            error_msg.setText("Incorrect format of expiration date!");
            throw new SQLException();
        }

        if (cvv.length() != 3 || !cvv.matches("[0-9]+")) {
            error_msg.setText("Please enter correct cvv numbers!");
            throw new SQLException();
        }
    }


    // Display the stage for the user to enter the customer's information
    // Throws IOException if there is an error while doing so.
    //
    // @Param name - complete name of the customer
    private void displayAddCustomer(String name) throws IOException {
        final Stage addCustomerStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainSalesApplication.class.getResource("add-customer.fxml"));
        addCustomerStage.initModality(Modality.APPLICATION_MODAL);
        addCustomerStage.initOwner((Stage) pane.getScene().getWindow());
        Scene addCustomerScene = new Scene(fxmlLoader.load(), 500, 300);
        AddCustomerController controller = fxmlLoader.getController();
        controller.setName(name);
        addCustomerStage.setScene(addCustomerScene);
        addCustomerStage.show();
    }

}
