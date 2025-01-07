package ui;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import dao.Database;
import dao.OrderDAO;
import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import models.User;
import models.Product;
import dao.ProductDAO;
import java.text.SimpleDateFormat;
import java.util.Locale;
import models.Order; // Order modelini import et

public class MainPage extends JFrame {
    private User user;
    private JTextField searchField;
    private JPanel dynamicArea;
    private Map<Product, Integer> cartProducts;
    private ProductDAO productDAO;
    private OrderDAO orderDAO;  // Ekledik
    private SellerPage sellerPage;

    // UI Constants
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 16);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);

    public MainPage(User user) {
        this.user = user;
        this.cartProducts = new HashMap<>();
        this.productDAO = new ProductDAO();
        this.orderDAO = new OrderDAO();  // Ekledik
        this.sellerPage = null;

        setupFrame();
        createComponents();
        loadInitialCartItems();
        loadRestaurants();
    }

    private void setupFrame() {
        setTitle("Ana Sayfa");
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void createComponents() {
        add(createTopPanel(), BorderLayout.NORTH);
        add(createSidePanel(), BorderLayout.WEST);
        createDynamicArea();
    }

    private JPanel createTopPanel() {
        JPanel topPanel = new JPanel(new BorderLayout()); // FlowLayout yerine BorderLayout kullanalım
        topPanel.setBackground(PRIMARY_COLOR);
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));

        // Sol panel (logo ve ana sayfa butonu için)
        JPanel leftPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftPanel.setOpaque(false);

        // Logo
        JLabel titleLabel = new JLabel("TABAKLA");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        titleLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                loadRestaurants();
            }
        });
        leftPanel.add(titleLabel);

        // Ana Sayfa butonu
        JButton homeButton = createStyledButton("Ana Sayfa", PRIMARY_COLOR);
        homeButton.addActionListener(e -> loadRestaurants());
        leftPanel.add(homeButton);

        // Arama alanı
        searchField = createSearchField();
        leftPanel.add(searchField);

        JButton searchButton = createStyledButton("Ara", new Color(46, 139, 87));
        searchButton.addActionListener(e -> searchProducts());
        leftPanel.add(searchButton);

        topPanel.add(leftPanel, BorderLayout.WEST);

        // Sağ panel (diğer butonlar için)
        JPanel rightButtons = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtons.setOpaque(false);

        JButton previousOrdersButton = createStyledButton("Önceki Siparişlerim", PRIMARY_COLOR);
        previousOrdersButton.addActionListener(e -> showPreviousOrders());

        JButton cartButton = createStyledButton("Sepet", PRIMARY_COLOR);
        cartButton.addActionListener(e -> openCart());

        JButton profileButton = createStyledButton("Profil", PRIMARY_COLOR);
        profileButton.addActionListener(e -> loadProfileEdit());

        JButton sellerPageButton = createStyledButton("Satıcı Sayfası", PRIMARY_COLOR);
        sellerPageButton.addActionListener(e -> {
            if (sellerPage == null) {
                sellerPage = new SellerPage(user);
            }
            sellerPage.setVisible(true);
        });

        JButton logoutButton = createStyledButton("Çıkış", new Color(220, 53, 69));
        logoutButton.addActionListener(e -> logout());

        rightButtons.add(previousOrdersButton);
        rightButtons.add(cartButton);
        rightButtons.add(profileButton);
        rightButtons.add(sellerPageButton);
        rightButtons.add(logoutButton);

        topPanel.add(rightButtons, BorderLayout.EAST);

        return topPanel;
    }

    private JPanel createSidePanel() {
        JPanel sidePanel = new JPanel();
        sidePanel.setLayout(new BoxLayout(sidePanel, BoxLayout.Y_AXIS));
        sidePanel.setBackground(Color.WHITE);
        sidePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        sidePanel.setPreferredSize(new Dimension(200, getHeight()));

        JLabel categoriesLabel = new JLabel("Kategoriler");
        categoriesLabel.setFont(TITLE_FONT);
        categoriesLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidePanel.add(categoriesLabel);
        sidePanel.add(Box.createRigidArea(new Dimension(0, 10)));

        // Kategori butonları
        String[] categories = {"Tümü", "Burger", "Pizza", "Döner", "Tatlı", "İçecek"};
        for (String category : categories) {
            JButton categoryButton = createCategoryButton(category);
            sidePanel.add(categoryButton);
            sidePanel.add(Box.createRigidArea(new Dimension(0, 5)));
        }

        return sidePanel;
    }

    private void createDynamicArea() {
        dynamicArea = new JPanel();
        dynamicArea.setLayout(new WrapLayout(FlowLayout.LEFT, 15, 15));
        dynamicArea.setBackground(BACKGROUND_COLOR);

        JScrollPane scrollPane = new JScrollPane(dynamicArea);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        scrollPane.setBackground(BACKGROUND_COLOR);

        add(scrollPane, BorderLayout.CENTER);
    }

    private void loadRestaurants() {
        dynamicArea.removeAll();
        List<User> restaurants = productDAO.getAllRestaurants();

        for (User restaurant : restaurants) {
            JPanel restaurantCard = createRestaurantCard(restaurant);
            dynamicArea.add(restaurantCard);
        }

        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private void showPreviousOrders() {
        dynamicArea.removeAll();

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık ve Yenile butonu için üst panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Önceki Siparişlerim");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshButton = createStyledButton("Siparişleri Yenile", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> {
            showPreviousOrders();
            JOptionPane.showMessageDialog(this,
                    "Siparişler yenilendi.",
                    "Bilgi",
                    JOptionPane.INFORMATION_MESSAGE);
        });
        headerPanel.add(refreshButton, BorderLayout.EAST);

        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Siparişler paneli
        JPanel ordersPanel = new JPanel();
        ordersPanel.setLayout(new BoxLayout(ordersPanel, BoxLayout.Y_AXIS));
        ordersPanel.setBackground(Color.WHITE);

        List<Order> previousOrders = orderDAO.getPreviousOrders(user.getUserId());

        if (previousOrders.isEmpty()) {
            JLabel noOrderLabel = new JLabel("Henüz siparişiniz bulunmamaktadır.");
            noOrderLabel.setFont(new Font("Arial", Font.PLAIN, 16));
            ordersPanel.add(noOrderLabel);
        } else {
            for (Order order : previousOrders) {
                JPanel orderPanel = createPreviousOrderPanel(order);
                ordersPanel.add(orderPanel);
                ordersPanel.add(Box.createRigidArea(new Dimension(0, 10)));
            }
        }

        JScrollPane scrollPane = new JScrollPane(ordersPanel);
        scrollPane.setBorder(null);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        dynamicArea.add(mainPanel);
        dynamicArea.revalidate();
        dynamicArea.repaint();
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

    private JPanel createPreviousOrderPanel(Order order) {
        JPanel panel = new JPanel(new BorderLayout(10, 5));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Sol taraf - Sipariş bilgileri
        JPanel infoPanel = new JPanel(new GridLayout(4, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);

        SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("tr", "TR"));

        JLabel dateLabel = new JLabel("Tarih: " + sdf.format(order.getOrderDate()));
        JLabel productLabel = new JLabel("Ürün: " + order.getProductName());
        JLabel quantityLabel = new JLabel("Miktar: " + order.getQuantity());

        // Durumu Türkçe göster
        String statusText = switch (order.getStatus().toLowerCase()) {
            case "delivered" -> "Tamamlandı";
            case "cancelled" -> "İptal Edildi";
            case "pending" -> "Beklemede";
            default -> order.getStatus();
        };
        JLabel statusLabel = new JLabel("Durum: " + statusText);

        infoPanel.add(dateLabel);
        infoPanel.add(productLabel);
        infoPanel.add(quantityLabel);
        infoPanel.add(statusLabel);

        panel.add(infoPanel, BorderLayout.CENTER);

        // Sağ taraf - Tekrar sipariş ver butonu (sadece tamamlanmış siparişler için)
        if ("delivered".equals(order.getStatus())) {
            JButton reorderButton = createStyledButton("Tekrar Sipariş Ver", new Color(46, 139, 87));
            reorderButton.addActionListener(e -> reorderPreviousOrder(order));
            panel.add(reorderButton, BorderLayout.EAST);
        }

        return panel;
    }

    private String getStatusText(String status) {
        switch (status.toLowerCase()) {
            case "pending": return "Beklemede";
            case "delivered": return "Tamamlandı";
            case "cancelled": return "İptal Edildi";
            default: return status;
        }
    }

    private void reorderPreviousOrder(Order order) {
        // Ürünü bul
        Product product = productDAO.getProductById(order.getProductId());
        if (product != null) {
            // Sepete ekle
            addToCart(product);
        } else {
            JOptionPane.showMessageDialog(this,
                    "Ürün artık mevcut değil.",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
        }
    }

    private JPanel createRestaurantCard(User restaurant) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(280, 200));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Restaurant name
        JLabel nameLabel = new JLabel(restaurant.getRestaurantName());
        nameLabel.setFont(TITLE_FONT);
        card.add(nameLabel, BorderLayout.NORTH);

        // Categories panel
        JPanel categoriesPanel = new JPanel(new WrapLayout(FlowLayout.LEFT, 5, 5));
        categoriesPanel.setBackground(Color.WHITE);

        List<String> categories = productDAO.getCategoriesByRestaurant(restaurant.getUserId());
        for (String category : categories) {
            JButton categoryBtn = createCategoryChip(category, restaurant.getUserId());
            categoriesPanel.add(categoryBtn);
        }

        card.add(categoriesPanel, BorderLayout.CENTER);

        return card;
    }

    private void loadProductsByCategory(String category, int restaurantId) {
        dynamicArea.removeAll();
        List<Product> products = productDAO.getProductsByCategoryAndRestaurant(category, restaurantId);

        for (Product product : products) {
            JPanel productCard = createProductCard(product);
            dynamicArea.add(productCard);
        }

        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private JPanel createProductCard(Product product) {
        JPanel card = new JPanel(new BorderLayout(10, 10));
        card.setPreferredSize(new Dimension(200, 250));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));

        // Product info
        JPanel infoPanel = new JPanel(new GridLayout(3, 1, 5, 5));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(product.getProductName());
        nameLabel.setFont(TITLE_FONT);

        JLabel priceLabel = new JLabel(String.format("%.2f TL", product.getPrice()));
        priceLabel.setFont(REGULAR_FONT);

        JButton addButton = createStyledButton("Sepete Ekle", new Color(46, 139, 87));
        addButton.addActionListener(e -> addToCart(product));

        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        infoPanel.add(addButton);

        card.add(infoPanel, BorderLayout.CENTER);

        return card;
    }

    private void addToCart(Product product) {
        boolean isSameRestaurant = true;
        for (Product p : cartProducts.keySet()) {
            if (p.getRestaurantId() != product.getRestaurantId()) {
                isSameRestaurant = false;
                break;
            }
        }

        if (!isSameRestaurant && !cartProducts.isEmpty()) {
            int response = JOptionPane.showConfirmDialog(this,
                    "Sepetinizdeki ürünler farklı restoranlardan. Mevcut sepetiniz temizlenecek. Devam etmek istiyor musunuz?",
                    "Uyarı", JOptionPane.YES_NO_OPTION);

            if (response == JOptionPane.YES_OPTION) {
                productDAO.clearCart(user.getUserId());
                cartProducts.clear();
                addProductToCart(product);
            }
        } else {
            addProductToCart(product);
        }
    }

    private void addProductToCart(Product product) {
        int currentQuantity = cartProducts.getOrDefault(product, 0);
        cartProducts.put(product, currentQuantity + 1);
        productDAO.addToCart(user.getUserId(), product.getProductId(), currentQuantity + 1);
        JOptionPane.showMessageDialog(this, "Ürün sepete eklendi!");
    }

    private void loadInitialCartItems() {
        cartProducts.clear();
        List<Product> initialCartItems = productDAO.getCartItems(user.getUserId());
        for (Product product : initialCartItems) {
            cartProducts.put(product, product.getQuantity());
        }
    }

    private void openCart() {
        dynamicArea.removeAll();

        JPanel cartPanel = new JPanel(new BorderLayout(10, 10));
        cartPanel.setBackground(Color.WHITE);
        cartPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Sepetim");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        cartPanel.add(titleLabel, BorderLayout.NORTH);

        JPanel productsPanel = new JPanel();
        productsPanel.setLayout(new BoxLayout(productsPanel, BoxLayout.Y_AXIS));
        productsPanel.setBackground(Color.WHITE);



        double totalPrice = 0;
        for (Map.Entry<Product, Integer> entry : cartProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            totalPrice += product.getPrice() * quantity;

            JPanel productPanel = new JPanel(new BorderLayout(10, 5));
            productPanel.setBackground(Color.WHITE);
            productPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));

            // Ürün bilgileri
            JPanel infoPanel = new JPanel(new GridLayout(2, 1));
            infoPanel.setBackground(Color.WHITE);

            JLabel nameLabel = new JLabel(product.getProductName());
            nameLabel.setFont(new Font("Arial", Font.BOLD, 14));

            JLabel priceLabel = new JLabel(String.format("%.2f TL", product.getPrice() * quantity));
            priceLabel.setFont(new Font("Arial", Font.PLAIN, 14));

            infoPanel.add(nameLabel);
            infoPanel.add(priceLabel);
            productPanel.add(infoPanel, BorderLayout.CENTER);

            // Miktar kontrolleri
            JPanel quantityPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            quantityPanel.setBackground(Color.WHITE);

            JButton decreaseButton = new JButton("-");
            JLabel quantityLabel = new JLabel(String.valueOf(quantity));
            JButton increaseButton = new JButton("+");

            decreaseButton.addActionListener(e -> {
                int newQuantity = Integer.parseInt(quantityLabel.getText()) - 1;
                if (newQuantity > 0) {
                    quantityLabel.setText(String.valueOf(newQuantity));
                    cartProducts.put(product, newQuantity);
                    productDAO.updateCartItemQuantity(user.getUserId(), product.getProductId(), newQuantity);
                    openCart(); // Sepeti yenile
                }
            });

            increaseButton.addActionListener(e -> {
                int newQuantity = Integer.parseInt(quantityLabel.getText()) + 1;
                quantityLabel.setText(String.valueOf(newQuantity));
                cartProducts.put(product, newQuantity);
                productDAO.updateCartItemQuantity(user.getUserId(), product.getProductId(), newQuantity);
                openCart(); // Sepeti yenile
            });
            quantityPanel.add(decreaseButton);
            quantityPanel.add(quantityLabel);
            quantityPanel.add(increaseButton);
            productPanel.add(quantityPanel, BorderLayout.EAST);

            productsPanel.add(productPanel);
            productsPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        JScrollPane scrollPane = new JScrollPane(productsPanel);
        scrollPane.setBorder(null);
        cartPanel.add(scrollPane, BorderLayout.CENTER);

        // Alt panel
        JPanel bottomPanel = new JPanel(new BorderLayout(10, 10));
        bottomPanel.setBackground(Color.WHITE);

        JLabel totalLabel = new JLabel(String.format("Toplam: %.2f TL", totalPrice));
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        bottomPanel.add(totalLabel, BorderLayout.WEST);

        // Butonlar
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);

        JButton confirmButton = createStyledButton("Sepeti Onayla", new Color(46, 139, 87));
        JButton clearButton = createStyledButton("Sepeti Boşalt", new Color(220, 53, 69));

        confirmButton.addActionListener(e -> {
            if (!cartProducts.isEmpty()) {
                int response = JOptionPane.showConfirmDialog(this,
                        "Siparişi onaylamak istiyor musunuz?",
                        "Onay",
                        JOptionPane.YES_NO_OPTION);

                if (response == JOptionPane.YES_OPTION) {
                    // İlk ürünün satıcı ID'sini al
                    Product firstProduct = cartProducts.keySet().iterator().next();
                    int sellerId = firstProduct.getUserId();

                    // Siparişi oluştur
                    if (orderDAO.createOrder(user.getUserId(), sellerId, new ArrayList<>(cartProducts.keySet()))) {
                        // Satıcı sayfasını güncelle
                        if (sellerPage != null) {
                            for (Map.Entry<Product, Integer> entry : cartProducts.entrySet()) {
                                Product product = entry.getKey();
                                int quantity = entry.getValue();

                                // Sipariş detaylarını oluştur
                                String[] orderDetails = {
                                        String.valueOf(product.getProductId()),
                                        product.getProductName(),
                                        String.valueOf(quantity),
                                        user.getName(),  // Müşteri adı
                                        "pending",       // Sipariş durumu
                                        new java.util.Date().toString()  // Sipariş tarihi
                                };

                                // Satıcı sayfasına siparişi ekle
                                sellerPage.addOrderToTable(orderDetails);
                            }
                        }

                        JOptionPane.showMessageDialog(this,
                                "Siparişiniz başarıyla oluşturuldu.",
                                "Başarılı",
                                JOptionPane.INFORMATION_MESSAGE);

                        // Sepeti temizle
                        productDAO.clearCart(user.getUserId());
                        cartProducts.clear();
                        openCart();
                    } else {
                        JOptionPane.showMessageDialog(this,
                                "Sipariş oluşturulurken bir hata oluştu.",
                                "Hata",
                                JOptionPane.ERROR_MESSAGE);
                    }
                }
            }
        });

        clearButton.addActionListener(e -> {
            if (!cartProducts.isEmpty()) {
                productDAO.clearCart(user.getUserId());
                cartProducts.clear();
                openCart();
            }
        });

        buttonPanel.add(confirmButton);
        buttonPanel.add(clearButton);
        bottomPanel.add(buttonPanel, BorderLayout.EAST);

        cartPanel.add(bottomPanel, BorderLayout.SOUTH);

        dynamicArea.add(cartPanel);
        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private void loadProfileEdit() {
        dynamicArea.removeAll();
        ProfileForm profileForm = new ProfileForm(user, this::loadRestaurants);
        dynamicArea.add(profileForm);
        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    // Helper methods for UI components
    private JTextField createSearchField() {
        JTextField field = new JTextField(20);
        field.setFont(REGULAR_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Color.WHITE),
                BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(bgColor);  // Yazı rengi arka plan rengi olsun
        button.setBackground(Color.WHITE);  // Arka plan beyaz olsun
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(bgColor);
            }
        });

        return button;
    }

    private JButton createCategoryButton(String category) {
        JButton button = new JButton(category);
        button.setFont(REGULAR_FONT);
        button.setForeground(PRIMARY_COLOR);
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.addActionListener(e -> {
            if (category.equals("Tümü")) {
                loadRestaurants();
            } else {
                dynamicArea.removeAll();
                List<Product> products = productDAO.getProductsByCategory(category);
                for (Product product : products) {
                    JPanel productCard = createProductCard(product);
                    dynamicArea.add(productCard);
                }
                dynamicArea.revalidate();
                dynamicArea.repaint();
            }
        });
        return button;
    }

    private JButton createCategoryChip(String category, int restaurantId) {
        JButton chip = new JButton(category);
        chip.setFont(new Font("Arial", Font.PLAIN, 12));
        chip.setForeground(PRIMARY_COLOR);
        chip.setBackground(new Color(240, 240, 240));
        chip.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        chip.setFocusPainted(false);
        chip.setCursor(new Cursor(Cursor.HAND_CURSOR));
        chip.addActionListener(e -> loadProductsByCategory(category, restaurantId));
        return chip;
    }

    private void searchProducts() {
        String keyword = searchField.getText().trim().toLowerCase();
        if (!keyword.isEmpty()) {
            dynamicArea.removeAll();
            List<Product> products = productDAO.searchProducts(keyword);

            if (products.isEmpty()) {
                JLabel noResultLabel = new JLabel("Sonuç bulunamadı.");
                noResultLabel.setFont(new Font("Arial", Font.BOLD, 16));
                dynamicArea.add(noResultLabel);
            } else {
                for (Product product : products) {
                    JPanel productCard = createProductCard(product);
                    dynamicArea.add(productCard);
                }
            }

            dynamicArea.revalidate();
            dynamicArea.repaint();
        }
    }

    private void logout() {
        int response = JOptionPane.showConfirmDialog(this,
                "Çıkış yapmak istediğinize emin misiniz?",
                "Çıkış", JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm().setVisible(true);
        }
    }
}

