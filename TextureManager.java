import javax.imageio.ImageIO;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * TextureManager class for loading and managing game textures.
 * Implements Singleton pattern to ensure only one instance exists.
 */
public class TextureManager {
    private static TextureManager instance;
    private Map<String, BufferedImage> textureCache;
    private final String defaultResourcePath = "res/";
    
    /**
     * Private constructor to prevent instantiation from outside
     */
    private TextureManager() {
        textureCache = new HashMap<>();
    }
    
    /**
     * Get the singleton instance of TextureManager
     * @return The TextureManager instance
     */
    public static TextureManager getInstance() {
        if (instance == null) {
            instance = new TextureManager();
        }
        return instance;
    }
    
    /**
     * Load a texture from a file
     * @param fileName The name of the texture file
     * @return The loaded BufferedImage or null if loading failed
     */
    public BufferedImage loadTexture(String fileName) {
        // Check if texture is already in cache
        if (textureCache.containsKey(fileName)) {
            return textureCache.get(fileName);
        }
        
        // If not in cache, load it
        try {
            String filePath = defaultResourcePath + fileName;
            BufferedImage texture = ImageIO.read(new File(filePath));
            textureCache.put(fileName, texture);
            return texture;
        } catch (IOException e) {
            System.err.println("Error loading texture: " + fileName);
            e.printStackTrace();
            return null;
        }
    }
    
    /**
     * Get a texture from the cache or load it if not present
     * @param fileName The name of the texture file
     * @return The BufferedImage or null if loading failed
     */
    public BufferedImage getTexture(String fileName) {
        return loadTexture(fileName);
    }
    
    /**
     * Draw a texture at the specified position with default size
     * @param g The Graphics context to draw on
     * @param fileName The name of the texture file
     * @param x The x-coordinate to draw at
     * @param y The y-coordinate to draw at
     */
    public void drawTexture(Graphics g, String fileName, int x, int y) {
        BufferedImage texture = getTexture(fileName);
        if (texture != null) {
            g.drawImage(texture, x, y, null);
        }
    }
    
    /**
     * Draw a texture at the specified position with custom size
     * @param g The Graphics context to draw on
     * @param fileName The name of the texture file
     * @param x The x-coordinate to draw at
     * @param y The y-coordinate to draw at
     * @param width The width to draw the texture
     * @param height The height to draw the texture
     */
    public void drawTexture(Graphics g, String fileName, int x, int y, int width, int height) {
        BufferedImage texture = getTexture(fileName);
        if (texture != null) {
            g.drawImage(texture, x, y, width, height, null);
        }
    }
    
    /**
     * Draw a rotatable turret texture on top of a base texture
     * @param g The Graphics context to draw on
     * @param baseFileName The name of the base texture file
     * @param turretFileName The name of the turret texture file
     * @param x The x-coordinate to draw at
     * @param y The y-coordinate to draw at
     * @param width The width to draw the textures
     * @param height The height to draw the textures
     * @param angle The angle to rotate the turret (in radians)
     */
    public void drawTowerWithTurret(Graphics g, String baseFileName, String turretFileName, 
                                   int x, int y, int width, int height, double angle) {
        // Draw the base first
        drawTexture(g, baseFileName, x, y, width, height);
        
        // Then draw the rotated turret on top
        BufferedImage turret = getTexture(turretFileName);
        if (turret != null) {
            Graphics2D g2d = (Graphics2D) g;
            AffineTransform oldTransform = g2d.getTransform();
            
            // Calculate the center of the image for rotation
            int centerX = x + width / 2;
            int centerY = y + height / 2;
            
            // Create a new transform for rotation around the center
            AffineTransform transform = new AffineTransform();
            transform.translate(centerX, centerY);
            transform.rotate(angle);
            transform.translate(-turret.getWidth() / 2, -turret.getHeight() / 2);
            
            // Apply the transform and draw the turret
            g2d.setTransform(transform);
            g2d.drawImage(turret, 0, 0, null);
            
            // Restore the original transform
            g2d.setTransform(oldTransform);
        }
    }
    
    /**
     * Clear the texture cache to free memory
     */
    public void clearCache() {
        textureCache.clear();
    }
    
    /**
     * Remove a specific texture from the cache
     * @param fileName The name of the texture file to remove
     */
    public void removeFromCache(String fileName) {
        textureCache.remove(fileName);
    }
    
    /**
     * Get the Image object for a texture
     * @param fileName The name of the texture file
     * @return The Image object or null if loading failed
     */
    public Image getTextureAsImage(String fileName) {
        return getTexture(fileName);
    }
}
