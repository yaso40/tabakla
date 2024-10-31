// src/ui/SellerPage.java
package ui;

import javax.swing.*;
import java.awt.*;
import models.User;
import dao.ProductDAO;

public class SellerPage extends JFrame {
    private User user;
    private JTextField productNameField, productPriceField, productCategoryField, productDescriptionField;
    private JButton addProductButton, viewOrdersButton;

    public SellerPage(User user) {
        this.user = user;

        setTitle("Satıcı Sayfası");
        setSize(500, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Ürün ekleme paneli
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new GridLayout(5, 2));

        productPanel.add(new JLabel("Ürün Adı:"));  // product_name
        productNameField = new JTextField();
        productPanel.add(productNameField);

        productPanel.add(new JLabel("Fiyat:"));  // price
        productPriceField = new JTextField();
        productPanel.add(productPriceField);

        productPanel.add(new JLabel("Kategori:"));  // category
        productCategoryField = new JTextField();
        productPanel.add(productCategoryField);

        productPanel.add(new JLabel("Açıklama:"));  // description
        productDescriptionField = new JTextField();
        productPanel.add(productDescriptionField);

        addProductButton = new JButton("Ürün Ekle");
        addProductButton.addActionListener(e -> addProduct());
        productPanel.add(addProductButton);

        add(productPanel, BorderLayout.CENTER);

        // Siparişleri görüntüleme butonu
        viewOrdersButton = new JButton("Siparişleri Görüntüle");
        viewOrdersButton.addActionListener(e -> viewOrders());
        add(viewOrdersButton, BorderLayout.SOUTH);
    }

    private void addProduct() {
        String productName = productNameField.getText(); // product_name
        double productPrice = Double.parseDouble(productPriceField.getText()); // price
        String productCategory = productCategoryField.getText(); // category
        String productDescription = productDescriptionField.getText(); // description
        int sellerId = user.getUserId(); // Satıcının kimliğini al

        ProductDAO productDAO = new ProductDAO();
        if (productDAO.addProduct(productName, productPrice, productCategory, productDescription, sellerId)) {
            JOptionPane.showMessageDialog(this, "Ürün başarıyla eklendi.");
        } else {
            JOptionPane.showMessageDialog(this, "Ürün eklenirken hata oluştu.");
        }
    }

    private void viewOrders() {
        // Sipariş yönetim sayfasını aç
        OrderManagement orderManagement = new OrderManagement(user);
        orderManagement.setVisible(true);
    }
}
