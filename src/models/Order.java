package models;

import java.util.Date;

public class Order {
    private int orderId;
    private int buyerId;
    private double totalPrice;
    private Date orderDate;
    private String status;
    private String buyerName;
    private String buyerAddress;
    private int quantity;
    private String productName;
    private int productId;

    // Ana constructor
    public Order(int orderId, int buyerId, double totalPrice, Date orderDate,
                 String status, String productName, int quantity, int productId,
                 String buyerName, String buyerAddress) {
        this.orderId = orderId;
        this.buyerId = buyerId;
        this.totalPrice = totalPrice;
        this.orderDate = orderDate;
        this.status = status;
        this.productName = productName;
        this.quantity = quantity;
        this.productId = productId;
        this.buyerName = buyerName;
        this.buyerAddress = buyerAddress;
    }

    // Önceki siparişler için constructor
    public Order(int orderId, int buyerId, double totalPrice, Date orderDate,
                 String status, String productName, int quantity, int productId) {
        this(orderId, buyerId, totalPrice, orderDate, status, productName,
                quantity, productId, null, null);
    }

    // Getters
    public int getOrderId() { return orderId; }
    public int getBuyerId() { return buyerId; }
    public double getTotalPrice() { return totalPrice; }
    public Date getOrderDate() { return orderDate; }
    public String getStatus() { return status; }
    public String getBuyerName() { return buyerName; }
    public String getBuyerAddress() { return buyerAddress; }
    public int getQuantity() { return quantity; }
    public String getProductName() { return productName; }
    public int getProductId() { return productId; }

    // Setters
    public void setOrderId(int orderId) { this.orderId = orderId; }
    public void setBuyerId(int buyerId) { this.buyerId = buyerId; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }
    public void setOrderDate(Date orderDate) { this.orderDate = orderDate; }
    public void setStatus(String status) { this.status = status; }
    public void setBuyerName(String buyerName) { this.buyerName = buyerName; }
    public void setBuyerAddress(String buyerAddress) { this.buyerAddress = buyerAddress; }
    public void setQuantity(int quantity) { this.quantity = quantity; }
    public void setProductName(String productName) { this.productName = productName; }
    public void setProductId(int productId) { this.productId = productId; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", productName='" + productName + '\'' +
                ", quantity=" + quantity +
                ", totalPrice=" + totalPrice +
                ", status='" + status + '\'' +
                ", buyerName='" + buyerName + '\'' +
                ", buyerAddress='" + buyerAddress + '\'' +
                '}';
    }
}