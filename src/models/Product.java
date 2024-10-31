// src/models/Product.java
package models;

public class Product {
    private int productId;
    private String productName;
    private String category;
    private String description;
    private double price;

    public Product(int productId, String productName, String category, String description, double price) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public double getPrice() {
        return price;
    }
}
