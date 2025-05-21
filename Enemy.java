import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Enemy {
    private int x, y;
    private int width = 32, height = 32;
    private int maxHealth;
    private int health;
    private int speed;
    private boolean alive;

    private List<Point> path;
    private int pathIndex = 0;

    private BufferedImage[] walkFrames;
    private int animationFrame = 0;
    private int animationSpeed = 10;
    private int animationCounter = 0;

    public Enemy(int x, int y, int health, int speed, List<Point> path, BufferedImage[] walkFrames) {
        this.x = x;
        this.y = y;
        this.maxHealth = health;
        this.health = health;
        this.speed = speed;
        this.alive = true;
        this.path = path;
        this.walkFrames = walkFrames;
    }

    public void update() {
        if (pathIndex < path.size()) {
            Point target = path.get(pathIndex);
            int dx = target.x - x;
            int dy = target.y - y;
            double dist = Math.sqrt(dx * dx + dy * dy);
            if (dist < speed) {
                x = target.x;
                y = target.y;
                pathIndex++;
            } else {
                x += (int) (speed * dx / dist);
                y += (int) (speed * dy / dist);
            }
        }

        animationCounter++;
        if (animationCounter >= animationSpeed) {
            animationCounter = 0;
            animationFrame = (animationFrame + 1) % walkFrames.length;
        }
    }

    public void render(Graphics g) {
        if (walkFrames != null && walkFrames[animationFrame] != null) {
            g.drawImage(walkFrames[animationFrame], x, y, width, height, null);
        } else {
            g.setColor(Color.RED);
            g.fillRect(x, y, width, height);
        }

        g.setColor(Color.GREEN);
        int hpBarWidth = (int) ((double) health / maxHealth * width);
        g.fillRect(x, y - 6, hpBarWidth, 4);
    }

    public boolean isAlive() {
        return alive;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void takeDamage(int dmg) {
        health -= dmg;
        if (health <= 0) alive = false;
    }
}
