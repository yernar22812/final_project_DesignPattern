import javax.swing.*;
import java.awt.*;
import java.awt.geom.*;
import javax.swing.border.EmptyBorder;

public class MainMenu extends JPanel {
    private JButton playButton;
    private JButton settingsButton;
    private JButton exitButton;
    private Font titleFont;
    private Font menuFont;
    private TowerDefenseWindow parentWindow;
    private Color accentColor = new Color(48, 204, 116); // #30cc74 - Bright green for minimalist design
    private Timer animationTimer;
    private float animationValue = 0.0f;
    private boolean animationDirection = true;
    
    public MainMenu(TowerDefenseWindow window) {
        this.parentWindow = window;
        setLayout(new BorderLayout());
        
        // Setup animation
        setupAnimation();
        
        // Set background to white for minimalist design
        setBackground(Color.WHITE);
        
        // Create minimal title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setBackground(Color.WHITE);
        titlePanel.setBorder(new EmptyBorder(40, 0, 40, 0));
        
        // Create title with modern font
        JLabel titleLabel = new JLabel("TOWER DEFENSE");
        titleFont = new Font("Helvetica", Font.BOLD, 42);
        menuFont = new Font("Helvetica", Font.PLAIN, 18);
        titleLabel.setFont(titleFont);
        titleLabel.setForeground(new Color(50, 50, 50));
        
        // Add subtitle
        JLabel subtitleLabel = new JLabel("Protect your territory");
        subtitleLabel.setFont(new Font("Helvetica", Font.PLAIN, 16));
        subtitleLabel.setForeground(accentColor);
        
        // Configure title panel
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(0, 10)));
        titlePanel.add(subtitleLabel);
        
        // Create minimal button panel
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        
        // Create buttons with minimalist design
        playButton = createMenuButton("PLAY");
        playButton.addActionListener(e -> startGame());
        
        settingsButton = createMenuButton("SETTINGS");
        settingsButton.addActionListener(e -> openSettings());
        
        exitButton = createMenuButton("EXIT");
        exitButton.addActionListener(e -> System.exit(0));
        
        // Add buttons to panel with spacing
        buttonPanel.add(Box.createVerticalGlue());
        buttonPanel.add(playButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(settingsButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(0, 15)));
        buttonPanel.add(exitButton);
        buttonPanel.add(Box.createVerticalGlue());
        
        // Add panels to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        
        // Add minimal footer
        JPanel infoPanel = new JPanel();
        infoPanel.setBackground(Color.WHITE);
        JLabel versionLabel = new JLabel("Version 1.0 | 2025");
        versionLabel.setForeground(new Color(150, 150, 150));
        versionLabel.setFont(new Font("Helvetica", Font.PLAIN, 12));
        infoPanel.add(versionLabel);
        add(infoPanel, BorderLayout.SOUTH);
        
        // Set preferred size
        setPreferredSize(new Dimension(800, 600));
    }
    
    private void setupAnimation() {
        animationTimer = new Timer(50, e -> {
            // Обновляем значение анимации
            if (animationDirection) {
                animationValue += 0.05f;
                if (animationValue >= 1.0f) {
                    animationValue = 1.0f;
                    animationDirection = false;
                }
            } else {
                animationValue -= 0.05f;
                if (animationValue <= 0.0f) {
                    animationValue = 0.0f;
                    animationDirection = true;
                }
            }
            repaint();
        });
        animationTimer.start();
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Minimalist button design
                if (getModel().isPressed()) {
                    // Darker when pressed
                    g2d.setColor(accentColor.darker());
                } else if (getModel().isRollover()) {
                    // Slightly brighter when hovered
                    g2d.setColor(accentColor.brighter());
                } else {
                    // Normal state
                    g2d.setColor(accentColor);
                }
                
                // Draw a simple rounded rectangle
                g2d.fillRoundRect(0, 0, getWidth(), getHeight(), 4, 4);
                
                // Draw text
                FontMetrics fm = g2d.getFontMetrics();
                Rectangle2D rect = fm.getStringBounds(getText(), g2d);
                
                g2d.setColor(Color.WHITE);
                g2d.drawString(getText(), 
                            (int)(getWidth() - rect.getWidth()) / 2, 
                            (getHeight() - fm.getAscent() - fm.getDescent()) / 2 + fm.getAscent());
            }
            
            @Override
            public boolean isOpaque() {
                return false;
            }
        };
        
        // Configure button
        button.setFont(menuFont);
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 45));
        button.setPreferredSize(new Dimension(200, 45));
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        return button;
    }
    
    private void startGame() {
        parentWindow.startGame();
    }
    
    private void openSettings() {
        parentWindow.openSettings();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        // Draw clean white background
        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), getHeight());
        
        // Add subtle accent color border at the top
        g2d.setColor(accentColor);
        g2d.fillRect(0, 0, getWidth(), 4);
        
        // Add minimal decorative elements
        g2d.setColor(new Color(accentColor.getRed(), accentColor.getGreen(), accentColor.getBlue(), 10));
        g2d.fillRect(0, getHeight() - 40, getWidth(), 40);
    }
}
