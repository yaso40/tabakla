// src/ui/OrderManagement.java
package ui;

import models.User;
import javax.swing.*;
import java.awt.*;
import dao.OrderDAO;

public class OrderManagement extends JPanel {
    private User user;

    public OrderManagement(User user) {
        this.user = user;
        setLayout(new BorderLayout());

        JLabel titleLabel = new JLabel("Aktif Siparişler", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
        add(titleLabel, BorderLayout.NORTH);

        // Siparişleri listele
        JList<String> orderList = new JList<>(fetchOrders());
        add(new JScrollPane(orderList), BorderLayout.CENTER);

        JButton confirmButton = new JButton("Onayla");
        JButton cancelButton = new JButton("İptal Et");

        confirmButton.addActionListener(e -> {
            // Onaylama işlemleri
        });
        cancelButton.addActionListener(e -> {
            // İptal etme işlemleri
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(confirmButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    private String[] fetchOrders() {
        OrderDAO orderDAO = new OrderDAO();
        return orderDAO.getActiveOrders(user.getUserId());
    }
}
