import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class GamePanel extends JPanel implements ActionListener {

    private Enemy enemy;
    private Timer timer;

    public GamePanel() {
        this.setPreferredSize(new Dimension(800, 600));
        this.setBackground(Color.DARK_GRAY);

        enemy = new Enemy(50, 100);

        timer = new Timer(16, this); // ~60 FPS
        timer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (!enemy.isDead()) {
            enemy.draw(g);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        enemy.update();

        // Для примера — враг получает урон каждые 3 секунды
        if (System.currentTimeMillis() % 3000 < 20) {
            enemy.takeDamage(10);
        }

        repaint();
    }
}