// WrapLayout class for better grid layout
class WrapLayout extends FlowLayout {
    public WrapLayout() {
        super();
    }

    public WrapLayout(int align) {
        super(align);
    }

    public WrapLayout(int align, int hgap, int vgap) {
        super(align, hgap, vgap);
    }

    @Override
    public Dimension preferredLayoutSize(Container target) {
        return layoutSize(target, true);
    }

    @Override
    public Dimension minimumLayoutSize(Container target) {
        return layoutSize(target, false);
    }

    private Dimension layoutSize(Container target, boolean preferred) {
        synchronized (target.getTreeLock()) {
            int targetWidth = target.getWidth();
            if (targetWidth == 0) {
                targetWidth = Integer.MAX_VALUE;
            }

            int hgap = getHgap();
            int vgap = getVgap();
            Insets insets = target.getInsets();
            int horizontalInsetsAndGap = insets.left + insets.right + (hgap * 2);
            int maxWidth = targetWidth - horizontalInsetsAndGap;

            Dimension dim = new Dimension(0, 0);
            int rowWidth = 0;
            int rowHeight = 0;

            int nmembers = target.getComponentCount();
            for (int i = 0; i < nmembers; i++) {
                Component m = target.getComponent(i);
                if (m.isVisible()) {
                    Dimension d = preferred ? m.getPreferredSize() : m.getMinimumSize();
                    if (rowWidth + d.width > maxWidth) {
                        addRow(dim, rowWidth, rowHeight);
                        rowWidth = 0;
                        rowHeight = 0;
                    }
                    if (rowWidth != 0) {
                        rowWidth += hgap;
                    }
                    rowWidth += d.width;
                    rowHeight = Math.max(rowHeight, d.height);
                }
            }
            addRow(dim, rowWidth, rowHeight);

            dim.width += horizontalInsetsAndGap;
            dim.height += insets.top + insets.bottom + vgap * 2;

            return dim;
        }
    }

    private void addRow(Dimension dim, int rowWidth, int rowHeight) {
        dim.width = Math.max(dim.width, rowWidth);
        if (dim.height > 0) {
            dim.height += getVgap();
        }
        dim.height += rowHeight;
    }
}