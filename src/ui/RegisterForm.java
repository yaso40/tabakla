// src/ui/RegisterForm.java
package ui;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO;
import models.User;

public class RegisterForm extends JFrame {
    private JTextField nameField, surnameField, restaurantNameField, emailField;
    private JPasswordField passwordField, confirmPasswordField;
    private JButton customerButton, restaurantButton, registerButton;
    private JPanel formPanel;
    private boolean isRestaurant;

    public RegisterForm() {
        setTitle("Kayıt Ol");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.setBackground(new Color(240, 240, 240));

        customerButton = createStyledButton("Müşteri Ol", new Color(70, 130, 180));
        customerButton.addActionListener(e -> switchToCustomerForm());

        restaurantButton = createStyledButton("Restaurant Ekle", new Color(200, 100, 100));
        restaurantButton.addActionListener(e -> switchToRestaurantForm());

        buttonPanel.add(customerButton);
        buttonPanel.add(restaurantButton);

        add(buttonPanel, BorderLayout.NORTH);

        formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(245, 245, 245));
        add(formPanel, BorderLayout.CENTER);

        // Varsayılan olarak müşteri formunu yükleyelim
        switchToCustomerForm();
    }

    private void switchToCustomerForm() {
        isRestaurant = false;
        setupFormFields();
    }

    private void switchToRestaurantForm() {
        isRestaurant = true;
        setupFormFields();
    }

    private void setupFormFields() {
        formPanel.removeAll();
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Ad Alanı
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Ad:"), gbc);

        gbc.gridx = 1;
        nameField = new JTextField(20);
        formPanel.add(nameField, gbc);

        // Soyad Alanı
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Soyad:"), gbc);

        gbc.gridx = 1;
        surnameField = new JTextField(20);
        formPanel.add(surnameField, gbc);

        // Email Alanı
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Email:"), gbc);

        gbc.gridx = 1;
        emailField = new JTextField(20);
        formPanel.add(emailField, gbc);

        // Restaurant Adı Alanı (Sadece restaurant için)
        if (isRestaurant) {
            gbc.gridx = 0;
            gbc.gridy = 3;
            formPanel.add(new JLabel("Restaurant Adı:"), gbc);

            gbc.gridx = 1;
            restaurantNameField = new JTextField(20);
            formPanel.add(restaurantNameField, gbc);
        }

        // Şifre Alanı
        gbc.gridx = 0;
        gbc.gridy = isRestaurant ? 4 : 3;
        formPanel.add(new JLabel("Şifre:"), gbc);

        gbc.gridx = 1;
        passwordField = new JPasswordField(20);
        formPanel.add(passwordField, gbc);

        // Şifre Doğrulama Alanı
        gbc.gridx = 0;
        gbc.gridy++;
        formPanel.add(new JLabel("Şifre Doğrulama:"), gbc);

        gbc.gridx = 1;
        confirmPasswordField = new JPasswordField(20);
        formPanel.add(confirmPasswordField, gbc);

        // Kayıt Ol Butonu
        gbc.gridx = 1;
        gbc.gridy++;
        registerButton = createStyledButton("Kayıt Ol", new Color(0, 255, 0));
        registerButton.addActionListener(e -> registerUser());
        formPanel.add(registerButton, gbc);

        formPanel.revalidate();
        formPanel.repaint();
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBackground(color);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(150, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
        return button;
    }

    private void registerUser() {
        String name = nameField.getText();
        String surname = surnameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(this, "Şifreler eşleşmiyor.");
            return;
        }

        String userType = isRestaurant ? "seller" : "buyer";
        String restaurantName = isRestaurant ? restaurantNameField.getText() : null;

        User user = new User(0, name + " " + surname, email, password, userType);
        user.setRestaurantName(restaurantName);

        UserDAO userDAO = new UserDAO();

        if (isRestaurant && !userDAO.isUniqueRestaurantName(restaurantName)) {
            JOptionPane.showMessageDialog(this, "Bu restaurant adı zaten mevcut. Lütfen farklı bir ad girin.");
            return;
        }

        if (userDAO.register(user)) {
            JOptionPane.showMessageDialog(this, "Kayıt başarılı! Giriş yapabilirsiniz.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Kayıt başarısız.");
        }
    }
}
