package view.panels;

import model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;

public class DashboardPanel extends JPanel {
    private User currentUser;
    
    // Dashboard components
    private JPanel statsPanel;
    private JPanel recentOrdersPanel;
    private JPanel chartsPanel;
    private JPanel welcomePanel;
    
    // Colors
    private Color primaryBlue = new Color(0, 102, 204);
    private Color successGreen = new Color(46, 184, 46);
    private Color warningOrange = new Color(255, 165, 0);
    private Color lightGray = new Color(245, 245, 245);
    private Color dangerRed = new Color(211, 47, 47);
    
    public DashboardPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(lightGray);
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Welcome panel at the top
        welcomePanel = createWelcomePanel();
        
        // Main content panel with BoxLayout for vertical stacking
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setOpaque(false);
        
        // Add welcome panel
        contentPanel.add(welcomePanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Stats cards - will be created based on user role
        statsPanel = createStatsPanel();
        contentPanel.add(statsPanel);
        contentPanel.add(Box.createVerticalStrut(20));
        
        // Split panel for charts and recent activity
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        splitPanel.setOpaque(false);
        
        // Create specific panels based on user role
        if (currentUser.getRole().equals("ADMIN")) {
            // Admin sees everything
            recentOrdersPanel = createRecentOrdersPanel();
            chartsPanel = createChartsPanel("Monthly Overview", true);
        } else if (currentUser.getRole().equals("SALES_MANAGER") || 
                  currentUser.getRole().equals("SALES_STAFF")) {
            // Sales role focuses on sales data
            recentOrdersPanel = createRecentSalesPanel();
            chartsPanel = createChartsPanel("Sales Performance", false);
        } else if (currentUser.getRole().equals("PURCHASE_MANAGER") || 
                  currentUser.getRole().equals("PURCHASE_STAFF")) {
            // Purchase role focuses on POs and PRs
            recentOrdersPanel = createRecentOrdersPanel();
            chartsPanel = createChartsPanel("Purchase Overview", true);
        } else if (currentUser.getRole().equals("INVENTORY_MANAGER") || 
                  currentUser.getRole().equals("INVENTORY_STAFF")) {
            // Inventory role focuses on stock levels
            recentOrdersPanel = createLowStockPanel();
            chartsPanel = createChartsPanel("Inventory Status", false);
        } else {
            // Default for any other role
            recentOrdersPanel = createRecentOrdersPanel();
            chartsPanel = createChartsPanel("Monthly Overview", true);
        }
        
        splitPanel.add(recentOrdersPanel);
        splitPanel.add(chartsPanel);
        
        contentPanel.add(splitPanel);
        
        add(contentPanel, BorderLayout.CENTER);
        
        // Load dashboard data
        loadDashboardData();
    }
    
