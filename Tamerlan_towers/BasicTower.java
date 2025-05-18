package Tamerlan_towers;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;

public class BasicTower extends Tower {
    private Point targetPosition;
    private boolean showFireEffect = false;
    private long fireEffectEndTime = 0;
    private BufferedImage fireImage;
    private static final int FIRE_EFFECT_DURATION = 200; // milliseconds

    public BasicTower(Point position) {
        super(position, 3, 20, 100, 1.0);
        try {
            fireImage = ImageIO.read(new File("res/basicfire.png"));
        } catch (IOException e) {
            System.err.println("Error loading fire effect image: " + e.getMessage());
            // Continue without the fire image
        }
    }

    @Override
    public void attack() {
        if (!canAttack()) return;
        
        lastAttackTime = System.currentTimeMillis();
        showFireEffect = true;
        fireEffectEndTime = System.currentTimeMillis() + FIRE_EFFECT_DURATION;
        
        // Update turret angle if we have a target
        if (targetPosition != null) {
            setTurretAngle(calculateAngleTo(targetPosition));
        }
    }
    
    @Override
    public void draw(Graphics g) {
        // Сначала рисуем базовое изображение башни
        super.draw(g);
        
        // Draw fire effect when active
        if (showFireEffect && System.currentTimeMillis() < fireEffectEndTime && targetPosition != null && fireImage != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Calculate the center of the tower
            int centerX = position.x * 50 + 25;
            int centerY = position.y * 50 + 25;
            
            // Calculate the angle to the target
            double angle = calculateAngleTo(targetPosition);
            
            // Save the current transform
            AffineTransform oldTransform = g2d.getTransform();
            
            // Create a new transform for the fire effect
            AffineTransform transform = new AffineTransform();
            transform.translate(centerX, centerY);
            transform.rotate(angle - 2*Math.PI); // Adjust rotation to match turret + 90 degrees
            
            // Position the fire at the end of the turret
            // Adjust these values based on your turret size and fire image
            transform.translate(0, -25); // Move the fire to the end of the turret
            transform.translate(-fireImage.getWidth()/2, -fireImage.getHeight()/2); // Center the fire image
            
            // Apply the transform and draw the fire
            g2d.setTransform(transform);
            g2d.drawImage(fireImage, 0, 0, null);
            
            // Restore the original transform
            g2d.setTransform(oldTransform);
            
            // Draw range circle
            g2d.setColor(new Color(255, 255, 0, 50));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(centerX - range * 50, centerY - range * 50, 
                       range * 50 * 2, range * 50 * 2);
            
            // Check if fire effect should be hidden
            if (System.currentTimeMillis() >= fireEffectEndTime) {
                showFireEffect = false;
            }
        }
    }
    
    public void setTargetPosition(Point target) {
        this.targetPosition = target;
    }
}
