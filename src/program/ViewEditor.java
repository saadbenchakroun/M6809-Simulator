package program;

import java.awt.*;
import javax.swing.*;

public class ViewEditor extends JPanel {

    private JTextArea editorPane;
    private JTextArea lineNumbers;

    // Buttons
    public JButton btnAssemble, btnStep, btnRun, btnReset;

    public ViewEditor() {
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        // Editor Area
        editorPane = new JTextArea();
        editorPane.setBackground(new Color(20, 20, 20));
        editorPane.setForeground(new Color(220, 220, 220));
        editorPane.setCaretColor(Color.WHITE);
        editorPane.setFont(new Font("Monospaced", Font.PLAIN, 14));
        editorPane.setMargin(new Insets(5, 5, 5, 5));
        editorPane.setTabSize(4);
        
        // Add sample program
        editorPane.setText("; Sample Program\nLDA #$05\nADDA #$03\nSTA $20\nEND");

        lineNumbers = new JTextArea("1");
        lineNumbers.setBackground(new Color(40, 40, 40));
        lineNumbers.setForeground(new Color(120, 120, 120));
        lineNumbers.setFont(new Font("Monospaced", Font.PLAIN, 14));
        lineNumbers.setEditable(false);
        lineNumbers.setMargin(new Insets(5, 5, 5, 5));

        editorPane.addCaretListener(e -> {
            int lineCount = editorPane.getLineCount();
            StringBuilder text = new StringBuilder();
            for (int i = 1; i <= lineCount; i++) {
                text.append(i).append("\n");
            }
            lineNumbers.setText(text.toString());
        });

        JScrollPane scrollPane = new JScrollPane(editorPane);
        scrollPane.setRowHeaderView(lineNumbers);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        add(scrollPane, BorderLayout.CENTER);
        
        // Control Buttons
        JPanel toolbar = new JPanel();
        toolbar.setBackground(new Color(45, 45, 45));
        toolbar.setBorder(BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(70, 70, 70)));
        Color btnHoverColor = Color.cyan;

        btnAssemble = createStyledButton("Assemble", btnHoverColor);
        btnStep = createStyledButton("Step Execution", btnHoverColor);
        btnRun = createStyledButton("Run All", btnHoverColor);
        btnReset = createStyledButton("Reset CPU", btnHoverColor);

        toolbar.add(btnAssemble);
        toolbar.add(new JSeparator(SwingConstants.VERTICAL));
        toolbar.add(btnStep);
        toolbar.add(btnRun);
        toolbar.add(btnReset);

        add(toolbar, BorderLayout.SOUTH);
    }

    // Get editor text
    public String getEditorText() {
        return editorPane.getText();
    }

    private JButton createStyledButton(String text, Color hoverColor) {
        JButton b = new JButton(text);
        b.setFocusable(false);
        b.setFont(new Font("SansSerif", Font.BOLD, 12));
        b.setBackground(new Color(60, 60, 60));
        b.setForeground(Color.WHITE);
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(80, 80, 80)),
            BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));
        // hover effect
        b.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent evt) { 
                b.setBackground(hoverColor); 
                b.setForeground(Color.black);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent evt) { 
                b.setBackground(new Color(60, 60, 60)); 
                b.setForeground(Color.white); 
            }
        });

        return b;
    }
}