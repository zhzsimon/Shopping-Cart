// Hanze Simon Zhang
// This is the controller for the monthly report page.
// It reads data from the database and create a table, showing top 10 items in last month
// for each type of product.

package com.example.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class MonthlyReportController implements Initializable {
    @FXML
    private TableView<TopItems> monthlyTable; // Table for showing items
    @FXML
    private TableColumn<TopItems, Integer> monthlyId; // product id
    @FXML
    private TableColumn<TopItems, String> monthlyProduct; // product type
    @FXML
    private TableColumn<TopItems, String> monthlySize; // product size
    @FXML
    private TableColumn<TopItems, String> monthlyGender; // product gender
    @FXML
    private TableColumn<TopItems, String> monthlyColor; // product color
    @FXML
    private TableColumn<TopItems, String> monthlyPrice; // product price
    @FXML
    private TableColumn<TopItems, Integer> soldColumn; // number of sold in previous month
    @FXML
    private ChoiceBox<Object> typeSelector; // select product type

    private ObservableList<TopItems> monthlyItems; // Observable List for top 10 items table

    // Initialize observable list for the table and choice box when the page is initialized
    // Throws SQL exception if there is an error in database interaction
    // @Param URL -> The location used to resolve relative paths for the root object
    //               or null if the location is not known.
    // @Param ResourceBundle -> The resources used to localize the root object,
    //                          or null if the root object was not localized.
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
            monthlyItems = FXCollections.observableArrayList();
            typeSelector.setItems(FXCollections.observableArrayList("shirts", "jackets", "trousers"));
            TableManager.setFactory(monthlyProduct, monthlyColor, monthlyGender,
                    monthlyPrice, monthlySize, monthlyId);
            soldColumn.setCellValueFactory(new PropertyValueFactory<>("monthlySold"));
    }

    // Connect with the database to retrieve top sale items for each type in each month
    // Fill the table with these top items.
    // Throws SQLException if there is an error in database processing.
    @FXML
    public void fillTable() {
        monthlyTable.getItems().clear();
        try {
            String userSelectedType = (String) typeSelector.getSelectionModel().getSelectedItem();
            if (userSelectedType == null) {
                return;
            }
            DatabaseManager db = new DatabaseManager();
            db.findTopItems(userSelectedType, monthlyItems);
            monthlyTable.setItems(monthlyItems);
            db.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
