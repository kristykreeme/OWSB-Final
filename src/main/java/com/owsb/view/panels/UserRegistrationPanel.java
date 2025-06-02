package com.owsb.view.panels;

import com.owsb.model.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class UserRegistrationPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTextField userIdField;
    private JComboBox<String> userTypeComboBox;
    private JTextField fullNameField;
    private JTextField emailField;
    private JTextField phoneField;
    private JComboBox<String> departmentComboBox;
    private JTextField usernameField;
    private JPasswordField temporaryPasswordField;
    private JTextArea notesArea;
    
    // Modern UI Colors
    private static final Color PRIMARY_BLUE = new Color(37, 99, 235);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color GRAY_50 = new Color(249, 250, 251);
    private static final Color GRAY_100 = new Color(243, 244, 246);
    private static final Color GRAY_200 = new Color(229, 231, 235);
    private static final Color GRAY_300 = new Color(209, 213, 219);
    private static final Color GRAY_400 = new Color(156, 163, 175);
    private static final Color GRAY_500 = new Color(107, 114, 128);
    private static final Color GRAY_600 = new Color(75, 85, 99);
    private static final Color GRAY_700 = new Color(55, 65, 81);
    private static final Color GRAY_900 = new Color(17, 24, 39);
    
    public UserRegistrationPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        initializeComponents();
        setupUI();
    }
    
    private void initializeComponents() {
        // Initialize form fields
        userIdField = new JTextField();
        userIdField.setText(generateUserId());
        userIdField.setEditable(false);
        
        userTypeComboBox = new JComboBox<>(new String[]{
            "Select User Type",
            "ADMIN",
            "PURCHASE_MANAGER", 
            "PURCHASE_STAFF",
            "SALES_MANAGER",
            "SALES_STAFF", 
            "FINANCE_MANAGER",
            "FINANCE_STAFF",
            "INVENTORY_MANAGER",
            "INVENTORY_STAFF"
        });
        
        fullNameField = new JTextField();
        emailField = new JTextField();
        phoneField = new JTextField();
        
        departmentComboBox = new JComboBox<>(new String[]{
            "Select Department",
            "Administration",
            "Purchase",
            "Sales", 
            "Finance",
            "Inventory",
            "Operations"
        });
        
        usernameField = new JTextField();
        temporaryPasswordField = new JPasswordField();
        notesArea = new JTextArea(4, 0);
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
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
        
        JLabel titleLabel = new JLabel("User Registration");
        titleLabel.setFont(new Font("Inter", Font.BOLD, 32));
        titleLabel.setForeground(GRAY_900);
        
        titlePanel.add(titleLabel, BorderLayout.CENTER);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createContentPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        // Form section
        JPanel formSection = createFormSection();
        panel.add(formSection);
        
        // Add some spacing
        panel.add(Box.createVerticalStrut(40));
        
        // Action buttons
        JPanel buttonPanel = createButtonPanel();
        panel.add(buttonPanel);
        
        return panel;
    }
    
    private JPanel createFormSection() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        // Section title
        JLabel sectionTitle = new JLabel("User Registration");
        sectionTitle.setFont(new Font("Inter", Font.BOLD, 24));
        sectionTitle.setForeground(GRAY_900);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        sectionTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(sectionTitle);
        
        // Form grid
        JPanel formGrid = new JPanel(new GridBagLayout());
        formGrid.setBackground(Color.WHITE);
        formGrid.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(0, 0, 24, 24);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1: User ID, User Type, Full Name
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1.0;
        formGrid.add(createFormField("User ID", userIdField), gbc);
        
        gbc.gridx = 1; gbc.gridy = 0;
        formGrid.add(createFormField("User Type", userTypeComboBox), gbc);
        
        gbc.gridx = 2; gbc.gridy = 0; gbc.gridwidth = 1; gbc.insets = new Insets(0, 0, 24, 0);
        formGrid.add(createFormField("Full Name", fullNameField), gbc);
        
        // Row 2: Email, Phone, Department
        gbc.gridx = 0; gbc.gridy = 1; gbc.gridwidth = 1; gbc.insets = new Insets(0, 0, 24, 24);
        formGrid.add(createFormField("Email Address", emailField), gbc);
        
        gbc.gridx = 1; gbc.gridy = 1;
        formGrid.add(createFormField("Phone Number", phoneField), gbc);
        
        gbc.gridx = 2; gbc.gridy = 1; gbc.insets = new Insets(0, 0, 24, 0);
        formGrid.add(createFormField("Department", departmentComboBox), gbc);
        
        // Row 3: Username, Temporary Password
        gbc.gridx = 0; gbc.gridy = 2; gbc.insets = new Insets(0, 0, 24, 24);
        formGrid.add(createFormField("Username", usernameField), gbc);
        
        gbc.gridx = 1; gbc.gridy = 2; gbc.gridwidth = 2; gbc.insets = new Insets(0, 0, 24, 0);
        formGrid.add(createFormField("Temporary Password", temporaryPasswordField), gbc);
        
        // Row 4: Notes (full width)
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 3; gbc.fill = GridBagConstraints.BOTH; gbc.weighty = 1.0;
        JScrollPane notesScroll = new JScrollPane(notesArea);
        notesScroll.setBorder(BorderFactory.createLineBorder(GRAY_300, 1));
        notesScroll.setPreferredSize(new Dimension(0, 120));
        formGrid.add(createFormField("Notes", notesScroll), gbc);
        
        panel.add(formGrid);
        
        return panel;
    }
    
    private JPanel createFormField(String labelText, JComponent field) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Inter", Font.BOLD, 14));
        label.setForeground(GRAY_700);
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        label.setBorder(BorderFactory.createEmptyBorder(0, 0, 8, 0));
        panel.add(label);
        
        // Field styling
        if (field instanceof JTextField || field instanceof JPasswordField) {
            field.setFont(new Font("Inter", Font.PLAIN, 14));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRAY_300, 1),
                BorderFactory.createEmptyBorder(12, 16, 12, 16)
            ));
            field.setPreferredSize(new Dimension(0, 48));
            
            // Add placeholder styling for specific fields
            if (field == fullNameField) {
                ((JTextField) field).setText("");
                addPlaceholder((JTextField) field, "Enter full name");
            } else if (field == emailField) {
                addPlaceholder((JTextField) field, "Enter email address");
            } else if (field == phoneField) {
                addPlaceholder((JTextField) field, "Enter phone number");
            } else if (field == usernameField) {
                addPlaceholder((JTextField) field, "Enter username");
            } else if (field == temporaryPasswordField) {
                addPlaceholder((JPasswordField) field, "Enter temporary password");
            }
        } else if (field instanceof JComboBox) {
            field.setFont(new Font("Inter", Font.PLAIN, 14));
            field.setBorder(BorderFactory.createLineBorder(GRAY_300, 1));
            field.setPreferredSize(new Dimension(0, 48));
            ((JComboBox<?>) field).setBackground(Color.WHITE);
        } else if (field instanceof JScrollPane) {
            // Notes area is already styled in the calling method
            field.setPreferredSize(new Dimension(0, 120));
            
            // Style the text area inside
            JTextArea textArea = (JTextArea) ((JScrollPane) field).getViewport().getView();
            textArea.setFont(new Font("Inter", Font.PLAIN, 14));
            textArea.setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            textArea.setBackground(Color.WHITE);
            addPlaceholder(textArea, "Enter any additional notes...");
        }
        
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        panel.add(field);
        
        return panel;
    }
    
    private void addPlaceholder(JTextField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(GRAY_400);
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (field.getText().equals(placeholder)) {
                    field.setText("");
                    field.setForeground(GRAY_900);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (field.getText().trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(GRAY_400);
                }
            }
        });
    }
    
    private void addPlaceholder(JPasswordField field, String placeholder) {
        field.setText(placeholder);
        field.setForeground(GRAY_400);
        field.setEchoChar((char) 0); // Show placeholder text
        
        field.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (String.valueOf(field.getPassword()).equals(placeholder)) {
                    field.setText("");
                    field.setForeground(GRAY_900);
                    field.setEchoChar('•'); // Hide password
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (String.valueOf(field.getPassword()).trim().isEmpty()) {
                    field.setText(placeholder);
                    field.setForeground(GRAY_400);
                    field.setEchoChar((char) 0); // Show placeholder
                }
            }
        });
    }
    
    private void addPlaceholder(JTextArea area, String placeholder) {
        area.setText(placeholder);
        area.setForeground(GRAY_400);
        
        area.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (area.getText().equals(placeholder)) {
                    area.setText("");
                    area.setForeground(GRAY_900);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (area.getText().trim().isEmpty()) {
                    area.setText(placeholder);
                    area.setForeground(GRAY_400);
                }
            }
        });
    }
    
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.setBackground(Color.WHITE);
        
        // Create User button
        JButton createButton = createStyledButton("Create User", PRIMARY_BLUE, Color.WHITE);
        createButton.setPreferredSize(new Dimension(140, 48));
        createButton.addActionListener(e -> createUser());
        
        // Clear Form button
        JButton clearButton = createStyledButton("Clear Form", GRAY_600, Color.WHITE);
        clearButton.setPreferredSize(new Dimension(120, 48));
        clearButton.addActionListener(e -> clearForm());
        
        panel.add(createButton);
        panel.add(Box.createHorizontalStrut(16));
        panel.add(clearButton);
        
        return panel;
    }
    
    private JButton createStyledButton(String text, Color bgColor, Color textColor) {
        JButton button = new JButton(text);
        button.setFont(new Font("Inter", Font.BOLD, 14));
        button.setBackground(bgColor);
        button.setForeground(textColor);
        button.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
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
    
    private void createUser() {
        // Validate form
        if (!validateForm()) {
            return;
        }
        
        // Get form values
        String userId = userIdField.getText().trim();
        String userType = (String) userTypeComboBox.getSelectedItem();
        String fullName = getFieldValue(fullNameField, "Enter full name");
        String email = getFieldValue(emailField, "Enter email address");
        String phone = getFieldValue(phoneField, "Enter phone number");
        String department = (String) departmentComboBox.getSelectedItem();
        String username = getFieldValue(usernameField, "Enter username");
        String password = getFieldValue(temporaryPasswordField, "Enter temporary password");
        String notes = getFieldValue(notesArea, "Enter any additional notes...");
        
        // Create user object
        User newUser = new User(userId, fullName, username, password, userType);
        
        // Save user
        boolean success = newUser.saveUser();
        
        if (success) {
            JOptionPane.showMessageDialog(this,
                "User created successfully!\n\n" +
                "User ID: " + userId + "\n" +
                "Username: " + username + "\n" +
                "Temporary Password: " + password + "\n\n" +
                "Please inform the user to change their password on first login.",
                "User Created",
                JOptionPane.INFORMATION_MESSAGE);
            
            clearForm();
            userIdField.setText(generateUserId()); // Generate new ID for next user
        } else {
            JOptionPane.showMessageDialog(this,
                "Error creating user. Please try again.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean validateForm() {
        // Check required fields
        if (userTypeComboBox.getSelectedIndex() == 0) {
            showValidationError("Please select a user type.");
            return false;
        }
        
        if (isPlaceholder(fullNameField, "Enter full name")) {
            showValidationError("Please enter the full name.");
            return false;
        }
        
        if (isPlaceholder(emailField, "Enter email address")) {
            showValidationError("Please enter the email address.");
            return false;
        }
        
        if (isPlaceholder(usernameField, "Enter username")) {
            showValidationError("Please enter the username.");
            return false;
        }
        
        if (isPlaceholder(temporaryPasswordField, "Enter temporary password")) {
            showValidationError("Please enter a temporary password.");
            return false;
        }
        
        if (departmentComboBox.getSelectedIndex() == 0) {
            showValidationError("Please select a department.");
            return false;
        }
        
        // Check if username already exists
        String username = getFieldValue(usernameField, "Enter username");
        List<User> existingUsers = User.getAllUsers();
        for (User user : existingUsers) {
            if (user.getUsername().equals(username)) {
                showValidationError("Username already exists. Please choose a different username.");
                return false;
            }
        }
        
        return true;
    }
    
    private boolean isPlaceholder(JTextField field, String placeholder) {
        return field.getText().equals(placeholder) || field.getText().trim().isEmpty();
    }
    
    private boolean isPlaceholder(JPasswordField field, String placeholder) {
        return String.valueOf(field.getPassword()).equals(placeholder) || 
               String.valueOf(field.getPassword()).trim().isEmpty();
    }
    
    private String getFieldValue(JTextField field, String placeholder) {
        String value = field.getText();
        return value.equals(placeholder) ? "" : value;
    }
    
    private String getFieldValue(JPasswordField field, String placeholder) {
        String value = String.valueOf(field.getPassword());
        return value.equals(placeholder) ? "" : value;
    }
    
    private String getFieldValue(JTextArea area, String placeholder) {
        String value = area.getText();
        return value.equals(placeholder) ? "" : value;
    }
    
    private void showValidationError(String message) {
        JOptionPane.showMessageDialog(this, message, "Validation Error", JOptionPane.WARNING_MESSAGE);
    }
    
    private void clearForm() {
        userTypeComboBox.setSelectedIndex(0);
        
        // Reset text fields to placeholders
        fullNameField.setText("Enter full name");
        fullNameField.setForeground(GRAY_400);
        
        emailField.setText("Enter email address");
        emailField.setForeground(GRAY_400);
        
        phoneField.setText("Enter phone number");
        phoneField.setForeground(GRAY_400);
        
        departmentComboBox.setSelectedIndex(0);
        
        usernameField.setText("Enter username");
        usernameField.setForeground(GRAY_400);
        
        temporaryPasswordField.setText("Enter temporary password");
        temporaryPasswordField.setForeground(GRAY_400);
        temporaryPasswordField.setEchoChar((char) 0);
        
        notesArea.setText("Enter any additional notes...");
        notesArea.setForeground(GRAY_400);
        
        // Generate new user ID
        userIdField.setText(generateUserId());
    }
    
    private String generateUserId() {
        // Get all existing users
        List<User> users = User.getAllUsers();
        
        // Find the highest user ID number
        int highestId = 0;
        for (User user : users) {
            String userId = user.getUserId();
            if (userId.startsWith("USR-") && userId.length() > 4) {
                try {
                    String numberPart = userId.substring(4);
                    int id = Integer.parseInt(numberPart);
                    if (id > highestId) {
                        highestId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore invalid formats
                }
            }
        }
        
        // Generate new ID
        return String.format("USR-%03d", highestId + 1);
    }
} 