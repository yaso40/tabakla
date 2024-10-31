// src/dao/ProductDAO.java
package dao;

import models.Product;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM products WHERE product_name LIKE ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, "%" + keyword + "%");

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getDouble("price")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = category == null ? "SELECT * FROM products" : "SELECT * FROM products WHERE category = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            if (category != null) {
                stmt.setString(1, category);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getDouble("price")
                );
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public boolean addProduct(String name, double price, String category, String description, int sellerId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO products (product_name, price, category, description, seller_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setString(3, category);
            stmt.setString(4, description);
            stmt.setInt(5, sellerId); // seller_id'yi ekle

            int rows = stmt.executeUpdate();
            return rows > 0; // Başarıyla eklenmişse true döner
        } catch (SQLException e) {
            e.printStackTrace(); // Hata ayıklama için hata mesajını konsola yazdır
        }
        return false; // Eklenememişse false döner
    }

}
