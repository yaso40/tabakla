package models;

public class Product {
    private int productId;
    private String productName;
    private String category;
    private String description;
    private double price;
    private int quantity; // Yeni eklenen miktar alanı

    public Product(int productId, String productName, String category, String description, double price, int quantity) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
    }

    // Ürün miktarı olmadan kullanılan yapıcı
    public Product(int productId, String productName, String category, String description, double price) {
        this(productId, productName, category, description, price, 0); // Default olarak 0 miktar
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

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}
