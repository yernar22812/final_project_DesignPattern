import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TowerDefenseWindow extends JFrame {
    private TowerDefenseGame gamePanel;
    
    public TowerDefenseWindow(String mapPath) {
        setTitle("Tower Defense Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        gamePanel = new TowerDefenseGame(mapPath);
        add(gamePanel);
        
        // Устанавливаем правильный размер окна с учетом HUD
        pack();
        // Добавляем дополнительное пространство для HUD
        Dimension size = getSize();
        setSize(size.width, size.height + 50);
        
        setLocationRelativeTo(null);
        
        // Добавляем слушатель окна для установки фокуса на игровую панель
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                gamePanel.requestFocusInWindow();
            }
        });
        
        // Важно: сначала показываем окно, затем устанавливаем фокус
        setVisible(true);
        gamePanel.requestFocusInWindow();
        
        // Выводим сообщение для отладки
        System.out.println("Окно игры создано. Используйте клавиши 1-3 для выбора башен и ПРОБЕЛ для запуска волны.");
    }
}
