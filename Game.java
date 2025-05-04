import javax.swing.*;

public class Game {
    public static void main(String[] args) {
        JFrame frame = new JFrame("Tower Defense");
        GamePanel panel = new GamePanel();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }
}
