import Tamerlan_towers.*;
import Yernar_map.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.Timer;
import javax.imageio.ImageIO;

public class TowerDefenseGame extends GamePanel {
    private List<Tower> towers = new ArrayList<>();
    private List<Enemy> enemies = new ArrayList<>();
    private List<List<Point>> gamePaths;
    private int money = 300;
    private int lives = 5;
    private int currentWave = 0;
    private boolean gameOver = false;
    private boolean gameWon = false;
    private boolean waveInProgress = false;
    private Timer gameTimer;
    private Timer waveTimer;
    private int selectedTowerType = -1; // -1: none, 0: Basic, 1: Area, 2: Slow
    private SoundPlayer soundPlayer;
    private int score = 0;
    private int enemiesKilled = 0;
    private int enemiesSpawnedInCurrentWave = 0;
    private int enemiesKilledInCurrentWave = 0;
    private BufferedImage basicTowerImage;
    private BufferedImage areaTowerImage;
    private BufferedImage slowTowerImage;
    private BufferedImage coinImage;
    private BufferedImage heartImage;
    private BufferedImage waveImage;
    private BufferedImage scoreImage;
    private BufferedImage enemyImage;
    private Font gameFont;
    private Color hudBackgroundColor = new Color(0, 0, 0, 180);
    private BufferedImage[] enemyImages = new BufferedImage[3];

    public TowerDefenseGame(String path) {
        super(path);
        gamePaths = findAllPaths();
        
        if (gamePaths == null || gamePaths.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Invalid map: No path found from start to finish!");
            return;
        }
        
        // Важно для обработки клавиатурных событий
        setFocusable(true);
        
        // Добавляем глобальные горячие клавиши
        addGlobalKeyBindings();
        
        try {
            soundPlayer = new SoundPlayer();
            
            // Load tower images
            basicTowerImage = ImageIO.read(new File("res/basictower.png"));
            areaTowerImage = ImageIO.read(new File("res/areatower.png"));
            slowTowerImage = ImageIO.read(new File("res/slowtower.jpg"));
            
            // Не загружаем текстуры для интерфейса, будем использовать цветные круги
            coinImage = null;
            heartImage = null;
            waveImage = null;
            scoreImage = null;
            enemyImage = null;
            
            // Load custom font
            gameFont = new Font("Arial", Font.BOLD, 16);
        } catch (IOException e) {
            System.out.println("Warning: Resources could not be loaded: " + e.getMessage());
        }
        
        setupMouseListeners();
        setupKeyListeners();
        setupGameTimer();
        setupWaveTimer();
        try {
            enemyImages[0] = ImageIO.read(new File("res/enemy1.png"));
            enemyImages[1] = ImageIO.read(new File("res/enemy2.png"));
            enemyImages[2] = ImageIO.read(new File("res/enemy3.png"));
        } catch (IOException e) {
            System.out.println("Failed to load enemy images: " + e.getMessage());
        }
    }
    
    private void addGlobalKeyBindings() {
        // Используем InputMap и ActionMap для надежной обработки клавиш
        InputMap inputMap = getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW);
        ActionMap actionMap = getActionMap();
        
