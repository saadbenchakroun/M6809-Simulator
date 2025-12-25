package program;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.TitledBorder;

public class ViewCPU extends JPanel {
    final JTextField txtA, txtB, txtD, txtX, txtY, txtS, txtU, txtPC, txtDP, txtCC;
    final JLabel lblCurrentInstruction;

    public ViewCPU() {
        // Set up layout and styling
        setLayout(new BorderLayout(0, 15));
        setBackground(new Color(25, 25, 25));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Instruction Bar
        JPanel instructionBar = new JPanel(new BorderLayout());
        instructionBar.setBackground(new Color(40, 40, 40));
        instructionBar.setBorder(BorderFactory.createLineBorder(Color.CYAN, 1));

        lblCurrentInstruction = new JLabel("READY - LOAD PROGRAM", SwingConstants.CENTER);
        lblCurrentInstruction.setForeground(Color.WHITE);
        lblCurrentInstruction.setFont(new Font("Monospaced", Font.BOLD, 16));
        lblCurrentInstruction.setPreferredSize(new Dimension(0, 45));
        instructionBar.add(lblCurrentInstruction);
        
        // Core Registers
        JPanel centerGrid = new JPanel();
        centerGrid.setLayout(new BoxLayout(centerGrid, BoxLayout.Y_AXIS));
        centerGrid.setOpaque(false);

        // Accumulator (A, B, D)
        JPanel accGroup = new JPanel(new GridBagLayout());
        accGroup.setOpaque(false);
        accGroup.setBorder(createTitledBorder("ACCUMULATORS"));

        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(5, 0, 5, 0);

        txtA = createField(2, "00");
        txtB = createField(2, "00");
        txtD = createField(4, "0000");
        // A and B at the top
        g.gridy = 0; g.gridx = 0; accGroup.add(createLabeledField("A  ", txtA), g);
        g.gridx = 1; accGroup.add(createLabeledField("B  ", txtB), g);
        // D at the bottom
        g.gridy = 1; g.gridx = 0; g.gridwidth = 2;
        accGroup.add(createLabeledField("D  ", txtD), g);

        // Index Registers (X, Y)
        JPanel indexGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        indexGroup.setOpaque(false);
        indexGroup.setBorder(createTitledBorder("INDEX REGISTERS"));

        txtX = createField(4, "0000");
        txtY = createField(4, "0000");
        indexGroup.add(createLabeledField("X  ", txtX));
        indexGroup.add(createLabeledField("Y  ", txtY));

        // Stack Pointers (S, U)
        JPanel pointerGroup = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5));
        pointerGroup.setOpaque(false);
        pointerGroup.setBorder(createTitledBorder("STACK POINTERS"));

        txtS = createField(4, "0000");
        txtU = createField(4, "0000");
        pointerGroup.add(createLabeledField("S  ", txtS));
        pointerGroup.add(createLabeledField("U  ", txtU));

        // Add to the center grid
        centerGrid.add(accGroup);
        centerGrid.add(Box.createVerticalStrut(10));
        centerGrid.add(indexGroup);
        centerGrid.add(Box.createVerticalStrut(10));
        centerGrid.add(pointerGroup);

        // The Status Bar
        JPanel bottomBar = new JPanel(new GridLayout(3, 1, 0, 5));
        bottomBar.setOpaque(false);
        
        // PC Register
        txtPC = createField(4, "0000");
        bottomBar.add(createLabeledField("PC  ", txtPC));
        // DP Register
        txtDP = createField(2, "00");
        bottomBar.add(createLabeledField("DP  ", txtDP));
        // Flags Register
        txtCC = createField(8, "00010000");
        txtCC.setForeground(Color.YELLOW);
        bottomBar.add(createLabeledField("EFHINZVC  ", txtCC));

        // Add Components to the CPU
        add(instructionBar, BorderLayout.NORTH);
        add(centerGrid, BorderLayout.SOUTH);
        add(bottomBar, BorderLayout.CENTER);
    }

    // Update all CPU registers from CPU object
    public void updateRegisters(CPU cpu) {
        txtA.setText(String.format("%02X", cpu.regA));
        txtB.setText(String.format("%02X", cpu.regB));
        txtD.setText(String.format("%04X", cpu.getRegD()));
        txtX.setText(String.format("%04X", cpu.regX));
        txtY.setText(String.format("%04X", cpu.regY));
        txtS.setText(String.format("%04X", cpu.regS));
        txtU.setText(String.format("%04X", cpu.regU));
        txtPC.setText(String.format("%04X", cpu.regPC));
        txtDP.setText(String.format("%02X", cpu.regDP));
        
        // Convert CC to binary string
        String ccBinary = String.format("%8s", Integer.toBinaryString(cpu.regCC & 0xFF)).replace(' ', '0');
        txtCC.setText(ccBinary);
    }
    
    // Set instruction text
    public void setInstructionText(String text) {
        lblCurrentInstruction.setText(text);
    }

    // Create Register Fields
    private JTextField createField(int columns, String defaultText) {
        JTextField field = new JTextField(defaultText, (columns+2));
        field.setFont(new Font("Monospaced", Font.BOLD, 20));
        field.setHorizontalAlignment(JTextField.CENTER);
        field.setEditable(false);
        field.setFocusable(false);
        field.setBackground(new Color(50, 50, 50));
        field.setForeground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(Color.CYAN));
        return field;
    }

    // Create Labeled Field Panels
    private JPanel createLabeledField(String label, JTextField field) {
        JPanel p = new JPanel(new FlowLayout(FlowLayout.CENTER));
        p.setOpaque(false);
        JLabel l = new JLabel(label);
        l.setForeground(Color.WHITE);
        p.add(l);
        p.add(field);
        return p;
    }

    // Titled Border Creator for center grid components
    private TitledBorder createTitledBorder(String title) {
        TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(50, 50, 50)),
            title);
        border.setTitleColor(Color.GRAY);
        border.setTitleFont(new Font("SansSerif", Font.BOLD, 10));
        return border;
    }
}