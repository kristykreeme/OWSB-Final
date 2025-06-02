package com.owsb.view.panels;

import com.owsb.model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class PaymentProcessingPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTable invoiceTable;
    private DefaultTableModel tableModel;
    private JTextField searchField;
    private JComboBox<String> statusFilter;
    private JComboBox<String> supplierFilter;
    
    // Colors matching the design
    private Color primaryBlue = new Color(52, 144, 220);
    private Color successGreen = new Color(46, 184, 46);
    private Color warningOrange = new Color(255, 165, 0);
    private Color dangerRed = new Color(211, 47, 47);
    private Color backgroundColor = Color.WHITE;
    private Color borderColor = new Color(224, 224, 224);
    private Color lightGray = new Color(248, 249, 250);
    
    public PaymentProcessingPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        
        initializeComponents();
        loadInvoiceData();
    }
    
    private void initializeComponents() {
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header section
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Controls section (search, filters, buttons)
        JPanel controlsPanel = createControlsPanel();
        
        // Table section
        JPanel tablePanel = createTablePanel();
        
        // Combine controls and table in center
        JPanel centerPanel = new JPanel(new BorderLayout());
        centerPanel.setBackground(backgroundColor);
        centerPanel.add(controlsPanel, BorderLayout.NORTH);
        centerPanel.add(tablePanel, BorderLayout.CENTER);
        
        mainPanel.add(centerPanel, BorderLayout.CENTER);
        
        add(mainPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Title and description
        JPanel titlePanel = new JPanel();
        titlePanel.setLayout(new BoxLayout(titlePanel, BoxLayout.Y_AXIS));
        titlePanel.setBackground(backgroundColor);
        titlePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel titleLabel = new JLabel("Payment Processing");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(55, 65, 81));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel descLabel = new JLabel("Manage supplier payments and invoices");
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        descLabel.setForeground(new Color(107, 114, 128));
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        titlePanel.add(titleLabel);
        titlePanel.add(Box.createVerticalStrut(8));
        titlePanel.add(descLabel);
        
        panel.add(titlePanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 25, 0));
        
        // Controls row
        JPanel controlsRow = new JPanel(new BorderLayout());
        controlsRow.setBackground(backgroundColor);
        
        // Left side - Search and filters
        JPanel leftControls = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        leftControls.setBackground(backgroundColor);
        
        // Search field
        searchField = new JTextField(20);
        searchField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        searchField.setPreferredSize(new Dimension(250, 40));
        
        // Add placeholder text
        searchField.setText("Search invoices...");
        searchField.setForeground(new Color(156, 163, 175));
        searchField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (searchField.getText().equals("Search invoices...")) {
                    searchField.setText("");
                    searchField.setForeground(new Color(55, 65, 81));
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (searchField.getText().trim().isEmpty()) {
                    searchField.setText("Search invoices...");
                    searchField.setForeground(new Color(156, 163, 175));
                }
            }
        });
        
        // Status filter
        String[] statuses = {"All Status", "Pending", "Overdue", "Paid"};
        statusFilter = new JComboBox<>(statuses);
        statusFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        statusFilter.setPreferredSize(new Dimension(120, 40));
        statusFilter.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        
        // Supplier filter
        String[] suppliers = {"All Suppliers", "ABC Trading Sdn Bhd", "XYZ Supplies Ltd", "Global Foods Enterprise"};
        supplierFilter = new JComboBox<>(suppliers);
        supplierFilter.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        supplierFilter.setPreferredSize(new Dimension(150, 40));
        supplierFilter.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        
        leftControls.add(searchField);
        leftControls.add(statusFilter);
        leftControls.add(supplierFilter);
        
        // Right side - Process Payment button
        JPanel rightControls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        rightControls.setBackground(backgroundColor);
        
        JButton processPaymentBtn = new JButton("Process Payment");
        processPaymentBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        processPaymentBtn.setBackground(primaryBlue);
        processPaymentBtn.setForeground(Color.WHITE);
        processPaymentBtn.setPreferredSize(new Dimension(150, 40));
        processPaymentBtn.setBorderPainted(false);
        processPaymentBtn.setFocusPainted(false);
        processPaymentBtn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        processPaymentBtn.addActionListener(e -> processPayment());
        
        rightControls.add(processPaymentBtn);
        
        controlsRow.add(leftControls, BorderLayout.WEST);
        controlsRow.add(rightControls, BorderLayout.EAST);
        
        panel.add(controlsRow, BorderLayout.NORTH);
        
        return panel;
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        
        // Create table
        String[] columns = {
            "Invoice No", "PO Reference", "Supplier", "Invoice Date", 
            "Due Date", "Amount", "Status", "Days Overdue", "Actions"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column == 8; // Only Actions column is editable
            }
        };
        
        invoiceTable = new JTable(tableModel);
        invoiceTable.setRowHeight(60);
        invoiceTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        invoiceTable.setGridColor(borderColor);
        invoiceTable.setShowGrid(true);
        invoiceTable.setSelectionBackground(new Color(239, 246, 255));
        invoiceTable.setSelectionForeground(new Color(55, 65, 81));
        
        // Header styling
        JTableHeader header = invoiceTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(lightGray);
        header.setForeground(new Color(55, 65, 81));
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
        
        // Set column widths
        TableColumnModel columnModel = invoiceTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(120); // Invoice No
        columnModel.getColumn(1).setPreferredWidth(120); // PO Reference
        columnModel.getColumn(2).setPreferredWidth(180); // Supplier
        columnModel.getColumn(3).setPreferredWidth(100); // Invoice Date
        columnModel.getColumn(4).setPreferredWidth(100); // Due Date
        columnModel.getColumn(5).setPreferredWidth(100); // Amount
        columnModel.getColumn(6).setPreferredWidth(80);  // Status
        columnModel.getColumn(7).setPreferredWidth(100); // Days Overdue
        columnModel.getColumn(8).setPreferredWidth(150); // Actions
        
        // Custom renderers
        invoiceTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        invoiceTable.getColumnModel().getColumn(8).setCellRenderer(new ActionButtonRenderer());
        invoiceTable.getColumnModel().getColumn(8).setCellEditor(new ActionButtonEditor());
        
        // Make sure the Actions column is properly configured
        invoiceTable.getColumnModel().getColumn(8).setMinWidth(150);
        invoiceTable.getColumnModel().getColumn(8).setMaxWidth(200);
        
        JScrollPane scrollPane = new JScrollPane(invoiceTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        scrollPane.setBackground(backgroundColor);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private void loadInvoiceData() {
        // Sample data matching the screenshot
        Object[][] data = {
            {"INV-2025-001", "PO-2025-001", "ABC Trading Sdn Bhd", "2025-05-20", "2025-06-19", "RM 759.00", "Pending", "-", new String[]{"Pay", "View"}},
            {"INV-2025-002", "PO-2025-002", "XYZ Supplies Ltd", "2025-05-15", "2025-05-20", "RM 1,245.50", "Overdue", "5 days", new String[]{"Pay Now", "View"}},
            {"INV-2025-003", "PO-2025-003", "Global Foods Enterprise", "2025-05-10", "2025-05-15", "RM 890.75", "Paid", "-", new String[]{"Receipt", "View"}}
        };
        
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }
    
    private void processPayment() {
        int selectedRow = invoiceTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, 
                "Please select an invoice to process payment.", 
                "No Selection", 
                JOptionPane.WARNING_MESSAGE);
            return;
        }
        
        String invoiceNo = (String) tableModel.getValueAt(selectedRow, 0);
        String supplier = (String) tableModel.getValueAt(selectedRow, 2);
        String amount = (String) tableModel.getValueAt(selectedRow, 5);
        
        showPaymentDialog(invoiceNo, supplier, amount, selectedRow);
    }
    
    private void showPaymentDialog(String invoiceNo, String supplier, String amount, int tableRow) {
        JDialog dialog = new JDialog((Frame) SwingUtilities.getWindowAncestor(this), "Process Payment", true);
        dialog.setSize(500, 700);
        dialog.setLocationRelativeTo(this);
        dialog.setResizable(true);
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header
        JLabel titleLabel = new JLabel("Process Payment");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(new Color(55, 65, 81));
        titleLabel.setHorizontalAlignment(SwingConstants.CENTER);
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        mainPanel.add(titleLabel, BorderLayout.NORTH);
        
        // Form panel with scroll pane
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setBackground(backgroundColor);
        formPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        // Invoice Number field
        JPanel invoicePanel = createFormField("Invoice Number", invoiceNo, false);
        formPanel.add(invoicePanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Supplier field
        JPanel supplierPanel = createFormField("Supplier", supplier, false);
        formPanel.add(supplierPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Invoice Amount field
        JPanel amountPanel = createFormField("Invoice Amount", amount, false);
        formPanel.add(amountPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Payment Method dropdown
        JPanel methodPanel = new JPanel();
        methodPanel.setLayout(new BoxLayout(methodPanel, BoxLayout.Y_AXIS));
        methodPanel.setBackground(backgroundColor);
        methodPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel methodLabel = new JLabel("Payment Method");
        methodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        methodLabel.setForeground(new Color(55, 65, 81));
        methodLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        String[] paymentMethods = {"Select Payment Method", "Bank Transfer", "Credit Card", "Cash", "Cheque"};
        JComboBox<String> methodCombo = new JComboBox<>(paymentMethods);
        methodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        methodCombo.setPreferredSize(new Dimension(400, 44));
        methodCombo.setMaximumSize(new Dimension(400, 44));
        methodCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        methodCombo.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        methodPanel.add(methodLabel);
        methodPanel.add(Box.createVerticalStrut(8));
        methodPanel.add(methodCombo);
        
        formPanel.add(methodPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Payment Date field
        JTextField dateField = new JTextField("05/25/2025");
        dateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateField.setPreferredSize(new Dimension(400, 44));
        dateField.setMaximumSize(new Dimension(400, 44));
        dateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        JPanel datePanel = createFormFieldWithComponent("Payment Date", dateField);
        formPanel.add(datePanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Reference Number field
        JTextField refField = new JTextField();
        refField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        refField.setPreferredSize(new Dimension(400, 44));
        refField.setMaximumSize(new Dimension(400, 44));
        refField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Add placeholder text
        refField.setText("Enter reference number");
        refField.setForeground(new Color(156, 163, 175));
        refField.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (refField.getText().equals("Enter reference number")) {
                    refField.setText("");
                    refField.setForeground(new Color(55, 65, 81));
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (refField.getText().trim().isEmpty()) {
                    refField.setText("Enter reference number");
                    refField.setForeground(new Color(156, 163, 175));
                }
            }
        });
        
        JPanel refPanel = createFormFieldWithComponent("Reference Number", refField);
        formPanel.add(refPanel);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Payment Notes field
        JTextArea notesArea = new JTextArea(4, 30);
        notesArea.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        notesArea.setLineWrap(true);
        notesArea.setWrapStyleWord(true);
        notesArea.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        // Add placeholder text
        notesArea.setText("Enter payment notes...");
        notesArea.setForeground(new Color(156, 163, 175));
        notesArea.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                if (notesArea.getText().equals("Enter payment notes...")) {
                    notesArea.setText("");
                    notesArea.setForeground(new Color(55, 65, 81));
                }
            }
            
            @Override
            public void focusLost(FocusEvent e) {
                if (notesArea.getText().trim().isEmpty()) {
                    notesArea.setText("Enter payment notes...");
                    notesArea.setForeground(new Color(156, 163, 175));
                }
            }
        });
        
        JScrollPane notesScrollPane = new JScrollPane(notesArea);
        notesScrollPane.setPreferredSize(new Dimension(400, 100));
        notesScrollPane.setMaximumSize(new Dimension(400, 100));
        notesScrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        
        JPanel notesPanel = createFormFieldWithComponent("Payment Notes", notesScrollPane);
        formPanel.add(notesPanel);
        
        // Add the form panel to a scroll pane
        JScrollPane formScrollPane = new JScrollPane(formPanel);
        formScrollPane.setBorder(BorderFactory.createEmptyBorder());
        formScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        formScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        formScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        mainPanel.add(formScrollPane, BorderLayout.CENTER);
        
        // Button panel
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        buttonPanel.setBackground(backgroundColor);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        JButton cancelButton = new JButton("Cancel");
        cancelButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        cancelButton.setPreferredSize(new Dimension(100, 40));
        cancelButton.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        cancelButton.setBackground(backgroundColor);
        cancelButton.setForeground(new Color(55, 65, 81));
        cancelButton.addActionListener(e -> dialog.dispose());
        
        JButton processButton = new JButton("Process Payment");
        processButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        processButton.setPreferredSize(new Dimension(150, 40));
        processButton.setBackground(primaryBlue);
        processButton.setForeground(Color.WHITE);
        processButton.setBorderPainted(false);
        processButton.setFocusPainted(false);
        processButton.addActionListener(e -> {
            // Validate form
            if (methodCombo.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(dialog, "Please select a payment method.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            
            // Process the payment
            tableModel.setValueAt("Paid", tableRow, 6);
            tableModel.setValueAt("-", tableRow, 7);
            tableModel.setValueAt(new String[]{"Receipt", "View"}, tableRow, 8);
            
            dialog.dispose();
            
            JOptionPane.showMessageDialog(this,
                "Payment processed successfully for " + invoiceNo + "\n\n" +
                "Payment Method: " + methodCombo.getSelectedItem() + "\n" +
                "Amount: " + amount + "\n" +
                "Date: " + dateField.getText(),
                "Payment Successful",
                JOptionPane.INFORMATION_MESSAGE);
        });
        
        buttonPanel.add(cancelButton);
        buttonPanel.add(processButton);
        
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);
        
        dialog.add(mainPanel);
        dialog.setVisible(true);
    }
    
    private JPanel createFormField(String labelText, String value, boolean editable) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(backgroundColor);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JTextField field = new JTextField(value);
        field.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        field.setPreferredSize(new Dimension(400, 44));
        field.setMaximumSize(new Dimension(400, 44));
        field.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        field.setEditable(editable);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        if (!editable) {
            field.setBackground(new Color(249, 250, 251));
            field.setForeground(new Color(107, 114, 128));
        }
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(field);
        
        return panel;
    }
    
    private JPanel createFormFieldWithComponent(String labelText, JComponent component) {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(backgroundColor);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JLabel label = new JLabel(labelText);
        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        label.setForeground(new Color(55, 65, 81));
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        component.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(label);
        panel.add(Box.createVerticalStrut(8));
        panel.add(component);
        
        return panel;
    }
    
    // Custom cell renderer for status column
    private class StatusCellRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JLabel label = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            label.setHorizontalAlignment(SwingConstants.CENTER);
            label.setOpaque(true);
            label.setFont(new Font("Segoe UI", Font.BOLD, 12));
            
            String status = (String) value;
            switch (status) {
                case "Pending":
                    label.setBackground(new Color(255, 243, 205)); // Light yellow
                    label.setForeground(new Color(146, 64, 14)); // Dark yellow
                    break;
                case "Overdue":
                    label.setBackground(new Color(254, 226, 226)); // Light red
                    label.setForeground(new Color(153, 27, 27)); // Dark red
                    break;
                case "Paid":
                    label.setBackground(new Color(220, 252, 231)); // Light green
                    label.setForeground(new Color(22, 101, 52)); // Dark green
                    break;
                default:
                    label.setBackground(Color.WHITE);
                    label.setForeground(Color.BLACK);
            }
            
            if (isSelected) {
                label.setBackground(table.getSelectionBackground());
                label.setForeground(table.getSelectionForeground());
            }
            
            return label;
        }
    }
    
    // Custom cell renderer for action buttons
    private class ActionButtonRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, 
                boolean isSelected, boolean hasFocus, int row, int column) {
            
            JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setOpaque(true);
            panel.setBackground(isSelected ? table.getSelectionBackground() : Color.WHITE);
            
            String[] actions = (String[]) value;
            if (actions != null) {
                for (String action : actions) {
                    JButton button = new JButton(action);
                    button.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    button.setPreferredSize(new Dimension(65, 28));
                    button.setBorderPainted(false);
                    button.setFocusPainted(false);
                    button.setOpaque(true);
                    
                    switch (action) {
                        case "Pay":
                            button.setBackground(successGreen);
                            button.setForeground(Color.WHITE);
                            break;
                        case "Pay Now":
                            button.setBackground(dangerRed);
                            button.setForeground(Color.WHITE);
                            break;
                        case "View":
                            button.setBackground(primaryBlue);
                            button.setForeground(Color.WHITE);
                            break;
                        case "Receipt":
                            button.setBackground(new Color(107, 114, 128));
                            button.setForeground(Color.WHITE);
                            break;
                    }
                    
                    panel.add(button);
                }
            }
            
            return panel;
        }
    }
    
    // Custom cell editor for action buttons
    private class ActionButtonEditor extends AbstractCellEditor implements TableCellEditor {
        private JPanel panel;
        private String[] actions;
        
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, 
                boolean isSelected, int row, int column) {
            
            panel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
            panel.setOpaque(true);
            panel.setBackground(Color.WHITE);
            
            actions = (String[]) value;
            if (actions != null) {
                for (String action : actions) {
                    JButton button = new JButton(action);
                    button.setFont(new Font("Segoe UI", Font.BOLD, 11));
                    button.setPreferredSize(new Dimension(65, 28));
                    button.setBorderPainted(false);
                    button.setFocusPainted(false);
                    button.setOpaque(true);
                    
                    switch (action) {
                        case "Pay":
                            button.setBackground(successGreen);
                            button.setForeground(Color.WHITE);
                            break;
                        case "Pay Now":
                            button.setBackground(dangerRed);
                            button.setForeground(Color.WHITE);
                            break;
                        case "View":
                            button.setBackground(primaryBlue);
                            button.setForeground(Color.WHITE);
                            break;
                        case "Receipt":
                            button.setBackground(new Color(107, 114, 128));
                            button.setForeground(Color.WHITE);
                            break;
                    }
                    
                    button.addActionListener(e -> handleAction(action, row));
                    panel.add(button);
                }
            }
            
            return panel;
        }
        
        @Override
        public Object getCellEditorValue() {
            return actions;
        }
        
        private void handleAction(String action, int row) {
            String invoiceNo = (String) tableModel.getValueAt(row, 0);
            String supplier = (String) tableModel.getValueAt(row, 2);
            String amount = (String) tableModel.getValueAt(row, 5);
            
            switch (action) {
                case "Pay":
                case "Pay Now":
                    showPaymentDialog(invoiceNo, supplier, amount, row);
                    break;
                    
                case "View":
                    JOptionPane.showMessageDialog(PaymentProcessingPanel.this,
                        "Viewing details for " + invoiceNo + "\n\nThis feature will open the invoice details.",
                        "Invoice Details",
                        JOptionPane.INFORMATION_MESSAGE);
                    break;
                    
                case "Receipt":
                    JOptionPane.showMessageDialog(PaymentProcessingPanel.this,
                        "Generating receipt for " + invoiceNo + "\n\nReceipt will be downloaded.",
                        "Receipt Generated",
                        JOptionPane.INFORMATION_MESSAGE);
                    break;
            }
            
            fireEditingStopped();
        }
    }
} 