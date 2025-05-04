package Tamerlan_towers;

import java.awt.*;
import java.util.List;

public class Enemy {
    private Point position;
    private int health;
    private int maxHealth;
    private double speed;
    private double slowEffect = 1.0;
    private long slowEffectEndTime = 0;
    private List<Point> path;
    private int currentPathIndex;
    private boolean isDead = false;
    private boolean reachedEnd = false;
    private Image enemyImage;
    private Color enemyColor = Color.RED;
    private Color slowedColor = new Color(150, 150, 255);
    private double progressToNextTile = 0.0;

    public Enemy(List<Point> path, int health, double speed) {
        this.path = path;
        this.health = health;
        this.maxHealth = health;
        this.speed = speed;
        this.currentPathIndex = 0;
        if (!path.isEmpty()) {
            this.position = new Point(path.get(0).x * 50, path.get(0).y * 50);
        }
    }

    public void move() {
        if (currentPathIndex >= path.size() - 1 || isDead) {
            // Если враг мертв, не отмечаем его как достигшего финиша
            if (!isDead) {
                reachedEnd = true;
            }
            return;
        }

        // Check if slow effect has expired
        if (System.currentTimeMillis() > slowEffectEndTime) {
            resetSlowEffect();
        }

        Point currentTile = path.get(currentPathIndex);
        Point nextTile = path.get(currentPathIndex + 1);
        
        // Вычисляем направление движения
        int dx = nextTile.x - currentTile.x;
        int dy = nextTile.y - currentTile.y;
        
        // Применяем эффект замедления
        double actualSpeed = speed * slowEffect;
        
        // Увеличиваем прогресс движения к следующей клетке
        progressToNextTile += actualSpeed * 0.02;  // Коэффициент для регулировки скорости
        
        // Если достигли следующей клетки
        if (progressToNextTile >= 1.0) {
            currentPathIndex++;
            progressToNextTile = 0.0;
            
            // Если достигли конца пути
            if (currentPathIndex >= path.size() - 1) {
                // Если враг не мертв, отмечаем его как достигшего финиша
                if (!isDead) {
                    reachedEnd = true;
                }
                return;
            }
            
            // Обновляем текущую и следующую клетки
            currentTile = path.get(currentPathIndex);
            nextTile = path.get(currentPathIndex + 1);
            dx = nextTile.x - currentTile.x;
            dy = nextTile.y - currentTile.y;
        }
        
        // Вычисляем текущую позицию между клетками
        double posX = currentTile.x + dx * progressToNextTile;
        double posY = currentTile.y + dy * progressToNextTile;
        
        // Обновляем позицию в пикселях
        position.x = (int)(posX * 50);
        position.y = (int)(posY * 50);
    }

    public void takeDamage(int damage) {
        health -= damage;
        if (health <= 0) {
            isDead = true;
        }
    }

    public void applySlowEffect(double slowAmount, int duration) {
        // Apply the strongest slow effect
        double newSlowEffect = 1.0 - slowAmount;
        if (newSlowEffect < slowEffect) {
            slowEffect = newSlowEffect;
            slowEffectEndTime = System.currentTimeMillis() + duration;
        } else if (System.currentTimeMillis() + duration > slowEffectEndTime) {
            // Extend the duration if the current slow is stronger
            slowEffectEndTime = System.currentTimeMillis() + duration;
        }
    }

    public void draw(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        if (enemyImage != null) {
            g2d.drawImage(enemyImage, (int)position.x, (int)position.y, 40, 40, null);
        } else {
            // Draw enemy with different color if slowed
            if (slowEffect < 1.0) {
                g2d.setColor(slowedColor);
            } else {
                g2d.setColor(enemyColor);
            }
            g2d.fillOval((int)position.x, (int)position.y, 40, 40);
            
            // Add a border
            g2d.setColor(Color.BLACK);
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval((int)position.x, (int)position.y, 40, 40);
        }

        // Draw health bar with border
        g2d.setColor(Color.BLACK);
        g2d.fillRect((int)position.x - 2, (int)position.y - 12, 44, 9);
        
        // Health bar background
        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect((int)position.x, (int)position.y - 10, 40, 5);
        
        // Health bar
        if (health > maxHealth * 0.6) {
            g2d.setColor(Color.GREEN);
        } else if (health > maxHealth * 0.3) {
            g2d.setColor(Color.YELLOW);
        } else {
            g2d.setColor(Color.RED);
        }
        g2d.fillRect((int)position.x, (int)position.y - 10, 
                  (int)(40 * ((double)health / maxHealth)), 5);
                  
        // Draw slow effect indicator if slowed
        if (slowEffect < 1.0) {
            g2d.setColor(new Color(0, 0, 255, 150));
            int slowSize = 10;
            g2d.fillOval((int)position.x + 30, (int)position.y + 30, slowSize, slowSize);
        }
    }

    public Point getPosition() {
        return new Point((int)position.x / 50, (int)position.y / 50);
    }

    public boolean isDead() {
        return isDead;
    }

    public boolean hasReachedEnd() {
        return reachedEnd;
    }
    
    public void setReachedEnd(boolean reachedEnd) {
        this.reachedEnd = reachedEnd;
    }

    public double getSlowEffect() {
        return slowEffect;
    }

    public void resetSlowEffect() {
        slowEffect = 1.0;
    }
    
    public void setEnemyImage(Image image) {
        this.enemyImage = image;
    }
}
