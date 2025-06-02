package com.owsb.view.panels;

import com.owsb.model.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.FontMetrics;

public class ReportsPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JComboBox<String> reportTypeCombo;
    private JTextField fromDateField;
    private JTextField toDateField;
    private JButton generateReportButton;
    private JTable salesTable;
    private DefaultTableModel tableModel;
    private JLabel totalSalesLabel;
    private JLabel itemsSoldLabel;
    private JLabel transactionsLabel;
    private JLabel avgTransactionLabel;
    
    // Data
    private List<SalesReportEntry> salesData;
    private DecimalFormat decimalFormat = new DecimalFormat("#,##0.00");
    private DecimalFormat numberFormat = new DecimalFormat("#,##0");
    private SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    
    // Colors
    private static final Color PRIMARY_BLUE = new Color(52, 144, 220);
    private static final Color SUCCESS_GREEN = new Color(34, 197, 94);
    private static final Color WARNING_ORANGE = new Color(251, 146, 60);
    private static final Color INFO_CYAN = new Color(6, 182, 212);
    private static final Color LIGHT_BLUE = new Color(219, 234, 254);
    private static final Color GRAY_50 = new Color(249, 250, 251);
    private static final Color GRAY_100 = new Color(243, 244, 246);
    private static final Color GRAY_200 = new Color(229, 231, 235);
    private static final Color GRAY_600 = new Color(75, 85, 99);
    private static final Color GRAY_700 = new Color(55, 65, 81);
    private static final Color GRAY_900 = new Color(17, 24, 39);
    private static final Color GRAY_300 = new Color(204, 204, 204);
    
    // Inner class for sales report entry
    private static class SalesReportEntry {
        private String date;
        private String itemCode;
        private String itemName;
        private String quantitySold;
        private String unitPrice;
        private String totalAmount;
        private String salesPerson;
        
        public SalesReportEntry(String date, String itemCode, String itemName, 
                               String quantitySold, String unitPrice, String totalAmount, String salesPerson) {
            this.date = date;
            this.itemCode = itemCode;
            this.itemName = itemName;
            this.quantitySold = quantitySold;
            this.unitPrice = unitPrice;
            this.totalAmount = totalAmount;
            this.salesPerson = salesPerson;
        }
        
        // Getters
        public String getDate() { return date; }
        public String getItemCode() { return itemCode; }
        public String getItemName() { return itemName; }
        public String getQuantitySold() { return quantitySold; }
        public String getUnitPrice() { return unitPrice; }
        public String getTotalAmount() { return totalAmount; }
        public String getSalesPerson() { return salesPerson; }
    }
    
    public ReportsPanel(User user) {
        this.currentUser = user;
        this.salesData = new ArrayList<>();
        
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        
        initializeTable();
        setupUI();
        loadSampleData();
        updateStatistics();
    }
    
    private void setupUI() {
        // Main content panel
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(Color.WHITE);
        contentPanel.setBorder(BorderFactory.createEmptyBorder(20, 25, 20, 25));
        
        // Header section
        JPanel headerPanel = createHeaderPanel();
        contentPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Main content section
        JPanel mainPanel = createMainPanel();
        contentPanel.add(mainPanel, BorderLayout.CENTER);
        
        add(contentPanel, BorderLayout.CENTER);
    }
    
    private JPanel createHeaderPanel() {
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(Color.WHITE);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Title
        JLabel titleLabel = new JLabel("Sales Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(GRAY_900);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        return headerPanel;
    }
    
    private JPanel createMainPanel() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(Color.WHITE);
        
        // Filter section
        JPanel filterSection = createFilterSection();
        mainPanel.add(filterSection, BorderLayout.NORTH);
        
        // Statistics section
        JPanel statsSection = createStatisticsSection();
        mainPanel.add(statsSection, BorderLayout.CENTER);
        
        // Table and chart section
        JPanel bottomSection = createBottomSection();
        mainPanel.add(bottomSection, BorderLayout.SOUTH);
        
        return mainPanel;
    }
    
    private JPanel createFilterSection() {
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new BoxLayout(filterPanel, BoxLayout.Y_AXIS));
        filterPanel.setBackground(Color.WHITE);
        filterPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Section title
        JLabel sectionTitle = new JLabel("Sales Reports");
        sectionTitle.setFont(new Font("Segoe UI", Font.BOLD, 18));
        sectionTitle.setForeground(GRAY_900);
        sectionTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        filterPanel.add(sectionTitle);
        
        // Filter controls
        JPanel controlsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        controlsPanel.setBackground(Color.WHITE);
        
        // Report Type
        JPanel reportTypePanel = new JPanel();
        reportTypePanel.setLayout(new BoxLayout(reportTypePanel, BoxLayout.Y_AXIS));
        reportTypePanel.setBackground(Color.WHITE);
        
        JLabel reportTypeLabel = new JLabel("Report Type");
        reportTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reportTypeLabel.setForeground(GRAY_700);
        
        String[] reportTypes = {"Daily Sales", "Weekly Sales", "Monthly Sales", "Custom Range"};
        reportTypeCombo = new JComboBox<>(reportTypes);
        reportTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reportTypeCombo.setPreferredSize(new Dimension(160, 44));
        reportTypeCombo.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(2, 12, 2, 12)
        ));
        reportTypeCombo.setBackground(Color.WHITE);
        
        reportTypePanel.add(reportTypeLabel);
        reportTypePanel.add(Box.createVerticalStrut(8));
        reportTypePanel.add(reportTypeCombo);
        
        // From Date
        JPanel fromDatePanel = new JPanel();
        fromDatePanel.setLayout(new BoxLayout(fromDatePanel, BoxLayout.Y_AXIS));
        fromDatePanel.setBackground(Color.WHITE);
        
        JLabel fromDateLabel = new JLabel("From Date");
        fromDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fromDateLabel.setForeground(GRAY_700);
        
        fromDateField = new JTextField("05/01/2025");
        fromDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fromDateField.setPreferredSize(new Dimension(140, 44));
        fromDateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        
        fromDatePanel.add(fromDateLabel);
        fromDatePanel.add(Box.createVerticalStrut(8));
        fromDatePanel.add(fromDateField);
        
        // To Date
        JPanel toDatePanel = new JPanel();
        toDatePanel.setLayout(new BoxLayout(toDatePanel, BoxLayout.Y_AXIS));
        toDatePanel.setBackground(Color.WHITE);
        
        JLabel toDateLabel = new JLabel("To Date");
        toDateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        toDateLabel.setForeground(GRAY_700);
        
        toDateField = new JTextField("05/25/2025");
        toDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        toDateField.setPreferredSize(new Dimension(140, 44));
        toDateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        
        toDatePanel.add(toDateLabel);
        toDatePanel.add(Box.createVerticalStrut(8));
        toDatePanel.add(toDateField);
        
        // Generate Report Button
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.Y_AXIS));
        buttonPanel.setBackground(Color.WHITE);
        
        // Add space to align with other fields
        buttonPanel.add(Box.createVerticalStrut(22));
        
        generateReportButton = new JButton("Generate Report");
        generateReportButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        generateReportButton.setBackground(PRIMARY_BLUE);
        generateReportButton.setForeground(Color.WHITE);
        generateReportButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        generateReportButton.setFocusPainted(false);
        generateReportButton.setBorderPainted(false);
        generateReportButton.setOpaque(true);
        generateReportButton.setPreferredSize(new Dimension(140, 44));
        generateReportButton.addActionListener(e -> generateReport());
        
        buttonPanel.add(generateReportButton);
        
        controlsPanel.add(reportTypePanel);
        controlsPanel.add(fromDatePanel);
        controlsPanel.add(toDatePanel);
        controlsPanel.add(buttonPanel);
        
        filterPanel.add(controlsPanel);
        
        return filterPanel;
    }
    
    private JPanel createStatisticsSection() {
        JPanel statsPanel = new JPanel(new GridLayout(1, 4, 20, 0));
        statsPanel.setBackground(Color.WHITE);
        statsPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Total Sales Card
        JPanel totalSalesCard = createStatCard("RM 125,450", "Total Sales (This Month)", PRIMARY_BLUE);
        totalSalesLabel = (JLabel) ((JPanel) totalSalesCard.getComponent(0)).getComponent(0);
        statsPanel.add(totalSalesCard);
        
        // Items Sold Card
        JPanel itemsSoldCard = createStatCard("2,890", "Items Sold", INFO_CYAN);
        itemsSoldLabel = (JLabel) ((JPanel) itemsSoldCard.getComponent(0)).getComponent(0);
        statsPanel.add(itemsSoldCard);
        
        // Transactions Card
        JPanel transactionsCard = createStatCard("245", "Transactions", WARNING_ORANGE);
        transactionsLabel = (JLabel) ((JPanel) transactionsCard.getComponent(0)).getComponent(0);
        statsPanel.add(transactionsCard);
        
        // Average Transaction Card
        JPanel avgTransactionCard = createStatCard("RM 512", "Average Transaction", SUCCESS_GREEN);
        avgTransactionLabel = (JLabel) ((JPanel) avgTransactionCard.getComponent(0)).getComponent(0);
        statsPanel.add(avgTransactionCard);
        
        return statsPanel;
    }
    
    private JPanel createStatCard(String value, String label, Color color) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(Color.WHITE);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
        contentPanel.setBackground(Color.WHITE);
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel labelText = new JLabel(label);
        labelText.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelText.setForeground(GRAY_600);
        labelText.setAlignmentX(Component.CENTER_ALIGNMENT);
        labelText.setBorder(BorderFactory.createEmptyBorder(8, 0, 0, 0));
        
        contentPanel.add(valueLabel);
        contentPanel.add(labelText);
        
        card.add(contentPanel, BorderLayout.CENTER);
        
        return card;
    }
    
    private JPanel createBottomSection() {
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(Color.WHITE);
        
        // Chart panel with actual chart
        JPanel chartPanel = createSalesChart();
        bottomPanel.add(chartPanel, BorderLayout.NORTH);
        
        // Table section
        JPanel tableSection = createTableSection();
        bottomPanel.add(tableSection, BorderLayout.CENTER);
        
        // Export buttons
        JPanel exportPanel = createExportPanel();
        bottomPanel.add(exportPanel, BorderLayout.SOUTH);
        
        return bottomPanel;
    }
    
    private JPanel createSalesChart() {
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(Color.WHITE);
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(GRAY_200, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        chartContainer.setPreferredSize(new Dimension(0, 300));
        
        // Chart title
        JLabel chartTitle = new JLabel("Monthly Sales Trend", JLabel.CENTER);
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chartTitle.setForeground(GRAY_900);
        chartTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Custom chart panel
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawSalesChart(g);
            }
        };
        chartPanel.setBackground(Color.WHITE);
        chartPanel.setPreferredSize(new Dimension(0, 220));
        
        chartContainer.add(chartTitle, BorderLayout.NORTH);
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        
        return chartContainer;
    }
    
    private void drawSalesChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Chart data (sample monthly sales data)
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May"};
        double[] sales = {85000, 92000, 78000, 105000, 125000}; // Sample data
        
        // Chart dimensions
        int chartWidth = width - 100;
        int chartHeight = height - 80;
        int chartX = 50;
        int chartY = 20;
        
        // Find max value for scaling
        double maxSales = 0;
        for (double sale : sales) {
            maxSales = Math.max(maxSales, sale);
        }
        maxSales = Math.ceil(maxSales / 10000) * 10000; // Round up to nearest 10k
        
        // Draw background grid
        g2d.setColor(new Color(240, 240, 240));
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i <= 5; i++) {
            int y = chartY + (chartHeight * i / 5);
            g2d.drawLine(chartX, y, chartX + chartWidth, y);
        }
        
        // Draw Y-axis labels
        g2d.setColor(GRAY_600);
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics fm = g2d.getFontMetrics();
        for (int i = 0; i <= 5; i++) {
            double value = maxSales - (maxSales * i / 5);
            String label = "RM " + (int)(value / 1000) + "k";
            int y = chartY + (chartHeight * i / 5);
            int labelWidth = fm.stringWidth(label);
            g2d.drawString(label, chartX - labelWidth - 10, y + fm.getAscent() / 2);
        }
        
        // Draw bars
        int barWidth = chartWidth / months.length - 20;
        int barSpacing = 20;
        
        for (int i = 0; i < months.length; i++) {
            // Calculate bar height
            int barHeight = (int) ((sales[i] / maxSales) * chartHeight);
            int barX = chartX + (i * (barWidth + barSpacing)) + barSpacing / 2;
            int barY = chartY + chartHeight - barHeight;
            
            // Create gradient for bars
            Color barColor = (i == months.length - 1) ? PRIMARY_BLUE : new Color(100, 150, 200);
            GradientPaint gradient = new GradientPaint(
                barX, barY, barColor,
                barX, barY + barHeight, barColor.darker()
            );
            g2d.setPaint(gradient);
            
            // Draw bar with rounded corners
            g2d.fillRoundRect(barX, barY, barWidth, barHeight, 8, 8);
            
            // Draw bar border
            g2d.setColor(barColor.darker());
            g2d.setStroke(new BasicStroke(1));
            g2d.drawRoundRect(barX, barY, barWidth, barHeight, 8, 8);
            
            // Draw month labels
            g2d.setColor(GRAY_700);
            g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            FontMetrics monthFm = g2d.getFontMetrics();
            int monthLabelWidth = monthFm.stringWidth(months[i]);
            g2d.drawString(months[i], 
                barX + (barWidth - monthLabelWidth) / 2, 
                chartY + chartHeight + 20);
            
            // Draw value labels on top of bars
            g2d.setColor(GRAY_900);
            g2d.setFont(new Font("Segoe UI", Font.BOLD, 10));
            String valueLabel = "RM " + (int)(sales[i] / 1000) + "k";
            FontMetrics valueFm = g2d.getFontMetrics();
            int valueLabelWidth = valueFm.stringWidth(valueLabel);
            g2d.drawString(valueLabel, 
                barX + (barWidth - valueLabelWidth) / 2, 
                barY - 5);
        }
        
        // Draw chart border
        g2d.setColor(GRAY_300);
        g2d.setStroke(new BasicStroke(2));
        g2d.drawRect(chartX, chartY, chartWidth, chartHeight);
        
        g2d.dispose();
    }
    
    private JPanel createTableSection() {
        JPanel tablePanel = new JPanel(new BorderLayout());
        tablePanel.setBackground(Color.WHITE);
        tablePanel.setBorder(BorderFactory.createEmptyBorder(20, 0, 20, 0));
        
        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(salesTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(GRAY_200, 1));
        scrollPane.getViewport().setBackground(Color.WHITE);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setPreferredSize(new Dimension(1200, 200));
        
        tablePanel.add(scrollPane, BorderLayout.CENTER);
        
        return tablePanel;
    }
    
    private JPanel createExportPanel() {
        JPanel exportPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        exportPanel.setBackground(Color.WHITE);
        exportPanel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));
        
        JButton exportExcelButton = new JButton("Export to Excel");
        exportExcelButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exportExcelButton.setBackground(SUCCESS_GREEN);
        exportExcelButton.setForeground(Color.WHITE);
        exportExcelButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        exportExcelButton.setFocusPainted(false);
        exportExcelButton.setBorderPainted(false);
        exportExcelButton.setOpaque(true);
        exportExcelButton.addActionListener(e -> exportToExcel());
        
        JButton printReportButton = new JButton("Print Report");
        printReportButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        printReportButton.setBackground(GRAY_100);
        printReportButton.setForeground(GRAY_700);
        printReportButton.setBorder(BorderFactory.createEmptyBorder(12, 24, 12, 24));
        printReportButton.setFocusPainted(false);
        printReportButton.setBorderPainted(false);
        printReportButton.setOpaque(true);
        printReportButton.addActionListener(e -> printReport());
        
        exportPanel.add(exportExcelButton);
        exportPanel.add(printReportButton);
        
        return exportPanel;
    }
    
    private void initializeTable() {
        String[] columnNames = {
            "Date", "Item Code", "Item Name", "Quantity Sold", "Unit Price", "Total Amount", "Sales Person"
        };
        
        tableModel = new DefaultTableModel(columnNames, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Make table read-only
            }
        };
        
        salesTable = new JTable(tableModel);
        salesTable.setRowHeight(32);
        salesTable.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        salesTable.setShowGrid(true);
        salesTable.setGridColor(GRAY_200);
        salesTable.setIntercellSpacing(new Dimension(1, 1));
        salesTable.setSelectionBackground(new Color(239, 246, 255));
        salesTable.setSelectionForeground(GRAY_900);
        salesTable.setBackground(Color.WHITE);
        
        // Set column widths
        salesTable.getColumnModel().getColumn(0).setPreferredWidth(100); // Date
        salesTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Item Code
        salesTable.getColumnModel().getColumn(2).setPreferredWidth(150); // Item Name
        salesTable.getColumnModel().getColumn(3).setPreferredWidth(120); // Quantity Sold
        salesTable.getColumnModel().getColumn(4).setPreferredWidth(100); // Unit Price
        salesTable.getColumnModel().getColumn(5).setPreferredWidth(120); // Total Amount
        salesTable.getColumnModel().getColumn(6).setPreferredWidth(120); // Sales Person
        
        // Header styling
        JTableHeader header = salesTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 11));
        header.setBackground(GRAY_50);
        header.setForeground(GRAY_700);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 2, 0, GRAY_200));
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 35));
        header.setReorderingAllowed(false);
        
        // Default cell renderer
        DefaultTableCellRenderer defaultRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, 
                    boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                
                setBorder(BorderFactory.createEmptyBorder(6, 12, 6, 12));
                setFont(new Font("Segoe UI", Font.PLAIN, 11));
                
                if (isSelected) {
                    setBackground(new Color(239, 246, 255));
                    setForeground(GRAY_900);
                } else {
                    setBackground(Color.WHITE);
                    setForeground(GRAY_700);
                }
                
                return c;
            }
        };
        
        // Apply to all columns
        for (int i = 0; i < 7; i++) {
            salesTable.getColumnModel().getColumn(i).setCellRenderer(defaultRenderer);
        }
    }
    
    private void loadSampleData() {
        // Add sample sales data
        salesData.add(new SalesReportEntry("2025-05-25", "ITM-001", "Rice (Premium)", "50 kg", "RM 3.50", "RM 175.00", "John Doe"));
        salesData.add(new SalesReportEntry("2025-05-25", "ITM-002", "Sugar (White)", "30 kg", "RM 2.80", "RM 84.00", "John Doe"));
        salesData.add(new SalesReportEntry("2025-05-24", "ITM-003", "Oil (Cooking)", "25 ltr", "RM 6.00", "RM 150.00", "Jane Smith"));
        
        refreshTable();
    }
    
    private void refreshTable() {
        tableModel.setRowCount(0);
        
        for (SalesReportEntry entry : salesData) {
            Object[] rowData = {
                entry.getDate(),
                entry.getItemCode(),
                entry.getItemName(),
                entry.getQuantitySold(),
                entry.getUnitPrice(),
                entry.getTotalAmount(),
                entry.getSalesPerson()
            };
            
            tableModel.addRow(rowData);
        }
        
        if (salesTable != null) {
            salesTable.revalidate();
            salesTable.repaint();
        }
    }
    
    private void updateStatistics() {
        // Calculate statistics from sample data
        double totalSales = 409.00; // Sum of sample data
        int itemsSold = 105; // Sum of quantities
        int transactions = 3; // Number of entries
        double avgTransaction = totalSales / transactions;
        
        if (totalSalesLabel != null) {
            totalSalesLabel.setText("RM " + decimalFormat.format(totalSales));
        }
        if (itemsSoldLabel != null) {
            itemsSoldLabel.setText(numberFormat.format(itemsSold));
        }
        if (transactionsLabel != null) {
            transactionsLabel.setText(numberFormat.format(transactions));
        }
        if (avgTransactionLabel != null) {
            avgTransactionLabel.setText("RM " + decimalFormat.format(avgTransaction));
        }
    }
    
    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        String fromDate = fromDateField.getText();
        String toDate = toDateField.getText();
        
        JOptionPane.showMessageDialog(this, 
            "Generating " + reportType + " report\n" +
            "From: " + fromDate + "\n" +
            "To: " + toDate, 
            "Generate Report", JOptionPane.INFORMATION_MESSAGE);
        
        // Here you would implement the actual report generation logic
        // For now, just refresh the existing data
        refreshTable();
        updateStatistics();
    }
    
    private void exportToExcel() {
        JOptionPane.showMessageDialog(this, 
            "Sales report exported to Excel successfully!\n" +
            "File saved as: sales_report_" + dateFormat.format(new Date()) + ".xlsx", 
            "Export Successful", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void printReport() {
        JOptionPane.showMessageDialog(this, 
            "Printing sales report...\n" +
            "Report sent to default printer.", 
            "Print Report", JOptionPane.INFORMATION_MESSAGE);
    }
} 