package Yernar_map;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.swing.Timer;

public class GamePanel extends JPanel {
    protected final int TILE_SIZE = 50;
    public boolean showTileSelector;
    protected int[][] map;
    protected Map<Integer, Image> tileImages = new HashMap<>();
    protected int selectedTile = 0; // по умолчанию "трава"

    protected List<Image> waterFrames = new ArrayList<>();
    protected int currentWaterFrame = 0;
    protected Timer animationTimer;
    protected boolean showPathPreview = false; // переключатель отображения пути

    public GamePanel(String path) {
        loadMapFromFile(path);
        loadTileImages();
        setPreferredSize(new Dimension(map[0].length * TILE_SIZE, map.length * TILE_SIZE + TILE_SIZE));

        // Важно для обработки клавиатурных событий
        setFocusable(true);
        
        // Добавим клавишу "P" для переключения режима предпросмотра пути
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                System.out.println("GamePanel key pressed: " + KeyEvent.getKeyText(e.getKeyCode()));
                if (e.getKeyCode() == KeyEvent.VK_P) {
                    showPathPreview = !showPathPreview;
                    repaint();
                }
            }
        });
    }

    protected void loadTileImages() {
        try {
            tileImages.put(0, ImageIO.read(new File("res/grass.png")));
            tileImages.put(1, ImageIO.read(new File("res/road.png")));
            tileImages.put(2, ImageIO.read(new File("res/start.png")));
            tileImages.put(3, ImageIO.read(new File("res/finish.png")));

            for (int i = 0; i < 3; i++) {
                waterFrames.add(ImageIO.read(new File("res/water_" + i + ".png")));
            }

            animationTimer = new Timer(300, e -> {
                currentWaterFrame = (currentWaterFrame + 1) % waterFrames.size();
                repaint();
            });
            animationTimer.start();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    protected void loadMapFromFile(String fileName) {
        List<int[]> rows = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    String[] tokens = line.trim().split("\\s+");
                    int[] row = Arrays.stream(tokens).mapToInt(Integer::parseInt).toArray();
                    rows.add(row);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!rows.isEmpty()) {
            map = new int[rows.size()][rows.get(0).length];
            for (int i = 0; i < rows.size(); i++) {
                map[i] = rows.get(i);
            }
        } else {
            int targetRows = 15;
            int targetCols = 20;
            map = new int[targetRows][targetCols];
        }
    }

    public void saveMapToFile(String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int[] row : map) {
                for (int i = 0; i < row.length; i++) {
                    writer.write(row[i] + (i < row.length - 1 ? " " : ""));
                }
                writer.newLine();
            }
            System.out.println("Карта сохранена в файл: " + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                int tile = map[row][col];

                Image tileImage;
                if (tile == 4 && !waterFrames.isEmpty()) {
                    tileImage = waterFrames.get(currentWaterFrame);
                } else {
                    tileImage = tileImages.get(tile);
                }

                if (tileImage != null) {
                    g2d.drawImage(tileImage, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                } else {
                    g2d.setColor(Color.PINK);
                    g2d.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }

                g2d.setColor(new Color(0, 0, 0, 60));
                g2d.setStroke(new BasicStroke(0.5f));
                g2d.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        if (showPathPreview) {
            List<List<Point>> allPaths = findAllPaths();
            if (allPaths != null) {
                for (List<Point> path : allPaths) {
                    g2d.setColor(Color.YELLOW);
                    for (Point p : path) {
                        g2d.fillRect(p.x * TILE_SIZE + TILE_SIZE / 4, p.y * TILE_SIZE + TILE_SIZE / 4, TILE_SIZE / 2, TILE_SIZE / 2);
                    }
                }
            }
        }

        int yOffset = map.length * TILE_SIZE;
        for (int i = 0; i <= 4; i++) {
            Image img = tileImages.get(i);
            if (i == 4 && !waterFrames.isEmpty()) {
                img = waterFrames.get(currentWaterFrame);
            }

            if (img != null) {
                g.drawImage(img, i * TILE_SIZE, yOffset, TILE_SIZE, TILE_SIZE, null);
                if (i == selectedTile) {
                    g.setColor(Color.RED);
                    g.drawRect(i * TILE_SIZE, yOffset, TILE_SIZE, TILE_SIZE);
                    g.drawRect(i * TILE_SIZE + 1, yOffset + 1, TILE_SIZE - 2, TILE_SIZE - 2);
                }
            }
        }
    }

    public List<Point> findPath() {
        Point start = null, end = null;

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                if (map[row][col] == 2) start = new Point(col, row);
                if (map[row][col] == 3) end = new Point(col, row);
            }
        }

        if (start == null || end == null) {
            System.out.println("Старт или финиш не найдены.");
            return null;
        }

        boolean[][] visited = new boolean[map.length][map[0].length];
        Point[][] prev = new Point[map.length][map[0].length];
        Queue<Point> queue = new LinkedList<>();
        queue.add(start);
        visited[start.y][start.x] = true;

        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};

        while (!queue.isEmpty()) {
            Point curr = queue.poll();
            if (curr.equals(end)) break;

            for (int[] dir : directions) {
                int nx = curr.x + dir[0];
                int ny = curr.y + dir[1];

                if (ny >= 0 && ny < map.length && nx >= 0 && nx < map[0].length &&
                        !visited[ny][nx] && isWalkable(map[ny][nx])) {
                    queue.add(new Point(nx, ny));
                    visited[ny][nx] = true;
                    prev[ny][nx] = curr;
                }
            }
        }

        List<Point> path = new LinkedList<>();
        Point step = end;
        while (step != null && !step.equals(start)) {
            path.add(0, step);
            step = prev[step.y][step.x];
        }

        if (step == null) {
            System.out.println("Путь не найден.");
            return null;
        }

        path.add(0, start);
        return path;
    }

    public List<List<Point>> findAllPaths() {
        Point start = null, end = null;

        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                if (map[row][col] == 2) start = new Point(col, row);
                if (map[row][col] == 3) end = new Point(col, row);
            }
        }

        if (start == null || end == null) {
            System.out.println("Старт или финиш не найдены.");
            return null;
        }

        List<List<Point>> allPaths = new ArrayList<>();
        boolean[][] visited = new boolean[map.length][map[0].length];
        List<Point> currentPath = new ArrayList<>();
        currentPath.add(start);
        
        findPathsDFS(start, end, visited, currentPath, allPaths);
        
        if (allPaths.isEmpty()) {
            System.out.println("Путь не найден.");
            // Если путей не найдено, используем стандартный алгоритм BFS
            List<Point> bfsPath = findPath();
            if (bfsPath != null) {
                allPaths.add(bfsPath);
            }
        }
        
        return allPaths;
    }
    
    private void findPathsDFS(Point current, Point end, boolean[][] visited, 
                             List<Point> currentPath, List<List<Point>> allPaths) {
        // Если достигли конца, добавляем путь в список всех путей
        if (current.equals(end)) {
            allPaths.add(new ArrayList<>(currentPath));
            return;
        }
        
        // Отмечаем текущую клетку как посещенную
        visited[current.y][current.x] = true;
        
        // Проверяем все четыре направления
        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}};
        
        for (int[] dir : directions) {
            int nx = current.x + dir[0];
            int ny = current.y + dir[1];
            
            if (ny >= 0 && ny < map.length && nx >= 0 && nx < map[0].length &&
                    !visited[ny][nx] && isWalkable(map[ny][nx])) {
                
                Point next = new Point(nx, ny);
                currentPath.add(next);
                findPathsDFS(next, end, visited, currentPath, allPaths);
                currentPath.remove(currentPath.size() - 1);
            }
        }
        
        // Снимаем отметку о посещении, чтобы эта клетка могла быть использована в других путях
        visited[current.y][current.x] = false;
    }

    private boolean isWalkable(int tile) {
        return tile == 1 || tile == 2 || tile == 3;  // Дорога (1), Старт (2), Финиш (3)
    }
}
