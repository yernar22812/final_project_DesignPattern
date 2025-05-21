import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SoundPlayer {
    private Map<String, Clip> soundClips = new HashMap<>();
    private boolean soundEnabled = true;
    private float volume = 1.0f; // Default volume (0.0 to 1.0)
    private FloatControl[] gainControls;
    
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
            loadSound("BUYING", "res/BUYING.mp3");
            loadSound("basicsound", "res/basicsound.wav");
            loadSound("BOOOM", "res/BOOOM.wav");
            loadSound("freeze-04-101soundboards", "res/freeze-04-101soundboards.mp3");
            loadSound("menu_click", "res/menu_click.wav");
            
            // Initialize gain controls array
            gainControls = new FloatControl[soundClips.size()];
            int i = 0;
            for (Clip clip : soundClips.values()) {
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    gainControls[i++] = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                }
            }
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
                
                // Apply volume before playing
                if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    // Convert linear volume (0.0 to 1.0) to dB scale
                    float dB = (volume > 0.0f) ? (float) (20.0 * Math.log10(volume)) : -80.0f;
                    // Ensure the value is within the control's range
                    dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                    gainControl.setValue(dB);
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
    
    public boolean isSoundEnabled() {
        return soundEnabled;
    }
    
    public void setSoundEnabled(boolean enabled) {
        this.soundEnabled = enabled;
        if (!enabled) {
            stopAllSounds();
        }
    }
    
    public float getVolume() {
        return volume;
    }
    
    public void setVolume(float volume) {
        // Ensure volume is between 0.0 and 1.0
        this.volume = Math.max(0.0f, Math.min(1.0f, volume));
        
        // Apply new volume to all currently loaded sounds
        for (Clip clip : soundClips.values()) {
            if (clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
                try {
                    FloatControl gainControl = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
                    // Convert linear volume (0.0 to 1.0) to dB scale
                    float dB = (volume > 0.0f) ? (float) (20.0 * Math.log10(volume)) : -80.0f;
                    // Ensure the value is within the control's range
                    dB = Math.max(gainControl.getMinimum(), Math.min(gainControl.getMaximum(), dB));
                    gainControl.setValue(dB);
                } catch (Exception e) {
                    System.out.println("Error setting volume: " + e.getMessage());
                }
            }
        }
    }
}
