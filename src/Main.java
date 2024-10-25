import javax.swing.*;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        SwingUtilities.invokeLater(() -> {
            girisEkrani giris = new girisEkrani();
            giris.setVisible(true);
        });
    }
}