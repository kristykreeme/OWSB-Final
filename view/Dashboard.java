package view;

import model.*;
import view.panels.*;

import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.awt.image.BufferedImage;

public class Dashboard extends JFrame {
    private User currentUser;
    
    // UI Components
    private JPanel contentPanel;
    private JPanel sidebarPanel;
    private JLabel userNameLabel;
    private JLabel userRoleLabel;
    
    // Sidebar buttons
    private Map<String, JButton> sidebarButtons = new HashMap<>();
    
    // Colors
    private Color sidebarColor = new Color(28, 37, 44); // Dark sidebar color
    private Color sidebarHoverColor = new Color(42, 55, 66); // Lighter sidebar color on hover
    private Color sidebarTextColor = new Color(220, 220, 220); // Light text color
    private Color backgroundColor = new Color(245, 245, 245); // Light gray
    private Color primaryBlue = new Color(0, 102, 204); // Blue for primary actions
    private Color successGreen = new Color(46, 184, 46); // Green for success indicators
    private Color warningOrange = new Color(255, 165, 0); // Orange for warnings
    private Color dangerRed = new Color(211, 47, 47); // Red for alerts
    
    private Map<String, JPanel> panels = new HashMap<>();
    
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
        
        // Create sidebar
        sidebarPanel = createSidebar();
        mainPanel.add(sidebarPanel, BorderLayout.WEST);
        
        // Create top bar
        JPanel topBar = createTopBar();
        mainPanel.add(topBar, BorderLayout.NORTH);
        
        // Create content panel
        contentPanel = new JPanel(new CardLayout());
        contentPanel.setBackground(backgroundColor);
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
        // Add panels based on user role
        addPanels();
        
        add(mainPanel);
        
