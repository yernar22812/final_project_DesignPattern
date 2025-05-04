package Yernar_map;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Tower Defense");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Открытие диалога выбора карты
        JFileChooser fileChooser = new JFileChooser(".");
        fileChooser.setDialogTitle("Выберите файл карты");
        int result = fileChooser.showOpenDialog(this);
        if (result != JFileChooser.APPROVE_OPTION) {
            System.out.println("Файл не выбран");
            System.exit(0);
        }

        File mapFile = fileChooser.getSelectedFile();
        GamePanel panel = new GamePanelWithPath(mapFile.getAbsolutePath());

        add(panel);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);

        // Клавиша S сохраняет карту
        panel.getInputMap().put(KeyStroke.getKeyStroke("S"), "save");
        panel.getActionMap().put("save", new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                panel.saveMapToFile(mapFile.getAbsolutePath());
            }
        });
    }
}
