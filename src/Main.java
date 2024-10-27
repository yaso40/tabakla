import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        JFrame frame = new JFrame("Ana Sayfa");
        frame.setContentPane(new AnaSayfa().getContentPane());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1512, 982); // Pencere boyutunu ayarlayın
        frame.setLocationRelativeTo(null);
        frame.setVisible(true); // Pencereyi gö

    }
}