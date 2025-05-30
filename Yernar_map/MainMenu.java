package Yernar_map;

import javax.swing.*;
import java.awt.*;

public class MainMenu extends JFrame {
    public MainMenu() {

        setTitle("Tower Defense — Главное меню");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new GridLayout(2, 1));

        SoundPlayer music = new SoundPlayer("res/background_music.wav");
        JButton playButton = new JButton("1) Играть");
        JButton editorButton = new JButton("2) Редактор карты");

        add(playButton);
        add(editorButton);

        playButton.addActionListener(e -> {
            GamePanel panel = new GamePanel("map.txt");
            panel.isEditorMode = false;
            panel.showTileSelector = false;
            showWindow("Игра", panel);
        });

        editorButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser(".");
            int result = chooser.showOpenDialog(this);
            if (result == JFileChooser.APPROVE_OPTION) {
                EditorPanel panel = new EditorPanel(chooser.getSelectedFile().getAbsolutePath());
                showWindow("Редактор карты", panel);
            }
        });

        setVisible(true);
    }

    private void showWindow(String title, JPanel panel) {
        JFrame frame = new JFrame(title);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.add(panel);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        panel.requestFocusInWindow();
    }
}
