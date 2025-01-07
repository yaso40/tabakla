package ui;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO;
import models.User;

public class RegisterForm extends JFrame {
    private JTextField nameField;
    private JTextField emailField;
    private JTextField restaurantNameField;
    private JTextField contactInfoField;
    private JTextField addressField;
    private JPasswordField passwordField;
    private JPasswordField confirmPasswordField;
    private boolean isRestaurant = false;
    private JScrollPane formScrollPane;

    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font BUTTON_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 14);

    public RegisterForm() {
        setupFrame();
        createComponents();
    }

    private void setupFrame() {
        setTitle("Kayıt Ol");
        setSize(500, 700);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(new Color(245, 245, 245));
    }

    private void createComponents() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));

        // Header Panel
        mainPanel.add(createHeaderPanel(), BorderLayout.NORTH);

        // Content Panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(new Color(245, 245, 245));

        // Type Selection Panel
        contentPanel.add(createTypeSelectionPanel(), BorderLayout.NORTH);

        // Form ScrollPane
        formScrollPane = new JScrollPane();
        formScrollPane.setBorder(null);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        contentPanel.add(formScrollPane, BorderLayout.CENTER);

        mainPanel.add(contentPanel, BorderLayout.CENTER);
        add(mainPanel);

        showUserTypeForm();
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));

        JLabel titleLabel = new JLabel("Yeni Hesap Oluştur");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel);

        return panel;
    }

    private JPanel createTypeSelectionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(new Color(245, 245, 245));

        JButton customerButton = createStyledButton("Müşteri Olarak Kayıt Ol", PRIMARY_COLOR);
        customerButton.addActionListener(e -> {
            isRestaurant = false;
            showUserTypeForm();
        });

        JButton restaurantButton = createStyledButton("Restaurant Olarak Kayıt Ol", SUCCESS_COLOR);
        restaurantButton.addActionListener(e -> {
            isRestaurant = true;
            showUserTypeForm();
        });

        panel.add(customerButton);
        panel.add(restaurantButton);

        return panel;
    }

    private void showUserTypeForm() {
        JPanel formPanel = createFormPanel();
        formPanel.setPreferredSize(new Dimension(400, isRestaurant ? 700 : 500));
        formScrollPane.setViewportView(formPanel);
        formScrollPane.revalidate();
        formScrollPane.repaint();
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        panel.setBorder(BorderFactory.createEmptyBorder(20, 40, 20, 40));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;
        gbc.gridx = 0;

        int gridy = 0;

        // Ad
        panel.add(createLabel("Ad:"), gbc);
        gbc.gridy = ++gridy;
        nameField = createTextField();
        panel.add(nameField, gbc);

        // Email
        gbc.gridy = ++gridy;
        panel.add(createLabel("Email:"), gbc);
        gbc.gridy = ++gridy;
        emailField = createTextField();
        panel.add(emailField, gbc);

        // Restaurant specific fields
        if (isRestaurant) {
            gbc.gridy = ++gridy;
            panel.add(createLabel("Restaurant Adı:"), gbc);
            gbc.gridy = ++gridy;
            restaurantNameField = createTextField();
            panel.add(restaurantNameField, gbc);

            gbc.gridy = ++gridy;
            panel.add(createLabel("İletişim Bilgisi:"), gbc);
            gbc.gridy = ++gridy;
            contactInfoField = createTextField();
            panel.add(contactInfoField, gbc);

            gbc.gridy = ++gridy;
            panel.add(createLabel("Adres:"), gbc);
            gbc.gridy = ++gridy;
            addressField = createTextField();
            panel.add(addressField, gbc);
        }

        // Şifre
        gbc.gridy = ++gridy;
        panel.add(createLabel("Şifre:"), gbc);
        gbc.gridy = ++gridy;
        passwordField = createPasswordField();
        panel.add(passwordField, gbc);

        // Şifre Tekrar
        gbc.gridy = ++gridy;
        panel.add(createLabel("Şifre Tekrar:"), gbc);
        gbc.gridy = ++gridy;
        confirmPasswordField = createPasswordField();
        panel.add(confirmPasswordField, gbc);

        // Register Button
        gbc.gridy = ++gridy;
        gbc.insets = new Insets(30, 0, 10, 0);
        JButton registerButton = createStyledButton("Kayıt Ol", SUCCESS_COLOR);
        registerButton.addActionListener(e -> registerUser());
        panel.add(registerButton, gbc);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FIELD_FONT);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(FIELD_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JPasswordField createPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(FIELD_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color color) {
        JButton button = new JButton(text);
        button.setFont(BUTTON_FONT);
        button.setForeground(color);
        button.setBackground(Color.WHITE);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(color, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(color);
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(color);
            }
        });

        return button;
    }

    private void registerUser() {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            showError("Lütfen tüm alanları doldurun.");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Şifreler eşleşmiyor.");
            return;
        }

        if (!isValidEmail(email)) {
            showError("Geçerli bir email adresi girin.");
            return;
        }

        User user = new User();
        user.setName(name);
        user.setEmail(email);
        user.setPassword(password);
        user.setUserType(isRestaurant ? "seller" : "buyer");

        if (isRestaurant) {
            String restaurantName = restaurantNameField.getText().trim();
            String contactInfo = contactInfoField.getText().trim();
            String address = addressField.getText().trim();

            if (restaurantName.isEmpty() || contactInfo.isEmpty() || address.isEmpty()) {
                showError("Lütfen tüm restaurant bilgilerini doldurun.");
                return;
            }

            user.setRestaurantName(restaurantName);
            user.setContactInfo(contactInfo);
            user.setAddress(address);
        }

        UserDAO userDAO = new UserDAO();
        if (userDAO.register(user)) {
            JOptionPane.showMessageDialog(this,
                    "Kayıt başarıyla tamamlandı!",
                    "Başarılı",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showError("Kayıt işlemi başarısız oldu.");
        }
    }

    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Hata",
                JOptionPane.ERROR_MESSAGE);
    }
}