        // Show default panel (Dashboard)
        showPanel("Dashboard");
    }
    
    private JPanel createSidebar() {
        JPanel sidebar = new JPanel();
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBackground(sidebarColor);
        sidebar.setPreferredSize(new Dimension(200, getHeight()));
        sidebar.setBorder(new EmptyBorder(0, 0, 0, 0));
        
        // Logo panel
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setBackground(sidebarColor);
        logoPanel.setBorder(new EmptyBorder(25, 20, 25, 20));
        logoPanel.setMaximumSize(new Dimension(200, 80));
        
        // User avatar (circle)
        JPanel avatarPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circle avatar
                g2d.setColor(new Color(150, 150, 150));
                int size = Math.min(40, 40);
                int x = 0;
                int y = 0;
                g2d.fillOval(x, y, size, size);
                
                g2d.dispose();
            }
        };
        avatarPanel.setPreferredSize(new Dimension(40, 40));
        avatarPanel.setMaximumSize(new Dimension(40, 40));
        avatarPanel.setOpaque(false);
        
        JLabel logoLabel = new JLabel("OWSB");
        logoLabel.setFont(new Font("Arial", Font.BOLD, 20));
        logoLabel.setForeground(Color.WHITE);
        logoLabel.setHorizontalAlignment(JLabel.CENTER);
        
        logoPanel.add(avatarPanel, BorderLayout.WEST);
        logoPanel.add(logoLabel, BorderLayout.CENTER);
        
        sidebar.add(logoPanel);
        
        // Add separator
        JSeparator separator = new JSeparator();
        separator.setForeground(new Color(60, 70, 80));
        separator.setBackground(new Color(60, 70, 80));
        separator.setMaximumSize(new Dimension(200, 1));
        sidebar.add(separator);
        
        // Add menu items with padding at top
        sidebar.add(Box.createVerticalStrut(20));
        
        // Dashboard button (always visible)
        addSidebarButton(sidebar, "Dashboard", "Dashboard", true);
        
        String role = currentUser.getRole();
        
        // Show different sidebar options based on the user role
        if (role.equals("ADMIN")) {
            // Admin sees all options
            addSidebarButton(sidebar, "Item Management", "Item Management", false);
            addSidebarButton(sidebar, "Supplier Management", "Supplier Management", false);
            addSidebarButton(sidebar, "Sales Data Entry", "Sales Data Entry", false);
            addSidebarButton(sidebar, "Purchase Requisition", "Purchase Requisition", false);
            addSidebarButton(sidebar, "Purchase Order", "Purchase Orders", false);
            addSidebarButton(sidebar, "User Management", "User Management", false);
        } else if (role.equals("SALES_MANAGER") || role.equals("SALES_STAFF")) {
            // Sales roles see sales-related options
            addSidebarButton(sidebar, "Sales Data Entry", "Sales Data Entry", false);
        } else if (role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF")) {
            // Purchase roles see purchase-related options
            addSidebarButton(sidebar, "Purchase Requisition", "Purchase Requisition", false);
            addSidebarButton(sidebar, "Purchase Order", "Purchase Orders", false);
            if (role.equals("PURCHASE_MANAGER")) {
                addSidebarButton(sidebar, "Supplier Management", "Supplier Management", false);
            }
        } else if (role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            // Inventory roles see inventory-related options
            addSidebarButton(sidebar, "Item Management", "Item Management", false);
            addSidebarButton(sidebar, "Inventory Management", "Inventory Management", false);
            if (role.equals("INVENTORY_MANAGER")) {
                addSidebarButton(sidebar, "Supplier Management", "Supplier Management", false);
                addSidebarButton(sidebar, "Purchase Requisition", "Purchase Requisition", false);
            }
        }
        
        // Reports (Everyone)
        addSidebarButton(sidebar, "Reports", "Reports", false);
        
        // Add user role indicator
        JPanel rolePanel = new JPanel(new BorderLayout());
        rolePanel.setBackground(sidebarColor);
        rolePanel.setBorder(new EmptyBorder(15, 20, 15, 20));
        rolePanel.setMaximumSize(new Dimension(200, 50));
        
        JLabel roleLabel = new JLabel(formatRole(currentUser.getRole()));
        roleLabel.setForeground(new Color(180, 180, 180));
        roleLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        rolePanel.add(roleLabel, BorderLayout.CENTER);
        
        // Add logout button at the bottom
        JPanel logoutPanel = new JPanel(new BorderLayout());
        logoutPanel.setBackground(sidebarColor);
        logoutPanel.setBorder(new EmptyBorder(10, 10, 20, 10));
        logoutPanel.setMaximumSize(new Dimension(200, 50));
        
        JButton logoutButton = new JButton("Logout");
        logoutButton.setFont(new Font("Arial", Font.PLAIN, 14));
        logoutButton.setBackground(sidebarColor);
        logoutButton.setForeground(sidebarTextColor);
        logoutButton.setHorizontalAlignment(SwingConstants.LEFT);
        logoutButton.setBorderPainted(false);
        logoutButton.setFocusPainted(false);
        logoutButton.setContentAreaFilled(false);
        logoutButton.addActionListener(e -> logout());
        
        logoutPanel.add(logoutButton, BorderLayout.CENTER);
        
        // Add glue to push logout and role to bottom
        sidebar.add(Box.createVerticalGlue());
        sidebar.add(rolePanel);
        sidebar.add(logoutPanel);
        
        return sidebar;
    }
    
    private void addSidebarButton(JPanel sidebar, String panelName, String buttonText, boolean isSelected) {
        JButton button = new JButton(buttonText);
        button.setHorizontalAlignment(SwingConstants.LEFT);
        button.setForeground(sidebarTextColor);
        button.setFont(new Font("Arial", Font.PLAIN, 14));
        
        if (isSelected) {
            button.setBackground(sidebarHoverColor);
        } else {
            button.setBackground(sidebarColor);
        }
        
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setContentAreaFilled(false);
        button.setMaximumSize(new Dimension(200, 40));
        button.setPreferredSize(new Dimension(200, 40));
        
        // Add padding
        button.setBorder(new EmptyBorder(10, 20, 10, 20));
        
        // Hover effect
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                button.setBackground(sidebarHoverColor);
                button.setContentAreaFilled(true);
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                if (!button.isSelected()) {
                    button.setContentAreaFilled(false);
                } else {
                    button.setBackground(sidebarHoverColor);
                    button.setContentAreaFilled(true);
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
            button.setContentAreaFilled(false);
            button.setSelected(false);
        }
        
        // Select the clicked button
        selectedButton.setBackground(sidebarHoverColor);
        selectedButton.setContentAreaFilled(true);
        selectedButton.setSelected(true);
    }
    
    private JPanel createTopBar() {
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(Color.WHITE);
        topBar.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createMatteBorder(0, 0, 1, 0, new Color(224, 224, 224)),
            new EmptyBorder(12, 20, 12, 20)
        ));
        
        // Search bar and user info
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 0));
        rightPanel.setBackground(Color.WHITE);
        
        // Search bar
        JTextField searchField = new JTextField(15);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224)),
            BorderFactory.createEmptyBorder(8, 8, 8, 8)
        ));
        searchField.setToolTipText("Search");
        
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.setBackground(Color.WHITE);
        searchPanel.add(searchField, BorderLayout.CENTER);
        
        // User icon
        JPanel userIconPanel = new JPanel(new BorderLayout());
        userIconPanel.setBackground(Color.WHITE);
        userIconPanel.setBorder(new EmptyBorder(0, 10, 0, 0));
        
        JLabel userIcon = new JLabel();
        userIcon.setIcon(createUserIcon());
        userIconPanel.add(userIcon, BorderLayout.CENTER);
        
        rightPanel.add(searchPanel);
        rightPanel.add(userIconPanel);
        
        topBar.add(rightPanel, BorderLayout.EAST);
        
        return topBar;
    }
    
    private Icon createUserIcon() {
        // Create a circular user icon
        ImageIcon icon = new ImageIcon(new BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB)) {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw circle
                g2d.setColor(new Color(200, 200, 200));
                g2d.fillOval(x, y, 32, 32);
                
                g2d.dispose();
            }
        };
        
        return icon;
    }
    
    private void addPanels() {
        // Dashboard panel (for all users)
        panels.put("Dashboard", new DashboardPanel(currentUser));
        
        // User Management panel (for admin only)
        if (currentUser.getRole().equals("ADMIN")) {
            panels.put("User Management", new UserManagementPanel(currentUser));
        }
        
        // Item Management panel (for admin and inventory roles)
        if (currentUser.getRole().equals("ADMIN") || 
            currentUser.getRole().equals("INVENTORY_MANAGER") ||
            currentUser.getRole().equals("INVENTORY_STAFF")) {
            panels.put("Item Management", new ItemManagementPanel(currentUser));
            panels.put("Inventory Management", new InventoryManagementPanel(currentUser));
        }
        
        // Supplier Management panel (for admin and inventory/purchase roles)
        if (currentUser.getRole().equals("ADMIN") || 
            currentUser.getRole().equals("INVENTORY_MANAGER") ||
            currentUser.getRole().equals("PURCHASE_MANAGER")) {
            panels.put("Supplier Management", new SupplierManagementPanel(currentUser));
        }
        
        // Sales Data Entry panel (for admin and sales roles)
        if (currentUser.getRole().equals("ADMIN") || 
            currentUser.getRole().equals("SALES_MANAGER") ||
            currentUser.getRole().equals("SALES_STAFF")) {
            panels.put("Sales Data Entry", new SalesDataEntryPanel(currentUser));
        }
        
        // Purchase Requisition panel (for admin, inventory and purchase roles)
        if (currentUser.getRole().equals("ADMIN") || 
            currentUser.getRole().equals("INVENTORY_MANAGER") ||
            currentUser.getRole().equals("PURCHASE_MANAGER") ||
            currentUser.getRole().equals("PURCHASE_STAFF")) {
            panels.put("Purchase Requisition", new PurchaseRequisitionPanel(currentUser));
        }
        
        // Purchase Order panel (for admin and purchase roles)
        if (currentUser.getRole().equals("ADMIN") || 
            currentUser.getRole().equals("PURCHASE_MANAGER") ||
            currentUser.getRole().equals("PURCHASE_STAFF")) {
            panels.put("Purchase Orders", new PurchaseOrderPanel(currentUser));
        }
        
        // Reports panel (for all users, but with different access levels)
        panels.put("Reports", new ReportsPanel(currentUser));
        
        // Add all panels to content panel
        for (Map.Entry<String, JPanel> entry : panels.entrySet()) {
            contentPanel.add(entry.getValue(), entry.getKey());
        }
    }
    
    private void showPanel(String panelName) {
        // Show the selected panel
        CardLayout cl = (CardLayout) contentPanel.getLayout();
        cl.show(contentPanel, panelName);
        
        // Update window title
        setTitle("OWSB - " + panelName);
    }
    
    private void logout() {
        int result = JOptionPane.showConfirmDialog(this,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION);
        
        if (result == JOptionPane.YES_OPTION) {
            // Close the dashboard
            this.dispose();
            
            // Open the login screen
            LoginScreen loginScreen = new LoginScreen();
            loginScreen.setVisible(true);
        }
    }
    
    private String formatRole(String role) {
        // Convert ADMIN to Admin, SALES_MANAGER to Sales Manager, etc.
        if (role == null || role.isEmpty()) {
            return "User";
        }
        
        String[] parts = role.split("_");
        StringBuilder result = new StringBuilder();
        
        for (String part : parts) {
            if (!part.isEmpty()) {
                result.append(part.substring(0, 1).toUpperCase())
                      .append(part.substring(1).toLowerCase())
                      .append(" ");
            }
        }
        
        return result.toString().trim();
    }
} 