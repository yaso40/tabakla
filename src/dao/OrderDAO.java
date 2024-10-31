// src/dao/OrderDAO.java
package dao;

import java.sql.*;
import java.util.ArrayList;

public class OrderDAO {
    public String[] getActiveOrders(int userId) {
        ArrayList<String> orders = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM Orders WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                orders.add(rs.getString("order_details"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders.toArray(new String[0]);
    }
}
