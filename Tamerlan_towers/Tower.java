package Tamerlan_towers;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D;

public abstract class Tower {
    protected Point position;
    protected int range;
    public int damage;  // Changed to public
    protected int cost;
    protected double attackSpeed; 
    protected long lastAttackTime;
    protected Image towerBaseImage;
    protected Image towerTurretImage;
    protected double turretAngle = 0.0; // Angle in radians

    public Tower(Point position, int range, int damage, int cost, double attackSpeed) {
        this.position = position;
        this.range = range;
        this.damage = damage;
        this.cost = cost;
        this.attackSpeed = attackSpeed;
        this.lastAttackTime = System.currentTimeMillis();
    }

    public boolean canAttack() {
        long currentTime = System.currentTimeMillis();
        return currentTime - lastAttackTime >= (1000 / attackSpeed);
    }

    public boolean isInRange(Point targetPosition) {  // Changed to public
        return Point2D.distance(position.x, position.y, 
                              targetPosition.x, targetPosition.y) <= range;
    }

    public abstract void attack();

    public void draw(Graphics g) {
        int x = position.x * 50;
        int y = position.y * 50;
        int width = 50;
        int height = 50;
        
        // Draw base image
        if (towerBaseImage != null) {
            g.drawImage(towerBaseImage, x, y, width, height, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, width, height);
        }
        
        // Draw turret image with rotation if available
        if (towerTurretImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform oldTransform = g2d.getTransform();
            
            // Calculate center for rotation
            int centerX = x + width / 2;
            int centerY = y + height / 2;
            
            // Create transform for rotation
            AffineTransform transform = new AffineTransform();
            transform.translate(centerX, centerY);
            transform.rotate(turretAngle);
            transform.translate(-width / 2, -height / 2);
            
            // Apply transform and draw turret
            g2d.setTransform(transform);
            g2d.drawImage(towerTurretImage, 0, 0, width, height, null);
            
            // Restore original transform
            g2d.setTransform(oldTransform);
        }
    }
    
    /**
     * Set the base image for the tower
     * @param image The base image
     */
    public void setTowerBaseImage(Image image) {
        this.towerBaseImage = image;
    }
    
    /**
     * Set the turret image for the tower
     * @param image The turret image
     */
    public void setTowerTurretImage(Image image) {
        this.towerTurretImage = image;
    }
    
    /**
     * Set the rotation angle of the turret
     * @param angle The angle in radians
     */
    public void setTurretAngle(double angle) {
        this.turretAngle = angle;
    }
    
    /**
     * Calculate the angle to a target position
     * @param targetPosition The target position
     * @return The angle in radians
     */
    public double calculateAngleTo(Point targetPosition) {
        double dx = (targetPosition.x - position.x);
        double dy = (targetPosition.y - position.y);
        // Add Math.PI/2 (90 degrees) offset since turret textures point upward by default
        // This makes the 0-degree angle point upward instead of to the right
        // Add Math.PI (180 degrees) to rotate the turret to face the opposite direction
        return Math.atan2(dy, dx) - Math.PI/2 + Math.PI;
    }
    
    /**
     * For backward compatibility
     */
    public void setTowerImage(Image image) {
        this.towerBaseImage = image;
    }

    public int getCost() {
        return cost;
    }

    public Point getPosition() {
        return position;
    }
}
