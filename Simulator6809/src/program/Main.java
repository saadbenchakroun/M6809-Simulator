package program;

import java.awt.*;
import javax.swing.*;


public class Main extends JFrame {
    final int progW=1250, progH=690;
 
    // Visual Components
    final private ViewMemory ramView;
    final private ViewMemory romView;

    public Main() {
        // Initialize Hardware

        // Initialize Visual Components
        ramView = new ViewMemory(32768, 0x0000); // 32KB RAM
        romView = new ViewMemory(32768, 0x8000); // 32KB ROM

        // Setup Window
        setTitle("Motorola6809 Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(progW, progH);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Attach menu bar
        setJMenuBar(new MenuBar());

        // Main Content Area
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(new Color(18, 18, 18));
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH; // Components expand vertically and horizontally
        gbc.insets = new Insets(5, 5, 5, 5); // Padding between components
        gbc.weighty = 1.0; // Equal vertical space distribution

        // CPU Column
        gbc.gridx = 0; // First column
        gbc.weightx = 0.30; // 30% width
        JPanel cpuContainer = createStyledPanel("CPU ARCHITECTURE");
        mainContent.add(cpuContainer, gbc);
        
        // Memory Column
        gbc.gridx = 1; // Second column
        gbc.weightx = 0.25; // 25% width
        JPanel memoryContainer = new JPanel(new GridLayout(2, 1, 0, 10));
        JPanel ramContainer = createStyledPanel("RAM (0000-7FFF)");
        JPanel romContainer = createStyledPanel("ROM (8000-FFFF)");
        memoryContainer.setOpaque(false); // Make Transparent

        ramContainer.add(ramView, BorderLayout.CENTER);
        romContainer.add(romView, BorderLayout.CENTER);
        memoryContainer.add(ramContainer);
        memoryContainer.add(romContainer);
        mainContent.add(memoryContainer, gbc);

        // Editor Column
        gbc.gridx = 2; // Third column
        gbc.weightx = 0.45; // 45% width
        JPanel editorContainer = createStyledPanel("ASSEMBLY EDITOR");
        
        mainContent.add(editorContainer, gbc);

        add(mainContent, BorderLayout.CENTER);
        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }

    private JPanel createStyledPanel(String title) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(30, 30, 30));
        panel.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(60, 60, 60)), 
            title, 0, 0, null,
            new Color(0, 255, 255)
        ));
        return panel;
    }

}


