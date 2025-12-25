package program;

import java.awt.*;
import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;

public class ViewMemory extends JPanel {
    final JTable table;
    final DefaultTableModel model;
    int offset; // To show real addresses (e.g. $8000 for ROM)

    public ViewMemory(int size, int addressOffset) {
        this.offset = addressOffset;
        setLayout(new BorderLayout());
        setBackground(new Color(30, 30, 30));

        // Define Columns
        String[] columnNames = {"Address", "Hex", "Binary"};
        model = new DefaultTableModel(columnNames, 0) {
            @Override // Disable cell editing
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };

        table = new JTable(model);
        setupTableStyle();

        // Initial Data Load (Placeholder rows)
        for (int i = 0; i < size && i < 256; i++) {
            updateRow(i, 0x00);
        }

        // add scroll pane to table
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder()); // no border

        add(scrollPane, BorderLayout.CENTER); 
    }

    // Style Memory Table
    private void setupTableStyle() {
        table.setBackground(new Color(30, 30, 30));
        table.setForeground(Color.WHITE);
        table.setGridColor(new Color(60, 60, 60));
        table.setFont(new Font("Monospaced", Font.PLAIN, 12));
        table.setRowHeight(20);

        // Center Alignment
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        // table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
        // table.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);

        // Header styling
        table.getTableHeader().setBackground(new Color(45, 45, 45));
        table.getTableHeader().setForeground(Color.CYAN);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        // table.getTableHeader().setReorderingAllowed(false); // No column reordering
        table.getTableHeader().setResizingAllowed(false); // No column resizing

    }
    
    // update table row
    final public void updateRow(int index, int value) {
        String address = String.format("$%04X", index + offset);
        String hexValue = String.format("%02X", value & 0xFF); // Two-digit hex
        String binaryValue = String.format("%8s", Integer.toBinaryString(value & 0xFF)).replace(' ', '0');

        if (index < model.getRowCount()) { // Update existing row
            model.setValueAt(hexValue, index, 1); // Update Hex Value
            model.setValueAt(binaryValue, index, 2); // Update Binary Value
        } else { // Add new row if it doesn't exist
            model.addRow(new Object[]{address, hexValue, binaryValue});
        }
    }
}
