// src/ui/SellerPage.java
package ui;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import dao.UserDAO;
import dao.ProductDAO;
import dao.OrderDAO;
import models.User;

public class SellerPage extends JFrame {
    private User user;
    private JTextField nameField, emailField, restaurantNameField, contactInfoField, addressField;
    private JTextField productNameField, productDescriptionField;
    private JFormattedTextField productPriceField;
    private JComboBox<String> categoryComboBox;
    private DefaultListModel<String> orderListModel;
    private JList<String> orderList;
    private JPanel orderPanel, profilePanel, productPanel;
    private JButton addProductButton, editButton, saveButton, cancelButton;
    private boolean isEditing = false;

    public SellerPage(User user) {
        this.user = user;

        setTitle("Satıcı Paneli");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        JPanel leftPanel = new JPanel();
        leftPanel.setLayout(new BoxLayout(leftPanel, BoxLayout.Y_AXIS));
        leftPanel.setBackground(new Color(240, 240, 240));
        leftPanel.setPreferredSize(new Dimension(250, 600));

        addProductButton = createButton("Ürün Ekle", new Color(0, 123, 255), e -> openProductPanel());
        leftPanel.add(addProductButton);

        editButton = createButton("Profili Düzenle", new Color(255, 152, 0), e -> toggleEditMode());
        leftPanel.add(editButton);

        add(leftPanel, BorderLayout.WEST);

        orderPanel = createOrderPanel();
        add(orderPanel, BorderLayout.CENTER);

        profilePanel = createProfilePanel();
        profilePanel.setVisible(false);
        add(profilePanel, BorderLayout.EAST);

        productPanel = createProductPanel();
        productPanel.setVisible(false);
        add(productPanel, BorderLayout.CENTER);

        loadOrders();
    }

    private JPanel createOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JLabel titleLabel = new JLabel("Aktif Siparişler", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        panel.add(titleLabel, BorderLayout.NORTH);

        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        orderList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        orderList.setCellRenderer(new OrderCellRenderer());
        panel.add(new JScrollPane(orderList), BorderLayout.CENTER);

        return panel;
    }

    public void addOrder(String order) {
        orderListModel.addElement(order);
    }

