package Tamerlan_towers;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class AreaTower extends Tower {
    private int splashRadius;
    private Color areaEffectColor = new Color(255, 100, 0, 80);
    private int explosionRadius = 0;
    private long lastExplosionTime = 0;
    private BufferedImage rocketImage;
    private boolean showRocketEffect = false;
    private long rocketEffectEndTime = 0;
    private static final int ROCKET_EFFECT_DURATION = 300; // milliseconds

    public AreaTower(Point position) {
        super(position, 2, 15, 150, 0.75);
        this.splashRadius = 1;
        
        try {
            rocketImage = ImageIO.read(new File("res/arearocket.png"));
        } catch (IOException e) {
            System.err.println("Error loading rocket effect image: " + e.getMessage());
            // Continue without the rocket image
        }
    }

    private Point targetPosition;

    @Override
    public void attack() {
        if (!canAttack()) return;
        
        lastAttackTime = System.currentTimeMillis();
        lastExplosionTime = System.currentTimeMillis();
        explosionRadius = range * 50;  // Visual effect when tower attacks
        
        // Show rocket effect
        showRocketEffect = true;
        rocketEffectEndTime = System.currentTimeMillis() + ROCKET_EFFECT_DURATION;
        
        // Update turret angle if we have a target
        if (targetPosition != null) {
            setTurretAngle(calculateAngleTo(targetPosition));
        }
    }
    
    public void setTargetPosition(Point target) {
        this.targetPosition = target;
    }
    
    @Override
    public void draw(Graphics g) {
        super.draw(g);
        
        // Calculate the center of the tower
        int centerX = position.x * 50 + 25;
        int centerY = position.y * 50 + 25;
        
        // Draw rocket effect when active
        if (showRocketEffect && System.currentTimeMillis() < rocketEffectEndTime && targetPosition != null && rocketImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Calculate the angle to the target
            double angle = calculateAngleTo(targetPosition);
            
            // Save the current transform
            AffineTransform oldTransform = g2d.getTransform();
            
            // Create a new transform for the rocket effect
            AffineTransform transform = new AffineTransform();
            transform.translate(centerX, centerY);
            transform.rotate(angle - 2*Math.PI); // Use the same rotation as BasicTower fire effect
            
            // Position the rocket at the end of the turret
            transform.translate(0, -25); // Move the rocket to the end of the turret
            transform.translate(-rocketImage.getWidth()/2, -rocketImage.getHeight()/2); // Center the rocket image
            
            // Apply the transform and draw the rocket
            g2d.setTransform(transform);
            g2d.drawImage(rocketImage, 0, 0, null);
            
            // Restore the original transform
            g2d.setTransform(oldTransform);
            
            // Check if rocket effect should be hidden
            if (System.currentTimeMillis() >= rocketEffectEndTime) {
                showRocketEffect = false;
            }
        }
        
        // Draw explosion effect when active
        if (System.currentTimeMillis() - lastExplosionTime < 300) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Draw explosion effect circle
            g2d.setColor(areaEffectColor);
            g2d.fillOval(centerX - explosionRadius, centerY - explosionRadius, 
                       explosionRadius * 2, explosionRadius * 2);
            
            // Draw range circle
            g2d.setColor(new Color(255, 165, 0, 50));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(centerX - range * 50, centerY - range * 50, 
                       range * 50 * 2, range * 50 * 2);
        }
    }
    
    public int getSplashRadius() {
        return splashRadius;
    }
}
