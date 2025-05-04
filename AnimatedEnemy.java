import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class AnimatedEnemy {

    private int x, y;
    private int health = 100;
    private boolean alive = true;
    private boolean showBlood = false;
    private long bloodEffectTime;

    private BufferedImage currentImage;
    private List<BufferedImage> idleImages;
    private List<BufferedImage> walkImages;
    private List<BufferedImage> dieImages;
    private int currentFrame;
    private long lastUpdateTime;

    public enum State { IDLE, WALK, DIE }
    private State currentState = State.WALK;

    public AnimatedEnemy(int x, int y) {
        this.x = x;
        this.y = y;
        this.currentFrame = 0;
        this.lastUpdateTime = System.currentTimeMillis();

        idleImages = loadImages("res/Idle/");
        walkImages = loadImages("res/Walk/");
        dieImages = loadImages("res/Die/");

        currentImage = walkImages.get(0);
    }

    private List<BufferedImage> loadImages(String folder) {
        File folderPath = new File(folder);
        File[] files = folderPath.listFiles((dir, name) -> name.endsWith(".png"));
        if (files == null || files.length == 0) return Collections.emptyList();

        Arrays.sort(files); // Важно для порядка кадров
        List<BufferedImage> images = new ArrayList<>();

        try {
            for (File file : files) {
                images.add(ImageIO.read(file));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return images;
    }

    public void update() {
        if (!alive && currentState != State.DIE) return;

        if (currentState == State.WALK) {
            x += 1; // Движение по карте
        }

        if (System.currentTimeMillis() - lastUpdateTime > 100) {
            lastUpdateTime = System.currentTimeMillis();
            List<BufferedImage> anim = getCurrentAnimation();
            if (!anim.isEmpty()) {
                currentFrame = (currentFrame + 1) % anim.size();
                currentImage = anim.get(currentFrame);
            }
        }

        if (showBlood && System.currentTimeMillis() - bloodEffectTime > 200) {
            showBlood = false;
        }

        if (health <= 0 && alive) {
            alive = false;
            currentState = State.DIE;
            currentFrame = 0;
            lastUpdateTime = System.currentTimeMillis();
        }
    }

    private List<BufferedImage> getCurrentAnimation() {
        switch (currentState) {
            case WALK: return walkImages;
            case DIE: return dieImages;
            case IDLE: default: return idleImages;
        }
    }

    public void draw(Graphics g) {
        if (!alive && currentState != State.DIE) return;

        g.drawImage(currentImage, x, y, null);

        // HP Bar
        g.setColor(Color.RED);
        g.fillRect(x, y - 10, health, 5);

        // Blood Effect
        if (showBlood) {
            g.setColor(new Color(255, 0, 0, 150));
            g.fillOval(x + 20, y + 20, 20, 20);
        }
    }

    public void takeDamage(int damage) {
        if (!alive) return;

        health -= damage;
        showBlood = true;
        bloodEffectTime = System.currentTimeMillis();
    }

    public boolean isAlive() {
        return alive || currentState == State.DIE;
    }

    public boolean isCompletelyDead() {
        return !alive && currentFrame >= dieImages.size() - 1;
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, currentImage.getWidth(), currentImage.getHeight());
    }
}
