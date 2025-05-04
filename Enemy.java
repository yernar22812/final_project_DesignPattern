import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Enemy {
    private int x, y;
    private int health = 100;
    private boolean isDead = false;
    private boolean dying = false;

    private BufferedImage currentImage;
    private List<BufferedImage> walkImages;
    private List<BufferedImage> dieImages;

    private int currentFrame = 0;
    private long lastUpdateTime = 0;

    private long bloodEffectStartTime = 0;
    private boolean showBloodEffect = false;

    public Enemy(int x, int y) {
        this.x = x;
        this.y = y;
        walkImages = loadImages("res/Walk");
        dieImages = loadImages("res/Die");
        currentImage = walkImages.size() > 0 ? walkImages.get(0) : null;
        lastUpdateTime = System.currentTimeMillis();
    }

    private List<BufferedImage> loadImages(String folder) {
        List<BufferedImage> images = new ArrayList<>();
        File folderPath = new File(folder);
        File[] files = folderPath.listFiles((dir, name) -> name.endsWith(".png"));

        if (files != null) {
            for (File file : files) {
                try {
                    images.add(ImageIO.read(file));
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return images;
    }

    public void update() {
        if (isDead) return;

        long currentTime = System.currentTimeMillis();

        // Кадры анимации
        if (currentTime - lastUpdateTime > 100) {
            lastUpdateTime = currentTime;
            currentFrame++;

            if (dying) {
                if (currentFrame >= dieImages.size()) {
                    isDead = true;
                } else {
                    currentImage = dieImages.get(currentFrame);
                }
            } else {
                currentFrame %= walkImages.size();
                currentImage = walkImages.get(currentFrame);
            }
        }

        if (!dying) {
            x += 1; // Движение по X
        }

        // Убираем эффект крови через 200 мс
        if (showBloodEffect && (currentTime - bloodEffectStartTime > 200)) {
            showBloodEffect = false;
        }
    }

    public void draw(Graphics g) {
        if (isDead || currentImage == null) return;

        g.drawImage(currentImage, x, y, null);

        // Полоска HP
        int barWidth = 50;
        int barHeight = 6;
        int filledWidth = (int) ((health / 100.0) * barWidth);
        g.setColor(Color.RED);
        g.fillRect(x, y - 10, barWidth, barHeight);
        g.setColor(Color.GREEN);
        g.fillRect(x, y - 10, filledWidth, barHeight);
        g.setColor(Color.BLACK);
        g.drawRect(x, y - 10, barWidth, barHeight);

        // Эффект крови
        if (showBloodEffect) {
            g.setColor(new Color(255, 0, 0, 120)); // Полупрозрачный красный
            g.fillOval(x + 20, y + 20, 30, 30);
        }
    }

    public void takeDamage(int damage) {
        if (isDead || dying) return;

        health -= damage;
        showBloodEffect = true;
        bloodEffectStartTime = System.currentTimeMillis();

        if (health <= 0) {
            dying = true;
            currentFrame = 0;
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    public boolean isDead() {
        return isDead;
    }

    public int getX() { return x; }
    public int getY() { return y; }
}