    private JPanel createProfilePanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Ad:"), gbc);
        gbc.gridx = 1;
        nameField = new JTextField(user.getName(), 20);
        nameField.setEditable(false);
        panel.add(nameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Email:"), gbc);
        gbc.gridx = 1;
        emailField = new JTextField(user.getEmail(), 20);
        emailField.setEditable(false);
        panel.add(emailField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Restaurant Adı:"), gbc);
        gbc.gridx = 1;
        restaurantNameField = new JTextField(user.getRestaurantName(), 20);
        restaurantNameField.setEditable(false);
        panel.add(restaurantNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("İletişim Bilgisi:"), gbc);
        gbc.gridx = 1;
        contactInfoField = new JTextField(user.getContactInfo(), 20);
        contactInfoField.setEditable(false);
        panel.add(contactInfoField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 4;
        panel.add(new JLabel("Adres:"), gbc);
        gbc.gridx = 1;
        addressField = new JTextField(user.getAddress(), 20);
        addressField.setEditable(false);
        panel.add(addressField, gbc);

        saveButton = createButton("Kaydet", new Color(76, 175, 80), e -> saveProfile());
        cancelButton = createButton("İptal", new Color(244, 67, 54), e -> cancelEdit());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveButton);
        buttonPanel.add(cancelButton);

        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private JPanel createProductPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        gbc.gridx = 0;
        gbc.gridy = 0;
        panel.add(new JLabel("Ürün Adı:"), gbc);
        gbc.gridx = 1;
        productNameField = new JTextField(20);
        panel.add(productNameField, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        panel.add(new JLabel("Fiyat:"), gbc);
        gbc.gridx = 1;
        NumberFormat format = new DecimalFormat("#,##0.00");
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setAllowsInvalid(false);
        formatter.setMinimum(0.0);

        productPriceField = new JFormattedTextField(formatter);
        productPriceField.setColumns(15);
        JPanel pricePanel = new JPanel(new BorderLayout());
        pricePanel.add(productPriceField, BorderLayout.CENTER);
        JLabel currencyLabel = new JLabel("₺");
        currencyLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        pricePanel.add(currencyLabel, BorderLayout.EAST);
        panel.add(pricePanel, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        panel.add(new JLabel("Kategori:"), gbc);
        gbc.gridx = 1;
        String[] categories = {"Burger", "Pizza", "Döner", "Tatlı", "İçecek"};
        categoryComboBox = new JComboBox<>(categories);
        panel.add(categoryComboBox, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        panel.add(new JLabel("Açıklama:"), gbc);
        gbc.gridx = 1;
        productDescriptionField = new JTextField(20);
        panel.add(productDescriptionField, gbc);

        gbc.gridx = 1;
        gbc.gridy = 4;
        JButton saveProductButton = createButton("Kaydet", new Color(0, 123, 255), e -> addProduct());
        JButton cancelProductButton = createButton("İptal", new Color(244, 67, 54), e -> closeProductPanel());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(saveProductButton);
        buttonPanel.add(cancelProductButton);
        panel.add(buttonPanel, gbc);

        return panel;
    }

    private void openProductPanel() {
        orderPanel.setVisible(false);
        profilePanel.setVisible(false);
        productPanel.setVisible(true);
    }

    private void closeProductPanel() {
        productPanel.setVisible(false);
        orderPanel.setVisible(true);
    }
    private class OrderCellRenderer extends JPanel implements ListCellRenderer<String> {
        private JLabel orderLabel;
        private JButton confirmButton;
        private JButton cancelButton;

        public OrderCellRenderer() {
            setLayout(new BorderLayout());
            orderLabel = new JLabel();
            confirmButton = new JButton("Onayla");
            cancelButton = new JButton("İptal Et");

            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            buttonPanel.add(confirmButton);
            buttonPanel.add(cancelButton);

            add(orderLabel, BorderLayout.CENTER);
            add(buttonPanel, BorderLayout.EAST);

            confirmButton.addActionListener(e -> JOptionPane.showMessageDialog(SellerPage.this, "Sipariş onaylandı."));
            cancelButton.addActionListener(e -> JOptionPane.showMessageDialog(SellerPage.this, "Sipariş iptal edildi."));
        }

        @Override
        public Component getListCellRendererComponent(JList<? extends String> list, String value, int index, boolean isSelected, boolean cellHasFocus) {
            orderLabel.setText(value);
            setBackground(isSelected ? Color.LIGHT_GRAY : Color.WHITE);
            return this;
        }
    }

    private void addProduct() {
        String productName = productNameField.getText();
        double productPrice = ((Number) productPriceField.getValue()).doubleValue();
        String productCategory = (String) categoryComboBox.getSelectedItem();
        String productDescription = productDescriptionField.getText();
        int userId = user.getUserId();

        ProductDAO productDAO = new ProductDAO();
        if (productDAO.addProduct(productName, productPrice, productCategory, productDescription, userId)) {
            JOptionPane.showMessageDialog(this, "Ürün başarıyla eklendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
            closeProductPanel();
        } else {
            JOptionPane.showMessageDialog(this, "Ürün eklenirken hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void toggleEditMode() {
        isEditing = !isEditing;
        orderPanel.setVisible(!isEditing);
        profilePanel.setVisible(isEditing);
        nameField.setEditable(isEditing);
        emailField.setEditable(isEditing);
        restaurantNameField.setEditable(isEditing);
        contactInfoField.setEditable(isEditing);
        addressField.setEditable(isEditing);
    }

    private void saveProfile() {
        user.setName(nameField.getText());
        user.setEmail(emailField.getText());
        user.setRestaurantName(restaurantNameField.getText());
        user.setContactInfo(contactInfoField.getText());
        user.setAddress(addressField.getText());

        UserDAO userDAO = new UserDAO();
        if (userDAO.updateUser(user)) {
            JOptionPane.showMessageDialog(this, "Profil başarıyla güncellendi.");
            toggleEditMode();
        } else {
            JOptionPane.showMessageDialog(this, "Profil güncellenirken bir hata oluştu.");
        }
    }

    private void cancelEdit() {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        restaurantNameField.setText(user.getRestaurantName());
        contactInfoField.setText(user.getContactInfo());
        addressField.setText(user.getAddress());

        toggleEditMode();
    }

    private void loadOrders() {
        OrderDAO orderDAO = new OrderDAO();
        String[] orders = orderDAO.getActiveOrders(user.getUserId());
        orderListModel.clear();
        for (String order : orders) {
            orderListModel.addElement(order);
        }
    }

    private void confirmOrder() {
        String selectedOrder = orderList.getSelectedValue();
        if (selectedOrder != null) {
            int option = JOptionPane.showConfirmDialog(this, "Bu siparişi onaylamak istiyor musunuz?", "Siparişi Onayla", JOptionPane.YES_NO_OPTION);
            if (option == JOptionPane.YES_OPTION) {
                JOptionPane.showMessageDialog(this, "Sipariş onaylandı.");
                orderListModel.removeElement(selectedOrder);
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen onaylamak için bir sipariş seçin.");
        }
    }

    private JButton createButton(String text, Color bgColor, java.awt.event.ActionListener action) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        button.addActionListener(action);
        return button;
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SellerPage(new User()).setVisible(true));
    }
}
