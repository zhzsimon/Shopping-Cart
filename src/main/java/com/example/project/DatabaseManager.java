package com.example.project;

import javafx.collections.ObservableList;

import java.io.IOException;
import java.sql.*;
import java.util.Map;
import java.util.Random;

public class DatabaseManager {
    private final Connection con; // Connection to the database

    public DatabaseManager() throws SQLException {
        this.con = DriverManager.getConnection("jdbc:sqlite:C:/Users/zhang/Desktop/" +
                    "Personal Item/CMU/CMU JAVA/Project/identifier.sqlite");
    }

    // Fill items retrieved from the given sql query to the give observable list
    //
    // @Param query -> sql query
    // @Param list -> observable list of its corresponding table
    // @Param isTopItem -> determine top item or not
    // @Param random -> select random items or not
    public void fillTable(String query, ObservableList<Item> list,
                          boolean random) throws SQLException {
        ResultSet rs;
        if (random) {
            Random rand = new Random();
            int randLimit = rand.nextInt(15);
            PreparedStatement pstmt = con.prepareStatement(query);
            pstmt.setInt(1, randLimit);
            rs = pstmt.executeQuery();
        } else {
            Statement stmt = con.createStatement();
            rs = stmt.executeQuery(query);
        }

        while (rs.next()) {
            int avail = rs.getInt("Available Number");
            String type = rs.getString("Product Type");
            String color = rs.getString("Color");
            String gender = rs.getString("Gender");
            String description = rs.getString("Description");
            String size = rs.getString("Size");
            String availability = rs.getString("Availability");
            int price = rs.getInt("Price");
            String id = rs.getString("ID");

            list.add(new Item(type, color, gender, avail, description, availability, size, price, id));
        }
    }

    // Insert new customer information into the customer table with the given name, gender and age
    //
    // @param name - name of the customer
    // @param gender - gender of the customer
    // @param age - age of the customer
    public long insertCustomer(String name, String gender, int age) throws SQLException {
        String query = "INSERT INTO Customers (name, gender, age) VALUES (?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, name);

        stmt.setString(2, gender);
        stmt.setInt(3, age);
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();

        long id = -1;
        while (rs.next()) {
            id = rs.getLong(1);
        }
        return id;
    }

    // Insert the new item with the given information to the item table
    //
    // @param amount - product available amount
    // @param color - color of the product
    // @param price - price of the product
    // @param size - size of the product
    // @param description - product description
    // @param gender - which gender this product is for
    // @param type - which type of the product is
    // @param availability - is the product back-order currently
    public long insertItem(String amount, String color, String price, String size,
                           String description, String gender, String type,
                           String availability) throws SQLException {
        String query = "INSERT INTO Items ('Available Number', Color, Price, " +
                "Size, Description, Gender, 'Product Type', Availability)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setInt(1, Integer.parseInt(amount));
        stmt.setString(2, color);
        stmt.setInt(3, Integer.parseInt(price));
        stmt.setString(4, size);
        stmt.setString(5, description);
        stmt.setString(6, gender);
        stmt.setString(7, type);
        stmt.setString(8, availability);
        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        long orderID = -1;
        while (rs.next()) {
            orderID = rs.getLong(1);
        }
        return orderID;
    }

    // update item available amount in the database
    //
    // @param amount - the updated amount
    // @param id - id of the item
    public void updateItem(String amount, String id) throws SQLException {
        String query = "UPDATE Items SET 'Available Number' = ? WHERE ID = ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setInt(1, Integer.parseInt(amount));
        stmt.setInt(2, Integer.parseInt(id));
        int result = stmt.executeUpdate();
        if (result <= 0) {
            throw new SQLException();
        }
    }

    // Find top sale items for the given product type
    //
    // @param userSelectedType - user selected product type
    // @param monthlyItems - the observable list for the table holding the top items
    public void findTopItems(String userSelectedType,
                             ObservableList<TopItems> monthlyItems) throws SQLException {
        String query = "SELECT * FROM Items WHERE \"Product Type\" = ? ORDER BY monthly_sold DESC LIMIT 10";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, userSelectedType);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            String type = rs.getString("Product Type");
            String color = rs.getString("Color");
            String gender = rs.getString("Gender");
            String size = rs.getString("Size");
            int price = rs.getInt("Price");
            String id = rs.getString("ID");
            int soldAmount = rs.getInt("monthly_sold");

