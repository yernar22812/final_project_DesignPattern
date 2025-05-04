package Tamerlan_towers;

import java.awt.*;
import java.awt.geom.Point2D;

public abstract class Tower {
    protected Point position;
    protected int range;
    public int damage;  // Changed to public
    protected int cost;
    protected double attackSpeed; 
    protected long lastAttackTime;
    protected Image towerImage;

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
        if (towerImage != null) {
            g.drawImage(towerImage, position.x * 50, position.y * 50, 50, 50, null);
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(position.x * 50, position.y * 50, 50, 50);
        }
    }
    
    public void setTowerImage(Image image) {
        this.towerImage = image;
    }

    public int getCost() {
        return cost;
    }

    public Point getPosition() {
        return position;
    }
}
