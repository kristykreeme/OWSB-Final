package com.owsb.view.panels;

import com.owsb.model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.text.DecimalFormat;

public class ItemManagementPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JComboBox<String> supplierFilter;
    private JButton addButton;
    private JButton exportButton;
    private JButton importButton;
    private DecimalFormat priceFormat = new DecimalFormat("RM #0.00");
    
    // Item form components
    private JDialog itemDialog;
    private JTextField itemCodeField;
    private JTextField itemNameField;
    private JTextArea descriptionField;
    private JComboBox<Supplier> supplierComboBox;
    private JComboBox<String> categoryComboBox;
    private JComboBox<String> unitComboBox;
    private JTextField stockQuantityField;
    private JTextField unitPriceField;
    private JTextField minimumStockField;
    private JTextField maximumStockField;
    
    // List of all items
    private List<Item> items;
    
    // Modern UI Colors - Updated to match the blue theme from screenshots
    private static final Color PRIMARY_BLUE = new Color(52, 144, 220);
    private static final Color LIGHT_BACKGROUND = new Color(248, 249, 250);
    private static final Color BORDER_COLOR = new Color(224, 224, 224);
    private static final Color TEXT_PRIMARY = new Color(33, 37, 41);
    private static final Color TEXT_SECONDARY = new Color(108, 117, 125);
    private static final Color SUCCESS_GREEN = new Color(40, 167, 69);
    private static final Color DANGER_RED = new Color(220, 53, 69);
    private static final Color WARNING_ORANGE = new Color(255, 193, 7);
    private static final Color EDIT_BLUE = new Color(23, 162, 184);
    private static final Color LOW_STOCK_YELLOW = new Color(255, 235, 59);
    private static final Color OUT_OF_STOCK_RED = new Color(244, 67, 54);
    
    public ItemManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        initializeTable();
        setupUI();
        loadItems();
    }
    
    private void initializeTable() {
        String[] columnNames = {
            "Item Code", "Item Name", "Category", "Supplier", 
            "Unit", "Current Stock", "Min Stock", "Unit Price", 
            "Status", "Actions"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Only Actions column is editable
            }
        };
        
        itemTable = new JTable(tableModel);
        itemTable.setRowHeight(50);
        itemTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        itemTable.setShowGrid(true);
        itemTable.setGridColor(new Color(229, 231, 235));
        itemTable.setIntercellSpacing(new Dimension(1, 1));
        itemTable.setSelectionBackground(new Color(239, 246, 255));
        itemTable.setSelectionForeground(new Color(31, 41, 55));
        itemTable.setBackground(Color.WHITE);
        
        // Set column widths
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(70);  // Item Code
        itemTable.getColumnModel().getColumn(1).setPreferredWidth(110); // Item Name
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(70);  // Category
        itemTable.getColumnModel().getColumn(3).setPreferredWidth(90);  // Supplier
        itemTable.getColumnModel().getColumn(4).setPreferredWidth(40);  // Unit
        itemTable.getColumnModel().getColumn(5).setPreferredWidth(80);  // Current Stock
        itemTable.getColumnModel().getColumn(6).setPreferredWidth(70);  // Min Stock
        itemTable.getColumnModel().getColumn(7).setPreferredWidth(70);  // Unit Price
        itemTable.getColumnModel().getColumn(8).setPreferredWidth(80);  // Status
        itemTable.getColumnModel().getColumn(9).setPreferredWidth(180); // Actions
        
        // Header styling
        JTableHeader header = itemTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(new Color(55, 65, 81));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        header.setReorderingAllowed(false);
        
        // Custom renderer for Status column
        itemTable.getColumnModel().getColumn(8).setCellRenderer(new StatusCellRenderer());
        
        // Custom renderer and editor for Actions column
        itemTable.getColumnModel().getColumn(9).setCellRenderer(new ActionButtonRenderer());
        itemTable.getColumnModel().getColumn(9).setCellEditor(new ActionButtonEditor());
        
        // Default cell renderer for other columns
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBorder(BorderFactory.createEmptyBorder(6, 10, 6, 10));
                setFont(new Font("Segoe UI", Font.PLAIN, 11));
                
                if (isSelected) {
                    setBackground(new Color(239, 246, 255));
                    setForeground(new Color(31, 41, 55));
                } else {
                    setBackground(Color.WHITE);
                    setForeground(new Color(55, 65, 81));
                }
                
                return c;
            }
        };
        
        // Apply to regular columns (not status or actions)
        for (int i = 0; i < 8; i++) {
            itemTable.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }
        
        // Force table to refresh and show buttons
        SwingUtilities.invokeLater(() -> {
            itemTable.revalidate();
            itemTable.repaint();
        });
    }
    
    private void setupUI() {
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Header section
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Title
        JLabel titleLabel = new JLabel("Item Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(31, 41, 55));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Add New Item button
        addButton = new JButton("Add New Item");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(new Color(59, 130, 246));
        addButton.setForeground(Color.WHITE);
        addButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setOpaque(true);
        addButton.addActionListener(e -> showItemDialog(null));
        headerPanel.add(addButton, BorderLayout.EAST);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Search and filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Search field
        searchField = new JTextField("Search items...", 25);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setForeground(new Color(156, 163, 175));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        searchField.setPreferredSize(new Dimension(300, 44));
        
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search items...")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(31, 41, 55));
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search items...");
                    searchField.setForeground(new Color(156, 163, 175));
                }
            }
        });
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!searchField.getText().equals("Search items...")) {
                    filterItems();
                }
            }
        });
        
        // Category filter
        String[] categories = {"All Categories", "Groceries", "Spices", "Grains", "Dairy", "Beverages", "Canned Goods", "Dry Goods", "Fresh Produce"};
        categoryFilter = new JComboBox<>(categories);
        categoryFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryFilter.setPreferredSize(new Dimension(160, 44));
        categoryFilter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)
        ));
        categoryFilter.setBackground(Color.WHITE);
        categoryFilter.addActionListener(e -> filterItems());
        
        // Supplier filter
        String[] suppliers = {"All Suppliers", "ABC Trading", "XYZ Supplies", "Fresh Foods Co.", "Global Imports"};
        supplierFilter = new JComboBox<>(suppliers);
        supplierFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        supplierFilter.setPreferredSize(new Dimension(160, 44));
        supplierFilter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)
        ));
        supplierFilter.setBackground(Color.WHITE);
        supplierFilter.addActionListener(e -> filterItems());
        
        filterPanel.add(searchField);
        filterPanel.add(categoryFilter);
        filterPanel.add(supplierFilter);
        
        // Create main content panel for filters and table
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(itemTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(1200, 320));
        
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(mainContentPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void loadItems() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load items from data source
        items = Item.getAllItems();
        
        // If no items exist, add some sample data for testing
        if (items == null || items.isEmpty()) {
            Object[][] sampleData = {
                {"ITM-001", "Rice (Premium)", "Groceries", "ABC Trading", "kg", "250", "50", "RM 3.50", "Active"},
                {"ITM-002", "Sugar (White)", "Groceries", "ABC Trading", "kg", "15", "30", "RM 2.80", "Low Stock"},
                {"ITM-003", "Oil (Cooking)", "Groceries", "XYZ Supplies", "ltr", "0", "20", "RM 6.00", "Out of Stock"},
                {"ITM-004", "Flour (All Purpose)", "Groceries", "ABC Trading", "kg", "100", "25", "RM 4.20", "Active"},
                {"ITM-005", "Salt (Table)", "Groceries", "Global Imports", "kg", "80", "15", "RM 1.50", "Active"}
            };
            
            for (Object[] row : sampleData) {
                tableModel.addRow(new Object[]{
                    row[0], row[1], row[2], row[3], row[4], 
                    row[5], row[6], row[7], row[8], ""
                });
            }
            return;
        }
        
        for (Item item : items) {
            // Get supplier name
            Supplier supplier = Supplier.getSupplierById(item.getSupplierId());
            String supplierName = supplier != null ? supplier.getSupplierName() : "Unknown";
            
            // Determine status based on stock levels
            String status;
            int currentStock = item.getCurrentStock();
            int minStock = item.getReorderLevel();
            
            if (currentStock == 0) {
                status = "Out of Stock";
            } else if (currentStock <= minStock) {
                status = "Low Stock";
            } else {
                status = "Active";
            }
            
            // Add row to table
            Object[] rowData = {
                item.getItemCode(),
                item.getItemName(),
                item.getCategory(),
                supplierName,
                "kg", // Default unit - you may want to add this field to Item model
                String.valueOf(currentStock),
                String.valueOf(minStock),
                priceFormat.format(item.getUnitPrice()),
                status,
                "" // Actions column (handled by custom renderer)
            };
            
            tableModel.addRow(rowData);
        }
    }
    
    private void filterItems() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        String selectedSupplier = (String) supplierFilter.getSelectedItem();
        
        // Clear table
        tableModel.setRowCount(0);
        
        // If no items loaded, show sample data
        if (items == null || items.isEmpty()) {
            Object[][] sampleData = {
                {"ITM-001", "Rice (Premium)", "Groceries", "ABC Trading", "kg", "250", "50", "RM 3.50", "Active"},
                {"ITM-002", "Sugar (White)", "Groceries", "ABC Trading", "kg", "15", "30", "RM 2.80", "Low Stock"},
                {"ITM-003", "Oil (Cooking)", "Groceries", "XYZ Supplies", "ltr", "0", "20", "RM 6.00", "Out of Stock"},
                {"ITM-004", "Flour (All Purpose)", "Groceries", "ABC Trading", "kg", "100", "25", "RM 4.20", "Active"},
                {"ITM-005", "Salt (Table)", "Groceries", "Global Imports", "kg", "80", "15", "RM 1.50", "Active"}
            };
            
            for (Object[] row : sampleData) {
                String itemName = row[1].toString().toLowerCase();
                String itemCode = row[0].toString().toLowerCase();
                String category = row[2].toString();
                String supplier = row[3].toString();
                
                boolean matchesSearch = searchText.isEmpty() || searchText.equals("search items...") ||
                    itemName.contains(searchText) || itemCode.contains(searchText);
                
                boolean matchesCategory = selectedCategory.equals("All Categories") || category.equals(selectedCategory);
                boolean matchesSupplier = selectedSupplier.equals("All Suppliers") || supplier.equals(selectedSupplier);
                
                if (matchesSearch && matchesCategory && matchesSupplier) {
                    tableModel.addRow(new Object[]{
                        row[0], row[1], row[2], row[3], row[4], 
                        row[5], row[6], row[7], row[8], ""
                    });
                }
            }
            return;
        }
        
        for (Item item : items) {
            // Get supplier name
            Supplier supplier = Supplier.getSupplierById(item.getSupplierId());
            String supplierName = supplier != null ? supplier.getSupplierName() : "Unknown";
            
            // Apply filters
            boolean matchesSearch = searchText.isEmpty() || searchText.equals("search items...") ||
                item.getItemName().toLowerCase().contains(searchText) ||
                item.getItemCode().toLowerCase().contains(searchText);
            
            boolean matchesCategory = selectedCategory.equals("All Categories") || 
                item.getCategory().equals(selectedCategory);
            
            boolean matchesSupplier = selectedSupplier.equals("All Suppliers") || 
                supplierName.equals(selectedSupplier);
            
            if (matchesSearch && matchesCategory && matchesSupplier) {
                // Determine status
                String status;
                int currentStock = item.getCurrentStock();
                int minStock = item.getReorderLevel();
                
                if (currentStock == 0) {
                    status = "Out of Stock";
                } else if (currentStock <= minStock) {
                    status = "Low Stock";
                } else {
                    status = "Active";
                }
                
                // Add row to table
                Object[] rowData = {
                    item.getItemCode(),
                    item.getItemName(),
                    item.getCategory(),
                    supplierName,
                    "kg",
                    String.valueOf(currentStock),
                    String.valueOf(minStock),
                    priceFormat.format(item.getUnitPrice()),
                    status,
                    ""
                };
                
                tableModel.addRow(rowData);
            }
        }
    }
    
    // Custom renderer for Status column
    class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            String status = value.toString();
            setHorizontalAlignment(JLabel.CENTER);
            setFont(new Font("Segoe UI", Font.BOLD, 11));
            
            if (!isSelected) {
                switch (status) {
                    case "Active":
                        setBackground(new Color(34, 197, 94));
                        setForeground(Color.WHITE);
                        break;
                    case "Low Stock":
                        setBackground(new Color(245, 158, 11));
                        setForeground(Color.WHITE);
                        break;
                    case "Out of Stock":
                        setBackground(new Color(220, 38, 127));
                        setForeground(Color.WHITE);
                        break;
                    default:
                        setBackground(Color.WHITE);
                        setForeground(Color.BLACK);
                }
            } else {
                setBackground(new Color(239, 246, 255));
                setForeground(new Color(31, 41, 55));
            }
            
            setBorder(BorderFactory.createEmptyBorder(4, 10, 4, 10));
            setOpaque(true);
            
            return c;
        }
    }
    
    // Custom renderer for Actions column
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton viewButton, editButton, deleteButton;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 8));
            setOpaque(true);
            
            viewButton = new JButton("View");
            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");
            
            styleButton(viewButton, new Color(59, 130, 246));
            styleButton(editButton, new Color(249, 115, 22));
            styleButton(deleteButton, new Color(239, 68, 68));
            
            add(viewButton);
            add(editButton);
            add(deleteButton);
        }
        
        private void styleButton(JButton button, Color bgColor) {
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.BOLD, 10));
            button.setPreferredSize(new Dimension(50, 28));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            if (isSelected) {
                setBackground(new Color(239, 246, 255));
            } else {
                setBackground(Color.WHITE);
            }
            return this;
        }
    }
    
    // Custom editor for Actions column
    class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton viewButton, editButton, deleteButton;
        private int currentRow;
        
        public ActionButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 8));
            panel.setOpaque(true);
            
            viewButton = new JButton("View");
            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");
            
            styleButton(viewButton, new Color(59, 130, 246));
            styleButton(editButton, new Color(249, 115, 22));
            styleButton(deleteButton, new Color(239, 68, 68));
            
            viewButton.addActionListener(e -> {
                viewItem(currentRow);
                fireEditingStopped();
            });
            
            editButton.addActionListener(e -> {
                editItem(currentRow);
                fireEditingStopped();
            });
            
            deleteButton.addActionListener(e -> {
                deleteItem(currentRow);
                fireEditingStopped();
            });
            
            panel.add(viewButton);
            panel.add(editButton);
            panel.add(deleteButton);
        }
        
        private void styleButton(JButton button, Color bgColor) {
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.BOLD, 10));
            button.setPreferredSize(new Dimension(50, 28));
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(Color.WHITE);
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
        
        private void viewItem(int row) {
            String itemCode = (String) tableModel.getValueAt(row, 0);
            Item item = Item.getItemByCode(itemCode);
            
            if (item != null) {
                showItemDetailsDialog(item);
            } else {
                // Show sample data details
                StringBuilder info = new StringBuilder();
                info.append("Item Details:\n\n");
                for (int i = 0; i < itemTable.getColumnCount() - 1; i++) {
                    info.append(itemTable.getColumnName(i)).append(": ")
                        .append(itemTable.getValueAt(row, i)).append("\n");
                }
                JOptionPane.showMessageDialog(itemTable, info.toString(), "Item Details", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        private void editItem(int row) {
            String itemCode = (String) tableModel.getValueAt(row, 0);
            Item item = Item.getItemByCode(itemCode);
            
            if (item != null) {
                showItemDialog(item);
            } else {
                JOptionPane.showMessageDialog(itemTable, "Edit functionality would open an edit dialog for row " + (row + 1));
            }
        }
        
        private void deleteItem(int row) {
            String itemCode = (String) tableModel.getValueAt(row, 0);
            String itemName = (String) tableModel.getValueAt(row, 1);
            
            int result = JOptionPane.showConfirmDialog(
                itemTable, 
                "Are you sure you want to delete item: " + itemName + " (" + itemCode + ")?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                Item item = Item.getItemByCode(itemCode);
                if (item != null && Item.deleteItem(itemCode)) {
                    JOptionPane.showMessageDialog(itemTable, "Item deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadItems();
                } else {
                    // For sample data, just remove from table
                    tableModel.removeRow(row);
                }
            }
        }
    }
    
    private void showItemDetailsDialog(Item item) {
        JDialog detailsDialog = new JDialog();
        detailsDialog.setTitle("Item Details - " + item.getItemName());
        detailsDialog.setSize(500, 400);
        detailsDialog.setLocationRelativeTo(this);
        detailsDialog.setModal(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Create details content
        JPanel detailsPanel = new JPanel(new GridBagLayout());
        detailsPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.anchor = GridBagConstraints.WEST;
        gbc.insets = new Insets(10, 10, 10, 10);
        
        // Add item details
        addDetailRow(detailsPanel, gbc, 0, "Item Code:", item.getItemCode());
        addDetailRow(detailsPanel, gbc, 1, "Item Name:", item.getItemName());
        addDetailRow(detailsPanel, gbc, 2, "Category:", item.getCategory());
        
        Supplier supplier = Supplier.getSupplierById(item.getSupplierId());
        String supplierName = supplier != null ? supplier.getSupplierName() : "Unknown";
        addDetailRow(detailsPanel, gbc, 3, "Supplier:", supplierName);
        
        addDetailRow(detailsPanel, gbc, 4, "Current Stock:", String.valueOf(item.getCurrentStock()));
        addDetailRow(detailsPanel, gbc, 5, "Minimum Stock:", String.valueOf(item.getReorderLevel()));
        addDetailRow(detailsPanel, gbc, 6, "Unit Price:", priceFormat.format(item.getUnitPrice()));
        addDetailRow(detailsPanel, gbc, 7, "Description:", item.getDescription() != null ? item.getDescription() : "No description");
        
        mainPanel.add(detailsPanel, BorderLayout.CENTER);
        
        // Close button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        JButton closeButton = new JButton("Close");
        closeButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        closeButton.setBackground(new Color(59, 130, 246));
        closeButton.setForeground(Color.WHITE);
        closeButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        closeButton.setFocusPainted(false);
        closeButton.setOpaque(true);
        closeButton.addActionListener(e -> detailsDialog.dispose());
        buttonPanel.add(closeButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        detailsDialog.add(mainPanel);
        detailsDialog.setVisible(true);
    }
    
    private void addDetailRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelComponent.setForeground(new Color(33, 37, 41));
        panel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        valueComponent.setForeground(new Color(108, 117, 125));
        panel.add(valueComponent, gbc);
    }
    
    private void showItemDialog(Item item) {
        boolean isEditMode = (item != null);
        
        // Create dialog
        itemDialog = new JDialog();
        itemDialog.setTitle(isEditMode ? "Edit Item" : "Add New Item");
        itemDialog.setSize(600, 700);
        itemDialog.setLocationRelativeTo(this);
        itemDialog.setModal(true);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel(isEditMode ? "Edit Item" : "Add New Item");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(33, 37, 41));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel with scroll
        JPanel formContainer = new JPanel(new BorderLayout());
        formContainer.setBackground(Color.WHITE);
        
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(10, 5, 10, 5);
        
        // Item Code
        addFormField(formPanel, gbc, 0, "Item Code", 
            itemCodeField = createTextField(isEditMode ? item.getItemCode() : Item.generateItemCode(), !isEditMode));
        
        // Item Name
        addFormField(formPanel, gbc, 1, "Item Name", 
            itemNameField = createTextField(isEditMode ? item.getItemName() : "", true));
        
        // Category
        String[] categories = {"Groceries", "Spices", "Grains", "Dairy", "Beverages", "Canned Goods", "Dry Goods", "Fresh Produce"};
        categoryComboBox = new JComboBox<>(categories);
        categoryComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryComboBox.setPreferredSize(new Dimension(300, 40));
        if (isEditMode) {
            categoryComboBox.setSelectedItem(item.getCategory());
        }
        addFormField(formPanel, gbc, 2, "Category", categoryComboBox);
        
        // Primary Supplier
        supplierComboBox = new JComboBox<>();
        supplierComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        supplierComboBox.setPreferredSize(new Dimension(300, 40));
        List<Supplier> suppliers = Supplier.getAllSuppliers();
        for (Supplier supplier : suppliers) {
            supplierComboBox.addItem(supplier);
        }
        if (isEditMode) {
            for (int i = 0; i < supplierComboBox.getItemCount(); i++) {
                Supplier supplier = (Supplier) supplierComboBox.getItemAt(i);
                if (supplier.getSupplierId().equals(item.getSupplierId())) {
                    supplierComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        addFormField(formPanel, gbc, 3, "Primary Supplier", supplierComboBox);
        
        // Unit of Measurement
        String[] units = {"kg", "ltr", "pcs", "box", "pack", "bottle", "can"};
        unitComboBox = new JComboBox<>(units);
        unitComboBox.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        unitComboBox.setPreferredSize(new Dimension(300, 40));
        addFormField(formPanel, gbc, 4, "Unit of Measurement", unitComboBox);
        
        // Unit Price
        addFormField(formPanel, gbc, 5, "Unit Price (RM)", 
            unitPriceField = createTextField(isEditMode ? String.format("%.2f", item.getUnitPrice()) : "0.00", true));
        
        // Minimum Stock Level
        addFormField(formPanel, gbc, 6, "Minimum Stock Level", 
            minimumStockField = createTextField(isEditMode ? String.valueOf(item.getReorderLevel()) : "10", true));
        
        // Maximum Stock Level
        addFormField(formPanel, gbc, 7, "Maximum Stock Level", 
            maximumStockField = createTextField("100", true));
        
        // Description
        descriptionField = new JTextArea(4, 30);
        descriptionField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        descriptionField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        if (isEditMode && item.getDescription() != null) {
            descriptionField.setText(item.getDescription());
        }
        JScrollPane descScrollPane = new JScrollPane(descriptionField);
        descScrollPane.setPreferredSize(new Dimension(300, 100));
        addFormField(formPanel, gbc, 8, "Description", descScrollPane);
        
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(BorderFactory.createEmptyBorder());
        formScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formContainer.add(formScrollPane, BorderLayout.CENTER);
        
        mainPanel.add(formContainer, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setBackground(Color.WHITE);
        cancelButton.setForeground(new Color(33, 37, 41));
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224)),
            BorderFactory.createEmptyBorder(10, 20, 10, 20)
        ));
        cancelButton.setFocusPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.addActionListener(e -> itemDialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = new JButton("Save Item");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(59, 130, 246));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setOpaque(true);
        saveButton.addActionListener(e -> saveItem(isEditMode));
        buttonPanel.add(saveButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        itemDialog.add(mainPanel);
        itemDialog.setVisible(true);
    }
    
    private JTextField createTextField(String text, boolean editable) {
        JTextField field = new JTextField(text);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(300, 40));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224)),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        field.setEditable(editable);
        if (!editable) {
            field.setBackground(new Color(248, 249, 250));
        }
        return field;
    }
    
    private void addFormField(JPanel panel, GridBagConstraints gbc, int row, String labelText, JComponent field) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.BOLD, 14));
        label.setForeground(new Color(33, 37, 41));
        panel.add(label, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        gbc.anchor = GridBagConstraints.WEST;
        panel.add(field, gbc);
    }
    
    private void saveItem(boolean isEditMode) {
        // Validate input
        if (itemNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(itemDialog, "Please enter an item name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get values from form
        String itemCode = itemCodeField.getText().trim();
        String itemName = itemNameField.getText().trim();
        Supplier selectedSupplier = (Supplier) supplierComboBox.getSelectedItem();
        String supplierId = selectedSupplier.getSupplierId();
        String category = (String) categoryComboBox.getSelectedItem();
        String description = descriptionField.getText().trim();
        
        int minimumStock = 0;
        double unitPrice = 0.0;
        
        try {
            minimumStock = Integer.parseInt(minimumStockField.getText().trim());
            if (minimumStock < 0) {
                JOptionPane.showMessageDialog(itemDialog, "Minimum stock cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(itemDialog, "Please enter a valid minimum stock level", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            unitPrice = Double.parseDouble(unitPriceField.getText().trim());
            if (unitPrice < 0) {
                JOptionPane.showMessageDialog(itemDialog, "Unit price cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(itemDialog, "Please enter a valid unit price", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get current stock for edit mode
        int currentStock = 0;
        if (isEditMode) {
            Item existingItem = Item.getItemByCode(itemCode);
            if (existingItem != null) {
                currentStock = existingItem.getCurrentStock();
            }
        }
        
        // Create or update item
        Item newItem = new Item(
            itemCode, 
            itemName, 
            description,
            category, 
            unitPrice, 
            currentStock, // Keep existing stock for edit, 0 for new
            minimumStock, 
            supplierId
        );
        
        boolean success;
        if (isEditMode) {
            success = newItem.updateItem();
        } else {
            success = newItem.saveItem();
        }
        
        if (success) {
            // Update supplier with the item code
            if (!isEditMode) {
                selectedSupplier.addItemCode(itemCode);
                selectedSupplier.updateSupplier();
            }
            
            JOptionPane.showMessageDialog(itemDialog, "Item saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            itemDialog.dispose();
            loadItems();
        } else {
            JOptionPane.showMessageDialog(itemDialog, "Failed to save item", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 