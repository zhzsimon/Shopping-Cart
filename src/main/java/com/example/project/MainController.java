// Hanze Simon Zhang
// This is the main controller of the whole Adelaide Sales System.
// It allows a sales person to enter new items, orders and customers into database.
// It also allows one to check availability of an item.
// It is able to generate customer demographic report, quarterly sales report,
// monthly sales report, and display all items with its available quantity.

package com.example.project;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.converter.IntegerStringConverter;

import java.io.IOException;
import java.sql.*;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private TableView<Item> table; // Table displaying all items
    @FXML
    private TableView<Item> cart; // Table displaying items that have been added to cart
    @FXML
    private TableColumn<Item, String> product_type; // item table column product type
    @FXML
    private TableColumn<Item, Integer> product_id; // item table column product id
    @FXML
    private TableColumn<Item, Integer> cart_id; // cart table column product type
    @FXML
    private TableColumn<Item, String> cart_product; // cart table column product
    @FXML
    private TableColumn<Item, String> product_color; // item table column product color
    @FXML
    private TableColumn<Item, String> cart_color; // cart table column product color
    @FXML
    private TableColumn<Item, String> product_gender; // item table column product gender
    @FXML
    private TableColumn<Item, String> cart_gender; // cart table column product color
    @FXML
    private TableColumn<Item, Integer> available_number; // item table column product available number
    @FXML
    private TableColumn<Item, Integer> cart_amount; // cart table column product available number
    @FXML
    private TableColumn<Item, String> product_size; // item table column product size
    @FXML
    private TableColumn<Item, String> cart_size; // cart table column product size
    @FXML
    private TableColumn<Item, String> product_availability; // item table column product availability
    @FXML
    private TableColumn<Item, Integer> product_price; // item table column product price
    @FXML
    private TableColumn<Item, Integer> cart_price; // cart table column product available number
    @FXML
    private TableColumn<Item, String> product_description; // item table column product description
    @FXML
    private TextField select_id; // item id user enters
    @FXML
    private TextField select_gender; // gender user enters
    @FXML
    private TextField select_color; // color user enters
    @FXML
    private TextField select_size; // size user enters
    @FXML
    private TextField total_cost; // total cost of all items in cart
    @FXML
    private ChoiceBox<Object> select_type; // to let users select product type
    @FXML
    private VBox stage_vbox; // The container of the page
    @FXML
    private Label message;

    private ObservableList<Item> items; // store items in item table
    private ObservableList<Item> cart_items; // store items in the cart table
    private ObservableList<Item> trackItem; // track item for modifying quantity
    private double total; // total price

    // Set up the table when the stage is loaded.
    // Throws SQL exception if there is an error in database interaction
    // @Param URL -> The location used to resolve relative paths for the root object
    //               or null if the location is not known.
    // @Param ResourceBundle -> The resources used to localize the root object,
    //                          or null if the root object was not localized.
    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            /*Connection con = DriverManager.getConnection("jdbc:sqlite:C:/Users/zhang/Desktop/" +
                    "Personal Item/CMU/CMU JAVA/Project/identifier.sqlite");
            Statement stmt = con.createStatement();*/

            // ResultSet rs = stmt.executeQuery(query);
            items = FXCollections.observableArrayList();
            cart_items = FXCollections.observableArrayList();
            trackItem = FXCollections.observableArrayList();

            DatabaseManager db = new DatabaseManager();
            String query = "SELECT * FROM Items";
            db.fillTable(query, items, false);
            // fillTable(con, rs);
            db.close();
            setItemFactory();
            setCartFactory();

            table.setItems(items);
            cart.setItems(cart_items);
            cart.setEditable(true);
            total_cost.setText(String.valueOf(total));

            // con.close();

            select_type.setItems(FXCollections.observableArrayList(null, new Separator(), "shirts",
                    "jackets", "trousers"));
        } catch (SQLException e) {
            message.setText("Error while accessing database");
            e.printStackTrace();
        }
    }

    // When the search item is clicked, fill the item table user selected items
    // Throws SQLException if there is an error in database interaction
    @FXML
    public void searchClicked() {
        try {
            String userSelectedType = (String) select_type.getSelectionModel().getSelectedItem();
            String userSelectedId = select_id.getText();
            String userSelectedGender = select_gender.getText();
            String userSelectedColor = select_color.getText();
            String userSelectedSize = select_size.getText();
            String query = "SELECT * FROM Items";
            String selection = processSearchOptions(userSelectedType, userSelectedGender,
                    userSelectedColor, userSelectedSize, userSelectedId);
            if (!selection.equals(" WHERE")) {
                query += selection;
            }
            table.getItems().clear();
            DatabaseManager db = new DatabaseManager();
            db.fillTable(query, items, false);
            db.close();
        } catch(SQLException e) {
            e.printStackTrace();
        }
    }

    // Add user selected item to cart and add up total price
    @FXML
    public void addToCart() {
        message.setText("");
        Item selectedItem = table.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            message.setText("Please select an item to add!");
            return;
        }
        trackItem.add(selectedItem);
        cart_items.add(new Item(selectedItem.getType(), selectedItem.getColor(), selectedItem.getGender(),
                1, selectedItem.getDescription(), selectedItem.getAvailability(), selectedItem.getSize(),
                selectedItem.getPrice(), selectedItem.getId()));
        total += selectedItem.getPrice();
        total_cost.setText(String.valueOf(total));
    }

    // Remove item from the cart and decrease the total price
    @FXML
    public void removeFromCart() {
        Item selectedItem = cart.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            message.setText("Please select an item to remove!");
            return;
        }
        trackItem.remove(cart.getSelectionModel().getSelectedIndex());
        cart_items.remove(selectedItem);
        total -= selectedItem.getPrice();
        total_cost.setText(String.valueOf(total));
    }

    // When the checkout button is clicked, display the payment page
    @FXML
    public void checkoutClicked() throws IOException {
        message.setText("");
        if (total == 0) {
            message.setText("Please add items to cart before you checkout!");
            return;
        }

        final Stage paymentStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainSalesApplication.class.getResource("payment-page.fxml"));
        paymentStage.initModality(Modality.APPLICATION_MODAL);
        paymentStage.initOwner((Stage) stage_vbox.getScene().getWindow());
        Scene paymentScene = new Scene(fxmlLoader.load(), 600, 400);
        paymentStage.setScene(paymentScene);
        PaymentController controller = fxmlLoader.getController();
        controller.initTotalPay(total_cost.getText());
        controller.initPurchaseListID(getAllOrderedItem());
        controller.initBackOrderStatus(checkQuantity());
        paymentStage.show();


    }

    // Change the cart total price and item when the user edit the quantity column
    // in the cart
    // @Param TableColumn.CellEditEvent -> The table column that has been edited by the user
    @FXML
    public void editCartAmount(TableColumn.CellEditEvent<Item,Integer> t) {
        message.setText("");
        Item i = t.getTableView().getItems().get(t.getTablePosition().getRow());
        Item originalItem = trackItem.get(cart.getSelectionModel().getSelectedIndex());
        i.setAmount(t.getNewValue());
        i.setPrice(originalItem.getPrice() * i.getAvailNum());
        cart.refresh();
        updateTotal();
    }

    // Display the stage containing customer demographic report
    @FXML
    public void displayCustomerReport() throws IOException {
        final Stage customerStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainSalesApplication.class.getResource("customer-report.fxml"));
        customerStage.initModality(Modality.APPLICATION_MODAL);
        customerStage.initOwner((Stage) stage_vbox.getScene().getWindow());
        Scene paymentScene = new Scene(fxmlLoader.load(), 800, 800);
        customerStage.setScene(paymentScene);
        customerStage.show();
    }

    // Display the enter entry stage for the user to enter new items
    // or update existing item quantities
    @FXML
    public void displayEnterItemWindow() throws IOException {
        final Stage itemEntryStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainSalesApplication.class.getResource("item-entry.fxml"));
        itemEntryStage.initModality(Modality.APPLICATION_MODAL);
        itemEntryStage.initOwner((Stage) stage_vbox.getScene().getWindow());
        Scene itemEntryScene = new Scene(fxmlLoader.load(), 500, 500);
        itemEntryStage.setScene(itemEntryScene);
        itemEntryStage.show();
    }

    // Display the stage of catalog
    @FXML
    public void displayRandomCatalog() throws IOException {
        final Stage catalogStage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainSalesApplication.class.getResource("catalog-page.fxml"));
        catalogStage.initModality(Modality.APPLICATION_MODAL);
        catalogStage.initOwner((Stage) stage_vbox.getScene().getWindow());
        Scene catalogScene = new Scene(fxmlLoader.load(), 800, 800);
        catalogStage.setScene(catalogScene);
        catalogStage.show();
    }

    // Display the stage for quarterly sales report
    @FXML
    public void displayQuarterlyReport() throws IOException {
        final Stage quarterlyReportStage = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(
                MainSalesApplication.class.getResource("quarterly-report.fxml")
        );

        quarterlyReportStage.initModality(Modality.APPLICATION_MODAL);
        quarterlyReportStage.initOwner((Stage) stage_vbox.getScene().getWindow());
        Scene quarterlyScene = new Scene(fxmlLoader.load(), 700, 500);
        quarterlyReportStage.setScene(quarterlyScene);
        quarterlyReportStage.show();
    }

    // Display the stage for monthly report
    @FXML
    public void displayMonthlyReport() throws IOException {
        final Stage monthlyReportStage = new Stage();

        FXMLLoader fxmlLoader = new FXMLLoader(
                MainSalesApplication.class.getResource("monthly-report.fxml")
        );

        monthlyReportStage.initModality(Modality.APPLICATION_MODAL);
        monthlyReportStage.initOwner((Stage) stage_vbox.getScene().getWindow());
        Scene monthlyScene = new Scene(fxmlLoader.load(), 720, 600);
        monthlyReportStage.setScene(monthlyScene);
        monthlyReportStage.show();
    }

    // Check if user entered search option are valid
    // Communicate with database to retrieve corresponding items
    //
    // @Param userSelectedType -> product type selected
    // @Param userSelectedGender -> user selected product gender
    // @Param userSelectedColor -> user selected product color
    // @Param userSelectedGender -> user selected product size
    // @Param userSelectedGender -> user selected product id
    private String processSearchOptions(String userSelectedType, String userSelectedGender,
                                        String userSelectedColor, String userSelectedSize, String userSelectedId) {
        String selection = " WHERE";

        if (!userSelectedGender.trim().equals("")) {
            selection += " Gender = '" + userSelectedGender + "'";
        }

        if (userSelectedType != null) {
            if (selection.equals(" WHERE")) {
                selection += " \"Product Type\" = '" + userSelectedType + "'";
            } else {
                selection += " AND \"Product Type\" = '" + userSelectedType + "'";
            }
        }

        if (!userSelectedColor.trim().equals("")) {
            if (selection.equals(" WHERE")) {
                selection += " Color = '" + userSelectedColor + "'";
            } else {
                selection += " AND Color = '" + userSelectedColor + "'";
            }
        }

        if (!userSelectedSize.trim().equals("")) {
            if (selection.equals(" WHERE")) {
                selection += " Size = '" + userSelectedSize + "'";
            } else {
                selection += " AND Size = '" + userSelectedSize + "'";
            }
        }

        if (!userSelectedId.trim().equals("")) {
            if (selection.equals(" WHERE")) {
                selection += " ID = '" + userSelectedId + "'";
            } else {
                selection += " AND ID = '" + userSelectedId + "'";
            }
        }

        return selection;
    }

    // Set cell factory of each column in the all item table
    private void setItemFactory() {
        TableManager.setFactory(product_type, product_color, product_gender,
                product_price, product_size, product_id);
        available_number.setCellValueFactory(new PropertyValueFactory<>("availNum"));
        product_availability.setCellValueFactory(new PropertyValueFactory<>("availability"));
        product_description.setCellValueFactory(new PropertyValueFactory<>("description"));
    }

    // Set cell factory of each column in the cart table
    private void setCartFactory() {
        TableManager.setFactory(cart_product, cart_color, cart_gender, cart_price, cart_size, cart_id);
        cart_amount.setCellValueFactory(new PropertyValueFactory<>("availNum"));
        cart_amount.setCellFactory(TextFieldTableCell.forTableColumn(new IntegerStringConverter()));
    }

    // Process items in the cart to store its ids
    // and quantities for future order processing
    private String getAllOrderedItem() {
        if (cart_items.size() == 0) {
            return "";
        }

        String allItem = "";
        for (int i = 0; i < cart_items.size() - 1; i++) {
            Item temp = cart_items.get(i);
            allItem += temp.getId() + "x" + temp.getAvailNum() + ", ";
        }
        Item last = cart_items.get(cart_items.size() - 1);
        allItem += last.getId() + "x" + last.getAvailNum();
        return allItem;
    }

    // Update total price
    private void updateTotal() {
        total = 0;
        for (Item i : cart_items) {
            total += i.getPrice();
        }
        total_cost.setText(String.valueOf(total));
    }

    // Check if the quantity of the item that user wants to purchase
    // exceeds the available amount
    private boolean checkQuantity() {
        for (int i = 0; i < cart_items.size(); i++) {
            Item inCart = cart_items.get(i);
            Item original = trackItem.get(i);
            if (inCart.getAvailNum() > original.getAvailNum()) {
                return false;
            }
        }
        return true;
    }

    @FXML
    public void refreshTable() throws SQLException {
        DatabaseManager db = new DatabaseManager();
        table.getItems().clear();
        String query = "SELECT * FROM Items";
        db.fillTable(query, items, false);
        db.close();
    }
}