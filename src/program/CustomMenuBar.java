package program;

import java.awt.*;
import javax.swing.*;

public class CustomMenuBar extends JMenuBar {
    public CustomMenuBar() {
        setBackground(new Color(45, 45, 45));
        setBorder(BorderFactory.createLineBorder(new Color(60, 60, 60)));

        // File Menu
        JMenu fileMenu = createMenu("File");
        fileMenu.add(new JMenuItem("Load"));
        fileMenu.add(new JMenuItem("Save"));

        // Simulation Menu
        JMenu simMenu = createMenu("Simulation");
        simMenu.add(new JMenuItem("Assemble"));
        simMenu.add(new JMenuItem("Step"));
        simMenu.add(new JMenuItem("Reset"));

        // Options Menu
        JMenu optMenu = createMenu("Options");
        optMenu.add(new JMenuItem("Reset"));
        optMenu.add(new JMenuItem("IRQ"));
        optMenu.add(new JMenuItem("FIRQ"));
        optMenu.add(new JMenuItem("NMI"));

        add(fileMenu);
        add(simMenu);
        add(optMenu);
    }

    private JMenu createMenu(String text) {
        JMenu menu = new JMenu(text);
        menu.setForeground(Color.WHITE);
        return menu;
    }
}