        // Клавиша 1 - выбор базовой башни
        inputMap.put(KeyStroke.getKeyStroke("1"), "selectBasicTower");
        actionMap.put("selectBasicTower", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectedTowerType = 0;
                System.out.println("Selected Basic Tower (via key binding)");
                repaint();
            }
        });
        
        // Клавиша 2 - выбор башни с областным уроном
        inputMap.put(KeyStroke.getKeyStroke("2"), "selectAreaTower");
        actionMap.put("selectAreaTower", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectedTowerType = 1;
                System.out.println("Selected Area Tower (via key binding)");
                repaint();
            }
        });
        
        // Клавиша 3 - выбор замедляющей башни
        inputMap.put(KeyStroke.getKeyStroke("3"), "selectSlowTower");
        actionMap.put("selectSlowTower", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                selectedTowerType = 2;
                System.out.println("Selected Slow Tower (via key binding)");
                repaint();
            }
        });
        
        // Пробел - запуск следующей волны
        inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_SPACE, 0), "startNextWave");
        actionMap.put("startNextWave", new AbstractAction() {
            @Override
            public void actionPerformed(java.awt.event.ActionEvent e) {
                if (!waveInProgress && !gameOver) {
                    System.out.println("Starting next wave (via key binding)");
                    startNextWave();
                }
            }
        });
    }
    
    private void setupKeyListeners() {
        // Удаляем старые слушатели клавиатуры, если они есть
        for (KeyListener kl : getKeyListeners()) {
            removeKeyListener(kl);
        }
        
        // Добавляем новый слушатель клавиатуры
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("Key pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
                
                switch (e.getKeyCode()) {
                    case KeyEvent.VK_1:
                        selectedTowerType = 0; // Basic Tower
                        System.out.println("Selected Basic Tower");
                        break;
                    case KeyEvent.VK_2:
                        selectedTowerType = 1; // Area Tower
                        System.out.println("Selected Area Tower");
                        break;
                    case KeyEvent.VK_3:
                        selectedTowerType = 2; // Slow Tower
                        System.out.println("Selected Slow Tower");
                        break;
                    case KeyEvent.VK_SPACE:
                        if (!waveInProgress && !gameOver) {
                            System.out.println("Starting next wave");
                            startNextWave();
                        }
                        break;
                }
                repaint();
            }
        });
    }
    
    private void setupMouseListeners() {
        // Удаляем старые слушатели мыши, если они есть
        for (MouseListener ml : getMouseListeners()) {
            removeMouseListener(ml);
        }
        
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int x = e.getX() / TILE_SIZE;
                int y = e.getY() / TILE_SIZE;
                
                // Проверяем, что клик был в пределах карты
                if (y >= map.length) {
                    // Клик в панели выбора башен
                    if (y == map.length && x >= 0 && x <= 4) {
                        selectedTowerType = x;
                        System.out.println("Selected tower type: " + selectedTowerType);
                        repaint();
                    }
                    return;
                }
                
                // Проверяем, что клик был на допустимой клетке
                if (x < 0 || x >= map[0].length || y < 0 || y >= map.length) {
                    return;
                }
                
                // Нельзя строить на дороге, старте или финише
                if (map[y][x] == 1 || map[y][x] == 2 || map[y][x] == 3 || map[y][x] == 4) {
                    System.out.println("Cannot build on this tile!");
                    return;
                }
                
                // Проверяем, нет ли уже башни на этой клетке
                for (Tower tower : towers) {
                    if (tower.getPosition().x == x && tower.getPosition().y == y) {
                        System.out.println("Tower already exists at this position!");
                        return;
                    }
                }
                
                // Строим башню в зависимости от выбранного типа
                Tower newTower = null;
                int cost = 0;
                
                switch (selectedTowerType) {
                    case 0: // Basic Tower
                        cost = 100;
                        if (money >= cost) {
                            newTower = new BasicTower(new Point(x, y));
                            if (basicTowerImage != null) {
                                newTower.setTowerImage(basicTowerImage);
                            }
                        }
                        break;
                    case 1: // Area Tower
                        cost = 150;
                        if (money >= cost) {
                            newTower = new AreaTower(new Point(x, y));
                            if (areaTowerImage != null) {
                                newTower.setTowerImage(areaTowerImage);
                            }
                        }
                        break;
                    case 2: // Slow Tower
                        cost = 150;
                        if (money >= cost) {
                            newTower = new SlowTower(new Point(x, y));
                            if (slowTowerImage != null) {
                                newTower.setTowerImage(slowTowerImage);
                            }
                        }
                        break;
                    default:
                        System.out.println("No tower type selected!");
                        return;
                }
                
                if (newTower != null) {
                    money -= cost;
                    towers.add(newTower);
                    try {
                        soundPlayer.playSound("build");
                    } catch (Exception ex) {
                        System.out.println("Error playing sound: " + ex.getMessage());
                    }
                    System.out.println("Tower built at " + x + ", " + y);
                } else if (cost > 0) {
                    System.out.println("Not enough money to build this tower!");
                }
                
                repaint();
            }
        });
    }
    
    private void setupGameTimer() {
        gameTimer = new Timer(16, e -> {  // ~60 FPS
            if (gameOver) return;
            
            // Move enemies
            Iterator<Enemy> enemyIterator = enemies.iterator();
            while (enemyIterator.hasNext()) {
                Enemy enemy = enemyIterator.next();
                enemy.move();
                
                // Check if enemy reached the end
                if (enemy.hasReachedEnd()) {
                    // Враг достиг финиша - отмечаем его, удаляем из игры и уменьшаем HP игрока
                    lives--;
                    try {
                        soundPlayer.playSound("damage");
                    } catch (Exception ex) {
                        System.out.println("Error playing sound: " + ex.getMessage());
                    }
                    
                    enemiesKilledInCurrentWave++;
                    System.out.println("Enemy reached the end! Lives remaining: " + lives);
                    enemyIterator.remove();
                    
                    if (lives <= 0) {
                        gameOver = true;
                        JOptionPane.showMessageDialog(this, "Game Over! You lost all your lives.");
                    }
                } else if (enemy.isDead()) {
                    // Враг убит башней - получаем деньги и очки
                    money += 10 + (currentWave * 2);
                    score += 20 + (currentWave * 5);
                    enemiesKilled++;
                    enemiesKilledInCurrentWave++;
                    try {
                        soundPlayer.playSound("coin");
                    } catch (Exception ex) {
                        System.out.println("Error playing sound: " + ex.getMessage());
                    }
                    enemyIterator.remove();
                    System.out.println("Enemy killed! Money: " + money + ", Score: " + score);
                }
            }
            
            // Tower attacks
            for (Tower tower : towers) {
                if (tower.canAttack()) {
                    boolean hasTarget = false;
                    
                    if (tower instanceof BasicTower) {
                        for (Enemy enemy : enemies) {
                            if (tower.isInRange(enemy.getPosition())) {
                                enemy.takeDamage(tower.damage);
                                hasTarget = true;
                                BasicTower basicTower = (BasicTower) tower;
                                basicTower.setTargetPosition(enemy.getPosition());
                                tower.attack();
                                try {
                                    soundPlayer.playSound("shoot");
                                } catch (Exception ex) {
                                    System.out.println("Error playing sound: " + ex.getMessage());
                                }
                                break;  // Basic tower only attacks one enemy
                            }
                        }
                    } else if (tower instanceof AreaTower) {
                        for (Enemy enemy : enemies) {
                            if (tower.isInRange(enemy.getPosition())) {
                                enemy.takeDamage(tower.damage);
                                hasTarget = true;
                            }
                        }
                        if (hasTarget) {
                            tower.attack();
                            try {
                                soundPlayer.playSound("explosion");
                            } catch (Exception ex) {
                                System.out.println("Error playing sound: " + ex.getMessage());
                            }
                        }
                    } else if (tower instanceof SlowTower) {
                        SlowTower slowTower = (SlowTower) tower;
                        for (Enemy enemy : enemies) {
                            if (tower.isInRange(enemy.getPosition())) {
                                enemy.applySlowEffect(slowTower.getSlowAmount(), slowTower.getSlowDuration());
                                hasTarget = true;
                            }
                        }
                        if (hasTarget) {
                            tower.attack();
                            try {
                                soundPlayer.playSound("slow");
                            } catch (Exception ex) {
                                System.out.println("Error playing sound: " + ex.getMessage());
                            }
                        }
                    }
                }
            }
            
            // Check if wave is complete
            if (waveInProgress && !waveTimer.isRunning() && enemiesKilledInCurrentWave >= 15) {
                waveInProgress = false;
                enemiesKilledInCurrentWave = 0;
                enemiesSpawnedInCurrentWave = 0;
                System.out.println("Wave " + currentWave + " completed! All enemies defeated.");
                
                if (currentWave >= 10) {
                    gameOver = true;
                    gameWon = true;
                    int finalScore = score + (lives * 50) + (money * 2);
                    JOptionPane.showMessageDialog(this, 
                        "Congratulations! You've completed all waves!\n" +
                        "Final Score: " + finalScore + "\n" +
                        "Enemies Defeated: " + enemiesKilled + "\n" +
                        "Lives Remaining: " + lives + "\n" +
                        "Money Remaining: " + money);
                }
            }
            
            repaint();
        });
        gameTimer.start();
    }
    
    private void setupWaveTimer() {
        waveTimer = new Timer(1000, e -> {  // Спавн врагов каждую секунду
            if (gameOver || !waveInProgress) return;
            
            // Фиксированное количество врагов в каждой волне - 15
            int maxEnemiesPerWave = 15;
            // Максимальное количество врагов на экране одновременно
            int maxEnemiesOnScreen = 3 + (currentWave / 2);
            
            int spawnedEnemies = 0;
            for (Enemy enemy : enemies) {
                if (!enemy.isDead()) {
                    spawnedEnemies++;
                }
            }
            
            // Если уже достаточно врагов в игре, не спавним новых
            if (spawnedEnemies >= maxEnemiesOnScreen) {
                return;
            }
            
            // Если уже создали 15 врагов для этой волны, останавливаем таймер
            if (enemiesSpawnedInCurrentWave >= maxEnemiesPerWave) {
                ((Timer)e.getSource()).stop();
                System.out.println("Wave " + currentWave + " spawning completed. Total enemies: " + maxEnemiesPerWave);
                return;
            }
            
            // Спавним нового врага с увеличенными характеристиками в зависимости от волны
            // Здоровье врагов увеличивается с каждой волной
            int baseHealth = 50;
            int healthPerWave = 20 * currentWave;  // Значительное увеличение здоровья с каждой волной
            int health = baseHealth + healthPerWave;
            
            // Скорость врагов увеличивается с каждой волной
            double baseSpeed = 0.8;
            double speedPerWave = 0.15 * currentWave;  // Значительное увеличение скорости с каждой волной
            double speed = baseSpeed + (speedPerWave / 10.0);  // Делим на 10 для более плавного увеличения
            
            // Выбираем случайный путь для врага
            List<Point> selectedPath = null;
            int randomPathIndex = 0;
            if (!gamePaths.isEmpty()) {
                randomPathIndex = (int)(Math.random() * gamePaths.size());
                selectedPath = gamePaths.get(randomPathIndex);
            }
            
            if (selectedPath == null || selectedPath.isEmpty()) {
                System.out.println("Error: No valid path found for enemy!");
                return;
            }

            Enemy enemy = new Enemy(selectedPath, health, speed);
            int randomIndex = (int)(Math.random() * 3); // от 0 до 2
            enemy.setEnemyImage(enemyImages[randomIndex]);
            enemies.add(enemy);
            
            // Set enemy image if available
            if (enemyImage != null) {
                enemy.setEnemyImage(enemyImage);
            }
            
            enemies.add(enemy);
            enemiesSpawnedInCurrentWave++;
            System.out.println("Enemy spawned, total: " + enemies.size() + ", Wave: " + currentWave + 
                              ", Health: " + health + ", Speed: " + String.format("%.2f", speed) +
                              ", Spawned in wave: " + enemiesSpawnedInCurrentWave + "/15" +
                              ", Path: " + randomPathIndex);
        });
    }
    
    private void startNextWave() {
        if (currentWave >= 10) {
            gameOver = true;
            gameWon = true;
            int finalScore = score + (lives * 50) + (money * 2);
            JOptionPane.showMessageDialog(this, 
                "Congratulations! You've completed all waves!\n" +
                "Final Score: " + finalScore + "\n" +
                "Enemies Defeated: " + enemiesKilled + "\n" +
                "Lives Remaining: " + lives + "\n" +
                "Money Remaining: " + money);
            return;
        }
        
        currentWave++;
        waveInProgress = true;
        enemiesSpawnedInCurrentWave = 0;
        enemiesKilledInCurrentWave = 0;
        waveTimer.start();
        try {
            soundPlayer.playSound("wave");
        } catch (Exception ex) {
            System.out.println("Error playing sound: " + ex.getMessage());
        }
        System.out.println("Wave " + currentWave + " started");
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        
        // Enable anti-aliasing for smoother graphics
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw towers
        for (Tower tower : towers) {
            tower.draw(g);
        }
        
        // Draw enemies
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        
        // Вычисляем размеры HUD
        int panelWidth = getWidth();
        int hudY = map.length * TILE_SIZE + 10;
        int hudHeight = 100;
        
        // Draw HUD background
        g2d.setColor(hudBackgroundColor);
        g2d.fillRoundRect(10, hudY, panelWidth - 20, hudHeight, 15, 15);
        
        // Set font
        g2d.setFont(gameFont);
        
        // Draw resources
        int resourceY = hudY + 30;
        int iconSize = 24;
        int textOffset = 30;
        
        // Money
        g2d.setColor(Color.YELLOW);
        if (coinImage != null) {
            g2d.drawImage(coinImage, 20, resourceY, iconSize, iconSize, null);
        } else {
            g2d.fillOval(20, resourceY, iconSize, iconSize);
        }
        g2d.drawString(": " + money, 20 + textOffset, resourceY + 18);
        
        // Lives
        g2d.setColor(lives <= 5 ? Color.RED : Color.GREEN);  // Красный цвет, если осталось мало жизней
        if (heartImage != null) {
            g2d.drawImage(heartImage, 120, resourceY, iconSize, iconSize, null);
        } else {
            g2d.fillOval(120, resourceY, iconSize, iconSize);
        }
        g2d.drawString(": " + lives, 120 + textOffset, resourceY + 18);
        
        // Wave
        g2d.setColor(Color.CYAN);
        if (waveImage != null) {
            g2d.drawImage(waveImage, 220, resourceY, iconSize, iconSize, null);
        } else {
            g2d.fillOval(220, resourceY, iconSize, iconSize);
        }
        g2d.drawString(": " + currentWave + "/10", 220 + textOffset, resourceY + 18);
        
        // Score
        g2d.setColor(Color.GREEN);
        if (scoreImage != null) {
            g2d.drawImage(scoreImage, 320, resourceY, iconSize, iconSize, null);
        } else {
            g2d.fillOval(320, resourceY, iconSize, iconSize);
        }
        g2d.drawString(": " + score, 320 + textOffset, resourceY + 18);
        
        // Draw selected tower info
        int towerInfoY = resourceY + 40;
        g2d.setColor(Color.WHITE);
        g2d.drawString("Selected Tower: ", 20, towerInfoY);
        
        switch (selectedTowerType) {
            case 0:
                g2d.setColor(Color.CYAN);
                g2d.drawString("Basic Tower ($100)", 150, towerInfoY);
                g2d.drawString("Single target, high damage", 320, towerInfoY);
                break;
            case 1:
                g2d.setColor(Color.ORANGE);
                g2d.drawString("Area Tower ($150)", 150, towerInfoY);
                g2d.drawString("Multiple targets, medium damage", 320, towerInfoY);
                break;
            case 2:
                g2d.setColor(Color.BLUE);
                g2d.drawString("Slow Tower ($150)", 150, towerInfoY);
                g2d.drawString("Slows enemies, low damage", 320, towerInfoY);
                break;
            default:
                g2d.setColor(Color.GRAY);
                g2d.drawString("None (Press 1-3 to select)", 150, towerInfoY);
        }
        
        // Draw game instructions
        if (!waveInProgress && !gameOver) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRoundRect(getWidth()/2 - 180, getHeight()/2 - 60, 360, 120, 20, 20);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Press SPACE to start Wave " + (currentWave + 1), getWidth()/2 - 150, getHeight()/2 - 30);
            g2d.drawString("Press 1-3 to select tower type", getWidth()/2 - 150, getHeight()/2);
            g2d.drawString("Click on map to build towers", getWidth()/2 - 150, getHeight()/2 + 30);
        }
        
        // Draw wave in progress indicator
        if (waveInProgress) {
            g2d.setColor(new Color(255, 0, 0, 100));
            g2d.fillRoundRect(getWidth() - 150, 10, 140, 30, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.drawString("WAVE " + currentWave + " ACTIVE", getWidth() - 140, 30);
            
            // Показываем информацию о текущей волне
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRoundRect(10, 10, 200, 60, 10, 10);
            g2d.setColor(Color.WHITE);
            g2d.drawString("Wave: " + currentWave + "/10", 20, 30);
            
            // Показываем характеристики врагов текущей волны
            int baseHealth = 50;
            int healthPerWave = 20 * currentWave;
            int health = baseHealth + healthPerWave;
            
            double baseSpeed = 0.8;
            double speedPerWave = 0.15 * currentWave;
            double speed = baseSpeed + (speedPerWave / 10.0);
            
            g2d.drawString("Enemy Health: " + health, 20, 50);
            g2d.drawString("Enemy Speed: " + String.format("%.2f", speed), 20, 70);
        }
        
        // Draw game over message
        if (gameOver) {
            g2d.setColor(new Color(0, 0, 0, 180));
            g2d.fillRoundRect(getWidth()/2 - 150, getHeight()/2 - 40, 300, 80, 20, 20);
            g2d.setColor(gameWon ? Color.GREEN : Color.RED);
            g2d.setFont(new Font("Arial", Font.BOLD, 24));
            g2d.drawString(gameWon ? "VICTORY!" : "GAME OVER", getWidth()/2 - 70, getHeight()/2 - 10);
            g2d.setFont(gameFont);
            g2d.drawString("Final Score: " + (score + (lives * 50) + (money * 2)), getWidth()/2 - 80, getHeight()/2 + 20);
        }
    }
}
