package Tamerlan_towers;

import java.awt.*;

public class BasicTower extends Tower {
    private Color attackColor = new Color(255, 255, 0, 150);
    private Point targetPosition;
    private boolean showAttackLine = false;
    private long attackLineEndTime = 0;

    public BasicTower(Point position) {
        super(position, 3, 20, 100, 1.0);
    }

    @Override
    public void attack() {
        if (!canAttack()) return;
        
        lastAttackTime = System.currentTimeMillis();
        showAttackLine = true;
        attackLineEndTime = System.currentTimeMillis() + 200; // Show attack line for 200ms
    }
    
    @Override
    public void draw(Graphics g) {
        // Сначала рисуем базовое изображение башни
        super.draw(g);
        
        // Draw attack line when active
        if (showAttackLine && System.currentTimeMillis() < attackLineEndTime && targetPosition != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            
            // Calculate the center of the tower
            int centerX = position.x * 50 + 25;
            int centerY = position.y * 50 + 25;
            
            // Draw attack line
            g2d.setColor(attackColor);
            g2d.setStroke(new BasicStroke(3));
            g2d.drawLine(centerX, centerY, 
                       targetPosition.x * 50 + 25, targetPosition.y * 50 + 25);
            
            // Draw range circle
            g2d.setColor(new Color(255, 255, 0, 50));
            g2d.setStroke(new BasicStroke(2));
            g2d.drawOval(centerX - range * 50, centerY - range * 50, 
                       range * 50 * 2, range * 50 * 2);
            
            // Check if attack line should be hidden
            if (System.currentTimeMillis() >= attackLineEndTime) {
                showAttackLine = false;
                targetPosition = null;
            }
        }
    }
    
    public void setTargetPosition(Point target) {
        this.targetPosition = target;
    }
}
