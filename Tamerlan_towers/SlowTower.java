package Tamerlan_towers;

import java.awt.*;

public class SlowTower extends Tower {
    private double slowAmount = 0.5;  // Increased from 0.3 to 0.5 (50% slow)
    private int slowDuration = 3000;  // Increased from 2000 to 3000 (3 seconds)
    private Color slowEffectColor = new Color(0, 100, 255, 80);
    private int slowEffectRadius = 0;
    private long lastSlowEffectTime = 0;

    public SlowTower(Point position) {
        super(position, 3, 5, 150, 0.5);  // Increased range from 2 to 3
    }

    @Override
    public void attack() {
        if (!canAttack()) return;
        
        lastAttackTime = System.currentTimeMillis();
        lastSlowEffectTime = System.currentTimeMillis();
        slowEffectRadius = range * 50;  // Visual effect when tower attacks
    }
    
    @Override
    public void draw(Graphics g) {
        super.draw(g);
        
        // Draw slow effect radius when active
        if (System.currentTimeMillis() - lastSlowEffectTime < 500) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Calculate the center of the tower
            int centerX = position.x * 50 + 25;
            int centerY = position.y * 50 + 25;
            
            // Draw slow effect circle
            g2d.setColor(slowEffectColor);
            g2d.fillOval(centerX - slowEffectRadius, centerY - slowEffectRadius, 
                       slowEffectRadius * 2, slowEffectRadius * 2);
            
            // Draw range circle
            g2d.setColor(new Color(0, 0, 255, 50));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(centerX - range * 50, centerY - range * 50, 
                       range * 50 * 2, range * 50 * 2);
        }
    }
    
    public double getSlowAmount() {
        return slowAmount;
    }
    
    public int getSlowDuration() {
        return slowDuration;
    }
}
