package dao;

import java.sql.*;
import java.util.ArrayList;

public class OrderDAO {

    // Aktif siparişleri alma metodu
    public String[] getActiveOrders(int buyerId) {
        ArrayList<String> orders = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT order_id, total_price FROM orders WHERE buyer_id = ? AND status = 'Active'";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, buyerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                int orderId = rs.getInt("order_id");
                double totalPrice = rs.getDouble("total_price");
                orders.add("Sipariş " + orderId + " - Toplam: " + totalPrice + " TL");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders.toArray(new String[0]);
    }

    // Sipariş iptal etme metodu
    public boolean cancelOrder(int orderId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE orders SET status = 'Cancelled' WHERE order_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, orderId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}
