package ui;

import javax.swing.*;
import java.awt.*;
import models.User;
import dao.UserDAO;

public class SellerProfileForm extends JFrame {
    private User user;
    private JTextField nameField, emailField, restaurantNameField, contactInfoField, addressField;
    private JButton editButton, saveButton, cancelButton;
    private boolean isEditing = false;

    public SellerProfileForm(User user) {
        this.user = user;
        setTitle("Satıcı Profili");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // Başlık
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(new Color(70, 130, 180));
        JLabel titleLabel = new JLabel("Satıcı Profil Bilgileri");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 20));
        headerPanel.add(titleLabel);
        add(headerPanel, BorderLayout.NORTH);

        // İçerik Paneli
        JPanel contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ad Alanı
        gbc.gridx = 0;
        gbc.gridy = 0;
        contentPanel.add(new JLabel("Ad:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(user.getName(), 20);
        nameField.setEditable(false);
        contentPanel.add(nameField, gbc);

        // Email Alanı
        gbc.gridx = 0;
        gbc.gridy = 1;
        contentPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(user.getEmail(), 20);
        emailField.setEditable(false);
        contentPanel.add(emailField, gbc);

        // Restaurant Adı Alanı
        gbc.gridx = 0;
        gbc.gridy = 2;
        contentPanel.add(new JLabel("Restaurant Adı:"), gbc);

        gbc.gridx = 1;
        restaurantNameField = new JTextField(user.getRestaurantName(), 20);
        restaurantNameField.setEditable(false);
        contentPanel.add(restaurantNameField, gbc);

        // İletişim Bilgisi Alanı
        gbc.gridx = 0;
        gbc.gridy = 3;
        contentPanel.add(new JLabel("İletişim Bilgisi:"), gbc);

        gbc.gridx = 1;
        contactInfoField = new JTextField(user.getContactInfo(), 20);
        contactInfoField.setEditable(false);
        contentPanel.add(contactInfoField, gbc);

        // Adres Alanı
        gbc.gridx = 0;
        gbc.gridy = 4;
        contentPanel.add(new JLabel("Adres:"), gbc);

        gbc.gridx = 1;
        addressField = new JTextField(user.getAddress(), 20);
        addressField.setEditable(false);
        contentPanel.add(addressField, gbc);

        add(contentPanel, BorderLayout.CENTER);

        // Buton Paneli
        JPanel buttonPanel = new JPanel(new FlowLayout());
        editButton = createStyledButton("Düzenle", new Color(255, 193, 7));
        editButton.addActionListener(e -> toggleEditMode());
        buttonPanel.add(editButton);

        saveButton = createStyledButton("Kaydet", new Color(76, 175, 80));
        saveButton.setEnabled(false);
        saveButton.addActionListener(e -> saveProfile());
        buttonPanel.add(saveButton);

        cancelButton = createStyledButton("İptal", new Color(244, 67, 54));
        cancelButton.setEnabled(false);
        cancelButton.addActionListener(e -> cancelEdit());
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    // Buton stilini ayarlayan yardımcı metot
    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(100, 40));
        return button;
    }

    // Düzenleme Modunu Aç/Kapat
    private void toggleEditMode() {
        isEditing = !isEditing;
        nameField.setEditable(isEditing);
        emailField.setEditable(isEditing);
        restaurantNameField.setEditable(isEditing);
        contactInfoField.setEditable(isEditing);
        addressField.setEditable(isEditing);

        editButton.setEnabled(!isEditing);
        saveButton.setEnabled(isEditing);
        cancelButton.setEnabled(isEditing);
    }

    // Profili Kaydetme
    private void saveProfile() {
        user.setName(nameField.getText());
        user.setEmail(emailField.getText());
        user.setRestaurantName(restaurantNameField.getText());
        user.setContactInfo(contactInfoField.getText());
        user.setAddress(addressField.getText());

        UserDAO userDAO = new UserDAO();
        if (userDAO.updateUser(user)) {  // updateUser metodu UserDAO'da tanımlı olmalı
            JOptionPane.showMessageDialog(this, "Profil başarıyla güncellendi.");
            toggleEditMode();
        } else {
            JOptionPane.showMessageDialog(this, "Profil güncellenirken bir hata oluştu.");
        }
    }

    // Düzenleme İşleminden Vazgeçme
    private void cancelEdit() {
        nameField.setText(user.getName());
        emailField.setText(user.getEmail());
        restaurantNameField.setText(user.getRestaurantName());
        contactInfoField.setText(user.getContactInfo());
        addressField.setText(user.getAddress());

        toggleEditMode();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SellerProfileForm(new User()).setVisible(true));
    }
}
