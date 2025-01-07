package ui;

import models.User;
import javax.swing.*;
import java.awt.*;

public class ProfileForm extends JPanel {
    private User user;
    private JTextField nameField;
    private JTextField emailField;
    private JTextField addressField;
    private JButton editButton;
    private JButton saveButton;
    private boolean isEditing = false;
    private Runnable onSaveCallback;

    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Arial", Font.BOLD, 14);
    private static final Font FIELD_FONT = new Font("Arial", Font.PLAIN, 14);

    public ProfileForm(User user, Runnable onSaveCallback) {
        this.user = user;
        this.onSaveCallback = onSaveCallback;

        setupPanel();
        createComponents();
    }

    private void setupPanel() {
        setLayout(new BorderLayout(20, 20));
        setBackground(new Color(245, 245, 245));
        setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
    }

    private void createComponents() {
        // Header
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Form
        add(createFormPanel(), BorderLayout.CENTER);

        // Buttons
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Profil Bilgileri");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel);

        return panel;
    }

    private JPanel createFormPanel() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(new Color(245, 245, 245));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 0, 10, 0);
        gbc.gridwidth = 1;
        gbc.weightx = 0.3;

        // Ad Alanı
        gbc.gridy = 0;
        gbc.gridx = 0;
        panel.add(createLabel("Ad:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        nameField = createTextField(user.getName());
        panel.add(nameField, gbc);

        // Email Alanı
        gbc.gridy = 1;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        panel.add(createLabel("Email:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        emailField = createTextField(user.getEmail());
        panel.add(emailField, gbc);

        // Adres Alanı
        gbc.gridy = 2;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        panel.add(createLabel("Adres:"), gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        addressField = createTextField(user.getAddress());
        panel.add(addressField, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(new Color(245, 245, 245));

        editButton = createStyledButton("Düzenle", PRIMARY_COLOR);
        editButton.addActionListener(e -> toggleEditMode());
        panel.add(editButton);

        saveButton = createStyledButton("Kaydet", SUCCESS_COLOR);
        saveButton.addActionListener(e -> saveProfile());
        saveButton.setVisible(false);
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
        addressField.setEditable(isEditing);

        editButton.setVisible(!isEditing);
        saveButton.setVisible(isEditing);

        if (isEditing) {
            nameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            emailField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            addressField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(PRIMARY_COLOR),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        } else {
            nameField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            emailField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
            addressField.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(new Color(200, 200, 200)),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        }
    }

    private void saveProfile() {
        String newName = nameField.getText().trim();
        String newEmail = emailField.getText().trim();
        String newAddress = addressField.getText().trim();

        if (newName.isEmpty() || newEmail.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "Lütfen gerekli alanları doldurun.",
                    "Hata",
                    JOptionPane.ERROR_MESSAGE);
            return;
        }

        user.setName(newName);
        user.setEmail(newEmail);
        user.setAddress(newAddress);

        JOptionPane.showMessageDialog(this,
                "Profil bilgileriniz başarıyla güncellendi.",
                "Başarılı",
                JOptionPane.INFORMATION_MESSAGE);

        toggleEditMode();

        if (onSaveCallback != null) {
            onSaveCallback.run();
        }
    }
}