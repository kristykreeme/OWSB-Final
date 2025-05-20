package view.panels;

import model.*;
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
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton adjustStockButton;
    private JButton exportButton;
    private JButton importButton;
    
    // Item form components
    private JDialog itemDialog;
    private JTextField itemCodeField;
    private JTextField itemNameField;
    private JTextArea descriptionField;
    private JComboBox<String> categoryComboBox;
    private JTextField unitPriceField;
    private JTextField currentStockField;
    private JTextField reorderLevelField;
    private JComboBox<Supplier> supplierComboBox;
    
    // List of all items
    private List<Item> items;
    private List<Supplier> suppliers;
    
    // For number formatting
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    
    public InventoryManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Item Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Search and buttons panel
        JPanel actionPanel = new JPanel(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Item: "));
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterItems();
            }
        });
        searchPanel.add(searchField);
        
        // Category filter
        searchPanel.add(new JLabel("Category: "));
        categoryFilter = new JComboBox<>();
        categoryFilter.addItem("All");
        
        // Get unique categories
        Set<String> categories = new HashSet<>();
        categories.add("Grains");
        categories.add("Dairy");
        categories.add("Cooking");
        categories.add("Spices");
        categories.add("Miscellaneous");
        
        // Add categories to combobox
        for (String category : categories) {
            categoryFilter.addItem(category);
        }
        
        categoryFilter.addActionListener(e -> filterItems());
        searchPanel.add(categoryFilter);
        
        actionPanel.add(searchPanel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        addButton = new JButton("Add Item");
        addButton.setBackground(new Color(0, 102, 204));
        addButton.setForeground(Color.WHITE);
        addButton.setFocusPainted(false);
        addButton.addActionListener(e -> showItemDialog(null));
        buttonPanel.add(addButton);
        
        exportButton = new JButton("Export");
        exportButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Export feature is not implemented in this version"));
        buttonPanel.add(exportButton);
        
        importButton = new JButton("Import");
        importButton.addActionListener(e -> JOptionPane.showMessageDialog(this, "Import feature is not implemented in this version"));
        buttonPanel.add(importButton);
        
        actionPanel.add(buttonPanel, BorderLayout.EAST);
        
        contentPanel.add(actionPanel, BorderLayout.NORTH);
        
        // Table
        createItemTable();
        JScrollPane scrollPane = new JScrollPane(itemTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Load data
        loadItems();
        loadSuppliers();
    }
    
    private void createItemTable() {
        // Define table columns
        String[] columns = {"Item Code", "Item Name", "Supplier", "Stock Level", "Status", "Actions"};
        
        // Create table model
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        // Create table
        itemTable = new JTable(tableModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.setRowHeight(35);
        itemTable.getTableHeader().setReorderingAllowed(false);
        itemTable.getTableHeader().setFont(new Font("Arial", Font.BOLD, 12));
        
        // Set custom renderer for actions column
        itemTable.getColumnModel().getColumn(5).setCellRenderer(new ButtonRenderer());
        
        // Add action listeners for buttons in the table
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int column = itemTable.getColumnModel().getColumnIndexAtX(e.getX());
                int row = e.getY() / itemTable.getRowHeight();
                
                if (row < itemTable.getRowCount() && row >= 0 && column < itemTable.getColumnCount() && column >= 0) {
                    if (column == 5) { // Actions column
                        String itemCode = (String) tableModel.getValueAt(row, 0);
                        final Item selectedItem;
                        Item foundItem = null;
                        
                        for (Item item : items) {
                            if (item.getItemCode().equals(itemCode)) {
                                foundItem = item;
                                break;
                            }
                        }
                        selectedItem = foundItem;
                        
                        if (selectedItem != null) {
                            // Show options menu
                            JPopupMenu contextMenu = new JPopupMenu();
                            JMenuItem editItem = new JMenuItem("Edit");
                            JMenuItem deleteItem = new JMenuItem("Delete");
                            JMenuItem adjustItem = new JMenuItem("Adjust Stock");
                            
                            editItem.addActionListener(event -> showItemDialog(selectedItem));
                            deleteItem.addActionListener(event -> {
                                int confirmation = JOptionPane.showConfirmDialog(
                                    null, 
                                    "Are you sure you want to delete this item?", 
                                    "Confirm Delete", 
                                    JOptionPane.YES_NO_OPTION
                                );
                                
                                if (confirmation == JOptionPane.YES_OPTION) {
                                    if (Item.deleteItem(selectedItem.getItemCode())) {
                                        JOptionPane.showMessageDialog(null, "Item deleted successfully");
                                        loadItems();
                                    } else {
                                        JOptionPane.showMessageDialog(null, "Failed to delete item");
                                    }
                                }
                            });
                            
                            adjustItem.addActionListener(event -> showAdjustStockDialog(selectedItem));
                            
                            contextMenu.add(editItem);
                            contextMenu.add(deleteItem);
                            contextMenu.add(adjustItem);
                            
                            contextMenu.show(itemTable, e.getX(), e.getY());
                        }
                    }
                }
            }
        });
    }
    
    private void loadItems() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load items from model
        items = Item.getAllItems();
        
        // Add items to table
        for (Item item : items) {
            loadItemIntoTable(item);
        }
    }
    
    private void loadItemIntoTable(Item item) {
        Supplier supplier = Supplier.getSupplierById(item.getSupplierId());
        String supplierName = supplier != null ? supplier.getCompanyName() : "Unknown";
        
        // Highlight items below reorder level with red text
        boolean belowReorderLevel = item.getCurrentStock() <= item.getReorderLevel();
        
        String stockLevel = item.getCurrentStock() + " units";
        String status;
        
        if (belowReorderLevel) {
            stockLevel = "<html><font color='red'>" + stockLevel + "</font></html>";
            status = "<html><font color='red'>Low Stock</font></html>";
        } else if (item.getCurrentStock() == 0) {
            status = "<html><font color='red'>Out of Stock</font></html>";
        } else {
            status = "<html><font color='green'>In Stock</font></html>";
        }
        
        Object[] row = {
            item.getItemCode(),
            item.getItemName(),
            supplierName,
            stockLevel,
            status,
            "Actions"
        };
        
        tableModel.addRow(row);
    }
    
    private void loadSuppliers() {
        suppliers = Supplier.getAllSuppliers();
    }
    
    private void filterItems() {
        // Clear table
        tableModel.setRowCount(0);
        
        String searchTerm = searchField.getText().toLowerCase();
        String category = (String) categoryFilter.getSelectedItem();
        
        for (Item item : items) {
            boolean categoryMatch = category.equals("All") || (item.getCategory() != null && item.getCategory().equals(category));
            
            if (categoryMatch && (
                item.getItemCode().toLowerCase().contains(searchTerm) || 
                item.getItemName().toLowerCase().contains(searchTerm))) {
                
                loadItemIntoTable(item);
            }
        }
    }
    
    private void showItemDialog(Item item) {
        boolean isEditMode = (item != null);
        
        // Create dialog
        itemDialog = new JDialog();
        itemDialog.setTitle(isEditMode ? "Edit Item" : "Add New Item");
        itemDialog.setSize(450, 500);
        itemDialog.setLocationRelativeTo(this);
        itemDialog.setModal(true);
        
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
        itemCodeField = new JTextField(15);
        if (isEditMode) {
            itemCodeField.setText(item.getItemCode());
            itemCodeField.setEditable(false); // Can't change item code in edit mode
        } else {
            itemCodeField.setText(Item.generateItemCode());
            itemCodeField.setEditable(false); // Auto-generated code
        }
        formPanel.add(itemCodeField, gbc);
        
        // Item Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Item Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        itemNameField = new JTextField(15);
        if (isEditMode) {
            itemNameField.setText(item.getItemName());
        }
        formPanel.add(itemNameField, gbc);
        
        // Description
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Description:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        descriptionField = new JTextArea(3, 15);
        descriptionField.setLineWrap(true);
        descriptionField.setWrapStyleWord(true);
        if (isEditMode && item.getDescription() != null) {
            descriptionField.setText(item.getDescription());
        }
        JScrollPane descScrollPane = new JScrollPane(descriptionField);
        formPanel.add(descScrollPane, gbc);
        
        // Category
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        categoryComboBox = new JComboBox<>(new String[]{"Grains", "Dairy", "Cooking", "Spices", "Miscellaneous"});
        if (isEditMode && item.getCategory() != null) {
            for (int i = 0; i < categoryComboBox.getItemCount(); i++) {
                if (categoryComboBox.getItemAt(i).equals(item.getCategory())) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        formPanel.add(categoryComboBox, gbc);
        
        // Unit Price
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Unit Price (RM):"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        unitPriceField = new JTextField(15);
        if (isEditMode) {
            unitPriceField.setText(String.format("%.2f", item.getUnitPrice()));
        }
        formPanel.add(unitPriceField, gbc);
        
        // Current Stock
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Current Stock:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        currentStockField = new JTextField(15);
        if (isEditMode) {
            currentStockField.setText(String.valueOf(item.getCurrentStock()));
        } else {
            currentStockField.setText("0"); // Default for new items
        }
        formPanel.add(currentStockField, gbc);
        
        // Reorder Level
        gbc.gridx = 0;
        gbc.gridy = 6;
        formPanel.add(new JLabel("Reorder Level:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 6;
        reorderLevelField = new JTextField(15);
        if (isEditMode) {
            reorderLevelField.setText(String.valueOf(item.getReorderLevel()));
        } else {
            reorderLevelField.setText("10"); // Default reorder level
        }
        formPanel.add(reorderLevelField, gbc);
        
        // Supplier
        gbc.gridx = 0;
        gbc.gridy = 7;
        formPanel.add(new JLabel("Supplier:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 7;
        supplierComboBox = new JComboBox<>();
        
        // Add suppliers to combobox
        for (Supplier supplier : suppliers) {
            supplierComboBox.addItem(supplier);
        }
        
        // Set selected supplier if in edit mode
        if (isEditMode) {
            for (int i = 0; i < supplierComboBox.getItemCount(); i++) {
                Supplier supplier = (Supplier) supplierComboBox.getItemAt(i);
                if (supplier.getSupplierId().equals(item.getSupplierId())) {
                    supplierComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        
        formPanel.add(supplierComboBox, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> itemDialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveItem(isEditMode));
        buttonPanel.add(saveButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        itemDialog.add(mainPanel);
        itemDialog.setVisible(true);
    }
    
    private void saveItem(boolean isEditMode) {
        // Validate input
        if (itemNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(itemDialog, "Please enter an item name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
            if (unitPrice < 0) {
                JOptionPane.showMessageDialog(itemDialog, "Unit price must be a positive number", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(itemDialog, "Please enter a valid unit price", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int currentStock = Integer.parseInt(currentStockField.getText().trim());
            if (currentStock < 0) {
                JOptionPane.showMessageDialog(itemDialog, "Current stock cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(itemDialog, "Please enter a valid stock quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        try {
            int reorderLevel = Integer.parseInt(reorderLevelField.getText().trim());
            if (reorderLevel < 0) {
                JOptionPane.showMessageDialog(itemDialog, "Reorder level cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(itemDialog, "Please enter a valid reorder level", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get values from form
        String itemCode = itemCodeField.getText().trim();
        String itemName = itemNameField.getText().trim();
        String description = descriptionField.getText().trim();
        String category = (String) categoryComboBox.getSelectedItem();
        double unitPrice = Double.parseDouble(unitPriceField.getText().trim());
        int currentStock = Integer.parseInt(currentStockField.getText().trim());
        int reorderLevel = Integer.parseInt(reorderLevelField.getText().trim());
        Supplier supplier = (Supplier) supplierComboBox.getSelectedItem();
        
        // Create or update item
        Item itemToSave = new Item(
            itemCode,
            itemName,
            description,
            category,
            unitPrice,
            currentStock,
            reorderLevel,
            supplier.getSupplierId()
        );
        
        boolean success;
        if (isEditMode) {
            success = itemToSave.updateItem();
        } else {
            success = itemToSave.saveItem();
        }
        
        if (success) {
            // Update supplier with the item code
            if (!isEditMode) {
                supplier.addItemCode(itemCode);
                supplier.updateSupplier();
            }
            
            JOptionPane.showMessageDialog(itemDialog, "Item saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            itemDialog.dispose();
            loadItems();
        } else {
            JOptionPane.showMessageDialog(itemDialog, "Failed to save item", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private void showAdjustStockDialog(Item item) {
        // Create dialog for stock adjustment
        JDialog adjustmentDialog = new JDialog();
        adjustmentDialog.setTitle("Stock Adjustment - " + item.getItemName());
        adjustmentDialog.setSize(400, 300);
        adjustmentDialog.setLocationRelativeTo(this);
        adjustmentDialog.setModal(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Item info panel
        JPanel infoPanel = new JPanel(new GridLayout(3, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("Item Information"));
        
        infoPanel.add(new JLabel("Item Code:"));
        infoPanel.add(new JLabel(item.getItemCode()));
        
        infoPanel.add(new JLabel("Item Name:"));
        infoPanel.add(new JLabel(item.getItemName()));
        
        infoPanel.add(new JLabel("Current Stock:"));
        infoPanel.add(new JLabel(String.valueOf(item.getCurrentStock())));
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // Adjustment panel
        JPanel adjustmentPanel = new JPanel(new GridBagLayout());
        adjustmentPanel.setBorder(BorderFactory.createTitledBorder("Stock Adjustment"));
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        gbc.gridx = 0;
        gbc.gridy = 0;
        adjustmentPanel.add(new JLabel("Adjustment Type:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        String[] adjustmentTypes = {"ADD", "SUBTRACT"};
        JComboBox<String> adjustmentTypeCombo = new JComboBox<>(adjustmentTypes);
        adjustmentPanel.add(adjustmentTypeCombo, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 1;
        adjustmentPanel.add(new JLabel("Quantity:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField quantityField = new JTextField(10);
        adjustmentPanel.add(quantityField, gbc);
        
        gbc.gridx = 0;
        gbc.gridy = 2;
        adjustmentPanel.add(new JLabel("Reason:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField reasonField = new JTextField(15);
        adjustmentPanel.add(reasonField, gbc);
        
        mainPanel.add(adjustmentPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> adjustmentDialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = new JButton("Save Adjustment");
        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> {
            try {
                int quantity = Integer.parseInt(quantityField.getText().trim());
                
                if (quantity <= 0) {
                    JOptionPane.showMessageDialog(adjustmentDialog, 
                        "Quantity must be a positive number", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                String adjustmentType = (String) adjustmentTypeCombo.getSelectedItem();
                String reason = reasonField.getText().trim();
                
                if (reason.isEmpty()) {
                    JOptionPane.showMessageDialog(adjustmentDialog, 
                        "Please provide a reason for the adjustment", 
                        "Validation Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Create stock adjustment
                StockAdjustment adjustment = new StockAdjustment(
                    StockAdjustment.generateAdjustmentId(),
                    item.getItemCode(),
                    new Date(),
                    adjustmentType,
                    quantity,
                    reason,
                    currentUser.getUserId()
                );
                
                // Save adjustment and update item stock
                if (adjustment.saveAdjustment()) {
                    // Update item stock
                    int newStock;
                    if (adjustmentType.equals("ADD")) {
                        newStock = item.getCurrentStock() + quantity;
                    } else {
                        newStock = item.getCurrentStock() - quantity;
                        if (newStock < 0) {
                            JOptionPane.showMessageDialog(adjustmentDialog, 
                                "Cannot adjust to negative stock level", 
                                "Error", 
                                JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    }
                    
                    item.setCurrentStock(newStock);
                    item.updateItem();
                    
                    JOptionPane.showMessageDialog(adjustmentDialog, 
                        "Stock adjustment saved successfully", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    adjustmentDialog.dispose();
                    loadItems();
                } else {
                    JOptionPane.showMessageDialog(adjustmentDialog, 
                        "Failed to save stock adjustment", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                }
                
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(adjustmentDialog, 
                    "Please enter a valid quantity", 
                    "Validation Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        });
        buttonPanel.add(saveButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        adjustmentDialog.add(mainPanel);
        adjustmentDialog.setVisible(true);
    }
    
    // Custom renderer for action buttons
    private class ButtonRenderer extends JButton implements TableCellRenderer {
        public ButtonRenderer() {
            setOpaque(true);
            setFocusPainted(false);
            setBorderPainted(false);
            setHorizontalAlignment(JButton.CENTER);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            setText("⋯");
            setToolTipText("Actions");
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                setBackground(table.getBackground());
                setForeground(table.getForeground());
            }
            
            return this;
        }
    }
} 