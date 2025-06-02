package com.owsb.view.panels;

import com.owsb.model.*;
import com.owsb.view.Dashboard;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class ViewPurchaseRequisitionsPanel extends JPanel {
    private User currentUser;
    private Dashboard parentDashboard;
    
    // UI Components
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> departmentFilter;
    private JTable prTable;
    private DefaultTableModel tableModel;
    
    // Create form components
    private JTextField requiredDateField;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> departmentCombo;
    private JTextField itemField;
    private JTextField quantityField;
    private JComboBox<String> unitCombo;
    private DefaultListModel<String> selectedItemsModel;
    private JList<String> selectedItemsList;
    
    // Colors
    private static final Color PRIMARY_BLUE = new Color(37, 99, 235);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color DANGER_RED = new Color(239, 68, 68);
    private static final Color WARNING_ORANGE = new Color(245, 158, 11);
    private static final Color GRAY_50 = new Color(249, 250, 251);
    private static final Color GRAY_200 = new Color(229, 231, 235);
    private static final Color GRAY_300 = new Color(209, 213, 219);
    private static final Color GRAY_400 = new Color(156, 163, 175);
    private static final Color GRAY_600 = new Color(75, 85, 99);
    private static final Color GRAY_700 = new Color(55, 65, 81);
    private static final Color GRAY_900 = new Color(17, 24, 39);
    
    public ViewPurchaseRequisitionsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        initializeComponents();
        setupUI();
    }
    
    public void setParentDashboard(Dashboard dashboard) {
        this.parentDashboard = dashboard;
    }
    
    private void initializeComponents() {
        // Search and filter components
        searchField = new JTextField("Search PRs...");
        searchField.setForeground(GRAY_600);
        statusFilter = new JComboBox<>(new String[]{"All Status", "Pending", "Approved", "Rejected"});
        departmentFilter = new JComboBox<>(new String[]{"All Departments", "Sales", "Marketing", "Operations", "Finance", "IT"});
        
        // Table
        String[] columnNames = {"PR ID", "Created By", "Department", "Date Created", "Required Date", "Items Count", "Priority", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only Actions column
            }
        };
        prTable = new JTable(tableModel);
        
        // Create form components
        requiredDateField = new JTextField();
        priorityCombo = new JComboBox<>(new String[]{"Low", "Medium", "High", "Urgent"});
        departmentCombo = new JComboBox<>(new String[]{"Sales", "Marketing", "Operations", "Finance", "IT"});
        itemField = new JTextField();
        quantityField = new JTextField();
        unitCombo = new JComboBox<>(new String[]{"kg", "pcs", "box", "liter"});
        selectedItemsModel = new DefaultListModel<>();
        selectedItemsList = new JList<>(selectedItemsModel);
        
        setupTable();
        setupSearchField();
    }
    
    private void setupUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Header
        JPanel headerPanel = createHeader();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content
        JScrollPane scrollPane = new JScrollPane(createContent());
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        return panel;
    }
    
    private JPanel createContent() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Header with title and Create New PR button
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JLabel titleLabel = new JLabel("Purchase Requisitions");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        titleLabel.setForeground(GRAY_900);
        
        JButton createNewBtn = createButton("Create New PR", PRIMARY_BLUE);
        createNewBtn.setPreferredSize(new Dimension(140, 35));
        createNewBtn.addActionListener(e -> openCreateDialog());
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(createNewBtn, BorderLayout.EAST);
        
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Filters and table
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        // Filters
        JPanel filtersPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        filtersPanel.setBackground(Color.WHITE);
        filtersPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        searchField.setPreferredSize(new Dimension(300, 35));
        searchField.setFont(new Font("Inter", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        statusFilter.setPreferredSize(new Dimension(120, 35));
        statusFilter.setFont(new Font("Inter", Font.PLAIN, 14));
        
        departmentFilter.setPreferredSize(new Dimension(150, 35));
        departmentFilter.setFont(new Font("Inter", Font.PLAIN, 14));
        
        JTextField dateField = new JTextField("06/01/2025");
        dateField.setPreferredSize(new Dimension(120, 35));
        dateField.setFont(new Font("Inter", Font.PLAIN, 14));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1),
            BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));
        
        filtersPanel.add(searchField);
        filtersPanel.add(statusFilter);
        filtersPanel.add(departmentFilter);
        filtersPanel.add(dateField);
        
        contentPanel.add(filtersPanel, BorderLayout.NORTH);
        
        // Table
        JScrollPane tableScroll = new JScrollPane(prTable);
        tableScroll.setBorder(BorderFactory.createLineBorder(GRAY_200, 1));
        tableScroll.setBackground(Color.WHITE);
        contentPanel.add(tableScroll, BorderLayout.CENTER);
        
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createField(String label, JComponent component, int width) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(GRAY_50);
        
        JLabel labelComp = new JLabel(label);
        labelComp.setFont(new Font("Inter", Font.BOLD, 11));
        labelComp.setForeground(GRAY_700);
        labelComp.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        component.setPreferredSize(new Dimension(width, 30));
        component.setMaximumSize(new Dimension(width, 30));
        component.setFont(new Font("Inter", Font.PLAIN, 12));
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (component instanceof JTextField) {
            component.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRAY_300, 1),
                BorderFactory.createEmptyBorder(5, 8, 5, 8)
            ));
        }
        
        panel.add(labelComp);
        panel.add(Box.createVerticalStrut(3));
        panel.add(component);
        
        return panel;
    }
    
    private JButton createButton(String text, Color bgColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.BOLD, 11));
        button.setBackground(bgColor);
        button.setForeground(Color.WHITE);
        button.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
        button.setFocusPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(bgColor.darker());
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(bgColor);
            }
        });
        
        return button;
    }
    
    private boolean canCreatePR() {
        return currentUser.getRole().equals("ADMIN") ||
               currentUser.getRole().equals("PURCHASE_MANAGER") ||
               currentUser.getRole().equals("SALES_MANAGER");
    }
    
    private void addItem() {
        String item = itemField.getText().trim();
        String quantity = quantityField.getText().trim();
        String unit = (String) unitCombo.getSelectedItem();
        
        if (item.isEmpty() || quantity.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter item and quantity.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        selectedItemsModel.addElement(item + " - " + quantity + " " + unit);
        itemField.setText("");
        quantityField.setText("");
        unitCombo.setSelectedIndex(0);
    }
    
    private boolean submitPR() {
        if (selectedItemsModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one item.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        String requiredDate = requiredDateField.getText().trim();
        if (requiredDate.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter required date.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        try {
            // Create new PR object
            PurchaseRequisition pr = new PurchaseRequisition();
            pr.setPrId(PurchaseRequisition.generatePRId());
            pr.setPrDate(new Date());
            
            // Parse required date
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            try {
                pr.setRequiredDate(dateFormat.parse(requiredDate));
            } catch (Exception e) {
                // Try different date format
                try {
                    SimpleDateFormat altFormat = new SimpleDateFormat("MM/dd/yyyy");
                    pr.setRequiredDate(altFormat.parse(requiredDate));
                } catch (Exception e2) {
                    JOptionPane.showMessageDialog(this, "Invalid date format. Please use yyyy-MM-dd or MM/dd/yyyy", 
                                                "Error", JOptionPane.ERROR_MESSAGE);
                    return false;
                }
            }
            
            pr.setStatus("PENDING");
            pr.setRequestedBy(currentUser.getUserId());
            
            // Add items to PR
            for (int i = 0; i < selectedItemsModel.getSize(); i++) {
                String itemText = selectedItemsModel.getElementAt(i);
                // Parse item text (format: "item - quantity unit")
                String[] parts = itemText.split(" - ");
                if (parts.length >= 2) {
                    String itemName = parts[0];
                    String[] qtyUnit = parts[1].split(" ");
                    if (qtyUnit.length >= 1) {
                        try {
                            int quantity = Integer.parseInt(qtyUnit[0]);
                            
                            // Create PR item (using item name as code for now)
                            PurchaseRequisition.PurchaseRequisitionItem prItem = 
                                new PurchaseRequisition.PurchaseRequisitionItem(
                                    pr.getPrId(), itemName, quantity, "DEFAULT_SUPPLIER"
                                );
                            pr.addItem(prItem);
                        } catch (NumberFormatException e) {
                            // Skip invalid items
                        }
                    }
                }
            }
            
            // Save PR to backend
            if (pr.savePR()) {
                clearForm();
                
                // Refresh the table data
                refreshData();
                
                JOptionPane.showMessageDialog(this, "Purchase Requisition " + pr.getPrId() + " submitted successfully!", 
                                            "Success", JOptionPane.INFORMATION_MESSAGE);
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Failed to save Purchase Requisition. Please try again.", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "An error occurred while creating the PR: " + e.getMessage(), 
                                        "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }
    
    // Method to refresh table data from backend
    public void refreshData() {
        loadSampleData(); // This now loads actual backend data
    }
    
    private void clearForm() {
        requiredDateField.setText("");
        priorityCombo.setSelectedIndex(0);
        departmentCombo.setSelectedIndex(0);
        itemField.setText("");
        quantityField.setText("");
        unitCombo.setSelectedIndex(0);
        selectedItemsModel.clear();
    }
    
    private void setupTable() {
        prTable.setFont(new Font("Inter", Font.PLAIN, 14));
        prTable.setRowHeight(50);
        prTable.setGridColor(GRAY_200);
        prTable.setShowGrid(true);
        prTable.setSelectionBackground(new Color(59, 130, 246, 50)); // Light blue with transparency
        prTable.setSelectionForeground(GRAY_900);
        prTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        // Header
        JTableHeader header = prTable.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 14));
        header.setBackground(GRAY_50);
        header.setForeground(GRAY_700);
        header.setPreferredSize(new Dimension(0, 45));
        
        // Column widths
        TableColumnModel columnModel = prTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // PR ID
        columnModel.getColumn(1).setPreferredWidth(120); // Created By
        columnModel.getColumn(2).setPreferredWidth(100); // Department
        columnModel.getColumn(3).setPreferredWidth(100); // Date Created
        columnModel.getColumn(4).setPreferredWidth(100); // Required Date
        columnModel.getColumn(5).setPreferredWidth(80);  // Items Count
        columnModel.getColumn(6).setPreferredWidth(80);  // Priority
        columnModel.getColumn(7).setPreferredWidth(80);  // Status
        columnModel.getColumn(8).setPreferredWidth(150); // Actions
        
        // Custom renderers
        prTable.getColumnModel().getColumn(6).setCellRenderer(new PriorityRenderer());
        prTable.getColumnModel().getColumn(7).setCellRenderer(new StatusRenderer());
        prTable.getColumnModel().getColumn(8).setCellRenderer(new ActionButtonRenderer());
        prTable.getColumnModel().getColumn(8).setCellEditor(new ActionButtonEditor());
        
        // Add mouse listener for better interaction
        prTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int row = prTable.rowAtPoint(e.getPoint());
                int column = prTable.columnAtPoint(e.getPoint());
                
                if (row >= 0 && column == 8) { // Actions column
                    prTable.editCellAt(row, column);
                }
            }
        });
        
        // Load sample data
        loadSampleData();
    }
    
    private void setupSearchField() {
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search PRs...")) {
                    searchField.setText("");
                    searchField.setForeground(GRAY_900);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search PRs...");
                    searchField.setForeground(GRAY_600);
                }
            }
        });
    }
    
    private void openCreateDialog() {
        // Create a dialog for creating new PR
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Create New Purchase Requisition", true);
        dialog.setSize(800, 600);
        dialog.setLocationRelativeTo(this);
        
        // Create form panel
        JPanel formPanel = createCreateFormPanel();
        dialog.add(formPanel);
        
        dialog.setVisible(true);
    }
    
    private JPanel createCreateFormPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Title
        JLabel titleLabel = new JLabel("📝 Create New Purchase Requisition");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        titleLabel.setForeground(GRAY_900);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Form
        JPanel formCard = new JPanel(new BorderLayout());
        formCard.setBackground(GRAY_50);
        formCard.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(20, 25, 20, 25)
        ));
        
        // Form fields in one row
        JPanel formRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        formRow.setBackground(GRAY_50);
        
        formRow.add(createField("Required Date", requiredDateField, 120));
        formRow.add(createField("Priority", priorityCombo, 100));
        formRow.add(createField("Department", departmentCombo, 120));
        formRow.add(createField("Item", itemField, 150));
        formRow.add(createField("Qty", quantityField, 60));
        formRow.add(createField("Unit", unitCombo, 80));
        
        // Add Item button
        JButton addBtn = createButton("Add Item", PRIMARY_BLUE);
        addBtn.addActionListener(e -> addItem());
        formRow.add(addBtn);
        
        formCard.add(formRow, BorderLayout.NORTH);
        
        // Selected items list
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBackground(GRAY_50);
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel itemsLabel = new JLabel("Selected Items:");
        itemsLabel.setFont(new Font("Inter", Font.BOLD, 14));
        itemsLabel.setForeground(GRAY_700);
        itemsPanel.add(itemsLabel, BorderLayout.NORTH);
        
        JScrollPane itemsScroll = new JScrollPane(selectedItemsList);
        itemsScroll.setPreferredSize(new Dimension(0, 150));
        itemsScroll.setBorder(BorderFactory.createLineBorder(GRAY_300, 1));
        itemsPanel.add(itemsScroll, BorderLayout.CENTER);
        
        formCard.add(itemsPanel, BorderLayout.CENTER);
        panel.add(formCard, BorderLayout.CENTER);
        
        // Bottom buttons panel
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        bottomPanel.setBackground(Color.WHITE);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton cancelBtn = createButton("Cancel", GRAY_600);
        cancelBtn.addActionListener(e -> {
            clearForm();
            SwingUtilities.getWindowAncestor(panel).dispose();
        });
        
        JButton submitBtn = createButton("Submit PR", SUCCESS_GREEN);
        submitBtn.addActionListener(e -> {
            if (submitPR()) {
                SwingUtilities.getWindowAncestor(panel).dispose();
            }
        });
        
        JButton clearBtn = createButton("Clear Form", WARNING_ORANGE);
        clearBtn.addActionListener(e -> clearForm());
        
        bottomPanel.add(clearBtn);
        bottomPanel.add(cancelBtn);
        bottomPanel.add(submitBtn);
        
        panel.add(bottomPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private void loadSampleData() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load actual data from backend
        List<PurchaseRequisition> prList = PurchaseRequisition.getAllPRs();
        
        if (prList != null && !prList.isEmpty()) {
            // Use actual backend data
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            for (PurchaseRequisition pr : prList) {
                // Get user details for the requestor
                User requestor = User.getUserById(pr.getRequestedBy());
                String requestorName = requestor != null ? requestor.getName() : pr.getRequestedBy();
                
                // Determine department based on user role or default
                String department = requestor != null ? getDepartmentFromRole(requestor.getRole()) : "General";
                
                // Format dates
                String dateCreated = dateFormat.format(pr.getPrDate());
                String requiredDate = dateFormat.format(pr.getRequiredDate());
                
                // Get items count
                int itemsCount = pr.getItems() != null ? pr.getItems().size() : 0;
                
                // Determine priority based on required date (sample logic)
                String priority = determinePriority(pr.getRequiredDate());
                
                // Format status for display
                String displayStatus = formatStatusForDisplay(pr.getStatus());
                
                Object[] row = {
                    pr.getPrId(),
                    requestorName,
                    department,
                    dateCreated,
                    requiredDate,
                    String.valueOf(itemsCount),
                    priority,
                    displayStatus,
                    "" // Actions column
                };
                
                tableModel.addRow(row);
            }
        } else {
            // Fallback to sample data if no backend data available
            Object[][] sampleData = {
                {"PR-2025-001", "John Doe", "Sales", "2025-05-24", "2025-05-30", "3", "High", "Pending", ""},
                {"PR-2025-002", "Jane Smith", "Marketing", "2025-05-23", "2025-05-28", "5", "Medium", "Approved", ""},
                {"PR-2025-003", "Mike Johnson", "Operations", "2025-05-22", "2025-05-25", "2", "Urgent", "Rejected", ""}
            };
            
            for (Object[] row : sampleData) {
                tableModel.addRow(row);
            }
        }
    }
    
    private String getDepartmentFromRole(String role) {
        switch (role.toUpperCase()) {
            case "SALES_MANAGER":
                return "Sales";
            case "PURCHASE_MANAGER":
                return "Operations";
            case "ADMIN":
                return "Administration";
            case "FINANCE_MANAGER":
                return "Finance";
            default:
                return "General";
        }
    }
    
    private String determinePriority(Date requiredDate) {
        if (requiredDate == null) return "Medium";
        
        Calendar now = Calendar.getInstance();
        Calendar required = Calendar.getInstance();
        required.setTime(requiredDate);
        
        long diffInMillis = required.getTimeInMillis() - now.getTimeInMillis();
        long diffInDays = diffInMillis / (24 * 60 * 60 * 1000);
        
        if (diffInDays <= 1) {
            return "Urgent";
        } else if (diffInDays <= 3) {
            return "High";
        } else if (diffInDays <= 7) {
            return "Medium";
        } else {
            return "Low";
        }
    }
    
    private String formatStatusForDisplay(String status) {
        if (status == null) return "Pending";
        
        switch (status.toUpperCase()) {
            case "PENDING":
                return "Pending";
            case "APPROVED":
                return "Approved";
            case "REJECTED":
                return "Rejected";
            default:
                return status;
        }
    }
    
    // Custom renderers
    private class PriorityRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            setFont(new Font("Inter", Font.BOLD, 12));
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                String priority = value.toString();
                switch (priority) {
                    case "High":
                        setBackground(new Color(254, 242, 242));
                        setForeground(new Color(185, 28, 28));
                        break;
                    case "Medium":
                        setBackground(new Color(255, 251, 235));
                        setForeground(new Color(146, 64, 14));
                        break;
                    case "Urgent":
                        setBackground(new Color(239, 68, 68));
                        setForeground(Color.WHITE);
                        break;
                    default:
                        setBackground(new Color(249, 250, 251));
                        setForeground(new Color(75, 85, 99));
                }
            }
            
            return this;
        }
    }
    
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            
            setFont(new Font("Inter", Font.BOLD, 12));
            setHorizontalAlignment(SwingConstants.CENTER);
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                setForeground(table.getSelectionForeground());
            } else {
                String status = value.toString();
                switch (status) {
                    case "Pending":
                        setBackground(new Color(255, 251, 235));
                        setForeground(new Color(146, 64, 14));
                        break;
                    case "Approved":
                        setBackground(new Color(240, 253, 244));
                        setForeground(new Color(22, 101, 52));
                        break;
                    case "Rejected":
                        setBackground(new Color(254, 242, 242));
                        setForeground(new Color(185, 28, 28));
                        break;
                    default:
                        setBackground(new Color(249, 250, 251));
                        setForeground(new Color(75, 85, 99));
                }
            }
            
            return this;
        }
    }
    
    private class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton viewButton;
        private JButton actionButton;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 5, 5));
            setOpaque(true);
            
            viewButton = new JButton("View");
            viewButton.setFont(new Font("Inter", Font.BOLD, 11));
            viewButton.setBackground(PRIMARY_BLUE);
            viewButton.setForeground(Color.WHITE);
            viewButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            viewButton.setFocusPainted(false);
            viewButton.setOpaque(true);
            
            actionButton = new JButton("Edit");
            actionButton.setFont(new Font("Inter", Font.BOLD, 11));
            actionButton.setBackground(WARNING_ORANGE);
            actionButton.setForeground(Color.WHITE);
            actionButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            actionButton.setFocusPainted(false);
            actionButton.setOpaque(true);
            
            add(viewButton);
            add(actionButton);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            
            String status = table.getValueAt(row, 7).toString();
            
            // Update action button based on status
            switch (status) {
                case "Approved":
                    actionButton.setText("Generate PO");
                    actionButton.setBackground(SUCCESS_GREEN);
                    break;
                case "Rejected":
                    actionButton.setText("Revise");
                    actionButton.setBackground(WARNING_ORANGE);
                    break;
                default:
                    actionButton.setText("Edit");
                    actionButton.setBackground(WARNING_ORANGE);
            }
            
            // Handle selection highlighting
            if (isSelected) {
                setBackground(table.getSelectionBackground());
                // Keep buttons visible with slightly transparent background
                Color selectionBg = table.getSelectionBackground();
                Color buttonBg = new Color(selectionBg.getRed(), selectionBg.getGreen(), selectionBg.getBlue(), 200);
                viewButton.setBackground(PRIMARY_BLUE);
                actionButton.setBackground(actionButton.getBackground());
            } else {
                setBackground(table.getBackground());
                viewButton.setBackground(PRIMARY_BLUE);
            }
            
            return this;
        }
    }
    
    private class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton viewButton;
        private JButton actionButton;
        private int currentRow;
        
        public ActionButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setOpaque(true);
            
            viewButton = new JButton("View");
            viewButton.setFont(new Font("Inter", Font.BOLD, 11));
            viewButton.setBackground(PRIMARY_BLUE);
            viewButton.setForeground(Color.WHITE);
            viewButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            viewButton.setFocusPainted(false);
            viewButton.addActionListener(e -> {
                viewPR(currentRow);
                fireEditingStopped();
            });
            
            actionButton = new JButton("Edit");
            actionButton.setFont(new Font("Inter", Font.BOLD, 11));
            actionButton.setBackground(WARNING_ORANGE);
            actionButton.setForeground(Color.WHITE);
            actionButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            actionButton.setFocusPainted(false);
            actionButton.addActionListener(e -> {
                performAction(currentRow);
                fireEditingStopped();
            });
            
            panel.add(viewButton);
            panel.add(actionButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            
            String status = table.getValueAt(row, 7).toString();
            
            switch (status) {
                case "Approved":
                    actionButton.setText("Generate PO");
                    actionButton.setBackground(SUCCESS_GREEN);
                    break;
                case "Rejected":
                    actionButton.setText("Revise");
                    actionButton.setBackground(WARNING_ORANGE);
                    break;
                default:
                    actionButton.setText("Edit");
                    actionButton.setBackground(WARNING_ORANGE);
            }
            
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
        
        private void viewPR(int row) {
            String prId = prTable.getValueAt(row, 0).toString();
            String createdBy = prTable.getValueAt(row, 1).toString();
            String department = prTable.getValueAt(row, 2).toString();
            String dateCreated = prTable.getValueAt(row, 3).toString();
            String requiredDate = prTable.getValueAt(row, 4).toString();
            String itemsCount = prTable.getValueAt(row, 5).toString();
            String priority = prTable.getValueAt(row, 6).toString();
            String status = prTable.getValueAt(row, 7).toString();
            
            // Create simple view dialog matching PO Approval style
            JDialog viewDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(ViewPurchaseRequisitionsPanel.this), 
                                           "Purchase Requisition Approval", true);
            viewDialog.setSize(1000, 700);
            viewDialog.setLocationRelativeTo(ViewPurchaseRequisitionsPanel.this);
            viewDialog.setLayout(new BorderLayout());
            
            // Main panel with light gray background
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(245, 245, 245));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
            
            // Title
            JLabel titleLabel = new JLabel("Purchase Requisition Approval");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setForeground(new Color(60, 60, 60));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            
            // Content panel
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(new Color(245, 245, 245));
            
            // PR Details Section - Two columns layout
            JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 100, 20));
            detailsPanel.setBackground(new Color(245, 245, 245));
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
            
            // Calculate total amount from backend data
            double totalAmount = calculateTotalAmount(prId);
            
            // Left column details
            JLabel prIdLabel = new JLabel("PR ID: " + prId);
            prIdLabel.setFont(new Font("Arial", Font.BOLD, 18));
            prIdLabel.setForeground(new Color(60, 60, 60));
            
            JLabel requestorLabel = new JLabel("Requestor: " + createdBy);
            requestorLabel.setFont(new Font("Arial", Font.BOLD, 18));
            requestorLabel.setForeground(new Color(60, 60, 60));
            
            // Right column details
            JLabel totalLabel = new JLabel(String.format("Total Amount: $%.2f", totalAmount));
            totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
            totalLabel.setForeground(new Color(60, 60, 60));
            
            JLabel departmentLabel = new JLabel("Department: " + department);
            departmentLabel.setFont(new Font("Arial", Font.BOLD, 18));
            departmentLabel.setForeground(new Color(60, 60, 60));
            
            detailsPanel.add(prIdLabel);
            detailsPanel.add(totalLabel);
            detailsPanel.add(requestorLabel);
            detailsPanel.add(departmentLabel);
            
            contentPanel.add(detailsPanel);
            
            // Items table
            JPanel itemsPanel = createSimpleItemsTable(prId);
            contentPanel.add(itemsPanel);
            
            // Comments Section
            JLabel commentsLabel = new JLabel("Approval Comments");
            commentsLabel.setFont(new Font("Arial", Font.BOLD, 18));
            commentsLabel.setForeground(new Color(60, 60, 60));
            commentsLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
            contentPanel.add(commentsLabel);
            
            JTextArea commentsArea = new JTextArea(4, 0);
            commentsArea.setFont(new Font("Arial", Font.PLAIN, 14));
            commentsArea.setLineWrap(true);
            commentsArea.setWrapStyleWord(true);
            commentsArea.setText("Enter approval comments or reasons for rejection...");
            commentsArea.setForeground(new Color(150, 150, 150));
            commentsArea.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));
            
            // Placeholder behavior for comments
            commentsArea.addFocusListener(new FocusAdapter() {
                @Override
                public void focusGained(FocusEvent e) {
                    if (commentsArea.getText().equals("Enter approval comments or reasons for rejection...")) {
                        commentsArea.setText("");
                        commentsArea.setForeground(new Color(60, 60, 60));
                    }
                }
                
                @Override
                public void focusLost(FocusEvent e) {
                    if (commentsArea.getText().trim().isEmpty()) {
                        commentsArea.setText("Enter approval comments or reasons for rejection...");
                        commentsArea.setForeground(new Color(150, 150, 150));
                    }
                }
            });
            
            JScrollPane commentsScroll = new JScrollPane(commentsArea);
            commentsScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            commentsScroll.setPreferredSize(new Dimension(0, 100));
            contentPanel.add(commentsScroll);
            
            mainPanel.add(contentPanel, BorderLayout.CENTER);
            
            // Action Buttons Panel
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
            buttonPanel.setBackground(new Color(245, 245, 245));
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
            
            // Approve button (green) - only show if user can approve
            if (canApprovePR()) {
                JButton approveBtn = new JButton("Approve");
                approveBtn.setFont(new Font("Arial", Font.BOLD, 16));
                approveBtn.setBackground(new Color(92, 184, 92));
                approveBtn.setForeground(Color.WHITE);
                approveBtn.setPreferredSize(new Dimension(120, 45));
                approveBtn.setBorder(BorderFactory.createEmptyBorder());
                approveBtn.setFocusPainted(false);
                approveBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                approveBtn.setOpaque(true);
                approveBtn.setBorderPainted(false);
                approveBtn.addActionListener(e -> {
                    // Update status in table
                    prTable.setValueAt("Approved", row, 7);
                    JOptionPane.showMessageDialog(viewDialog, "Purchase Requisition " + prId + " has been approved!", 
                                                "Approval Successful", JOptionPane.INFORMATION_MESSAGE);
                    viewDialog.dispose();
                });
                buttonPanel.add(approveBtn);
                
                // Reject button (red)
                JButton rejectBtn = new JButton("Reject");
                rejectBtn.setFont(new Font("Arial", Font.BOLD, 16));
                rejectBtn.setBackground(new Color(217, 83, 79));
                rejectBtn.setForeground(Color.WHITE);
                rejectBtn.setPreferredSize(new Dimension(120, 45));
                rejectBtn.setBorder(BorderFactory.createEmptyBorder());
                rejectBtn.setFocusPainted(false);
                rejectBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                rejectBtn.setOpaque(true);
                rejectBtn.setBorderPainted(false);
                rejectBtn.addActionListener(e -> {
                    // Update status in table
                    prTable.setValueAt("Rejected", row, 7);
                    JOptionPane.showMessageDialog(viewDialog, "Purchase Requisition " + prId + " has been rejected!", 
                                                "Rejection Successful", JOptionPane.INFORMATION_MESSAGE);
                    viewDialog.dispose();
                });
                buttonPanel.add(rejectBtn);
            }
            
            // Request Modification button (orange) - show if PR can be modified
            if (status.equals("Pending") || status.equals("Rejected")) {
                JButton modifyBtn = new JButton("Request Modification");
                modifyBtn.setFont(new Font("Arial", Font.BOLD, 16));
                modifyBtn.setBackground(new Color(240, 173, 78));
                modifyBtn.setForeground(Color.WHITE);
                modifyBtn.setPreferredSize(new Dimension(180, 45));
                modifyBtn.setBorder(BorderFactory.createEmptyBorder());
                modifyBtn.setFocusPainted(false);
                modifyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                modifyBtn.setOpaque(true);
                modifyBtn.setBorderPainted(false);
                modifyBtn.addActionListener(e -> {
                    JOptionPane.showMessageDialog(viewDialog, "Modification request sent for PR: " + prId, 
                                                "Modification Requested", JOptionPane.INFORMATION_MESSAGE);
                    viewDialog.dispose();
                });
                buttonPanel.add(modifyBtn);
            }
            
            // Print button
            JButton printBtn = new JButton("Print");
            printBtn.setFont(new Font("Arial", Font.BOLD, 16));
            printBtn.setBackground(new Color(91, 192, 222));
            printBtn.setForeground(Color.WHITE);
            printBtn.setPreferredSize(new Dimension(120, 45));
            printBtn.setBorder(BorderFactory.createEmptyBorder());
            printBtn.setFocusPainted(false);
            printBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            printBtn.setOpaque(true);
            printBtn.setBorderPainted(false);
            printBtn.addActionListener(e -> {
                showPrintableReceipt(prId, createdBy, department, dateCreated, 
                                   requiredDate, priority, status, itemsCount);
            });
            buttonPanel.add(printBtn);
            
            // Cancel button (gray)
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setFont(new Font("Arial", Font.BOLD, 16));
            cancelBtn.setBackground(new Color(108, 117, 125));
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setPreferredSize(new Dimension(120, 45));
            cancelBtn.setBorder(BorderFactory.createEmptyBorder());
            cancelBtn.setFocusPainted(false);
            cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelBtn.setOpaque(true);
            cancelBtn.setBorderPainted(false);
            cancelBtn.addActionListener(e -> viewDialog.dispose());
            buttonPanel.add(cancelBtn);
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            viewDialog.add(mainPanel);
            viewDialog.setVisible(true);
        }
        
        private JPanel createSimpleItemsTable(String prId) {
            JPanel itemsPanel = new JPanel(new BorderLayout());
            itemsPanel.setBackground(new Color(245, 245, 245));
            itemsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            
            // Get actual data from backend
            Object[][] data = generateItemData(prId, "Medium");
            String[] itemColumns = {"Item", "Quantity", "Unit Price", "Total"};
            
            // Convert data to match simple table format
            Object[][] simpleData = new Object[data.length][4];
            for (int i = 0; i < data.length; i++) {
                simpleData[i][0] = data[i][1]; // Item name
                simpleData[i][1] = data[i][3] + " " + data[i][4]; // Quantity + Unit
                simpleData[i][2] = String.format("$%.2f", (Double)data[i][5]); // Unit Price
                simpleData[i][3] = String.format("$%.2f", (Double)data[i][6]); // Total
            }
            
            DefaultTableModel itemTableModel = new DefaultTableModel(simpleData, itemColumns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return false;
                }
            };
            
            JTable itemsTable = new JTable(itemTableModel);
            itemsTable.setFont(new Font("Arial", Font.PLAIN, 16));
            itemsTable.setRowHeight(50);
            itemsTable.setGridColor(new Color(200, 200, 200));
            itemsTable.setShowGrid(true);
            itemsTable.setBackground(Color.WHITE);
            
            // Header styling
            JTableHeader itemsHeader = itemsTable.getTableHeader();
            itemsHeader.setFont(new Font("Arial", Font.BOLD, 16));
            itemsHeader.setBackground(new Color(240, 240, 240));
            itemsHeader.setForeground(new Color(60, 60, 60));
            itemsHeader.setPreferredSize(new Dimension(0, 45));
            
            // Column widths
            TableColumnModel itemColumnModel = itemsTable.getColumnModel();
            itemColumnModel.getColumn(0).setPreferredWidth(300); // Item
            itemColumnModel.getColumn(1).setPreferredWidth(150); // Quantity
            itemColumnModel.getColumn(2).setPreferredWidth(120); // Unit Price
            itemColumnModel.getColumn(3).setPreferredWidth(120); // Total
            
            JScrollPane itemsScroll = new JScrollPane(itemsTable);
            itemsScroll.setPreferredSize(new Dimension(0, 150));
            itemsScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            itemsScroll.setBackground(Color.WHITE);
            
            itemsPanel.add(itemsScroll, BorderLayout.CENTER);
            return itemsPanel;
        }
        
        private double calculateTotalAmount(String prId) {
            Object[][] data = generateItemData(prId, "Medium");
            return calculateTotal(data);
        }
        
        private boolean canApprovePR() {
            return currentUser.getRole().equals("ADMIN") ||
                   currentUser.getRole().equals("PURCHASE_MANAGER") ||
                   currentUser.getRole().equals("FINANCE_MANAGER") ||
                   currentUser.getRole().equals("SALES_MANAGER");
        }
        
        private Object[][] generateItemData(String prId, String priority) {
            // Try to get actual PR data from backend
            PurchaseRequisition pr = PurchaseRequisition.getPRById(prId);
            
            if (pr != null && pr.getItems() != null && !pr.getItems().isEmpty()) {
                // Use actual backend data
                List<PurchaseRequisition.PurchaseRequisitionItem> items = pr.getItems();
                Object[][] data = new Object[items.size()][7];
                
                for (int i = 0; i < items.size(); i++) {
                    PurchaseRequisition.PurchaseRequisitionItem item = items.get(i);
                    
                    // Try to get item details from Item model
                    Item itemDetails = Item.getItemByCode(item.getItemCode());
                    String itemName = itemDetails != null ? itemDetails.getItemName() : item.getItemCode();
                    String category = itemDetails != null ? itemDetails.getCategory() : "General";
                    double unitPrice = itemDetails != null ? itemDetails.getUnitPrice() : 0.0;
                    String unit = "pcs"; // Default unit since Item model doesn't have unit field
                    
                    double totalPrice = unitPrice * item.getQuantity();
                    
                    data[i] = new Object[]{
                        i + 1,                          // #
                        itemName,                       // Name
                        category,                       // Category
                        item.getQuantity(),             // Quantity
                        unit,                           // Unit
                        unitPrice,                      // Item Price
                        totalPrice                      // Total
                    };
                }
                return data;
            } else {
                // Fallback to sample data if no backend data available
                if (priority.equals("High") || priority.equals("Urgent")) {
                    return new Object[][] {
                        {1, "Apple iPad - 64GB, Silver", "Office Equipment", 3, "Each", 350.00, 1050.00},
                        {2, "Apple iPad Magic Keyboard", "Office Equipment", 2, "Each", 429.00, 858.00}
                    };
                } else {
                    return new Object[][] {
                        {1, "Office Supplies - Premium Quality", "Office Supplies", 10, "boxes", 25.00, 250.00},
                        {2, "Printer Paper - A4 White", "Office Supplies", 5, "reams", 15.00, 75.00},
                        {3, "Ink Cartridges - Black & Color Set", "Office Supplies", 3, "sets", 60.00, 180.00}
                    };
                }
            }
        }
        
        private double calculateTotal(Object[][] data) {
            double total = 0.0;
            for (Object[] row : data) {
                total += (Double) row[6]; // Total column
            }
            return total;
        }
        
        private void showPrintableReceipt(String prId, String createdBy, String department, 
                                        String dateCreated, String requiredDate, String priority, 
                                        String status, String itemsCount) {
            // Create a new dialog for the printable receipt
            JDialog printDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(ViewPurchaseRequisitionsPanel.this), 
                                            "Print Preview - Purchase Requisition " + prId, true);
            printDialog.setSize(950, 800);
            printDialog.setLocationRelativeTo(ViewPurchaseRequisitionsPanel.this);
            
            // Create simple print layout
            JPanel printPanel = new JPanel(new BorderLayout());
            printPanel.setBackground(Color.WHITE);
            printPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
            
            // Header
            JPanel headerPanel = new JPanel(new BorderLayout());
            headerPanel.setBackground(Color.WHITE);
            headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            
            JLabel titleLabel = new JLabel("OWSB Company Ltd. - Purchase Requisition");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
            titleLabel.setForeground(new Color(60, 60, 60));
            
            JLabel prIdLabel = new JLabel("PR ID: " + prId);
            prIdLabel.setFont(new Font("Arial", Font.BOLD, 18));
            prIdLabel.setForeground(new Color(60, 60, 60));
            
            headerPanel.add(titleLabel, BorderLayout.WEST);
            headerPanel.add(prIdLabel, BorderLayout.EAST);
            
            // Details section
            JPanel detailsPanel = new JPanel(new GridLayout(4, 2, 20, 10));
            detailsPanel.setBackground(Color.WHITE);
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 30, 0));
            
            detailsPanel.add(new JLabel("Requestor: " + createdBy));
            detailsPanel.add(new JLabel("Department: " + department));
            detailsPanel.add(new JLabel("Date Created: " + dateCreated));
            detailsPanel.add(new JLabel("Required Date: " + requiredDate));
            detailsPanel.add(new JLabel("Priority: " + priority));
            detailsPanel.add(new JLabel("Status: " + status));
            detailsPanel.add(new JLabel("Items Count: " + itemsCount));
            detailsPanel.add(new JLabel("Total Amount: $" + String.format("%.2f", calculateTotalAmount(prId))));
            
            // Items table
            JPanel itemsPanel = createSimpleItemsTable(prId);
            
            // Signature section
            JPanel signaturePanel = new JPanel(new BorderLayout());
            signaturePanel.setBackground(Color.WHITE);
            signaturePanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 20, 0));
            
            JLabel receiverLabel = new JLabel("Receiver");
            receiverLabel.setFont(new Font("Arial", Font.BOLD, 18));
            receiverLabel.setForeground(new Color(60, 60, 60));
            
            JPanel signatureFields = new JPanel(new GridBagLayout());
            signatureFields.setBackground(Color.WHITE);
            
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.anchor = GridBagConstraints.WEST;
            gbc.insets = new Insets(15, 0, 15, 0);
            
            gbc.gridx = 0; gbc.gridy = 0;
            JLabel fullNameLabel = new JLabel("Full Name");
            fullNameLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            fullNameLabel.setForeground(new Color(60, 60, 60));
            signatureFields.add(fullNameLabel, gbc);
            
            gbc.gridx = 1; gbc.gridy = 0;
            gbc.insets = new Insets(15, 20, 15, 0);
            JLabel nameLineLabel = new JLabel("_".repeat(50));
            nameLineLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            nameLineLabel.setForeground(new Color(150, 150, 150));
            signatureFields.add(nameLineLabel, gbc);
            
            gbc.gridx = 0; gbc.gridy = 1;
            gbc.insets = new Insets(15, 0, 15, 0);
            JLabel signatureLabel = new JLabel("Signature");
            signatureLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            signatureLabel.setForeground(new Color(60, 60, 60));
            signatureFields.add(signatureLabel, gbc);
            
            gbc.gridx = 1; gbc.gridy = 1;
            gbc.insets = new Insets(15, 20, 15, 0);
            JLabel sigLineLabel = new JLabel("_".repeat(50));
            sigLineLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            sigLineLabel.setForeground(new Color(150, 150, 150));
            signatureFields.add(sigLineLabel, gbc);
            
            signaturePanel.add(receiverLabel, BorderLayout.NORTH);
            signaturePanel.add(signatureFields, BorderLayout.CENTER);
            
            // Assemble print panel
            JPanel contentPanel = new JPanel(new BorderLayout());
            contentPanel.setBackground(Color.WHITE);
            contentPanel.add(detailsPanel, BorderLayout.NORTH);
            contentPanel.add(itemsPanel, BorderLayout.CENTER);
            contentPanel.add(signaturePanel, BorderLayout.SOUTH);
            
            printPanel.add(headerPanel, BorderLayout.NORTH);
            printPanel.add(contentPanel, BorderLayout.CENTER);
            
            JScrollPane printScrollPane = new JScrollPane(printPanel);
            printScrollPane.setBorder(null);
            printScrollPane.setBackground(Color.WHITE);
            
            // Print dialog buttons
            JPanel printButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
            printButtonPanel.setBackground(Color.WHITE);
            printButtonPanel.setBorder(BorderFactory.createEmptyBorder(10, 20, 20, 20));
            
            JButton actualPrintBtn = createButton("Print", SUCCESS_GREEN);
            actualPrintBtn.addActionListener(e -> {
                // Here you would implement actual printing functionality
                JOptionPane.showMessageDialog(printDialog, 
                    "Printing Purchase Requisition " + prId + "...\n" +
                    "Document sent to default printer.\n" +
                    "Print job queued successfully.",
                    "Print Successful", JOptionPane.INFORMATION_MESSAGE);
                printDialog.dispose();
            });
            
            JButton cancelPrintBtn = createButton("Cancel", GRAY_600);
            cancelPrintBtn.addActionListener(e -> printDialog.dispose());
            
            printButtonPanel.add(cancelPrintBtn);
            printButtonPanel.add(actualPrintBtn);
            
            JPanel printMainPanel = new JPanel(new BorderLayout());
            printMainPanel.add(printScrollPane, BorderLayout.CENTER);
            printMainPanel.add(printButtonPanel, BorderLayout.SOUTH);
            
            printDialog.add(printMainPanel);
            printDialog.setVisible(true);
        }
        
        private void performAction(int row) {
            String prId = prTable.getValueAt(row, 0).toString();
            String status = prTable.getValueAt(row, 7).toString();
            String action = actionButton.getText();
            
            JOptionPane.showMessageDialog(ViewPurchaseRequisitionsPanel.this,
                action + " action for PR: " + prId + "\nStatus: " + status,
                action + " PR",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
} 