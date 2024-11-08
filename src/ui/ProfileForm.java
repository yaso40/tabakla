// src/ui/ProfileForm.java
package ui;

import models.User;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;

public class ProfileForm extends JPanel {
    private User user;
    private JTextField nameField;
    private JTextField emailField;
    private JButton saveButton;
    private boolean editing = false;

    public ProfileForm(User user, Runnable onClose) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));
        setBackground(Color.WHITE);

        JLabel titleLabel = new JLabel("Profil Düzenle", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        add(titleLabel, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridLayout(0, 2, 10, 10));
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        formPanel.add(new JLabel("Ad:"));
        nameField = new JTextField(user.getName());
        nameField.setEditable(false);
        formPanel.add(nameField);

        formPanel.add(new JLabel("Email:"));
        emailField = new JTextField(user.getEmail());
        emailField.setEditable(false);
        formPanel.add(emailField);

        add(formPanel, BorderLayout.CENTER);

        saveButton = new JButton("Düzenle");
        saveButton.setBackground(new Color(0, 123, 255));
        saveButton.setForeground(Color.WHITE);
        saveButton.setFocusPainted(false);
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.addActionListener(e -> toggleEditing(onClose));
        add(saveButton, BorderLayout.SOUTH);
    }

    private void toggleEditing(Runnable onClose) {
        editing = !editing;
        nameField.setEditable(editing);
        emailField.setEditable(editing);
        saveButton.setText(editing ? "Kaydet" : "Düzenle");

        if (!editing) {
            user.setName(nameField.getText());
            user.setEmail(emailField.getText());
            JOptionPane.showMessageDialog(this, "Profil güncellendi.");
            onClose.run();
        }
    }
}
