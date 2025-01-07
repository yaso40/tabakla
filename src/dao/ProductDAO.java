package dao;

import models.Product;
import models.User;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Ürün arama
    public List<Product> searchProducts(String keyword) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM products WHERE product_name LIKE ? AND status = 'active'";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, "%" + keyword + "%");
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        Product product = new Product(
                                rs.getInt("product_id"),
                                rs.getString("product_name"),
                                rs.getString("category"),
                                rs.getString("description"),
                                rs.getDouble("price"),
                                rs.getInt("user_id")
                        );
                        products.add(product);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }
    public boolean addProduct(String name, double price, String category, String description, int userId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO products (product_name, price, category, description, user_id, status) " +
                    "VALUES (?, ?, ?, ?, ?, 'active')";

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, name);
                stmt.setDouble(2, price);
                stmt.setString(3, category);
                stmt.setString(4, description);
                stmt.setInt(5, userId);

                return stmt.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
    // Sepete ürün ekleme
    public void addToCart(int userId, int productId, int quantity) {
        try (Connection conn = Database.getConnection()) {
            // Önce eski kayıtları temizle
            String deleteSql = "DELETE FROM cart_items WHERE user_id = ?";
            try (PreparedStatement deleteStmt = conn.prepareStatement(deleteSql)) {
                deleteStmt.setInt(1, userId);
                deleteStmt.executeUpdate();
            }

            // Yeni kaydı ekle
            String insertSql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?)";
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql)) {
                insertStmt.setInt(1, userId);
                insertStmt.setInt(2, productId);
                insertStmt.setInt(3, quantity);
                insertStmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Sepetteki ürünleri getir
    public List<Product> getCartItems(int userId) {
        List<Product> cartItems = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT p.*, c.quantity FROM products p " +
                    "JOIN cart_items c ON p.product_id = c.product_id " +
                    "WHERE c.user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                ResultSet rs = stmt.executeQuery();
                while (rs.next()) {
                    Product product = new Product(
                            rs.getInt("product_id"),
                            rs.getString("product_name"),
                            rs.getString("category"),
                            rs.getString("description"),
                            rs.getDouble("price"),
                            rs.getInt("quantity"),
                            rs.getInt("user_id")
                    );
                    cartItems.add(product);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartItems;
    }

    // Restoranları getir
    public List<User> getAllRestaurants() {
        List<User> restaurants = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT u.user_id, s.restaurant_name, s.contact_info, s.address " +
                    "FROM users u JOIN sellers s ON u.user_id = s.user_id " +
                    "WHERE u.user_type = 'seller'";

            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    User restaurant = new User();
                    restaurant.setUserId(rs.getInt("user_id"));
                    restaurant.setRestaurantName(rs.getString("restaurant_name"));
                    restaurant.setContactInfo(rs.getString("contact_info"));
                    restaurant.setAddress(rs.getString("address"));
                    restaurants.add(restaurant);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurants;
    }

    // Restoran kategorilerini getir
    public List<String> getCategoriesByRestaurant(int restaurantId) {
        List<String> categories = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT DISTINCT category FROM products WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, restaurantId);
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) {
                        categories.add(rs.getString("category"));
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // Kategoriye göre ürünleri getir
    public List<Product> getProductsByCategoryAndRestaurant(String category, int restaurantId) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql;
            PreparedStatement stmt;

            if (category.equals("Tümü")) {
                sql = "SELECT * FROM products WHERE user_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setInt(1, restaurantId);
            } else {
                sql = "SELECT * FROM products WHERE category = ? AND user_id = ?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, category);
                stmt.setInt(2, restaurantId);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        restaurantId
                );
                product.setUserId(restaurantId);
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    public Product getProductById(int productId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM products WHERE product_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, productId);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                return new Product(
                        rs.getInt("product_id"),
                        rs.getInt("user_id"),
                        rs.getString("product_name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category")  // category_id yerine category kullanıyoruz
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Product> getProductsByCategory(String category) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = category.equals("Tümü") ?
                    "SELECT * FROM products" :
                    "SELECT * FROM products WHERE category = ?";  // category_id yerine category kullanıyoruz

            PreparedStatement stmt = conn.prepareStatement(sql);
            if (!category.equals("Tümü")) {
                stmt.setString(1, category);
            }

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getInt("user_id"),
                        rs.getString("product_name"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getString("category")  // category_id yerine category kullanıyoruz
                );
                products.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return products;
    }

    // Sepeti temizle
    public void clearCart(int userId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM cart_items WHERE user_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, userId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Ürün miktarını güncelle
    public void updateCartItemQuantity(int userId, int productId, int newQuantity) {
        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE cart_items SET quantity = ? " +
                    "WHERE user_id = ? AND product_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, newQuantity);
                stmt.setInt(2, userId);
                stmt.setInt(3, productId);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}