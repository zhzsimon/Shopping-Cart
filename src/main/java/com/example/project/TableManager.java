package com.example.project;

import javafx.scene.control.TableColumn;
import javafx.scene.control.cell.PropertyValueFactory;

public class TableManager {
    // Set cell factory for the given columns
    //
    // @param type - table column holding product type
    // @param color - table column holding product color
    // @param gender - table column holding gender that this product is for
    // @param price - table column holding product price
    // @param size - table column holding product size
    // @param id - table column holding product id
    public static void setFactory(TableColumn type, TableColumn color,
                                  TableColumn gender, TableColumn price,
                                  TableColumn size, TableColumn id) {
        type.setCellValueFactory(new PropertyValueFactory<>("type"));
        color.setCellValueFactory(new PropertyValueFactory<>("color"));
        gender.setCellValueFactory(new PropertyValueFactory<>("gender"));
        price.setCellValueFactory(new PropertyValueFactory<>("price"));
        size.setCellValueFactory(new PropertyValueFactory<>("size"));
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
    }
}
