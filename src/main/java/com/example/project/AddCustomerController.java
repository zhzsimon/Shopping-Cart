// Hanze Simon Zhang
// This is the controller for the page of adding new customers
// This page will show up if the customer is not in the database

package com.example.project;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class AddCustomerController implements Initializable {
    @FXML
    private ChoiceBox<Object> selectGender; // The customer's gender
    @FXML
    private TextField age; // The customer's age
    @FXML
    private Label message; // Label for displaying error message

    private String name; // The customer's name

    // Set choices in the select gender box when this stage is first initialized
    //
    // @Param url -> The location used to resolve relative paths for the root object
    //               or null if the location is not known.
    // @Param ResourceBundle -> The resources used to localize the root object,
    //                          or null if the root object was not localized.
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        selectGender.setItems(FXCollections.observableArrayList("Female", "Male"));
    }

    // Connect with the database and insert the new customer into the Customer table
    // Throws SQL Exception if there is an error in the database interaction
    // Throws Number Format Exception the user does not enter number for age
    @FXML
    public void enterBtnClicked() {
        try {
            String gender = (String) selectGender.getSelectionModel().getSelectedItem();
            if (gender == null || Integer.parseInt(age.getText()) <= 0) {
                throw new NumberFormatException();
            }
            DatabaseManager db = new DatabaseManager();
            long orderID = db.insertCustomer(name, gender, Integer.parseInt(age.getText()));
            message.setText("Your customer ID is: " + orderID);
            db.close();
        } catch (SQLException e) {
            message.setText("Error in database processing");
        } catch (NumberFormatException e) {
            message.setText("Please enter field correctly!");
        }

    }

    // Set the customer's name
    public void setName(String name) {
        this.name = name;
    }
}
