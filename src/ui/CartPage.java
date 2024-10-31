// src/ui/CartPage.java
package ui;

import models.User;
import javax.swing.*;

public class CartPage extends JFrame {
    private User user;

    public CartPage(User user) {
        this.user = user;

        setTitle("Sepet");
        setSize(300, 400);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        JLabel cartLabel = new JLabel("Sepetiniz burada görüntülenecek.");
        add(cartLabel);
    }
}
