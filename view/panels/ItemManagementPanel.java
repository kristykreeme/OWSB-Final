package view.panels;

import model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ItemManagementPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTable itemTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    
    // Item form components
    private JDialog itemDialog;
    private JTextField itemCodeField;
    private JTextField itemNameField;
    private JComboBox<Supplier> supplierComboBox;
    private JComboBox<String> categoryComboBox;
    private JTextField stockQuantityField;
    private JTextField unitPriceField;
    
    // List of all items
    private List<Item> items;
    
    public ItemManagementPanel(User user) {
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
        actionPanel.add(searchPanel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        addButton = new JButton("Add Item");
        addButton.setBackground(new Color(0, 102, 204));
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.setMinimumSize(new Dimension(120, 35));
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.addActionListener(e -> showItemDialog(null));
        buttonPanel.add(addButton);
        
        editButton = new JButton("Edit");
        editButton.setBackground(new Color(46, 184, 46));
        editButton.setForeground(Color.WHITE);
        editButton.setOpaque(true);
        editButton.setBorderPainted(false);
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.setMinimumSize(new Dimension(100, 35));
        editButton.setFont(new Font("Arial", Font.BOLD, 12));
        editButton.addActionListener(e -> editSelectedItem());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(255, 51, 51));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.setMinimumSize(new Dimension(100, 35));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.addActionListener(e -> deleteSelectedItem());
        buttonPanel.add(deleteButton);
        
        actionPanel.add(buttonPanel, BorderLayout.EAST);
        
        contentPanel.add(actionPanel, BorderLayout.NORTH);
        
        // Table
        createItemTable();
        JScrollPane scrollPane = new JScrollPane(itemTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Load data
        loadItems();
    }
    
    private void createItemTable() {
        // Define table columns
        String[] columns = {"Item Code", "Item Name", "Supplier", "Category", "Stock Level", "Status"};
        
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
        itemTable.setRowHeight(25);
        itemTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        itemTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        itemTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        itemTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        itemTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        itemTable.getColumnModel().getColumn(4).setPreferredWidth(100);
        itemTable.getColumnModel().getColumn(5).setPreferredWidth(100);
        
        // Add double-click listener to edit item
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedItem();
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
            Supplier supplier = Supplier.getSupplierById(item.getSupplierId());
            String supplierName = supplier != null ? supplier.getSupplierName() : "Unknown";
            
            String status = (item.getStockQuantity() > 0) ? "In Stock" : "Out of Stock";
            String stockLevel = String.valueOf(item.getStockQuantity()) + " units";
            
            Object[] row = {
                item.getItemCode(),
                item.getItemName(),
                supplierName,
                item.getCategory(),
                stockLevel,
                status
            };
            
            tableModel.addRow(row);
        }
    }
    
    private void filterItems() {
        String searchTerm = searchField.getText().toLowerCase();
        
        tableModel.setRowCount(0);
        
        for (Item item : items) {
            if (item.getItemCode().toLowerCase().contains(searchTerm) || 
                item.getItemName().toLowerCase().contains(searchTerm)) {
                
                Supplier supplier = Supplier.getSupplierById(item.getSupplierId());
                String supplierName = supplier != null ? supplier.getSupplierName() : "Unknown";
                
                String status = (item.getStockQuantity() > 0) ? "In Stock" : "Out of Stock";
                String stockLevel = String.valueOf(item.getStockQuantity()) + " units";
                
                Object[] row = {
                    item.getItemCode(),
                    item.getItemName(),
                    supplierName,
                    item.getCategory(),
                    stockLevel,
                    status
                };
                
                tableModel.addRow(row);
            }
        }
    }
    
    private void editSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String itemCode = (String) tableModel.getValueAt(selectedRow, 0);
        Item selectedItem = null;
        
        for (Item item : items) {
            if (item.getItemCode().equals(itemCode)) {
                selectedItem = item;
                break;
            }
        }
        
        if (selectedItem != null) {
            showItemDialog(selectedItem);
        }
    }
    
    private void deleteSelectedItem() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an item to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String itemCode = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this item?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmation == JOptionPane.YES_OPTION) {
            if (Item.deleteItem(itemCode)) {
                JOptionPane.showMessageDialog(this, "Item deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadItems();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete item", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void showItemDialog(Item item) {
        boolean isEditMode = (item != null);
        
        // Create dialog
        itemDialog = new JDialog();
        itemDialog.setTitle(isEditMode ? "Edit Item" : "Add New Item");
        itemDialog.setSize(400, 400);
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
        itemCodeField.setEditable(!isEditMode); // Only editable when adding new item
        if (isEditMode) {
            itemCodeField.setText(item.getItemCode());
        } else {
            itemCodeField.setText(Item.generateItemCode());
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
        
        // Supplier
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Supplier:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        supplierComboBox = new JComboBox<>();
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
        formPanel.add(supplierComboBox, gbc);
        
        // Category
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Category:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        String[] categories = {"Grains", "Dairy", "Cooking", "Spices", "Miscellaneous"};
        categoryComboBox = new JComboBox<>(categories);
        if (isEditMode) {
            for (int i = 0; i < categories.length; i++) {
                if (categories[i].equals(item.getCategory())) {
                    categoryComboBox.setSelectedIndex(i);
                    break;
                }
            }
        }
        formPanel.add(categoryComboBox, gbc);
        
        // Stock Quantity
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Stock Quantity:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        stockQuantityField = new JTextField(15);
        if (isEditMode) {
            stockQuantityField.setText(String.valueOf(item.getStockQuantity()));
        } else {
            stockQuantityField.setText("0");
        }
        formPanel.add(stockQuantityField, gbc);
        
        // Unit Price
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Unit Price (RM):"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        unitPriceField = new JTextField(15);
        if (isEditMode) {
            unitPriceField.setText(String.valueOf(item.getUnitPrice()));
        } else {
            unitPriceField.setText("0.00");
        }
        formPanel.add(unitPriceField, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelButton.addActionListener(e -> itemDialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
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
        
        // Get values from form
        String itemCode = itemCodeField.getText().trim();
        String itemName = itemNameField.getText().trim();
        Supplier selectedSupplier = (Supplier) supplierComboBox.getSelectedItem();
        String supplierId = selectedSupplier.getSupplierId();
        String category = (String) categoryComboBox.getSelectedItem();
        
        int stockQuantity = 0;
        double unitPrice = 0.0;
        
        try {
            stockQuantity = Integer.parseInt(stockQuantityField.getText().trim());
            if (stockQuantity < 0) {
                JOptionPane.showMessageDialog(itemDialog, "Stock quantity cannot be negative", "Validation Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(itemDialog, "Please enter a valid stock quantity", "Validation Error", JOptionPane.ERROR_MESSAGE);
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
        
        // Create or update item
        Item item = new Item(
            itemCode, 
            itemName, 
            "", // description (empty by default)
            category, 
            unitPrice, 
            stockQuantity, 
            10, // default reorder level
            supplierId
        );
        
        boolean success;
        if (isEditMode) {
            success = item.updateItem();
        } else {
            success = item.saveItem();
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