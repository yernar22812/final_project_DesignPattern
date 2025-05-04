import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SoundPlayer {
    private Map<String, Clip> soundClips = new HashMap<>();
    private boolean soundEnabled = true;
    
    public SoundPlayer() {
        // Пытаемся загрузить звуковые эффекты
        try {
            // Проверяем, доступна ли звуковая система
            AudioSystem.getMixerInfo();
            
            // Загружаем звуки, если они есть
            loadSound("build", "res/build.wav");
            loadSound("shoot", "res/shoot.wav");
            loadSound("explosion", "res/explosion.wav");
            loadSound("slow", "res/slow.wav");
            loadSound("damage", "res/damage.wav");
            loadSound("coin", "res/coin.wav");
            loadSound("wave", "res/wave.wav");
        } catch (Exception e) {
            System.out.println("Warning: Sound system not available. Sounds will be disabled.");
            soundEnabled = false;
        }
    }
    
    private void loadSound(String name, String path) {
        try {
            File soundFile = new File(path);
            if (!soundFile.exists()) {
                System.out.println("Warning: Sound file not found: " + path);
                return;
            }
            
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            Clip clip = AudioSystem.getClip();
            clip.open(audioIn);
            soundClips.put(name, clip);
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            System.out.println("Error loading sound: " + path + " - " + e.getMessage());
        }
    }
    
    public void playSound(String name) {
        if (!soundEnabled) return;
        
        Clip clip = soundClips.get(name);
        if (clip != null) {
            try {
                if (clip.isRunning()) {
                    clip.stop();
                }
                clip.setFramePosition(0);
                clip.start();
            } catch (Exception e) {
                System.out.println("Error playing sound: " + name + " - " + e.getMessage());
            }
        } else {
            // Просто выводим предупреждение, но не выбрасываем исключение
            System.out.println("Warning: Sound not found: " + name);
        }
    }
    
    public void stopAllSounds() {
        if (!soundEnabled) return;
        
        for (Clip clip : soundClips.values()) {
            try {
                if (clip.isRunning()) {
                    clip.stop();
                }
            } catch (Exception e) {
                System.out.println("Error stopping sound: " + e.getMessage());
            }
        }
    }
}
