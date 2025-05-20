package view.panels;

import model.*;
import model.PurchaseOrder.PurchaseOrderItem;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class PurchaseOrderPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTabbedPane tabbedPane;
    private JTable poTable;
    private DefaultTableModel poTableModel;
    private JButton createPOButton;
    private JButton viewPOButton;
    private JButton approvePOButton;
    private JButton rejectPOButton;
    private JButton receiveInventoryButton;
    
    // DateFormat
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    
    public PurchaseOrderPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Purchase Order Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        
        // Create PO tab
        JPanel createPOPanel = createPOPanel();
        tabbedPane.addTab("Create Purchase Order", createPOPanel);
        
        // View POs tab
        JPanel viewPOPanel = viewPOPanel();
        tabbedPane.addTab("View Purchase Orders", viewPOPanel);
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createPOPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel for instructions
        JPanel topPanel = new JPanel(new BorderLayout());
        JLabel instructionsLabel = new JLabel("Select pending purchase requisitions to create a Purchase Order");
        instructionsLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        topPanel.add(instructionsLabel, BorderLayout.WEST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // PR Table
        JPanel prPanel = new JPanel(new BorderLayout());
        prPanel.setBorder(BorderFactory.createTitledBorder("Pending Purchase Requisitions"));
        
        // Create PR table
        String[] prColumns = {"PR ID", "PR Date", "Required Date", "Requested By", "Items Count"};
        DefaultTableModel prTableModel = new DefaultTableModel(prColumns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable prTable = new JTable(prTableModel);
        prTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        prTable.setRowHeight(25);
        
        JScrollPane prScrollPane = new JScrollPane(prTable);
        prPanel.add(prScrollPane, BorderLayout.CENTER);
        
        // Load pending PRs
        List<PurchaseRequisition> pendingPRs = PurchaseRequisition.getPendingPRs();
        for (PurchaseRequisition pr : pendingPRs) {
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
                requestedByName,
                pr.getItems().size()
            };
            
            prTableModel.addRow(row);
        }
        
        // Button to create PO
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        createPOButton = new JButton("Create Purchase Order");
        createPOButton.setBackground(new Color(0, 102, 204));
        createPOButton.setForeground(Color.WHITE);
        createPOButton.setOpaque(true);
        createPOButton.setBorderPainted(false);
        createPOButton.setPreferredSize(new Dimension(180, 35));
        createPOButton.setFont(new Font("Arial", Font.BOLD, 12));
        createPOButton.addActionListener(e -> createPurchaseOrder(prTable));
        buttonPanel.add(createPOButton);
        
        prPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        panel.add(prPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel viewPOPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Top panel with search and filter
        JPanel topPanel = new JPanel(new BorderLayout());
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Search PO: "));
        JTextField searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterPurchaseOrders(searchField.getText());
            }
        });
        filterPanel.add(searchField);
        
        // Status filter
        filterPanel.add(new JLabel("Status: "));
        String[] statuses = {"All", "PENDING", "APPROVED", "REJECTED", "RECEIVED"};
        JComboBox<String> statusComboBox = new JComboBox<>(statuses);
        statusComboBox.addActionListener(e -> filterPurchaseOrdersByStatus((String) statusComboBox.getSelectedItem()));
        filterPanel.add(statusComboBox);
        
        topPanel.add(filterPanel, BorderLayout.WEST);
        
        // Action buttons
        JPanel actionPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        viewPOButton = new JButton("View Details");
        viewPOButton.setBackground(new Color(0, 102, 204));
        viewPOButton.setForeground(Color.WHITE);
        viewPOButton.setOpaque(true);
        viewPOButton.setBorderPainted(false);
        viewPOButton.setPreferredSize(new Dimension(120, 35));
        viewPOButton.setFont(new Font("Arial", Font.BOLD, 12));
        viewPOButton.addActionListener(e -> viewPODetails());
        actionPanel.add(viewPOButton);
        
        // Only show approve/reject buttons for Finance Manager role
        if (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("FINANCE_MANAGER")) {
            approvePOButton = new JButton("Approve");
            approvePOButton.setBackground(new Color(46, 184, 46));
            approvePOButton.setForeground(Color.WHITE);
            approvePOButton.setOpaque(true);
            approvePOButton.setBorderPainted(false);
            approvePOButton.setPreferredSize(new Dimension(100, 35));
            approvePOButton.setFont(new Font("Arial", Font.BOLD, 12));
            approvePOButton.addActionListener(e -> approvePO());
            actionPanel.add(approvePOButton);
            
            rejectPOButton = new JButton("Reject");
            rejectPOButton.setBackground(new Color(255, 51, 51));
            rejectPOButton.setForeground(Color.WHITE);
            rejectPOButton.setOpaque(true);
            rejectPOButton.setBorderPainted(false);
            rejectPOButton.setPreferredSize(new Dimension(100, 35));
            rejectPOButton.setFont(new Font("Arial", Font.BOLD, 12));
            rejectPOButton.addActionListener(e -> rejectPO());
            actionPanel.add(rejectPOButton);
        }
        
        // Only show receive inventory button for Inventory Manager role
        if (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("INVENTORY_MANAGER")) {
            receiveInventoryButton = new JButton("Receive Inventory");
            receiveInventoryButton.setBackground(new Color(255, 165, 0));
            receiveInventoryButton.setForeground(Color.WHITE);
            receiveInventoryButton.setOpaque(true);
            receiveInventoryButton.setBorderPainted(false);
            receiveInventoryButton.setPreferredSize(new Dimension(150, 35));
            receiveInventoryButton.setFont(new Font("Arial", Font.BOLD, 12));
            receiveInventoryButton.addActionListener(e -> receiveInventory());
            actionPanel.add(receiveInventoryButton);
        }
        
        topPanel.add(actionPanel, BorderLayout.EAST);
        
        panel.add(topPanel, BorderLayout.NORTH);
        
        // PO table
        createPOTable();
        JScrollPane scrollPane = new JScrollPane(poTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        // Load POs
        loadPurchaseOrders();
        
        return panel;
    }
    
    private void createPOTable() {
        // Define table columns
        String[] columns = {"PO ID", "PR ID", "PO Date", "Delivery Date", "Status", "Created By", "Total Amount"};
        
        // Create table model
        poTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Create table
        poTable = new JTable(poTableModel);
        poTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        poTable.setRowHeight(25);
        
        // Add double-click listener to view PO details
        poTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    viewPODetails();
                }
            }
        });
    }
    
    private void loadPurchaseOrders() {
        // Clear table
        poTableModel.setRowCount(0);
        
        // Load POs
        List<PurchaseOrder> pos = PurchaseOrder.getAllPOs();
        
        for (PurchaseOrder po : pos) {
            User createdBy = null;
            List<User> users = User.getAllUsers();
            for (User user : users) {
                if (user.getUserId().equals(po.getCreatedBy())) {
                    createdBy = user;
                    break;
                }
            }
            
            String createdByName = (createdBy != null) ? createdBy.getName() : po.getCreatedBy();
            
            Object[] row = {
                po.getPoId(),
                po.getPrId(),
                dateFormat.format(po.getPoDate()),
                dateFormat.format(po.getDeliveryDate()),
                po.getStatus(),
                createdByName,
                String.format("RM %.2f", po.getTotalAmount())
            };
            
            poTableModel.addRow(row);
        }
    }
    
    private void filterPurchaseOrders(String searchText) {
        // Clear table
        poTableModel.setRowCount(0);
        
        // Load and filter POs
        List<PurchaseOrder> pos = PurchaseOrder.getAllPOs();
        String searchLower = searchText.toLowerCase();
        
        for (PurchaseOrder po : pos) {
            if (po.getPoId().toLowerCase().contains(searchLower) || 
                po.getPrId().toLowerCase().contains(searchLower)) {
                
                User createdBy = null;
                List<User> users = User.getAllUsers();
                for (User user : users) {
                    if (user.getUserId().equals(po.getCreatedBy())) {
                        createdBy = user;
                        break;
                    }
                }
                
                String createdByName = (createdBy != null) ? createdBy.getName() : po.getCreatedBy();
                
                Object[] row = {
                    po.getPoId(),
                    po.getPrId(),
                    dateFormat.format(po.getPoDate()),
                    dateFormat.format(po.getDeliveryDate()),
                    po.getStatus(),
                    createdByName,
                    String.format("RM %.2f", po.getTotalAmount())
                };
                
                poTableModel.addRow(row);
            }
        }
    }
    
    private void filterPurchaseOrdersByStatus(String status) {
        // Clear table
        poTableModel.setRowCount(0);
        
        // Load and filter POs
        List<PurchaseOrder> pos = PurchaseOrder.getAllPOs();
        
        for (PurchaseOrder po : pos) {
            if (status.equals("All") || po.getStatus().equals(status)) {
                User createdBy = null;
                List<User> users = User.getAllUsers();
                for (User user : users) {
                    if (user.getUserId().equals(po.getCreatedBy())) {
                        createdBy = user;
                        break;
                    }
                }
                
                String createdByName = (createdBy != null) ? createdBy.getName() : po.getCreatedBy();
                
                Object[] row = {
                    po.getPoId(),
                    po.getPrId(),
                    dateFormat.format(po.getPoDate()),
                    dateFormat.format(po.getDeliveryDate()),
                    po.getStatus(),
                    createdByName,
                    String.format("RM %.2f", po.getTotalAmount())
                };
                
                poTableModel.addRow(row);
            }
        }
    }
    
    private void createPurchaseOrder(JTable prTable) {
        int selectedRow = prTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a purchase requisition to create a PO.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String prId = (String) prTable.getValueAt(selectedRow, 0);
        PurchaseRequisition pr = PurchaseRequisition.getPRById(prId);
        
        if (pr == null) {
            JOptionPane.showMessageDialog(this, 
                "PR not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create dialog for PO details
        JDialog poDialog = new JDialog();
        poDialog.setTitle("Create Purchase Order");
        poDialog.setSize(500, 600);
        poDialog.setLocationRelativeTo(this);
        poDialog.setModal(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // PO ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("PO ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        JTextField poIdField = new JTextField(PurchaseOrder.generatePOId());
        poIdField.setEditable(false);
        formPanel.add(poIdField, gbc);
        
        // PR ID
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("PR ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        JTextField prIdField = new JTextField(pr.getPrId());
        prIdField.setEditable(false);
        formPanel.add(prIdField, gbc);
        
        // PO Date
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("PO Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        JTextField poDateField = new JTextField(dateFormat.format(new Date()));
        poDateField.setEditable(false);
        formPanel.add(poDateField, gbc);
        
        // Delivery Date
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Delivery Date:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        
        // Create date picker for delivery date
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(pr.getRequiredDate());
        SpinnerDateModel dateModel = new SpinnerDateModel(calendar.getTime(), null, null, Calendar.DAY_OF_MONTH);
        JSpinner deliveryDateSpinner = new JSpinner(dateModel);
        deliveryDateSpinner.setEditor(new JSpinner.DateEditor(deliveryDateSpinner, "yyyy-MM-dd"));
        formPanel.add(deliveryDateSpinner, gbc);
        
        // Created By
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Created By:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        JTextField createdByField = new JTextField(currentUser.getName());
        createdByField.setEditable(false);
        formPanel.add(createdByField, gbc);
        
        mainPanel.add(formPanel, BorderLayout.NORTH);
        
        // Items table
        String[] columns = {"Item Code", "Item Name", "Quantity", "Unit Price", "Total"};
        DefaultTableModel itemsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3; // Only unit price is editable
            }
        };
        
        JTable itemsTable = new JTable(itemsTableModel);
        itemsTable.setRowHeight(25);
        
        // Add table model listener to update totals when unit price changes
        itemsTable.getModel().addTableModelListener(e -> {
            if (e.getType() == javax.swing.event.TableModelEvent.UPDATE && e.getColumn() == 3) {
                int row = e.getFirstRow();
                try {
                    double unitPrice = Double.parseDouble(itemsTable.getValueAt(row, 3).toString());
                    int quantity = Integer.parseInt(itemsTable.getValueAt(row, 2).toString());
                    double total = unitPrice * quantity;
                    itemsTable.setValueAt(String.format("%.2f", total), row, 4);
                    
                    // Update total at the bottom
                    updateTotal(itemsTable);
                } catch (NumberFormatException ex) {
                    itemsTable.setValueAt("0.00", row, 4);
                }
            }
        });
        
        // Load PR items
        List<PurchaseRequisition.PurchaseRequisitionItem> prItems = pr.getItems();
        for (PurchaseRequisition.PurchaseRequisitionItem prItem : prItems) {
            Item item = Item.getItemByCode(prItem.getItemCode());
            if (item != null) {
                String itemName = item.getItemName();
                int quantity = prItem.getQuantity();
                double unitPrice = item.getUnitPrice();
                double total = unitPrice * quantity;
                
                Object[] row = {
                    prItem.getItemCode(),
                    itemName,
                    quantity,
                    String.format("%.2f", unitPrice),
                    String.format("%.2f", total)
                };
                
                itemsTableModel.addRow(row);
            }
        }
        
        JScrollPane itemsScrollPane = new JScrollPane(itemsTable);
        itemsScrollPane.setBorder(BorderFactory.createTitledBorder("Purchase Order Items"));
        mainPanel.add(itemsScrollPane, BorderLayout.CENTER);
        
        // Total panel
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.add(new JLabel("Total Amount: RM"));
        
        JTextField totalField = new JTextField(10);
        totalField.setEditable(false);
        totalField.setFont(new Font("Arial", Font.BOLD, 14));
        
        // Calculate initial total
        double total = 0;
        for (int i = 0; i < itemsTable.getRowCount(); i++) {
            try {
                total += Double.parseDouble(itemsTable.getValueAt(i, 4).toString());
            } catch (NumberFormatException ex) {
                // Skip
            }
        }
        totalField.setText(String.format("%.2f", total));
        
        totalPanel.add(totalField);
        mainPanel.add(totalPanel, BorderLayout.SOUTH);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(e -> poDialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton createButton = new JButton("Create PO");
        createButton.setBackground(new Color(0, 102, 204));
        createButton.setForeground(Color.WHITE);
        createButton.addActionListener(e -> {
            savePurchaseOrder(
                poIdField.getText(),
                prIdField.getText(),
                (Date) deliveryDateSpinner.getValue(),
                itemsTable,
                Double.parseDouble(totalField.getText()),
                poDialog
            );
        });
        buttonPanel.add(createButton);
        
        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.add(totalPanel, BorderLayout.CENTER);
        southPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        mainPanel.add(southPanel, BorderLayout.SOUTH);
        
        poDialog.add(mainPanel);
        poDialog.setVisible(true);
    }
    
    private void updateTotal(JTable itemsTable) {
        double total = 0;
        for (int i = 0; i < itemsTable.getRowCount(); i++) {
            try {
                total += Double.parseDouble(itemsTable.getValueAt(i, 4).toString());
            } catch (NumberFormatException ex) {
                // Skip
            }
        }
        
        // Find the total field
        Container container = itemsTable.getParent();
        while (!(container instanceof JDialog)) {
            container = container.getParent();
        }
        
        // Look for the total field in all components
        Component[] components = ((JDialog) container).getContentPane().getComponents();
        for (Component component : components) {
            if (component instanceof JPanel) {
                Component[] panelComponents = ((JPanel) component).getComponents();
                for (Component panelComponent : panelComponents) {
                    if (panelComponent instanceof JPanel) {
                        Component[] subPanelComponents = ((JPanel) panelComponent).getComponents();
                        for (Component subPanelComponent : subPanelComponents) {
                            if (subPanelComponent instanceof JTextField) {
                                JTextField field = (JTextField) subPanelComponent;
                                try {
                                    Double.parseDouble(field.getText());
                                    field.setText(String.format("%.2f", total));
                                    return;
                                } catch (NumberFormatException ex) {
                                    // Not the total field
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void savePurchaseOrder(String poId, String prId, Date deliveryDate, JTable itemsTable, double totalAmount, JDialog dialog) {
        try {
            // Create PO object
            PurchaseOrder po = new PurchaseOrder(
                poId,
                prId,
                new Date(),
                deliveryDate,
                "PENDING",
                currentUser.getUserId(),
                totalAmount
            );
            
            // Add items to PO
            for (int i = 0; i < itemsTable.getRowCount(); i++) {
                String itemCode = itemsTable.getValueAt(i, 0).toString();
                int quantity = Integer.parseInt(itemsTable.getValueAt(i, 2).toString());
                double unitPrice = Double.parseDouble(itemsTable.getValueAt(i, 3).toString());
                
                // Get supplier ID from PR items
                PurchaseRequisition pr = PurchaseRequisition.getPRById(prId);
                String supplierId = null;
                for (PurchaseRequisition.PurchaseRequisitionItem prItem : pr.getItems()) {
                    if (prItem.getItemCode().equals(itemCode)) {
                        supplierId = prItem.getSupplierId();
                        break;
                    }
                }
                
                if (supplierId == null) {
                    supplierId = Item.getItemByCode(itemCode).getSupplierId();
                }
                
                PurchaseOrderItem poItem = new PurchaseOrderItem(
                    poId,
                    itemCode,
                    quantity,
                    unitPrice,
                    supplierId
                );
                
                po.addItem(poItem);
            }
            
            // Save PO to file
            if (po.savePO()) {
                JOptionPane.showMessageDialog(dialog, 
                    "Purchase Order created successfully.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                
                dialog.dispose();
                
                // Refresh views
                tabbedPane.setSelectedIndex(1);
                loadPurchaseOrders();
            } else {
                JOptionPane.showMessageDialog(dialog, 
                    "Failed to create Purchase Order. Please try again.", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(dialog, 
                "An error occurred: " + e.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    
    private void viewPODetails() {
        int selectedRow = poTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a PO to view details.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String poId = (String) poTableModel.getValueAt(selectedRow, 0);
        PurchaseOrder po = PurchaseOrder.getPOById(poId);
        
        if (po == null) {
            JOptionPane.showMessageDialog(this, 
                "PO not found.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Create dialog for PO details
        JDialog poDialog = new JDialog();
        poDialog.setTitle("Purchase Order Details - " + poId);
        poDialog.setSize(600, 500);
        poDialog.setLocationRelativeTo(this);
        poDialog.setModal(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // PO info panel
        JPanel infoPanel = new JPanel(new GridLayout(7, 2, 10, 10));
        infoPanel.setBorder(BorderFactory.createTitledBorder("PO Information"));
        
        infoPanel.add(new JLabel("PO ID:"));
        infoPanel.add(new JLabel(po.getPoId()));
        
        infoPanel.add(new JLabel("PR ID:"));
        infoPanel.add(new JLabel(po.getPrId()));
        
        infoPanel.add(new JLabel("PO Date:"));
        infoPanel.add(new JLabel(dateFormat.format(po.getPoDate())));
        
        infoPanel.add(new JLabel("Delivery Date:"));
        infoPanel.add(new JLabel(dateFormat.format(po.getDeliveryDate())));
        
        infoPanel.add(new JLabel("Status:"));
        JLabel statusLabel = new JLabel(po.getStatus());
        if (po.getStatus().equals("APPROVED")) {
            statusLabel.setForeground(new Color(0, 128, 0));
        } else if (po.getStatus().equals("REJECTED")) {
            statusLabel.setForeground(Color.RED);
        } else if (po.getStatus().equals("RECEIVED")) {
            statusLabel.setForeground(new Color(0, 0, 128));
        } else {
            statusLabel.setForeground(new Color(255, 165, 0));
        }
        infoPanel.add(statusLabel);
        
        User createdBy = null;
        List<User> users = User.getAllUsers();
        for (User user : users) {
            if (user.getUserId().equals(po.getCreatedBy())) {
                createdBy = user;
                break;
            }
        }
        
        infoPanel.add(new JLabel("Created By:"));
        infoPanel.add(new JLabel(createdBy != null ? createdBy.getName() : po.getCreatedBy()));
        
        infoPanel.add(new JLabel("Total Amount:"));
        infoPanel.add(new JLabel(String.format("RM %.2f", po.getTotalAmount())));
        
        mainPanel.add(infoPanel, BorderLayout.NORTH);
        
        // PO items table
        String[] columns = {"Item Code", "Item Name", "Quantity", "Unit Price", "Total"};
        DefaultTableModel detailsTableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable detailsTable = new JTable(detailsTableModel);
        detailsTable.setRowHeight(25);
        
        // Load PO items
        List<PurchaseOrderItem> items = po.getItems();
        for (PurchaseOrderItem poItem : items) {
            Item item = Item.getItemByCode(poItem.getItemCode());
            String itemName = (item != null) ? item.getItemName() : "[Item not found]";
            
            double total = poItem.getQuantity() * poItem.getUnitPrice();
            
            Object[] row = {
                poItem.getItemCode(),
                itemName,
                poItem.getQuantity(),
                String.format("RM %.2f", poItem.getUnitPrice()),
                String.format("RM %.2f", total)
            };
            
            detailsTableModel.addRow(row);
        }
        
        JScrollPane scrollPane = new JScrollPane(detailsTable);
        scrollPane.setBorder(BorderFactory.createTitledBorder("PO Items"));
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> poDialog.dispose());
        buttonPanel.add(closeButton);
        
        // Show action buttons based on role and PO status
        if (po.getStatus().equals("PENDING")) {
            // Finance manager can approve/reject
            if (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("FINANCE_MANAGER")) {
                JButton approveButton = new JButton("Approve PO");
                approveButton.setBackground(new Color(46, 184, 46));
                approveButton.setForeground(Color.WHITE);
                approveButton.addActionListener(e -> {
                    po.updateStatus("APPROVED");
                    JOptionPane.showMessageDialog(poDialog, 
                        "PO approved successfully.", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    poDialog.dispose();
                    loadPurchaseOrders();
                });
                buttonPanel.add(approveButton);
                
                JButton rejectButton = new JButton("Reject PO");
                rejectButton.setBackground(Color.RED);
                rejectButton.setForeground(Color.WHITE);
                rejectButton.addActionListener(e -> {
                    po.updateStatus("REJECTED");
                    JOptionPane.showMessageDialog(poDialog, 
                        "PO rejected.", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    poDialog.dispose();
                    loadPurchaseOrders();
                });
                buttonPanel.add(rejectButton);
            }
        } else if (po.getStatus().equals("APPROVED")) {
            // Inventory manager can receive
            if (currentUser.getRole().equals("ADMIN") || currentUser.getRole().equals("INVENTORY_MANAGER")) {
                JButton receiveButton = new JButton("Receive Inventory");
                receiveButton.setBackground(new Color(0, 102, 204));
                receiveButton.setForeground(Color.WHITE);
                receiveButton.addActionListener(e -> {
                    po.receivePO();
                    JOptionPane.showMessageDialog(poDialog, 
                        "Inventory received successfully.", 
                        "Success", 
                        JOptionPane.INFORMATION_MESSAGE);
                    poDialog.dispose();
                    loadPurchaseOrders();
                });
                buttonPanel.add(receiveButton);
            }
        }
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        poDialog.add(mainPanel);
        poDialog.setVisible(true);
    }
    
    private void approvePO() {
        int selectedRow = poTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a PO to approve.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String poId = (String) poTableModel.getValueAt(selectedRow, 0);
        String status = (String) poTableModel.getValueAt(selectedRow, 4);
        
        if (!status.equals("PENDING")) {
            JOptionPane.showMessageDialog(this, 
                "Only pending POs can be approved.", 
                "Invalid Operation", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        PurchaseOrder po = PurchaseOrder.getPOById(poId);
        if (po != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to approve this Purchase Order?",
                "Confirm Approval",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                po.updateStatus("APPROVED");
                JOptionPane.showMessageDialog(this, 
                    "Purchase Order approved successfully.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadPurchaseOrders();
            }
        }
    }
    
    private void rejectPO() {
        int selectedRow = poTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a PO to reject.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String poId = (String) poTableModel.getValueAt(selectedRow, 0);
        String status = (String) poTableModel.getValueAt(selectedRow, 4);
        
        if (!status.equals("PENDING")) {
            JOptionPane.showMessageDialog(this, 
                "Only pending POs can be rejected.", 
                "Invalid Operation", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        PurchaseOrder po = PurchaseOrder.getPOById(poId);
        if (po != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to reject this Purchase Order?",
                "Confirm Rejection",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                po.updateStatus("REJECTED");
                JOptionPane.showMessageDialog(this, 
                    "Purchase Order rejected.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadPurchaseOrders();
            }
        }
    }
    
    private void receiveInventory() {
        int selectedRow = poTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select a PO to receive inventory.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String poId = (String) poTableModel.getValueAt(selectedRow, 0);
        String status = (String) poTableModel.getValueAt(selectedRow, 4);
        
        if (!status.equals("APPROVED")) {
            JOptionPane.showMessageDialog(this, 
                "Only approved POs can be received.", 
                "Invalid Operation", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        PurchaseOrder po = PurchaseOrder.getPOById(poId);
        if (po != null) {
            int confirm = JOptionPane.showConfirmDialog(
                this,
                "Are you sure you want to mark this Purchase Order as received?\nThis will update the inventory.",
                "Confirm Receive",
                JOptionPane.YES_NO_OPTION,
                JOptionPane.QUESTION_MESSAGE
            );
            
            if (confirm == JOptionPane.YES_OPTION) {
                po.receivePO();
                JOptionPane.showMessageDialog(this, 
                    "Inventory received successfully.", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);
                loadPurchaseOrders();
            }
        }
    }
} 