// Hanze Simon Zhang
// This is the controller for the quarterly sales report.
// It retrieves data from the database and display sales in bar chart.

package com.example.project;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class QuarterlyController implements Initializable {
    @FXML
    private BarChart<String, Integer> quarterlyGraph; // bar chart for quarterly sales record
    @FXML
    private Label revenueLabel; // label to display total sales revenue in last quarter

    // Connect with the database to obtain quarterly sales data
    // Fill the data into the bar chart
    // Throws SQLException if there is an error in database connection
    //
    // @Param URL -> The location used to resolve relative paths for the root object
    //               or null if the location is not known.
    // @Param ResourceBundle -> The resources used to localize the root object,
    //                          or null if the root object was not localized.
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            DatabaseManager db = new DatabaseManager();
            int[] quarterlySold = db.getQuarterlyData();
            db.close();

            XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
            series1.setName("Quarterly Sales");
            series1.getData().add(new XYChart.Data<String, Integer>("Trousers", quarterlySold[1]));
            series1.getData().add(new XYChart.Data<String, Integer>("Shirts", quarterlySold[0]));
            series1.getData().add(new XYChart.Data<String, Integer>("Jackets", quarterlySold[2]));
            quarterlyGraph.getData().addAll(series1);

            revenueLabel.setText("The total revenue in this quarter is: " + quarterlySold[3]);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
