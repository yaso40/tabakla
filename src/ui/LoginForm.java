package ui;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import models.User;
import dao.UserDAO;

public class LoginForm extends JFrame {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JCheckBox rememberMeCheckBox;
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 24);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);

    public LoginForm() {
        setTitle("Giriş Yap");
        setSize(400, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(BACKGROUND_COLOR);

        add(createMainPanel(), BorderLayout.CENTER);
        loadRememberedEmail();
    }

    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new GridBagLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 20, 10, 20);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Logo veya başlık
        JLabel titleLabel = new JLabel("Hoş Geldiniz", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.gridwidth = 2;
        gbc.insets = new Insets(30, 20, 30, 20);
        mainPanel.add(titleLabel, gbc);

        // Email alanı
        gbc.gridy++;
        gbc.gridwidth = 1;
        gbc.insets = new Insets(10, 20, 5, 20);
        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(LABEL_FONT);
        mainPanel.add(emailLabel, gbc);

        gbc.gridy++;
        emailField = createStyledTextField();
        mainPanel.add(emailField, gbc);

        // Şifre alanı
        gbc.gridy++;
        gbc.insets = new Insets(15, 20, 5, 20);
        JLabel passwordLabel = new JLabel("Şifre:");
        passwordLabel.setFont(LABEL_FONT);
        mainPanel.add(passwordLabel, gbc);

        gbc.gridy++;
        passwordField = createStyledPasswordField();
        mainPanel.add(passwordField, gbc);

        // Beni Hatırla
        gbc.gridy++;
        gbc.insets = new Insets(15, 20, 15, 20);
        rememberMeCheckBox = new JCheckBox("Beni Hatırla");
        rememberMeCheckBox.setFont(LABEL_FONT);
        rememberMeCheckBox.setBackground(BACKGROUND_COLOR);
        mainPanel.add(rememberMeCheckBox, gbc);

        // Butonlar
        gbc.gridy++;
        gbc.insets = new Insets(20, 20, 10, 20);
        JButton loginButton = createStyledButton("Giriş Yap", PRIMARY_COLOR);
        loginButton.addActionListener(e -> loginUser());
        mainPanel.add(loginButton, gbc);

        gbc.gridy++;
        gbc.insets = new Insets(10, 20, 30, 20);
        JButton registerButton = createStyledButton("Kayıt Ol", new Color(46, 139, 87));
        registerButton.addActionListener(e -> openRegisterForm());
        mainPanel.add(registerButton, gbc);

        return mainPanel;
    }

    private JTextField createStyledTextField() {
        JTextField field = new JTextField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 40));
        field.setMinimumSize(new Dimension(300, 40));  // Minimum boyut ekledik
        field.setMaximumSize(new Dimension(300, 40));  // Maximum boyut ekledik
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)  // İç padding'i artırdık
        ));
        return field;
    }

    private JPasswordField createStyledPasswordField() {
        JPasswordField field = new JPasswordField();
        field.setFont(new Font("Arial", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 40));
        field.setMinimumSize(new Dimension(300, 40));  // Minimum boyut ekledik
        field.setMaximumSize(new Dimension(300, 40));  // Maximum boyut ekledik
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
                BorderFactory.createEmptyBorder(8, 15, 8, 15)  // İç padding'i artırdık
        ));
        return field;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(bgColor);  // Yazı rengi arka plan rengi olsun
        button.setBackground(Color.WHITE);  // Arka plan beyaz olsun
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(bgColor, 1),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));

        // Hover effect
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(bgColor);
                button.setForeground(Color.WHITE);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(Color.WHITE);
                button.setForeground(bgColor);
            }
        });

        return button;
    }

    private void loginUser() {
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());

        if (email.isEmpty() || password.isEmpty()) {
            showError("Lütfen tüm alanları doldurun.");
            return;
        }

        UserDAO userDAO = new UserDAO();
        User user = userDAO.login(email, password);

        if (user != null) {
            handleRememberMe(email);
            openAppropriateWindow(user);
        } else {
            showError("Geçersiz email veya şifre.");
        }
    }

    private void openAppropriateWindow(User user) {
        if ("buyer".equals(user.getUserType())) {
            new MainPage(user).setVisible(true);
        } else if ("seller".equals(user.getUserType())) {
            new SellerPage(user).setVisible(true);
        } else {
            showError("Geçersiz kullanıcı tipi.");
            return;
        }
        this.dispose();
    }

    private void openRegisterForm() {
        new RegisterForm().setVisible(true);
    }

    private void handleRememberMe(String email) {
        if (rememberMeCheckBox.isSelected()) {
            saveEmail(email);
        } else {
            clearSavedEmail();
        }
    }

    private void saveEmail(String email) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("remember_me.txt"))) {
            writer.write(email);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadRememberedEmail() {
        try (BufferedReader reader = new BufferedReader(new FileReader("remember_me.txt"))) {
            String savedEmail = reader.readLine();
            if (savedEmail != null && !savedEmail.isEmpty()) {
                emailField.setText(savedEmail);
                rememberMeCheckBox.setSelected(true);
            }
        } catch (IOException ignored) {}
    }

    private void clearSavedEmail() {
        File file = new File("remember_me.txt");
        if (file.exists()) {
            file.delete();
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this, message, "Hata", JOptionPane.ERROR_MESSAGE);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }

        SwingUtilities.invokeLater(() -> {
            new LoginForm().setVisible(true);
        });
    }
}