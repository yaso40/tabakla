// src/ui/OrderManagement.java
package ui;

import models.User;
import javax.swing.*;
import java.awt.*;
import dao.OrderDAO;
import java.util.Timer;
import java.util.TimerTask;

public class OrderManagement extends JPanel {
    private User user;
    private DefaultListModel<String> orderListModel;
    private JList<String> orderList;
    private JButton confirmButton, cancelButton;

    public OrderManagement(User user) {
        this.user = user;
        setLayout(new BorderLayout(10, 10));

        // Başlık
        JLabel titleLabel = new JLabel("Aktif Siparişler", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Sipariş listesi
        orderListModel = new DefaultListModel<>();
        orderList = new JList<>(orderListModel);
        orderList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        add(new JScrollPane(orderList), BorderLayout.CENTER);

        // Buton Paneli
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 10, 10));

        confirmButton = new JButton("Onayla");
        styleButton(confirmButton, new Color(76, 175, 80));
        confirmButton.addActionListener(e -> confirmOrder());

        cancelButton = new JButton("İptal Et");
        styleButton(cancelButton, new Color(244, 67, 54));
        cancelButton.addActionListener(e -> cancelOrder());

        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);
        add(buttonPanel, BorderLayout.SOUTH);

        // Siparişleri ilk yükleme ve her 10 saniyede bir güncelleme
        loadOrders();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                loadOrders();
            }
        }, 0, 10000); // 10 saniyede bir güncelle
    }

    // Siparişleri veritabanından yükleyen metot
    private void loadOrders() {
        OrderDAO orderDAO = new OrderDAO();
        String[] orders = orderDAO.getActiveOrders(user.getUserId());
        orderListModel.clear();
        for (String order : orders) {
            orderListModel.addElement(order);
        }
    }

    // Siparişi onaylayan metot
    private void confirmOrder() {
        String selectedOrder = orderList.getSelectedValue();
        if (selectedOrder != null) {
            // Onaylama işlemleri
            JOptionPane.showMessageDialog(this, "Sipariş onaylandı.");
            orderListModel.removeElement(selectedOrder);
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen onaylamak için bir sipariş seçin.");
        }
    }

    // Siparişi iptal eden metot
    private void cancelOrder() {
        String selectedOrder = orderList.getSelectedValue();
        if (selectedOrder != null) {
            // Burada order_id'yi ayrıştırmanız gerekebilir
            int orderId = getOrderIdFromSelection(selectedOrder);
            OrderDAO orderDAO = new OrderDAO();
            if (orderDAO.cancelOrder(orderId)) {
                JOptionPane.showMessageDialog(this, "Sipariş iptal edildi.");
                orderListModel.removeElement(selectedOrder);
            } else {
                JOptionPane.showMessageDialog(this, "Sipariş iptal edilemedi.");
            }
        } else {
            JOptionPane.showMessageDialog(this, "Lütfen iptal etmek için bir sipariş seçin.");
        }
    }

    // Seçilen sipariş metninden order_id'yi çıkarmak için yardımcı metot
    private int getOrderIdFromSelection(String orderText) {
        // Örnek: "Sipariş 123 - Burger" gibi bir metinden "123" alır
        String orderIdStr = orderText.split(" ")[1];
        return Integer.parseInt(orderIdStr);
    }

    // Butonlara stil uygulayan yardımcı metot
    private void styleButton(JButton button, Color bgColor) {
        button.setFocusPainted(false);
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setFont(new Font("SansSerif", Font.BOLD, 14));
        button.setPreferredSize(new Dimension(100, 40));
        button.setBorder(BorderFactory.createEmptyBorder(5, 15, 5, 15));
    }
}