    private JPanel createWelcomePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 20, 15, 20)
        ));
        
        String role = formatRoleName(currentUser.getRole());
        String welcomeText = "Welcome, " + currentUser.getName() + " | " + role;
        
        JLabel welcomeLabel = new JLabel(welcomeText);
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 16));
        
        // Get current date and time
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, MMMM d, yyyy");
        JLabel dateLabel = new JLabel(dateFormat.format(new Date()));
        dateLabel.setForeground(Color.GRAY);
        
        // Panel for welcome message and date
        JPanel infoPanel = new JPanel(new BorderLayout());
        infoPanel.setOpaque(false);
        infoPanel.add(welcomeLabel, BorderLayout.NORTH);
        infoPanel.add(dateLabel, BorderLayout.SOUTH);
        
        panel.add(infoPanel, BorderLayout.WEST);
        
        return panel;
    }
    
    private String formatRoleName(String role) {
        if (role == null || role.isEmpty()) return "User";
        
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
    
    private JPanel createStatsPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 20, 0));
        panel.setOpaque(false);
        
        String role = currentUser.getRole();
        
        if (role.equals("ADMIN")) {
            // Admin sees all key metrics
            panel.add(createStatCard("Total Items", "248", primaryBlue));
            panel.add(createStatCard("Active Suppliers", "35", successGreen));
            panel.add(createStatCard("Pending POs", "12", warningOrange));
            panel.add(createStatCard("Low Stock", "5", dangerRed));
        } else if (role.equals("SALES_MANAGER") || role.equals("SALES_STAFF")) {
            // Sales role metrics
            panel.add(createStatCard("Total Sales", "RM 45,236", primaryBlue));
            panel.add(createStatCard("Monthly Target", "78%", successGreen));
            panel.add(createStatCard("Top Products", "8", warningOrange));
            panel.add(createStatCard("New Orders", "14", primaryBlue));
        } else if (role.equals("PURCHASE_MANAGER") || role.equals("PURCHASE_STAFF")) {
            // Purchase role metrics
            panel.add(createStatCard("Pending POs", "12", warningOrange));
            panel.add(createStatCard("Pending PRs", "8", primaryBlue));
            panel.add(createStatCard("Active Suppliers", "35", successGreen));
            panel.add(createStatCard("Deliveries Due", "6", dangerRed));
        } else if (role.equals("INVENTORY_MANAGER") || role.equals("INVENTORY_STAFF")) {
            // Inventory role metrics
            panel.add(createStatCard("Total Items", "248", primaryBlue));
            panel.add(createStatCard("Low Stock", "5", dangerRed));
            panel.add(createStatCard("Stock Adjustments", "23", warningOrange));
            panel.add(createStatCard("Total Value", "RM 128,450", successGreen));
        } else {
            // Default metrics
            panel.add(createStatCard("Total Items", "248", primaryBlue));
            panel.add(createStatCard("Active Suppliers", "35", successGreen));
            panel.add(createStatCard("Pending POs", "12", warningOrange));
        }
        
        return panel;
    }
    
    private JPanel createStatCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        
        // Value
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        contentPanel.add(valueLabel, BorderLayout.CENTER);
        
        // Title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        contentPanel.add(titleLabel, BorderLayout.SOUTH);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createRecentOrdersPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel title
        JLabel titleLabel = new JLabel("Recent Purchase Orders");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create table with PO data
        String[] columns = {"PO ID", "Date", "Supplier", "Status"};
        Object[][] data = {
            {"PO-2023-001", "10/05/2023", "Supplier A", "Pending"},
            {"PO-2023-002", "12/05/2023", "Supplier B", "Approved"},
            {"PO-2023-003", "15/05/2023", "Supplier C", "Pending"}
        };
        
        JTable table = new JTable(data, columns);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Custom renderer to add colored status labels
        table.getColumnModel().getColumn(3).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setHorizontalAlignment(JLabel.CENTER);
                label.setBorder(BorderFactory.createEmptyBorder(0, 4, 0, 4));
                
                if ("Pending".equals(value)) {
                    label.setForeground(warningOrange);
                } else if ("Approved".equals(value)) {
                    label.setForeground(successGreen);
                }
                
                return label;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createRecentSalesPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel title
        JLabel titleLabel = new JLabel("Recent Sales");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create table with sales data
        String[] columns = {"Date", "Item", "Quantity", "Amount (RM)"};
        Object[][] data = {
            {"18/05/2023", "Basmati Rice 5kg", "30", "777.00"},
            {"17/05/2023", "Fresh Milk 1L", "45", "355.50"},
            {"17/05/2023", "Olive Oil 1L", "12", "430.80"},
            {"16/05/2023", "Flour 1kg", "25", "122.50"}
        };
        
        JTable table = new JTable(data, columns);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createLowStockPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel title
        JLabel titleLabel = new JLabel("Low Stock Items");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        // Create table with low stock items
        String[] columns = {"Item Code", "Item Name", "Current Stock", "Reorder Level"};
        Object[][] data = {
            {"I036", "Organic Quinoa 500g", "8", "10"},
            {"I037", "Truffle Oil 100ml", "5", "10"},
            {"I038", "Saffron 1g", "3", "5"},
            {"I039", "Maple Syrup 250ml", "7", "10"},
            {"I040", "Vanilla Pods 5pc", "2", "5"}
        };
        
        JTable table = new JTable(data, columns);
        table.setRowHeight(30);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        
        // Custom renderer to color low stock items in red
        table.getColumnModel().getColumn(2).setCellRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                JLabel label = new JLabel(value.toString());
                label.setHorizontalAlignment(JLabel.CENTER);
                
                // Get reorder level from column 3
                int reorderLevel = Integer.parseInt(table.getValueAt(row, 3).toString());
                int currentStock = Integer.parseInt(value.toString());
                
                if (currentStock <= reorderLevel) {
                    label.setForeground(dangerRed);
                    label.setFont(label.getFont().deriveFont(Font.BOLD));
                }
                
                return label;
            }
        });
        
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createEmptyBorder());
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createChartsPanel(String title, boolean showBarChart) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(Color.WHITE);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(230, 230, 230)),
            BorderFactory.createEmptyBorder(15, 15, 15, 15)
        ));
        
        // Panel title
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 16));
        panel.add(titleLabel, BorderLayout.NORTH);
        
        if (showBarChart) {
            // Bar chart panel
            JPanel chartPanel = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int width = getWidth();
                    int height = getHeight();
                    int padding = 40;
                    int chartWidth = width - 2 * padding;
                    int chartHeight = height - 2 * padding;
                    
                    // Draw bars
                    int barCount = 6;
                    int barWidth = chartWidth / (barCount * 2);
                    
                    // Sample data
                    int[] values = {180, 210, 170, 240, 220, 200};
                    int maxValue = 250;
                    
                    // Draw axes
                    g2d.setColor(new Color(220, 220, 220));
                    g2d.drawLine(padding, height - padding, width - padding, height - padding); // X-axis
                    
                    // Draw grid lines
                    g2d.setColor(new Color(240, 240, 240));
                    for (int i = 1; i <= 5; i++) {
                        int y = height - padding - (i * chartHeight / 5);
                        g2d.drawLine(padding, y, width - padding, y);
                    }
                    
                    // Draw bars
                    for (int i = 0; i < barCount; i++) {
                        int barHeight = (int) ((values[i] / (double) maxValue) * chartHeight);
                        int x = padding + i * (chartWidth / barCount) + (chartWidth / barCount - barWidth) / 2;
                        int y = height - padding - barHeight;
                        
                        // Create gradient for bars
                        GradientPaint gradient = new GradientPaint(
                            x, y, primaryBlue, 
                            x, height - padding, new Color(primaryBlue.getRed(), primaryBlue.getGreen(), primaryBlue.getBlue(), 160)
                        );
                        g2d.setPaint(gradient);
                        g2d.fillRect(x, y, barWidth, barHeight);
                        
                        // Draw month labels
                        g2d.setColor(Color.DARK_GRAY);
                        String[] months = {"Jan", "Feb", "Mar", "Apr", "May", "Jun"};
                        g2d.drawString(months[i], x + barWidth/2 - 10, height - padding + 20);
                    }
                    
                    g2d.dispose();
                }
            };
            
            panel.add(chartPanel, BorderLayout.CENTER);
        } else {
            // Pie chart panel
            JPanel piePanel = new JPanel(new BorderLayout());
            piePanel.setOpaque(false);
            
            // Pie chart
            JPanel pieChart = new JPanel() {
                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2d = (Graphics2D) g.create();
                    g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    
                    int width = getWidth();
                    int height = getHeight();
                    
                    // Define pie chart dimensions
                    int diameter = Math.min(width, height) - 50;
                    int x = (width - diameter) / 2;
                    int y = (height - diameter) / 2;
                    
                    // Sample data
                    int[] percentages = {35, 25, 20, 10, 10};
                    Color[] colors = {
                        new Color(41, 128, 185),   // Blue
                        new Color(46, 204, 113),   // Green
                        new Color(155, 89, 182),   // Purple
                        new Color(243, 156, 18),   // Orange
                        new Color(231, 76, 60)     // Red
                    };
                    
                    // Draw pie slices
                    int startAngle = 0;
                    for (int i = 0; i < percentages.length; i++) {
                        int arcAngle = Math.round(360 * percentages[i] / 100f);
                        g2d.setColor(colors[i]);
                        g2d.fillArc(x, y, diameter, diameter, startAngle, arcAngle);
                        startAngle += arcAngle;
                    }
                    
                    // Draw white circle in center for donut effect
                    g2d.setColor(Color.WHITE);
                    int innerDiameter = diameter / 2;
                    g2d.fillOval(x + (diameter - innerDiameter) / 2, y + (diameter - innerDiameter) / 2, innerDiameter, innerDiameter);
                    
                    g2d.dispose();
                }
            };
            
            // Add legend
            JPanel legendPanel = new JPanel();
            legendPanel.setOpaque(false);
            legendPanel.setLayout(new BoxLayout(legendPanel, BoxLayout.Y_AXIS));
            
            String[] labels;
            if (currentUser.getRole().equals("SALES_MANAGER") || currentUser.getRole().equals("SALES_STAFF")) {
                labels = new String[]{"Rice Products (35%)", "Dairy (25%)", "Cooking Oils (20%)", "Spices (10%)", "Beverages (10%)"};
            } else if (currentUser.getRole().equals("INVENTORY_MANAGER") || currentUser.getRole().equals("INVENTORY_STAFF")) {
                labels = new String[]{"In Stock (35%)", "Low Stock (25%)", "Critical (20%)", "Overstock (10%)", "On Order (10%)"};
            } else {
                labels = new String[]{"Item A (35%)", "Item B (25%)", "Item C (20%)", "Item D (10%)", "Item E (10%)"};
            }
            
            Color[] colors = {
                new Color(41, 128, 185),   // Blue
                new Color(46, 204, 113),   // Green
                new Color(155, 89, 182),   // Purple
                new Color(243, 156, 18),   // Orange
                new Color(231, 76, 60)     // Red
            };
            
            for (int i = 0; i < labels.length; i++) {
                final int index = i;
                JPanel itemPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
                itemPanel.setOpaque(false);
                
                JPanel colorBox = new JPanel() {
                    private final Color color = colors[index];
                    @Override
                    protected void paintComponent(Graphics g) {
                        super.paintComponent(g);
                        g.setColor(color);
                        g.fillRect(0, 0, getWidth(), getHeight());
                    }
                };
                colorBox.setPreferredSize(new Dimension(10, 10));
                
                JLabel label = new JLabel(labels[index]);
                label.setFont(new Font("Arial", Font.PLAIN, 10));
                
                itemPanel.add(colorBox);
                itemPanel.add(label);
                legendPanel.add(itemPanel);
            }
            
            JPanel pieChartWithLegend = new JPanel(new BorderLayout());
            pieChartWithLegend.setOpaque(false);
            pieChartWithLegend.add(pieChart, BorderLayout.CENTER);
            pieChartWithLegend.add(legendPanel, BorderLayout.EAST);
            
            piePanel.add(pieChartWithLegend, BorderLayout.CENTER);
            panel.add(piePanel, BorderLayout.CENTER);
        }
        
        return panel;
    }
    
    private void loadDashboardData() {
        // In a real application, we would load this data from the database
        // For now, we're using the static data already set in the stat cards
        
        // Get actual counts from data files if they exist
        try {
            List<Item> items = Item.getAllItems();
            List<Supplier> suppliers = Supplier.getAllSuppliers();
            List<PurchaseRequisition> pendingPRs = PurchaseRequisition.getPendingPRs();
            
            // Update appropriate stats based on user role
            if (currentUser.getRole().equals("ADMIN")) {
                if (statsPanel.getComponentCount() >= 1) {
                    updateStatCard(statsPanel.getComponent(0), "Total Items", String.valueOf(items.size()));
                }
                if (statsPanel.getComponentCount() >= 2) {
                    updateStatCard(statsPanel.getComponent(1), "Active Suppliers", String.valueOf(suppliers.size()));
                }
                if (statsPanel.getComponentCount() >= 3) {
                    updateStatCard(statsPanel.getComponent(2), "Pending POs", String.valueOf(pendingPRs.size()));
                }
            } else if (currentUser.getRole().equals("INVENTORY_MANAGER") || 
                      currentUser.getRole().equals("INVENTORY_STAFF")) {
                if (statsPanel.getComponentCount() >= 1) {
                    updateStatCard(statsPanel.getComponent(0), "Total Items", String.valueOf(items.size()));
                }
                // Count low stock items
                int lowStockCount = 0;
                for (Item item : items) {
                    if (item.getCurrentStock() <= item.getReorderLevel()) {
                        lowStockCount++;
                    }
                }
                if (statsPanel.getComponentCount() >= 2) {
                    updateStatCard(statsPanel.getComponent(1), "Low Stock", String.valueOf(lowStockCount));
                }
            } else if (currentUser.getRole().equals("PURCHASE_MANAGER") || 
                      currentUser.getRole().equals("PURCHASE_STAFF")) {
                if (statsPanel.getComponentCount() >= 1) {
                    updateStatCard(statsPanel.getComponent(0), "Pending POs", String.valueOf(pendingPRs.size()));
                }
                if (statsPanel.getComponentCount() >= 2) {
                    updateStatCard(statsPanel.getComponent(1), "Pending PRs", String.valueOf(pendingPRs.size()));
                }
                if (statsPanel.getComponentCount() >= 3) {
                    updateStatCard(statsPanel.getComponent(2), "Active Suppliers", String.valueOf(suppliers.size()));
                }
            }
        } catch (Exception e) {
            System.err.println("Error loading dashboard data: " + e.getMessage());
        }
    }
    
    private void updateStatCard(Component card, String title, String value) {
        if (card instanceof JPanel) {
            JPanel panel = (JPanel) card;
            Component contentPanel = panel.getComponent(0);
            
            if (contentPanel instanceof JPanel) {
                JPanel content = (JPanel) contentPanel;
                
                for (Component c : content.getComponents()) {
                    if (c instanceof JLabel) {
                        JLabel label = (JLabel) c;
                        if (label.getText().equals(title)) {
                            // Skip the title label
                            continue;
                        } else {
                            // Update the value label
                            label.setText(value);
                            break;
                        }
                    }
                }
            }
        }
    }
} 