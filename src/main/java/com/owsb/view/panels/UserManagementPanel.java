package com.owsb.view.panels;

import com.owsb.model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UserManagementPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> userTypeFilter;
    private JComboBox<String> statusFilter;
    
    // Modern UI Colors
    private static final Color PRIMARY_BLUE = new Color(37, 99, 235);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color WARNING_ORANGE = new Color(245, 158, 11);
    private static final Color DANGER_RED = new Color(239, 68, 68);
    private static final Color GRAY_50 = new Color(249, 250, 251);
    private static final Color GRAY_100 = new Color(243, 244, 246);
    private static final Color GRAY_200 = new Color(229, 231, 235);
    private static final Color GRAY_300 = new Color(209, 213, 219);
    private static final Color GRAY_400 = new Color(156, 163, 175);
    private static final Color GRAY_500 = new Color(107, 114, 128);
    private static final Color GRAY_600 = new Color(75, 85, 99);
    private static final Color GRAY_700 = new Color(55, 65, 81);
    private static final Color GRAY_900 = new Color(17, 24, 39);
    
    // List of all users
    private List<User> users;
    
    public UserManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        initializeComponents();
        setupUI();
        loadUsers();
    }
    
    private void initializeComponents() {
        // Search field
        searchField = new JTextField("Search users...");
        searchField.setForeground(GRAY_500);
        
        // Filter dropdowns
        userTypeFilter = new JComboBox<>(new String[]{
            "All User Types", "ADMIN", "PURCHASE_MANAGER", "PURCHASE_STAFF",
            "SALES_MANAGER", "SALES_STAFF", "FINANCE_MANAGER", "FINANCE_STAFF",
            "INVENTORY_MANAGER", "INVENTORY_STAFF"
        });
        
        statusFilter = new JComboBox<>(new String[]{"All Status", "Active", "Inactive"});
        
        // Setup search field placeholder behavior
        setupSearchFieldPlaceholder();
        
        // Initialize table
        String[] columnNames = {"User ID", "Name", "Email", "User Type", "Department", "Created Date", "Status", "Actions"};
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 7; // Only Actions column is editable
            }
        };
        
        userTable = new JTable(tableModel);
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
        
        // Content panel
        JPanel contentPanel = createContentPanel();
        mainContainer.add(contentPanel, BorderLayout.CENTER);
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        
        // Title section
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(GRAY_900);
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Section header with Add New User button
        JPanel sectionHeader = createSectionHeader();
        panel.add(sectionHeader, BorderLayout.NORTH);
        
        // Filters and table panel
        JPanel filtersAndTablePanel = createFiltersAndTablePanel();
        panel.add(filtersAndTablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSectionHeader() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        JLabel sectionTitle = new JLabel("User Management");
        sectionTitle.setFont(new Font("Inter", Font.BOLD, 24));
        sectionTitle.setForeground(GRAY_900);
        
        // Add New User button
        JButton addButton = createStyledButton("Add New User", PRIMARY_BLUE, Color.WHITE);
        addButton.setPreferredSize(new Dimension(140, 40));
        addButton.addActionListener(e -> showAddUserDialog());
        
        panel.add(sectionTitle, BorderLayout.WEST);
        panel.add(addButton, BorderLayout.EAST);
        
        return panel;
    }
    
    private JPanel createFiltersAndTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        // Filters row
        JPanel filtersRow = createFiltersRow();
        panel.add(filtersRow, BorderLayout.NORTH);
        
        // Table panel
        JPanel tablePanel = createTablePanel();
        panel.add(tablePanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createFiltersRow() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 0));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 24, 0));
        
        // Search field
        searchField.setPreferredSize(new Dimension(300, 40));
        searchField.setFont(new Font("Inter", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)
        ));
        
        // User Type filter
        userTypeFilter.setPreferredSize(new Dimension(160, 40));
        userTypeFilter.setFont(new Font("Inter", Font.PLAIN, 14));
        userTypeFilter.setBorder(BorderFactory.createLineBorder(GRAY_300, 1));
        userTypeFilter.setBackground(Color.WHITE);
        
        // Status filter
        statusFilter.setPreferredSize(new Dimension(120, 40));
        statusFilter.setFont(new Font("Inter", Font.PLAIN, 14));
        statusFilter.setBorder(BorderFactory.createLineBorder(GRAY_300, 1));
        statusFilter.setBackground(Color.WHITE);
        
        panel.add(searchField);
        panel.add(userTypeFilter);
        panel.add(statusFilter);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        
        JScrollPane scrollPane = new JScrollPane(userTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(GRAY_200, 1));
        scrollPane.setBackground(Color.WHITE);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        return panel;
    }
    
    private void setupTable() {
        userTable.setFont(new Font("Inter", Font.PLAIN, 14));
        userTable.setRowHeight(60);
        userTable.setGridColor(GRAY_200);
        userTable.setShowGrid(true);
        userTable.setIntercellSpacing(new Dimension(1, 1));
        userTable.setSelectionBackground(GRAY_50);
        userTable.setSelectionForeground(GRAY_900);
        
        // Header styling
        JTableHeader header = userTable.getTableHeader();
        header.setFont(new Font("Inter", Font.BOLD, 14));
        header.setBackground(GRAY_50);
        header.setForeground(GRAY_700);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, GRAY_200));
        header.setPreferredSize(new Dimension(0, 50));
        
        // Column widths
        TableColumnModel columnModel = userTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120); // User ID
        columnModel.getColumn(1).setPreferredWidth(150); // Name
        columnModel.getColumn(2).setPreferredWidth(200); // Email
        columnModel.getColumn(3).setPreferredWidth(140); // User Type
        columnModel.getColumn(4).setPreferredWidth(120); // Department
        columnModel.getColumn(5).setPreferredWidth(120); // Created Date
        columnModel.getColumn(6).setPreferredWidth(100); // Status
        columnModel.getColumn(7).setPreferredWidth(160); // Actions
        
        // Custom renderers
        userTable.getColumnModel().getColumn(6).setCellRenderer(new StatusRenderer());
        userTable.getColumnModel().getColumn(7).setCellRenderer(new ActionButtonRenderer());
        userTable.getColumnModel().getColumn(7).setCellEditor(new ActionButtonEditor());
    }
    
    private void setupSearchFieldPlaceholder() {
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search users...")) {
                    searchField.setText("");
                    searchField.setForeground(GRAY_900);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search users...");
                    searchField.setForeground(GRAY_500);
                }
            }
        });
        
        // Add search functionality
        searchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                filterUsers();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                filterUsers();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                filterUsers();
            }
        });
        
        // Add filter functionality
        userTypeFilter.addActionListener(e -> filterUsers());
        statusFilter.addActionListener(e -> filterUsers());
    }
    
    private void filterUsers() {
        String searchText = searchField.getText().trim();
        if (searchText.equals("Search users...")) {
            searchText = "";
        }
        
        String selectedUserType = (String) userTypeFilter.getSelectedItem();
        String selectedStatus = (String) statusFilter.getSelectedItem();
        
        // Clear existing data
        tableModel.setRowCount(0);
        
        // Filter and add users
        for (User user : users) {
            boolean matchesSearch = searchText.isEmpty() || 
                user.getUserId().toLowerCase().contains(searchText.toLowerCase()) ||
                user.getName().toLowerCase().contains(searchText.toLowerCase()) ||
                user.getUsername().toLowerCase().contains(searchText.toLowerCase());
            
            boolean matchesUserType = selectedUserType.equals("All User Types") || 
                user.getRole().equals(selectedUserType);
            
            // For demo purposes, assume all users are active
            boolean matchesStatus = selectedStatus.equals("All Status") || 
                selectedStatus.equals("Active");
            
            if (matchesSearch && matchesUserType && matchesStatus) {
                Object[] row = {
                    user.getUserId(),
                    user.getName(),
                    user.getUsername() + "@owsb.com", // Generate email
                    formatUserType(user.getRole()),
                    getDepartmentFromRole(user.getRole()),
                    "2025-05-20", // Demo date
                    "Active", // Demo status
                    "" // Actions column
                };
                tableModel.addRow(row);
            }
        }
    }
    
    private void loadUsers() {
        // Load users from model
        users = User.getAllUsers();
        
        // Clear table
        tableModel.setRowCount(0);
        
        // Add users to table
        for (User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getName(),
                user.getUsername() + "@owsb.com", // Generate email for demo
                formatUserType(user.getRole()),
                getDepartmentFromRole(user.getRole()),
                "2025-05-20", // Demo created date
                "Active", // Demo status
                "" // Actions column
            };
            tableModel.addRow(row);
        }
    }
    
    private String formatUserType(String role) {
        switch (role) {
            case "ADMIN": return "Admin";
            case "PURCHASE_MANAGER": return "Purchase Manager";
            case "PURCHASE_STAFF": return "Purchase Staff";
            case "SALES_MANAGER": return "Sales Manager";
            case "SALES_STAFF": return "Sales Staff";
            case "FINANCE_MANAGER": return "Finance Manager";
            case "FINANCE_STAFF": return "Finance Staff";
            case "INVENTORY_MANAGER": return "Inventory Manager";
            case "INVENTORY_STAFF": return "Inventory Staff";
            default: return role;
        }
    }
    
    private String getDepartmentFromRole(String role) {
        if (role.contains("PURCHASE")) return "Purchase";
        if (role.contains("SALES")) return "Sales";
        if (role.contains("FINANCE")) return "Finance";
        if (role.contains("INVENTORY")) return "Inventory";
        if (role.equals("ADMIN")) return "Administration";
        return "Operations";
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
                } else if (bgColor == WARNING_ORANGE) {
                    button.setBackground(new Color(217, 119, 6)); // Darker orange
                } else if (bgColor == DANGER_RED) {
                    button.setBackground(new Color(220, 38, 38)); // Darker red
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
    
    private void showAddUserDialog() {
        JOptionPane.showMessageDialog(this,
            "Add New User functionality is available in the User Registration panel.\n\n" +
            "Please navigate to 'User Registration' from the sidebar to create new users.",
            "Add New User",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    // Custom renderers and editors
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
                if ("Active".equals(status)) {
                    label.setBackground(new Color(220, 252, 231));
                    label.setForeground(new Color(22, 101, 52));
                } else if ("Inactive".equals(status)) {
                    label.setBackground(new Color(254, 226, 226));
                    label.setForeground(new Color(185, 28, 28));
                } else {
                    label.setBackground(GRAY_100);
                    label.setForeground(GRAY_700);
                }
            }
            
            return label;
        }
    }
    
    private class ActionButtonRenderer extends JPanel implements TableCellRenderer {
        private JButton editButton;
        private JButton actionButton;
        
        public ActionButtonRenderer() {
            setLayout(new FlowLayout(FlowLayout.CENTER, 8, 8));
            setOpaque(true);
            
            editButton = new JButton("Edit");
            editButton.setFont(new Font("Inter", Font.BOLD, 12));
            editButton.setBackground(WARNING_ORANGE);
            editButton.setForeground(Color.WHITE);
            editButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            editButton.setFocusPainted(false);
            editButton.setBorderPainted(false);
            editButton.setOpaque(true);
            
            actionButton = new JButton();
            actionButton.setFont(new Font("Inter", Font.BOLD, 12));
            actionButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            actionButton.setFocusPainted(false);
            actionButton.setBorderPainted(false);
            actionButton.setOpaque(true);
            
            add(editButton);
            add(actionButton);
        }
        
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected,
                boolean hasFocus, int row, int column) {
            
            if (isSelected) {
                setBackground(table.getSelectionBackground());
            } else {
                setBackground(Color.WHITE);
            }
            
            // Get status from the table to determine action button
            String status = (String) table.getValueAt(row, 6);
            
            if ("Active".equals(status)) {
                actionButton.setText("Deactivate");
                actionButton.setBackground(DANGER_RED);
                actionButton.setForeground(Color.WHITE);
            } else {
                actionButton.setText("Activate");
                actionButton.setBackground(SUCCESS_GREEN);
                actionButton.setForeground(Color.WHITE);
            }
            
            return this;
        }
    }
    
    private class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private JButton editButton;
        private JButton actionButton;
        private int currentRow;
        
        public ActionButtonEditor() {
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 8));
            panel.setOpaque(true);
            
            editButton = new JButton("Edit");
            editButton.setFont(new Font("Inter", Font.BOLD, 12));
            editButton.setBackground(WARNING_ORANGE);
            editButton.setForeground(Color.WHITE);
            editButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            editButton.setFocusPainted(false);
            editButton.setBorderPainted(false);
            editButton.setOpaque(true);
            editButton.addActionListener(e -> {
                fireEditingStopped();
                editUser(currentRow);
            });
            
            actionButton = new JButton();
            actionButton.setFont(new Font("Inter", Font.BOLD, 12));
            actionButton.setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
            actionButton.setFocusPainted(false);
            actionButton.setBorderPainted(false);
            actionButton.setOpaque(true);
            actionButton.addActionListener(e -> {
                fireEditingStopped();
                toggleUserStatus(currentRow);
            });
            
            panel.add(editButton);
            panel.add(actionButton);
        }
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            currentRow = row;
            
            panel.setBackground(table.getSelectionBackground());
            
            // Get status from the table to determine action button
            String status = (String) table.getValueAt(row, 6);
            
            if ("Active".equals(status)) {
                actionButton.setText("Deactivate");
                actionButton.setBackground(DANGER_RED);
                actionButton.setForeground(Color.WHITE);
            } else {
                actionButton.setText("Activate");
                actionButton.setBackground(SUCCESS_GREEN);
                actionButton.setForeground(Color.WHITE);
            }
            
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return "";
        }
        
        private void editUser(int row) {
            String userId = (String) tableModel.getValueAt(row, 0);
            String userName = (String) tableModel.getValueAt(row, 1);
            String userEmail = (String) tableModel.getValueAt(row, 2);
            String userType = (String) tableModel.getValueAt(row, 3);
            String department = (String) tableModel.getValueAt(row, 4);
            
            // Find the actual user object
            User userToEdit = null;
            for (User user : users) {
                if (user.getUserId().equals(userId)) {
                    userToEdit = user;
                    break;
                }
            }
            
            if (userToEdit == null) {
                JOptionPane.showMessageDialog(UserManagementPanel.this,
                    "User not found!",
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            // Create edit dialog
            showEditUserDialog(userToEdit, row);
        }
        
        private void showEditUserDialog(User user, int tableRow) {
            JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(UserManagementPanel.this), "Edit User", true);
            dialog.setSize(500, 600);
            dialog.setLocationRelativeTo(UserManagementPanel.this);
            dialog.setLayout(new BorderLayout());
            
            // Main panel
            JPanel mainPanel = new JPanel();
            mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
            mainPanel.setBackground(Color.WHITE);
            mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
            
            // Title
            JLabel titleLabel = new JLabel("Edit User Information");
            titleLabel.setFont(new Font("Inter", Font.BOLD, 24));
            titleLabel.setForeground(GRAY_900);
            titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            mainPanel.add(titleLabel);
            mainPanel.add(Box.createVerticalStrut(30));
            
            // Form fields
            JPanel formPanel = new JPanel(new GridBagLayout());
            formPanel.setBackground(Color.WHITE);
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(10, 0, 10, 0);
            gbc.anchor = GridBagConstraints.WEST;
            
            // User ID (read-only)
            gbc.gridx = 0; gbc.gridy = 0;
            JLabel userIdLabel = new JLabel("User ID:");
            userIdLabel.setFont(new Font("Inter", Font.BOLD, 14));
            formPanel.add(userIdLabel, gbc);
            
            gbc.gridx = 1;
            JTextField userIdField = new JTextField(user.getUserId());
            userIdField.setPreferredSize(new Dimension(250, 35));
            userIdField.setEditable(false);
            userIdField.setBackground(GRAY_100);
            formPanel.add(userIdField, gbc);
            
            // Full Name
            gbc.gridx = 0; gbc.gridy = 1;
            JLabel nameLabel = new JLabel("Full Name:");
            nameLabel.setFont(new Font("Inter", Font.BOLD, 14));
            formPanel.add(nameLabel, gbc);
            
            gbc.gridx = 1;
            JTextField nameField = new JTextField(user.getName());
            nameField.setPreferredSize(new Dimension(250, 35));
            nameField.setFont(new Font("Inter", Font.PLAIN, 14));
            formPanel.add(nameField, gbc);
            
            // Username
            gbc.gridx = 0; gbc.gridy = 2;
            JLabel usernameLabel = new JLabel("Username:");
            usernameLabel.setFont(new Font("Inter", Font.BOLD, 14));
            formPanel.add(usernameLabel, gbc);
            
            gbc.gridx = 1;
            JTextField usernameField = new JTextField(user.getUsername());
            usernameField.setPreferredSize(new Dimension(250, 35));
            usernameField.setFont(new Font("Inter", Font.PLAIN, 14));
            formPanel.add(usernameField, gbc);
            
            // User Type
            gbc.gridx = 0; gbc.gridy = 3;
            JLabel userTypeLabel = new JLabel("User Type:");
            userTypeLabel.setFont(new Font("Inter", Font.BOLD, 14));
            formPanel.add(userTypeLabel, gbc);
            
            gbc.gridx = 1;
            JComboBox<String> userTypeCombo = new JComboBox<>(new String[]{
                "ADMIN", "PURCHASE_MANAGER", "PURCHASE_STAFF",
                "SALES_MANAGER", "SALES_STAFF", "FINANCE_MANAGER", "FINANCE_STAFF",
                "INVENTORY_MANAGER", "INVENTORY_STAFF"
            });
            userTypeCombo.setSelectedItem(user.getRole());
            userTypeCombo.setPreferredSize(new Dimension(250, 35));
            userTypeCombo.setFont(new Font("Inter", Font.PLAIN, 14));
            formPanel.add(userTypeCombo, gbc);
            
            // Password Reset Option
            gbc.gridx = 0; gbc.gridy = 4;
            JLabel passwordLabel = new JLabel("Reset Password:");
            passwordLabel.setFont(new Font("Inter", Font.BOLD, 14));
            formPanel.add(passwordLabel, gbc);
            
            gbc.gridx = 1;
            JCheckBox resetPasswordCheck = new JCheckBox("Generate new temporary password");
            resetPasswordCheck.setFont(new Font("Inter", Font.PLAIN, 14));
            resetPasswordCheck.setBackground(Color.WHITE);
            formPanel.add(resetPasswordCheck, gbc);
            
            mainPanel.add(formPanel);
            mainPanel.add(Box.createVerticalStrut(30));
            
            // Buttons
            JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 0));
            buttonPanel.setBackground(Color.WHITE);
            
            JButton saveButton = createStyledButton("Save Changes", PRIMARY_BLUE, Color.WHITE);
            saveButton.setPreferredSize(new Dimension(120, 40));
            saveButton.addActionListener(e -> {
                // Validate inputs
                String newName = nameField.getText().trim();
                String newUsername = usernameField.getText().trim();
                String newRole = (String) userTypeCombo.getSelectedItem();
                
                if (newName.isEmpty() || newUsername.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog,
                        "Please fill in all required fields.",
                        "Validation Error",
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                // Check if username is already taken (excluding current user)
                for (User existingUser : users) {
                    if (!existingUser.getUserId().equals(user.getUserId()) && 
                        existingUser.getUsername().equals(newUsername)) {
                        JOptionPane.showMessageDialog(dialog,
                            "Username '" + newUsername + "' is already taken.",
                            "Validation Error",
                            JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                
                // Update user
                user.setName(newName);
                user.setUsername(newUsername);
                user.setRole(newRole);
                
                if (resetPasswordCheck.isSelected()) {
                    String tempPassword = "temp" + System.currentTimeMillis() % 10000;
                    user.setPassword(tempPassword);
                    JOptionPane.showMessageDialog(dialog,
                        "User updated successfully!\n\nNew temporary password: " + tempPassword,
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(dialog,
                        "User updated successfully!",
                        "Success",
                        JOptionPane.INFORMATION_MESSAGE);
                }
                
                // Update table row
                tableModel.setValueAt(user.getName(), tableRow, 1);
                tableModel.setValueAt(user.getUsername() + "@owsb.com", tableRow, 2);
                tableModel.setValueAt(formatUserType(user.getRole()), tableRow, 3);
                tableModel.setValueAt(getDepartmentFromRole(user.getRole()), tableRow, 4);
                
                dialog.dispose();
            });
            
            JButton cancelButton = createStyledButton("Cancel", GRAY_500, Color.WHITE);
            cancelButton.setPreferredSize(new Dimension(100, 40));
            cancelButton.addActionListener(e -> dialog.dispose());
            
            buttonPanel.add(saveButton);
            buttonPanel.add(cancelButton);
            
            mainPanel.add(buttonPanel);
            
            dialog.add(mainPanel, BorderLayout.CENTER);
            dialog.setVisible(true);
        }
        
        private void toggleUserStatus(int row) {
            String userId = (String) tableModel.getValueAt(row, 0);
            String userName = (String) tableModel.getValueAt(row, 1);
            String currentStatus = (String) tableModel.getValueAt(row, 6);
            String newStatus = "Active".equals(currentStatus) ? "Inactive" : "Active";
            
            int result = JOptionPane.showConfirmDialog(UserManagementPanel.this,
                "Are you sure you want to " + newStatus.toLowerCase() + " user: " + userName + "?",
                "Confirm Status Change",
                JOptionPane.YES_NO_OPTION);
            
            if (result == JOptionPane.YES_OPTION) {
                // Update the status in the table
                tableModel.setValueAt(newStatus, row, 6);
                
                JOptionPane.showMessageDialog(UserManagementPanel.this,
                    "User " + userName + " has been " + newStatus.toLowerCase() + "d successfully!",
                    "Status Updated",
                    JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }
} 