package view.panels;

import model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.List;

public class ReportsPanel extends JPanel {
    private User currentUser;
    private JTabbedPane tabbedPane;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    private DecimalFormat currencyFormat = new DecimalFormat("#,##0.00");
    
    public ReportsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Title
        JLabel titleLabel = new JLabel("Reports");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        add(titleLabel, BorderLayout.NORTH);
        
        // Create tabbed pane for reports
        tabbedPane = new JTabbedPane();
        
        // Add report tabs
        tabbedPane.addTab("Dashboard", createDashboardPanel());
        tabbedPane.addTab("Sales Report", createSalesReportPanel());
        tabbedPane.addTab("Inventory Status", createInventoryPanel());
        tabbedPane.addTab("Purchase Orders", createPurchaseOrderPanel());
        
        add(tabbedPane, BorderLayout.CENTER);
    }
    
    private JPanel createDashboardPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Stats cards
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        
        // Total sales
        JPanel salesCard = createStatsCard("Total Sales", "RM " + getTotalSales(), new Color(0, 102, 204));
        statsPanel.add(salesCard);
        
        // Total items
        JPanel itemsCard = createStatsCard("Total Products", ""+getItemCount(), new Color(46, 184, 46));
        statsPanel.add(itemsCard);
        
        // Total suppliers
        JPanel suppliersCard = createStatsCard("Total Suppliers", ""+getSupplierCount(), new Color(255, 165, 0));
        statsPanel.add(suppliersCard);
        
        // Low stock items
        JPanel lowStockCard = createStatsCard("Low Stock Items", ""+getLowStockCount(), new Color(211, 47, 47));
        statsPanel.add(lowStockCard);
        
        panel.add(statsPanel, BorderLayout.NORTH);
        
        // Recent activity panel
        JPanel activityPanel = new JPanel(new BorderLayout());
        activityPanel.setBorder(BorderFactory.createTitledBorder("Recent Activity"));
        
        JTable activityTable = createRecentActivityTable();
        JScrollPane scrollPane = new JScrollPane(activityTable);
        activityPanel.add(scrollPane, BorderLayout.CENTER);
        
        panel.add(activityPanel, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createStatsCard(String title, String value, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224)),
            new EmptyBorder(15, 15, 15, 15)
        ));
        card.setBackground(Color.WHITE);
        
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(new Font("Arial", Font.PLAIN, 14));
        titleLabel.setForeground(Color.GRAY);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Arial", Font.BOLD, 24));
        valueLabel.setForeground(color);
        
        JPanel textPanel = new JPanel(new BorderLayout());
        textPanel.setOpaque(false);
        textPanel.add(titleLabel, BorderLayout.NORTH);
        textPanel.add(valueLabel, BorderLayout.CENTER);
        
        card.add(textPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createSalesReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Date Range: "));
        
        String[] dateRanges = {"Today", "Last 7 Days", "Last 30 Days", "This Month", "Custom..."};
        JComboBox<String> dateRangeCombo = new JComboBox<>(dateRanges);
        filterPanel.add(dateRangeCombo);
        
        JButton generateButton = new JButton("Generate Report");
        generateButton.setBackground(new Color(0, 102, 204));
        generateButton.setForeground(Color.WHITE);
        filterPanel.add(generateButton);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Sales table
        String[] columns = {"Date", "Item", "Quantity", "Amount (RM)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Add sample data
        List<DailySales> sales = DailySales.getAllSales();
        for (DailySales sale : sales) {
            Item item = Item.getItemByCode(sale.getItemCode());
            String itemName = item != null ? item.getItemName() : sale.getItemCode();
            
            Object[] row = {
                dateFormat.format(sale.getSalesDate()),
                itemName,
                sale.getQuantity(),
                String.format("%.2f", sale.getSalesAmount())
            };
            model.addRow(row);
        }
        
        JTable salesTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(salesTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createInventoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Category: "));
        
        String[] categories = {"All", "Grains", "Dairy", "Cooking", "Spices"};
        JComboBox<String> categoryCombo = new JComboBox<>(categories);
        filterPanel.add(categoryCombo);
        
        JCheckBox lowStockCheckbox = new JCheckBox("Show Low Stock Only");
        filterPanel.add(lowStockCheckbox);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // Inventory table
        String[] columns = {"Item Code", "Item Name", "Category", "Current Stock", "Reorder Level", "Status"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Add sample data
        List<Item> items = Item.getAllItems();
        for (Item item : items) {
            String status = item.getCurrentStock() <= item.getReorderLevel() ? "Low Stock" : "In Stock";
            
            Object[] row = {
                item.getItemCode(),
                item.getItemName(),
                item.getCategory(),
                item.getCurrentStock(),
                item.getReorderLevel(),
                status
            };
            model.addRow(row);
        }
        
        JTable inventoryTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(inventoryTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createPurchaseOrderPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(new EmptyBorder(20, 20, 20, 20));
        
        // Filter panel
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        filterPanel.add(new JLabel("Status: "));
        
        String[] statuses = {"All", "PENDING", "APPROVED", "DELIVERED"};
        JComboBox<String> statusCombo = new JComboBox<>(statuses);
        filterPanel.add(statusCombo);
        
        panel.add(filterPanel, BorderLayout.NORTH);
        
        // PO table
        String[] columns = {"PO Number", "Date", "Supplier", "Status", "Total (RM)"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        
        // Add sample data
        List<PurchaseOrder> orders = PurchaseOrder.getAllPOs();
        for (PurchaseOrder po : orders) {
            String supplierName = "Unknown";
            if (!po.getItems().isEmpty()) {
                Supplier supplier = Supplier.getSupplierById(po.getItems().get(0).getSupplierId());
                if (supplier != null) {
                    supplierName = supplier.getCompanyName();
                }
            }
            
            Object[] row = {
                po.getPoId(),
                dateFormat.format(po.getPoDate()),
                supplierName,
                po.getStatus(),
                String.format("%.2f", po.getTotalAmount())
            };
            model.addRow(row);
        }
        
        JTable poTable = new JTable(model);
        JScrollPane scrollPane = new JScrollPane(poTable);
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JTable createRecentActivityTable() {
        String[] columns = {"Date", "Activity", "User", "Details"};
        DefaultTableModel model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        // Get recent sales
        addSalesActivities(model);
        
        // Get recent purchase orders
        addPurchaseActivities(model);
        
        // Get recent stock adjustments
        addAdjustmentActivities(model);
        
        // Sort by date (most recent first)
        TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
        JTable table = new JTable(model);
        table.setRowSorter(sorter);
        List<RowSorter.SortKey> sortKeys = new ArrayList<>();
        sortKeys.add(new RowSorter.SortKey(0, SortOrder.DESCENDING));
        sorter.setSortKeys(sortKeys);
        
        table.setRowHeight(25);
        return table;
    }
    
    private void addSalesActivities(DefaultTableModel model) {
        List<DailySales> sales = DailySales.getAllSales();
        for (DailySales sale : sales) {
            Item item = Item.getItemByCode(sale.getItemCode());
            String itemName = item != null ? item.getItemName() : sale.getItemCode();
            
            User user = User.getUserById(sale.getRecordedBy());
            String userName = user != null ? user.getName() : sale.getRecordedBy();
            
            Object[] row = {
                dateFormat.format(sale.getSalesDate()),
                "Sale",
                userName,
                itemName + " - " + sale.getQuantity() + " units - RM" + String.format("%.2f", sale.getSalesAmount())
            };
            model.addRow(row);
        }
    }
    
    private void addPurchaseActivities(DefaultTableModel model) {
        List<PurchaseOrder> orders = PurchaseOrder.getAllPOs();
        for (PurchaseOrder po : orders) {
            User user = User.getUserById(po.getCreatedBy());
            String userName = user != null ? user.getName() : po.getCreatedBy();
            
            Object[] row = {
                dateFormat.format(po.getPoDate()),
                "Purchase Order",
                userName,
                po.getPoId() + " - " + po.getStatus() + " - RM" + String.format("%.2f", po.getTotalAmount())
            };
            model.addRow(row);
        }
    }
    
    private void addAdjustmentActivities(DefaultTableModel model) {
        List<StockAdjustment> adjustments = StockAdjustment.getAllAdjustments();
        for (StockAdjustment adj : adjustments) {
            Item item = Item.getItemByCode(adj.getItemCode());
            String itemName = item != null ? item.getItemName() : adj.getItemCode();
            
            User user = User.getUserById(adj.getAdjustedBy());
            String userName = user != null ? user.getName() : adj.getAdjustedBy();
            
            Object[] row = {
                dateFormat.format(adj.getAdjustmentDate()),
                "Stock Adjustment",
                userName,
                itemName + " - " + adj.getAdjustmentType() + " " + adj.getQuantity() + " units"
            };
            model.addRow(row);
        }
    }
    
    // Helper methods to get stats
    private String getTotalSales() {
        double total = 0;
        for (DailySales sale : DailySales.getAllSales()) {
            total += sale.getSalesAmount();
        }
        return String.format("%.2f", total);
    }
    
    private int getItemCount() {
        return Item.getAllItems().size();
    }
    
    private int getSupplierCount() {
        return Supplier.getAllSuppliers().size();
    }
    
    private int getLowStockCount() {
        int count = 0;
        for (Item item : Item.getAllItems()) {
            if (item.getCurrentStock() <= item.getReorderLevel()) {
                count++;
            }
        }
        return count;
    }
} 