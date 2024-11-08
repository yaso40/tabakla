// src/ui/MainPage.java
package ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.Map;
import models.User;
import models.Product;
import dao.ProductDAO;
import java.util.List;


public class MainPage extends JFrame {
    private User user;
    private JTextField searchField;
    private JPanel dynamicArea;
    private JButton profileButton, searchButton, cartButton, logoutButton, homeButton;
    private Map<Product, Integer> cartProducts; // Sepet ürünlerini saklayan harita
    private ProductDAO productDAO;
    private SellerPage sellerPage; // SellerPage referansı

    public MainPage(User user) {
        this.user = user;
        this.cartProducts = new HashMap<>(); // cartProducts başlangıç olarak boş bir harita
        this.productDAO = new ProductDAO();  // ProductDAO nesnesi oluşturulması eklendi
        this.sellerPage = new SellerPage(user); // SellerPage örneği

        setTitle("Ana Sayfa");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Üst panel: Arama çubuğu, sepet ve ana sayfa butonları
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(60, 63, 65));

        JLabel searchLabel = new JLabel("Ara:");
        searchLabel.setForeground(Color.WHITE);

        searchField = new JTextField(15);
        searchButton = new JButton("Ara");
        cartButton = new JButton("Sepet");
        logoutButton = new JButton("Çıkış");
        homeButton = new JButton("Ana Sayfa");

        styleButton(searchButton);
        styleButton(cartButton);
        styleButton(logoutButton);
        styleButton(homeButton);

        searchButton.addActionListener(e -> searchProducts());
        cartButton.addActionListener(e -> openCart());
        logoutButton.addActionListener(e -> logout());
        homeButton.addActionListener(e -> loadRestaurants()); // Ana sayfa butonu için etkinleştirilmiş eylem

        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(cartButton);
        topPanel.add(homeButton);
        topPanel.add(logoutButton);

        add(topPanel, BorderLayout.NORTH);

        // Dinamik alan (ana ekran)
        dynamicArea = new JPanel(new GridLayout(0, 1, 10, 10));
        dynamicArea.setBackground(new Color(255, 255, 255));
        JScrollPane dynamicScrollPane = new JScrollPane(dynamicArea);
        add(dynamicScrollPane, BorderLayout.CENTER);

        // Sağ panel: Profil
        JPanel rightPanel = new JPanel();
        rightPanel.setLayout(new BorderLayout(10, 10));
        rightPanel.setPreferredSize(new Dimension(200, 600));
        rightPanel.setBackground(new Color(240, 240, 240));

        profileButton = createButton("Profil", new Color(0, 123, 255), e -> loadProfileEdit());
        rightPanel.add(profileButton, BorderLayout.NORTH);

        add(rightPanel, BorderLayout.EAST);

        // Kullanıcının sepetindeki mevcut ürünleri yükle
        loadInitialCartItems();

        // Varsayılan görünüm olarak restoranları yükleme
        loadRestaurants();
    }

    private void loadRestaurants() {
        dynamicArea.removeAll(); // Ana ekranı temizleyin
        for (User restaurant : productDAO.getAllRestaurants()) {
            JPanel restaurantPanel = new JPanel(new BorderLayout());
            restaurantPanel.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            JLabel restaurantLabel = new JLabel("<html><b>" + restaurant.getRestaurantName() + "</b></html>");
            restaurantLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
            restaurantPanel.add(restaurantLabel, BorderLayout.NORTH);

            // Kategoriler
            JPanel categoryPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            for (String category : productDAO.getCategoriesByRestaurant(restaurant.getUserId())) {
                JButton categoryButton = createButton(category, new Color(70, 130, 180), e -> loadProductsByCategory(category));
                categoryButton.setFont(new Font("Arial", Font.PLAIN, 9));
                categoryButton.setPreferredSize(new Dimension(70, 25));
                categoryPanel.add(categoryButton);
            }
            restaurantPanel.add(categoryPanel, BorderLayout.CENTER);
            dynamicArea.add(restaurantPanel); // Restoranları ana ekran dinamik alanına ekleyin
        }
        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private void loadProfileEdit() {
        dynamicArea.removeAll(); // Ana alanı temizleyin ve profil düzenleme ekranını ekleyin
        ProfileForm profileForm = new ProfileForm(user, () -> {
            dynamicArea.removeAll(); // Profil düzenlemeden çıkış yapılırsa ana sayfayı yükleyin
            loadRestaurants();
        });
        dynamicArea.add(profileForm); // Profil ekranını dinamik alana ekleyin
        dynamicArea.setVisible(true); // Dinamik alanı görünür yapın
        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private void loadProductsByCategory(String category) {
        dynamicArea.removeAll();
        List<Product> products = productDAO.getProductsByCategory(category);
        for (Product product : products) {
            dynamicArea.add(createProductPanel(product));
        }
        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private JPanel createProductPanel(Product product) {
        JPanel productPanel = new JPanel(new BorderLayout());
        productPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        productPanel.setBackground(Color.WHITE);
        productPanel.setPreferredSize(new Dimension(130, 80));

        JLabel nameLabel = new JLabel(product.getProductName(), SwingConstants.CENTER);
        nameLabel.setFont(new Font("SansSerif", Font.PLAIN, 10));
        JLabel priceLabel = new JLabel(product.getPrice() + " TL", SwingConstants.CENTER);
        priceLabel.setFont(new Font("SansSerif", Font.PLAIN, 9));
        JButton addToCartButton = createButton("Ekle", new Color(70, 130, 180), e -> addToCart(product));
        addToCartButton.setFont(new Font("Arial", Font.PLAIN, 9));

        productPanel.add(nameLabel, BorderLayout.CENTER);
        productPanel.add(priceLabel, BorderLayout.SOUTH);
        productPanel.add(addToCartButton, BorderLayout.EAST);

        return productPanel;
    }

    private void searchProducts() {
        String keyword = searchField.getText();
        dynamicArea.removeAll();
        List<Product> products = productDAO.searchProducts(keyword);
        for (Product product : products) {
            dynamicArea.add(createProductPanel(product));
        }
        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private void openCart() {
        dynamicArea.removeAll();
        CartPage cartPage = new CartPage(user, cartProducts, sellerPage); // SellerPage parametresi eklendi
        dynamicArea.add(cartPage);
        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private void addToCart(Product product) {
        cartProducts.put(product, cartProducts.getOrDefault(product, 0) + 1);
        productDAO.addToCart(user.getUserId(), product.getProductId(), 1); // Veritabanına ekle
        JOptionPane.showMessageDialog(this, product.getProductName() + " sepete eklendi!");
    }

    private void loadInitialCartItems() {
        List<Product> initialCartItems = productDAO.getCartItems(user.getUserId());
        for (Product product : initialCartItems) {
            cartProducts.put(product, 1); // Her üründen bir adet eklenmiş kabul edildi
        }
    }

    private void logout() {
        dispose();
        new LoginForm().setVisible(true);
    }

    private JButton createButton(String text, Color bgColor, ActionListener action) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 9));
        button.setPreferredSize(new Dimension(70, 25));
        button.addActionListener(action);
        return button;
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 9));
        button.setPreferredSize(new Dimension(90, 25));
    }
}
