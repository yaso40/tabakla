package models;

public class Product {
    private int userId;
    private int productId;
    private String productName;
    private String category;
    private String description;
    private double price;
    private int quantity;
    private int restaurantId;

    // Mevcut constructorlar
    public Product(int productId, String productName, String category,
                   String description, double price, int quantity, int restaurantId) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.description = description;
        this.price = price;
        this.quantity = quantity;
        this.restaurantId = restaurantId;
    }

    public Product(int productId, String productName, String category,
                   String description, double price, int restaurantId) {
        this(productId, productName, category, description, price, 0, restaurantId);
    }

    // Veritabanı için yeni constructor
    public Product(int productId, int userId, String productName,
                   String description, double price, String category) {
        this.productId = productId;
        this.userId = userId;
        this.productName = productName;
        this.description = description;
        this.price = price;
        this.category = category;
        this.quantity = 0;
        this.restaurantId = userId; // user_id'yi restaurant_id olarak kullan
    }

    // Mevcut getters
    public int getProductId() { return productId; }
    public String getProductName() { return productName; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getQuantity() { return quantity; }
    public int getRestaurantId() { return restaurantId; }
    public int getUserId() { return userId; }

    // Mevcut setters
    public void setProductId(int productId) { this.productId = productId; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setCategory(String category) { this.category = category; }
    public void setDescription(String description) { this.description = description; }
    public void setPrice(double price) { this.price = price; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setRestaurantId(int restaurantId) { this.restaurantId = restaurantId; }
    public void setUserId(int userId) { this.userId = userId; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return productId == product.productId;
    }

    @Override
    public int hashCode() {
        return Integer.hashCode(productId);
    }

    @Override
    public String toString() {
        return "Product{" +
                "productName='" + productName + '\'' +
                ", price=" + price +
                ", quantity=" + quantity +
                '}';
    }
}