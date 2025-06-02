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

public class DashboardPanel extends JPanel {
    private User currentUser;
    
    // Dashboard components
    private JPanel statsPanel;
    private JPanel recentActivitiesPanel;
    private JPanel topSellingPanel;
    
    // Colors matching the screenshot
    private Color primaryBlue = new Color(52, 152, 219);
    private Color lightBlue = new Color(174, 213, 255);
    private Color backgroundColor = new Color(248, 249, 250);
    private Color cardBackground = Color.WHITE;
    private Color textPrimary = new Color(33, 37, 41);
    private Color textSecondary = new Color(108, 117, 125);
    private Color warningBackground = new Color(255, 243, 205);
    private Color warningBorder = new Color(255, 193, 7);
    
    public DashboardPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        setBorder(new EmptyBorder(30, 40, 30, 40));
        
        // Create main content
        createContent();
        
        // Load dashboard data
        loadDashboardData();
    }
    
    private void createContent() {
        // Main content panel
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Header with title and user info
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel);
        contentPanel.add(Box.createVerticalStrut(30));
        
        // Stats cards row
        statsPanel = createStatsPanel();
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Low stock alert (only for inventory-related roles)
        if (shouldShowInventoryAlerts()) {
            JPanel alertPanel = createLowStockAlert();
            contentPanel.add(alertPanel);
            contentPanel.add(Box.createVerticalStrut(30));
        }
        
        // Bottom section with Recent Activities and Top Selling Items
        JPanel bottomPanel = new JPanel(new GridLayout(1, 2, 30, 0));
        bottomPanel.setOpaque(false);
        
        recentActivitiesPanel = createRecentActivitiesPanel();
        topSellingPanel = createTopSellingItemsPanel();
        
        bottomPanel.add(recentActivitiesPanel);
        bottomPanel.add(topSellingPanel);
        
        contentPanel.add(bottomPanel);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private boolean shouldShowInventoryAlerts() {
        String role = currentUser.getRole();
        return role.equals("ADMIN") || role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF");
    }
    
    private JPanel createHeaderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setOpaque(false);
        
        // Left side - Title (role-specific)
        String dashboardTitle = getDashboardTitle();
        JLabel titleLabel = new JLabel(dashboardTitle);
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 32));
        titleLabel.setForeground(textPrimary);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private String getDashboardTitle() {
        String role = currentUser.getRole();
        switch (role) {
            case "ADMIN":
                return "Management Dashboard";
            case "INVENTORY_MANAGER":
                return "Stock Management Dashboard";
            case "INVENTORY_STAFF":
                return "Inventory Dashboard";
            case "SALES_MANAGER":
                return "Sales Management Dashboard";
            case "SALES_STAFF":
                return "Sales Dashboard";
            case "PURCHASE_MANAGER":
                return "Purchase Management Dashboard";
            case "PURCHASE_STAFF":
                return "Purchase Dashboard";
            case "FINANCE_MANAGER":
                return "Finance Dashboard";
            default:
                return "Dashboard";
        }
    }
    
    private String getAvatarInitials() {
        String role = currentUser.getRole();
        switch (role) {
            case "ADMIN":
                return "AD";
            case "INVENTORY_MANAGER":
                return "SM"; // Stock Manager
            case "INVENTORY_STAFF":
                return "IS";
            case "SALES_MANAGER":
                return "SM";
            case "SALES_STAFF":
                return "SS";
            case "PURCHASE_MANAGER":
                return "PM";
            case "PURCHASE_STAFF":
                return "PS";
            case "FINANCE_MANAGER":
                return "FM";
            default:
                return "US"; // User
        }
    }
    
    private String getRoleDisplayName() {
        String role = currentUser.getRole();
        switch (role) {
            case "ADMIN":
                return "Admin User";
            case "INVENTORY_MANAGER":
                return "Stock Manager";
            case "INVENTORY_STAFF":
                return "Inventory Staff";
            case "SALES_MANAGER":
                return "Sales Manager";
            case "SALES_STAFF":
                return "Sales Staff";
            case "PURCHASE_MANAGER":
                return "Purchase Manager";
            case "PURCHASE_STAFF":
                return "Purchase Staff";
            case "FINANCE_MANAGER":
                return "Finance Manager";
            default:
                return "User";
        }
    }
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        
        String role = currentUser.getRole();
        
        // Role-specific statistics
        if (role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            // Stock Manager specific stats
            panel.add(createStatCard("1,245", "Total Items", primaryBlue));
            panel.add(createStatCard("15", "Low Stock Items", primaryBlue));
            panel.add(createStatCard("3", "Out of Stock", primaryBlue));
            panel.add(createStatCard("RM 245,680", "Total Stock Value", primaryBlue));
        } else if (role.equals("SALES_MANAGER") || role.equals("SALES_STAFF")) {
            // Sales specific stats
            panel.add(createStatCard("RM 125,450", "Monthly Sales", primaryBlue));
            panel.add(createStatCard("342", "Orders Processed", primaryBlue));
            panel.add(createStatCard("RM 8,750", "Daily Revenue", primaryBlue));
            panel.add(createStatCard("28", "Active Customers", primaryBlue));
        } else if (role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF")) {
            // Purchase specific stats
            panel.add(createStatCard("58", "Active Suppliers", primaryBlue));
            panel.add(createStatCard("23", "Pending Orders", primaryBlue));
            panel.add(createStatCard("RM 89,200", "Monthly Purchases", primaryBlue));
            panel.add(createStatCard("12", "New Requisitions", primaryBlue));
        } else if (role.equals("FINANCE_MANAGER")) {
            // Finance specific stats
            panel.add(createStatCard("RM 125,450", "Monthly Revenue", primaryBlue));
            panel.add(createStatCard("RM 89,200", "Monthly Expenses", primaryBlue));
            panel.add(createStatCard("RM 36,250", "Net Profit", primaryBlue));
            panel.add(createStatCard("15", "Pending Payments", primaryBlue));
        } else {
            // Admin or default stats
            panel.add(createStatCard("1,245", "Total Items", primaryBlue));
            panel.add(createStatCard("58", "Active Suppliers", primaryBlue));
            panel.add(createStatCard("RM 125,450", "Monthly Sales", primaryBlue));
            panel.add(createStatCard("15", "Low Stock Alerts", primaryBlue));
        }
        
        return panel;
    }
    
    private JPanel createStatCard(String value, String title, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(cardBackground);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        
        // Value label
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        // Title label
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        titleLabel.setForeground(textSecondary);
        titleLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(5));
        card.add(titleLabel);
        
        return card;
    }
    
    private JPanel createLowStockAlert() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(warningBackground);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(warningBorder, 1),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        String alertMessage = "Low Stock Alert! 15 items are running low on stock. Please review inventory levels.";
        if (currentUser.getRole().equals("INVENTORY_MANAGER")) {
            alertMessage = "Stock Manager Alert! 15 items require immediate attention. Review stock levels and initiate reorders.";
        }
        
        JLabel alertLabel = new JLabel(alertMessage);
        alertLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        alertLabel.setForeground(new Color(133, 100, 4));
        
        panel.add(alertLabel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRecentActivitiesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBackground);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Header
        JLabel headerLabel = new JLabel("Recent Activities");
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(textPrimary);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Role-specific activities
        Object[][] data = getRoleSpecificActivities();
        String[] columns = {"Activity", "User", "Time"};
        
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(245, 245, 245));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(textSecondary);
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private Object[][] getRoleSpecificActivities() {
        String role = currentUser.getRole();
        
        if (role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            return new Object[][]{
                {"Stock adjusted: Rice (Premium)", "Stock Manager", "5 min ago"},
                {"Low stock alert: Sugar (White)", "System", "15 min ago"},
                {"New supplier added", "Stock Manager", "1 hour ago"}
            };
        } else if (role.equals("SALES_MANAGER") || role.equals("SALES_STAFF")) {
            return new Object[][]{
                {"Sales order completed", "Sales Staff", "2 min ago"},
                {"Customer payment received", "Sales Manager", "20 min ago"},
                {"New customer registered", "Sales Staff", "45 min ago"}
            };
        } else if (role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF")) {
            return new Object[][]{
                {"Purchase order approved", "Purchase Manager", "10 min ago"},
                {"Supplier quote received", "Purchase Staff", "30 min ago"},
                {"Requisition submitted", "Purchase Staff", "1 hour ago"}
            };
        } else {
            // Default activities
            return new Object[][]{
                {"New item added", "John Doe", "2 min ago"},
                {"Supplier updated", "Jane Smith", "15 min ago"},
                {"Sales entry", "Mike Johnson", "1 hour ago"}
            };
        }
    }
    
    private JPanel createTopSellingItemsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(cardBackground);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        // Header - role specific
        String headerText = getRoleSpecificPanelTitle();
        JLabel headerLabel = new JLabel(headerText);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        headerLabel.setForeground(textPrimary);
        headerLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        // Table with role-specific data
        Object[][] data = getRoleSpecificTableData();
        String[] columns = getRoleSpecificTableColumns();
        
        DefaultTableModel model = new DefaultTableModel(data, columns) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        JTable table = new JTable(model);
        table.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        table.setRowHeight(35);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setSelectionBackground(new Color(245, 245, 245));
        
        // Header styling
        JTableHeader header = table.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 13));
        header.setForeground(textSecondary);
        header.setBackground(Color.WHITE);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(230, 230, 230)));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(null);
        scrollPane.getViewport().setBackground(Color.WHITE);
        
        panel.add(headerLabel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private String getRoleSpecificPanelTitle() {
        String role = currentUser.getRole();
        
        if (role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            return "Critical Stock Items";
        } else if (role.equals("SALES_MANAGER") || role.equals("SALES_STAFF")) {
            return "Top Selling Items";
        } else if (role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF")) {
            return "Pending Orders";
        } else if (role.equals("FINANCE_MANAGER")) {
            return "Payment Status";
        } else {
            return "Top Selling Items";
        }
    }
    
    private String[] getRoleSpecificTableColumns() {
        String role = currentUser.getRole();
        
        if (role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            return new String[]{"Item", "Current Stock", "Status"};
        } else if (role.equals("SALES_MANAGER") || role.equals("SALES_STAFF")) {
            return new String[]{"Item", "Sales", "Revenue"};
        } else if (role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF")) {
            return new String[]{"Supplier", "Order Value", "Status"};
        } else if (role.equals("FINANCE_MANAGER")) {
            return new String[]{"Invoice", "Amount", "Status"};
        } else {
            return new String[]{"Item", "Sales", "Revenue"};
        }
    }
    
    private Object[][] getRoleSpecificTableData() {
        String role = currentUser.getRole();
        
        if (role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            return new Object[][]{
                {"Sugar (White)", "15 kg", "Low Stock"},
                {"Oil (Cooking)", "0 ltr", "Out of Stock"},
                {"Flour (All Purpose)", "25 kg", "Low Stock"}
            };
        } else if (role.equals("SALES_MANAGER") || role.equals("SALES_STAFF")) {
            return new Object[][]{
                {"Rice (Premium)", "500 kg", "RM 1,750"},
                {"Sugar (White)", "300 kg", "RM 840"},
                {"Oil (Cooking)", "200 ltr", "RM 1,200"}
            };
        } else if (role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF")) {
            return new Object[][]{
                {"ABC Suppliers", "RM 15,000", "Pending"},
                {"XYZ Trading", "RM 8,500", "Approved"},
                {"Global Foods", "RM 12,300", "Processing"}
            };
        } else if (role.equals("FINANCE_MANAGER")) {
            return new Object[][]{
                {"INV-2025-001", "RM 5,200", "Paid"},
                {"INV-2025-002", "RM 3,800", "Pending"},
                {"INV-2025-003", "RM 7,100", "Overdue"}
            };
        } else {
            return new Object[][]{
                {"Rice (Premium)", "500 kg", "RM 1,750"},
                {"Sugar (White)", "300 kg", "RM 840"},
                {"Oil (Cooking)", "200 ltr", "RM 1,200"}
            };
        }
    }
    
    private void loadDashboardData() {
        // Load actual data here if needed
        // For now, using static data as shown in the screenshot
    }
} 