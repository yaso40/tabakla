package ui;

import dao.Database;
import models.User;
import models.Order;
import dao.OrderDAO;
import dao.ProductDAO;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class SellerPage extends JFrame {
    private User user;
    private JPanel mainPanel;
    private DefaultTableModel orderTableModel;
    private JTable orderTable;
    private OrderDAO orderDAO;
    private ProductDAO productDAO;

    // UI Constants
    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color BACKGROUND_COLOR = new Color(245, 245, 245);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font REGULAR_FONT = new Font("Arial", Font.PLAIN, 14);

    public SellerPage(User user) {
        this.user = user;
        this.orderDAO = new OrderDAO();
        this.productDAO = new ProductDAO();

        setupFrame();
        createComponents();
    }

    private void setupFrame() {
        setTitle("Satıcı Paneli - " + user.getRestaurantName());
        setSize(1200, 800);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(BACKGROUND_COLOR);
    }

    private void createComponents() {
        setLayout(new BorderLayout());

        // Header Panel
        add(createHeaderPanel(), BorderLayout.NORTH);

        // Main Split Pane
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                createSidePanel(),
                createMainPanel());
        splitPane.setDividerLocation(250);
        splitPane.setDividerSize(1);
        add(splitPane, BorderLayout.CENTER);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(PRIMARY_COLOR);
        panel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 20));

        // Restaurant Name
        JLabel titleLabel = new JLabel(user.getRestaurantName());
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(Color.WHITE);
        panel.add(titleLabel, BorderLayout.WEST);

        // Right buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setOpaque(false);

        JButton profileButton = createHeaderButton("Profil");
        profileButton.addActionListener(e -> openProfileForm());

        JButton logoutButton = createHeaderButton("Çıkış");
        logoutButton.addActionListener(e -> logout());

        buttonPanel.add(profileButton);
        buttonPanel.add(logoutButton);
        panel.add(buttonPanel, BorderLayout.EAST);

        return panel;
    }

    private JPanel createSidePanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JButton addProductButton = createMenuButton("Yeni Ürün Ekle");
        addProductButton.addActionListener(e -> openProductForm());

        JButton ordersButton = createMenuButton("Siparişler");
        ordersButton.addActionListener(e -> showOrders());

        panel.add(addProductButton);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
        panel.add(ordersButton);
        panel.add(Box.createVerticalGlue());

        return panel;
    }

    private JPanel createMainPanel() {
        mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(BACKGROUND_COLOR);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        showOrders();

        return mainPanel;
    }

    private void showOrders() {
        mainPanel.removeAll();

        // Header Panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(BACKGROUND_COLOR);

        JLabel titleLabel = new JLabel("Siparişler");
        titleLabel.setFont(TITLE_FONT);
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton refreshButton = createActionButton("Tüm Siparişleri Yenile", PRIMARY_COLOR);
        refreshButton.addActionListener(e -> refreshOrdersFromDatabase());  // Yeni metot çağrılıyor
        headerPanel.add(refreshButton, BorderLayout.EAST);


        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Orders table
        String[] columns = {"Sipariş ID", "Ürün", "Miktar", "Müşteri", "Durum", "Tarih", "Müşteri Adresi"};
        orderTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        orderTable = new JTable(orderTableModel);
        orderTable.setFont(REGULAR_FONT);
        orderTable.setRowHeight(40);
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));

        // Tablo sütun genişliklerini ayarla
        orderTable.getColumnModel().getColumn(0).setPreferredWidth(70);  // Sipariş ID
        orderTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Ürün
        orderTable.getColumnModel().getColumn(2).setPreferredWidth(70);  // Miktar
        orderTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Müşteri
        orderTable.getColumnModel().getColumn(4).setPreferredWidth(200); // Adres
        orderTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Durum
        orderTable.getColumnModel().getColumn(6).setPreferredWidth(150); // Tarih

        // Hücre içeriğini ortala
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < orderTable.getColumnCount(); i++) {
            orderTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Tablo scroll pane
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(BACKGROUND_COLOR);

        JButton completeButton = createActionButton("Siparişi Tamamla", new Color(76, 175, 80));
        completeButton.addActionListener(e -> completeSelectedOrder());

        JButton cancelButton = createActionButton("Siparişi İptal Et", new Color(244, 67, 54));
        cancelButton.addActionListener(e -> cancelSelectedOrder());

        buttonPanel.add(completeButton);
        buttonPanel.add(cancelButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        loadOrders();
        mainPanel.revalidate();
        mainPanel.repaint();
    }

    private void loadOrders() {
        try {
            orderTableModel.setRowCount(0);
            List<Order> orders = orderDAO.getPendingOrdersForSeller(user.getUserId());

            for (Order order : orders) {
                Object[] row = {
                        order.getOrderId(),
                        order.getProductName(),
                        order.getQuantity(),
                        order.getBuyerName(),
                        order.getStatus(),
                        new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("tr", "TR")).format(order.getOrderDate()),
                        order.getBuyerAddress()
                };
                orderTableModel.addRow(row);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage("Siparişler yüklenirken hata oluştu: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }



    private void completeSelectedOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Lütfen bir sipariş seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = Integer.parseInt(orderTableModel.getValueAt(selectedRow, 0).toString());

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE orders SET status = 'delivered' WHERE order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, orderId);
                int result = stmt.executeUpdate();

                if (result > 0) {
                    orderTableModel.removeRow(selectedRow);  // **Sadece ilgili satırı kaldır**
                    showMessage("Sipariş başarıyla tamamlandı.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showMessage("Sipariş tamamlanamadı.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void cancelSelectedOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Lütfen bir sipariş seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = Integer.parseInt(orderTableModel.getValueAt(selectedRow, 0).toString());

        try (Connection conn = Database.getConnection()) {
            String sql = "UPDATE orders SET status = 'cancelled' WHERE order_id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, orderId);
                int result = stmt.executeUpdate();

                if (result > 0) {
                    orderTableModel.removeRow(selectedRow);  // **Sadece seçili siparişi kaldır**
                    showMessage("Sipariş başarıyla iptal edildi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    showMessage("Sipariş iptal edilemedi.", "Hata", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            showMessage("Veritabanı hatası: " + e.getMessage(), "Hata", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void refreshOrdersFromDatabase() {
        orderTableModel.setRowCount(0);
        List<Order> pendingOrders = orderDAO.getAllPendingOrders();  // Tüm satıcıların siparişlerini getirir.

        if (pendingOrders.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Beklemede sipariş bulunmamaktadır.", "Bilgi", JOptionPane.INFORMATION_MESSAGE);
        } else {
            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM yyyy HH:mm", new Locale("tr", "TR"));
            for (Order order : pendingOrders) {
                orderTableModel.addRow(new Object[]{
                        order.getOrderId(),
                        order.getProductName(),
                        order.getQuantity(),
                        order.getBuyerName(),
                        order.getStatus(),
                        sdf.format(order.getOrderDate()),
                        order.getBuyerAddress()
                });
            }
        }
        orderTable.revalidate();
        orderTable.repaint();
        JOptionPane.showMessageDialog(this, "Tüm bekleyen siparişler başarıyla yenilendi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
    }





    private void openProductForm() {
        new ProductForm(user).setVisible(true);
    }

    private void openProfileForm() {
        new SellerProfileForm(user).setVisible(true);
    }

    private void logout() {
        int response = JOptionPane.showConfirmDialog(this,
                "Çıkış yapmak istediğinize emin misiniz?",
                "Çıkış",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            dispose();
            new LoginForm().setVisible(true);
        }
    }

    // Helper methods for UI components
    private JButton createHeaderButton(String text) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(PRIMARY_COLOR);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setForeground(Color.DARK_GRAY);
        button.setBackground(Color.WHITE);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        button.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        return button;
    }

    private JButton createActionButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(REGULAR_FONT);
        button.setForeground(Color.WHITE);
        button.setBackground(bgColor);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        return button;
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    public void addOrder(String orderDetails) {
        loadOrders();
    }

    public void addOrderToTable(String[] rowData) {
        SwingUtilities.invokeLater(() -> {
            if (orderTableModel != null) {
                orderTableModel.addRow(rowData);
                orderTable.revalidate();
                orderTable.repaint();
            }
        });
    }
}