package com.owsb.view.panels;

import com.owsb.model.*;
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
    private JComboBox<String> statusFilter;
    private JComboBox<String> locationFilter;
    private JButton addButton;
    
    // Supplier form components
    private JDialog supplierDialog;
    private JTextField supplierCodeField;
    private JTextField companyNameField;
    private JTextField contactPersonField;
    private JTextField phoneNumberField;
    private JTextField emailAddressField;
    private JTextField locationField;
    private JTextArea addressField;
    private JTextField paymentTermsField;
    
    // List of all suppliers
    private List<Supplier> suppliers;
    
    public SupplierManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        initializeTable();
        setupUI();
        loadSuppliers();
    }
    
    private void initializeTable() {
        String[] columnNames = {
            "Supplier Code", "Company Name", "Contact Person", "Phone", 
            "Email", "Location", "Items Supplied", "Status", "Actions"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only Actions column is editable
            }
        };
        
        supplierTable = new JTable(tableModel);
        supplierTable.setRowHeight(32);
        supplierTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        supplierTable.setShowGrid(true);
        supplierTable.setGridColor(new Color(229, 231, 235));
        supplierTable.setIntercellSpacing(new Dimension(1, 1));
        supplierTable.setSelectionBackground(new Color(239, 246, 255));
        supplierTable.setSelectionForeground(new Color(31, 41, 55));
        supplierTable.setBackground(Color.WHITE);
        
        // Set column widths
        supplierTable.getColumnModel().getColumn(0).setPreferredWidth(90);  // Supplier Code
        supplierTable.getColumnModel().getColumn(1).setPreferredWidth(150); // Company Name
        supplierTable.getColumnModel().getColumn(2).setPreferredWidth(120); // Contact Person
        supplierTable.getColumnModel().getColumn(3).setPreferredWidth(100); // Phone
        supplierTable.getColumnModel().getColumn(4).setPreferredWidth(150); // Email
        supplierTable.getColumnModel().getColumn(5).setPreferredWidth(100); // Location
        supplierTable.getColumnModel().getColumn(6).setPreferredWidth(90);  // Items Supplied
        supplierTable.getColumnModel().getColumn(7).setPreferredWidth(80);  // Status
        supplierTable.getColumnModel().getColumn(8).setPreferredWidth(200); // Actions
        
        // Header styling
        JTableHeader header = supplierTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(new Color(249, 250, 251));
        header.setForeground(new Color(55, 65, 81));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, new Color(229, 231, 235)));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        header.setReorderingAllowed(false);
        
        // Custom renderer for Status column
        supplierTable.getColumnModel().getColumn(7).setCellRenderer(new StatusCellRenderer());
        
        // Custom renderer and editor for Actions column
        supplierTable.getColumnModel().getColumn(8).setCellRenderer(new ActionButtonRenderer());
        supplierTable.getColumnModel().getColumn(8).setCellEditor(new ActionButtonEditor());
        
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
        for (int i = 0; i < 7; i++) {
            supplierTable.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }
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
        JLabel titleLabel = new JLabel("Supplier Management");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(31, 41, 55));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Add New Supplier button
        addButton = new JButton("Add New Supplier");
        addButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        addButton.setBackground(new Color(59, 130, 246));
        addButton.setForeground(Color.WHITE);
        addButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        addButton.setFocusPainted(false);
        addButton.setBorderPainted(false);
        addButton.setOpaque(true);
        addButton.addActionListener(e -> showSupplierDialog(null));
        headerPanel.add(addButton, BorderLayout.EAST);
        
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Search and filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        
        // Search field
        searchField = new JTextField("Search suppliers...", 25);
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
                if (searchField.getText().equals("Search suppliers...")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(31, 41, 55));
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().isEmpty()) {
                    searchField.setText("Search suppliers...");
                    searchField.setForeground(new Color(156, 163, 175));
                }
            }
        });
        
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (!searchField.getText().equals("Search suppliers...")) {
                    filterSuppliers();
                }
            }
        });
        
        // Status filter
        String[] statuses = {"All Status", "Active", "Inactive"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilter.setPreferredSize(new Dimension(160, 44));
        statusFilter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)
        ));
        statusFilter.setBackground(Color.WHITE);
        statusFilter.addActionListener(e -> filterSuppliers());
        
        // Location filter
        String[] locations = {"Shah Alam", "Kuala Lumpur", "Petaling Jaya", "Subang Jaya", "Klang"};
        locationFilter = new JComboBox<>(locations);
        locationFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        locationFilter.setPreferredSize(new Dimension(160, 44));
        locationFilter.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(209, 213, 219), 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)
        ));
        locationFilter.setBackground(Color.WHITE);
        locationFilter.addActionListener(e -> filterSuppliers());
        
        filterPanel.add(searchField);
        filterPanel.add(statusFilter);
        filterPanel.add(locationFilter);
        
        // Create main content panel for filters and table
        JPanel mainContentPanel = new JPanel(new BorderLayout());
        mainContentPanel.setBackground(Color.WHITE);
        mainContentPanel.add(filterPanel, BorderLayout.NORTH);
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(supplierTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(new Color(229, 231, 235), 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(1200, 320));
        
        mainContentPanel.add(scrollPane, BorderLayout.CENTER);
        contentPanel.add(mainContentPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private void loadSuppliers() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load suppliers from data source
        suppliers = Supplier.getAllSuppliers();
        
        // If no suppliers exist, add some sample data for testing
        if (suppliers == null || suppliers.isEmpty()) {
            Object[][] sampleData = {
                {"SUP-001", "ABC Trading Sdn Bhd", "Ahmad Rahman", "03-1234-5678", "ahmad@abctrading.com", "Kuala Lumpur", "15 items", "Active"},
                {"SUP-002", "XYZ Supplies Ltd", "Lim Wei Ming", "03-2345-6789", "wm.lim@xyzsupplies.com", "Petaling Jaya", "8 items", "Active"},
                {"SUP-003", "Global Foods Enterprise", "Raj Kumar", "03-3456-7890", "raj@globalfoods.com", "Shah Alam", "22 items", "Inactive"}
            };
            
            for (Object[] row : sampleData) {
                tableModel.addRow(new Object[]{
                    row[0], row[1], row[2], row[3], row[4], 
                    row[5], row[6], row[7], ""
                });
            }
            return;
        }
        
        for (Supplier supplier : suppliers) {
            // Determine status (you may want to add this field to Supplier model)
            String status = "Active"; // Default status
            
            // Count items supplied
            int itemCount = supplier.getItemsCodes() != null ? supplier.getItemsCodes().size() : 0;
            String itemsSupplied = itemCount + " items";
            
            // Add row to table
            Object[] rowData = {
                supplier.getSupplierId(),
                supplier.getSupplierName(),
                supplier.getContactPerson(),
                supplier.getPhoneNumber(),
                supplier.getEmail(),
                supplier.getAddress(), // Using address as location for now
                itemsSupplied,
                status,
                "" // Actions column (handled by custom renderer)
            };
            
            tableModel.addRow(rowData);
        }
    }
    
    private void filterSuppliers() {
        String searchText = searchField.getText().toLowerCase().trim();
        String selectedStatus = (String) statusFilter.getSelectedItem();
        String selectedLocation = (String) locationFilter.getSelectedItem();
        
        // Clear table
        tableModel.setRowCount(0);
        
        // If no suppliers loaded, show sample data
        if (suppliers == null || suppliers.isEmpty()) {
            Object[][] sampleData = {
                {"SUP-001", "ABC Trading Sdn Bhd", "Ahmad Rahman", "03-1234-5678", "ahmad@abctrading.com", "Kuala Lumpur", "15 items", "Active"},
                {"SUP-002", "XYZ Supplies Ltd", "Lim Wei Ming", "03-2345-6789", "wm.lim@xyzsupplies.com", "Petaling Jaya", "8 items", "Active"},
                {"SUP-003", "Global Foods Enterprise", "Raj Kumar", "03-3456-7890", "raj@globalfoods.com", "Shah Alam", "22 items", "Inactive"}
            };
            
            for (Object[] row : sampleData) {
                String supplierName = row[1].toString().toLowerCase();
                String supplierCode = row[0].toString().toLowerCase();
                String contactPerson = row[2].toString().toLowerCase();
                String status = row[7].toString();
                String location = row[5].toString();
                
                boolean matchesSearch = searchText.isEmpty() || searchText.equals("search suppliers...") ||
                    supplierName.contains(searchText) || supplierCode.contains(searchText) || contactPerson.contains(searchText);
                
                boolean matchesStatus = selectedStatus.equals("All Status") || status.equals(selectedStatus);
                boolean matchesLocation = selectedLocation == null || location.contains(selectedLocation);
                
                if (matchesSearch && matchesStatus && matchesLocation) {
                    tableModel.addRow(new Object[]{
                        row[0], row[1], row[2], row[3], row[4], 
                        row[5], row[6], row[7], ""
                    });
                }
            }
            return;
        }
        
        for (Supplier supplier : suppliers) {
            // Apply filters
            boolean matchesSearch = searchText.isEmpty() || searchText.equals("search suppliers...") ||
                supplier.getSupplierName().toLowerCase().contains(searchText) ||
                supplier.getSupplierId().toLowerCase().contains(searchText) ||
                supplier.getContactPerson().toLowerCase().contains(searchText);
            
            boolean matchesStatus = selectedStatus.equals("All Status"); // For now, all are active
            boolean matchesLocation = selectedLocation == null || 
                supplier.getAddress().contains(selectedLocation);
            
            if (matchesSearch && matchesStatus && matchesLocation) {
                // Determine status
                String status = "Active"; // Default status
                
                // Count items supplied
                int itemCount = supplier.getItemsCodes() != null ? supplier.getItemsCodes().size() : 0;
                String itemsSupplied = itemCount + " items";
                
                // Add row to table
                Object[] rowData = {
                    supplier.getSupplierId(),
                    supplier.getSupplierName(),
                    supplier.getContactPerson(),
                    supplier.getPhoneNumber(),
                    supplier.getEmail(),
                    supplier.getAddress(),
                    itemsSupplied,
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
                    case "Inactive":
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
        private JButton viewButton, editButton, deleteButton, activateButton;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 2, 3));
            setOpaque(true);
            
            viewButton = new JButton("View");
            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");
            activateButton = new JButton("Activate");
            
            styleButton(viewButton, new Color(59, 130, 246));
            styleButton(editButton, new Color(249, 115, 22));
            styleButton(deleteButton, new Color(239, 68, 68));
            styleButton(activateButton, new Color(34, 197, 94));
            
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
            button.setPreferredSize(new Dimension(45, 22));
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value,
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            // Check if this row is inactive to show activate button
            String status = (String) table.getValueAt(row, 7);
            removeAll();
            add(viewButton);
            add(editButton);
            if ("Inactive".equals(status)) {
                add(activateButton);
            } else {
                add(deleteButton);
            }
            
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
        private JButton viewButton, editButton, deleteButton, activateButton;
        private int currentRow;
        
        public ActionButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 2, 3));
            panel.setOpaque(true);
            
            viewButton = new JButton("View");
            editButton = new JButton("Edit");
            deleteButton = new JButton("Delete");
            activateButton = new JButton("Activate");
            
            styleButton(viewButton, new Color(59, 130, 246));
            styleButton(editButton, new Color(249, 115, 22));
            styleButton(deleteButton, new Color(239, 68, 68));
            styleButton(activateButton, new Color(34, 197, 94));
            
            viewButton.addActionListener(e -> {
                viewSupplier(currentRow);
                fireEditingStopped();
            });
            
            editButton.addActionListener(e -> {
                editSupplier(currentRow);
                fireEditingStopped();
            });
            
            deleteButton.addActionListener(e -> {
                deleteSupplier(currentRow);
                fireEditingStopped();
            });
            
            activateButton.addActionListener(e -> {
                activateSupplier(currentRow);
                fireEditingStopped();
            });
        }
        
        private void styleButton(JButton button, Color bgColor) {
            button.setBackground(bgColor);
            button.setForeground(Color.WHITE);
            button.setFocusPainted(false);
            button.setBorderPainted(false);
            button.setOpaque(true);
            button.setFont(new Font("Segoe UI", Font.BOLD, 10));
            button.setPreferredSize(new Dimension(45, 22));
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value,
                boolean isSelected, int row, int column) {
            currentRow = row;
            
            // Check if this row is inactive to show activate button
            String status = (String) table.getValueAt(row, 7);
            panel.removeAll();
            panel.add(viewButton);
            panel.add(editButton);
            if ("Inactive".equals(status)) {
                panel.add(activateButton);
            } else {
                panel.add(deleteButton);
            }
            
            panel.setBackground(Color.WHITE);
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
        
        private void viewSupplier(int row) {
            String supplierCode = (String) tableModel.getValueAt(row, 0);
            Supplier supplier = Supplier.getSupplierById(supplierCode);
            
            if (supplier != null) {
                showSupplierDetailsDialog(supplier);
            } else {
                // Show sample data details
                StringBuilder info = new StringBuilder();
                info.append("Supplier Details:\n\n");
                for (int i = 0; i < supplierTable.getColumnCount() - 1; i++) {
                    info.append(supplierTable.getColumnName(i)).append(": ")
                        .append(supplierTable.getValueAt(row, i)).append("\n");
                }
                JOptionPane.showMessageDialog(supplierTable, info.toString(), "Supplier Details", JOptionPane.INFORMATION_MESSAGE);
            }
        }
        
        private void editSupplier(int row) {
            String supplierCode = (String) tableModel.getValueAt(row, 0);
            Supplier supplier = Supplier.getSupplierById(supplierCode);
            
            if (supplier != null) {
                showSupplierDialog(supplier);
            } else {
                JOptionPane.showMessageDialog(supplierTable, "Edit functionality would open an edit dialog for row " + (row + 1));
            }
        }
        
        private void deleteSupplier(int row) {
            String supplierCode = (String) tableModel.getValueAt(row, 0);
            String supplierName = (String) tableModel.getValueAt(row, 1);
            
            int result = JOptionPane.showConfirmDialog(
                supplierTable, 
                "Are you sure you want to delete supplier: " + supplierName + " (" + supplierCode + ")?", 
                "Confirm Delete", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.WARNING_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                if (Supplier.deleteSupplier(supplierCode)) {
                    JOptionPane.showMessageDialog(supplierTable, "Supplier deleted successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadSuppliers();
                } else {
                    // For sample data, just remove from table
                    tableModel.removeRow(row);
                }
            }
        }
        
        private void activateSupplier(int row) {
            String supplierCode = (String) tableModel.getValueAt(row, 0);
            String supplierName = (String) tableModel.getValueAt(row, 1);
            
            int result = JOptionPane.showConfirmDialog(
                supplierTable, 
                "Are you sure you want to activate supplier: " + supplierName + " (" + supplierCode + ")?", 
                "Confirm Activation", 
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (result == JOptionPane.YES_OPTION) {
                // Update status in table
                tableModel.setValueAt("Active", row, 7);
                JOptionPane.showMessageDialog(supplierTable, "Supplier activated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
    
    private void showSupplierDetailsDialog(Supplier supplier) {
        JDialog detailsDialog = new JDialog();
        detailsDialog.setTitle("Supplier Details - " + supplier.getSupplierName());
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
        
        // Add supplier details
        addDetailRow(detailsPanel, gbc, 0, "Supplier Code:", supplier.getSupplierId());
        addDetailRow(detailsPanel, gbc, 1, "Company Name:", supplier.getSupplierName());
        addDetailRow(detailsPanel, gbc, 2, "Contact Person:", supplier.getContactPerson());
        addDetailRow(detailsPanel, gbc, 3, "Phone:", supplier.getPhoneNumber());
        addDetailRow(detailsPanel, gbc, 4, "Email:", supplier.getEmail());
        addDetailRow(detailsPanel, gbc, 5, "Address:", supplier.getAddress());
        
        int itemCount = supplier.getItemsCodes() != null ? supplier.getItemsCodes().size() : 0;
        addDetailRow(detailsPanel, gbc, 6, "Items Supplied:", itemCount + " items");
        
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
        closeButton.setBorderPainted(false);
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
    
    private void showSupplierDialog(Supplier supplier) {
        boolean isEditMode = (supplier != null);
        
        // Create dialog
        supplierDialog = new JDialog();
        supplierDialog.setTitle(isEditMode ? "Edit Supplier" : "Add/Edit Supplier");
        supplierDialog.setSize(600, 700);
        supplierDialog.setLocationRelativeTo(this);
        supplierDialog.setModal(true);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel(isEditMode ? "Edit Supplier" : "Add/Edit Supplier");
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
        
        // Supplier Code
        addFormField(formPanel, gbc, 0, "Supplier Code", 
            supplierCodeField = createTextField(isEditMode ? supplier.getSupplierId() : Supplier.generateSupplierId(), !isEditMode));
        
        // Company Name
        addFormField(formPanel, gbc, 1, "Company Name", 
            companyNameField = createTextField(isEditMode ? supplier.getSupplierName() : "", true));
        
        // Contact Person
        addFormField(formPanel, gbc, 2, "Contact Person", 
            contactPersonField = createTextField(isEditMode ? supplier.getContactPerson() : "", true));
        
        // Phone Number
        addFormField(formPanel, gbc, 3, "Phone Number", 
            phoneNumberField = createTextField(isEditMode ? supplier.getPhoneNumber() : "", true));
        
        // Email Address
        addFormField(formPanel, gbc, 4, "Email Address", 
            emailAddressField = createTextField(isEditMode ? supplier.getEmail() : "", true));
        
        // Location
        addFormField(formPanel, gbc, 5, "Location", 
            locationField = createTextField("", true));
        
        // Address
        addressField = new JTextArea(4, 30);
        addressField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        addressField.setLineWrap(true);
        addressField.setWrapStyleWord(true);
        addressField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224)),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        if (isEditMode && supplier.getAddress() != null) {
            addressField.setText(supplier.getAddress());
        }
        JScrollPane addressScrollPane = new JScrollPane(addressField);
        addressScrollPane.setPreferredSize(new Dimension(300, 100));
        addFormField(formPanel, gbc, 6, "Address", addressScrollPane);
        
        // Payment Terms
        addFormField(formPanel, gbc, 7, "Payment Terms", 
            paymentTermsField = createTextField("Net 30", true));
        
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
        cancelButton.addActionListener(e -> supplierDialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = new JButton("Save Supplier");
        saveButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        saveButton.setBackground(new Color(59, 130, 246));
        saveButton.setForeground(Color.WHITE);
        saveButton.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));
        saveButton.setFocusPainted(false);
        saveButton.setBorderPainted(false);
        saveButton.setOpaque(true);
        saveButton.addActionListener(e -> saveSupplier(isEditMode, supplier));
        buttonPanel.add(saveButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        supplierDialog.add(mainPanel);
        supplierDialog.setVisible(true);
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
    
    private void saveSupplier(boolean isEditMode, Supplier existingSupplier) {
        // Validate input
        if (companyNameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(supplierDialog, "Please enter a company name", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (contactPersonField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(supplierDialog, "Please enter a contact person", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        if (phoneNumberField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(supplierDialog, "Please enter a phone number", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Get values from form
        String supplierCode = supplierCodeField.getText().trim();
        String companyName = companyNameField.getText().trim();
        String contactPerson = contactPersonField.getText().trim();
        String phoneNumber = phoneNumberField.getText().trim();
        String emailAddress = emailAddressField.getText().trim();
        String address = addressField.getText().trim();
        
        // Create or update supplier
        Supplier supplier = new Supplier(supplierCode, companyName, contactPerson, address, phoneNumber, emailAddress);
        
        if (isEditMode && existingSupplier != null) {
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