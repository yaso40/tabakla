package ui;

import models.Product;
import models.User;
import javax.swing.*;
import java.awt.*;
import java.util.Map;
import dao.ProductDAO;

public class CartPage extends JPanel {
    private User user;
    private Map<Product, Integer> cartProducts;
    private JPanel productListPanel;
    private JLabel totalPriceLabel;
    private ProductDAO productDAO;
    private SellerPage sellerPage;
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 18);
    private static final Font ITEM_FONT = new Font("Arial", Font.PLAIN, 14);

    public CartPage(User user, Map<Product, Integer> cartProducts, SellerPage sellerPage) {
        this.user = user;
        this.cartProducts = cartProducts;
        this.productDAO = new ProductDAO();
        this.sellerPage = sellerPage;

        setLayout(new BorderLayout(10, 10));
        setBackground(BACKGROUND_COLOR);
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        // Başlık ve toplam fiyat paneli
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Ürün listesi
        createProductListPanel();
        JScrollPane scrollPane = new JScrollPane(productListPanel);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        add(scrollPane, BorderLayout.CENTER);

        // Alt panel (Butonlar)
        JPanel bottomPanel = createBottomPanel();
        add(bottomPanel, BorderLayout.SOUTH);

        refreshCartDisplay();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Sepetim", SwingConstants.LEFT);
        titleLabel.setFont(TITLE_FONT);
        panel.add(titleLabel, BorderLayout.WEST);

        totalPriceLabel = new JLabel("Toplam: 0.00 TL", SwingConstants.RIGHT);
        totalPriceLabel.setFont(TITLE_FONT);
        panel.add(totalPriceLabel, BorderLayout.EAST);

        return panel;
    }

    private void createProductListPanel() {
        productListPanel = new JPanel();
        productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));
        productListPanel.setBackground(BACKGROUND_COLOR);
    }

    private JPanel createBottomPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(BACKGROUND_COLOR);

        JButton clearButton = createStyledButton("Sepeti Boşalt", new Color(244, 67, 54));
        clearButton.addActionListener(e -> clearCart());

        JButton confirmButton = createStyledButton("Sipariş Ver", new Color(76, 175, 80));
        confirmButton.addActionListener(e -> confirmCart());

        panel.add(clearButton);
        panel.add(confirmButton);

        return panel;
    }

    private void refreshCartDisplay() {
        productListPanel.removeAll();
        double total = 0;

        for (Map.Entry<Product, Integer> entry : cartProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            total += product.getPrice() * quantity;

            JPanel productCard = createProductCard(product, quantity);
            productListPanel.add(productCard);
            productListPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        }

        totalPriceLabel.setText(String.format("Toplam: %.2f TL", total));
        productListPanel.revalidate();
        productListPanel.repaint();
    }

    private JPanel createProductCard(Product product, int quantity) {
        JPanel card = new JPanel(new BorderLayout(10, 0));
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 80));

        // Sol taraf - Ürün bilgileri
        JPanel infoPanel = new JPanel(new GridLayout(2, 1));
        infoPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(product.getProductName());
        nameLabel.setFont(ITEM_FONT);

        JLabel priceLabel = new JLabel(String.format("%.2f TL", product.getPrice()));
        priceLabel.setFont(ITEM_FONT);

        infoPanel.add(nameLabel);
        infoPanel.add(priceLabel);
        card.add(infoPanel, BorderLayout.WEST);

        // Sağ taraf - Miktar kontrolleri
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 5, 0));
        controlPanel.setBackground(Color.WHITE);

        JButton decreaseButton = createQuantityButton("-");
        decreaseButton.addActionListener(e -> updateQuantity(product, -1));

        JLabel quantityLabel = new JLabel(String.valueOf(quantity), SwingConstants.CENTER);
        quantityLabel.setPreferredSize(new Dimension(30, 25));
        quantityLabel.setFont(ITEM_FONT);

        JButton increaseButton = createQuantityButton("+");
        increaseButton.addActionListener(e -> updateQuantity(product, 1));

        JButton deleteButton = createStyledButton("Sil", new Color(244, 67, 54));
        deleteButton.addActionListener(e -> removeProductFromCart(product));

        controlPanel.add(decreaseButton);
        controlPanel.add(quantityLabel);
        controlPanel.add(increaseButton);
        controlPanel.add(deleteButton);

        card.add(controlPanel, BorderLayout.EAST);

        return card;
    }

    private JButton createQuantityButton(String text) {
        JButton button = new JButton(text);
        button.setFont(ITEM_FONT);
        button.setPreferredSize(new Dimension(25, 25));
        button.setFocusPainted(false);
        button.setBackground(new Color(240, 240, 240));
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        return button;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(ITEM_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        return button;
    }

    private void updateQuantity(Product product, int change) {
        int currentQuantity = cartProducts.getOrDefault(product, 0);
        int newQuantity = currentQuantity + change;

        if (newQuantity > 0) {
            cartProducts.put(product, newQuantity);
            productDAO.updateCartItemQuantity(user.getUserId(), product.getProductId(), newQuantity);
        } else {
            removeProductFromCart(product);
        }
        refreshCartDisplay();
    }

    private void removeProductFromCart(Product product) {
        cartProducts.remove(product);
        productDAO.updateCartItemQuantity(user.getUserId(), product.getProductId(), 0);
        refreshCartDisplay();
    }

    private void clearCart() {
        int response = JOptionPane.showConfirmDialog(this,
                "Sepeti boşaltmak istediğinize emin misiniz?",
                "Onay",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            cartProducts.clear();
            productDAO.clearCart(user.getUserId());
            refreshCartDisplay();
        }
    }

    private void confirmCart() {
        if (cartProducts.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Sepetiniz boş!");
            return;
        }

        int response = JOptionPane.showConfirmDialog(this,
                "Siparişinizi onaylamak istiyor musunuz?",
                "Sipariş Onayı",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            StringBuilder orderDetails = new StringBuilder();
            for (Map.Entry<Product, Integer> entry : cartProducts.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                orderDetails.append(product.getProductName())
                        .append(" - Adet: ")
                        .append(quantity)
                        .append("\n");

                if (sellerPage != null) {
                    String[] rowData = {
                            String.valueOf(product.getProductId()),
                            product.getProductName(),
                            String.valueOf(quantity),
                            user.getAddress()
                    };
                    sellerPage.addOrderToTable(rowData);
                }
            }

            if (sellerPage != null) {
                sellerPage.addOrder(orderDetails.toString());
                JOptionPane.showMessageDialog(this, "Siparişiniz başarıyla oluşturuldu!");
                clearCart();
            }
        }
    }
}