import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class TowerDefenseWindow extends JFrame {
    private TowerDefenseGame gamePanel;
    private MainMenu mainMenu;
    private SettingsMenu settingsMenu;
    private JPanel cardPanel;
    private CardLayout cardLayout;
    private String mapPath;
    private SoundPlayer soundPlayer;
    
    // Screen identifiers
    private static final String MAIN_MENU_SCREEN = "MainMenu";
    private static final String SETTINGS_SCREEN = "Settings";
    private static final String GAME_SCREEN = "Game";
    
    public TowerDefenseWindow(String mapPath) {
        this.mapPath = mapPath;
        setTitle("Tower Defense Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        
        // Initialize sound player
        soundPlayer = new SoundPlayer();
        
        // Set up card layout for screen switching
        cardLayout = new CardLayout();
        cardPanel = new JPanel(cardLayout);
        
        // Create menu screens
        mainMenu = new MainMenu(this);
        settingsMenu = new SettingsMenu(this, soundPlayer);
        
        // Add screens to card panel
        cardPanel.add(mainMenu, MAIN_MENU_SCREEN);
        cardPanel.add(settingsMenu, SETTINGS_SCREEN);
        
        // Add card panel to frame
        add(cardPanel);
        
        // Set initial size based on menu dimensions
        setSize(800, 600);
        setLocationRelativeTo(null);
        
        // Show main menu initially
        cardLayout.show(cardPanel, MAIN_MENU_SCREEN);
        
        // Add window listener for focus management
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                // Set focus to current panel
                Component currentPanel = getCurrentPanel();
                if (currentPanel != null) {
                    currentPanel.requestFocusInWindow();
                }
            }
        });
        
        // Show the window
        setVisible(true);
        
        System.out.println("Tower Defense Game started. Main menu loaded.");
    }
    
    // Method to get the currently visible panel
    private Component getCurrentPanel() {
        for (Component comp : cardPanel.getComponents()) {
            if (comp.isVisible()) {
                return comp;
            }
        }
        return null;
    }
    
    // Method to start the game (called from main menu)
    public void startGame() {
        // Play sound effect
        soundPlayer.playSound("menu_click");
        
        // Create game panel if it doesn't exist
        if (gamePanel == null) {
            gamePanel = new TowerDefenseGame(mapPath);
            gamePanel.setSoundPlayer(soundPlayer);
            cardPanel.add(gamePanel, GAME_SCREEN);
            
            // Adjust window size for game
            gamePanel.setPreferredSize(gamePanel.getPreferredSize());
            pack();
            // Add extra space for HUD
            Dimension size = getSize();
            setSize(size.width, size.height + 50);
            setLocationRelativeTo(null);
        }
        
        // Switch to game screen
        cardLayout.show(cardPanel, GAME_SCREEN);
        gamePanel.requestFocusInWindow();
        gamePanel.resumeGame();
    }
    
    // Method to open settings (called from main menu)
    public void openSettings() {
        soundPlayer.playSound("menu_click");
        cardLayout.show(cardPanel, SETTINGS_SCREEN);
    }
    
    // Method to return to main menu (called from settings)
    public void showMainMenu() {
        soundPlayer.playSound("menu_click");
        cardLayout.show(cardPanel, MAIN_MENU_SCREEN);
    }
    
    // Method to pause game and return to main menu
    public void pauseAndShowMainMenu() {
        if (gamePanel != null) {
            gamePanel.pauseGame();
        }
        showMainMenu();
    }
}
