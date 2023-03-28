// Hanze Simon Zhang
// This is the controller for the customer report.
// This contains two graphs of customer information.
// One is the graph of gender related to sales
// Another is the graph of age related to sales of each product type.

package com.example.project;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;

import java.net.URL;
import java.sql.*;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

public class CustomerReportController implements Initializable {
    @FXML
    private BarChart<String, Integer> gender_bar; // Gender bar graph
    @FXML
    private BarChart<String, Integer> age_bar; // Age bar graph

    // Initialize the two graphs when the stage is first loaded
    // Retrieves data from the database and fill two charts
    //
    // @Param URL -> The location used to resolve relative paths for the root object
    //               or null if the location is not known.
    // @Param ResourceBundle -> The resources used to localize the root object,
    //                          or null if the root object was not localized.
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            fillGenderData();

            Map<String, int[]> ageData = getAgeData();
            String[] ageRange = {"10-20", "20-30", "30-40",
                    "40-50", "50-60", "60-70", "70-80"};
            for (String range: ageRange) {
                fillAgeData(range, ageData);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Start database connection to retrieve gender data and the number
    // of products they buy for each type
    // Return a map of relating each gender to each number of product type they buy
    // Throws SQLException if there is an error in database interaction
    private Map<String, int[]> getGenderData() throws SQLException {
        DatabaseManager db = new DatabaseManager();
        Map<String, int[]> genderData = new HashMap<>();
        genderData.put("Male", new int[3]); // 0 -> trousers, 1 -> shirts, 2-> jackets
        genderData.put("Female", new int[3]);
        db.fillBarChart("gender", genderData);
        db.close();

        return genderData;
    }

    // Start database connection to retrieve age data and the number
    // of products they buy for each type
    // Return a map of relating each age range to each number of product type they buy
    // Throws SQLException if there is an error in database interaction
    private Map<String, int[]> getAgeData() throws SQLException {
        Map<String, int[]> ageData = new HashMap<>();

        ageData.put("10-20", new int[3]); // 0 -> trousers, 1 -> shirts, 2-> jackets
        ageData.put("20-30", new int[3]);
        ageData.put("30-40", new int[3]);
        ageData.put("40-50", new int[3]);
        ageData.put("50-60", new int[3]);
        ageData.put("60-70", new int[3]);
        ageData.put("70-80", new int[3]);

        DatabaseManager db = new DatabaseManager();
        db.fillBarChart("age", ageData);
        db.close();

        return ageData;
    }

    // Fill gender data into the gender bar chart.
    // Throws SQLException if there is an error in database connection
    private void fillGenderData() throws SQLException {
        Map<String, int[]> genderData = getGenderData();
        XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
        series1.setName("Male");
        series1.getData().add(new XYChart.Data<String, Integer>("Trousers", genderData.get("Male")[0]));
        series1.getData().add(new XYChart.Data<String, Integer>("Shirts", genderData.get("Male")[1]));
        series1.getData().add(new XYChart.Data<String, Integer>("Jackets", genderData.get("Male")[2]));
        gender_bar.getData().addAll(series1);

        XYChart.Series<String, Integer> series2 = new XYChart.Series<>();
        series2.setName("Female");
        series2.getData().add(new XYChart.Data<String, Integer>("Trousers", genderData.get("Female")[0]));
        series2.getData().add(new XYChart.Data<String, Integer>("Shirts", genderData.get("Female")[1]));
        series2.getData().add(new XYChart.Data<String, Integer>("Jackets", genderData.get("Female")[2]));
        gender_bar.getData().addAll(series2);
    }

    // Fill age data into the gender bar chart.
    // Throws SQLException if there is an error in database connection
    private void fillAgeData(String ageRange, Map<String, int[]> ageData) {
        XYChart.Series<String, Integer> series1 = new XYChart.Series<>();
        series1.setName(ageRange);
        series1.getData().add(new XYChart.Data("Trousers", ageData.get(ageRange)[0]));
        series1.getData().add(new XYChart.Data("Shirts", ageData.get(ageRange)[1]));
        series1.getData().add(new XYChart.Data("Jackets", ageData.get(ageRange)[2]));
        age_bar.getData().addAll(series1);
    }
}
