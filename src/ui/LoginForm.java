// src/ui/LoginForm.java
package ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import models.User;
import dao.UserDAO;

public class LoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton loginButton, registerButton;
    private JCheckBox rememberMeCheckBox;

    public LoginForm() {
        setTitle("Giriş Yap");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());

        // Ana içerik paneli
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        contentPanel.add(createLoginPanel(), BorderLayout.CENTER);

        add(contentPanel, BorderLayout.CENTER);

        // "Beni Hatırla" seçeneği seçilmişse email'i otomatik doldurma
        loadRememberedEmail();
    }

    private JPanel createLoginPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(null);
        panel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Hoş Geldiniz");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 22));
        titleLabel.setBounds(130, 20, 200, 30);
        panel.add(titleLabel);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(50, 70, 80, 25);
        panel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(150, 70, 180, 25);
        panel.add(emailField);

        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setBounds(50, 110, 80, 25);
        panel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(150, 110, 180, 25);
        panel.add(passwordField);

        rememberMeCheckBox = new JCheckBox("Beni Hatırla");
        rememberMeCheckBox.setBounds(150, 140, 180, 25);
        panel.add(rememberMeCheckBox);

        loginButton = new JButton("Giriş Yap");
        loginButton.setBounds(50, 180, 120, 30);
        loginButton.setBackground(new Color(70, 130, 180));
        loginButton.setForeground(Color.WHITE);
        panel.add(loginButton);

        registerButton = new JButton("Kayıt Ol");
        registerButton.setBounds(210, 180, 120, 30);
        registerButton.setBackground(new Color(100, 200, 100));
        registerButton.setForeground(Color.WHITE);
        panel.add(registerButton);

        loginButton.addActionListener(e -> loginUser());
        registerButton.addActionListener(e -> new RegisterForm().setVisible(true));

        return panel;
    }

    private void loginUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(email, password);

        if (user != null) {
            // "Beni Hatırla" seçeneği seçilmişse email'i kaydetme
            if (rememberMeCheckBox.isSelected()) {
                rememberEmail(email);
            } else {
                clearRememberedEmail();
            }

            if (user.getUserType().equals("buyer")) {
                loadMainPage(user);
            } else if (user.getUserType().equals("seller")) {
                new SellerPage(user).setVisible(true);
                this.dispose();
            }
        } else {
            JOptionPane.showMessageDialog(this, "Geçersiz email veya şifre.");
        }
    }

    private void loadMainPage(User user) {
        new MainPage(user).setVisible(true);
        this.dispose();
    }

    private void rememberEmail(String email) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("remember_me.txt"))) {
            writer.write(email);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRememberedEmail() {
        try (BufferedReader reader = new BufferedReader(new FileReader("remember_me.txt"))) {
            String rememberedEmail = reader.readLine();
            if (rememberedEmail != null) {
                emailField.setText(rememberedEmail);
                rememberMeCheckBox.setSelected(true);
            }
        } catch (IOException e) {
            System.out.println("Daha önce kaydedilmiş bir email bulunamadı.");
        }
    }

    private void clearRememberedEmail() {
        File file = new File("remember_me.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new LoginForm().setVisible(true));
    }
}
