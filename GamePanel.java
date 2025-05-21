import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.Timer;

public class GamePanel extends JPanel implements ActionListener {
    private Timer timer;
    private java.util.List<Enemy> enemies = new ArrayList<>();
    private BufferedImage[] walkFrames;
    private java.util.List<Point> path;

    public GamePanel() {
        setPreferredSize(new Dimension(800, 600));
        setBackground(Color.BLACK);

        walkFrames = loadWalkFrames();

        path = new ArrayList<>();
        path.add(new Point(0, 100));
        path.add(new Point(200, 100));
        path.add(new Point(400, 150));
        path.add(new Point(600, 300));
        path.add(new Point(800, 300));

        Enemy e = new Enemy(0, 100, 100, 2, path, walkFrames);
        enemies.add(e);

        timer = new Timer(30, this);
        timer.start();
    }

    private BufferedImage[] loadWalkFrames() {
        String[] fileNames = { "enemy1.png", "enemy2.png", "enemy3.png" };
        BufferedImage[] frames = new BufferedImage[fileNames.length];
        for (int i = 0; i < fileNames.length; i++) {
            try {
                frames[i] = ImageIO.read(new File("res/" + fileNames[i]));
            } catch (Exception e) {
                System.err.println("Ошибка загрузки изображения: " + fileNames[i]);
            }
        }
        return frames;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (int i = 0; i < enemies.size(); i++) {
            Enemy en = enemies.get(i);
            en.update();
            if (!en.isAlive()) {
                enemies.remove(i);
                i--;
            }
        }
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Enemy e : enemies) {
            e.render(g);
        }
    }
}
