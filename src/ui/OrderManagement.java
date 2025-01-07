package ui;

import models.Order;
import models.User;
import dao.OrderDAO;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OrderManagement extends JPanel {
    private User user;
    private OrderDAO orderDAO;
    private JTable orderTable;
    private DefaultTableModel tableModel;
    private Timer refreshTimer;

    private static final Color PRIMARY_COLOR = new Color(70, 130, 180);
    private static final Color SUCCESS_COLOR = new Color(76, 175, 80);
    private static final Color DANGER_COLOR = new Color(244, 67, 54);
    private static final Font TITLE_FONT = new Font("Arial", Font.BOLD, 20);
    private static final Font TABLE_FONT = new Font("Arial", Font.PLAIN, 14);

    public OrderManagement(User user) {
        this.user = user;
        this.orderDAO = new OrderDAO();

        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        setBackground(new Color(245, 245, 245));

        createComponents();
        startAutoRefresh();
    }

    private void createComponents() {
        // Header Panel
        JPanel headerPanel = createHeaderPanel();
        add(headerPanel, BorderLayout.NORTH);

        // Table Panel
        JPanel tablePanel = createTablePanel();
        add(tablePanel, BorderLayout.CENTER);

        // Button Panel
        JPanel buttonPanel = createButtonPanel();
        add(buttonPanel, BorderLayout.SOUTH);
    }

    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(245, 245, 245));

        JLabel titleLabel = new JLabel("Aktif Siparişler", SwingConstants.CENTER);
        titleLabel.setFont(TITLE_FONT);
        titleLabel.setForeground(PRIMARY_COLOR);
        panel.add(titleLabel, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));

        // Table Model
        String[] columns = {"Sipariş ID", "Ürün", "Miktar", "Toplam Fiyat", "Tarih", "Durum"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        // Table
        orderTable = new JTable(tableModel);
        orderTable.setFont(TABLE_FONT);
        orderTable.setRowHeight(30);
        orderTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 14));
        orderTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        orderTable.setShowGrid(true);
        orderTable.setGridColor(new Color(230, 230, 230));

        // Scroll Pane
        JScrollPane scrollPane = new JScrollPane(orderTable);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        panel.setBackground(new Color(245, 245, 245));

        JButton confirmButton = createStyledButton("Siparişi Tamamla", SUCCESS_COLOR);
        confirmButton.addActionListener(e -> confirmSelectedOrder());

        JButton cancelButton = createStyledButton("Siparişi İptal Et", DANGER_COLOR);
        cancelButton.addActionListener(e -> cancelSelectedOrder());

        panel.add(confirmButton);
        panel.add(cancelButton);

        return panel;
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

    private void loadOrders() {
        tableModel.setRowCount(0);

        // Doğru metodu çağırıyoruz:
        List<Order> orders = orderDAO.getPendingOrdersForSeller(user.getUserId());

        for (Order order : orders) {
            Object[] row = {
                    order.getOrderId(),
                    order.getProductName(),
                    order.getQuantity(),
                    String.format("%.2f TL", order.getTotalPrice()),
                    order.getOrderDate(),
                    order.getStatus()
            };
            tableModel.addRow(row);
        }
    }


    private void confirmSelectedOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Lütfen bir sipariş seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        int response = JOptionPane.showConfirmDialog(this,
                "Seçili siparişi tamamlamak istediğinize emin misiniz?",
                "Onay",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            if (orderDAO.markOrderAsDelivered(orderId)) {
                showMessage("Sipariş başarıyla tamamlandı.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
            } else {
                showMessage("Sipariş tamamlanırken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void cancelSelectedOrder() {
        int selectedRow = orderTable.getSelectedRow();
        if (selectedRow == -1) {
            showMessage("Lütfen bir sipariş seçin.", "Uyarı", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int orderId = (int) tableModel.getValueAt(selectedRow, 0);
        int response = JOptionPane.showConfirmDialog(this,
                "Seçili siparişi iptal etmek istediğinize emin misiniz?",
                "Onay",
                JOptionPane.YES_NO_OPTION);

        if (response == JOptionPane.YES_OPTION) {
            if (orderDAO.cancelOrder(orderId)) {
                showMessage("Sipariş başarıyla iptal edildi.", "Başarılı", JOptionPane.INFORMATION_MESSAGE);
                loadOrders();
            } else {
                showMessage("Sipariş iptal edilirken bir hata oluştu.", "Hata", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void startAutoRefresh() {
        refreshTimer = new Timer();
        refreshTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> loadOrders());
            }
        }, 0, 30000); // Her 30 saniyede bir güncelle
    }

    private void showMessage(String message, String title, int messageType) {
        JOptionPane.showMessageDialog(this, message, title, messageType);
    }

    @Override
    public void removeNotify() {
        super.removeNotify();
        if (refreshTimer != null) {
            refreshTimer.cancel();
        }
    }
}