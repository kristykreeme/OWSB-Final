package view.panels;

import model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class SupplierManagementPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTable supplierTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    private JButton viewDetailsButton;
    
    // Supplier form components
    private JDialog supplierDialog;
    private JTextField supplierIdField;
    private JTextField supplierNameField;
    private JTextField contactPersonField;
    private JTextField addressField;
    private JTextField phoneField;
    private JTextField emailField;
    
    // List of all suppliers
    private List<Supplier> suppliers;
    
    public SupplierManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Supplier Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Search and buttons panel
        JPanel actionPanel = new JPanel(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search Supplier: "));
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterSuppliers();
            }
        });
        searchPanel.add(searchField);
        actionPanel.add(searchPanel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        addButton = new JButton("Add Supplier");
        addButton.setBackground(new Color(0, 102, 204));
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.setMinimumSize(new Dimension(120, 35));
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.addActionListener(e -> showSupplierDialog(null));
        buttonPanel.add(addButton);
        
        editButton = new JButton("Edit");
        editButton.setBackground(new Color(46, 184, 46));
        editButton.setForeground(Color.WHITE);
        editButton.setOpaque(true);
        editButton.setBorderPainted(false);
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.setMinimumSize(new Dimension(100, 35));
        editButton.setFont(new Font("Arial", Font.BOLD, 12));
        editButton.addActionListener(e -> editSelectedSupplier());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(255, 51, 51));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.setMinimumSize(new Dimension(100, 35));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.addActionListener(e -> deleteSelectedSupplier());
        buttonPanel.add(deleteButton);
        
        viewDetailsButton = new JButton("View Details");
        viewDetailsButton.setBackground(new Color(100, 100, 100));
        viewDetailsButton.setForeground(Color.WHITE);
        viewDetailsButton.setOpaque(true);
        viewDetailsButton.setBorderPainted(false);
        viewDetailsButton.setPreferredSize(new Dimension(120, 35));
        viewDetailsButton.setMinimumSize(new Dimension(120, 35));
        viewDetailsButton.setFont(new Font("Arial", Font.BOLD, 12));
        viewDetailsButton.addActionListener(e -> viewSupplierDetails());
        buttonPanel.add(viewDetailsButton);
        
        actionPanel.add(buttonPanel, BorderLayout.EAST);
        
        contentPanel.add(actionPanel, BorderLayout.NORTH);
        
        // Table
        createSupplierTable();
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Load data
        loadSuppliers();
    }
    
    private void createSupplierTable() {
        // Define table columns
        String[] columns = {"Supplier ID", "Supplier Name", "Contact Person", "Phone", "Email"};
        
        // Create table model
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        // Create table
        supplierTable = new JTable(tableModel);
        supplierTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        supplierTable.setRowHeight(25);
        supplierTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        supplierTable.getColumnModel().getColumn(0).setPreferredWidth(100);
        supplierTable.getColumnModel().getColumn(1).setPreferredWidth(200);
        supplierTable.getColumnModel().getColumn(2).setPreferredWidth(150);
        supplierTable.getColumnModel().getColumn(3).setPreferredWidth(120);
        supplierTable.getColumnModel().getColumn(4).setPreferredWidth(200);
        
        // Add double-click listener to view details
        supplierTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewSupplierDetails();
                }
            }
        });
    }
    
    private void loadSuppliers() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load suppliers from model
        suppliers = Supplier.getAllSuppliers();
        
        // Add suppliers to table
        for (Supplier supplier : suppliers) {
            Object[] row = {
                supplier.getSupplierId(),
                supplier.getSupplierName(),
                supplier.getContactPerson(),
                supplier.getPhone(),
                supplier.getEmail()
            };
            
            tableModel.addRow(row);
        }
    }
    
    private void filterSuppliers() {
        String searchTerm = searchField.getText().toLowerCase();
        
        tableModel.setRowCount(0);
        
        for (Supplier supplier : suppliers) {
            if (supplier.getSupplierId().toLowerCase().contains(searchTerm) || 
                supplier.getSupplierName().toLowerCase().contains(searchTerm) || 
                supplier.getContactPerson().toLowerCase().contains(searchTerm)) {
                
                Object[] row = {
                    supplier.getSupplierId(),
                    supplier.getSupplierName(),
                    supplier.getContactPerson(),
                    supplier.getPhone(),
                    supplier.getEmail()
                };
                
                tableModel.addRow(row);
            }
        }
    }
    
    private void editSelectedSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String supplierId = (String) tableModel.getValueAt(selectedRow, 0);
        Supplier selectedSupplier = null;
        
        for (Supplier supplier : suppliers) {
            if (supplier.getSupplierId().equals(supplierId)) {
                selectedSupplier = supplier;
                break;
            }
        }
        
        if (selectedSupplier != null) {
            showSupplierDialog(selectedSupplier);
        }
    }
    
    private void deleteSelectedSupplier() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String supplierId = (String) tableModel.getValueAt(selectedRow, 0);
        
        int confirmation = JOptionPane.showConfirmDialog(
            this,
            "Are you sure you want to delete this supplier?",
            "Confirm Deletion",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE
        );
        
        if (confirmation == JOptionPane.YES_OPTION) {
            if (Supplier.deleteSupplier(supplierId)) {
                JOptionPane.showMessageDialog(this, "Supplier deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                loadSuppliers();
            } else {
                JOptionPane.showMessageDialog(this, "Failed to delete supplier", "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    private void viewSupplierDetails() {
        int selectedRow = supplierTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a supplier to view details", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String supplierId = (String) tableModel.getValueAt(selectedRow, 0);
        Supplier selectedSupplier = null;
        
        for (Supplier supplier : suppliers) {
            if (supplier.getSupplierId().equals(supplierId)) {
                selectedSupplier = supplier;
                break;
            }
        }
        
        if (selectedSupplier != null) {
            // Create dialog
            JDialog detailsDialog = new JDialog();
            detailsDialog.setTitle("Supplier Details - " + selectedSupplier.getSupplierName());
            detailsDialog.setSize(500, 500);
            detailsDialog.setLocationRelativeTo(this);
            detailsDialog.setModal(true);
            
            // Main panel
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
            
            // Basic info panel
            JPanel infoPanel = new JPanel(new GridLayout(7, 2, 10, 10));
            infoPanel.setBorder(BorderFactory.createTitledBorder("Supplier Information"));
            
            infoPanel.add(new JLabel("ID:"));
            infoPanel.add(new JLabel(selectedSupplier.getSupplierId()));
            
            infoPanel.add(new JLabel("Name:"));
            infoPanel.add(new JLabel(selectedSupplier.getSupplierName()));
            
            infoPanel.add(new JLabel("Contact Person:"));
            infoPanel.add(new JLabel(selectedSupplier.getContactPerson()));
            
            infoPanel.add(new JLabel("Address:"));
            infoPanel.add(new JLabel(selectedSupplier.getAddress()));
            
            infoPanel.add(new JLabel("Phone:"));
            infoPanel.add(new JLabel(selectedSupplier.getPhone()));
            
            infoPanel.add(new JLabel("Email:"));
            infoPanel.add(new JLabel(selectedSupplier.getEmail()));
            
            // Items panel
            JPanel itemsPanel = new JPanel(new BorderLayout());
            itemsPanel.setBorder(BorderFactory.createTitledBorder("Supplied Items"));
            
            DefaultListModel<String> itemsListModel = new DefaultListModel<>();
            List<String> itemCodes = selectedSupplier.getItemsCodes();
            for (String itemCode : itemCodes) {
                Item item = Item.getItemByCode(itemCode);
                if (item != null) {
                    itemsListModel.addElement(item.getItemCode() + " - " + item.getItemName());
                } else {
                    itemsListModel.addElement(itemCode + " - [Item not found]");
                }
            }
            
            JList<String> itemsList = new JList<>(itemsListModel);
            JScrollPane itemsScrollPane = new JScrollPane(itemsList);
            itemsPanel.add(itemsScrollPane, BorderLayout.CENTER);
            
            // Previous orders panel (not implemented fully)
            JPanel ordersPanel = new JPanel(new BorderLayout());
            ordersPanel.setBorder(BorderFactory.createTitledBorder("Recent Orders"));
            
            DefaultListModel<String> ordersListModel = new DefaultListModel<>();
            ordersListModel.addElement("No recent orders found");
            JList<String> ordersList = new JList<>(ordersListModel);
            JScrollPane ordersScrollPane = new JScrollPane(ordersList);
            ordersPanel.add(ordersScrollPane, BorderLayout.CENTER);
            
            // Add panels to main panel
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.add(infoPanel, BorderLayout.NORTH);
            
            JPanel listsPanel = new JPanel(new GridLayout(2, 1, 0, 10));
            listsPanel.add(itemsPanel);
            listsPanel.add(ordersPanel);
            contentPanel.add(listsPanel, BorderLayout.CENTER);
            
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            
            // Close button
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
            JButton closeButton = new JButton("Close");
            closeButton.addActionListener(e -> detailsDialog.dispose());
            buttonPanel.add(closeButton);
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            detailsDialog.add(mainPanel);
            detailsDialog.setVisible(true);
        }
    }
    
    private void showSupplierDialog(Supplier supplier) {
        boolean isEditMode = (supplier != null);
        
        // Create dialog
        supplierDialog = new JDialog();
        supplierDialog.setTitle(isEditMode ? "Edit Supplier" : "Add New Supplier");
        supplierDialog.setSize(450, 450);
        supplierDialog.setLocationRelativeTo(this);
        supplierDialog.setModal(true);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // Supplier ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("Supplier ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        supplierIdField = new JTextField(15);
        supplierIdField.setEditable(!isEditMode); // Only editable when adding new supplier
        if (isEditMode) {
            supplierIdField.setText(supplier.getSupplierId());
        } else {
            supplierIdField.setText(Supplier.generateSupplierId());
        }
        formPanel.add(supplierIdField, gbc);
        
        // Supplier Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Supplier Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        supplierNameField = new JTextField(15);
        if (isEditMode) {
            supplierNameField.setText(supplier.getSupplierName());
        }
        formPanel.add(supplierNameField, gbc);
        
        // Contact Person
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Contact Person:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        contactPersonField = new JTextField(15);
        if (isEditMode) {
            contactPersonField.setText(supplier.getContactPerson());
        }
        formPanel.add(contactPersonField, gbc);
        
        // Address
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Address:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        addressField = new JTextField(15);
        if (isEditMode) {
            addressField.setText(supplier.getAddress());
        }
        formPanel.add(addressField, gbc);
        
        // Phone
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Phone:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        phoneField = new JTextField(15);
        if (isEditMode) {
            phoneField.setText(supplier.getPhone());
        }
        formPanel.add(phoneField, gbc);
        
        // Email
        gbc.gridx = 0;
        gbc.gridy = 5;
        formPanel.add(new JLabel("Email:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 5;
        emailField = new JTextField(15);
        if (isEditMode) {
            emailField.setText(supplier.getEmail());
        }
        formPanel.add(emailField, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> supplierDialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.addActionListener(e -> saveSupplier(isEditMode, supplier));
        buttonPanel.add(saveButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        supplierDialog.add(mainPanel);
        supplierDialog.setVisible(true);
    }
    
    private void saveSupplier(boolean isEditMode, Supplier existingSupplier) {
        // Validate input
        if (supplierNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(supplierDialog, "Please enter a supplier name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (contactPersonField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(supplierDialog, "Please enter a contact person", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (phoneField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(supplierDialog, "Please enter a phone number", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get values from form
        String supplierId = supplierIdField.getText().trim();
        String supplierName = supplierNameField.getText().trim();
        String contactPerson = contactPersonField.getText().trim();
        String address = addressField.getText().trim();
        String phone = phoneField.getText().trim();
        String email = emailField.getText().trim();
        
        // Create or update supplier
        Supplier supplier = new Supplier(supplierId, supplierName, contactPerson, address, phone, email);
        
        if (isEditMode) {
            // Preserve the item codes from existing supplier
            supplier.setItemsCodes(existingSupplier.getItemsCodes());
        }
        
        boolean success;
        if (isEditMode) {
            success = supplier.updateSupplier();
        } else {
            success = supplier.saveSupplier();
        }
        
        if (success) {
            JOptionPane.showMessageDialog(supplierDialog, "Supplier saved successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
            supplierDialog.dispose();
            loadSuppliers();
        } else {
            JOptionPane.showMessageDialog(supplierDialog, "Failed to save supplier", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
} 