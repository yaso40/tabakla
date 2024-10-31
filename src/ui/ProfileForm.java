// src/ui/ProfileEdit.java
package ui;

import models.User;

import javax.swing.*;
import java.awt.*;

public class ProfileForm extends JPanel {
    private User user;
    private JTextField nameField;
    private JTextField emailField;
    private JButton editButton;
    private boolean editing = false;

    public ProfileForm(User user) {
        this.user = user;
        setLayout(new GridLayout(0, 2));

        add(new JLabel("Ad: "));
        nameField = new JTextField(user.getName());
        nameField.setEditable(false); // Başlangıçta düzenlenemez
        add(nameField);

        add(new JLabel("Email: "));
        emailField = new JTextField(user.getEmail());
        emailField.setEditable(false); // Başlangıçta düzenlenemez
        add(emailField);

        editButton = new JButton("Düzenle");
        editButton.addActionListener(e -> toggleEditing());
        add(editButton);
    }

    private void toggleEditing() {
        editing = !editing;
        nameField.setEditable(editing);
        emailField.setEditable(editing);
        editButton.setText(editing ? "Kaydet" : "Düzenle");

        if (!editing) {
            user.setName(nameField.getText());
            user.setEmail(emailField.getText());
            JOptionPane.showMessageDialog(this, "Profil güncellendi.");
        }
    }
}
