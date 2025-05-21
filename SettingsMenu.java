import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class SettingsMenu extends JPanel {
    private JButton backButton;
    private JSlider volumeSlider;
    private JCheckBox soundEnabledCheckbox;
    private Font menuFont;
    private Image backgroundImage;
    private TowerDefenseWindow parentWindow;
    private SoundPlayer soundPlayer;
    
    public SettingsMenu(TowerDefenseWindow window, SoundPlayer soundPlayer) {
        this.parentWindow = window;
        this.soundPlayer = soundPlayer;
        setLayout(new BorderLayout());
        
        // Load background image
        try {
            backgroundImage = new ImageIcon("res/menu_background.png").getImage();
        } catch (Exception e) {
            System.out.println("Could not load menu background: " + e.getMessage());
            // Will use default background color if image fails to load
        }
        
        // Create title panel
        JPanel titlePanel = new JPanel();
        titlePanel.setOpaque(false);
        JLabel titleLabel = new JLabel("НАСТРОЙКИ");
        menuFont = new Font("Arial", Font.BOLD, 36);
        titleLabel.setFont(menuFont);
        titleLabel.setForeground(Color.WHITE);
        titlePanel.add(titleLabel);
        
        // Create settings panel
        JPanel settingsPanel = new JPanel();
        settingsPanel.setLayout(new BoxLayout(settingsPanel, BoxLayout.Y_AXIS));
        settingsPanel.setOpaque(false);
        
        // Sound enabled checkbox
        soundEnabledCheckbox = new JCheckBox("Звук включен", soundPlayer.isSoundEnabled());
        styleComponent(soundEnabledCheckbox);
        soundEnabledCheckbox.addActionListener(e -> toggleSound());
        
        // Volume slider
        JPanel volumePanel = new JPanel();
        volumePanel.setLayout(new BoxLayout(volumePanel, BoxLayout.X_AXIS));
        volumePanel.setOpaque(false);
        volumePanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel volumeLabel = new JLabel("Громкость: ");
        styleComponent(volumeLabel);
        
        volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, (int)(soundPlayer.getVolume() * 100));
        volumeSlider.setOpaque(false);
        volumeSlider.setPreferredSize(new Dimension(200, 50));
        volumeSlider.setMajorTickSpacing(20);
        volumeSlider.setMinorTickSpacing(5);
        volumeSlider.setPaintTicks(true);
        volumeSlider.setPaintLabels(true);
        volumeSlider.setForeground(Color.WHITE);
        volumeSlider.addChangeListener(e -> adjustVolume());
        
        volumePanel.add(volumeLabel);
        volumePanel.add(volumeSlider);
        
        // Back button
        backButton = createMenuButton("НАЗАД");
        backButton.addActionListener(e -> goBack());
        
        // Add components to settings panel with spacing
        settingsPanel.add(Box.createVerticalGlue());
        settingsPanel.add(soundEnabledCheckbox);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 20)));
        settingsPanel.add(volumePanel);
        settingsPanel.add(Box.createRigidArea(new Dimension(0, 40)));
        settingsPanel.add(backButton);
        settingsPanel.add(Box.createVerticalGlue());
        
        // Add panels to main panel
        add(titlePanel, BorderLayout.NORTH);
        add(settingsPanel, BorderLayout.CENTER);
        
        // Set preferred size
        setPreferredSize(new Dimension(800, 600));
    }
    
    private void styleComponent(JComponent component) {
        component.setFont(new Font("Arial", Font.BOLD, 20));
        component.setForeground(Color.WHITE);
        component.setAlignmentX(Component.CENTER_ALIGNMENT);
        if (component instanceof JCheckBox) {
            ((JCheckBox) component).setIcon(new CheckBoxIcon());
        }
    }
    
    private JButton createMenuButton(String text) {
        JButton button = new JButton(text);
        button.setFont(new Font("Arial", Font.BOLD, 24));
        button.setForeground(Color.WHITE);
        button.setBackground(new Color(70, 130, 180));
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setMaximumSize(new Dimension(200, 50));
        
        // Add hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(new Color(100, 149, 237));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(new Color(70, 130, 180));
            }
        });
        
        return button;
    }
    
    private void toggleSound() {
        boolean enabled = soundEnabledCheckbox.isSelected();
        soundPlayer.setSoundEnabled(enabled);
        
        // Play a test sound if enabled
        if (enabled) {
            soundPlayer.playSound("coin");
        }
    }
    
    private void adjustVolume() {
        float volume = volumeSlider.getValue() / 100.0f;
        soundPlayer.setVolume(volume);
        
        // Play a test sound if sound is enabled
        if (soundPlayer.isSoundEnabled()) {
            soundPlayer.playSound("coin");
        }
    }
    
    private void goBack() {
        parentWindow.showMainMenu();
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // Draw background
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        } else {
            // Use gradient background if image is not available
            Graphics2D g2d = (Graphics2D) g;
            GradientPaint gradient = new GradientPaint(
                0, 0, new Color(0, 0, 50),
                0, getHeight(), new Color(0, 0, 100)
            );
            g2d.setPaint(gradient);
            g2d.fillRect(0, 0, getWidth(), getHeight());
        }
    }
    
    // Custom checkbox icon to make it visible on dark background
    private class CheckBoxIcon implements Icon {
        private final int SIZE = 20;
        
        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2d = (Graphics2D) g.create();
            
            // Draw checkbox border
            g2d.setColor(Color.WHITE);
            g2d.drawRect(x, y, SIZE, SIZE);
            
            // Fill if selected
            if (((JCheckBox) c).isSelected()) {
                g2d.setColor(new Color(70, 130, 180));
                g2d.fillRect(x + 2, y + 2, SIZE - 4, SIZE - 4);
                
                // Draw checkmark
                g2d.setColor(Color.WHITE);
                g2d.setStroke(new BasicStroke(2));
                g2d.drawLine(x + 4, y + SIZE/2, x + SIZE/2 - 2, y + SIZE - 4);
                g2d.drawLine(x + SIZE/2 - 2, y + SIZE - 4, x + SIZE - 4, y + 4);
            }
            
            g2d.dispose();
        }
        
        @Override
        public int getIconWidth() {
            return SIZE;
        }
        
        @Override
        public int getIconHeight() {
            return SIZE;
        }
    }
}
