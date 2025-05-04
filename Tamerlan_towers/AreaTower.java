package Tamerlan_towers;

import java.awt.*;

public class AreaTower extends Tower {
    private int splashRadius;
    private Color areaEffectColor = new Color(255, 100, 0, 80);
    private int explosionRadius = 0;
    private long lastExplosionTime = 0;

    public AreaTower(Point position) {
        super(position, 2, 15, 150, 0.75);
        this.splashRadius = 1;
    }

    @Override
    public void attack() {
        if (!canAttack()) return;
        
        lastAttackTime = System.currentTimeMillis();
        lastExplosionTime = System.currentTimeMillis();
        explosionRadius = range * 50;  // Visual effect when tower attacks
    }
    
    @Override
    public void draw(Graphics g) {
        super.draw(g);
        
        // Draw explosion effect when active
        if (System.currentTimeMillis() - lastExplosionTime < 300) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Calculate the center of the tower
            int centerX = position.x * 50 + 25;
            int centerY = position.y * 50 + 25;
            
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
