package ui;

import javax.swing.*;
import java.awt.*;
import models.User;
import dao.UserDAO;

public class SellerProfileForm extends JFrame {
    private User user;
    private UserDAO userDAO;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField restaurantNameField;
    private JTextField contactInfoField;
    private JTextField addressField;
    private boolean isEditing = false;

    // UI Constants
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 14);

    public SellerProfileForm(User user) {
        this.user = user;
        this.userDAO = new UserDAO();

        setupFrame();
        createComponents();
    }

    private void setupFrame() {
        setTitle("Satıcı Profili");
        setSize(600, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void createComponents() {
        setLayout(new BorderLayout(20, 20));
        ((JPanel)getContentPane()).setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));

        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Form
        add(createFormPanel(), BorderLayout.CENTER);

        // Buttons
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(BACKGROUND_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));

        JLabel titleLabel = new JLabel("Profil Bilgileri");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = 1;
        gbc.weightx = 1.0;

        // Ad
        gbc.gridy = 0;
        panel.add(createLabel("Ad:"), gbc);

        gbc.gridy = 1;
        nameField = createTextField(user.getName());
        panel.add(nameField, gbc);

        // Email
        gbc.gridy = 2;
        panel.add(createLabel("Email:"), gbc);

        gbc.gridy = 3;
        emailField = createTextField(user.getEmail());
        panel.add(emailField, gbc);

        // Restaurant Adı
        gbc.gridy = 4;
        panel.add(createLabel("Restaurant Adı:"), gbc);

        gbc.gridy = 5;
        restaurantNameField = createTextField(user.getRestaurantName());
        panel.add(restaurantNameField, gbc);

        // İletişim Bilgisi
        gbc.gridy = 6;
        panel.add(createLabel("İletişim Bilgisi:"), gbc);

        gbc.gridy = 7;
        contactInfoField = createTextField(user.getContactInfo());
        panel.add(contactInfoField, gbc);

        // Adres
        gbc.gridy = 8;
        panel.add(createLabel("Adres:"), gbc);

        gbc.gridy = 9;
        addressField = createTextField(user.getAddress());
        panel.add(addressField, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(BACKGROUND_COLOR);

        JButton editButton = createStyledButton("Düzenle", PRIMARY_COLOR);
        editButton.addActionListener(e -> toggleEditMode());

        JButton saveButton = createStyledButton("Kaydet", SUCCESS_COLOR);
        saveButton.addActionListener(e -> saveProfile());
        saveButton.setVisible(false);

        panel.add(editButton);
        panel.add(saveButton);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        return label;
    }

    private JTextField createTextField(String text) {
        JTextField field = new JTextField(text);
        field.setFont(FIELD_FONT);
        field.setEditable(false);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void toggleEditMode() {
        isEditing = !isEditing;

        nameField.setEditable(isEditing);
        emailField.setEditable(isEditing);
        restaurantNameField.setEditable(isEditing);
        contactInfoField.setEditable(isEditing);
        addressField.setEditable(isEditing);

        // Update field borders based on edit mode
        Color borderColor = isEditing ? PRIMARY_COLOR : new Color(200, 200, 200);
        updateFieldBorders(borderColor);

        // Toggle button visibility
        for (Component comp : getContentPane().getComponents()) {
            if (comp instanceof JPanel) {
                for (Component button : ((JPanel) comp).getComponents()) {
                    if (button instanceof JButton) {
                        JButton btn = (JButton) button;
                        if (btn.getText().equals("Düzenle")) {
                            btn.setVisible(!isEditing);
                        } else if (btn.getText().equals("Kaydet")) {
                            btn.setVisible(isEditing);
                        }
                    }
                }
            }
        }
    }

    private void updateFieldBorders(Color color) {
        JTextField[] fields = {nameField, emailField, restaurantNameField, contactInfoField, addressField};
        for (JTextField field : fields) {
            field.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(color),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        }
    }

    private void saveProfile() {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newRestaurantName = restaurantNameField.getText().trim();
        String newContactInfo = contactInfoField.getText().trim();
        String newAddress = addressField.getText().trim();

        // Validation
        if (newName.isEmpty() || newEmail.isEmpty() || newRestaurantName.isEmpty() ||
                newContactInfo.isEmpty() || newAddress.isEmpty()) {
            showError("Lütfen tüm alanları doldurun.");
            return;
        }

        if (!isValidEmail(newEmail)) {
            showError("Geçerli bir email adresi girin.");
            return;
        }

        // Update user object
        user.setName(newName);
        user.setEmail(newEmail);
        user.setRestaurantName(newRestaurantName);
        user.setContactInfo(newContactInfo);
        user.setAddress(newAddress);

        // Save to database
        if (userDAO.updateUser(user)) {
            JOptionPane.showMessageDialog(this,
                    "Profil bilgileriniz başarıyla güncellendi.",
                    "Başarılı",
                    JOptionPane.INFORMATION_MESSAGE);
            toggleEditMode();
        } else {
            showError("Profil güncellenirken bir hata oluştu.");
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