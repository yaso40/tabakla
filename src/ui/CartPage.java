// src/ui/CartPage.java
package ui;

import models.Product;
import models.User;

import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class CartPage extends JPanel {
    private User user;
    private Map<Product, Integer> cartProducts;
    private JPanel productListPanel;
    private JLabel totalPriceLabel;
    private SellerPage sellerPage; // SellerPage referansı

    public CartPage(User user, Map<Product, Integer> cartProducts, SellerPage sellerPage) { // SellerPage parametre olarak alındı
        this.user = user;
        this.cartProducts = cartProducts;
        this.sellerPage = sellerPage;

        setLayout(new BorderLayout(10, 10));
        setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Sepetiniz", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        productListPanel = new JPanel();
        productListPanel.setLayout(new BoxLayout(productListPanel, BoxLayout.Y_AXIS));
        productListPanel.setBackground(new Color(245, 245, 245));
        add(new JScrollPane(productListPanel), BorderLayout.CENTER);

        // Sepeti boşalt ve sepeti onayla butonları
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton clearCartButton = new JButton("Sepeti Boşalt");
        clearCartButton.addActionListener(e -> clearCart());
        JButton confirmCartButton = new JButton("Sepeti Onayla");
        confirmCartButton.addActionListener(e -> confirmCart());

        buttonPanel.add(clearCartButton);
        buttonPanel.add(confirmCartButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Toplam fiyat label
        totalPriceLabel = new JLabel("Toplam Tutar: 0 TL", SwingConstants.RIGHT);
        totalPriceLabel.setFont(new Font("SansSerif", Font.BOLD, 14));
        add(totalPriceLabel, BorderLayout.NORTH);

        refreshCartDisplay(); // Sepet ürünlerini yükler ve gösterir
    }

    private void refreshCartDisplay() {
        productListPanel.removeAll();
        double total = 0;

        for (Map.Entry<Product, Integer> entry : cartProducts.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            total += product.getPrice() * quantity; // Toplam fiyat hesaplanıyor
            productListPanel.add(createCartProductPanel(product, quantity));
        }

        totalPriceLabel.setText("Toplam Tutar: " + String.format("%.2f TL", total));
        productListPanel.revalidate();
        productListPanel.repaint();
    }

    private JPanel createCartProductPanel(Product product, int quantity) {
        JPanel productPanel = new JPanel(new BorderLayout(10, 10));
        productPanel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        productPanel.setBackground(Color.WHITE);

        JLabel nameLabel = new JLabel(product.getProductName());
        JLabel quantityLabel = new JLabel("Adet: " + quantity);

        JButton decreaseButton = new JButton("-");
        decreaseButton.addActionListener(e -> updateQuantity(product, -1));

        JButton increaseButton = new JButton("+");
        increaseButton.addActionListener(e -> updateQuantity(product, 1));

        JButton deleteButton = new JButton("Sil");
        deleteButton.addActionListener(e -> removeProductFromCart(product));

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(decreaseButton);
        buttonPanel.add(quantityLabel);
        buttonPanel.add(increaseButton);
        buttonPanel.add(deleteButton);

        productPanel.add(nameLabel, BorderLayout.WEST);
        productPanel.add(buttonPanel, BorderLayout.EAST);

        return productPanel;
    }

    private void updateQuantity(Product product, int change) {
        int currentQuantity = cartProducts.getOrDefault(product, 0);
        int newQuantity = currentQuantity + change;

        if (newQuantity > 0) {
            cartProducts.put(product, newQuantity);
        } else {
            cartProducts.remove(product);
        }
        refreshCartDisplay();
    }

    private void removeProductFromCart(Product product) {
        cartProducts.remove(product);
        refreshCartDisplay();
    }

    private void clearCart() {
        cartProducts.clear();
        refreshCartDisplay();
    }

    private void confirmCart() {
        int response = JOptionPane.showConfirmDialog(this, "Sepeti onaylamak istediğinize emin misiniz?", "Onay", JOptionPane.YES_NO_OPTION);
        if (response == JOptionPane.YES_OPTION) {
            StringBuilder orderDetails = new StringBuilder("Sipariş Detayları:\n");
            for (Map.Entry<Product, Integer> entry : cartProducts.entrySet()) {
                Product product = entry.getKey();
                int quantity = entry.getValue();
                orderDetails.append(product.getProductName()).append(" - Adet: ").append(quantity).append("\n");
            }
            // SellerPage'deki sipariş listesine ekleme
            if (sellerPage != null) {
                sellerPage.addOrder(orderDetails.toString());
                JOptionPane.showMessageDialog(this, "Sipariş onaylandı ve satıcıya iletildi.");
            } else {
                JOptionPane.showMessageDialog(this, "SellerPage bağlantısı yok.");
            }

            // Sepeti temizleme
            clearCart();
        }
    }
}
