// Hanze Simon Zhang
// This is the controller for the item entry page.
// The page allows the user to update item quantity as well as enter new items.

package com.example.project;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class ItemEntryController implements Initializable {
    @FXML
    private TextField update_id; // id of the item whose quantity needs to be updated
    @FXML
    private TextField update_amount; // updated amount
    @FXML
    private ChoiceBox<Object> select_type; // Type of the product that needs to be entered
    @FXML
    private ChoiceBox<Object> select_gender; // gender for the new product
    @FXML
    private TextField enter_color; // color for the new product
    @FXML
    private TextField enter_size; // size for the new product
    @FXML
    private TextField enter_amount; // available amount for the new product
    @FXML
    private TextField enter_price; // price for the new product
    @FXML
    private Label message; // status message for update
    @FXML
    private Label enter_message; // status message for enter new items
    @FXML
    private TextField enter_description; // item description

    // set choice box contents when the page is first initialized
    // @Param URL -> The location used to resolve relative paths for the root object
    //               or null if the location is not known.
    // @Param ResourceBundle -> The resources used to localize the root object,
    //                          or null if the root object was not localized.
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        select_type.setItems(FXCollections.observableArrayList(null, new Separator(), "shirts",
                "jackets", "trousers"));
        select_gender.setItems(FXCollections.observableArrayList(null, new Separator(), "Male",
                "Female"));
    }

    // Connect to the database and update given item's amount based on its item id.
    // Throws SQLException if there is an error in database connection.
    @FXML
    public void updateItemAmount() {
        try {
            message.setText("");
            String amount = update_amount.getText();
            String id = update_id.getText();

            if (amount.trim().equals("") || id.trim().equals("")) {
                throw new SQLException();
            }
            DatabaseManager db = new DatabaseManager();
            db.updateItem(amount, id);
            message.setText("Success!");
            db.close();
        } catch (SQLException e) {
            message.setText("Please enter correctly!");
            e.printStackTrace();
        }
    }

    // Connect with the database for inserting new items into the item table.
    // Insert new items into the item table with given item information.
    // Throws SQLException if there is an error in database connection
    @FXML
    public void insertNewItem() {
        try {
            enter_message.setText("");
            String amount = enter_amount.getText();
            String color = enter_color.getText();
            String price = enter_price.getText();
            String size = enter_size.getText();
            String description = enter_description.getText();
            String gender = (String) select_gender.getSelectionModel().getSelectedItem();
            String type = (String) select_type.getSelectionModel().getSelectedItem();

            checkEmptyTextField(amount, color, price, size, description, gender, type);
            String availability = "Available";
            if (Integer.parseInt(amount) <= 0) {
                availability = "Back-ordered";
            }

            DatabaseManager db = new DatabaseManager();
            long orderID = db.insertItem(amount, color, price, size,
                    description, gender, type, availability);
            db.close();
            enter_message.setText("Success! Item ID of the new item is: " + orderID);
        } catch (SQLException e) {
            enter_message.setText("please enter fields correctly!");
            e.printStackTrace();
        }
    }

    // Check if all textfields are filled.
    // @Param amount - available amount
    // @Param color - product color
    // @Param price - product price
    // @Param size - available size
    // @Param description - product description
    // @Param gender - which gender the product is for
    // @Param type - product type
    // @Param con - database connection
    private void checkEmptyTextField(String amount, String color, String price,
                                     String size, String description,
                                     String gender, String type) throws SQLException {
        if (amount.trim().equals("") || color.trim().equals("") || price.trim().equals("") ||
                size.trim().equals("") || description.trim().equals("") ||
                gender == null || type == null) {
            throw new SQLException();
        }
    }
}
