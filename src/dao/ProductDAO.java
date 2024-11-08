package dao;

import models.Product;
import models.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProductDAO {

    // Mevcut kodlar burada korunmuştur, satır sayısı azaltılmamıştır.

    // Belirli bir anahtar kelimeye göre ürünleri arar
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

    // Belirli bir kategoriye göre ürünleri getirir
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

    // Yeni ürün ekler
    public boolean addProduct(String name, double price, String category, String description, int userId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO products (product_name, price, category, description, user_id) VALUES (?, ?, ?, ?, ?)";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, name);
            stmt.setDouble(2, price);
            stmt.setString(3, category);
            stmt.setString(4, description);
            stmt.setInt(5, userId);

            int rows = stmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // Tüm restoranları getirir (Satıcıları)
    public List<User> getAllRestaurants() {
        List<User> restaurants = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT user_id, restaurant_name, contact_info, address FROM sellers";
            PreparedStatement stmt = conn.prepareStatement(sql);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                User restaurant = new User();
                restaurant.setUserId(rs.getInt("user_id"));
                restaurant.setRestaurantName(rs.getString("restaurant_name"));
                restaurant.setContactInfo(rs.getString("contact_info"));
                restaurant.setAddress(rs.getString("address"));
                restaurants.add(restaurant);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return restaurants;
    }

    // Belirli bir restoranın kategorilerini getirir
    public List<String> getCategoriesByRestaurant(int restaurantId) {
        List<String> categories = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT DISTINCT category FROM products WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, restaurantId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                categories.add(rs.getString("category"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return categories;
    }

    // Belirli bir kategori ve restorana göre ürünleri getirir
    public List<Product> getProductsByCategoryAndRestaurant(String category, int restaurantId) {
        List<Product> products = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT * FROM products WHERE category = ? AND user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setString(1, category);
            stmt.setInt(2, restaurantId);

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

    // Kullanıcının sepetindeki başlangıç ürünlerini getirir
    public List<Product> getInitialCartProducts(int userId) {
        List<Product> cartProducts = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT p.product_id, p.product_name, p.category, p.description, p.price, c.quantity " +
                    "FROM cart c JOIN products p ON c.product_id = p.product_id " +
                    "WHERE c.buyer_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getDouble("price")
                        // quantity eklenmedi çünkü Product sınıfında yok
                );
                // Alternatif olarak, quantity gibi ek bilgiler için ek işlemler yapılabilir.
                cartProducts.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartProducts;
    }

    // Sepete ürün ekler veya miktarı günceller (EKLENDİ)
    public void addToCart(int userId, int productId, int quantity) {
        try (Connection conn = Database.getConnection()) {
            String sql = "INSERT INTO cart_items (user_id, product_id, quantity) VALUES (?, ?, ?) " +
                    "ON DUPLICATE KEY UPDATE quantity = quantity + ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.setInt(2, productId);
            stmt.setInt(3, quantity);
            stmt.setInt(4, quantity);

            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Kullanıcının sepetindeki ürünleri getirir (EKLENDİ)
    public List<Product> getCartItems(int userId) {
        List<Product> cartProducts = new ArrayList<>();
        try (Connection conn = Database.getConnection()) {
            String sql = "SELECT p.product_id, p.product_name, p.category, p.description, p.price, ci.quantity " +
                    "FROM cart_items ci JOIN products p ON ci.product_id = p.product_id " +
                    "WHERE ci.user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);

            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                Product product = new Product(
                        rs.getInt("product_id"),
                        rs.getString("product_name"),
                        rs.getString("category"),
                        rs.getString("description"),
                        rs.getDouble("price")
                );
                // Ürün miktarını ayrıca eklemek isterseniz, Product sınıfında quantity alanı açılabilir.
                cartProducts.add(product);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return cartProducts;
    }

    // Sepeti temizler (EKLENDİ)
    public void clearCart(int userId) {
        try (Connection conn = Database.getConnection()) {
            String sql = "DELETE FROM cart_items WHERE user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(sql);
            stmt.setInt(1, userId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
