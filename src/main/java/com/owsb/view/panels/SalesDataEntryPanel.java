package com.owsb.view.panels;

import com.owsb.model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SalesDataEntryPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JTextField dateField;
    private JComboBox<String> salesPersonCombo;
    private JComboBox<String> itemCombo;
    private JTextField quantityField;
    private JTextField unitPriceField;
    private JButton addEntryButton;
    private JButton saveEntriesButton;
    private JButton clearAllButton;
    private JLabel totalSalesLabel;
    
    // Data
    private List<SalesEntry> salesEntries;
    private List<Item> items;
    private List<User> salesPersons;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    
    // Colors
    private static final Color PRIMARY_BLUE = new Color(52, 144, 220);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color DANGER_RED = new Color(239, 68, 68);
    private static final Color LIGHT_BLUE = new Color(219, 234, 254);
    private static final Color GRAY_50 = new Color(249, 250, 251);
    private static final Color GRAY_100 = new Color(243, 244, 246);
    private static final Color GRAY_200 = new Color(229, 231, 235);
    private static final Color GRAY_600 = new Color(75, 85, 99);
    private static final Color GRAY_700 = new Color(55, 65, 81);
    private static final Color GRAY_900 = new Color(17, 24, 39);
    
    // Inner class for sales entry
    private static class SalesEntry {
        private String itemCode;
        private String itemName;
        private int quantitySold;
        private double unitPrice;
        private double totalAmount;
        
        public SalesEntry(String itemCode, String itemName, int quantitySold, double unitPrice) {
            this.itemCode = itemCode;
            this.itemName = itemName;
            this.quantitySold = quantitySold;
            this.unitPrice = unitPrice;
            this.totalAmount = quantitySold * unitPrice;
        }
        
        // Getters
        public String getItemCode() { return itemCode; }
        public String getItemName() { return itemName; }
        public int getQuantitySold() { return quantitySold; }
        public double getUnitPrice() { return unitPrice; }
        public double getTotalAmount() { return totalAmount; }
    }
    
    public SalesDataEntryPanel(User user) {
        this.currentUser = user;
        this.salesEntries = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        loadData();
        initializeTable(); // Initialize table first to create the table object
        setupUI(); // Then setup UI which uses the table
        addSampleData(); // Add sample data after everything is set up
    }
    
    private void loadData() {
        // Load items
        items = Item.getAllItems();
        if (items == null) {
            items = new ArrayList<>();
        }
        
        // Load sales persons (users with sales roles)
        salesPersons = User.getAllUsers();
        if (salesPersons == null) {
            salesPersons = new ArrayList<>();
        }
    }
    
    private void setupUI() {
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Header section
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main form and table section
        JPanel mainPanel = createMainPanel();
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Title and description
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Daily Sales Entry");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(GRAY_900);
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("Record daily item-wise sales data");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(GRAY_600);
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        descLabel.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 0));
        
        titlePanel.add(titleLabel);
        titlePanel.add(descLabel);
        
        headerPanel.add(titlePanel, BorderLayout.WEST);
        
        // Today's date info box
        JPanel dateInfoPanel = new JPanel(new BorderLayout());
        dateInfoPanel.setBackground(LIGHT_BLUE);
        dateInfoPanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(PRIMARY_BLUE, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        dateInfoPanel.setPreferredSize(new Dimension(300, 60));
        
        JLabel todayLabel = new JLabel("Today's Date: " + dateFormat.format(new Date()));
        todayLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        todayLabel.setForeground(PRIMARY_BLUE);
        
        dateInfoPanel.add(todayLabel, BorderLayout.CENTER);
        headerPanel.add(dateInfoPanel, BorderLayout.EAST);
        
        return headerPanel;
    }
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Form section
        JPanel formSection = createFormSection();
        mainPanel.add(formSection, BorderLayout.NORTH);
        
        // Table section
        JPanel tableSection = createTableSection();
        mainPanel.add(tableSection, BorderLayout.CENTER);
        
        // Bottom buttons section
        JPanel bottomSection = createBottomSection();
        mainPanel.add(bottomSection, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createFormSection() {
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(Color.WHITE);
        formPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Date and Sales Person row
        JPanel topRowPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        topRowPanel.setBackground(Color.WHITE);
        
        // Select Date
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
        datePanel.setBackground(Color.WHITE);
        
        JLabel dateLabel = new JLabel("Select Date");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(GRAY_700);
        
        dateField = new JTextField(dateFormat.format(new Date()));
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateField.setPreferredSize(new Dimension(200, 44));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        
        datePanel.add(dateLabel);
        datePanel.add(Box.createVerticalStrut(8));
        datePanel.add(dateField);
        
        // Sales Person
        JPanel salesPersonPanel = new JPanel();
        salesPersonPanel.setLayout(new BoxLayout(salesPersonPanel, BoxLayout.Y_AXIS));
        salesPersonPanel.setBackground(Color.WHITE);
        
        JLabel salesPersonLabel = new JLabel("Sales Person");
        salesPersonLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salesPersonLabel.setForeground(GRAY_700);
        
                 String[] salesPersonOptions = {"Select Sales Person"};
         if (salesPersons != null && !salesPersons.isEmpty()) {
             salesPersonOptions = new String[salesPersons.size() + 1];
             salesPersonOptions[0] = "Select Sales Person";
             for (int i = 0; i < salesPersons.size(); i++) {
                 salesPersonOptions[i + 1] = salesPersons.get(i).getName();
             }
         }
        
        salesPersonCombo = new JComboBox<>(salesPersonOptions);
        salesPersonCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        salesPersonCombo.setPreferredSize(new Dimension(250, 44));
        salesPersonCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)
        ));
        salesPersonCombo.setBackground(Color.WHITE);
        
        salesPersonPanel.add(salesPersonLabel);
        salesPersonPanel.add(Box.createVerticalStrut(8));
        salesPersonPanel.add(salesPersonCombo);
        
        topRowPanel.add(datePanel);
        topRowPanel.add(salesPersonPanel);
        
        formPanel.add(topRowPanel);
        formPanel.add(Box.createVerticalStrut(30));
        
        // Add Sales Entry section
        JPanel addEntryPanel = createAddEntryPanel();
        formPanel.add(addEntryPanel);
        
        return formPanel;
    }
    
    private JPanel createAddEntryPanel() {
        JPanel entryPanel = new JPanel(new BorderLayout());
        entryPanel.setBackground(Color.WHITE);
        entryPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Section title
        JLabel sectionTitle = new JLabel("Add Sales Entry");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(GRAY_900);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        entryPanel.add(sectionTitle, BorderLayout.NORTH);
        
        // Form fields
        JPanel fieldsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        fieldsPanel.setBackground(Color.WHITE);
        
        // Item dropdown
        JPanel itemPanel = new JPanel();
        itemPanel.setLayout(new BoxLayout(itemPanel, BoxLayout.Y_AXIS));
        itemPanel.setBackground(Color.WHITE);
        
        JLabel itemLabel = new JLabel("Item");
        itemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        itemLabel.setForeground(GRAY_700);
        
        String[] itemOptions = {"Select Item"};
        if (items != null && !items.isEmpty()) {
            itemOptions = new String[items.size() + 1];
            itemOptions[0] = "Select Item";
            for (int i = 0; i < items.size(); i++) {
                itemOptions[i + 1] = items.get(i).getItemName() + " (" + items.get(i).getItemCode() + ")";
            }
        }
        
        itemCombo = new JComboBox<>(itemOptions);
        itemCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        itemCombo.setPreferredSize(new Dimension(250, 44));
        itemCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)
        ));
        itemCombo.setBackground(Color.WHITE);
        itemCombo.addActionListener(e -> updateUnitPrice());
        
        itemPanel.add(itemLabel);
        itemPanel.add(Box.createVerticalStrut(8));
        itemPanel.add(itemCombo);
        
        // Quantity Sold
        JPanel quantityPanel = new JPanel();
        quantityPanel.setLayout(new BoxLayout(quantityPanel, BoxLayout.Y_AXIS));
        quantityPanel.setBackground(Color.WHITE);
        
        JLabel quantityLabel = new JLabel("Quantity Sold");
        quantityLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quantityLabel.setForeground(GRAY_700);
        
        quantityField = new JTextField("Enter quantity");
        quantityField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        quantityField.setForeground(new Color(156, 163, 175));
        quantityField.setPreferredSize(new Dimension(150, 44));
        quantityField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        
        quantityField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (quantityField.getText().equals("Enter quantity")) {
                    quantityField.setText("");
                    quantityField.setForeground(GRAY_900);
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
        
        // Unit Price
        JPanel pricePanel = new JPanel();
        pricePanel.setLayout(new BoxLayout(pricePanel, BoxLayout.Y_AXIS));
        pricePanel.setBackground(Color.WHITE);
        
        JLabel priceLabel = new JLabel("Unit Price (RM)");
        priceLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        priceLabel.setForeground(GRAY_700);
        
        unitPriceField = new JTextField("0.00");
        unitPriceField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        unitPriceField.setPreferredSize(new Dimension(120, 44));
        unitPriceField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        
        pricePanel.add(priceLabel);
        pricePanel.add(Box.createVerticalStrut(8));
        pricePanel.add(unitPriceField);
        
        // Add Entry Button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        
        // Add some space to align with other fields
        buttonPanel.add(Box.createVerticalStrut(22));
        
        addEntryButton = new JButton("Add Entry");
        addEntryButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addEntryButton.setBackground(SUCCESS_GREEN);
        addEntryButton.setForeground(Color.WHITE);
        addEntryButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        addEntryButton.setFocusPainted(false);
        addEntryButton.setBorderPainted(false);
        addEntryButton.setOpaque(true);
        addEntryButton.setPreferredSize(new Dimension(120, 44));
        addEntryButton.addActionListener(e -> addSalesEntry());
        
        buttonPanel.add(addEntryButton);
        
        fieldsPanel.add(itemPanel);
        fieldsPanel.add(quantityPanel);
        fieldsPanel.add(pricePanel);
        fieldsPanel.add(buttonPanel);
        
        entryPanel.add(fieldsPanel, BorderLayout.CENTER);
        
        return entryPanel;
    }
    
    private JPanel createTableSection() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        
        System.out.println("Creating table section. Table is null: " + (salesTable == null));
        
        if (salesTable == null) {
            // If table is null, create a placeholder
            JLabel placeholderLabel = new JLabel("Table not initialized", JLabel.CENTER);
            placeholderLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
            placeholderLabel.setForeground(GRAY_600);
            tablePanel.add(placeholderLabel, BorderLayout.CENTER);
            return tablePanel;
        }
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(GRAY_200, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(1200, 250));
        scrollPane.setMinimumSize(new Dimension(800, 200));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createBottomSection() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        // Total Sales
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        totalPanel.setBackground(Color.WHITE);
        
        JLabel totalLabel = new JLabel("Total Sales:");
        totalLabel.setFont(new Font("Segoe UI", Font.BOLD, 16));
        totalLabel.setForeground(GRAY_700);
        
        totalSalesLabel = new JLabel("RM 0.00");
        totalSalesLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        totalSalesLabel.setForeground(PRIMARY_BLUE);
        totalSalesLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        totalPanel.add(totalLabel);
        totalPanel.add(totalSalesLabel);
        
        bottomPanel.add(totalPanel, BorderLayout.NORTH);
        
        // Action buttons
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        buttonPanel.setBackground(Color.WHITE);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        saveEntriesButton = new JButton("Save Sales Entries");
        saveEntriesButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveEntriesButton.setBackground(PRIMARY_BLUE);
        saveEntriesButton.setForeground(Color.WHITE);
        saveEntriesButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        saveEntriesButton.setFocusPainted(false);
        saveEntriesButton.setBorderPainted(false);
        saveEntriesButton.setOpaque(true);
        saveEntriesButton.addActionListener(e -> saveSalesEntries());
        
        clearAllButton = new JButton("Clear All");
        clearAllButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        clearAllButton.setBackground(GRAY_100);
        clearAllButton.setForeground(GRAY_700);
        clearAllButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        clearAllButton.setFocusPainted(false);
        clearAllButton.setBorderPainted(false);
        clearAllButton.setOpaque(true);
        clearAllButton.addActionListener(e -> clearAllEntries());
        
        buttonPanel.add(saveEntriesButton);
        buttonPanel.add(clearAllButton);
        
        bottomPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        return bottomPanel;
    }
    
    private void initializeTable() {
        String[] columnNames = {
            "Item Code", "Item Name", "Quantity Sold", "Unit Price", "Total Amount", "Actions"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 5; // Only Actions column is editable
            }
        };
        
        salesTable = new JTable(tableModel);
        salesTable.setRowHeight(32);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        salesTable.setShowGrid(true);
        salesTable.setGridColor(GRAY_200);
        salesTable.setIntercellSpacing(new Dimension(1, 1));
        salesTable.setSelectionBackground(new Color(239, 246, 255));
        salesTable.setSelectionForeground(GRAY_900);
        salesTable.setBackground(Color.WHITE);
        
        // Set column widths
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Item Code
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(200); // Item Name
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Quantity Sold
        salesTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Unit Price
        salesTable.getColumnModel().getColumn(4).setPreferredWidth(120); // Total Amount
        salesTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Actions
        
        // Header styling
        JTableHeader header = salesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(GRAY_50);
        header.setForeground(GRAY_700);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GRAY_200));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        header.setReorderingAllowed(false);
        
        // Custom renderer and editor for Actions column
        salesTable.getColumnModel().getColumn(5).setCellRenderer(new ActionButtonRenderer());
        salesTable.getColumnModel().getColumn(5).setCellEditor(new ActionButtonEditor());
        
        // Default cell renderer for other columns
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                setFont(new Font("Segoe UI", Font.PLAIN, 11));
                
                if (isSelected) {
                    setBackground(new Color(239, 246, 255));
                    setForeground(GRAY_900);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(GRAY_700);
                }
                
                return c;
            }
        };
        
        // Apply to regular columns (not actions)
        for (int i = 0; i < 5; i++) {
            salesTable.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }
    }
    
    private void addSampleData() {
        // Add some sample sales entries for testing
        salesEntries.add(new SalesEntry("ITM-001", "Rice (Premium)", 50, 3.50));
        salesEntries.add(new SalesEntry("ITM-002", "Sugar (White)", 30, 2.80));
        
        refreshTable();
        updateTotalSales();
    }
    
    private void updateUnitPrice() {
        int selectedIndex = itemCombo.getSelectedIndex();
        if (selectedIndex > 0 && items != null && selectedIndex <= items.size()) {
            Item selectedItem = items.get(selectedIndex - 1);
            unitPriceField.setText(String.valueOf(selectedItem.getUnitPrice()));
        } else {
            unitPriceField.setText("0.00");
        }
    }
    
    private void addSalesEntry() {
        try {
            // Validate inputs
            int selectedItemIndex = itemCombo.getSelectedIndex();
            if (selectedItemIndex == 0) {
                JOptionPane.showMessageDialog(this, "Please select an item", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            String quantityText = quantityField.getText().trim();
            if (quantityText.isEmpty() || quantityText.equals("Enter quantity")) {
                JOptionPane.showMessageDialog(this, "Please enter quantity", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            int quantity = Integer.parseInt(quantityText);
            if (quantity <= 0) {
                JOptionPane.showMessageDialog(this, "Quantity must be greater than 0", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            double unitPrice = Double.parseDouble(unitPriceField.getText());
            if (unitPrice <= 0) {
                JOptionPane.showMessageDialog(this, "Unit price must be greater than 0", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Get selected item
            Item selectedItem = items.get(selectedItemIndex - 1);
            
            // Create new sales entry
            SalesEntry newEntry = new SalesEntry(
                selectedItem.getItemCode(),
                selectedItem.getItemName(),
                quantity,
                unitPrice
            );
            
            salesEntries.add(newEntry);
            refreshTable();
            updateTotalSales();
            
            // Clear form
            itemCombo.setSelectedIndex(0);
            quantityField.setText("Enter quantity");
            quantityField.setForeground(new Color(156, 163, 175));
            unitPriceField.setText("0.00");
            
            JOptionPane.showMessageDialog(this, "Sales entry added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers", "Validation Error", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void refreshTable() {
        if (tableModel == null) {
            System.out.println("Table model is null!");
            return;
        }
        
        tableModel.setRowCount(0);
        
        System.out.println("Refreshing table with " + salesEntries.size() + " entries");
        
        for (SalesEntry entry : salesEntries) {
            Object[] rowData = {
                entry.getItemCode(),
                entry.getItemName(),
                entry.getQuantitySold() + " kg",
                "RM " + decimalFormat.format(entry.getUnitPrice()),
                "RM " + decimalFormat.format(entry.getTotalAmount()),
                "" // Actions column (handled by custom renderer)
            };
            
            tableModel.addRow(rowData);
        }
        
        // Force table to repaint
        if (salesTable != null) {
            salesTable.revalidate();
            salesTable.repaint();
        }
    }
    
    private void updateTotalSales() {
        double total = 0.0;
        for (SalesEntry entry : salesEntries) {
            total += entry.getTotalAmount();
        }
        if (totalSalesLabel != null) {
            totalSalesLabel.setText("RM " + decimalFormat.format(total));
        }
    }
    
    private void removeSalesEntry(int index) {
        if (index >= 0 && index < salesEntries.size()) {
            salesEntries.remove(index);
            refreshTable();
            updateTotalSales();
        }
    }
    
    private void saveSalesEntries() {
        if (salesEntries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No sales entries to save", "No Data", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Here you would implement the actual saving logic
        // For now, just show a success message
        JOptionPane.showMessageDialog(this, 
            "Sales entries saved successfully!\n" + 
            "Total entries: " + salesEntries.size() + "\n" +
            "Total amount: " + totalSalesLabel.getText(), 
            "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearAllEntries() {
        if (salesEntries.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No entries to clear", "No Data", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        
        int confirm = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to clear all entries?",
            "Confirm Clear",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirm == JOptionPane.YES_OPTION) {
            salesEntries.clear();
            refreshTable();
            updateTotalSales();
            JOptionPane.showMessageDialog(this, "All entries cleared", "Success", JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    // Custom renderer for Actions column
    class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton removeButton;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 3, 3));
            setOpaque(true);
            
            removeButton = new JButton("Remove");
            removeButton.setBackground(DANGER_RED);
            removeButton.setForeground(Color.WHITE);
            removeButton.setFocusPainted(false);
            removeButton.setBorderPainted(false);
            removeButton.setOpaque(true);
            removeButton.setFont(new Font("Segoe UI", Font.BOLD, 10));
            removeButton.setPreferredSize(new Dimension(70, 22));
            
            add(removeButton);
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
        private JButton removeButton;
        private int currentRow;
        
        public ActionButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 3, 3));
            panel.setOpaque(true);
            
            removeButton = new JButton("Remove");
            removeButton.setBackground(DANGER_RED);
            removeButton.setForeground(Color.WHITE);
            removeButton.setFocusPainted(false);
            removeButton.setBorderPainted(false);
            removeButton.setOpaque(true);
            removeButton.setFont(new Font("Segoe UI", Font.BOLD, 10));
            removeButton.setPreferredSize(new Dimension(70, 22));
            
            removeButton.addActionListener(e -> {
                removeSalesEntry(currentRow);
                fireEditingStopped();
            });
            
            panel.add(removeButton);
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
    }
} 