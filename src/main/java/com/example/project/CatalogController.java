// Hanze Simon Zhang
// This is the controller for the catalog stage.
// It will display a randomly selected item to mark 15% discount on them
// and display them in a table

package com.example.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.sql.*;
import java.util.Random;
import java.util.ResourceBundle;

public class CatalogController implements Initializable {
    @FXML
    private TableView<Item> catalogTable; // catalog table
    @FXML
    private TableColumn<Item, Integer> catalogId; // column of id
    @FXML
    private TableColumn<Item, Integer> catalogAvail; // column of available number
    @FXML
    private TableColumn<Item, String> catalogProduct; // column of product type
    @FXML
    private TableColumn<Item, String> catalogSize; // column of product size
    @FXML
    private TableColumn<Item, String> catalogGender; // column of product gender
    @FXML
    private TableColumn<Item, String> catalogColor; // column of product color
    @FXML
    private TableColumn<Item, Integer> catalogOrigPrice; // column of product price
    @FXML
    private TableColumn<Item, Double> catalogDiscPrice; // column of product discounted price

    private ObservableList<Item> discountedItems; // observable list for catalog table

    // Initialize the stage, fill randomly selected items into the table
    // Throws SQLException if there is an error in database interaction
    //
    // @Param URL -> The location used to resolve relative paths for the root object
    //               or null if the location is not known.
    // @Param ResourceBundle -> The resources used to localize the root object,
    //                          or null if the root object was not localized.
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            discountedItems = FXCollections.observableArrayList();
            String query = "SELECT * FROM Items ORDER BY random() LIMIT ?";
            DatabaseManager db = new DatabaseManager();
            db.fillTable(query, discountedItems, true);
            db.close();
            setCatalogFactory();
            catalogTable.setItems(discountedItems);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // set cell factory for all table columns
    private void setCatalogFactory() {
        TableManager.setFactory(catalogProduct, catalogColor, catalogGender,
                catalogOrigPrice, catalogSize, catalogId);
        catalogAvail.setCellValueFactory(new PropertyValueFactory<>("availNum"));
        catalogDiscPrice.setCellValueFactory(new PropertyValueFactory<>("discountedPrice"));
    }
}
