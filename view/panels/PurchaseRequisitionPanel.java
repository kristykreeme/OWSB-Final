package view.panels;

import model.*;
import model.PurchaseRequisition.PurchaseRequisitionItem;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class PurchaseRequisitionPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable itemTable;
    private DefaultTableModel itemTableModel;
    private JTable prTable;
    private DefaultTableModel prTableModel;
    private JTable prItemsTable;
    private DefaultTableModel prItemsTableModel;
    private JButton createPRButton;
    private JButton viewPRButton;
    
    // Create PR components
    private List<Item> selectedItems = new ArrayList<>();
    private Map<String, Integer> quantityMap = new HashMap<>();
    
    // DateFormat
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public PurchaseRequisitionPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Purchase Requisition");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create PR tab
        JPanel createPRPanel = createPRPanel();
        tabbedPane.addTab("Create Purchase Requisition", createPRPanel);
        
        // View PRs tab
        JPanel viewPRPanel = viewPRPanel();
        tabbedPane.addTab("View Purchase Requisitions", viewPRPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createPRPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel for instructions
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel instructionsLabel = new JLabel("Select items to create a Purchase Requisition. Double-click items to add them to the PR.");
        instructionsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        topPanel.add(instructionsLabel, BorderLayout.WEST);
        
        // Create PR button
        createPRButton = new JButton("Create Purchase Requisition");
        createPRButton.setBackground(new Color(0, 102, 204));
        createPRButton.setForeground(Color.WHITE);
        createPRButton.setOpaque(true);
        createPRButton.setBorderPainted(false);
        createPRButton.setPreferredSize(new Dimension(200, 35));
        createPRButton.setMinimumSize(new Dimension(200, 35));
        createPRButton.setFont(new Font("Arial", Font.BOLD, 12));
        createPRButton.addActionListener(e -> createPurchaseRequisition());
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(createPRButton);
        topPanel.add(buttonPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // Split pane for items and selected items
        JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
        splitPane.setResizeWeight(0.5);
        
        // Items table panel
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBorder(BorderFactory.createTitledBorder("Available Items"));
        
        // Create items table
        createItemsTable();
        JScrollPane itemsScrollPane = new JScrollPane(itemTable);
        itemsPanel.add(itemsScrollPane, BorderLayout.CENTER);
        
        // Add search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search: "));
        JTextField searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterItems(searchField.getText());
            }
        });
        searchPanel.add(searchField);
        itemsPanel.add(searchPanel, BorderLayout.NORTH);
        
        splitPane.setTopComponent(itemsPanel);
        
        // Selected items panel
        JPanel selectedItemsPanel = new JPanel(new BorderLayout());
        selectedItemsPanel.setBorder(BorderFactory.createTitledBorder("Selected Items for PR"));
        
        // Create selected items table
        createSelectedItemsTable();
        JScrollPane selectedItemsScrollPane = new JScrollPane(prItemsTable);
        selectedItemsPanel.add(selectedItemsScrollPane, BorderLayout.CENTER);
        
        // Button panel for selected items
        JPanel selectedButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton removeButton = new JButton("Remove Selected");
        removeButton.setBackground(new Color(255, 51, 51));
        removeButton.setForeground(Color.WHITE);
        removeButton.setOpaque(true);
        removeButton.setBorderPainted(false);
        removeButton.setPreferredSize(new Dimension(150, 35));
        removeButton.setMinimumSize(new Dimension(150, 35));
        removeButton.setFont(new Font("Arial", Font.BOLD, 12));
        removeButton.addActionListener(e -> removeSelectedItem());
        selectedButtonPanel.add(removeButton);
        
        JButton clearButton = new JButton("Clear All");
        clearButton.setBackground(new Color(150, 150, 150));
        clearButton.setForeground(Color.WHITE);
        clearButton.setOpaque(true);
        clearButton.setBorderPainted(false);
        clearButton.setPreferredSize(new Dimension(100, 35));
        clearButton.setMinimumSize(new Dimension(100, 35));
        clearButton.setFont(new Font("Arial", Font.BOLD, 12));
        clearButton.addActionListener(e -> clearSelectedItems());
        selectedButtonPanel.add(clearButton);
        
        selectedItemsPanel.add(selectedButtonPanel, BorderLayout.SOUTH);
        
        splitPane.setBottomComponent(selectedItemsPanel);
        
        panel.add(splitPane, BorderLayout.CENTER);
        
        // Load items
        loadItems();
        
        return panel;
    }
    
    private void createItemsTable() {
        // Define table columns
        String[] columns = {"Item Code", "Item Name", "Category", "Current Stock", "Supplier"};
        
        // Create table model
        itemTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        itemTable = new JTable(itemTableModel);
        itemTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemTable.setRowHeight(25);
        
        // Add double-click listener to add item to PR
        itemTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    addItemToPR();
                }
            }
        });
    }
    
    // Placeholder method for updateTotal since it was referenced in the code 
    private void updateTotal() {
        // Calculate total items or quantities if needed
    }
    
    private void createSelectedItemsTable() {
        // Define table columns
        String[] columns = {"Item Code", "Item Name", "Quantity", "Supplier", "Actions"};
        
        // Create table model
        prItemsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 2; // Only quantity is editable
            }
        };
        
        // Create table
        prItemsTable = new JTable(prItemsTableModel);
        prItemsTable.setRowHeight(25);
        
        // Add cell editor for quantity column
        TableColumn quantityColumn = prItemsTable.getColumnModel().getColumn(2);
        JTextField quantityField = new JTextField();
        quantityField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                    e.consume();
                }
            }
        });
        
        DefaultCellEditor quantityEditor = new DefaultCellEditor(quantityField);
        quantityColumn.setCellEditor(quantityEditor);
        
        // Add table model listener to update quantities
        prItemsTableModel.addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 2) {
                int row = e.getFirstRow();
                String itemCode = (String) prItemsTableModel.getValueAt(row, 0);
                try {
                    int quantity = Integer.parseInt(prItemsTableModel.getValueAt(row, 2).toString());
                    quantityMap.put(itemCode, quantity);
                } catch (NumberFormatException ex) {
                    prItemsTableModel.setValueAt("1", row, 2);
                    quantityMap.put(itemCode, 1);
                }
            }
        });
    }
    
    private void loadItems() {
        // Clear table
        itemTableModel.setRowCount(0);
        
        // Load items
        List<Item> items = Item.getAllItems();
        
        for (Item item : items) {
            Supplier supplier = Supplier.getSupplierById(item.getSupplierId());
            String supplierName = (supplier != null) ? supplier.getSupplierName() : "Unknown";
            
            Object[] row = {
                item.getItemCode(),
                item.getItemName(),
                item.getCategory(),
                item.getStockQuantity() + " units",
                supplierName
            };
            
            itemTableModel.addRow(row);
        }
    }
    
    private void filterItems(String searchText) {
        // Clear table
        itemTableModel.setRowCount(0);
        
        // Load and filter items
        List<Item> items = Item.getAllItems();
        String searchLower = searchText.toLowerCase();
        
        for (Item item : items) {
            if (item.getItemCode().toLowerCase().contains(searchLower) || 
                item.getItemName().toLowerCase().contains(searchLower)) {
                
                Supplier supplier = Supplier.getSupplierById(item.getSupplierId());
                String supplierName = (supplier != null) ? supplier.getSupplierName() : "Unknown";
                
                Object[] row = {
                    item.getItemCode(),
                    item.getItemName(),
                    item.getCategory(),
                    item.getStockQuantity() + " units",
                    supplierName
                };
                
                itemTableModel.addRow(row);
            }
        }
    }
    
    private void addItemToPR() {
        int selectedRow = itemTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        String itemCode = (String) itemTableModel.getValueAt(selectedRow, 0);
        String itemName = (String) itemTableModel.getValueAt(selectedRow, 1);
        String supplierName = (String) itemTableModel.getValueAt(selectedRow, 4);
        
        // Check if item is already in the list
        for (int i = 0; i < prItemsTableModel.getRowCount(); i++) {
            if (prItemsTableModel.getValueAt(i, 0).equals(itemCode)) {
                JOptionPane.showMessageDialog(this, 
                    "This item is already in the requisition list.", 
                    "Duplicate Item", 
                    JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        // Add to the list of selected items
        Item item = Item.getItemByCode(itemCode);
        if (item != null) {
            selectedItems.add(item);
            
            // Default quantity is 10 or current stock if less than 10
            int defaultQuantity = Math.min(10, Math.max(1, 10 - item.getStockQuantity()));
            quantityMap.put(itemCode, defaultQuantity);
            
            Object[] row = {
                itemCode,
                itemName,
                defaultQuantity,
                supplierName,
                "Remove"
            };
            
            prItemsTableModel.addRow(row);
        }
    }
    
    private void removeSelectedItem() {
        int selectedRow = prItemsTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }
        
        String itemCode = (String) prItemsTableModel.getValueAt(selectedRow, 0);
        
        // Remove from lists
        Iterator<Item> iterator = selectedItems.iterator();
        while (iterator.hasNext()) {
            Item item = iterator.next();
            if (item.getItemCode().equals(itemCode)) {
                iterator.remove();
                break;
            }
        }
        
        quantityMap.remove(itemCode);
        prItemsTableModel.removeRow(selectedRow);
    }
    
    private void clearSelectedItems() {
        selectedItems.clear();
        quantityMap.clear();
        prItemsTableModel.setRowCount(0);
    }
    
    private void createPurchaseRequisition() {
        if (selectedItems.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please select at least one item for the requisition.", 
                "No Items Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Create dialog for PR details
        JDialog prDialog = new JDialog();
        prDialog.setTitle("Create Purchase Requisition");
        prDialog.setSize(400, 300);
        prDialog.setLocationRelativeTo(this);
        prDialog.setModal(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // PR ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("PR ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField prIdField = new JTextField(PurchaseRequisition.generatePRId());
        prIdField.setEditable(false);
        formPanel.add(prIdField, gbc);
        
        // PR Date
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("PR Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField prDateField = new JTextField(dateFormat.format(new Date()));
        prDateField.setEditable(false);
        formPanel.add(prDateField, gbc);
        
        // Required Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Required Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        
        // Create date picker for required date
        // For simplicity, we'll use a JSpinner instead of a full date picker
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, 7); // Default to 7 days from now
        SpinnerDateModel dateModel = new SpinnerDateModel(calendar.getTime(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner requiredDateSpinner = new JSpinner(dateModel);
        requiredDateSpinner.setEditor(new JSpinner.DateEditor(requiredDateSpinner, "yyyy-MM-dd"));
        formPanel.add(requiredDateSpinner, gbc);
        
        // Requested By
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Requested By:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        JTextField requestedByField = new JTextField(currentUser.getName());
        requestedByField.setEditable(false);
        formPanel.add(requestedByField, gbc);
        
        // Items Count
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Items Count:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField itemsCountField = new JTextField(String.valueOf(selectedItems.size()));
        itemsCountField.setEditable(false);
        formPanel.add(itemsCountField, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> prDialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton submitButton = new JButton("Submit PR");
        submitButton.setBackground(new Color(0, 102, 204));
        submitButton.setForeground(Color.WHITE);
        submitButton.addActionListener(e -> {
            Date requiredDate = (Date) requiredDateSpinner.getValue();
            savePurchaseRequisition(prIdField.getText(), requiredDate);
            prDialog.dispose();
        });
        buttonPanel.add(submitButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        prDialog.add(mainPanel);
        prDialog.setVisible(true);
    }
    
    private void savePurchaseRequisition(String prId, Date requiredDate) {
        try {
            // Create PR object
            PurchaseRequisition pr = new PurchaseRequisition(
                prId,
                new Date(),
                requiredDate,
                "PENDING",
                currentUser.getUserId()
            );
            
            // Add items to PR
            for (Item item : selectedItems) {
                String itemCode = item.getItemCode();
                int quantity = quantityMap.getOrDefault(itemCode, 1);
                String supplierId = item.getSupplierId();
                
                PurchaseRequisitionItem prItem = new PurchaseRequisitionItem(
                    prId,
                    itemCode,
                    quantity,
                    supplierId
                );
                
                pr.addItem(prItem);
            }
            
            // Save PR to file
            if (pr.savePR()) {
                JOptionPane.showMessageDialog(this, 
                    "Purchase Requisition created successfully.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Clear selected items
                clearSelectedItems();
                
                // Switch to view PR tab
                tabbedPane.setSelectedIndex(1);
                loadPurchaseRequisitions();
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Failed to create Purchase Requisition. Please try again.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, 
                "An error occurred: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private JPanel viewPRPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel with search and filter
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Search PR: "));
        JTextField searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterPurchaseRequisitions(searchField.getText());
            }
        });
        filterPanel.add(searchField);
        
        // Status filter
        filterPanel.add(new JLabel("Status: "));
        String[] statuses = {"All", "PENDING", "APPROVED", "REJECTED"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.addActionListener(e -> filterPurchaseRequisitionsByStatus((String) statusComboBox.getSelectedItem()));
        filterPanel.add(statusComboBox);
        
        topPanel.add(filterPanel, BorderLayout.WEST);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        viewPRButton = new JButton("View Details");
        viewPRButton.setBackground(new Color(0, 102, 204));
        viewPRButton.setForeground(Color.WHITE);
        viewPRButton.setOpaque(true);
        viewPRButton.setBorderPainted(false);
        viewPRButton.setPreferredSize(new Dimension(120, 35));
        viewPRButton.setMinimumSize(new Dimension(120, 35));
        viewPRButton.setFont(new Font("Arial", Font.BOLD, 12));
        viewPRButton.addActionListener(e -> viewPRDetails());
        actionPanel.add(viewPRButton);
        
        topPanel.add(actionPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // PR table
        createPRTable();
        JScrollPane scrollPane = new JScrollPane(prTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load PRs
        loadPurchaseRequisitions();
        
        return panel;
    }
    
    private void createPRTable() {
        // Define table columns
        String[] columns = {"PR ID", "PR Date", "Required Date", "Status", "Requested By", "Items Count"};
        
        // Create table model
        prTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        prTable = new JTable(prTableModel);
        prTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prTable.setRowHeight(25);
        
        // Add double-click listener to view PR details
        prTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewPRDetails();
                }
            }
        });
    }
    
    private void loadPurchaseRequisitions() {
        // Clear table
        prTableModel.setRowCount(0);
        
        // Load PRs
        List<PurchaseRequisition> prs = PurchaseRequisition.getAllPRs();
        
        for (PurchaseRequisition pr : prs) {
            User requestedBy = null;
            List<User> users = User.getAllUsers();
            for (User user : users) {
                if (user.getUserId().equals(pr.getRequestedBy())) {
                    requestedBy = user;
                    break;
                }
            }
            
            String requestedByName = (requestedBy != null) ? requestedBy.getName() : pr.getRequestedBy();
            
            Object[] row = {
                pr.getPrId(),
                dateFormat.format(pr.getPrDate()),
                dateFormat.format(pr.getRequiredDate()),
                pr.getStatus(),
                requestedByName,
                pr.getItems().size()
            };
            
            prTableModel.addRow(row);
        }
    }
    
    private void filterPurchaseRequisitions(String searchText) {
        // Apply current status filter too
        JComboBox<String> statusComboBox = null;
        
        // Find the status combo box in the component hierarchy 
        Component topPanel = ((JPanel) tabbedPane.getComponentAt(1)).getComponent(0);
        if (topPanel instanceof JPanel) {
            Component[] panelComponents = ((JPanel) topPanel).getComponents();
            for (Component component : panelComponents) {
                if (component instanceof JPanel) {
                    Component[] innerComponents = ((JPanel) component).getComponents();
                    for (Component innerComponent : innerComponents) {
                        if (innerComponent instanceof JComboBox) {
                            statusComboBox = (JComboBox<String>) innerComponent;
                            break;
                        }
                    }
                }
                if (statusComboBox != null) break;
            }
        }
        
        String statusFilter = (statusComboBox != null) ? (String) statusComboBox.getSelectedItem() : "All";
        
        // Clear table
        prTableModel.setRowCount(0);
        
        // Load and filter PRs
        List<PurchaseRequisition> prs = PurchaseRequisition.getAllPRs();
        String searchLower = searchText.toLowerCase();
        
        for (PurchaseRequisition pr : prs) {
            if ((pr.getPrId().toLowerCase().contains(searchLower)) &&
                (statusFilter.equals("All") || pr.getStatus().equals(statusFilter))) {
                
                User requestedBy = null;
                List<User> users = User.getAllUsers();
                for (User user : users) {
                    if (user.getUserId().equals(pr.getRequestedBy())) {
                        requestedBy = user;
                        break;
                    }
                }
                
                String requestedByName = (requestedBy != null) ? requestedBy.getName() : pr.getRequestedBy();
                
                Object[] row = {
                    pr.getPrId(),
                    dateFormat.format(pr.getPrDate()),
                    dateFormat.format(pr.getRequiredDate()),
                    pr.getStatus(),
                    requestedByName,
                    pr.getItems().size()
                };
                
                prTableModel.addRow(row);
            }
        }
    }
    
    private void filterPurchaseRequisitionsByStatus(String status) {
        // Apply current text filter too
        JTextField searchField = null;
        
        // Find the search field in the component hierarchy
        Component topPanel = ((JPanel) tabbedPane.getComponentAt(1)).getComponent(0);
        if (topPanel instanceof JPanel) {
            Component[] panelComponents = ((JPanel) topPanel).getComponents();
            for (Component component : panelComponents) {
                if (component instanceof JPanel) {
                    Component[] innerComponents = ((JPanel) component).getComponents();
                    for (Component innerComponent : innerComponents) {
                        if (innerComponent instanceof JTextField) {
                            searchField = (JTextField) innerComponent;
                            break;
                        }
                    }
                }
                if (searchField != null) break;
            }
        }
        
        String searchText = (searchField != null) ? searchField.getText() : "";
        
        // Clear table
        prTableModel.setRowCount(0);
        
        // Load and filter PRs
        List<PurchaseRequisition> prs = PurchaseRequisition.getAllPRs();
        String searchLower = searchText.toLowerCase();
        
        for (PurchaseRequisition pr : prs) {
            if ((pr.getPrId().toLowerCase().contains(searchLower)) &&
                (status.equals("All") || pr.getStatus().equals(status))) {
                
                User requestedBy = null;
                List<User> users = User.getAllUsers();
                for (User user : users) {
                    if (user.getUserId().equals(pr.getRequestedBy())) {
                        requestedBy = user;
                        break;
                    }
                }
                
                String requestedByName = (requestedBy != null) ? requestedBy.getName() : pr.getRequestedBy();
                
                Object[] row = {
                    pr.getPrId(),
                    dateFormat.format(pr.getPrDate()),
                    dateFormat.format(pr.getRequiredDate()),
                    pr.getStatus(),
                    requestedByName,
                    pr.getItems().size()
                };
                
                prTableModel.addRow(row);
            }
        }
    }
    
    private void viewPRDetails() {
        int selectedRow = prTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a PR to view details.", 
                "No PR Selected", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String prId = (String) prTableModel.getValueAt(selectedRow, 0);
        PurchaseRequisition pr = PurchaseRequisition.getPRById(prId);
        
        if (pr == null) {
            JOptionPane.showMessageDialog(this, 
                "PR not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create dialog for PR details
        JDialog prDialog = new JDialog();
        prDialog.setTitle("Purchase Requisition Details - " + prId);
        prDialog.setSize(600, 500);
        prDialog.setLocationRelativeTo(this);
        prDialog.setModal(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // PR info panel
        JPanel infoPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("PR Information"));
        
        infoPanel.add(new JLabel("PR ID:"));
        infoPanel.add(new JLabel(pr.getPrId()));
        
        infoPanel.add(new JLabel("PR Date:"));
        infoPanel.add(new JLabel(dateFormat.format(pr.getPrDate())));
        
        infoPanel.add(new JLabel("Required Date:"));
        infoPanel.add(new JLabel(dateFormat.format(pr.getRequiredDate())));
        
        infoPanel.add(new JLabel("Status:"));
        JLabel statusLabel = new JLabel(pr.getStatus());
        if (pr.getStatus().equals("APPROVED")) {
            statusLabel.setForeground(new Color(0, 128, 0));
        } else if (pr.getStatus().equals("REJECTED")) {
            statusLabel.setForeground(Color.RED);
        } else {
            statusLabel.setForeground(new Color(255, 165, 0));
        }
        infoPanel.add(statusLabel);
        
        User requestedBy = null;
        List<User> users = User.getAllUsers();
        for (User user : users) {
            if (user.getUserId().equals(pr.getRequestedBy())) {
                requestedBy = user;
                break;
            }
        }
        
        infoPanel.add(new JLabel("Requested By:"));
        infoPanel.add(new JLabel(requestedBy != null ? requestedBy.getName() : pr.getRequestedBy()));
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // PR items table
        String[] columns = {"Item Code", "Item Name", "Quantity", "Supplier"};
        DefaultTableModel detailsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable detailsTable = new JTable(detailsTableModel);
        detailsTable.setRowHeight(25);
        
        // Load PR items
        List<PurchaseRequisitionItem> items = pr.getItems();
        for (PurchaseRequisitionItem prItem : items) {
            Item item = Item.getItemByCode(prItem.getItemCode());
            String itemName = (item != null) ? item.getItemName() : "[Item not found]";
            
            Supplier supplier = Supplier.getSupplierById(prItem.getSupplierId());
            String supplierName = (supplier != null) ? supplier.getSupplierName() : "[Supplier not found]";
            
            Object[] row = {
                prItem.getItemCode(),
                itemName,
                prItem.getQuantity(),
                supplierName
            };
            
            detailsTableModel.addRow(row);
        }
        
        JScrollPane scrollPane = new JScrollPane(detailsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("PR Items"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> prDialog.dispose());
        buttonPanel.add(closeButton);
        
        // If PR is still pending and the user is the one who created it, allow cancellation
        if (pr.getStatus().equals("PENDING") && pr.getRequestedBy().equals(currentUser.getUserId())) {
            JButton cancelPRButton = new JButton("Cancel PR");
            cancelPRButton.setBackground(Color.RED);
            cancelPRButton.setForeground(Color.WHITE);
            cancelPRButton.addActionListener(e -> {
                int confirm = JOptionPane.showConfirmDialog(
                    prDialog,
                    "Are you sure you want to cancel this PR?",
                    "Confirm Cancellation",
                    JOptionPane.YES_NO_OPTION,
                    JOptionPane.WARNING_MESSAGE
                );
                
                if (confirm == JOptionPane.YES_OPTION) {
                    pr.updateStatus("CANCELLED");
                    JOptionPane.showMessageDialog(prDialog, 
                        "PR cancelled successfully.", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    prDialog.dispose();
                    loadPurchaseRequisitions();
                }
            });
            buttonPanel.add(cancelPRButton);
        }
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        prDialog.add(mainPanel);
        prDialog.setVisible(true);
    }
} 