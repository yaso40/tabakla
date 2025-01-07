package dao;

import models.User;
import java.sql.*;

public class UserDAO {

    public User login(String email, String password) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT u.*, s.restaurant_name, s.contact_info, s.address " +
                    "FROM users u " +
                    "LEFT JOIN sellers s ON u.user_id = s.user_id " +
                    "WHERE u.email = ? AND u.password = ?";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, password);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        User user = new User(
                                rs.getInt("user_id"),
                                rs.getString("name"),
                                rs.getString("email"),
                                rs.getString("password"),
                                rs.getString("user_type")
                        );

                        // Eğer satıcı ise ek bilgileri set et
                        if (user.getUserType().equals("seller")) {
                            user.setRestaurantName(rs.getString("restaurant_name"));
                            user.setContactInfo(rs.getString("contact_info"));
                            user.setAddress(rs.getString("address"));
                        }

                        return user;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(User user) {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);  // Transaction başlat

            // Users tablosuna ekle
            String userSql = "INSERT INTO users (name, email, password, user_type) VALUES (?, ?, ?, ?)";
            try (PreparedStatement userStmt = conn.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
                userStmt.setString(1, user.getName());
                userStmt.setString(2, user.getEmail());
                userStmt.setString(3, user.getPassword());
                userStmt.setString(4, user.getUserType());

                int affectedRows = userStmt.executeUpdate();

                if (affectedRows == 0) {
                    throw new SQLException("Kullanıcı oluşturulamadı.");
                }

                // Oluşturulan user_id'yi al
                try (ResultSet generatedKeys = userStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int userId = generatedKeys.getInt(1);
                        user.setUserId(userId);

                        // Eğer satıcı ise sellers tablosuna da ekle
                        if (user.getUserType().equals("seller")) {
                            String sellerSql = "INSERT INTO sellers (user_id, restaurant_name, email, contact_info, address) " +
                                    "VALUES (?, ?, ?, ?, ?)";
                            try (PreparedStatement sellerStmt = conn.prepareStatement(sellerSql)) {
                                sellerStmt.setInt(1, userId);
                                sellerStmt.setString(2, user.getRestaurantName());
                                sellerStmt.setString(3, user.getEmail());
                                sellerStmt.setString(4, user.getContactInfo());
                                sellerStmt.setString(5, user.getAddress());

                                sellerStmt.executeUpdate();
                            }
                        }
                    } else {
                        throw new SQLException("Kullanıcı ID'si alınamadı.");
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean updateUser(User user) {
        Connection conn = null;
        try {
            conn = Database.getConnection();
            conn.setAutoCommit(false);

            // Users tablosunu güncelle
            String userSql = "UPDATE users SET name = ?, email = ? WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(userSql)) {
                stmt.setString(1, user.getName());
                stmt.setString(2, user.getEmail());
                stmt.setInt(3, user.getUserId());
                stmt.executeUpdate();

                // Eğer satıcı ise sellers tablosunu da güncelle
                if (user.getUserType().equals("seller")) {
                    String sellerSql = "UPDATE sellers SET restaurant_name = ?, contact_info = ?, address = ? " +
                            "WHERE user_id = ?";
                    try (PreparedStatement sellerStmt = conn.prepareStatement(sellerSql)) {
                        sellerStmt.setString(1, user.getRestaurantName());
                        sellerStmt.setString(2, user.getContactInfo());
                        sellerStmt.setString(3, user.getAddress());
                        sellerStmt.setInt(4, user.getUserId());
                        sellerStmt.executeUpdate();
                    }
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e) {
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean isUniqueRestaurantName(String restaurantName) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT COUNT(*) FROM sellers WHERE restaurant_name = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, restaurantName);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        return rs.getInt(1) == 0;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
}