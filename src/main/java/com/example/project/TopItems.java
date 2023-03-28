// This class stores information of a top sale item.
// It includes every attribute of an item, and monthly sold quantities.

package com.example.project;

public class TopItems {
    private final String type; // product type
    private final String color; // product color
    private final String gender; // for which gender
    private final String size; // product size
    private final int price; // product prize
    private final String id; // product id
    private final int monthlySold; // quatity sold in a last month

    // Create a new top sold item based on given parameters
    //
    // @Param type - product type
    // @Param color - product color
    // @Param gender - for which gender
    // @Param size - product size
    // @Param price - product price
    // @Param id - product id
    // @Param monthlySold - the quantity this item sold in a month
    public TopItems(String type, String color, String gender,
                    String size, int price, String id, int monthlySold) {
        this.type = type;
        this.color = color;
        this.gender = gender;
        this.size = size;
        this.price = price;
        this.id = id;
        this.monthlySold = monthlySold;
    }

    // return the product type
    public String getType() {
        return type;
    }

    // return the product color
    public String getColor(){
        return color;
    }

    // return the gender that the product is for
    public String getGender() {
        return gender;
    }

    // return the product price
    public int getPrice() {
        return price;
    }

    // return the product size
    public String getSize() {
        return size;
    }

    // return the product id
    public String getId() {
        return id;
    }

    // get the monthly sold quantity
    public int getMonthlySold() {
        return monthlySold;
    }
}
