package com.owsb.view;

import com.owsb.model.*;
import com.owsb.view.panels.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class Dashboard extends JFrame {
    private User currentUser;
    
    // UI Components
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JLabel notificationBadge;
    
    // Sidebar buttons
    private Map<String, JButton> sidebarButtons = new HashMap<>();
    
    // Colors - updated to match the exact design from images
    private Color sidebarColor = new Color(28, 37, 44); // Dark sidebar color
    private Color sidebarHoverColor = new Color(42, 55, 66); // Lighter sidebar color on hover
    private Color sidebarTextColor = Color.WHITE; // White text
    private Color backgroundColor = Color.WHITE; // White background
    private Color primaryBlue = new Color(25, 118, 210); // Blue for primary actions
    private Color successGreen = new Color(46, 184, 46); // Green for success indicators
    private Color warningOrange = new Color(255, 165, 0); // Orange for warnings
    private Color dangerRed = new Color(211, 47, 47); // Red for alerts
    private Color borderColor = new Color(224, 224, 224); // Light gray border
    
    private Map<String, JPanel> panels = new HashMap<>();
    private NotificationPanel notificationPanel;
    private javax.swing.Timer notificationTimer;
    
    public Dashboard(User user) {
        this.currentUser = user;
        
        // Setup frame
        setTitle("OWSB - Omega Wholesale Sdn Bhd");
        setSize(1200, 800);
        setMinimumSize(new Dimension(1000, 700));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Main panel with border layout
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        
        // Create sidebar with scroll pane (now includes user profile)
        JPanel sidebar = createSidebar();
        JScrollPane sidebarScrollPane = new JScrollPane(sidebar);
        sidebarScrollPane.setPreferredSize(new Dimension(280, getHeight()));
        sidebarScrollPane.setBorder(null);
        sidebarScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        sidebarScrollPane.getVerticalScrollBar().setUnitIncrement(16);
        sidebarScrollPane.getVerticalScrollBar().setBlockIncrement(64);
        sidebarScrollPane.setBackground(sidebarColor);
        sidebarScrollPane.getViewport().setBackground(sidebarColor);
        
        // Ensure the scroll pane respects the preferred size
        sidebarScrollPane.setMinimumSize(new Dimension(280, 0));
        sidebarScrollPane.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));
        
        // Enable mouse wheel scrolling
        sidebarScrollPane.setWheelScrollingEnabled(true);
        
        mainPanel.add(sidebarScrollPane, BorderLayout.WEST);
        
        // Create content panel with a small padding
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(backgroundColor);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        JPanel contentWrapper = new JPanel(new BorderLayout());
        contentWrapper.setBackground(backgroundColor);
        contentWrapper.add(contentPanel, BorderLayout.CENTER);
        
        mainPanel.add(contentWrapper, BorderLayout.CENTER);
        
        // Add panels based on user role
        addPanels();
        
        add(mainPanel);
        
        // Show default panel (Dashboard)
        showPanel("Dashboard");
        
        // Set up notification timer to check for new notifications
        setupNotificationTimer();
    }
    
    private void setupNotificationTimer() {
        // Check for new notifications every 30 seconds
        notificationTimer = new javax.swing.Timer(30000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                updateNotificationBadge();
            }
        });
        notificationTimer.start();
    }
    
    private void updateNotificationBadge() {
        if (notificationPanel != null && notificationBadge != null) {
            int unreadCount = notificationPanel.getUnreadNotificationCount();
            
            if (unreadCount > 0) {
                notificationBadge.setText(unreadCount > 9 ? "9+" : String.valueOf(unreadCount));
                notificationBadge.setVisible(true);
            } else {
                notificationBadge.setVisible(false);
            }
            
            // Refresh notification panel if it's currently visible
            if (panels.get("Notifications") == notificationPanel) {
                CardLayout cl = (CardLayout) contentPanel.getLayout();
                if (cl.toString().contains("Notifications")) {
                    notificationPanel.refreshNotifications();
                }
            }
        }
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sidebarColor);
        sidebar.setBorder(null);
        
        sidebar.setMinimumSize(new Dimension(280, 0));
        sidebar.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));
        sidebar.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Header with company logo and title
        JPanel headerPanel = new JPanel();
        headerPanel.setLayout(new BoxLayout(headerPanel, BoxLayout.Y_AXIS));
        headerPanel.setBackground(sidebarColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(280, Integer.MAX_VALUE));
        
        JLabel companyLabel = new JLabel("OMEGA WHOLESALE");
        companyLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        companyLabel.setForeground(new Color(52, 144, 220));
        companyLabel.setAlignmentX(0.0f);
        
        JLabel systemLabel = new JLabel("Management System");
        systemLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        systemLabel.setForeground(new Color(160, 170, 180));
        systemLabel.setAlignmentX(0.0f);
        
        headerPanel.add(companyLabel);
        headerPanel.add(Box.createVerticalStrut(5));
        headerPanel.add(systemLabel);
        
        sidebar.add(headerPanel);
        
        // Dashboard button (always first)
        addSidebarButton(sidebar, "Dashboard", "Dashboard", true);
        
        String role = currentUser.getRole();
        
        // USER MANAGEMENT SECTION (Admin only)
        if (role.equals("ADMIN")) {
            addSectionHeader(sidebar, "USER MANAGEMENT");
            addSidebarButton(sidebar, "User Registration", "User Registration", false);
            addSidebarButton(sidebar, "Manage Users", "Manage Users", false);
        }
        
        // INVENTORY SECTION
        if (role.equals("ADMIN") || 
            role.equals("SALES_MANAGER") || role.equals("SALES_STAFF") ||
            role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF") ||
            role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            
            addSectionHeader(sidebar, "INVENTORY");
            addSidebarButton(sidebar, "Item Management", "Item Management", false);
            addSidebarButton(sidebar, "Supplier Management", "Supplier Management", false);
            
            if (role.equals("ADMIN") || role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
                addSidebarButton(sidebar, "Inventory Management", "Stock Management", false);
            }
        }
        
        // SALES & OPERATIONS SECTION
        if (role.equals("ADMIN") || role.equals("SALES_MANAGER") || role.equals("SALES_STAFF")) {
            addSectionHeader(sidebar, "SALES & OPERATIONS");
            addSidebarButton(sidebar, "Sales Data Entry", "Daily Sales Entry", false);
            addSidebarButton(sidebar, "Reports", "Sales Reports", false);
        }
        
        // PURCHASE SECTION
        if (role.equals("ADMIN") || role.equals("SALES_MANAGER") || role.equals("SALES_STAFF") ||
            role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF")) {
            
            addSectionHeader(sidebar, "PURCHASE");
            addSidebarButton(sidebar, "Purchase Requisition", "Create Requisition", false);
            addSidebarButton(sidebar, "View Purchase Requisitions", "View Requisitions", false);
            
            if (role.equals("ADMIN") || role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF") ||
                role.equals("FINANCE_MANAGER") || role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
                addSidebarButton(sidebar, "Purchase Order", "Purchase Orders", false);
            }
        }
        
        // FINANCE SECTION
        if (role.equals("ADMIN") || role.equals("FINANCE_MANAGER")) {
            addSectionHeader(sidebar, "FINANCE");
            addSidebarButton(sidebar, "Payment Processing", "Payment Processing", false);
            addSidebarButton(sidebar, "Financial Reports", "Financial Reports", false);
        }
        
        // Add flexible space at bottom
        sidebar.add(Box.createVerticalGlue());
        
        // Add user profile badge at the bottom
        JPanel userProfileSection = createSidebarUserProfile();
        sidebar.add(userProfileSection);
        
        return sidebar;
    }
    
    private void addSectionHeader(JPanel sidebar, String title) {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(sidebarColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(25, 30, 15, 30));
        headerPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        headerPanel.setMaximumSize(new Dimension(280, 50));
        headerPanel.setMinimumSize(new Dimension(280, 50));
        headerPanel.setPreferredSize(new Dimension(280, 50));
        
        JLabel headerLabel = new JLabel(title);
        headerLabel.setFont(new Font("Segoe UI", Font.BOLD, 11));
        headerLabel.setForeground(new Color(120, 130, 140));
        
        headerPanel.add(headerLabel, BorderLayout.WEST);
        sidebar.add(headerPanel);
    }
    
    private void addSeparator(JPanel sidebar) {
        JPanel separatorPanel = new JPanel();
        separatorPanel.setBackground(sidebarColor);
        separatorPanel.setBorder(BorderFactory.createEmptyBorder(15, 20, 15, 8));
        separatorPanel.setMaximumSize(new Dimension(280, 35));
        separatorPanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        JPanel line = new JPanel();
        line.setBackground(new Color(60, 70, 80));
        line.setPreferredSize(new Dimension(252, 1));
        line.setMaximumSize(new Dimension(252, 1));
        
        separatorPanel.add(line);
        sidebar.add(separatorPanel);
    }
    
    private void addSidebarButton(JPanel sidebar, String panelName, String buttonText, boolean isSelected) {
        JButton button = new JButton(buttonText);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setForeground(sidebarTextColor);
        button.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        
        // Setting the appropriate background color for selected state
        if (isSelected) {
            button.setBackground(primaryBlue);
            button.setContentAreaFilled(true);
        } else {
            button.setBackground(sidebarColor);
            button.setContentAreaFilled(false);
        }
        
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Fixed button sizing for better alignment - ensure full width
        button.setMinimumSize(new Dimension(280, 45));
        button.setPreferredSize(new Dimension(280, 45));
        button.setMaximumSize(new Dimension(280, 45));
        
        // Better padding for proper text alignment - consistent with header
        button.setBorder(BorderFactory.createEmptyBorder(12, 30, 12, 30));
        
        // Improved hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!isSelected) {
                    button.setBackground(new Color(45, 55, 65));
                    button.setContentAreaFilled(true);
                }
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!isSelected) {
                    button.setContentAreaFilled(false);
                    button.setBackground(sidebarColor);
                }
            }
        });
        
        button.addActionListener(e -> {
            selectButton(button);
            showPanel(panelName);
        });
        
        sidebarButtons.put(panelName, button);
        sidebar.add(button);
    }
    
    private void selectButton(JButton selectedButton) {
        // Deselect all buttons
        for (JButton button : sidebarButtons.values()) {
            button.setBackground(sidebarColor);
            button.setContentAreaFilled(false);
        }
        
        // Select the clicked button
        selectedButton.setBackground(primaryBlue);
        selectedButton.setContentAreaFilled(true);
    }
    
    private void addPanels() {
        // Create and add all panels
        JPanel dashboardPanel = new DashboardPanel(currentUser);
        panels.put("Dashboard", dashboardPanel);
        contentPanel.add(dashboardPanel, "Dashboard");
        
        // Create and add workflow dashboard panel - COMMENTED OUT as panel was deleted
        // JPanel workflowDashboardPanel = new WorkflowDashboardPanel(currentUser);
        // panels.put("Workflow Dashboard", workflowDashboardPanel);
        // contentPanel.add(workflowDashboardPanel, "Workflow Dashboard");
        
        // Create and add notification panel
        notificationPanel = new NotificationPanel(currentUser);
        panels.put("Notifications", notificationPanel);
        contentPanel.add(notificationPanel, "Notifications");
        
        // Role-specific panels
        String role = currentUser.getRole();
        
        // For all user types, add Item Management panel
        if (role.equals("ADMIN") || 
            role.equals("SALES_MANAGER") || role.equals("SALES_STAFF") ||
            role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF") ||
            role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            
            JPanel itemPanel = new ItemManagementPanel(currentUser);
            panels.put("Item Management", itemPanel);
            contentPanel.add(itemPanel, "Item Management");
        }
        
        // For all user types except Finance Manager, add Supplier Management panel
        if (role.equals("ADMIN") || 
            role.equals("SALES_MANAGER") || role.equals("SALES_STAFF") ||
            role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF") ||
            role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            
            JPanel supplierPanel = new SupplierManagementPanel(currentUser);
            panels.put("Supplier Management", supplierPanel);
            contentPanel.add(supplierPanel, "Supplier Management");
        }
        
        // For Sales roles, add Sales Data Entry panel
        if (role.equals("ADMIN") ||
            role.equals("SALES_MANAGER") || role.equals("SALES_STAFF")) {
            
            JPanel salesPanel = new SalesDataEntryPanel(currentUser);
            panels.put("Sales Data Entry", salesPanel);
            contentPanel.add(salesPanel, "Sales Data Entry");
        }
        
        // For all user types except Inventory, add Purchase Requisition panel
        if (role.equals("ADMIN") ||
            role.equals("SALES_MANAGER") || role.equals("SALES_STAFF") ||
            role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF") ||
            role.equals("FINANCE_MANAGER")) {
            
            JPanel prPanel = new PurchaseRequisitionPanel(currentUser);
            panels.put("Purchase Requisition", prPanel);
            contentPanel.add(prPanel, "Purchase Requisition");
            
            // Add View Purchase Requisitions panel
            ViewPurchaseRequisitionsPanel viewPRPanel = new ViewPurchaseRequisitionsPanel(currentUser);
            viewPRPanel.setParentDashboard(this); // Set parent dashboard reference
            panels.put("View Purchase Requisitions", viewPRPanel);
            contentPanel.add(viewPRPanel, "View Purchase Requisitions");
        }
        
        // For Purchase, Finance and Inventory roles, add Purchase Order panel
        if (role.equals("ADMIN") ||
            role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF") ||
            role.equals("FINANCE_MANAGER") ||
            role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            
            JPanel poPanel = new PurchaseOrderPanel(currentUser);
            panels.put("Purchase Order", poPanel);
            contentPanel.add(poPanel, "Purchase Order");
        }
        
        // Only Admin can access User Management
        if (role.equals("ADMIN")) {
            JPanel userRegistrationPanel = new UserRegistrationPanel(currentUser);
            panels.put("User Registration", userRegistrationPanel);
            contentPanel.add(userRegistrationPanel, "User Registration");
            
            JPanel userPanel = new UserManagementPanel(currentUser);
            panels.put("Manage Users", userPanel);
            contentPanel.add(userPanel, "Manage Users");
        }
        
        // For Inventory roles, add Inventory Management panel
        if (role.equals("ADMIN") ||
            role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            
            JPanel inventoryPanel = new InventoryManagementPanel(currentUser);
            panels.put("Inventory Management", inventoryPanel);
            contentPanel.add(inventoryPanel, "Inventory Management");
        }
        
        // All roles can see Reports
        JPanel reportsPanel = new ReportsPanel(currentUser);
        panels.put("Reports", reportsPanel);
        contentPanel.add(reportsPanel, "Reports");
        
        // Add default panels for Finance section
        if (role.equals("ADMIN") || role.equals("FINANCE_MANAGER")) {
            JPanel paymentPanel = new PaymentProcessingPanel(currentUser);
            panels.put("Payment Processing", paymentPanel);
            contentPanel.add(paymentPanel, "Payment Processing");
            
            JPanel financialReportsPanel = new FinancialReportsPanel(currentUser);
            panels.put("Financial Reports", financialReportsPanel);
            contentPanel.add(financialReportsPanel, "Financial Reports");
        }
        
        // Add default panel for options that are not implemented
        for (String buttonText : sidebarButtons.keySet()) {
            if (!panels.containsKey(buttonText)) {
                JPanel defaultPanel = createDefaultPanel(buttonText);
                panels.put(buttonText, defaultPanel);
                contentPanel.add(defaultPanel, buttonText);
            }
        }
    }
    
    private JPanel createDefaultPanel(String panelName) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        
        JLabel label = new JLabel("Coming Soon: " + panelName);
        label.setFont(new Font("Arial", Font.BOLD, 24));
        label.setHorizontalAlignment(JLabel.CENTER);
        
        panel.add(label, BorderLayout.CENTER);
        
        return panel;
    }
    
    // Public method to show a specific panel
    public void showPanel(String panelName) {
        CardLayout cardLayout = (CardLayout) contentPanel.getLayout();
        cardLayout.show(contentPanel, panelName);
        
        // If showing notifications, refresh them
        if (panelName.equals("Notifications")) {
            notificationPanel.refreshNotifications();
            updateNotificationBadge();
        }
        
        // If showing workflow dashboard, refresh it
        if (panelName.equals("Workflow Dashboard")) {
            // WorkflowDashboardPanel workflowPanel = (WorkflowDashboardPanel) panels.get("Workflow Dashboard");
            // workflowPanel.refreshDashboard();
        }
    }
    
    // Add method to get a panel by name
    public JPanel getPanelByName(String panelName) {
        return panels.get(panelName);
    }
    
    private void logout() {
        // Stop notification timer
        if (notificationTimer != null) {
            notificationTimer.stop();
        }
        
        dispose();
        new LoginScreen().setVisible(true);
    }
    
    private JPanel createSidebarUserProfile() {
        JPanel profilePanel = new JPanel(new BorderLayout());
        profilePanel.setBackground(new Color(35, 45, 55)); // Slightly lighter than sidebar
        profilePanel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(1, 0, 0, 0, new Color(60, 70, 80)),
            BorderFactory.createEmptyBorder(15, 30, 15, 30)
        ));
        profilePanel.setMaximumSize(new Dimension(280, 80));
        profilePanel.setPreferredSize(new Dimension(280, 80));
        profilePanel.setMinimumSize(new Dimension(280, 80));
        profilePanel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        // Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(primaryBlue);
                g2d.fillOval(0, 0, 40, 40);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                String initials = getUserInitials();
                int x = (40 - fm.stringWidth(initials)) / 2;
                int y = (40 + fm.getAscent()) / 2 - 2;
                g2d.drawString(initials, x, y);
                g2d.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(40, 40));
        avatar.setOpaque(false);
        
        // User info
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBackground(new Color(35, 45, 55));
        userInfo.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        
        JLabel nameLabel = new JLabel(currentUser.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 13));
        nameLabel.setForeground(Color.WHITE);
        
        JLabel roleLabel = new JLabel(getRoleDisplayName());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        roleLabel.setForeground(new Color(180, 190, 200));
        
        userInfo.add(nameLabel);
        userInfo.add(roleLabel);
        
        // Make the profile clickable for logout
        profilePanel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        profilePanel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showUserMenu(profilePanel, e.getX(), e.getY());
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                profilePanel.setBackground(new Color(45, 55, 65));
                userInfo.setBackground(new Color(45, 55, 65));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                profilePanel.setBackground(new Color(35, 45, 55));
                userInfo.setBackground(new Color(35, 45, 55));
            }
        });
        
        profilePanel.add(avatar, BorderLayout.WEST);
        profilePanel.add(userInfo, BorderLayout.CENTER);
        
        return profilePanel;
    }
    
    private String getUserInitials() {
        String role = currentUser.getRole();
        switch (role) {
            case "ADMIN": return "AD";
            case "PURCHASE_MANAGER": return "PM";
            case "PURCHASE_STAFF": return "PS";
            case "SALES_MANAGER": return "SM";
            case "SALES_STAFF": return "SS";
            case "FINANCE_MANAGER": return "FM";
            case "FINANCE_STAFF": return "FS";
            case "INVENTORY_MANAGER": return "IM";
            case "INVENTORY_STAFF": return "IS";
            default: return "US";
        }
    }
    
    private String getRoleDisplayName() {
        String role = currentUser.getRole();
        switch (role) {
            case "ADMIN": return "Administrator";
            case "PURCHASE_MANAGER": return "Purchase Manager";
            case "PURCHASE_STAFF": return "Purchase Staff";
            case "SALES_MANAGER": return "Sales Manager";
            case "SALES_STAFF": return "Sales Staff";
            case "FINANCE_MANAGER": return "Finance Manager";
            case "FINANCE_STAFF": return "Finance Staff";
            case "INVENTORY_MANAGER": return "Inventory Manager";
            case "INVENTORY_STAFF": return "Inventory Staff";
            default: return "User";
        }
    }
    
    private void showUserMenu(JComponent parent, int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(60, 70, 80), 1),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));
        
        // User info header
        JMenuItem userInfoItem = new JMenuItem("<html><b>" + currentUser.getName() + "</b><br/>" + 
                                              "<font color='gray'>" + currentUser.getUsername() + "@owsb.com</font></html>");
        userInfoItem.setEnabled(false);
        userInfoItem.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        menu.add(userInfoItem);
        
        menu.addSeparator();
        
        // Logout option
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        logoutItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        logoutItem.addActionListener(e -> logout());
        menu.add(logoutItem);
        
        // Show menu above the profile panel
        menu.show(parent, x, y - menu.getPreferredSize().height);
    }
} 