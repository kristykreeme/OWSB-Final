package view.panels;

import model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class UserManagementPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTable userTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JButton addButton;
    private JButton editButton;
    private JButton deleteButton;
    
    // User form components
    private JDialog userDialog;
    private JTextField userIdField;
    private JTextField nameField;
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JComboBox<String> roleComboBox;
    
    // List of all users
    private List<User> users;
    
    public UserManagementPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("User Management");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(20, 0, 0, 0));
        
        // Search and buttons panel
        JPanel actionPanel = new JPanel(new BorderLayout());
        
        // Search panel
        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        searchPanel.add(new JLabel("Search User: "));
        searchField = new JTextField(20);
        searchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                filterUsers();
            }
        });
        searchPanel.add(searchField);
        actionPanel.add(searchPanel, BorderLayout.WEST);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        addButton = new JButton("Add User");
        addButton.setBackground(new Color(0, 102, 204));
        addButton.setForeground(Color.WHITE);
        addButton.setOpaque(true);
        addButton.setBorderPainted(false);
        addButton.setPreferredSize(new Dimension(120, 35));
        addButton.setMinimumSize(new Dimension(120, 35));
        addButton.setFont(new Font("Arial", Font.BOLD, 12));
        addButton.addActionListener(e -> showUserDialog(null));
        buttonPanel.add(addButton);
        
        editButton = new JButton("Edit");
        editButton.setBackground(new Color(46, 184, 46));
        editButton.setForeground(Color.WHITE);
        editButton.setOpaque(true);
        editButton.setBorderPainted(false);
        editButton.setPreferredSize(new Dimension(100, 35));
        editButton.setMinimumSize(new Dimension(100, 35));
        editButton.setFont(new Font("Arial", Font.BOLD, 12));
        editButton.addActionListener(e -> editSelectedUser());
        buttonPanel.add(editButton);
        
        deleteButton = new JButton("Delete");
        deleteButton.setBackground(new Color(255, 51, 51));
        deleteButton.setForeground(Color.WHITE);
        deleteButton.setOpaque(true);
        deleteButton.setBorderPainted(false);
        deleteButton.setPreferredSize(new Dimension(100, 35));
        deleteButton.setMinimumSize(new Dimension(100, 35));
        deleteButton.setFont(new Font("Arial", Font.BOLD, 12));
        deleteButton.addActionListener(e -> deleteSelectedUser());
        buttonPanel.add(deleteButton);
        
        actionPanel.add(buttonPanel, BorderLayout.EAST);
        
        contentPanel.add(actionPanel, BorderLayout.NORTH);
        
        // Table
        createUserTable();
        JScrollPane scrollPane = new JScrollPane(userTable);
        contentPanel.add(scrollPane, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Load data
        loadUsers();
    }
    
    private void createUserTable() {
        // Define table columns
        String[] columns = {"User ID", "Name", "Username", "Role"};
        
        // Create table model
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make all cells non-editable
            }
        };
        
        // Create table
        userTable = new JTable(tableModel);
        userTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        userTable.setRowHeight(25);
        userTable.getTableHeader().setReorderingAllowed(false);
        
        // Set column widths
        userTable.getColumnModel().getColumn(0).setPreferredWidth(70);
        userTable.getColumnModel().getColumn(1).setPreferredWidth(150);
        userTable.getColumnModel().getColumn(2).setPreferredWidth(100);
        userTable.getColumnModel().getColumn(3).setPreferredWidth(100);
        
        // Add double-click listener to edit user
        userTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    editSelectedUser();
                }
            }
        });
    }
    
    private void loadUsers() {
        // Clear table
        tableModel.setRowCount(0);
        
        // Load users from model
        users = User.getAllUsers();
        
        // Add users to table
        for (User user : users) {
            Object[] row = {
                user.getUserId(),
                user.getName(),
                user.getUsername(),
                user.getRole()
            };
            
            tableModel.addRow(row);
        }
    }
    
    private void filterUsers() {
        String searchTerm = searchField.getText().toLowerCase();
        
        tableModel.setRowCount(0);
        
        for (User user : users) {
            if (user.getUserId().toLowerCase().contains(searchTerm) || 
                user.getName().toLowerCase().contains(searchTerm) || 
                user.getUsername().toLowerCase().contains(searchTerm)) {
                
                Object[] row = {
                    user.getUserId(),
                    user.getName(),
                    user.getUsername(),
                    user.getRole()
                };
                
                tableModel.addRow(row);
            }
        }
    }
    
    private void editSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to edit", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Don't allow editing the admin user if not admin
        if (userId.equals("U001") && !currentUser.getUserId().equals("U001")) {
            JOptionPane.showMessageDialog(this, "You don't have permission to edit the admin user", "Permission Denied", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        User selectedUser = null;
        
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                selectedUser = user;
                break;
            }
        }
        
        if (selectedUser != null) {
            showUserDialog(selectedUser);
        }
    }
    
    private void deleteSelectedUser() {
        int selectedRow = userTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a user to delete", "No Selection", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String userId = (String) tableModel.getValueAt(selectedRow, 0);
        
        // Don't allow deleting the admin user
        if (userId.equals("U001")) {
            JOptionPane.showMessageDialog(this, "The admin user cannot be deleted", "Cannot Delete", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Don't allow managers to delete users not created by them
        if (!currentUser.getRole().equals("ADMIN")) {
            String role = (String) tableModel.getValueAt(selectedRow, 3);
            
            // Managers can only delete staff under their management
            if (currentUser.getRole().equals("SALES_MANAGER") && !role.equals("SALES_STAFF")) {
                JOptionPane.showMessageDialog(this, "You can only delete sales staff users", "Permission Denied", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (currentUser.getRole().equals("PURCHASE_MANAGER") && !role.equals("PURCHASE_STAFF")) {
                JOptionPane.showMessageDialog(this, "You can only delete purchase staff users", "Permission Denied", JOptionPane.WARNING_MESSAGE);
                return;
            } else if (currentUser.getRole().equals("INVENTORY_MANAGER") && !role.equals("INVENTORY_STAFF")) {
                JOptionPane.showMessageDialog(this, "You can only delete inventory staff users", "Permission Denied", JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        
        int option = JOptionPane.showConfirmDialog(this, 
            "Are you sure you want to delete this user?", 
            "Confirm Delete", 
            JOptionPane.YES_NO_OPTION);
        
        if (option == JOptionPane.YES_OPTION) {
            User userToDelete = null;
            
            for (User user : users) {
                if (user.getUserId().equals(userId)) {
                    userToDelete = user;
                    break;
                }
            }
            
            if (userToDelete != null) {
                boolean success = User.deleteUser(userId);
                
                if (success) {
                    JOptionPane.showMessageDialog(this, "User deleted successfully", "Success", JOptionPane.INFORMATION_MESSAGE);
                    loadUsers();
                } else {
                    JOptionPane.showMessageDialog(this, "Error deleting user", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }
    
    private void showUserDialog(User user) {
        // Create dialog
        userDialog = new JDialog((JFrame) SwingUtilities.getWindowAncestor(this), "User Details", true);
        userDialog.setSize(400, 370);
        userDialog.setLocationRelativeTo(null);
        userDialog.setResizable(false);
        
        // Main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Form panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(5, 5, 5, 5);
        
        // User ID
        gbc.gridx = 0;
        gbc.gridy = 0;
        formPanel.add(new JLabel("User ID:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 0;
        userIdField = new JTextField(15);
        
        // If editing existing user or not admin, disable ID field
        if (user != null || !currentUser.getRole().equals("ADMIN")) {
            userIdField.setEditable(false);
        }
        
        formPanel.add(userIdField, gbc);
        
        // Name
        gbc.gridx = 0;
        gbc.gridy = 1;
        formPanel.add(new JLabel("Name:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 1;
        nameField = new JTextField(15);
        formPanel.add(nameField, gbc);
        
        // Username
        gbc.gridx = 0;
        gbc.gridy = 2;
        formPanel.add(new JLabel("Username:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 2;
        usernameField = new JTextField(15);
        formPanel.add(usernameField, gbc);
        
        // Password
        gbc.gridx = 0;
        gbc.gridy = 3;
        formPanel.add(new JLabel("Password:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 3;
        passwordField = new JPasswordField(15);
        formPanel.add(passwordField, gbc);
        
        // Role
        gbc.gridx = 0;
        gbc.gridy = 4;
        formPanel.add(new JLabel("Role:"), gbc);
        
        gbc.gridx = 1;
        gbc.gridy = 4;
        
        // Different roles based on current user's role
        String[] roles;
        if (currentUser.getRole().equals("ADMIN")) {
            // Admin can assign any role
            roles = new String[]{
                "ADMIN", 
                "SALES_MANAGER", "SALES_STAFF",
                "PURCHASE_MANAGER", "PURCHASE_STAFF",
                "INVENTORY_MANAGER", "INVENTORY_STAFF",
                "FINANCE_MANAGER"
            };
        } else if (currentUser.getRole().equals("SALES_MANAGER")) {
            // Sales managers can only create sales staff
            roles = new String[]{"SALES_STAFF"};
        } else if (currentUser.getRole().equals("PURCHASE_MANAGER")) {
            // Purchase managers can only create purchase staff
            roles = new String[]{"PURCHASE_STAFF"};
        } else if (currentUser.getRole().equals("INVENTORY_MANAGER")) {
            // Inventory managers can only create inventory staff
            roles = new String[]{"INVENTORY_STAFF"};
        } else {
            // Default, shouldn't happen but just in case
            roles = new String[]{"SALES_STAFF", "PURCHASE_STAFF", "INVENTORY_STAFF"};
        }
        
        roleComboBox = new JComboBox<>(roles);
        formPanel.add(roleComboBox, gbc);
        
        // Role description
        gbc.gridx = 0;
        gbc.gridy = 5;
        gbc.gridwidth = 2;
        JTextArea roleDescArea = new JTextArea(3, 20);
        roleDescArea.setEditable(false);
        roleDescArea.setLineWrap(true);
        roleDescArea.setWrapStyleWord(true);
        roleDescArea.setBackground(new Color(245, 245, 245));
        roleDescArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));
        
        // Update role description when selection changes
        roleComboBox.addActionListener(e -> {
            String selectedRole = (String) roleComboBox.getSelectedItem();
            roleDescArea.setText(getRoleDescription(selectedRole));
        });
        
        // Set initial description
        roleDescArea.setText(getRoleDescription((String) roleComboBox.getSelectedItem()));
        
        formPanel.add(roleDescArea, gbc);
        
        mainPanel.add(formPanel, BorderLayout.CENTER);
        
        // Buttons panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setPreferredSize(new Dimension(100, 35));
        cancelButton.setFont(new Font("Arial", Font.PLAIN, 12));
        cancelButton.addActionListener(e -> userDialog.dispose());
        buttonPanel.add(cancelButton);
        
        JButton saveButton = new JButton("Save");
        saveButton.setBackground(new Color(0, 102, 204));
        saveButton.setForeground(Color.WHITE);
        saveButton.setOpaque(true);
        saveButton.setBorderPainted(false);
        saveButton.setPreferredSize(new Dimension(100, 35));
        saveButton.setFont(new Font("Arial", Font.BOLD, 12));
        saveButton.addActionListener(e -> saveUser(user != null));
        buttonPanel.add(saveButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        // If editing, populate fields
        if (user != null) {
            userIdField.setText(user.getUserId());
            nameField.setText(user.getName());
            usernameField.setText(user.getUsername());
            passwordField.setText(user.getPassword());
            
            // For admin users, only the admin (U001) can edit them
            if (user.getRole().equals("ADMIN") && !currentUser.getUserId().equals("U001")) {
                roleComboBox.setEnabled(false);
            } else {
                roleComboBox.setSelectedItem(user.getRole());
            }
        } else {
            // New user, generate ID
            String newId = generateUserId();
            userIdField.setText(newId);
        }
        
        userDialog.add(mainPanel);
        userDialog.setVisible(true);
    }
    
    // Return a description for each role
    private String getRoleDescription(String role) {
        switch (role) {
            case "ADMIN":
                return "Full access to all system features and user management.";
            case "SALES_MANAGER":
                return "Manages sales data, approves sales transactions, and manages sales staff.";
            case "SALES_STAFF":
                return "Records daily sales, manages customer inquiries and sales data entry.";
            case "PURCHASE_MANAGER":
                return "Approves purchase orders, manages suppliers and purchase staff.";
            case "PURCHASE_STAFF":
                return "Creates purchase requisitions, processes purchase orders.";
            case "INVENTORY_MANAGER":
                return "Manages inventory levels, approves stock adjustments, and manages inventory staff.";
            case "INVENTORY_STAFF":
                return "Maintains stock records, performs stock counts and adjustments.";
            case "FINANCE_MANAGER":
                return "Manages financial transactions, approves budgets and payments.";
            default:
                return "Basic system access with limited permissions.";
        }
    }
    
    private void saveUser(boolean isEditMode) {
        // Get field values
        String userId = userIdField.getText().trim();
        String name = nameField.getText().trim();
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        String role = (String) roleComboBox.getSelectedItem();
        
        // Validate fields
        if (userId.isEmpty() || name.isEmpty() || username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(userDialog, 
                "Please fill in all fields", 
                "Validation Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Check if username already exists (for new users)
        if (!isEditMode) {
            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    JOptionPane.showMessageDialog(userDialog, 
                        "Username already exists. Please choose a different username.", 
                        "Duplicate Username", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
        }
        
        // Create or update user
        User user = new User(userId, name, username, password, role);
        
        // Check for admin protections
        if (isEditMode && userId.equals("U001") && !currentUser.getUserId().equals("U001")) {
            JOptionPane.showMessageDialog(userDialog, 
                "You don't have permission to edit the admin user", 
                "Permission Denied", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        // Save user
        boolean success = user.saveUser();
        
        if (success) {
            JOptionPane.showMessageDialog(userDialog, 
                isEditMode ? "User updated successfully" : "User created successfully", 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Close dialog
            userDialog.dispose();
            
            // Reload users
            loadUsers();
        } else {
            JOptionPane.showMessageDialog(userDialog, 
                "Error saving user", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    // Helper method to generate a new user ID
    private String generateUserId() {
        // Get the highest user ID and increment by 1
        int highestId = 0;
        
        for (User user : users) {
            String userId = user.getUserId();
            if (userId.startsWith("U")) {
                try {
                    int id = Integer.parseInt(userId.substring(1));
                    if (id > highestId) {
                        highestId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore if not a number
                }
            }
        }
        
        // Format new ID with leading zeros
        return String.format("U%03d", highestId + 1);
    }
} 