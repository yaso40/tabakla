// src/ui/RegisterForm.java
package ui;

import javax.swing.*;
import java.awt.*;
import dao.UserDAO;
import models.User;

public class RegisterForm extends JFrame {
    private JTextField nameField, emailField;
    private JPasswordField passwordField;
    private JComboBox<String> userTypeCombo;
    private JButton registerButton;

    public RegisterForm() {
        setTitle("Kayıt Ol");
        setSize(300, 250);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(5, 2));

        add(new JLabel("Ad:"));
        nameField = new JTextField();
        add(nameField);

        add(new JLabel("Email:"));
        emailField = new JTextField();
        add(emailField);

        add(new JLabel("Şifre:"));
        passwordField = new JPasswordField();
        add(passwordField);

        add(new JLabel("Kullanıcı Tipi:"));
        userTypeCombo = new JComboBox<>(new String[]{"buyer", "seller"});
        add(userTypeCombo);

        registerButton = new JButton("Kayıt Ol");
        registerButton.addActionListener(e -> registerUser());
        add(registerButton);
    }

    private void registerUser() {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String userType = (String) userTypeCombo.getSelectedItem();

        User user = new User(0, name, email, password, userType);
        UserDAO userDAO = new UserDAO();

        if (userDAO.register(user)) {
            JOptionPane.showMessageDialog(this, "Kayıt başarılı! Giriş yapabilirsiniz.");
            dispose();
        } else {
            JOptionPane.showMessageDialog(this, "Kayıt başarısız.");
        }
    }
}
