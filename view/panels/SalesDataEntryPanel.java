package view.panels;

import model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class SalesDataEntryPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JLabel dateLabel;
    private JButton selectDateButton;
    private JButton addSalesButton;
    private JButton editSalesButton;
    private JButton deleteSalesButton;
    private JButton saveButton;
    private JButton importCSVButton;
    
    // Date selection
    private Date selectedDate;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private SimpleDateFormat displayDateFormat = new SimpleDateFormat("dd/MM/yyyy");
    
    // List of sales for the selected date
    private List<DailySales> dailySales;
    
    public SalesDataEntryPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Set default selected date to today
        selectedDate = new Date();
        
        // Title
        JLabel titleLabel = new JLabel("Daily Sales Data Entry");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Date selection panel
        JPanel datePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        datePanel.add(new JLabel("Select Date: "));
        
        dateLabel = new JLabel(displayDateFormat.format(selectedDate));
        dateLabel.setFont(new Font("Arial", Font.BOLD, 14));
        dateLabel.setPreferredSize(new Dimension(150, 30));
        dateLabel.setMinimumSize(new Dimension(150, 30));
        dateLabel.setHorizontalAlignment(JLabel.CENTER);
        dateLabel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.LIGHT_GRAY),
            BorderFactory.createEmptyBorder(5, 10, 5, 10)
        ));
        datePanel.add(dateLabel);
        
        selectDateButton = new JButton("...");
        selectDateButton.addActionListener(e -> selectDate());
        selectDateButton.setPreferredSize(new Dimension(40, 30));
        selectDateButton.setMinimumSize(new Dimension(40, 30));
        selectDateButton.setBackground(new Color(240, 240, 240));
        selectDateButton.setOpaque(true);
        selectDateButton.setBorderPainted(true);
        selectDateButton.setFont(new Font("Arial", Font.BOLD, 14));
        datePanel.add(selectDateButton);
        
        // Load data button and action buttons panel
        JPanel actionPanel = new JPanel(new BorderLayout());
        actionPanel.add(datePanel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        addSalesButton = new JButton("Add Sales");
        addSalesButton.setBackground(new Color(0, 102, 204));
        addSalesButton.setForeground(Color.WHITE);
        addSalesButton.setOpaque(true);
        addSalesButton.setBorderPainted(false);
        addSalesButton.setPreferredSize(new Dimension(120, 35));
        addSalesButton.setMinimumSize(new Dimension(120, 35));
        addSalesButton.setFont(new Font("Arial", Font.BOLD, 12));
        addSalesButton.addActionListener(e -> showSalesDialog(null));
        buttonPanel.add(addSalesButton);
        
        editSalesButton = new JButton("Edit");
        editSalesButton.setBackground(new Color(46, 184, 46));
        editSalesButton.setForeground(Color.WHITE);
        editSalesButton.setOpaque(true);
        editSalesButton.setBorderPainted(false);
        editSalesButton.setPreferredSize(new Dimension(100, 35));
        editSalesButton.setMinimumSize(new Dimension(100, 35));
        editSalesButton.setFont(new Font("Arial", Font.BOLD, 12));
        editSalesButton.addActionListener(e -> editSelectedSales());
        buttonPanel.add(editSalesButton);
        
        deleteSalesButton = new JButton("Delete");
        deleteSalesButton.setBackground(new Color(255, 51, 51));
        deleteSalesButton.setForeground(Color.WHITE);
        deleteSalesButton.setOpaque(true);
        deleteSalesButton.setBorderPainted(false);
        deleteSalesButton.setPreferredSize(new Dimension(100, 35));
        deleteSalesButton.setMinimumSize(new Dimension(100, 35));
        deleteSalesButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteSalesButton.addActionListener(e -> deleteSelectedSales());
        buttonPanel.add(deleteSalesButton);
        
        importCSVButton = new JButton("Import CSV");
        importCSVButton.setBackground(new Color(150, 150, 150));
        importCSVButton.setForeground(Color.WHITE);
        importCSVButton.setOpaque(true);
        importCSVButton.setBorderPainted(false);
        importCSVButton.setPreferredSize(new Dimension(120, 35));
        importCSVButton.setMinimumSize(new Dimension(120, 35));
        importCSVButton.setFont(new Font("Arial", Font.BOLD, 12));
        importCSVButton.addActionListener(e -> importCSV());
        buttonPanel.add(importCSVButton);
        
        saveButton = new JButton("Save All");
        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setMinimumSize(new Dimension(100, 35));
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.addActionListener(e -> saveAllSales());
        buttonPanel.add(saveButton);
        
        actionPanel.add(buttonPanel, BorderLayout.EAST);
        
        contentPanel.add(actionPanel, BorderLayout.NORTH);
        
        // Create sales table
        createSalesTable();
        JScrollPane tableScrollPane = new JScrollPane(salesTable);
        contentPanel.add(tableScrollPane, BorderLayout.CENTER);
        
        // Add info panel at the bottom
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setBorder(new EmptyBorder(10, 0, 0, 0));
        
        // Last updated info
        JLabel lastUpdatedLabel = new JLabel("Last Updated: 10/05/2025 14:30 (Jim Chen - Sales Manager)");
        lastUpdatedLabel.setForeground(Color.GRAY);
        lastUpdatedLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        infoPanel.add(lastUpdatedLabel, BorderLayout.WEST);
        
        contentPanel.add(infoPanel, BorderLayout.SOUTH);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Load data for selected date
        loadSalesData();
    }
    
    private void createSalesTable() {
        // Define table columns
        String[] columns = {"Item Code", "Item Name", "Current Stock", "Sales Quantity", "Uplifted Price", "Status"};
        
        // Create table model
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        // Create table
        salesTable = new JTable(tableModel);
        salesTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        salesTable.setRowHeight(25);
        salesTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        salesTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        salesTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        salesTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Add double-click listener
        salesTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedSales();
                }
            }
        });
    }
    
    private void loadSalesData() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Get all items
        List<Item> allItems = Item.getAllItems();
        
        // Get sales for selected date
        dailySales = DailySales.getSalesByDate(selectedDate);
        
        // Create a map for quick lookup of sales by item code
        Map<String, DailySales> salesMap = new HashMap<>();
        for (DailySales sales : dailySales) {
            salesMap.put(sales.getItemCode(), sales);
        }
        
        // Add items to table
        for (Item item : allItems) {
            DailySales sales = salesMap.get(item.getItemCode());
            
            int salesQuantity = (sales != null) ? sales.getSalesQuantity() : 0;
            double salesAmount = (sales != null) ? sales.getSalesAmount() : 0.0;
            
            String status = (salesQuantity > 0) ? "Recorded" : "Not Recorded";
            
            Object[] row = {
                item.getItemCode(),
                item.getItemName(),
                item.getStockQuantity() + " units",
                salesQuantity,
                "RM " + String.format("%.2f", salesAmount),
                status
            };
            
            tableModel.addRow(row);
        }
    }
    
    private void selectDate() {
        // Create a JDateChooser (using JCalendar library) or implement a custom date picker
        // For simplicity, we'll use a JOptionPane with a JSpinner
        JPanel panel = new JPanel();
        panel.add(new JLabel("Select date: "));
        
        // Create a date spinner
        SpinnerDateModel dateModel = new SpinnerDateModel(selectedDate, null, null, Calendar.DAY_OF_MONTH);
        JSpinner dateSpinner = new JSpinner(dateModel);
        dateSpinner.setEditor(new JSpinner.DateEditor(dateSpinner, "dd/MM/yyyy"));
        panel.add(dateSpinner);
        
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "Select Date",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );
        
        if (result == JOptionPane.OK_OPTION) {
            selectedDate = (Date) dateSpinner.getValue();
            dateLabel.setText(displayDateFormat.format(selectedDate));
            loadSalesData();
        }
    }
    
    private void editSelectedSales() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String itemCode = (String) tableModel.getValueAt(selectedRow, 0);
        Item item = Item.getItemByCode(itemCode);
        
        if (item != null) {
            // Find existing sales record
            DailySales existingSales = null;
            for (DailySales sales : dailySales) {
                if (sales.getItemCode().equals(itemCode)) {
                    existingSales = sales;
                    break;
                }
            }
            
            showSalesDialog(existingSales);
        }
    }
    
    private void deleteSelectedSales() {
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a row to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String itemCode = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Find existing sales record
        DailySales salesToDelete = null;
        for (DailySales sales : dailySales) {
            if (sales.getItemCode().equals(itemCode)) {
                salesToDelete = sales;
                break;
            }
        }
        
        if (salesToDelete != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to delete this sales record?",
                "Confirm Deletion",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                if (DailySales.deleteSales(salesToDelete.getSalesId())) {
                    JOptionPane.showMessageDialog(this, "Sales record deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadSalesData();
                } else {
                    JOptionPane.showMessageDialog(this, "Failed to delete sales record", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "No sales record found for this item on the selected date", "No Record", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void showSalesDialog(DailySales existingSales) {
        boolean isEditMode = (existingSales != null);
        
        int selectedRow = salesTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String itemCode = (String) tableModel.getValueAt(selectedRow, 0);
        String itemName = (String) tableModel.getValueAt(selectedRow, 1);
        
        Item item = Item.getItemByCode(itemCode);
        if (item == null) {
            JOptionPane.showMessageDialog(this, "Item not found", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create dialog
        JDialog salesDialog = new JDialog();
        salesDialog.setTitle((isEditMode ? "Edit" : "Add") + " Sales for " + itemName);
        salesDialog.setSize(400, 300);
        salesDialog.setLocationRelativeTo(this);
        salesDialog.setModal(true);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Item Code
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Item Code:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField itemCodeField = new JTextField(itemCode);
        itemCodeField.setEditable(false);
        formPanel.add(itemCodeField, gbc);
        
        // Item Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Item Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField itemNameField = new JTextField(itemName);
        itemNameField.setEditable(false);
        formPanel.add(itemNameField, gbc);
        
        // Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField dateField = new JTextField(displayDateFormat.format(selectedDate));
        dateField.setEditable(false);
        formPanel.add(dateField, gbc);
        
        // Current Stock
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Current Stock:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField stockField = new JTextField(item.getStockQuantity() + " units");
        stockField.setEditable(false);
        formPanel.add(stockField, gbc);
        
        // Sales Quantity
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Sales Quantity:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField quantityField = new JTextField();
        if (isEditMode) {
            quantityField.setText(String.valueOf(existingSales.getSalesQuantity()));
        }
        formPanel.add(quantityField, gbc);
        
        // Unit Price
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Unit Price (RM):"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        JTextField unitPriceField = new JTextField(String.valueOf(item.getUnitPrice()));
        unitPriceField.setEditable(false);
        formPanel.add(unitPriceField, gbc);
        
        // Total Amount
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Total Amount (RM):"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        JTextField totalAmountField = new JTextField();
        if (isEditMode) {
            totalAmountField.setText(String.valueOf(existingSales.getSalesAmount()));
        }
        totalAmountField.setEditable(false);
        formPanel.add(totalAmountField, gbc);
        
        // Calculate total when quantity changes
        quantityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                try {
                    int quantity = Integer.parseInt(quantityField.getText());
                    double unitPrice = Double.parseDouble(unitPriceField.getText());
                    double total = quantity * unitPrice;
                    totalAmountField.setText(String.format("%.2f", total));
                } catch (NumberFormatException ex) {
                    totalAmountField.setText("0.00");
                }
            }
        });
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelButton.addActionListener(e -> salesDialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.addActionListener(e -> {
            // Validate input
            try {
                int quantity = Integer.parseInt(quantityField.getText());
                if (quantity < 0) {
                    JOptionPane.showMessageDialog(salesDialog, "Quantity cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                if (quantity > item.getStockQuantity() && !isEditMode) {
                    int confirm = JOptionPane.showConfirmDialog(
                        salesDialog,
                        "Sales quantity exceeds current stock. Continue?",
                        "Confirm Sales",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE
                    );
                    
                    if (confirm != JOptionPane.YES_OPTION) {
                        return;
                    }
                }
                
                double amount = Double.parseDouble(totalAmountField.getText());
                
                if (isEditMode) {
                    // Update existing record
                    int oldQuantity = existingSales.getSalesQuantity();
                    existingSales.setSalesQuantity(quantity);
                    existingSales.setSalesAmount(amount);
                    
                    if (existingSales.updateSales(oldQuantity)) {
                        JOptionPane.showMessageDialog(salesDialog, "Sales record updated successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        salesDialog.dispose();
                        loadSalesData();
                    } else {
                        JOptionPane.showMessageDialog(salesDialog, "Failed to update sales record", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    // Create new record
                    DailySales newSales = new DailySales(
                        DailySales.generateSalesId(),
                        itemCode,
                        selectedDate,
                        quantity,
                        amount,
                        currentUser.getUserId()
                    );
                    
                    if (newSales.saveSales()) {
                        JOptionPane.showMessageDialog(salesDialog, "Sales record added successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                        salesDialog.dispose();
                        loadSalesData();
                    } else {
                        JOptionPane.showMessageDialog(salesDialog, "Failed to add sales record", "Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(salesDialog, "Please enter valid numbers", "Validation Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        
        buttonPanel.add(saveButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        salesDialog.add(mainPanel);
        salesDialog.setVisible(true);
    }
    
    private void saveAllSales() {
        JOptionPane.showMessageDialog(this, "All changes have been saved", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void importCSV() {
        JOptionPane.showMessageDialog(this, "CSV import feature is not implemented in this version", "Not Implemented", JOptionPane.INFORMATION_MESSAGE);
    }
} 