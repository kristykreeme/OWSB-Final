package com.owsb.view.panels;

import com.owsb.model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import javax.swing.event.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.text.DecimalFormat;

public class InventoryManagementPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTable stockTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JComboBox<String> stockStatusFilter;
    private JButton stockAdjustmentButton;
    private JButton generateReportButton;
    
    // Statistics labels
    private JLabel totalItemsLabel;
    private JLabel lowStockItemsLabel;
    private JLabel outOfStockLabel;
    private JLabel totalStockValueLabel;
    
    // List of all items
    private List<Item> items;
    private List<Supplier> suppliers;
    
    // For number formatting
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private DecimalFormat numberFormat = new DecimalFormat("#,##0");
    
    public InventoryManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        initializeTable();
        setupUI();
        loadItems();
        loadSuppliers();
        updateStatistics();
    }
    
    private void initializeTable() {
        String[] columnNames = {
            "Item Code", "Item Name", "Current Stock", "Min Level", 
            "Max Level", "Last Updated", "Stock Status", "Actions"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions column is editable
            }
        };
        
        stockTable = new JTable(tableModel);
        stockTable.setRowHeight(32);
        stockTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        stockTable.setShowGrid(true);
        stockTable.setGridColor(new Color(229, 231, 235));
        stockTable.setIntercellSpacing(new Dimension(1, 1));
        stockTable.setSelectionBackground(new Color(239, 246, 255));
        stockTable.setSelectionForeground(new Color(31, 41, 55));
        stockTable.setBackground(Color.WHITE);
        
        // Set column widths
        stockTable.getColumnModel().getColumn(0).setPreferredWidth(80);  // Item Code
        stockTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Item Name
        stockTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Current Stock
        stockTable.getColumnModel().getColumn(3).setPreferredWidth(80);  // Min Level
        stockTable.getColumnModel().getColumn(4).setPreferredWidth(80);  // Max Level
        stockTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Last Updated
        stockTable.getColumnModel().getColumn(6).setPreferredWidth(100); // Stock Status
        stockTable.getColumnModel().getColumn(7).setPreferredWidth(150); // Actions
        
        // Header styling
        JTableHeader header = stockTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(new Color(55, 65, 81));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        header.setReorderingAllowed(false);
        
        // Custom renderer for Stock Status column
        stockTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        
        // Custom renderer and editor for Actions column
        stockTable.getColumnModel().getColumn(7).setCellRenderer(new ActionButtonRenderer());
        stockTable.getColumnModel().getColumn(7).setCellEditor(new ActionButtonEditor());
        
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
        for (int i = 0; i < 6; i++) {
            stockTable.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }
    }
    
    private void setupUI() {
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Header section with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Stock Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(31, 41, 55));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Statistics cards panel
        JPanel statsPanel = createStatisticsPanel();
        contentPanel.add(statsPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createStatisticsPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Statistics cards
        JPanel statsCardsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsCardsPanel.setBackground(Color.WHITE);
        statsCardsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Total Items card
        JPanel totalItemsCard = createStatCard("1,245", "Total Items", new Color(52, 144, 220));
        totalItemsLabel = (JLabel) ((JPanel) totalItemsCard.getComponent(0)).getComponent(0);
        statsCardsPanel.add(totalItemsCard);
        
        // Low Stock Items card
        JPanel lowStockCard = createStatCard("15", "Low Stock Items", new Color(52, 144, 220));
        lowStockItemsLabel = (JLabel) ((JPanel) lowStockCard.getComponent(0)).getComponent(0);
        statsCardsPanel.add(lowStockCard);
        
        // Out of Stock card
        JPanel outOfStockCard = createStatCard("3", "Out of Stock", new Color(52, 144, 220));
        outOfStockLabel = (JLabel) ((JPanel) outOfStockCard.getComponent(0)).getComponent(0);
        statsCardsPanel.add(outOfStockCard);
        
        // Total Stock Value card
        JPanel totalValueCard = createStatCard("RM 245,680", "Total Stock Value", new Color(52, 144, 220));
        totalStockValueLabel = (JLabel) ((JPanel) totalValueCard.getComponent(0)).getComponent(0);
        statsCardsPanel.add(totalValueCard);
        
        mainPanel.add(statsCardsPanel, BorderLayout.NORTH);
        
        // Filters and table section
        JPanel tableSection = new JPanel(new BorderLayout());
        tableSection.setBackground(Color.WHITE);
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
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
        String[] categories = {"All Categories", "Grains", "Dairy", "Cooking", "Spices", "Miscellaneous"};
        categoryFilter = new JComboBox<>(categories);
        categoryFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        categoryFilter.setPreferredSize(new Dimension(160, 44));
        categoryFilter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)
        ));
        categoryFilter.setBackground(Color.WHITE);
        categoryFilter.addActionListener(e -> filterItems());
        
        // Stock status filter
        String[] stockStatuses = {"All Stock Status", "Normal", "Low Stock", "Out of Stock"};
        stockStatusFilter = new JComboBox<>(stockStatuses);
        stockStatusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        stockStatusFilter.setPreferredSize(new Dimension(160, 44));
        stockStatusFilter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)
        ));
        stockStatusFilter.setBackground(Color.WHITE);
        stockStatusFilter.addActionListener(e -> filterItems());
        
        // Action buttons
        stockAdjustmentButton = new JButton("Stock Adjustment");
        stockAdjustmentButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        stockAdjustmentButton.setBackground(new Color(59, 130, 246));
        stockAdjustmentButton.setForeground(Color.WHITE);
        stockAdjustmentButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        stockAdjustmentButton.setFocusPainted(false);
        stockAdjustmentButton.setBorderPainted(false);
        stockAdjustmentButton.setOpaque(true);
        stockAdjustmentButton.addActionListener(e -> showStockAdjustmentDialog());
        
        generateReportButton = new JButton("Generate Report");
        generateReportButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        generateReportButton.setBackground(new Color(34, 197, 94));
        generateReportButton.setForeground(Color.WHITE);
        generateReportButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        generateReportButton.setFocusPainted(false);
        generateReportButton.setBorderPainted(false);
        generateReportButton.setOpaque(true);
        generateReportButton.addActionListener(e -> generateStockReport());
        
        filterPanel.add(searchField);
        filterPanel.add(categoryFilter);
        filterPanel.add(stockStatusFilter);
        filterPanel.add(stockAdjustmentButton);
        filterPanel.add(generateReportButton);
        
        tableSection.add(filterPanel, BorderLayout.NORTH);
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(stockTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(1200, 320));
        
        tableSection.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(tableSection, BorderLayout.CENTER);
        
        return mainPanel;
    }
    
    private JPanel createStatCard(String value, String label, Color accentColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(229, 231, 235), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        card.setPreferredSize(new Dimension(250, 100));
        
        // Value and label panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(accentColor);
        valueLabel.setHorizontalAlignment(JLabel.LEFT);
        
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(107, 114, 128));
        descLabel.setHorizontalAlignment(JLabel.LEFT);
        
        contentPanel.add(valueLabel, BorderLayout.NORTH);
        contentPanel.add(descLabel, BorderLayout.SOUTH);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private void loadItems() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load items from data source
        items = Item.getAllItems();
        
        // If no items exist, add some sample data for testing
        if (items == null || items.isEmpty()) {
            Object[][] sampleData = {
                {"ITM-001", "Rice (Premium)", "250 kg", "50 kg", "500 kg", "2025-05-24", "Normal"},
                {"ITM-002", "Sugar (White)", "15 kg", "30 kg", "200 kg", "2025-05-23", "Low Stock"},
                {"ITM-003", "Oil (Cooking)", "0 ltr", "20 ltr", "100 ltr", "2025-05-22", "Out of Stock"}
            };
            
            for (Object[] row : sampleData) {
                tableModel.addRow(new Object[]{
                    row[0], row[1], row[2], row[3], row[4], 
                    row[5], row[6], ""
                });
            }
            return;
        }
        
        for (Item item : items) {
            // Determine status based on stock levels
            String status;
            int currentStock = item.getCurrentStock();
            int minStock = item.getReorderLevel();
            
            if (currentStock == 0) {
                status = "Out of Stock";
            } else if (currentStock <= minStock) {
                status = "Low Stock";
            } else {
                status = "Normal";
            }
            
            // Format stock with units
            String stockDisplay = currentStock + " units";
            String minStockDisplay = minStock + " units";
            String maxStockDisplay = "100 units"; // Default max stock
            
            // Add row to table
            Object[] rowData = {
                item.getItemCode(),
                item.getItemName(),
                stockDisplay,
                minStockDisplay,
                maxStockDisplay,
                "2025-05-24", // Default date - you may want to add this field to Item model
                status,
                "" // Actions column (handled by custom renderer)
            };
            
            tableModel.addRow(rowData);
        }
    }
    
    private void loadSuppliers() {
        suppliers = Supplier.getAllSuppliers();
    }
    
    private void filterItems() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedCategory = (String) categoryFilter.getSelectedItem();
        String selectedStatus = (String) stockStatusFilter.getSelectedItem();
        
        // Clear table
        tableModel.setRowCount(0);
        
        // If no items loaded, show sample data
        if (items == null || items.isEmpty()) {
            Object[][] sampleData = {
                {"ITM-001", "Rice (Premium)", "250 kg", "50 kg", "500 kg", "2025-05-24", "Normal"},
                {"ITM-002", "Sugar (White)", "15 kg", "30 kg", "200 kg", "2025-05-23", "Low Stock"},
                {"ITM-003", "Oil (Cooking)", "0 ltr", "20 ltr", "100 ltr", "2025-05-22", "Out of Stock"}
            };
            
            for (Object[] row : sampleData) {
                String itemName = row[1].toString().toLowerCase();
                String itemCode = row[0].toString().toLowerCase();
                String status = row[6].toString();
                
                boolean matchesSearch = searchText.isEmpty() || searchText.equals("search items...") ||
                    itemName.contains(searchText) || itemCode.contains(searchText);
                
                boolean matchesCategory = selectedCategory.equals("All Categories"); // For sample data
                boolean matchesStatus = selectedStatus.equals("All Stock Status") || status.equals(selectedStatus);
                
                if (matchesSearch && matchesCategory && matchesStatus) {
                    tableModel.addRow(new Object[]{
                        row[0], row[1], row[2], row[3], row[4], 
                        row[5], row[6], ""
                    });
                }
            }
            return;
        }
        
        for (Item item : items) {
            // Apply filters
            boolean matchesSearch = searchText.isEmpty() || searchText.equals("search items...") ||
                item.getItemName().toLowerCase().contains(searchText) ||
                item.getItemCode().toLowerCase().contains(searchText);
            
            boolean matchesCategory = selectedCategory.equals("All Categories") || 
                item.getCategory().equals(selectedCategory);
            
            // Determine status
            String status;
            int currentStock = item.getCurrentStock();
            int minStock = item.getReorderLevel();
            
            if (currentStock == 0) {
                status = "Out of Stock";
            } else if (currentStock <= minStock) {
                status = "Low Stock";
            } else {
                status = "Normal";
            }
            
            boolean matchesStatus = selectedStatus.equals("All Stock Status") || status.equals(selectedStatus);
            
            if (matchesSearch && matchesCategory && matchesStatus) {
                // Format stock with units
                String stockDisplay = currentStock + " units";
                String minStockDisplay = minStock + " units";
                String maxStockDisplay = "100 units";
                
                // Add row to table
                Object[] rowData = {
                    item.getItemCode(),
                    item.getItemName(),
                    stockDisplay,
                    minStockDisplay,
                    maxStockDisplay,
                    "2025-05-24",
                    status,
                    ""
                };
                
                tableModel.addRow(rowData);
            }
        }
    }
    
    private void updateStatistics() {
        int totalItems = 0;
        int lowStockItems = 0;
        int outOfStockItems = 0;
        double totalValue = 0.0;
        
        if (items != null && !items.isEmpty()) {
            totalItems = items.size();
            
            for (Item item : items) {
                int currentStock = item.getCurrentStock();
                int minStock = item.getReorderLevel();
                
                if (currentStock == 0) {
                    outOfStockItems++;
                } else if (currentStock <= minStock) {
                    lowStockItems++;
                }
                
                totalValue += currentStock * item.getUnitPrice();
            }
        } else {
            // Sample data statistics
            totalItems = 1245;
            lowStockItems = 15;
            outOfStockItems = 3;
            totalValue = 245680.0;
        }
        
        // Update labels
        totalItemsLabel.setText(numberFormat.format(totalItems));
        lowStockItemsLabel.setText(String.valueOf(lowStockItems));
        outOfStockLabel.setText(String.valueOf(outOfStockItems));
        totalStockValueLabel.setText("RM " + numberFormat.format(totalValue));
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
                    case "Normal":
                        setBackground(new Color(34, 197, 94));
                        setForeground(Color.WHITE);
                        break;
                    case "Low Stock":
                        setBackground(new Color(245, 158, 11));
                        setForeground(Color.WHITE);
                        break;
                    case "Out of Stock":
                        setBackground(new Color(239, 68, 68));
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
        private JButton adjustButton, historyButton;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
            setOpaque(true);
            
            adjustButton = new JButton("Adjust");
            historyButton = new JButton("History");
            
            styleButton(adjustButton, new Color(59, 130, 246));
            styleButton(historyButton, new Color(249, 115, 22));
            
            add(adjustButton);
            add(historyButton);
        }
        
        private void styleButton(JButton button, Color bgColor) {
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.BOLD, 10));
            button.setPreferredSize(new Dimension(50, 22));
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
        private JButton adjustButton, historyButton;
        private int currentRow;
        
        public ActionButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
            panel.setOpaque(true);
            
            adjustButton = new JButton("Adjust");
            historyButton = new JButton("History");
            
            styleButton(adjustButton, new Color(59, 130, 246));
            styleButton(historyButton, new Color(249, 115, 22));
            
            adjustButton.addActionListener(e -> {
                adjustStock(currentRow);
                fireEditingStopped();
            });
            
            historyButton.addActionListener(e -> {
                showHistory(currentRow);
                fireEditingStopped();
            });
            
            panel.add(adjustButton);
            panel.add(historyButton);
        }
        
        private void styleButton(JButton button, Color bgColor) {
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.BOLD, 10));
            button.setPreferredSize(new Dimension(50, 22));
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
        
        private void adjustStock(int row) {
            String itemCode = (String) tableModel.getValueAt(row, 0);
            String itemName = (String) tableModel.getValueAt(row, 1);
            
            // Find the item
            Item item = null;
            if (items != null) {
                for (Item i : items) {
                    if (i.getItemCode().equals(itemCode)) {
                        item = i;
                        break;
                    }
                }
            }
            
            if (item != null) {
                showStockAdjustmentDialog(item);
            } else {
                JOptionPane.showMessageDialog(stockTable, "Stock adjustment for " + itemName + " (" + itemCode + ")");
            }
        }
        
        private void showHistory(int row) {
            String itemCode = (String) tableModel.getValueAt(row, 0);
            String itemName = (String) tableModel.getValueAt(row, 1);
            
            JOptionPane.showMessageDialog(stockTable, "Stock history for " + itemName + " (" + itemCode + ")");
        }
    }
    
    private void showStockAdjustmentDialog() {
        JOptionPane.showMessageDialog(this, "Please select an item from the table to adjust stock", "No Item Selected", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void showStockAdjustmentDialog(Item item) {
        JDialog adjustmentDialog = new JDialog();
        adjustmentDialog.setTitle("Stock Adjustment");
        adjustmentDialog.setSize(600, 700);
        adjustmentDialog.setLocationRelativeTo(this);
        adjustmentDialog.setModal(true);
        adjustmentDialog.setResizable(false);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 40, 40, 40));
        
        // Header
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("Stock Adjustment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(31, 41, 55));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Form panel
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Item Code
        JPanel itemCodePanel = createFormField("Item Code", item.getItemCode(), false);
        formPanel.add(itemCodePanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Item Name
        JPanel itemNamePanel = createFormField("Item Name", item.getItemName(), false);
        formPanel.add(itemNamePanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Current Stock
        JPanel currentStockPanel = createFormField("Current Stock", item.getCurrentStock() + " kg", false);
        formPanel.add(currentStockPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Adjustment Type
        JPanel adjustmentTypePanel = new JPanel();
        adjustmentTypePanel.setLayout(new BoxLayout(adjustmentTypePanel, BoxLayout.Y_AXIS));
        adjustmentTypePanel.setBackground(Color.WHITE);
        adjustmentTypePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel adjustmentTypeLabel = new JLabel("Adjustment Type");
        adjustmentTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        adjustmentTypeLabel.setForeground(new Color(55, 65, 81));
        adjustmentTypeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] types = {"Select Type", "Stock In", "Stock Out", "Stock Transfer", "Stock Adjustment", "Damaged/Lost"};
        JComboBox<String> typeCombo = new JComboBox<>(types);
        typeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        typeCombo.setPreferredSize(new Dimension(500, 44));
        typeCombo.setMaximumSize(new Dimension(500, 44));
        typeCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)
        ));
        typeCombo.setBackground(Color.WHITE);
        typeCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        adjustmentTypePanel.add(adjustmentTypeLabel);
        adjustmentTypePanel.add(Box.createVerticalStrut(8));
        adjustmentTypePanel.add(typeCombo);
        
        formPanel.add(adjustmentTypePanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Quantity
        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new BoxLayout(quantityPanel, BoxLayout.Y_AXIS));
        quantityPanel.setBackground(Color.WHITE);
        quantityPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel quantityLabel = new JLabel("Quantity");
        quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quantityLabel.setForeground(new Color(55, 65, 81));
        quantityLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField quantityField = new JTextField("Enter quantity");
        quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quantityField.setForeground(new Color(156, 163, 175));
        quantityField.setPreferredSize(new Dimension(500, 44));
        quantityField.setMaximumSize(new Dimension(500, 44));
        quantityField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        quantityField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        quantityField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (quantityField.getText().equals("Enter quantity")) {
                    quantityField.setText("");
                    quantityField.setForeground(new Color(31, 41, 55));
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (quantityField.getText().isEmpty()) {
                    quantityField.setText("Enter quantity");
                    quantityField.setForeground(new Color(156, 163, 175));
                }
            }
        });
        
        quantityPanel.add(quantityLabel);
        quantityPanel.add(Box.createVerticalStrut(8));
        quantityPanel.add(quantityField);
        
        formPanel.add(quantityPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Reference (PO/Invoice)
        JPanel referencePanel = new JPanel();
        referencePanel.setLayout(new BoxLayout(referencePanel, BoxLayout.Y_AXIS));
        referencePanel.setBackground(Color.WHITE);
        referencePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel referenceLabel = new JLabel("Reference (PO/Invoice)");
        referenceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        referenceLabel.setForeground(new Color(55, 65, 81));
        referenceLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField referenceField = new JTextField("Enter reference number");
        referenceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        referenceField.setForeground(new Color(156, 163, 175));
        referenceField.setPreferredSize(new Dimension(500, 44));
        referenceField.setMaximumSize(new Dimension(500, 44));
        referenceField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        referenceField.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        referenceField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (referenceField.getText().equals("Enter reference number")) {
                    referenceField.setText("");
                    referenceField.setForeground(new Color(31, 41, 55));
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (referenceField.getText().isEmpty()) {
                    referenceField.setText("Enter reference number");
                    referenceField.setForeground(new Color(156, 163, 175));
                }
            }
        });
        
        referencePanel.add(referenceLabel);
        referencePanel.add(Box.createVerticalStrut(8));
        referencePanel.add(referenceField);
        
        formPanel.add(referencePanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Reason/Notes
        JPanel reasonPanel = new JPanel();
        reasonPanel.setLayout(new BoxLayout(reasonPanel, BoxLayout.Y_AXIS));
        reasonPanel.setBackground(Color.WHITE);
        reasonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel reasonLabel = new JLabel("Reason/Notes");
        reasonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reasonLabel.setForeground(new Color(55, 65, 81));
        reasonLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextArea reasonArea = new JTextArea("Enter reason for adjustment...");
        reasonArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reasonArea.setForeground(new Color(156, 163, 175));
        reasonArea.setLineWrap(true);
        reasonArea.setWrapStyleWord(true);
        reasonArea.setRows(4);
        reasonArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        
        reasonArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (reasonArea.getText().equals("Enter reason for adjustment...")) {
                    reasonArea.setText("");
                    reasonArea.setForeground(new Color(31, 41, 55));
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (reasonArea.getText().isEmpty()) {
                    reasonArea.setText("Enter reason for adjustment...");
                    reasonArea.setForeground(new Color(156, 163, 175));
                }
            }
        });
        
        JScrollPane reasonScrollPane = new JScrollPane(reasonArea);
        reasonScrollPane.setBorder(null);
        reasonScrollPane.setPreferredSize(new Dimension(500, 100));
        reasonScrollPane.setMaximumSize(new Dimension(500, 100));
        reasonScrollPane.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        reasonPanel.add(reasonLabel);
        reasonPanel.add(Box.createVerticalStrut(8));
        reasonPanel.add(reasonScrollPane);
        
        formPanel.add(reasonPanel);
        formPanel.add(Box.createVerticalStrut(20)); // Add some bottom padding
        
        // Create scroll pane for the entire form
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(null);
        formScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        formScrollPane.getViewport().setBackground(Color.WHITE);
        
        mainPanel.add(formScrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        cancelButton.setBackground(new Color(243, 244, 246));
        cancelButton.setForeground(new Color(55, 65, 81));
        cancelButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        cancelButton.setFocusPainted(false);
        cancelButton.setBorderPainted(false);
        cancelButton.setOpaque(true);
        cancelButton.setPreferredSize(new Dimension(100, 44));
        cancelButton.addActionListener(e -> adjustmentDialog.dispose());
        
        JButton updateButton = new JButton("Update Stock");
        updateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        updateButton.setBackground(new Color(59, 130, 246));
        updateButton.setForeground(Color.WHITE);
        updateButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        updateButton.setFocusPainted(false);
        updateButton.setBorderPainted(false);
        updateButton.setOpaque(true);
        updateButton.setPreferredSize(new Dimension(140, 44));
        
        updateButton.addActionListener(e -> {
            try {
                String selectedType = (String) typeCombo.getSelectedItem();
                String quantityText = quantityField.getText().trim();
                String reference = referenceField.getText().trim();
                String reason = reasonArea.getText().trim();
                
                // Validation
                if (selectedType.equals("Select Type")) {
                    JOptionPane.showMessageDialog(adjustmentDialog, "Please select an adjustment type", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (quantityText.isEmpty() || quantityText.equals("Enter quantity")) {
                    JOptionPane.showMessageDialog(adjustmentDialog, "Please enter a quantity", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                int quantity = Integer.parseInt(quantityText);
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(adjustmentDialog, "Please enter a valid quantity", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                if (reason.isEmpty() || reason.equals("Enter reason for adjustment...")) {
                    JOptionPane.showMessageDialog(adjustmentDialog, "Please provide a reason for the adjustment", "Validation Error", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                
                // Update stock based on type
                int newStock = item.getCurrentStock();
                if (selectedType.equals("Stock In") || selectedType.equals("Stock Adjustment")) {
                    newStock += quantity;
                } else if (selectedType.equals("Stock Out") || selectedType.equals("Stock Transfer") || selectedType.equals("Damaged/Lost")) {
                    newStock -= quantity;
                    if (newStock < 0) {
                        JOptionPane.showMessageDialog(adjustmentDialog, "Cannot reduce stock below zero", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                }
                
                // Update the item
                item.setCurrentStock(newStock);
                item.updateItem();
                
                // Show success message
                JOptionPane.showMessageDialog(adjustmentDialog, 
                    "Stock adjustment completed successfully!\nNew stock level: " + newStock + " kg", 
                    "Success", JOptionPane.INFORMATION_MESSAGE);
                
                adjustmentDialog.dispose();
                loadItems();
                updateStatistics();
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(adjustmentDialog, "Please enter a valid quantity", "Validation Error", JOptionPane.WARNING_MESSAGE);
            }
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(Box.createHorizontalStrut(15));
        buttonPanel.add(updateButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        adjustmentDialog.add(mainPanel);
        adjustmentDialog.setVisible(true);
    }
    
    private JPanel createFormField(String labelText, String value, boolean editable) {
        JPanel fieldPanel = new JPanel();
        fieldPanel.setLayout(new BoxLayout(fieldPanel, BoxLayout.Y_AXIS));
        fieldPanel.setBackground(Color.WHITE);
        fieldPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField field = new JTextField(value);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setForeground(new Color(31, 41, 55));
        field.setPreferredSize(new Dimension(500, 44));
        field.setMaximumSize(new Dimension(500, 44));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        field.setEditable(editable);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (!editable) {
            field.setBackground(new Color(249, 250, 251));
        }
        
        fieldPanel.add(label);
        fieldPanel.add(Box.createVerticalStrut(8));
        fieldPanel.add(field);
        
        return fieldPanel;
    }
    
    private void addInfoRow(JPanel panel, GridBagConstraints gbc, int row, String label, String value) {
        gbc.gridx = 0;
        gbc.gridy = row;
        gbc.weightx = 0;
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Segoe UI", Font.BOLD, 12));
        panel.add(labelComponent, gbc);
        
        gbc.gridx = 1;
        gbc.weightx = 1;
        JLabel valueComponent = new JLabel(value);
        valueComponent.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        panel.add(valueComponent, gbc);
    }
    
    private void generateStockReport() {
        JOptionPane.showMessageDialog(this, "Stock report generation feature will be implemented soon.", "Generate Report", JOptionPane.INFORMATION_MESSAGE);
    }
} 