// src/ui/MainPage.java
package ui;

import javax.swing.*;
import java.awt.*;
import models.User;
import models.Product;
import dao.ProductDAO;

public class MainPage extends JFrame {
    private User user;
    private JTextField searchField;
    private JPanel dynamicArea;

    public MainPage(User user) {
        this.user = user;

        setTitle("Ana Sayfa");
        setSize(900, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Sol panel: Profil ve kategori butonları
        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(240, 240, 240));
        leftPanel.setPreferredSize(new Dimension(200, 600));

        // Profil Butonu
        JButton profileButton = new JButton("Profil");
        styleButton(profileButton);
        profileButton.addActionListener(e -> loadProfileEdit());
        leftPanel.add(profileButton);

        // Kategori Butonları
        JButton donerButton = new JButton("Döner");
        JButton burgerButton = new JButton("Burger");
        JButton kebabButton = new JButton("Kebap");
        JButton cigkofteButton = new JButton("Çiğ Köfte");

        styleButton(donerButton);
        styleButton(burgerButton);
        styleButton(kebabButton);
        styleButton(cigkofteButton);

        donerButton.addActionListener(e -> loadProductsByCategory("Döner"));
        burgerButton.addActionListener(e -> loadProductsByCategory("Burger"));
        kebabButton.addActionListener(e -> loadProductsByCategory("Kebap"));
        cigkofteButton.addActionListener(e -> loadProductsByCategory("Çiğ Köfte"));

        leftPanel.add(Box.createVerticalStrut(20));
        leftPanel.add(donerButton);
        leftPanel.add(burgerButton);
        leftPanel.add(kebabButton);
        leftPanel.add(cigkofteButton);

        add(leftPanel, BorderLayout.WEST);

        // Üst panel: Arama çubuğu ve sepet butonu
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        topPanel.setBackground(new Color(60, 63, 65));

        JLabel searchLabel = new JLabel("Ara:");
        searchLabel.setForeground(Color.WHITE);

        searchField = new JTextField(20);
        JButton searchButton = new JButton("Ara");
        JButton cartButton = new JButton("Sepet");

        styleButton(searchButton);
        styleButton(cartButton);

        searchButton.addActionListener(e -> searchProducts());
        cartButton.addActionListener(e -> openCart());

        topPanel.add(searchLabel);
        topPanel.add(searchField);
        topPanel.add(searchButton);
        topPanel.add(cartButton);

        add(topPanel, BorderLayout.NORTH);

        // Dinamik alan
        dynamicArea = new JPanel();
        dynamicArea.setLayout(new CardLayout());
        add(dynamicArea, BorderLayout.CENTER);

        // Varsayılan görünüm
        loadDefaultView();
    }

    private void loadDefaultView() {
        dynamicArea.removeAll();
        JLabel defaultLabel = new JLabel("Dinamik alan burada görüntülenecek.");
        dynamicArea.add(defaultLabel);
        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private void loadProductsByCategory(String category) {
        dynamicArea.removeAll();
        ProductDAO productDAO = new ProductDAO();
        for (Product product : productDAO.getProductsByCategory(category)) {
            dynamicArea.add(createProductPanel(product));
        }
        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private JPanel createProductPanel(Product product) {
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BorderLayout());
        productPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        productPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(product.getProductName(), SwingConstants.CENTER);
        JLabel priceLabel = new JLabel(product.getPrice() + " TL", SwingConstants.CENTER);

        productPanel.add(nameLabel, BorderLayout.CENTER);
        productPanel.add(priceLabel, BorderLayout.SOUTH);

        return productPanel;
    }

    private void loadProfileEdit() {
        dynamicArea.removeAll();
        dynamicArea.add(new ProfileForm(user));
        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private void openCart() {
        // Sepet ekranını aç
        new CartPage(user).setVisible(true);
    }

    private void searchProducts() {
        String keyword = searchField.getText();
        dynamicArea.removeAll();
        ProductDAO productDAO = new ProductDAO();
        for (Product product : productDAO.searchProducts(keyword)) {
            dynamicArea.add(createProductPanel(product));
        }
        dynamicArea.revalidate();
        dynamicArea.repaint();
    }

    private void styleButton(JButton button) {
        button.setFocusPainted(false);
        button.setBackground(new Color(70, 130, 180));
        button.setForeground(Color.WHITE);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
    }

    private void checkUserType() {
        if (user.getUserType().equals("seller")) {
            new SellerPage(user).setVisible(true);
        } else {
            loadDefaultView();
        }
    }
}
