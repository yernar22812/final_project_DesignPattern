package Yernar_map;


import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;

public class SoundPlayer {
    private Clip clip;

    public SoundPlayer(String filepath) {
        try {
            AudioInputStream audioStream = AudioSystem.getAudioInputStream(new File(filepath));
            clip = AudioSystem.getClip();
            clip.open(audioStream);
            clip.loop(Clip.LOOP_CONTINUOUSLY); // Цикличное воспроизведение
        } catch (UnsupportedAudioFileException | IOException | LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    public void stop() {
        if (clip != null && clip.isRunning()) {
            clip.stop();
        }
    }
}
