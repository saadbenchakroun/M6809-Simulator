package program;

import java.awt.*;
import javax.swing.*;


public class Main extends JFrame {
    final int progW=1250, progH=690;
 
    // Visual Components
    final private ViewMemory ramView;
    final private ViewMemory romView;
    final private ViewEditor editorView;
    final private ViewCPU cpuView;
    
    // Simulator
    final private Simulator simulator;

    public Main() {
        // Initialize Simulator
        simulator = new Simulator();

        // Initialize Visual Components
        ramView = new ViewMemory(32768, 0x0000); // 32KB RAM
        romView = new ViewMemory(32768, 0x8000); // 32KB ROM
        editorView = new ViewEditor();
        cpuView = new ViewCPU();

        // Setup Window
        setTitle("Motorola6809 Simulator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(progW, progH);
        setLayout(new BorderLayout());
        setLocationRelativeTo(null);

        // Attach menu bar
        setJMenuBar(new CustomMenuBar());

        // Main Content Area
        JPanel mainContent = new JPanel(new GridBagLayout());
        mainContent.setBackground(new Color(18, 18, 18));
        mainContent.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.weighty = 1.0;
        gbc.weightx = 1.0;

        // CPU Column
        gbc.gridx = 0;
        JPanel cpuContainer = createStyledPanel("CPU ARCHITECTURE");
        cpuContainer.add(cpuView, BorderLayout.CENTER);
        mainContent.add(cpuContainer, gbc);
        
        // Memory Column
        gbc.gridx = 1;
        JPanel memoryContainer = new JPanel(new GridLayout(2, 1, 0, 10));
        JPanel ramContainer = createStyledPanel("RAM (0000-7FFF)");
        JPanel romContainer = createStyledPanel("ROM (8000-FFFF)");
        memoryContainer.setOpaque(false);

        ramContainer.add(ramView, BorderLayout.CENTER);
        romContainer.add(romView, BorderLayout.CENTER);
        memoryContainer.add(ramContainer);
        memoryContainer.add(romContainer);
        mainContent.add(memoryContainer, gbc);

        // Editor Column
        gbc.gridx = 2;
        JPanel editorContainer = createStyledPanel("ASSEMBLY EDITOR");
        editorContainer.add(editorView, BorderLayout.CENTER);
        mainContent.add(editorContainer, gbc);

        add(mainContent, BorderLayout.CENTER);
        
        // Connect button actions
        setupButtonActions();
        
        // Initialize display
        updateDisplay();
        
        setVisible(true);
    }

    private void setupButtonActions() {
        // Assemble button
        editorView.btnAssemble.addActionListener(e -> {
            String sourceCode = editorView.getEditorText();
            boolean success = simulator.assemble(sourceCode);
            
            if (success) {
                simulator.loadProgram();
                simulator.reset();
                updateDisplay();
                cpuView.setInstructionText("ASSEMBLED - READY TO EXECUTE");
            } else {
                String error = simulator.getAssemblerError();
                cpuView.setInstructionText("ERROR: " + error);
                JOptionPane.showMessageDialog(this, error, "Assembly Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        // Step button
        editorView.btnStep.addActionListener(e -> {
            String instruction = simulator.step();
            cpuView.setInstructionText(instruction);
            updateDisplay();
        });
        
        // Run button
        editorView.btnRun.addActionListener(e -> {
            while (!simulator.getCPU().halted) {
                String instruction = simulator.step();
                cpuView.setInstructionText(instruction);
                updateDisplay();
                
                // Small delay to see execution
                try {
                    Thread.sleep(100);
                } catch (InterruptedException ex) {
                    break;
                }
            }
        });
        
        // Reset button
        editorView.btnReset.addActionListener(e -> {
            simulator.reset();
            updateDisplay();
            cpuView.setInstructionText("CPU RESET - READY");
        });
    }
    
    private void updateDisplay() {
        CPU cpu = simulator.getCPU();
        Memory memory = simulator.getMemory();
        
        // Update CPU registers
        cpuView.updateRegisters(cpu);
        
        // Update RAM view (first 256 bytes)
        for (int i = 0; i < 256; i++) {
            int value = memory.getRAM().read(i);
            ramView.updateRow(i, value);
        }
        
        // Update ROM view (last 256 bytes to show reset vector)
        for (int i = 0; i < 256; i++) {
            int value = memory.getROM().read(32768 - 256 + i);
            romView.updateRow(32768 - 256 + i, value);
        }
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::new);
    }
}