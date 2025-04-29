package Yernar_map;

import javax.swing.*;
import java.awt.event.*;

public class EditorPanel extends GamePanel {

    public EditorPanel(String path) {
        super(path);

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int col = e.getX() / TILE_SIZE;
                int row = e.getY() / TILE_SIZE;

                if (row == map.length) {
                    // Клик по нижней панели для выбора тайла
                    if (col >= 0 && col <= 4) {
                        selectedTile = col; // Выбираем нужный тайл
                        repaint();
                    }
                } else if (row < map.length && col < map[0].length) {
                    if (SwingUtilities.isLeftMouseButton(e)) {
                        // При клике на карте меняем тайл
                        map[row][col] = selectedTile;
                        repaint();
                    }
                }
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                int col = e.getX() / TILE_SIZE;
                int row = e.getY() / TILE_SIZE;
                if (row < map.length && col < map[0].length) {
                    // При перетаскивании также меняем тайл
                    map[row][col] = selectedTile;
                    repaint();
                }
            }
        });

        setFocusable(true);
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == 's' || e.getKeyChar() == 'S') {
                    saveMapToFile(path);
                }
            }
        });
    }
}