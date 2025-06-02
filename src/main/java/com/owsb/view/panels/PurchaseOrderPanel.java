package com.owsb.view.panels;

import com.owsb.model.*;
import com.owsb.model.PurchaseRequisition.PurchaseRequisitionItem;
import com.owsb.model.PurchaseOrder.PurchaseOrderItem;
import com.owsb.view.Dashboard;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PurchaseOrderPanel extends JPanel {
    private User currentUser;
    private Dashboard parentDashboard;
    
    // UI Components
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> supplierFilter;
    private JTextField dateField;
    private JTable poTable;
    private DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;
    
    // Modern UI Colors - matching ViewPurchaseRequisitionsPanel
    private static final Color PRIMARY_BLUE = new Color(37, 99, 235);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color DANGER_RED = new Color(239, 68, 68);
    private static final Color WARNING_ORANGE = new Color(245, 158, 11);
    private static final Color WARNING_YELLOW = new Color(251, 191, 36);
    private static final Color GRAY_50 = new Color(249, 250, 251);
    private static final Color GRAY_100 = new Color(243, 244, 246);
    private static final Color GRAY_200 = new Color(229, 231, 235);
    private static final Color GRAY_300 = new Color(209, 213, 219);
    private static final Color GRAY_600 = new Color(75, 85, 99);
    private static final Color GRAY_700 = new Color(55, 65, 81);
    private static final Color GRAY_900 = new Color(17, 24, 39);
    
    public PurchaseOrderPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        initializeComponents();
        setupUI();
        loadPurchaseOrders();
    }
    
    // Add method to set parent dashboard reference
    public void setParentDashboard(Dashboard dashboard) {
        this.parentDashboard = dashboard;
    }
    
    // Method to set selected PR (for navigation from ViewPurchaseRequisitionsPanel)
    public void setSelectedPR(String prId) {
        // Switch to the Generate PO tab and pre-select the PR
        SwingUtilities.invokeLater(() -> {
            if (tabbedPane != null) {
                tabbedPane.setSelectedIndex(1); // Switch to Generate PO tab
                // Here you could also pre-select the PR in the dropdown
            }
        });
    }
    
    private void initializeComponents() {
        // Search and filter components
        searchField = new JTextField("Search POs...");
        searchField.setForeground(GRAY_600);
        
        statusFilter = new JComboBox<>(new String[]{"All Status", "Pending Approval", "Approved", "Rejected", "Completed", "Draft"});
        supplierFilter = new JComboBox<>(new String[]{"All Suppliers", "ABC Trading Sdn Bhd", "XYZ Supplies Ltd", "Global Foods Enterprise", "Office Supply Co."});
        dateField = new JTextField("05/28/2025");
        
        // Setup search field placeholder behavior
        setupSearchFieldPlaceholder();
        
        // Initialize table
        String[] columnNames = {"PO ID", "PR Reference", "Supplier", "Created By", "Date Created", "Expected Delivery", "Total Amount", "Priority", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 9; // Only Actions column is editable
            }
        };
        
        poTable = new JTable(tableModel);
        setupTable();
    }
    
    private void setupUI() {
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Create tabbed pane
        tabbedPane = new JTabbedPane();
        tabbedPane.setFont(new Font("Inter", Font.BOLD, 14));
        tabbedPane.setBackground(Color.WHITE);
        
        // Tab 1: View Purchase Orders
        JPanel viewPOPanel = createViewPOPanel();
        tabbedPane.addTab("📋 View Purchase Orders", viewPOPanel);
        
        // Tab 2: Generate Purchase Order (only for users who can create POs)
        if (canCreatePO()) {
            JPanel generatePOPanel = createGeneratePOPanel();
            tabbedPane.addTab("📝 Generate Purchase Order", generatePOPanel);
        }
        
        mainContainer.add(tabbedPane, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        
        // Title section
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Purchase Orders");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(GRAY_900);
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createUserBadge() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setBackground(Color.WHITE);
        
        JPanel badge = new JPanel(new BorderLayout());
        badge.setBackground(GRAY_50);
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        
        // Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(PRIMARY_BLUE);
                g2d.fillOval(0, 0, 36, 36);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Inter", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                String initials = getUserInitials();
                int x = (36 - fm.stringWidth(initials)) / 2;
                int y = (36 + fm.getAscent()) / 2 - 2;
                g2d.drawString(initials, x, y);
                g2d.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setOpaque(false);
        
        // User info
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBackground(GRAY_50);
        userInfo.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        
        JLabel nameLabel = new JLabel(getRoleDisplayName(currentUser.getRole()));
        nameLabel.setFont(new Font("Inter", Font.BOLD, 14));
        nameLabel.setForeground(GRAY_900);
        
        JLabel roleLabel = new JLabel("OWSB Operations");
        roleLabel.setFont(new Font("Inter", Font.PLAIN, 12));
        roleLabel.setForeground(GRAY_600);
        
        userInfo.add(nameLabel);
        userInfo.add(roleLabel);
        
        badge.add(avatar, BorderLayout.WEST);
        badge.add(userInfo, BorderLayout.CENTER);
        
        panel.add(badge);
        return panel;
    }
    
    private String getUserInitials() {
        String role = currentUser.getRole();
        switch (role) {
            case "ADMIN": return "AD";
            case "PURCHASE_MANAGER": return "PM";
            case "PURCHASE_STAFF": return "PS";
            case "FINANCE_MANAGER": return "FM";
            case "INVENTORY_MANAGER": return "IM";
            case "INVENTORY_STAFF": return "IS";
            default: return "PO";
        }
    }
    
    private String getRoleDisplayName(String role) {
        switch (role) {
            case "ADMIN": return "Administrator";
            case "PURCHASE_MANAGER": return "Purchase Manager";
            case "PURCHASE_STAFF": return "Purchase Staff";
            case "FINANCE_MANAGER": return "Finance Manager";
            case "INVENTORY_MANAGER": return "Inventory Manager";
            case "INVENTORY_STAFF": return "Inventory Staff";
            default: return "User";
        }
    }
    
    private JPanel createViewPOPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Content panel (the existing content)
        JPanel contentPanel = createContentPanel();
        panel.add(contentPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createGeneratePOPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Section title
        JLabel sectionTitle = new JLabel("Generate Purchase Order");
        sectionTitle.setFont(new Font("Inter", Font.BOLD, 28));
        sectionTitle.setForeground(GRAY_900);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sectionTitle);
        
        // PR Selection Section
        JPanel prSelectionPanel = createPRSelectionSection();
        prSelectionPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(prSelectionPanel);
        panel.add(Box.createVerticalStrut(25));
        
        // Form fields section
        JPanel formFieldsPanel = createPOFormFields();
        formFieldsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(formFieldsPanel);
        panel.add(Box.createVerticalStrut(25));
        
        // Items section
        JPanel itemsPanel = createPOItemsSection();
        itemsPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(itemsPanel);
        panel.add(Box.createVerticalStrut(25));
        
        // Action buttons
        JPanel buttonPanel = createPOActionButtonsForTab();
        buttonPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Section header with Create New PO button
        JPanel sectionHeader = createSectionHeader();
        panel.add(sectionHeader, BorderLayout.NORTH);
        
        // Filters panel
        JPanel filtersPanel = createFiltersPanel();
        panel.add(filtersPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSectionHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        
        JLabel sectionTitle = new JLabel("Purchase Orders Management");
        sectionTitle.setFont(new Font("Inter", Font.BOLD, 24));
        sectionTitle.setForeground(GRAY_900);
        
        panel.add(sectionTitle, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createFiltersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Filters row
        JPanel filtersRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        filtersRow.setBackground(Color.WHITE);
        filtersRow.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        
        // Search field
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setFont(new Font("Inter", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        // Status filter
        statusFilter.setPreferredSize(new Dimension(140, 40));
        statusFilter.setFont(new Font("Inter", Font.PLAIN, 14));
        statusFilter.setBorder(BorderFactory.createLineBorder(GRAY_300, 1));
        
        // Supplier filter
        supplierFilter.setPreferredSize(new Dimension(180, 40));
        supplierFilter.setFont(new Font("Inter", Font.PLAIN, 14));
        supplierFilter.setBorder(BorderFactory.createLineBorder(GRAY_300, 1));
        
        // Date field
        dateField.setPreferredSize(new Dimension(120, 40));
        dateField.setFont(new Font("Inter", Font.PLAIN, 14));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        filtersRow.add(searchField);
        filtersRow.add(statusFilter);
        filtersRow.add(supplierFilter);
        filtersRow.add(dateField);
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        
        panel.add(filtersRow, BorderLayout.NORTH);
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(poTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(GRAY_200, 1));
        scrollPane.setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private void setupTable() {
        poTable.setFont(new Font("Inter", Font.PLAIN, 14));
        poTable.setRowHeight(60);
        poTable.setGridColor(GRAY_200);
        poTable.setShowGrid(true);
        poTable.setIntercellSpacing(new Dimension(1, 1));
        poTable.setSelectionBackground(GRAY_50);
        poTable.setSelectionForeground(GRAY_900);
        
        // Header styling
        JTableHeader header = poTable.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 14));
        header.setBackground(GRAY_50);
        header.setForeground(GRAY_700);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY_200));
        header.setPreferredSize(new Dimension(0, 50));
        
        // Column widths
        TableColumnModel columnModel = poTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // PO ID
        columnModel.getColumn(1).setPreferredWidth(110); // PR Reference
        columnModel.getColumn(2).setPreferredWidth(150); // Supplier
        columnModel.getColumn(3).setPreferredWidth(120); // Created By
        columnModel.getColumn(4).setPreferredWidth(120); // Date Created
        columnModel.getColumn(5).setPreferredWidth(120); // Expected Delivery
        columnModel.getColumn(6).setPreferredWidth(120); // Total Amount
        columnModel.getColumn(7).setPreferredWidth(100); // Priority
        columnModel.getColumn(8).setPreferredWidth(120); // Status
        columnModel.getColumn(9).setPreferredWidth(200); // Actions
        
        // Custom renderers
        poTable.getColumnModel().getColumn(7).setCellRenderer(new PriorityRenderer());
        poTable.getColumnModel().getColumn(8).setCellRenderer(new StatusRenderer());
        poTable.getColumnModel().getColumn(9).setCellRenderer(new ActionButtonRenderer());
        poTable.getColumnModel().getColumn(9).setCellEditor(new ActionButtonEditor());
    }
    
    private void setupSearchFieldPlaceholder() {
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search POs...")) {
                    searchField.setText("");
                    searchField.setForeground(GRAY_900);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search POs...");
                    searchField.setForeground(GRAY_600);
                }
            }
        });
        
        // Add search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterTable();
            }
        });
        
        // Add filter functionality
        statusFilter.addActionListener(e -> filterTable());
        supplierFilter.addActionListener(e -> filterTable());
    }
    
    private void filterTable() {
        String searchText = searchField.getText().trim();
        if (searchText.equals("Search POs...")) {
            searchText = "";
        }
        
        String selectedStatus = (String) statusFilter.getSelectedItem();
        String selectedSupplier = (String) supplierFilter.getSelectedItem();
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Load sample Purchase Orders (in real implementation, load from backend)
        loadPurchaseOrders();
    }
    
    private void loadPurchaseOrders() {
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Sample Purchase Orders data
        Object[][] samplePOs = {
            {"PO-2025-004", "PR-2025-005", "ABC Trading Sdn Bhd", "John Purchase", "2025-05-24", "2025-05-28", "RM 759.00", "High", "Pending Approval", ""},
            {"PO-2025-005", "PR-2025-006", "XYZ Supplies Ltd", "Sarah Purchase", "2025-05-24", "2025-05-30", "RM 1,245.50", "Urgent", "Pending Approval", ""},
            {"PO-2025-003", "PR-2025-004", "Global Foods Enterprise", "Mike Purchase", "2025-05-23", "2025-05-27", "RM 320.75", "Medium", "Approved", ""},
            {"PO-2025-002", "PR-2025-003", "Office Supply Co.", "Lisa Purchase", "2025-05-22", "2025-05-26", "RM 185.25", "Low", "Completed", ""},
            {"PO-2025-001", "PR-2025-002", "ABC Trading Sdn Bhd", "John Purchase", "2025-05-21", "2025-05-25", "RM 450.00", "Medium", "Completed", ""}
        };
        
        for (Object[] row : samplePOs) {
            tableModel.addRow(row);
        }
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(10, 16, 10, 16));
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setOpaque(true);
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Hover effect
        Color originalBg = bgColor;
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (bgColor == PRIMARY_BLUE) {
                    button.setBackground(new Color(29, 78, 216)); // Darker blue
                } else if (bgColor == SUCCESS_GREEN) {
                    button.setBackground(new Color(22, 163, 74)); // Darker green
                } else {
                    button.setBackground(originalBg.darker());
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBg);
            }
        });
        
        return button;
    }
    
    private void createNewPO() {
        // Show the Generate Purchase Order dialog
        showGeneratePODialog();
    }
    
    private void showGeneratePODialog() {
        // Create the Generate Purchase Order dialog
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Generate Purchase Order", true);
        dialog.setSize(1200, 800);
        dialog.setLocationRelativeTo(this);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Header matching the image design
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(new Color(52, 144, 220)); // Blue header
        headerPanel.setBorder(BorderFactory.createEmptyBorder(15, 25, 15, 25));
        
        // Left side - Title
        JLabel titleLabel = new JLabel("Generate Purchase Order");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        
        // Right side - User badge
        JPanel userBadge = createHeaderUserBadge();
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(userBadge, BorderLayout.EAST);
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Section title
        JLabel sectionTitle = new JLabel("Generate Purchase Order");
        sectionTitle.setFont(new Font("Inter", Font.BOLD, 28));
        sectionTitle.setForeground(GRAY_900);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        contentPanel.add(sectionTitle);
        
        // PR Selection Section
        JPanel prSelectionPanel = createPRSelectionSection();
        contentPanel.add(prSelectionPanel);
        contentPanel.add(Box.createVerticalStrut(25));
        
        // Form fields section
        JPanel formFieldsPanel = createPOFormFields();
        contentPanel.add(formFieldsPanel);
        contentPanel.add(Box.createVerticalStrut(25));
        
        // Items section
        JPanel itemsPanel = createPOItemsSection();
        contentPanel.add(itemsPanel);
        contentPanel.add(Box.createVerticalStrut(25));
        
        // Action buttons
        // JPanel buttonPanel = createPOActionButtons(dialog);
        // contentPanel.add(buttonPanel);
        
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private JPanel createHeaderUserBadge() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setBackground(new Color(52, 144, 220));
        
        JPanel badge = new JPanel(new BorderLayout());
        badge.setBackground(Color.WHITE);
        badge.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
        
        // Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(52, 144, 220));
                g2d.fillOval(0, 0, 32, 32);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Inter", Font.BOLD, 12));
                FontMetrics fm = g2d.getFontMetrics();
                String initials = "SM"; // Sales Manager as shown in image
                int x = (32 - fm.stringWidth(initials)) / 2;
                int y = (32 + fm.getAscent()) / 2 - 2;
                g2d.drawString(initials, x, y);
                g2d.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(32, 32));
        avatar.setOpaque(false);
        
        // User info
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBackground(Color.WHITE);
        userInfo.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 0));
        
        JLabel nameLabel = new JLabel("Sales Manager");
        nameLabel.setFont(new Font("Inter", Font.BOLD, 12));
        nameLabel.setForeground(GRAY_900);
        
        JLabel roleLabel = new JLabel("John Doe");
        roleLabel.setFont(new Font("Inter", Font.PLAIN, 10));
        roleLabel.setForeground(GRAY_600);
        
        userInfo.add(nameLabel);
        userInfo.add(roleLabel);
        
        badge.add(avatar, BorderLayout.WEST);
        badge.add(userInfo, BorderLayout.CENTER);
        
        panel.add(badge);
        return panel;
    }
    
    private JPanel createPRSelectionSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        // Section header
        JLabel sectionLabel = new JLabel("Select Approved Purchase Requisition");
        sectionLabel.setFont(new Font("Inter", Font.BOLD, 16));
        sectionLabel.setForeground(new Color(52, 144, 220));
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(sectionLabel);
        
        // Dropdown with approved PRs
        JComboBox<String> prDropdown = new JComboBox<>();
        prDropdown.addItem("Select Approved PR");
        prDropdown.addItem("PR-2025-002 - Jane Smith (5 items) - Due: 2025-05-28");
        prDropdown.addItem("PR-2025-004 - Tom Wilson (2 items) - Due: 2025-05-29");
        
        prDropdown.setFont(new Font("Inter", Font.PLAIN, 14));
        prDropdown.setPreferredSize(new Dimension(600, 40));
        prDropdown.setMaximumSize(new Dimension(600, 40));
        prDropdown.setAlignmentX(Component.LEFT_ALIGNMENT);
        prDropdown.setBorder(BorderFactory.createLineBorder(GRAY_300, 1));
        
        panel.add(prDropdown);
        
        return panel;
    }
    
    private JPanel createPOFormFields() {
        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(Color.WHITE);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.anchor = GridBagConstraints.WEST;
        
        // PO ID
        gbc.gridx = 0; gbc.gridy = 0;
        JTextField poIdField = new JTextField("PO-2025-001");
        poIdField.setFont(new Font("Inter", Font.PLAIN, 14));
        poIdField.setPreferredSize(new Dimension(200, 35));
        panel.add(poIdField, gbc);
        
        // PR Reference
        gbc.gridx = 1; gbc.gridy = 0;
        JTextField prRefField = new JTextField("PR-2025-002");
        prRefField.setFont(new Font("Inter", Font.PLAIN, 14));
        prRefField.setPreferredSize(new Dimension(200, 35));
        panel.add(prRefField, gbc);
        
        // Supplier dropdown
        gbc.gridx = 2; gbc.gridy = 0;
        JComboBox<String> supplierDropdown = new JComboBox<>();
        supplierDropdown.addItem("Select Supplier");
        supplierDropdown.addItem("ABC Trading Sdn Bhd");
        supplierDropdown.addItem("XYZ Supplies Ltd");
        supplierDropdown.addItem("Global Foods Enterprise");
        supplierDropdown.setFont(new Font("Inter", Font.PLAIN, 14));
        supplierDropdown.setPreferredSize(new Dimension(200, 35));
        panel.add(supplierDropdown, gbc);
        
        // Date field
        gbc.gridx = 3; gbc.gridy = 0;
        JTextField dateField = new JTextField("06/01/2025");
        dateField.setFont(new Font("Inter", Font.PLAIN, 14));
        dateField.setPreferredSize(new Dimension(150, 35));
        panel.add(dateField, gbc);
        
        // Payment Terms
        gbc.gridx = 0; gbc.gridy = 1;
        JLabel paymentLabel = new JLabel("Payment Terms");
        paymentLabel.setFont(new Font("Inter", Font.BOLD, 14));
        paymentLabel.setForeground(GRAY_700);
        panel.add(paymentLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 2;
        JComboBox<String> paymentTerms = new JComboBox<>();
        paymentTerms.addItem("Select Payment Terms");
        paymentTerms.addItem("Net 30");
        paymentTerms.addItem("Net 60");
        paymentTerms.addItem("Cash on Delivery");
        paymentTerms.setFont(new Font("Inter", Font.PLAIN, 14));
        paymentTerms.setPreferredSize(new Dimension(200, 35));
        panel.add(paymentTerms, gbc);
        
        // Delivery Address
        gbc.gridx = 1; gbc.gridy = 1;
        JLabel deliveryLabel = new JLabel("Delivery Address");
        deliveryLabel.setFont(new Font("Inter", Font.BOLD, 14));
        deliveryLabel.setForeground(GRAY_700);
        panel.add(deliveryLabel, gbc);
        
        gbc.gridx = 1; gbc.gridy = 2;
        JComboBox<String> deliveryAddress = new JComboBox<>();
        deliveryAddress.addItem("Select Delivery Address");
        deliveryAddress.addItem("Main Warehouse - Kuala Lumpur");
        deliveryAddress.addItem("Branch Office - Penang");
        deliveryAddress.setFont(new Font("Inter", Font.PLAIN, 14));
        deliveryAddress.setPreferredSize(new Dimension(200, 35));
        panel.add(deliveryAddress, gbc);
        
        // Special Instructions
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 4;
        JLabel instructionsLabel = new JLabel("Special Instructions");
        instructionsLabel.setFont(new Font("Inter", Font.BOLD, 14));
        instructionsLabel.setForeground(GRAY_700);
        panel.add(instructionsLabel, gbc);
        
        gbc.gridx = 0; gbc.gridy = 4; gbc.gridwidth = 4; gbc.fill = GridBagConstraints.HORIZONTAL;
        JTextArea instructionsArea = new JTextArea("Enter any special delivery or handling instructions...");
        instructionsArea.setFont(new Font("Inter", Font.PLAIN, 14));
        instructionsArea.setForeground(GRAY_600);
        instructionsArea.setRows(3);
        instructionsArea.setLineWrap(true);
        instructionsArea.setWrapStyleWord(true);
        instructionsArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        panel.add(instructionsArea, gbc);
        
        return panel;
    }
    
    private JPanel createPOItemsSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Section header
        JLabel sectionLabel = new JLabel("Purchase Order Items");
        sectionLabel.setFont(new Font("Inter", Font.BOLD, 18));
        sectionLabel.setForeground(GRAY_900);
        sectionLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        panel.add(sectionLabel, BorderLayout.NORTH);
        
        // Items table
        String[] columnNames = {"Item Code", "Item Name", "Requested Qty", "Approved Qty", "Unit Price (RM)", "Total (RM)", "Actions"};
        Object[][] itemData = {
            {"ITM-001", "Rice (Premium)", "50 kg", "50", "3.50", "175.00", "Remove"},
            {"ITM-002", "Sugar (White)", "30 kg", "30", "2.80", "84.00", "Remove"}
        };
        
        DefaultTableModel itemsModel = new DefaultTableModel(itemData, columnNames) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 3 || column == 4 || column == 6; // Approved Qty, Unit Price, and Actions
            }
        };
        
        JTable itemsTable = new JTable(itemsModel);
        itemsTable.setFont(new Font("Inter", Font.PLAIN, 13));
        itemsTable.setRowHeight(45);
        itemsTable.getTableHeader().setFont(new Font("Inter", Font.BOLD, 13));
        itemsTable.getTableHeader().setBackground(GRAY_50);
        itemsTable.getTableHeader().setForeground(GRAY_700);
        
        // Set column widths
        TableColumnModel columnModel = itemsTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(80);  // Item Code
        columnModel.getColumn(1).setPreferredWidth(150); // Item Name
        columnModel.getColumn(2).setPreferredWidth(100); // Requested Qty
        columnModel.getColumn(3).setPreferredWidth(100); // Approved Qty
        columnModel.getColumn(4).setPreferredWidth(100); // Unit Price
        columnModel.getColumn(5).setPreferredWidth(100); // Total
        columnModel.getColumn(6).setPreferredWidth(80);  // Actions
        
        JScrollPane scrollPane = new JScrollPane(itemsTable);
        scrollPane.setPreferredSize(new Dimension(0, 200));
        scrollPane.setBorder(BorderFactory.createLineBorder(GRAY_200, 1));
        
        // Total section
        JPanel totalPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        totalPanel.setBackground(Color.WHITE);
        totalPanel.setBorder(BorderFactory.createEmptyBorder(15, 0, 0, 0));
        
        JLabel totalLabel = new JLabel("Total Amount:");
        totalLabel.setFont(new Font("Inter", Font.BOLD, 16));
        totalLabel.setForeground(GRAY_700);
        
        JLabel totalAmount = new JLabel("RM 259.00");
        totalAmount.setFont(new Font("Inter", Font.BOLD, 18));
        totalAmount.setForeground(GRAY_900);
        totalAmount.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));
        
        totalPanel.add(totalLabel);
        totalPanel.add(totalAmount);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(totalPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private JPanel createPOActionButtonsForTab() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setBackground(Color.WHITE);
        
        JButton generateBtn = createStyledButton("Generate PO", PRIMARY_BLUE, Color.WHITE);
        generateBtn.setPreferredSize(new Dimension(120, 40));
        generateBtn.addActionListener(e -> {
            // Generate the PO
            String newPoId = "PO-2025-" + String.format("%03d", new Random().nextInt(999) + 1);
            
            // Add to the main table
            Object[] newRow = {newPoId, "PR-2025-002", "ABC Trading Sdn Bhd", currentUser.getName(), 
                             "2025-05-28", "2025-06-01", "RM 259.00", "Medium", "Pending Approval", ""};
            tableModel.addRow(newRow);
            
            // Switch back to View PO tab
            tabbedPane.setSelectedIndex(0);
            
            JOptionPane.showMessageDialog(this, 
                "Purchase Order " + newPoId + " has been generated successfully!\n\n" +
                "The PO is now pending approval and has been added to the Purchase Orders list.",
                "PO Generated", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton saveAsDraftBtn = createStyledButton("Save as Draft", GRAY_600, Color.WHITE);
        saveAsDraftBtn.setPreferredSize(new Dimension(120, 40));
        saveAsDraftBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Purchase Order saved as draft!", 
                                        "Draft Saved", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton previewBtn = createStyledButton("Preview PO", WARNING_ORANGE, Color.WHITE);
        previewBtn.setPreferredSize(new Dimension(120, 40));
        previewBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(this, "Preview functionality will be implemented soon!", 
                                        "Preview PO", JOptionPane.INFORMATION_MESSAGE);
        });
        
        JButton clearBtn = createStyledButton("Clear Form", GRAY_600, Color.WHITE);
        clearBtn.setPreferredSize(new Dimension(100, 40));
        clearBtn.addActionListener(e -> {
            // Clear form functionality can be implemented here
            JOptionPane.showMessageDialog(this, "Form cleared!", 
                                        "Form Cleared", JOptionPane.INFORMATION_MESSAGE);
        });
        
        panel.add(generateBtn);
        panel.add(saveAsDraftBtn);
        panel.add(previewBtn);
        panel.add(clearBtn);
        
        return panel;
    }
    
    // Role checking methods
    private boolean canCreatePO() {
        return currentUser.getRole().equals("ADMIN") ||
               currentUser.getRole().equals("PURCHASE_MANAGER") ||
               currentUser.getRole().equals("PURCHASE_STAFF");
    }
    
    private boolean canApprovePO() {
        return currentUser.getRole().equals("ADMIN") ||
               currentUser.getRole().equals("FINANCE_MANAGER") ||
               currentUser.getRole().equals("PURCHASE_MANAGER");
    }
    
    // Custom renderers and editors
    private class PriorityRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 12));
            label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                String priority = value.toString();
                switch (priority) {
                    case "Urgent":
                        label.setBackground(new Color(254, 226, 226));
                        label.setForeground(new Color(185, 28, 28));
                        break;
                    case "High":
                        label.setBackground(new Color(255, 237, 213));
                        label.setForeground(new Color(194, 65, 12));
                        break;
                    case "Medium":
                        label.setBackground(new Color(254, 249, 195));
                        label.setForeground(new Color(161, 98, 7));
                        break;
                    case "Low":
                        label.setBackground(new Color(220, 252, 231));
                        label.setForeground(new Color(22, 101, 52));
                        break;
                    default:
                        label.setBackground(GRAY_100);
                        label.setForeground(GRAY_700);
                }
            }
            
            return label;
        }
    }
    
    private class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            
            JLabel label = new JLabel(value.toString());
            label.setOpaque(true);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setFont(new Font("Inter", Font.BOLD, 12));
            label.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            } else {
                String status = value.toString();
                switch (status) {
                    case "Pending Approval":
                        label.setBackground(new Color(255, 243, 205));
                        label.setForeground(new Color(146, 64, 14));
                        break;
                    case "Approved":
                        label.setBackground(new Color(220, 252, 231));
                        label.setForeground(new Color(22, 101, 52));
                        break;
                    case "Rejected":
                        label.setBackground(new Color(254, 226, 226));
                        label.setForeground(new Color(185, 28, 28));
                        break;
                    case "Completed":
                        label.setBackground(new Color(219, 234, 254));
                        label.setForeground(new Color(30, 64, 175));
                        break;
                    case "Draft":
                        label.setBackground(GRAY_100);
                        label.setForeground(GRAY_700);
                        break;
                    default:
                        label.setBackground(GRAY_100);
                        label.setForeground(GRAY_700);
                }
            }
            
            return label;
        }
    }
    
    private class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton viewButton;
        private JButton actionButton;
        private JButton rejectButton;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 4, 4));
            setOpaque(true);
            
            viewButton = new JButton("View");
            viewButton.setFont(new Font("Inter", Font.BOLD, 11));
            viewButton.setBackground(PRIMARY_BLUE);
            viewButton.setForeground(Color.WHITE);
            viewButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            viewButton.setFocusPainted(false);
            viewButton.setBorderPainted(false);
            viewButton.setOpaque(true);
            
            actionButton = new JButton();
            actionButton.setFont(new Font("Inter", Font.BOLD, 11));
            actionButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            actionButton.setFocusPainted(false);
            actionButton.setBorderPainted(false);
            actionButton.setOpaque(true);
            
            rejectButton = new JButton("Reject");
            rejectButton.setFont(new Font("Inter", Font.BOLD, 11));
            rejectButton.setBackground(DANGER_RED);
            rejectButton.setForeground(Color.WHITE);
            rejectButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            rejectButton.setFocusPainted(false);
            rejectButton.setBorderPainted(false);
            rejectButton.setOpaque(true);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            
            // Remove all components first
            removeAll();
            
            // Get status from the same row
            String status = table.getValueAt(row, 8).toString();
            
            // Always add view button
            add(viewButton);
            
            if ("Pending Approval".equals(status)) {
                if (canApprovePO()) {
                    actionButton.setText("Approve");
                    actionButton.setBackground(SUCCESS_GREEN);
                    actionButton.setForeground(Color.WHITE);
                    add(actionButton);
                    // Add reject button for pending approval POs if user can approve
                    add(rejectButton);
                } else {
                    actionButton.setText("Edit");
                    actionButton.setBackground(WARNING_ORANGE);
                    actionButton.setForeground(Color.WHITE);
                    add(actionButton);
                }
            } else if ("Approved".equals(status)) {
                actionButton.setText("Print");
                actionButton.setBackground(GRAY_600);
                actionButton.setForeground(Color.WHITE);
                add(actionButton);
            } else if ("Completed".equals(status)) {
                actionButton.setText("Archive");
                actionButton.setBackground(GRAY_600);
                actionButton.setForeground(Color.WHITE);
                add(actionButton);
            } else if ("Rejected".equals(status)) {
                actionButton.setText("Revise");
                actionButton.setBackground(WARNING_ORANGE);
                actionButton.setForeground(Color.WHITE);
                add(actionButton);
            } else {
                actionButton.setText("Edit");
                actionButton.setBackground(WARNING_ORANGE);
                actionButton.setForeground(Color.WHITE);
                add(actionButton);
            }
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(table.getBackground());
            }
            
            return this;
        }
    }
    
    private class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton viewButton;
        private JButton actionButton;
        private JButton rejectButton;
        private int currentRow;
        
        public ActionButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 4));
            panel.setOpaque(true);
            
            viewButton = new JButton("View");
            viewButton.setFont(new Font("Inter", Font.BOLD, 11));
            viewButton.setBackground(PRIMARY_BLUE);
            viewButton.setForeground(Color.WHITE);
            viewButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            viewButton.setFocusPainted(false);
            viewButton.setBorderPainted(false);
            viewButton.setOpaque(true);
            viewButton.addActionListener(e -> {
                fireEditingStopped();
                viewPO(currentRow);
            });
            
            actionButton = new JButton();
            actionButton.setFont(new Font("Inter", Font.BOLD, 11));
            actionButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            actionButton.setFocusPainted(false);
            actionButton.setBorderPainted(false);
            actionButton.setOpaque(true);
            actionButton.addActionListener(e -> {
                fireEditingStopped();
                performAction(currentRow);
            });
            
            rejectButton = new JButton("Reject");
            rejectButton.setFont(new Font("Inter", Font.BOLD, 11));
            rejectButton.setBackground(DANGER_RED);
            rejectButton.setForeground(Color.WHITE);
            rejectButton.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
            rejectButton.setFocusPainted(false);
            rejectButton.setBorderPainted(false);
            rejectButton.setOpaque(true);
            rejectButton.addActionListener(e -> {
                fireEditingStopped();
                rejectPO(currentRow);
            });
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            
            // Remove all components first
            panel.removeAll();
            
            String status = table.getValueAt(row, 8).toString();
            
            // Always add view button
            panel.add(viewButton);
            
            if ("Pending Approval".equals(status)) {
                if (canApprovePO()) {
                    actionButton.setText("Approve");
                    actionButton.setBackground(SUCCESS_GREEN);
                    actionButton.setForeground(Color.WHITE);
                    panel.add(actionButton);
                    // Add reject button for pending approval POs if user can approve
                    panel.add(rejectButton);
                } else {
                    actionButton.setText("Edit");
                    actionButton.setBackground(WARNING_ORANGE);
                    actionButton.setForeground(Color.WHITE);
                    panel.add(actionButton);
                }
            } else if ("Approved".equals(status)) {
                actionButton.setText("Print");
                actionButton.setBackground(GRAY_600);
                actionButton.setForeground(Color.WHITE);
                panel.add(actionButton);
            } else if ("Completed".equals(status)) {
                actionButton.setText("Archive");
                actionButton.setBackground(GRAY_600);
                actionButton.setForeground(Color.WHITE);
                panel.add(actionButton);
            } else if ("Rejected".equals(status)) {
                actionButton.setText("Revise");
                actionButton.setBackground(WARNING_ORANGE);
                actionButton.setForeground(Color.WHITE);
                panel.add(actionButton);
            } else {
                actionButton.setText("Edit");
                actionButton.setBackground(WARNING_ORANGE);
                actionButton.setForeground(Color.WHITE);
                panel.add(actionButton);
            }
            
            panel.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
        
        private void viewPO(int row) {
            String poId = (String) tableModel.getValueAt(row, 0);
            String prReference = (String) tableModel.getValueAt(row, 1);
            String supplier = (String) tableModel.getValueAt(row, 2);
            String createdBy = (String) tableModel.getValueAt(row, 3);
            String totalAmount = (String) tableModel.getValueAt(row, 6);
            String status = (String) tableModel.getValueAt(row, 8);
            
            showPODetailsDialog(poId, prReference, supplier, createdBy, totalAmount, status);
        }
        
        private void performAction(int row) {
            String poId = (String) tableModel.getValueAt(row, 0);
            String status = (String) tableModel.getValueAt(row, 8);
            String action = actionButton.getText();
            
            switch (action) {
                case "Approve":
                    showApprovalDialog(row, poId);
                    break;
                case "Print":
                    printPO(poId);
                    break;
                case "Archive":
                    archivePO(poId);
                    break;
                case "Edit":
                    editPO(row, poId);
                    break;
                case "Revise":
                    revisePO(row, poId);
                    break;
                default:
                    JOptionPane.showMessageDialog(PurchaseOrderPanel.this, 
                        "Unknown action: " + action, 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
            }
        }
        
        private void showApprovalDialog(int row, String poId) {
            String supplier = (String) tableModel.getValueAt(row, 2);
            String createdBy = (String) tableModel.getValueAt(row, 3);
            String totalAmount = (String) tableModel.getValueAt(row, 6);
            String status = (String) tableModel.getValueAt(row, 8);
            
            // Create approval dialog matching the image design
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(PurchaseOrderPanel.this), "Purchase Order Approval", true);
            dialog.setSize(1000, 700);
            dialog.setLocationRelativeTo(PurchaseOrderPanel.this);
            dialog.setLayout(new BorderLayout());
            
            // Main panel with light gray background
            JPanel mainPanel = new JPanel(new BorderLayout());
            mainPanel.setBackground(new Color(245, 245, 245));
            mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
            
            // Title
            JLabel titleLabel = new JLabel("Purchase Order Approval");
            titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
            titleLabel.setForeground(new Color(60, 60, 60));
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            mainPanel.add(titleLabel, BorderLayout.NORTH);
            
            // Content panel
            JPanel contentPanel = new JPanel();
            contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
            contentPanel.setBackground(new Color(245, 245, 245));
            
            // PO Details Section - Two columns layout
            JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 100, 20));
            detailsPanel.setBackground(new Color(245, 245, 245));
            detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
            
            // Left column details
            JLabel poIdLabel = new JLabel("PO ID: " + poId);
            poIdLabel.setFont(new Font("Arial", Font.BOLD, 18));
            poIdLabel.setForeground(new Color(60, 60, 60));
            
            JLabel supplierLabel = new JLabel("Supplier: " + supplier);
            supplierLabel.setFont(new Font("Arial", Font.BOLD, 18));
            supplierLabel.setForeground(new Color(60, 60, 60));
            
            // Right column details
            JLabel totalLabel = new JLabel("Total Amount: " + totalAmount);
            totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
            totalLabel.setForeground(new Color(60, 60, 60));
            
            JLabel createdByLabel = new JLabel("Created By: " + createdBy);
            createdByLabel.setFont(new Font("Arial", Font.BOLD, 18));
            createdByLabel.setForeground(new Color(60, 60, 60));
            
            detailsPanel.add(poIdLabel);
            detailsPanel.add(totalLabel);
            detailsPanel.add(supplierLabel);
            detailsPanel.add(createdByLabel);
            
            contentPanel.add(detailsPanel);
            
            // Items table with Edit buttons
            JPanel itemsPanel = new JPanel(new BorderLayout());
            itemsPanel.setBackground(new Color(245, 245, 245));
            itemsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
            
            // Table data matching the image
            String[] itemColumns = {"Item", "Quantity", "Unit Price", "Total", "Modify"};
            Object[][] itemData = {
                {"Rice (Premium)", "100 kg", "RM 3.50", "RM 350.00", ""},
                {"Sugar (White)", "80 kg", "RM 2.80", "RM 224.00", ""}
            };
            
            DefaultTableModel itemTableModel = new DefaultTableModel(itemData, itemColumns) {
                @Override
                public boolean isCellEditable(int row, int column) {
                    return column == 4; // Only Edit column is editable
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
            itemColumnModel.getColumn(0).setPreferredWidth(200); // Item
            itemColumnModel.getColumn(1).setPreferredWidth(120); // Quantity
            itemColumnModel.getColumn(2).setPreferredWidth(120); // Unit Price
            itemColumnModel.getColumn(3).setPreferredWidth(120); // Total
            itemColumnModel.getColumn(4).setPreferredWidth(100); // Modify
            
            // Custom renderer and editor for Edit buttons
            itemsTable.getColumnModel().getColumn(4).setCellRenderer(new EditButtonRenderer());
            itemsTable.getColumnModel().getColumn(4).setCellEditor(new EditButtonEditor(itemTableModel));
            
            JScrollPane itemsScroll = new JScrollPane(itemsTable);
            itemsScroll.setPreferredSize(new Dimension(0, 150));
            itemsScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
            itemsScroll.setBackground(Color.WHITE);
            
            itemsPanel.add(itemsScroll, BorderLayout.CENTER);
            contentPanel.add(itemsPanel);
            
            // Approval Comments Section
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
            
            // Approve button (green)
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
                // Find the row in the main table and update status
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(poId)) {
                        tableModel.setValueAt("Approved", i, 8);
                        break;
                    }
                }
                JOptionPane.showMessageDialog(dialog, "Purchase Order " + poId + " has been approved!", 
                                            "Approval Successful", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            });
            
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
                // Find the row in the main table and update status
                for (int i = 0; i < tableModel.getRowCount(); i++) {
                    if (tableModel.getValueAt(i, 0).equals(poId)) {
                        tableModel.setValueAt("Rejected", i, 8);
                        break;
                    }
                }
                JOptionPane.showMessageDialog(dialog, "Purchase Order " + poId + " has been rejected!", 
                                            "Rejection Successful", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            });
            
            // Request Modification button (orange)
            JButton modifyBtn = new JButton("Request Modification");
            modifyBtn.setFont(new Font("Arial", Font.BOLD, 16));
            modifyBtn.setBackground(new Color(240, 173, 78));
            modifyBtn.setForeground(Color.WHITE);
            modifyBtn.setPreferredSize(new Dimension(200, 45));
            modifyBtn.setBorder(BorderFactory.createEmptyBorder());
            modifyBtn.setFocusPainted(false);
            modifyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            modifyBtn.setOpaque(true);
            modifyBtn.setBorderPainted(false);
            modifyBtn.addActionListener(e -> {
                JOptionPane.showMessageDialog(dialog, "Modification request sent for Purchase Order " + poId + "!", 
                                            "Modification Requested", JOptionPane.INFORMATION_MESSAGE);
                dialog.dispose();
            });
            
            // Cancel button (gray)
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setFont(new Font("Arial", Font.BOLD, 16));
            cancelBtn.setBackground(new Color(150, 150, 150));
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setPreferredSize(new Dimension(120, 45));
            cancelBtn.setBorder(BorderFactory.createEmptyBorder());
            cancelBtn.setFocusPainted(false);
            cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
            cancelBtn.setOpaque(true);
            cancelBtn.setBorderPainted(false);
            cancelBtn.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(approveBtn);
            buttonPanel.add(rejectBtn);
            buttonPanel.add(modifyBtn);
            buttonPanel.add(cancelBtn);
            
            mainPanel.add(buttonPanel, BorderLayout.SOUTH);
            
            dialog.add(mainPanel);
            dialog.setVisible(true);
        }
        
        private void printPO(String poId) {
            JOptionPane.showMessageDialog(PurchaseOrderPanel.this, 
                "Print functionality for PO " + poId + " will be implemented soon!", 
                "Print Purchase Order", JOptionPane.INFORMATION_MESSAGE);
        }
        
        private void archivePO(String poId) {
            JOptionPane.showMessageDialog(PurchaseOrderPanel.this, 
                "Archive functionality for PO " + poId + " will be implemented soon!", 
                "Archive Purchase Order", JOptionPane.INFORMATION_MESSAGE);
        }
        
        private void rejectPO(int row) {
            String poId = (String) tableModel.getValueAt(row, 0);
            String status = (String) tableModel.getValueAt(row, 8);
            
            if ("Pending Approval".equals(status)) {
                // Show rejection reason dialog
                String reason = JOptionPane.showInputDialog(PurchaseOrderPanel.this,
                    "Please provide a reason for rejecting PO " + poId + ":",
                    "Rejection Reason",
                    JOptionPane.QUESTION_MESSAGE);
                
                if (reason != null && !reason.trim().isEmpty()) {
                    int result = JOptionPane.showConfirmDialog(PurchaseOrderPanel.this,
                        "Are you sure you want to reject Purchase Order " + poId + "?\n\n" +
                        "Reason: " + reason + "\n\n" +
                        "This action will change the status to 'Rejected'.",
                        "Confirm Rejection",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);
                    
                    if (result == JOptionPane.YES_OPTION) {
                        // Update status to "Rejected"
                        tableModel.setValueAt("Rejected", row, 8);
                        poTable.repaint();
                        
                        JOptionPane.showMessageDialog(PurchaseOrderPanel.this,
                            "Purchase Order " + poId + " has been rejected.\n\n" +
                            "Reason: " + reason + "\n\n" +
                            "The requester will be notified of the rejection.",
                            "Rejection Successful",
                            JOptionPane.INFORMATION_MESSAGE);
                    }
                } else if (reason != null) {
                    JOptionPane.showMessageDialog(PurchaseOrderPanel.this,
                        "A rejection reason is required.",
                        "Missing Information",
                        JOptionPane.WARNING_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(PurchaseOrderPanel.this,
                    "This Purchase Order is not pending approval. Cannot reject.\n\n" +
                    "Current status: " + status,
                    "Rejection Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    
    // Custom renderer and editor for Edit buttons in approval dialog
    private class EditButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton;
        
        public EditButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 0, 0));
            setOpaque(true);
            
            editButton = new JButton("Edit");
            editButton.setFont(new Font("Arial", Font.BOLD, 12));
            editButton.setBackground(new Color(240, 173, 78));
            editButton.setForeground(Color.WHITE);
            editButton.setPreferredSize(new Dimension(60, 30));
            editButton.setBorder(BorderFactory.createEmptyBorder());
            editButton.setFocusPainted(false);
            
            add(editButton);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }
            
            return this;
        }
    }
    
    private class EditButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton editButton;
        private DefaultTableModel tableModel;
        private int currentRow;
        
        public EditButtonEditor(DefaultTableModel model) {
            this.tableModel = model;
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
            panel.setOpaque(true);
            
            editButton = new JButton("Edit");
            editButton.setFont(new Font("Arial", Font.BOLD, 12));
            editButton.setBackground(new Color(240, 173, 78));
            editButton.setForeground(Color.WHITE);
            editButton.setPreferredSize(new Dimension(60, 30));
            editButton.setBorder(BorderFactory.createEmptyBorder());
            editButton.setFocusPainted(false);
            editButton.addActionListener(e -> {
                fireEditingStopped();
                editItem(currentRow);
            });
            
            panel.add(editButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            panel.setBackground(table.getSelectionBackground());
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
        
        private void editItem(int row) {
            String itemName = (String) tableModel.getValueAt(row, 0);
            String quantity = (String) tableModel.getValueAt(row, 1);
            String unitPrice = (String) tableModel.getValueAt(row, 2);
            String total = (String) tableModel.getValueAt(row, 3);
            
            // Create edit dialog
            JDialog editDialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(PurchaseOrderPanel.this), "Edit Item", true);
            editDialog.setSize(400, 300);
            editDialog.setLocationRelativeTo(PurchaseOrderPanel.this);
            
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
            
            // Title
            JLabel titleLabel = new JLabel("Edit Item: " + itemName);
            titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
            mainPanel.add(titleLabel);
            
            // Form fields
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 10, 0);
            gbc.anchor = GridBagConstraints.WEST;
            
            // Quantity field
            gbc.gridx = 0; gbc.gridy = 0;
            JLabel quantityLabel = new JLabel("Quantity:");
            quantityLabel.setFont(new Font("Arial", Font.BOLD, 14));
            formPanel.add(quantityLabel, gbc);
            
            gbc.gridx = 1;
            JTextField quantityField = new JTextField(quantity.replace(" kg", ""));
            quantityField.setPreferredSize(new Dimension(150, 30));
            quantityField.setFont(new Font("Arial", Font.PLAIN, 14));
            formPanel.add(quantityField, gbc);
            
            // Unit Price field
            gbc.gridx = 0; gbc.gridy = 1;
            JLabel priceLabel = new JLabel("Unit Price (RM):");
            priceLabel.setFont(new Font("Arial", Font.BOLD, 14));
            formPanel.add(priceLabel, gbc);
            
            gbc.gridx = 1;
            JTextField priceField = new JTextField(unitPrice.replace("RM ", ""));
            priceField.setPreferredSize(new Dimension(150, 30));
            priceField.setFont(new Font("Arial", Font.PLAIN, 14));
            formPanel.add(priceField, gbc);
            
            mainPanel.add(formPanel);
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setBackground(Color.WHITE);
            buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
            
            JButton saveBtn = new JButton("Save");
            saveBtn.setFont(new Font("Arial", Font.BOLD, 14));
            saveBtn.setBackground(new Color(92, 184, 92));
            saveBtn.setForeground(Color.WHITE);
            saveBtn.setPreferredSize(new Dimension(80, 35));
            saveBtn.setBorder(BorderFactory.createEmptyBorder());
            saveBtn.setFocusPainted(false);
            saveBtn.addActionListener(e -> {
                try {
                    String newQuantity = quantityField.getText().trim();
                    String newPrice = priceField.getText().trim();
                    
                    if (newQuantity.isEmpty() || newPrice.isEmpty()) {
                        JOptionPane.showMessageDialog(editDialog, "Please fill in all fields.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    
                    double qty = Double.parseDouble(newQuantity);
                    double price = Double.parseDouble(newPrice);
                    double newTotal = qty * price;
                    
                    // Update table
                    tableModel.setValueAt(newQuantity + " kg", row, 1);
                    tableModel.setValueAt("RM " + String.format("%.2f", price), row, 2);
                    tableModel.setValueAt("RM " + String.format("%.2f", newTotal), row, 3);
                    
                    JOptionPane.showMessageDialog(editDialog, "Item updated successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                    editDialog.dispose();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(editDialog, "Please enter valid numbers for quantity and price.", "Validation Error", JOptionPane.ERROR_MESSAGE);
                }
            });
            
            JButton cancelBtn = new JButton("Cancel");
            cancelBtn.setFont(new Font("Arial", Font.BOLD, 14));
            cancelBtn.setBackground(new Color(150, 150, 150));
            cancelBtn.setForeground(Color.WHITE);
            cancelBtn.setPreferredSize(new Dimension(80, 35));
            cancelBtn.setBorder(BorderFactory.createEmptyBorder());
            cancelBtn.setFocusPainted(false);
            cancelBtn.addActionListener(e -> editDialog.dispose());
            
            buttonPanel.add(saveBtn);
            buttonPanel.add(cancelBtn);
            
            mainPanel.add(buttonPanel);
            
            editDialog.add(mainPanel);
            editDialog.setVisible(true);
        }
    }
    
    private void showPODetailsDialog(String poId, String prReference, String supplier, String createdBy, String totalAmount, String status) {
        // Create Purchase Order Approval dialog matching the image design
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Purchase Order Approval", true);
        dialog.setSize(1000, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());
        
        // Main panel with light gray background
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(new Color(245, 245, 245));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(40, 50, 40, 50));
        
        // Title
        JLabel titleLabel = new JLabel("Purchase Order Approval");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 28));
        titleLabel.setForeground(new Color(60, 60, 60));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(new Color(245, 245, 245));
        
        // PO Details Section - Two columns layout
        JPanel detailsPanel = new JPanel(new GridLayout(2, 2, 100, 20));
        detailsPanel.setBackground(new Color(245, 245, 245));
        detailsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        
        // Left column details
        JLabel poIdLabel = new JLabel("PO ID: " + poId);
        poIdLabel.setFont(new Font("Arial", Font.BOLD, 18));
        poIdLabel.setForeground(new Color(60, 60, 60));
        
        JLabel supplierLabel = new JLabel("Supplier: " + supplier);
        supplierLabel.setFont(new Font("Arial", Font.BOLD, 18));
        supplierLabel.setForeground(new Color(60, 60, 60));
        
        // Right column details
        JLabel totalLabel = new JLabel("Total Amount: " + totalAmount);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        totalLabel.setForeground(new Color(60, 60, 60));
        
        JLabel createdByLabel = new JLabel("Created By: " + createdBy);
        createdByLabel.setFont(new Font("Arial", Font.BOLD, 18));
        createdByLabel.setForeground(new Color(60, 60, 60));
        
        detailsPanel.add(poIdLabel);
        detailsPanel.add(totalLabel);
        detailsPanel.add(supplierLabel);
        detailsPanel.add(createdByLabel);
        
        contentPanel.add(detailsPanel);
        
        // Items table with Edit buttons
        JPanel itemsPanel = new JPanel(new BorderLayout());
        itemsPanel.setBackground(new Color(245, 245, 245));
        itemsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Table data matching the image
        String[] itemColumns = {"Item", "Quantity", "Unit Price", "Total", "Modify"};
        Object[][] itemData = {
            {"Rice (Premium)", "100 kg", "RM 3.50", "RM 350.00", ""},
            {"Sugar (White)", "80 kg", "RM 2.80", "RM 224.00", ""}
        };
        
        DefaultTableModel itemTableModel = new DefaultTableModel(itemData, itemColumns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 4; // Only Edit column is editable
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
        itemColumnModel.getColumn(0).setPreferredWidth(200); // Item
        itemColumnModel.getColumn(1).setPreferredWidth(120); // Quantity
        itemColumnModel.getColumn(2).setPreferredWidth(120); // Unit Price
        itemColumnModel.getColumn(3).setPreferredWidth(120); // Total
        itemColumnModel.getColumn(4).setPreferredWidth(100); // Modify
        
        // Custom renderer and editor for Edit buttons
        itemsTable.getColumnModel().getColumn(4).setCellRenderer(new EditButtonRenderer());
        itemsTable.getColumnModel().getColumn(4).setCellEditor(new EditButtonEditor(itemTableModel));
        
        JScrollPane itemsScroll = new JScrollPane(itemsTable);
        itemsScroll.setPreferredSize(new Dimension(0, 150));
        itemsScroll.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200), 1));
        itemsScroll.setBackground(Color.WHITE);
        
        itemsPanel.add(itemsScroll, BorderLayout.CENTER);
        contentPanel.add(itemsPanel);
        
        // Approval Comments Section
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
        
        // Approve button (green)
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
            // Find the row in the main table and update status
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(poId)) {
                    tableModel.setValueAt("Approved", i, 8);
                    break;
                }
            }
            JOptionPane.showMessageDialog(dialog, "Purchase Order " + poId + " has been approved!", 
                                        "Approval Successful", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
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
            // Find the row in the main table and update status
            for (int i = 0; i < tableModel.getRowCount(); i++) {
                if (tableModel.getValueAt(i, 0).equals(poId)) {
                    tableModel.setValueAt("Rejected", i, 8);
                    break;
                }
            }
            JOptionPane.showMessageDialog(dialog, "Purchase Order " + poId + " has been rejected!", 
                                        "Rejection Successful", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        // Request Modification button (orange)
        JButton modifyBtn = new JButton("Request Modification");
        modifyBtn.setFont(new Font("Arial", Font.BOLD, 16));
        modifyBtn.setBackground(new Color(240, 173, 78));
        modifyBtn.setForeground(Color.WHITE);
        modifyBtn.setPreferredSize(new Dimension(200, 45));
        modifyBtn.setBorder(BorderFactory.createEmptyBorder());
        modifyBtn.setFocusPainted(false);
        modifyBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        modifyBtn.setOpaque(true);
        modifyBtn.setBorderPainted(false);
        modifyBtn.addActionListener(e -> {
            JOptionPane.showMessageDialog(dialog, "Modification request sent for Purchase Order " + poId + "!", 
                                        "Modification Requested", JOptionPane.INFORMATION_MESSAGE);
            dialog.dispose();
        });
        
        // Cancel button (gray)
        JButton cancelBtn = new JButton("Cancel");
        cancelBtn.setFont(new Font("Arial", Font.BOLD, 16));
        cancelBtn.setBackground(new Color(150, 150, 150));
        cancelBtn.setForeground(Color.WHITE);
        cancelBtn.setPreferredSize(new Dimension(120, 45));
        cancelBtn.setBorder(BorderFactory.createEmptyBorder());
        cancelBtn.setFocusPainted(false);
        cancelBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        cancelBtn.setOpaque(true);
        cancelBtn.setBorderPainted(false);
        cancelBtn.addActionListener(e -> dialog.dispose());
        
        buttonPanel.add(approveBtn);
        buttonPanel.add(rejectBtn);
        buttonPanel.add(modifyBtn);
        buttonPanel.add(cancelBtn);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private void editPO(int row, String poId) {
        int result = JOptionPane.showConfirmDialog(PurchaseOrderPanel.this,
            "Do you want to edit Purchase Order " + poId + "?\n\n" +
            "This will open the edit form where you can modify the details.",
            "Edit Purchase Order",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE);
        
        if (result == JOptionPane.YES_OPTION) {
            JOptionPane.showMessageDialog(PurchaseOrderPanel.this,
                "Opening edit form for PO: " + poId + "\n\n" +
                "In a full implementation, this would:\n" +
                "• Open the Purchase Order form\n" +
                "• Pre-populate with existing data\n" +
                "• Allow modifications\n" +
                "• Save changes to database",
                "Edit Mode",
                JOptionPane.INFORMATION_MESSAGE);
        }
    }
    
    private void revisePO(int row, String poId) {
        String[] options = {"Create New Version", "Modify Existing", "Cancel"};
        int choice = JOptionPane.showOptionDialog(PurchaseOrderPanel.this,
            "How would you like to revise PO " + poId + "?\n\n" +
            "Create New Version: Creates a new PO based on this one\n" +
            "Modify Existing: Updates the current PO",
            "Revise Purchase Order",
            JOptionPane.YES_NO_CANCEL_OPTION,
            JOptionPane.QUESTION_MESSAGE,
            null,
            options,
            options[0]);
        
        switch (choice) {
            case 0: // Create New Version
                String newPoId = "PO-" + java.time.LocalDate.now().toString() + "-" + 
                               String.format("%03d", new java.util.Random().nextInt(999) + 1);
                JOptionPane.showMessageDialog(PurchaseOrderPanel.this,
                    "New Purchase Order Created!\n\n" +
                    "New PO ID: " + newPoId + "\n" +
                    "Based on: " + poId + "\n\n" +
                    "The new PO is ready for editing and submission.",
                    "New PO Created",
                    JOptionPane.INFORMATION_MESSAGE);
                break;
                
            case 1: // Modify Existing
                JOptionPane.showMessageDialog(PurchaseOrderPanel.this,
                    "Opening revision form for PO: " + poId + "\n\n" +
                    "In a full implementation, this would:\n" +
                    "• Open the PO form in revision mode\n" +
                    "• Allow modifications to address rejection reasons\n" +
                    "• Reset status to 'Pending Approval' after submission\n" +
                    "• Maintain revision history",
                    "Revision Mode",
                    JOptionPane.INFORMATION_MESSAGE);
                
                // Update status to "Pending Approval" (in real app, this would update database)
                tableModel.setValueAt("Pending Approval", row, 8);
                poTable.repaint();
                break;
                
            default: // Cancel
                break;
        }
    }
} 