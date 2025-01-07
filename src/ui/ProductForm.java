package ui;

import models.User;
import dao.ProductDAO;

import javax.swing.*;
import javax.swing.text.NumberFormatter;
import java.awt.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;

public class ProductForm extends JFrame {
    private User user;
    private ProductDAO productDAO;
    private JTextField productNameField;
    private JFormattedTextField productPriceField;
    private JComboBox<String> categoryComboBox;
    private JTextArea descriptionArea;

    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font LABEL_FONT = new Font("Arial", Font.PLAIN, 14);

    public ProductForm(User user) {
        this.user = user;
        this.productDAO = new ProductDAO();

        setupFrame();
        createComponents();
    }

    private void setupFrame() {
        setTitle("Yeni Ürün Ekle");
        setSize(500, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));
        getContentPane().setBackground(new Color(245, 245, 245));
    }

    private void createComponents() {
        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Form Panel
        add(createFormPanel(), BorderLayout.CENTER);

        // Button Panel
        add(createButtonPanel(), BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        panel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Yeni Ürün Ekle");
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel);

        return panel;
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

        // Ürün Adı
        gbc.gridy = 0;
        panel.add(createLabel("Ürün Adı:"), gbc);

        gbc.gridy = 1;
        productNameField = createTextField();
        panel.add(productNameField, gbc);

        // Fiyat
        gbc.gridy = 2;
        panel.add(createLabel("Fiyat:"), gbc);

        gbc.gridy = 3;
        productPriceField = createPriceField();
        panel.add(createPricePanel(productPriceField), gbc);

        // Kategori
        gbc.gridy = 4;
        panel.add(createLabel("Kategori:"), gbc);

        gbc.gridy = 5;
        categoryComboBox = createCategoryComboBox();
        panel.add(categoryComboBox, gbc);

        // Açıklama
        gbc.gridy = 6;
        panel.add(createLabel("Açıklama:"), gbc);

        gbc.gridy = 7;
        gbc.weighty = 1.0;
        gbc.fill = GridBagConstraints.BOTH;
        descriptionArea = createDescriptionArea();
        JScrollPane scrollPane = new JScrollPane(descriptionArea);
        scrollPane.setPreferredSize(new Dimension(0, 100));
        panel.add(scrollPane, gbc);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 20));
        panel.setBackground(new Color(245, 245, 245));

        JButton saveButton = createStyledButton("Kaydet", SUCCESS_COLOR);
        saveButton.addActionListener(e -> saveProduct());

        JButton cancelButton = createStyledButton("İptal", DANGER_COLOR);
        cancelButton.addActionListener(e -> dispose());

        panel.add(saveButton);
        panel.add(cancelButton);

        return panel;
    }

    private JLabel createLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(LABEL_FONT);
        return label;
    }

    private JTextField createTextField() {
        JTextField field = new JTextField();
        field.setFont(LABEL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JFormattedTextField createPriceField() {
        NumberFormat format = new DecimalFormat("#,##0.00");
        NumberFormatter formatter = new NumberFormatter(format);
        formatter.setValueClass(Double.class);
        formatter.setMinimum(0.0);
        formatter.setAllowsInvalid(false);

        JFormattedTextField field = new JFormattedTextField(formatter);
        field.setFont(LABEL_FONT);
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return field;
    }

    private JPanel createPricePanel(JFormattedTextField priceField) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        panel.add(priceField, BorderLayout.CENTER);

        JLabel currencyLabel = new JLabel("₺");
        currencyLabel.setFont(LABEL_FONT);
        currencyLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
        panel.add(currencyLabel, BorderLayout.EAST);

        return panel;
    }

    private JComboBox<String> createCategoryComboBox() {
        String[] categories = {"Burger", "Pizza", "Döner", "Tatlı", "İçecek"};
        JComboBox<String> comboBox = new JComboBox<>(categories);
        comboBox.setFont(LABEL_FONT);
        comboBox.setBackground(Color.WHITE);
        return comboBox;
    }

    private JTextArea createDescriptionArea() {
        JTextArea area = new JTextArea();
        area.setFont(LABEL_FONT);
        area.setLineWrap(true);
        area.setWrapStyleWord(true);
        area.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(200, 200, 200)),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        return area;
    }

    private JButton createStyledButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 14));
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void saveProduct() {
        String name = productNameField.getText().trim();
        String description = descriptionArea.getText().trim();
        String category = (String) categoryComboBox.getSelectedItem();

        if (name.isEmpty() || productPriceField.getValue() == null) {
            showError("Lütfen gerekli alanları doldurun.");
            return;
        }

        double price = ((Number) productPriceField.getValue()).doubleValue();

        if (productDAO.addProduct(name, price, category, description, user.getUserId())) {
            JOptionPane.showMessageDialog(this,
                    "Ürün başarıyla eklendi.",
                    "Başarılı",
                    JOptionPane.INFORMATION_MESSAGE);
            dispose();
        } else {
            showError("Ürün eklenirken bir hata oluştu.");
        }
    }

    private void showError(String message) {
        JOptionPane.showMessageDialog(this,
                message,
                "Hata",
                JOptionPane.ERROR_MESSAGE);
    }
}