            monthlyItems.add(new TopItems(type, color, gender, size, price, id, soldAmount));
        }
    }

    // fill bar chart with data retrieved from the database for the given x-axis label
    //
    // @param chartName - the x-axis label
    // @param map - map holding the data
    public void fillBarChart(String chartName, Map<String, int[]> map) throws SQLException {
        ResultSet rs;
        Statement stmt = con.createStatement();
        String query;
        if (chartName.equals("age")) {
            query = "SELECT age, purchased_trouser_nums, purchased_shirts_nums, purchased_jackets_nums FROM Customers";
        } else {
            query = "SELECT gender, purchased_trouser_nums, purchased_shirts_nums, purchased_jackets_nums FROM Customers";
        }
        rs = stmt.executeQuery(query);

        if (chartName.equals("age")) {
            fillAgeMap(map, rs);
        } else {
            fillGenderMap(map, rs);
        }
    }

    // insert order with the given order information to the order table
    //
    // @ param purchase_list - a list of items that the customer purchases
    // @ param card_number - the customer's card number
    // @ expiration - the expiration data of the customer's card
    // @ name - name of the customer
    // @ cvv - cvv number of the customer's card
    // @ total_pay - the total amount of the order
    // @ date - today's date
    // @ address - delivery address
    // @ isBackOrder - is there any back order item in the purchase list
    public long insertOrder(String purchase_list, String card_number, String expiration,
                            String name, String cvv, String total_pay, Date date,
                            String address, boolean isBackOrder) throws SQLException {
        String query = "INSERT INTO Orders (item_list, card_number, expiration_date, " +
                "customer_name, cvv_num, payment_amount, order_date, address, is_paid)" +
                " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        PreparedStatement stmt = con.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stmt.setString(1, purchase_list);
        stmt.setString(2, card_number);
        stmt.setString(3, expiration);
        stmt.setString(4, name);
        stmt.setString(5, cvv);
        stmt.setDouble(6, Double.parseDouble(total_pay));
        stmt.setDate(7, date);
        stmt.setString(8, address);
        stmt.setBoolean(9, isBackOrder);

        stmt.executeUpdate();

        ResultSet rs = stmt.getGeneratedKeys();
        long orderID = -1;
        while (rs.next()) {
            orderID = rs.getLong(1);
        }
        return orderID;
    }

    // Connect with the database to see if the current customer is in the customer table
    // If not, display a new stage for the user to enter the customer's information
    // to the database.
    // Throws SQLException if there is an error with database interaction
    // Throws IOException if there is an error while trying to display new stage
    public ResultSet checkCustomerExist(String name) throws SQLException, IOException {
        String query = "SELECT * FROM Customers WHERE name = ?";
        PreparedStatement stmt = con.prepareStatement(query);
        stmt.setString(1, name);
        ResultSet rs = stmt.executeQuery();

        return rs;
    }

    // update corresponding item's available amount in the item table
    //
    // @param idAndAmount - the id of the item and the new amount
    public void updateItemAmount(String[] idAndAmount) throws SQLException {
        String getCurrentAmount = "SELECT \"Available Number\" FROM Items WHERE ID = ?";
        PreparedStatement stmt = con.prepareStatement(getCurrentAmount);
        stmt.setInt(1, Integer.parseInt(idAndAmount[0]));
        ResultSet rs = stmt.executeQuery();
        int amount = 0;
        while (rs.next()) {
            // System.out.println(rs.toString());
            amount = rs.getInt("Available Number");
        }
        String query = "UPDATE Items SET 'Available Number' = ? WHERE ID = ?";
        stmt = con.prepareStatement(query);
        stmt.setInt(1, amount - Integer.parseInt(idAndAmount[1]));
        stmt.setInt(2, Integer.parseInt(idAndAmount[0]));
        stmt.executeUpdate();
    }

    // Retrieves quarterly sales data from the database
    // Return an int array storing sales information for each product type
    public int[] getQuarterlyData() throws SQLException {
        int[] quarterlySold = new int[4]; // 0 -> shirts, 1 -> trousers, 2-> jackets, 3 -> revenue
        String query  = "SELECT \"Product Type\", quarterly_sold, Price FROM Items";
        Statement stmt = con.createStatement();
        ResultSet rs = stmt.executeQuery(query);
        int revenue = 0;
        while (rs.next()) {
            int sold = rs.getInt("quarterly_sold");
            revenue += sold * rs.getInt("Price");
            if (rs.getString("Product Type").equals("shirts")) {
                quarterlySold[0] += sold;
            } else if (rs.getString("Product Type").equals("trousers")) {
                quarterlySold[1] += sold;
            } else if (rs.getString("Product Type").equals("jackets")) {
                quarterlySold[2] += sold;
            }
        }
        quarterlySold[3] = revenue;
        return quarterlySold;
    }

    // fill the map holding age information
    //
    // @param map - the map holding the age information
    // @param rs - result returned from the sql
    private void fillAgeMap(Map<String, int[]> map, ResultSet rs) throws SQLException {
        while (rs.next()) {
            int age = rs.getInt("age");
            if (age >= 10 && age < 20) {
                map.get("10-20")[0] += rs.getInt("purchased_trouser_nums");
                map.get("10-20")[1] += rs.getInt("purchased_shirts_nums");
                map.get("10-20")[2] += rs.getInt("purchased_jackets_nums");
            } else if (age >= 20 && age < 30) {
                map.get("20-30")[0] += rs.getInt("purchased_trouser_nums");
                map.get("20-30")[1] += rs.getInt("purchased_shirts_nums");
                map.get("20-30")[2] += rs.getInt("purchased_jackets_nums");
            } else if (age >= 30 && age < 40) {
                map.get("30-40")[0] += rs.getInt("purchased_trouser_nums");
                map.get("30-40")[1] += rs.getInt("purchased_shirts_nums");
                map.get("30-40")[2] += rs.getInt("purchased_jackets_nums");
            } else if (age >= 40 && age < 50) {
                map.get("40-50")[0] += rs.getInt("purchased_trouser_nums");
                map.get("40-50")[1] += rs.getInt("purchased_shirts_nums");
                map.get("40-50")[2] += rs.getInt("purchased_jackets_nums");
            } else if (age >= 50 && age < 60) {
                map.get("50-60")[0] += rs.getInt("purchased_trouser_nums");
                map.get("50-60")[1] += rs.getInt("purchased_shirts_nums");
                map.get("50-60")[2] += rs.getInt("purchased_jackets_nums");
            } else if (age >= 60 && age < 70) {
                map.get("60-70")[0] += rs.getInt("purchased_trouser_nums");
                map.get("60-70")[1] += rs.getInt("purchased_shirts_nums");
                map.get("60-70")[2] += rs.getInt("purchased_jackets_nums");
            } else if (age >= 70 && age < 80) {
                map.get("70-80")[0] += rs.getInt("purchased_trouser_nums");
                map.get("70-80")[1] += rs.getInt("purchased_shirts_nums");
                map.get("70-80")[2] += rs.getInt("purchased_jackets_nums");
            }
        }
    }

    // fill the map holding gender information
    //
    // @param map - the map holding the gender information
    // @param rs - result returned from the sql
    private void fillGenderMap(Map<String, int[]> map, ResultSet rs) throws SQLException {
        while (rs.next()) {
            String gender = rs.getString("gender");
            if (gender.equals("Female")) {
                map.get("Female")[0] += rs.getInt("purchased_trouser_nums");
                map.get("Female")[1] += rs.getInt("purchased_shirts_nums");
                map.get("Female")[2] += rs.getInt("purchased_jackets_nums");
            } else if (gender.equals("Male")) {
                map.get("Male")[0] += rs.getInt("purchased_trouser_nums");
                map.get("Male")[1] += rs.getInt("purchased_shirts_nums");
                map.get("Male")[2] += rs.getInt("purchased_jackets_nums");
            }
        }
    }

    // close the database connection
    public void close() throws SQLException {
        con.close();
    }
}
