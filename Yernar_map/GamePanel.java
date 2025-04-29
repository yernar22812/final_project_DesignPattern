package Yernar_map;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class GamePanel extends JPanel {
    protected final int TILE_SIZE = 50;
    protected int[][] map;
    protected Map<Integer, Image> tileImages = new HashMap<>();
    protected int selectedTile = 0; // по умолчанию "трава"

    public GamePanel(String path) {
        loadMapFromFile(path);
        loadTileImages();
        setPreferredSize(new Dimension(map[0].length * TILE_SIZE, map.length * TILE_SIZE + TILE_SIZE)); // доп. место под панель
    }

    protected void loadTileImages() {
        try {
            tileImages.put(0, ImageIO.read(new File("res/grass.png")));
            tileImages.put(1, ImageIO.read(new File("res/road.png")));
            tileImages.put(2, ImageIO.read(new File("res/start.png"))); // старт
            tileImages.put(3, ImageIO.read(new File("res/finish.png")));
            //tileImages.put(2, ImageIO.read(new File("res/tree.png"))); // добавить надо и другие текстуры
            //tileImages.put(3, ImageIO.read(new File("res/tiles/tree.png")));
            //tileImages.put(4, ImageIO.read(new File("res/tiles/rock.png")));
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
            // если пусто — создаём карту 15x20
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

        // Отрисовка карты
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                int tile = map[row][col];

                // Рисуем фон (например, траву) для каждой клетки
                Image tileImage = tileImages.get(tile);
                if (tileImage != null) {
                    g2d.drawImage(tileImage, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                } else {
                    g2d.setColor(Color.PINK);  // В случае если нет изображения
                    g2d.fillRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
                }

                // Рисуем объекты (например, дерево) поверх фона
                if (tile == 4) {  // Предположим, что 4 это код для дерева
                    Image treeImage = tileImages.get(4);  // Загрузка изображения дерева
                    if (treeImage != null) {
                        g2d.drawImage(treeImage, col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE, null);
                    }
                }

                // Рисуем границы клеток
                g2d.setColor(new Color(0, 0, 0, 60)); // полупрозрачный черный для границ
                g2d.setStroke(new BasicStroke(0.5f)); // тонкая линия
                g2d.drawRect(col * TILE_SIZE, row * TILE_SIZE, TILE_SIZE, TILE_SIZE);
            }
        }

        // Панель выбора тайлов внизу
        int yOffset = map.length * TILE_SIZE; // положение панели внизу
        for (int i = 0; i <= 4; i++) {
            Image img = tileImages.get(i);
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
    // ЛОГИКА ХОДЬБЫ
//    List<Point> enemyPath = gamePanel.findPath();  -> это писать в спавн врагов
//if (enemyPath != null) {
//        for (Point p : enemyPath) {
//            System.out.println("Шаг: " + p.x + "," + p.y);
//        }
//    }






    public List<Point> findPath() {
        Point start = null, end = null;

        // Находим старт и финиш
        for (int row = 0; row < map.length; row++) {
            for (int col = 0; col < map[0].length; col++) {
                if (map[row][col] == 5) start = new Point(col, row);
                if (map[row][col] == 6) end = new Point(col, row);
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

        int[][] directions = {{0,1}, {1,0}, {0,-1}, {-1,0}}; // вниз, вправо, вверх, влево

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

        // Восстанавливаем путь
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

    private boolean isWalkable(int tile) {
        return tile == 1 || tile == 5 || tile == 6; // можно ходить по дороге, старту и финишу
    }


}
