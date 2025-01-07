package dao;

import models.Order;
import models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {
    public List<Order> getOrdersBySeller(int sellerId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT o.order_id, o.buyer_id, o.total_price, o.order_date, o.status, " +
                    "u.name as buyer_name, " +
                    "p.product_name, " +
                    "oi.quantity, " +
                    "u.address as buyer_address " +
                    "FROM orders o " +
                    "JOIN users u ON o.buyer_id = u.user_id " +
                    "JOIN order_items oi ON o.order_id = oi.order_id " +
                    "JOIN products p ON oi.product_id = p.product_id " +
                    "WHERE o.seller_id = ? AND o.status = 'pending'";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, sellerId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Order order = new Order(
                            rs.getInt("order_id"),
                            rs.getInt("buyer_id"),
                            rs.getDouble("total_price"),
                            rs.getTimestamp("order_date"),
                            rs.getString("status"),
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            0,
                            rs.getString("buyer_name"),
                            rs.getString("buyer_address")
                    );
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getPendingOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.buyer_id, o.total_price, o.order_date, o.status, " +
                "u.name AS buyer_name, p.product_name, oi.quantity, u.address AS buyer_address " +
                "FROM orders o " +
                "JOIN users u ON o.buyer_id = u.user_id " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE LOWER(o.status) = 'pending'"; // seller_id kaldırıldı

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getInt("buyer_id"),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("order_date"),
                        rs.getString("status"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getInt("product_id"),
                        rs.getString("buyer_name"),
                        rs.getString("buyer_address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }


    public List<Order> getPendingOrdersForSeller(int sellerId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.buyer_id, o.total_price, o.order_date, o.status, " +
                "u.name as buyer_name, " +
                "p.product_name, " +
                "oi.quantity, " +
                "u.address as buyer_address " +
                "FROM orders o " +
                "JOIN users u ON o.buyer_id = u.user_id " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE o.seller_id = ? AND o.status = 'pending'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, sellerId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("buyer_id"),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("order_date"),
                        rs.getString("status"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getInt("product_id"),
                        rs.getString("buyer_name"),
                        rs.getString("buyer_address")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public List<Order> getAllPendingOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.buyer_id, o.total_price, o.order_date, o.status, " +
                "u.name AS buyer_name, p.product_name, oi.quantity, p.product_id, u.address AS buyer_address " +  // Düzeltildi
                "FROM orders o " +
                "JOIN users u ON o.buyer_id = u.user_id " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE LOWER(o.status) = 'pending'";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                orders.add(new Order(
                        rs.getInt("order_id"),
                        rs.getInt("buyer_id"),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("order_date"),
                        rs.getString("status"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getInt("product_id"),  // Düzeltildi
                        rs.getString("buyer_name"),
                        rs.getString("buyer_address")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }



    public List<Order> getPreviousOrders(int userId) {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT o.order_id, o.buyer_id, o.total_price, o.order_date, o.status, " +
                "p.product_name, p.product_id, oi.quantity " +
                "FROM orders o " +
                "JOIN order_items oi ON o.order_id = oi.order_id " +
                "JOIN products p ON oi.product_id = p.product_id " +
                "WHERE o.buyer_id = ? " +
                "ORDER BY o.order_date DESC";

        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Order order = new Order(
                        rs.getInt("order_id"),
                        rs.getInt("buyer_id"),
                        rs.getDouble("total_price"),
                        rs.getTimestamp("order_date"),
                        rs.getString("status"),
                        rs.getString("product_name"),
                        rs.getInt("quantity"),
                        rs.getInt("product_id")
                );
                orders.add(order);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public boolean updateOrderStatus(int orderId, String status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (Connection conn = Database.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, orderId);
            int result = stmt.executeUpdate();
            System.out.println("Update result: " + result); // Debug için
            return result > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markOrderAsDelivered(int orderId) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // Transaction başlat
            try {
                // Önce siparişin var olduğunu ve durumunu kontrol et
                String checkSql = "SELECT status FROM orders WHERE order_id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, orderId);
                    ResultSet rs = checkStmt.executeQuery();

                    if (!rs.next() || !"pending".equals(rs.getString("status"))) {
                        conn.rollback();
                        return false;
                    }
                }

                // Siparişi güncelle
                String updateSql = "UPDATE orders SET status = 'delivered' WHERE order_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, orderId);
                    int affected = updateStmt.executeUpdate();

                    if (affected > 0) {
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean cancelOrder(int orderId) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false); // Transaction başlat
            try {
                // Önce siparişin var olduğunu ve durumunu kontrol et
                String checkSql = "SELECT status FROM orders WHERE order_id = ?";
                try (PreparedStatement checkStmt = conn.prepareStatement(checkSql)) {
                    checkStmt.setInt(1, orderId);
                    ResultSet rs = checkStmt.executeQuery();

                    if (!rs.next() || !"pending".equals(rs.getString("status"))) {
                        conn.rollback();
                        return false;
                    }
                }

                // Siparişi güncelle
                String updateSql = "UPDATE orders SET status = 'cancelled' WHERE order_id = ?";
                try (PreparedStatement updateStmt = conn.prepareStatement(updateSql)) {
                    updateStmt.setInt(1, orderId);
                    int affected = updateStmt.executeUpdate();

                    if (affected > 0) {
                        conn.commit();
                        return true;
                    } else {
                        conn.rollback();
                        return false;
                    }
                }
            } catch (SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }



    public Product getProductById(int productId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM products WHERE product_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, productId);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getInt("user_id")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // OrderDAO'da createOrder metodunu güncelle
    public boolean createOrder(int buyerId, int sellerId, List<Product> cartItems) {
        try (Connection conn = Database.getConnection()) {
            conn.setAutoCommit(false);
            try {
                // Yeni sipariş oluştur
                String createOrderSql = "INSERT INTO orders (buyer_id, seller_id, total_price, status, order_date) " +
                        "VALUES (?, ?, 0.00, 'pending', NOW())";

                int orderId;
                try (PreparedStatement stmt = conn.prepareStatement(createOrderSql, Statement.RETURN_GENERATED_KEYS)) {
                    stmt.setInt(1, buyerId);
                    stmt.setInt(2, sellerId);
                    stmt.executeUpdate();

                    ResultSet rs = stmt.getGeneratedKeys();
                    if (!rs.next()) {
                        conn.rollback();
                        return false;
                    }
                    orderId = rs.getInt(1);
                }

                // Sipariş öğelerini ekle
                String createItemsSql = "INSERT INTO order_items (order_id, product_id, quantity, price) VALUES (?, ?, ?, ?)";
                try (PreparedStatement stmt = conn.prepareStatement(createItemsSql)) {
                    for (Product item : cartItems) {
                        stmt.setInt(1, orderId);
                        stmt.setInt(2, item.getProductId());
                        stmt.setInt(3, item.getQuantity());
                        stmt.setDouble(4, item.getPrice());
                        stmt.executeUpdate();
                    }
                }

                // Toplam fiyatı güncelle
                String updateTotalSql = "UPDATE orders SET total_price = (SELECT SUM(quantity * price) FROM order_items WHERE order_id = ?) WHERE order_id = ?";
                try (PreparedStatement stmt = conn.prepareStatement(updateTotalSql)) {
                    stmt.setInt(1, orderId);
                    stmt.setInt(2, orderId);
                    stmt.executeUpdate();
                }

                conn.commit();
                return true;
            } catch (SQLException e) {
                conn.rollback();
                e.printStackTrace();
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Order> getActiveOrders(int buyerId) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT o.order_id, o.buyer_id, o.total_price, o.order_date, o.status, " +
                    "p.product_name, oi.quantity " +
                    "FROM orders o " +
                    "JOIN order_items oi ON o.order_id = oi.order_id " +
                    "JOIN products p ON oi.product_id = p.product_id " +
                    "WHERE o.buyer_id = ? AND o.status = 'pending' " +
                    "ORDER BY o.order_date DESC";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, buyerId);
                ResultSet rs = stmt.executeQuery();

                while (rs.next()) {
                    Order order = new Order(
                            rs.getInt("order_id"),
                            rs.getInt("buyer_id"),
                            rs.getDouble("total_price"),
                            rs.getDate("order_date"),
                            rs.getString("status"),
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getInt("product_id")  // İkinci constructor'ı kullanıyoruz
                    );
                    orders.add(order);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }


}