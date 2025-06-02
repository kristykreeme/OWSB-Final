package com.owsb.view.panels;

import com.owsb.model.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class PurchaseRequisitionPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTextField prIdField;
    private JTextField requiredDateField;
    private JComboBox<String> priorityCombo;
    private JComboBox<String> departmentCombo;
    private JTextArea justificationArea;
    private JTextField itemSearchField;
    private JList<String> itemSuggestionsList;
    private JPopupMenu itemSuggestionsPopup;
    private DefaultListModel<String> itemSuggestionsModel;
    private JTextField quantityField;
    private JComboBox<String> unitCombo;
    private DefaultListModel<PRItem> selectedItemsModel;
    private JList<PRItem> selectedItemsList;
    
    // Sample items database (in real app, this would come from database)
    private String[] allItems = {
        "ITM-001 - Rice (Premium Basmati)",
        "ITM-002 - Rice (Jasmine Long Grain)",
        "ITM-003 - Rice (Brown Organic)",
        "ITM-004 - Sugar (White Granulated)",
        "ITM-005 - Sugar (Brown Raw)",
        "ITM-006 - Sugar (Powdered)",
        "ITM-007 - Oil (Olive Extra Virgin)",
        "ITM-008 - Oil (Coconut Organic)",
        "ITM-009 - Oil (Sunflower)",
        "ITM-010 - Oil (Vegetable)",
        "ITM-011 - Flour (All Purpose)",
        "ITM-012 - Flour (Whole Wheat)",
        "ITM-013 - Flour (Almond)",
        "ITM-014 - Salt (Table Fine)",
        "ITM-015 - Salt (Sea Coarse)",
        "ITM-016 - Salt (Himalayan Pink)",
        "ITM-017 - Pepper (Black Ground)",
        "ITM-018 - Pepper (White Whole)",
        "ITM-019 - Pasta (Spaghetti)",
        "ITM-020 - Pasta (Penne)",
        "ITM-021 - Pasta (Fusilli)",
        "ITM-022 - Beans (Black Dried)",
        "ITM-023 - Beans (Kidney Red)",
        "ITM-024 - Beans (Chickpeas)",
        "ITM-025 - Lentils (Red Split)",
        "ITM-026 - Lentils (Green Whole)",
        "ITM-027 - Quinoa (White Organic)",
        "ITM-028 - Quinoa (Red)",
        "ITM-029 - Oats (Rolled Old Fashioned)",
        "ITM-030 - Oats (Steel Cut)",
        "ITM-031 - Milk (Whole 3.25%)",
        "ITM-032 - Milk (Skim 0%)",
        "ITM-033 - Milk (Almond Unsweetened)",
        "ITM-034 - Cheese (Cheddar Sharp)",
        "ITM-035 - Cheese (Mozzarella Fresh)",
        "ITM-036 - Cheese (Parmesan Aged)",
        "ITM-037 - Butter (Unsalted)",
        "ITM-038 - Butter (Salted)",
        "ITM-039 - Eggs (Large Grade A)",
        "ITM-040 - Eggs (Organic Free Range)",
        "ITM-041 - Chicken (Breast Boneless)",
        "ITM-042 - Chicken (Thighs Bone-in)",
        "ITM-043 - Beef (Ground 80/20)",
        "ITM-044 - Beef (Sirloin Steak)",
        "ITM-045 - Fish (Salmon Atlantic)",
        "ITM-046 - Fish (Tuna Yellowfin)",
        "ITM-047 - Shrimp (Large Peeled)",
        "ITM-048 - Tomatoes (Roma Fresh)",
        "ITM-049 - Tomatoes (Cherry Organic)",
        "ITM-050 - Onions (Yellow Medium)"
        // In real application, this would be loaded from database
    };
    private String selectedItemCode = "";
    
    // Modern UI Colors
    private static final Color PRIMARY_BLUE = new Color(37, 99, 235);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color DANGER_RED = new Color(239, 68, 68);
    private static final Color WARNING_ORANGE = new Color(245, 158, 11);
    private static final Color GRAY_50 = new Color(249, 250, 251);
    private static final Color GRAY_100 = new Color(243, 244, 246);
    private static final Color GRAY_200 = new Color(229, 231, 235);
    private static final Color GRAY_300 = new Color(209, 213, 219);
    private static final Color GRAY_600 = new Color(75, 85, 99);
    private static final Color GRAY_700 = new Color(55, 65, 81);
    private static final Color GRAY_900 = new Color(17, 24, 39);
    
    public PurchaseRequisitionPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        
        initializeComponents();
        setupUI();
        initializeForm();
    }
    
    private void initializeComponents() {
        // Initialize form fields
        prIdField = new JTextField();
        requiredDateField = new JTextField();
        priorityCombo = new JComboBox<>(new String[]{"Select Priority", "Low", "Medium", "High", "Urgent"});
        departmentCombo = new JComboBox<>(new String[]{"Select Department", "Sales", "Marketing", "Operations", "Finance", "IT", "HR"});
        justificationArea = new JTextArea(4, 30);
        
        // Initialize searchable item field
        itemSearchField = new JTextField();
        itemSearchField.setText("Search for items...");
        itemSearchField.setForeground(GRAY_600);
        
        // Setup item suggestions
        itemSuggestionsModel = new DefaultListModel<>();
        itemSuggestionsList = new JList<>(itemSuggestionsModel);
        itemSuggestionsPopup = new JPopupMenu();
        
        // Configure suggestions list
        itemSuggestionsList.setFont(new Font("Inter", Font.PLAIN, 14));
        itemSuggestionsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        itemSuggestionsList.setVisibleRowCount(8);
        itemSuggestionsList.setCellRenderer(new ItemSuggestionRenderer());
        
        // Add scroll pane to popup
        JScrollPane suggestionsScrollPane = new JScrollPane(itemSuggestionsList);
        suggestionsScrollPane.setPreferredSize(new Dimension(400, 200));
        suggestionsScrollPane.setBorder(BorderFactory.createLineBorder(GRAY_300, 1));
        itemSuggestionsPopup.add(suggestionsScrollPane);
        
        // Add listeners for search functionality
        setupItemSearchListeners();
        
        quantityField = new JTextField();
        unitCombo = new JComboBox<>(new String[]{"Select Unit", "kg", "pcs", "box", "liter", "meter", "pack"});
        
        // Initialize selected items list
        selectedItemsModel = new DefaultListModel<>();
        selectedItemsList = new JList<>(selectedItemsModel);
    }
    
    private void setupUI() {
        // Main container with padding
        JPanel mainContainer = new JPanel(new BorderLayout());
        mainContainer.setBackground(Color.WHITE);
        mainContainer.setBorder(BorderFactory.createEmptyBorder(30, 40, 30, 40));
        
        // Header
        JPanel headerPanel = createHeaderPanel();
        mainContainer.add(headerPanel, BorderLayout.NORTH);
        
        // Content in scroll pane
        JScrollPane scrollPane = new JScrollPane(createContentPanel());
        scrollPane.setBorder(null);
        scrollPane.setBackground(Color.WHITE);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        mainContainer.add(scrollPane, BorderLayout.CENTER);
        
        add(mainContainer, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 40, 0));
        
        // Title section
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel("Purchase Requisition");
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
        
        // Basic Information Card
        panel.add(createBasicInfoCard());
        panel.add(Box.createVerticalStrut(24));
        
        // Justification Card
        panel.add(createJustificationCard());
        panel.add(Box.createVerticalStrut(24));
        
        // Add Items Card
        panel.add(createAddItemsCard());
        panel.add(Box.createVerticalStrut(24));
        
        // Selected Items Card
        panel.add(createSelectedItemsCard());
        panel.add(Box.createVerticalStrut(32));
        
        // Action Buttons
        panel.add(createActionButtons());
        
        return panel;
    }
    
    private JPanel createBasicInfoCard() {
        JPanel card = createCard("Basic Information", "Enter the basic details for this purchase requisition");
        
        JPanel content = new JPanel(new GridBagLayout());
        content.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(12, 12, 12, 12);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Row 1
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 1;
        content.add(createFormField("PR ID", prIdField, false), gbc);
        
        gbc.gridx = 1;
        content.add(createFormField("Required Date", requiredDateField, true), gbc);
        
        // Row 2
        gbc.gridx = 0; gbc.gridy = 1;
        content.add(createFormField("Priority", priorityCombo), gbc);
        
        gbc.gridx = 1;
        content.add(createFormField("Department", departmentCombo), gbc);
        
        card.add(content, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel createJustificationCard() {
        JPanel card = createCard("Justification", "Provide a reason for this purchase requisition");
        
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        justificationArea.setFont(new Font("Inter", Font.PLAIN, 14));
        justificationArea.setLineWrap(true);
        justificationArea.setWrapStyleWord(true);
        justificationArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1),
            BorderFactory.createEmptyBorder(12, 12, 12, 12)
        ));
        justificationArea.setText("Enter the business justification for this purchase...");
        justificationArea.setForeground(GRAY_600);
        
        justificationArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (justificationArea.getText().equals("Enter the business justification for this purchase...")) {
                    justificationArea.setText("");
                    justificationArea.setForeground(GRAY_900);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (justificationArea.getText().trim().isEmpty()) {
                    justificationArea.setText("Enter the business justification for this purchase...");
                    justificationArea.setForeground(GRAY_600);
                }
            }
        });
        
        content.add(justificationArea, BorderLayout.CENTER);
        card.add(content, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel createAddItemsCard() {
        JPanel card = createCard("Add Items", "Select items to include in this requisition");
        
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        // Form panel with better layout
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(Color.WHITE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        
        // Item selection
        gbc.gridx = 0; gbc.gridy = 0; gbc.weightx = 0.4;
        formPanel.add(createFormField("Item", itemSearchField), gbc);
        
        // Quantity
        gbc.gridx = 1; gbc.weightx = 0.2;
        quantityField.setText("1");
        formPanel.add(createFormField("Quantity", quantityField, true), gbc);
        
        // Unit
        gbc.gridx = 2; gbc.weightx = 0.2;
        formPanel.add(createFormField("Unit", unitCombo), gbc);
        
        // Add button - positioned properly
        gbc.gridx = 3; gbc.weightx = 0.2;
        gbc.fill = GridBagConstraints.NONE;
        gbc.anchor = GridBagConstraints.SOUTHWEST;
        gbc.insets = new Insets(32, 16, 8, 8); // More top margin to align with fields
        
        JButton addButton = createStyledButton("Add Item", SUCCESS_GREEN, Color.WHITE);
        addButton.setPreferredSize(new Dimension(100, 40));
        addButton.setMinimumSize(new Dimension(100, 40));
        addButton.setMaximumSize(new Dimension(100, 40));
        addButton.addActionListener(e -> addItem());
        addButton.setOpaque(true); // Ensure button is opaque
        addButton.setVisible(true); // Explicitly set visible
        formPanel.add(addButton, gbc);
        
        content.add(formPanel, BorderLayout.CENTER);
        card.add(content, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel createSelectedItemsCard() {
        JPanel card = createCard("Selected Items", "Items included in this requisition");
        
        JPanel content = new JPanel(new BorderLayout());
        content.setBackground(Color.WHITE);
        content.setBorder(BorderFactory.createEmptyBorder(16, 16, 16, 16));
        
        // Configure list
        selectedItemsList.setFont(new Font("Inter", Font.PLAIN, 14));
        selectedItemsList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        selectedItemsList.setCellRenderer(new PRItemRenderer());
        selectedItemsList.setBackground(GRAY_50);
        
        JScrollPane scrollPane = new JScrollPane(selectedItemsList);
        scrollPane.setBorder(BorderFactory.createLineBorder(GRAY_300, 1));
        scrollPane.setPreferredSize(new Dimension(0, 200));
        
        // Remove button
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(Color.WHITE);
        
        JButton removeButton = createStyledButton("Remove Selected", DANGER_RED, Color.WHITE);
        removeButton.addActionListener(e -> removeSelectedItem());
        buttonPanel.add(removeButton);
        
        content.add(scrollPane, BorderLayout.CENTER);
        content.add(buttonPanel, BorderLayout.SOUTH);
        
        card.add(content, BorderLayout.CENTER);
        return card;
    }
    
    private JPanel createActionButtons() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 16, 20));
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        panel.setPreferredSize(new Dimension(0, 80)); // Ensure adequate height
        
        JButton cancelButton = createStyledButton("Cancel", Color.WHITE, GRAY_700);
        cancelButton.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_300, 1),
            BorderFactory.createEmptyBorder(12, 24, 12, 24)
        ));
        cancelButton.setPreferredSize(new Dimension(100, 45));
        cancelButton.setMinimumSize(new Dimension(100, 45));
        cancelButton.setMaximumSize(new Dimension(100, 45));
        cancelButton.setOpaque(true);
        cancelButton.setVisible(true);
        cancelButton.addActionListener(e -> clearForm());
        
        JButton draftButton = createStyledButton("Save as Draft", GRAY_600, Color.WHITE);
        draftButton.setPreferredSize(new Dimension(130, 45));
        draftButton.setMinimumSize(new Dimension(130, 45));
        draftButton.setMaximumSize(new Dimension(130, 45));
        draftButton.setOpaque(true);
        draftButton.setVisible(true);
        draftButton.addActionListener(e -> saveDraft());
        
        JButton submitButton = createStyledButton("Submit Requisition", PRIMARY_BLUE, Color.WHITE);
        submitButton.setPreferredSize(new Dimension(160, 45));
        submitButton.setMinimumSize(new Dimension(160, 45));
        submitButton.setMaximumSize(new Dimension(160, 45));
        submitButton.setOpaque(true);
        submitButton.setVisible(true);
        submitButton.addActionListener(e -> submitPR());
        
        panel.add(cancelButton);
        panel.add(draftButton);
        panel.add(submitButton);
        
        return panel;
    }
    
    private JPanel createCard(String title, String subtitle) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(0, 0, 0, 0)
        ));
        
        // Header
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(GRAY_50);
        header.setBorder(BorderFactory.createEmptyBorder(16, 20, 16, 20));
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Inter", Font.BOLD, 18));
        titleLabel.setForeground(GRAY_900);
        
        JLabel subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(new Font("Inter", Font.PLAIN, 14));
        subtitleLabel.setForeground(GRAY_600);
        
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(GRAY_50);
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(4));
        titlePanel.add(subtitleLabel);
        
        header.add(titlePanel, BorderLayout.WEST);
        card.add(header, BorderLayout.NORTH);
        
        return card;
    }
    
    private JPanel createFormField(String label, JComponent component) {
        return createFormField(label, component, true);
    }
    
    private JPanel createFormField(String label, JComponent component, boolean editable) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Color.WHITE);
        
        JLabel labelComponent = new JLabel(label);
        labelComponent.setFont(new Font("Inter", Font.BOLD, 14));
        labelComponent.setForeground(GRAY_700);
        labelComponent.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        component.setFont(new Font("Inter", Font.PLAIN, 14));
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (component instanceof JTextField) {
            JTextField field = (JTextField) component;
            field.setPreferredSize(new Dimension(200, 40));
            field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(GRAY_300, 1),
                BorderFactory.createEmptyBorder(10, 12, 10, 12)
            ));
            field.setEditable(editable);
            if (!editable) {
                field.setBackground(GRAY_100);
            }
        } else if (component instanceof JComboBox) {
            component.setPreferredSize(new Dimension(200, 40));
            component.setBorder(BorderFactory.createLineBorder(GRAY_300, 1));
        }
        
        panel.add(labelComponent);
        panel.add(Box.createVerticalStrut(6));
        panel.add(component);
        
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
        button.setOpaque(true); // Ensure button background is painted
        button.setContentAreaFilled(true); // Ensure content area is filled
        button.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        // Add hover effects
        button.addMouseListener(new MouseAdapter() {
            Color originalBg = bgColor;
            
            @Override
            public void mouseEntered(MouseEvent e) {
                if (bgColor.equals(PRIMARY_BLUE)) {
                    button.setBackground(new Color(29, 78, 216)); // Darker blue
                } else if (bgColor.equals(SUCCESS_GREEN)) {
                    button.setBackground(new Color(22, 163, 74)); // Darker green
                } else if (bgColor.equals(DANGER_RED)) {
                    button.setBackground(new Color(220, 38, 38)); // Darker red
                } else if (bgColor.equals(GRAY_600)) {
                    button.setBackground(new Color(55, 65, 81)); // Darker gray
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                button.setBackground(originalBg);
            }
        });
        
        return button;
    }
    
    private void initializeForm() {
        // Generate PR ID
        String prId = "PR-" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "-" + 
                     String.format("%03d", new Random().nextInt(999) + 1);
        prIdField.setText(prId);
        
        // Set required date (7 days from now)
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_MONTH, 7);
        requiredDateField.setText(new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime()));
        
        // Add sample item
        selectedItemsModel.addElement(new PRItem("ITM-001 - Rice (Premium)", "50", "kg"));
    }
    
    private void addItem() {
        String selectedItem = itemSearchField.getText().trim();
        String quantity = quantityField.getText().trim();
        String unit = (String) unitCombo.getSelectedItem();
        
        if (selectedItem.isEmpty() || selectedItem.equals("Search for items...") || 
            quantity.isEmpty() || unit.equals("Select Unit")) {
            JOptionPane.showMessageDialog(this, "Please fill in all item details.", "Incomplete Information", JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        try {
            Integer.parseInt(quantity);
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Please enter a valid quantity.", "Invalid Quantity", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        selectedItemsModel.addElement(new PRItem(selectedItem, quantity, unit));
        
        // Reset form
        itemSearchField.setText("Search for items...");
        itemSearchField.setForeground(GRAY_600);
        quantityField.setText("1");
        unitCombo.setSelectedIndex(0);
        
        JOptionPane.showMessageDialog(this, "Item added successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void removeSelectedItem() {
        int selectedIndex = selectedItemsList.getSelectedIndex();
        if (selectedIndex != -1) {
            selectedItemsModel.removeElementAt(selectedIndex);
        } else {
            JOptionPane.showMessageDialog(this, "Please select an item to remove.", "No Selection", JOptionPane.WARNING_MESSAGE);
        }
    }
    
    private void submitPR() {
        if (validateForm()) {
            JOptionPane.showMessageDialog(this,
                "Purchase Requisition submitted successfully!\n\n" +
                "PR ID: " + prIdField.getText() + "\n" +
                "Status: Pending Approval\n\n" +
                "You will receive an email notification once reviewed.",
                "Submission Successful",
                JOptionPane.INFORMATION_MESSAGE);
            clearForm();
        }
    }
    
    private void saveDraft() {
        JOptionPane.showMessageDialog(this,
            "Purchase Requisition saved as draft.\n\n" +
            "PR ID: " + prIdField.getText() + "\n" +
            "You can continue editing this later.",
            "Draft Saved",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void clearForm() {
        initializeForm();
        priorityCombo.setSelectedIndex(0);
        departmentCombo.setSelectedIndex(0);
        justificationArea.setText("Enter the business justification for this purchase...");
        justificationArea.setForeground(GRAY_600);
        itemSearchField.setText("Search for items...");
        itemSearchField.setForeground(GRAY_600);
        quantityField.setText("1");
        unitCombo.setSelectedIndex(0);
        selectedItemsModel.clear();
    }
    
    private boolean validateForm() {
        if (priorityCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a priority level.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (departmentCombo.getSelectedIndex() == 0) {
            JOptionPane.showMessageDialog(this, "Please select a department.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (justificationArea.getText().equals("Enter the business justification for this purchase...") || 
            justificationArea.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide justification for this purchase.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (selectedItemsModel.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please add at least one item to the requisition.", "Validation Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        return true;
    }
    
    // Inner classes
    private static class PRItem {
        private String item;
        private String quantity;
        private String unit;
        
        public PRItem(String item, String quantity, String unit) {
            this.item = item;
            this.quantity = quantity;
            this.unit = unit;
        }
        
        @Override
        public String toString() {
            return item + " - " + quantity + " " + unit;
        }
    }
    
    private class PRItemRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            setFont(new Font("Inter", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(12, 16, 12, 16));
            
            if (isSelected) {
                setBackground(PRIMARY_BLUE);
                setForeground(Color.WHITE);
            } else {
                setBackground(index % 2 == 0 ? Color.WHITE : GRAY_50);
                setForeground(GRAY_900);
            }
            
            return this;
        }
    }
    
    private class ItemSuggestionRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                boolean isSelected, boolean cellHasFocus) {
            super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
            
            setFont(new Font("Inter", Font.PLAIN, 14));
            setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            
            if (isSelected) {
                setBackground(PRIMARY_BLUE);
                setForeground(Color.WHITE);
            } else {
                setBackground(Color.WHITE);
                setForeground(GRAY_900);
            }
            
            // Highlight search text
            String text = value.toString();
            String searchText = itemSearchField.getText().trim();
            if (!searchText.isEmpty() && !searchText.equals("Search for items...")) {
                String highlightedText = highlightSearchText(text, searchText);
                setText(highlightedText);
            }
            
            return this;
        }
        
        private String highlightSearchText(String text, String searchText) {
            if (searchText.isEmpty()) return text;
            
            String lowerText = text.toLowerCase();
            String lowerSearch = searchText.toLowerCase();
            int index = lowerText.indexOf(lowerSearch);
            
            if (index >= 0) {
                String before = text.substring(0, index);
                String match = text.substring(index, index + searchText.length());
                String after = text.substring(index + searchText.length());
                return "<html>" + before + "<b>" + match + "</b>" + after + "</html>";
            }
            
            return text;
        }
    }
    
    private void setupItemSearchListeners() {
        // Focus listener for placeholder text
        itemSearchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (itemSearchField.getText().equals("Search for items...")) {
                    itemSearchField.setText("");
                    itemSearchField.setForeground(GRAY_900);
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (itemSearchField.getText().trim().isEmpty()) {
                    itemSearchField.setText("Search for items...");
                    itemSearchField.setForeground(GRAY_600);
                    itemSuggestionsPopup.setVisible(false);
                }
            }
        });
        
        // Document listener for real-time search
        itemSearchField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateSuggestions();
            }
            
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateSuggestions();
            }
            
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateSuggestions();
            }
        });
        
        // Key listener for navigation
        itemSearchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (itemSuggestionsPopup.isVisible()) {
                    int selectedIndex = itemSuggestionsList.getSelectedIndex();
                    
                    if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                        if (selectedIndex < itemSuggestionsModel.getSize() - 1) {
                            itemSuggestionsList.setSelectedIndex(selectedIndex + 1);
                        }
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_UP) {
                        if (selectedIndex > 0) {
                            itemSuggestionsList.setSelectedIndex(selectedIndex - 1);
                        }
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                        if (selectedIndex >= 0) {
                            selectItem(itemSuggestionsModel.getElementAt(selectedIndex));
                        }
                        e.consume();
                    } else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
                        itemSuggestionsPopup.setVisible(false);
                        e.consume();
                    }
                }
            }
        });
        
        // Mouse listener for item selection
        itemSuggestionsList.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 1) {
                    int index = itemSuggestionsList.locationToIndex(e.getPoint());
                    if (index >= 0) {
                        selectItem(itemSuggestionsModel.getElementAt(index));
                    }
                }
            }
        });
    }
    
    private void updateSuggestions() {
        String searchText = itemSearchField.getText().trim();
        
        if (searchText.isEmpty() || searchText.equals("Search for items...")) {
            itemSuggestionsPopup.setVisible(false);
            return;
        }
        
        itemSuggestionsModel.clear();
        String searchLower = searchText.toLowerCase();
        
        // Filter items based on search text
        for (String item : allItems) {
            if (item.toLowerCase().contains(searchLower)) {
                itemSuggestionsModel.addElement(item);
            }
        }
        
        if (itemSuggestionsModel.getSize() > 0) {
            itemSuggestionsList.setSelectedIndex(0);
            
            // Position popup below the search field
            Point location = itemSearchField.getLocationOnScreen();
            itemSuggestionsPopup.show(itemSearchField, 0, itemSearchField.getHeight());
        } else {
            itemSuggestionsPopup.setVisible(false);
        }
    }
    
    private void selectItem(String item) {
        itemSearchField.setText(item);
        itemSearchField.setForeground(GRAY_900);
        selectedItemCode = item;
        itemSuggestionsPopup.setVisible(false);
        quantityField.requestFocus();
    }
} 