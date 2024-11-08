package dao;

import models.User;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public User login(String email, String password) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM Users WHERE email = ? AND password = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, email);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new User(rs.getInt("user_id"), rs.getString("name"), rs.getString("email"),
                        rs.getString("password"), rs.getString("user_type"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean register(User user) {
        try (Connection conn = Database.getConnection()) {
            // Kullanıcıyı users tablosuna ekle
            String userSql = "INSERT INTO Users (name, email, password, user_type) VALUES (?, ?, ?, ?)";
            PreparedStatement userStmt = conn.prepareStatement(userSql, PreparedStatement.RETURN_GENERATED_KEYS);
            userStmt.setString(1, user.getName());
            userStmt.setString(2, user.getEmail());
            userStmt.setString(3, user.getPassword());
            userStmt.setString(4, user.getUserType());

            int rows = userStmt.executeUpdate();

            // Kullanıcı eklenmişse (rows > 0)
            if (rows > 0 && user.getUserType().equals("seller")) {
                // Oluşturulan user_id'yi al
                ResultSet generatedKeys = userStmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    int userId = generatedKeys.getInt(1);

                    // Restaurant adının eşsiz olduğunu kontrol et
                    if (!isUniqueRestaurantName(user.getRestaurantName())) {
                        System.out.println("Restaurant adı zaten mevcut.");
                        return false;
                    }

                    // Satıcı bilgilerini sellers tablosuna ekle
                    String sellerSql = "INSERT INTO Sellers (user_id, restaurant_name, contact_info, address) VALUES (?, ?, ?, ?)";
                    PreparedStatement sellerStmt = conn.prepareStatement(sellerSql);
                    sellerStmt.setInt(1, userId);
                    sellerStmt.setString(2, user.getRestaurantName());
                    sellerStmt.setString(3, user.getContactInfo());
                    sellerStmt.setString(4, user.getAddress());

                    int sellerRows = sellerStmt.executeUpdate();
                    return sellerRows > 0;
                }
            }

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean isUniqueRestaurantName(String restaurantName) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT COUNT(*) FROM Sellers WHERE restaurant_name = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, restaurantName);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1) == 0; // Eğer count 0 ise, benzersizdir
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }
    public boolean updateUser(User user) {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE Users SET name = ?, email = ? WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, user.getName());
            stmt.setString(2, user.getEmail());
            stmt.setInt(3, user.getUserId());
            int rows = stmt.executeUpdate();

            // Eğer kullanıcı satıcı ise Sellers tablosunu da güncelle
            if (user.getUserType().equals("seller")) {
                String sellerSql = "UPDATE Sellers SET restaurant_name = ?, contact_info = ?, address = ? WHERE user_id = ?";
                PreparedStatement sellerStmt = conn.prepareStatement(sellerSql);
                sellerStmt.setString(1, user.getRestaurantName());
                sellerStmt.setString(2, user.getContactInfo());
                sellerStmt.setString(3, user.getAddress());
                sellerStmt.setInt(4, user.getUserId());
                sellerStmt.executeUpdate();
            }

            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
