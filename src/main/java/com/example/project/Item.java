// Hanze Simon Zhang
// This is an item class, storing information of each item/product

package com.example.project;

public class Item {
    private final String type; // Type of the product
    private final String color; // Color of the product
    private final String gender; // Gender of the product
    private int availNum; // The available number of the product
    private final String description; // The description of the product
    private final String availability; // The availability of the product
    private final String size; // Size of the product
    private int price; // Price of the product
    private final double discountedPrice; // 15% off price of the product
    private final String id; // id of the product

    // Construct a new item from the given type, color, gender, available number,
    // description, availability, size, price and id.
    // By default, set the discounted price to 15% off.
    public Item(String type, String color, String gender, int availNum,
                String description, String availability, String size, int price, String id) {
        this.type = type;
        this.color = color;
        this.gender = gender;
        this.availNum = availNum;
        this.description = description;
        this.availability = availability;
        this.size = size;
        this.price = price;
        this.id = id;
        this.discountedPrice = this.price * 0.85;

    }

    // Get the product type
    public String getType() {
        return type;
    }

    // Get the product color
    public String getColor(){
        return color;
    }

    // Get the gender for the product
    public String getGender() {
        return gender;
    }

    // Get the product available number
    public int getAvailNum() {
        return availNum;
    }

    // Get the product price
    public int getPrice() {
        return price;
    }

    // Get the product availability
    public String getAvailability() {
        return availability;
    }

    // Get the product description
    public String getDescription() {
        return description;
    }

    // Get the product size
    public String getSize() {
        return size;
    }

    // Get the product id
    public String getId() {
        return id;
    }

    // set the product available number
    public void setAmount(int availNum) {
        this.availNum = availNum;
    }

    // set the product price
    public void setPrice(int price) {
        this.price = price;
    }

    // Get the discounted price
    public double getDiscountedPrice() {
        return discountedPrice;
    }
}
