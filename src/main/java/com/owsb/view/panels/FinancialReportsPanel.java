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
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;

public class FinancialReportsPanel extends JPanel {
    private User currentUser;
    
    // UI Components
    private JTable transactionTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> reportTypeCombo;
    private JComboBox<String> periodCombo;
    private JTextField fromDateField;
    
    // Colors matching the design
    private Color primaryBlue = new Color(52, 144, 220);
    private Color successGreen = new Color(46, 184, 46);
    private Color warningOrange = new Color(255, 165, 0);
    private Color dangerRed = new Color(211, 47, 47);
    private Color backgroundColor = Color.WHITE;
    private Color borderColor = new Color(224, 224, 224);
    private Color lightGray = new Color(248, 249, 250);
    
    public FinancialReportsPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBackground(backgroundColor);
        
        initializeComponents();
        loadTransactionData();
    }
    
    private void initializeComponents() {
        // Main content panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBackground(backgroundColor);
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 30, 30, 30));
        
        // Header section
        JPanel headerPanel = createHeaderPanel();
        mainPanel.add(headerPanel, BorderLayout.NORTH);
        
        // Content section
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.setBackground(backgroundColor);
        
        // Controls and summary section
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(backgroundColor);
        
        // Controls section
        JPanel controlsPanel = createControlsPanel();
        topSection.add(controlsPanel, BorderLayout.NORTH);
        
        // Summary cards section
        JPanel summaryPanel = createSummaryPanel();
        topSection.add(summaryPanel, BorderLayout.CENTER);
        
        // Chart placeholder section
        JPanel chartPanel = createChartPanel();
        topSection.add(chartPanel, BorderLayout.SOUTH);
        
        contentPanel.add(topSection, BorderLayout.NORTH);
        
        // Table section
        JPanel tablePanel = createTablePanel();
        contentPanel.add(tablePanel, BorderLayout.CENTER);
        
        // Action buttons section
        JPanel actionPanel = createActionPanel();
        contentPanel.add(actionPanel, BorderLayout.SOUTH);
        
        mainPanel.add(contentPanel, BorderLayout.CENTER);
        
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
        
        JLabel titleLabel = new JLabel("Financial Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 28));
        titleLabel.setForeground(new Color(55, 65, 81));
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        
        panel.add(titleLabel, BorderLayout.WEST);
        
        return panel;
    }
    
    private JPanel createControlsPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Second header with title
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        headerPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 20, 0));
        
        JLabel titleLabel = new JLabel("Financial Reports");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 20));
        titleLabel.setForeground(new Color(55, 65, 81));
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        panel.add(headerPanel, BorderLayout.NORTH);
        
        // Controls row
        JPanel controlsRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 20, 0));
        controlsRow.setBackground(backgroundColor);
        
        // Report Type
        JPanel reportTypePanel = new JPanel();
        reportTypePanel.setLayout(new BoxLayout(reportTypePanel, BoxLayout.Y_AXIS));
        reportTypePanel.setBackground(backgroundColor);
        
        JLabel reportTypeLabel = new JLabel("Report Type");
        reportTypeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reportTypeLabel.setForeground(new Color(55, 65, 81));
        
        String[] reportTypes = {"Payment Summary", "Revenue Report", "Expense Report", "Profit & Loss", "Cash Flow"};
        reportTypeCombo = new JComboBox<>(reportTypes);
        reportTypeCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        reportTypeCombo.setPreferredSize(new Dimension(150, 40));
        reportTypeCombo.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        
        reportTypePanel.add(reportTypeLabel);
        reportTypePanel.add(Box.createVerticalStrut(5));
        reportTypePanel.add(reportTypeCombo);
        
        // Period
        JPanel periodPanel = new JPanel();
        periodPanel.setLayout(new BoxLayout(periodPanel, BoxLayout.Y_AXIS));
        periodPanel.setBackground(backgroundColor);
        
        JLabel periodLabel = new JLabel("Period");
        periodLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        periodLabel.setForeground(new Color(55, 65, 81));
        
        String[] periods = {"Current Month", "Last Month", "Last 3 Months", "Last 6 Months", "This Year", "Custom"};
        periodCombo = new JComboBox<>(periods);
        periodCombo.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        periodCombo.setPreferredSize(new Dimension(130, 40));
        periodCombo.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        
        periodPanel.add(periodLabel);
        periodPanel.add(Box.createVerticalStrut(5));
        periodPanel.add(periodCombo);
        
        // From Date
        JPanel datePanel = new JPanel();
        datePanel.setLayout(new BoxLayout(datePanel, BoxLayout.Y_AXIS));
        datePanel.setBackground(backgroundColor);
        
        JLabel dateLabel = new JLabel("From Date");
        dateLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        dateLabel.setForeground(new Color(55, 65, 81));
        
        fromDateField = new JTextField("05/01/2025");
        fromDateField.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        fromDateField.setPreferredSize(new Dimension(120, 40));
        fromDateField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(10, 15, 10, 15)
        ));
        
        datePanel.add(dateLabel);
        datePanel.add(Box.createVerticalStrut(5));
        datePanel.add(fromDateField);
        
        // Generate Report Button
        JButton generateButton = new JButton("Generate Report");
        generateButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        generateButton.setBackground(primaryBlue);
        generateButton.setForeground(Color.WHITE);
        generateButton.setPreferredSize(new Dimension(140, 40));
        generateButton.setBorderPainted(false);
        generateButton.setFocusPainted(false);
        generateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        generateButton.addActionListener(e -> generateReport());
        
        controlsRow.add(reportTypePanel);
        controlsRow.add(periodPanel);
        controlsRow.add(datePanel);
        controlsRow.add(Box.createHorizontalStrut(20));
        controlsRow.add(generateButton);
        
        panel.add(controlsRow, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createSummaryPanel() {
        JPanel panel = new JPanel(new GridLayout(1, 4, 30, 0));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(0, 0, 30, 0));
        
        // Summary cards
        panel.add(createSummaryCard("RM 45,680", "Total Payments (This Month)", primaryBlue));
        panel.add(createSummaryCard("RM 15,420", "Pending Payments", warningOrange));
        panel.add(createSummaryCard("RM 5,890", "Overdue Amounts", dangerRed));
        panel.add(createSummaryCard("32", "Transactions", new Color(107, 114, 128)));
        
        return panel;
    }
    
    private JPanel createSummaryCard(String value, String label, Color color) {
        JPanel card = new JPanel();
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBackground(backgroundColor);
        card.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(25, 20, 25, 20)
        ));
        
        JLabel valueLabel = new JLabel(value);
        valueLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        valueLabel.setForeground(color);
        valueLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        JLabel descLabel = new JLabel(label);
        descLabel.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        descLabel.setForeground(new Color(107, 114, 128));
        descLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        card.add(valueLabel);
        card.add(Box.createVerticalStrut(8));
        card.add(descLabel);
        
        return card;
    }
    
    private JPanel createChartPanel() {
        JPanel chartContainer = new JPanel(new BorderLayout());
        chartContainer.setBackground(backgroundColor);
        chartContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(borderColor, 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        chartContainer.setPreferredSize(new Dimension(0, 280));
        
        // Chart title
        JLabel chartTitle = new JLabel("Monthly Payment Trends", JLabel.CENTER);
        chartTitle.setFont(new Font("Segoe UI", Font.BOLD, 16));
        chartTitle.setForeground(new Color(55, 65, 81));
        chartTitle.setBorder(BorderFactory.createEmptyBorder(0, 0, 15, 0));
        
        // Custom chart panel
        JPanel chartPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                drawFinancialChart(g);
            }
        };
        chartPanel.setBackground(backgroundColor);
        chartPanel.setPreferredSize(new Dimension(0, 200));
        
        chartContainer.add(chartTitle, BorderLayout.NORTH);
        chartContainer.add(chartPanel, BorderLayout.CENTER);
        
        return chartContainer;
    }
    
    private void drawFinancialChart(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        
        int width = getWidth();
        int height = getHeight();
        
        // Chart data (sample monthly payment data)
        String[] months = {"Jan", "Feb", "Mar", "Apr", "May"};
        double[] payments = {35000, 42000, 38000, 45000, 46000}; // Total payments
        double[] pending = {8000, 12000, 15000, 18000, 15000}; // Pending payments
        double[] overdue = {3000, 5000, 4000, 6000, 6000}; // Overdue amounts
        
        // Chart dimensions
        int chartWidth = width - 100;
        int chartHeight = height - 80;
        int chartX = 50;
        int chartY = 20;
        
        // Find max value for scaling
        double maxValue = 0;
        for (int i = 0; i < months.length; i++) {
            double total = payments[i] + pending[i] + overdue[i];
            maxValue = Math.max(maxValue, total);
        }
        maxValue = Math.ceil(maxValue / 10000) * 10000; // Round up to nearest 10k
        
        // Draw background grid
        g2d.setColor(new Color(240, 240, 240));
        g2d.setStroke(new BasicStroke(1));
        for (int i = 0; i <= 5; i++) {
            int y = chartY + (chartHeight * i / 5);
            g2d.drawLine(chartX, y, chartX + chartWidth, y);
        }
        
        // Draw Y-axis labels
        g2d.setColor(new Color(107, 114, 128));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics fm = g2d.getFontMetrics();
        for (int i = 0; i <= 5; i++) {
            double value = maxValue - (maxValue * i / 5);
            String label = "RM " + (int)(value / 1000) + "k";
            int y = chartY + (chartHeight * i / 5);
            int labelWidth = fm.stringWidth(label);
            g2d.drawString(label, chartX - labelWidth - 10, y + fm.getAscent() / 2);
        }
        
        // Calculate points for lines
        int[] xPoints = new int[months.length];
        int[] paymentPoints = new int[months.length];
        int[] pendingPoints = new int[months.length];
        int[] overduePoints = new int[months.length];
        
        for (int i = 0; i < months.length; i++) {
            xPoints[i] = chartX + (i * chartWidth / (months.length - 1));
            paymentPoints[i] = chartY + chartHeight - (int)((payments[i] / maxValue) * chartHeight);
            pendingPoints[i] = chartY + chartHeight - (int)((pending[i] / maxValue) * chartHeight);
            overduePoints[i] = chartY + chartHeight - (int)((overdue[i] / maxValue) * chartHeight);
        }
        
        // Draw lines
        g2d.setStroke(new BasicStroke(3, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
        
        // Total Payments line (blue)
        g2d.setColor(primaryBlue);
        for (int i = 0; i < months.length - 1; i++) {
            g2d.drawLine(xPoints[i], paymentPoints[i], xPoints[i + 1], paymentPoints[i + 1]);
        }
        
        // Pending Payments line (orange)
        g2d.setColor(warningOrange);
        for (int i = 0; i < months.length - 1; i++) {
            g2d.drawLine(xPoints[i], pendingPoints[i], xPoints[i + 1], pendingPoints[i + 1]);
        }
        
        // Overdue Amounts line (red)
        g2d.setColor(dangerRed);
        for (int i = 0; i < months.length - 1; i++) {
            g2d.drawLine(xPoints[i], overduePoints[i], xPoints[i + 1], overduePoints[i + 1]);
        }
        
        // Draw data points
        for (int i = 0; i < months.length; i++) {
            // Total Payments points
            g2d.setColor(primaryBlue);
            g2d.fillOval(xPoints[i] - 4, paymentPoints[i] - 4, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(xPoints[i] - 2, paymentPoints[i] - 2, 4, 4);
            
            // Pending Payments points
            g2d.setColor(warningOrange);
            g2d.fillOval(xPoints[i] - 4, pendingPoints[i] - 4, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(xPoints[i] - 2, pendingPoints[i] - 2, 4, 4);
            
            // Overdue Amounts points
            g2d.setColor(dangerRed);
            g2d.fillOval(xPoints[i] - 4, overduePoints[i] - 4, 8, 8);
            g2d.setColor(Color.WHITE);
            g2d.fillOval(xPoints[i] - 2, overduePoints[i] - 2, 4, 4);
        }
        
        // Draw month labels
        g2d.setColor(new Color(75, 85, 99));
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        FontMetrics monthFm = g2d.getFontMetrics();
        for (int i = 0; i < months.length; i++) {
            int monthLabelWidth = monthFm.stringWidth(months[i]);
            g2d.drawString(months[i], 
                xPoints[i] - monthLabelWidth / 2, 
                chartY + chartHeight + 20);
        }
        
        // Draw legend
        int legendX = chartX + chartWidth - 200;
        int legendY = chartY + 20;
        
        g2d.setFont(new Font("Segoe UI", Font.PLAIN, 11));
        FontMetrics legendFm = g2d.getFontMetrics();
        
        // Total Payments legend
        g2d.setColor(primaryBlue);
        g2d.fillOval(legendX, legendY, 10, 10);
        g2d.setColor(new Color(55, 65, 81));
        g2d.drawString("Total Payments", legendX + 15, legendY + 8);
        
        // Pending Payments legend
        legendY += 20;
        g2d.setColor(warningOrange);
        g2d.fillOval(legendX, legendY, 10, 10);
        g2d.setColor(new Color(55, 65, 81));
        g2d.drawString("Pending Payments", legendX + 15, legendY + 8);
        
        // Overdue Amounts legend
        legendY += 20;
        g2d.setColor(dangerRed);
        g2d.fillOval(legendX, legendY, 10, 10);
        g2d.setColor(new Color(55, 65, 81));
        g2d.drawString("Overdue Amounts", legendX + 15, legendY + 8);
        
        // Draw chart border
        g2d.setColor(borderColor);
        g2d.setStroke(new BasicStroke(1));
        g2d.drawRect(chartX, chartY, chartWidth, chartHeight);
        
        g2d.dispose();
    }
    
    private JPanel createTablePanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(30, 0, 0, 0));
        
        // Create table
        String[] columns = {
            "Date", "Supplier", "Invoice No", "Amount Paid", 
            "Payment Method", "Reference", "Status"
        };
        
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        transactionTable = new JTable(tableModel);
        transactionTable.setRowHeight(50);
        transactionTable.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        transactionTable.setGridColor(borderColor);
        transactionTable.setShowGrid(true);
        transactionTable.setSelectionBackground(new Color(239, 246, 255));
        transactionTable.setSelectionForeground(new Color(55, 65, 81));
        
        // Header styling
        JTableHeader header = transactionTable.getTableHeader();
        header.setFont(new Font("Segoe UI", Font.BOLD, 14));
        header.setBackground(lightGray);
        header.setForeground(new Color(55, 65, 81));
        header.setPreferredSize(new Dimension(0, 50));
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, borderColor));
        
        // Set column widths
        TableColumnModel columnModel = transactionTable.getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(100); // Date
        columnModel.getColumn(1).setPreferredWidth(180); // Supplier
        columnModel.getColumn(2).setPreferredWidth(120); // Invoice No
        columnModel.getColumn(3).setPreferredWidth(120); // Amount Paid
        columnModel.getColumn(4).setPreferredWidth(130); // Payment Method
        columnModel.getColumn(5).setPreferredWidth(130); // Reference
        columnModel.getColumn(6).setPreferredWidth(100); // Status
        
        // Custom renderer for status column
        transactionTable.getColumnModel().getColumn(6).setCellRenderer(new StatusCellRenderer());
        
        JScrollPane scrollPane = new JScrollPane(transactionTable);
        scrollPane.setBorder(BorderFactory.createLineBorder(borderColor, 1));
        scrollPane.setBackground(backgroundColor);
        
        panel.add(scrollPane, BorderLayout.CENTER);
        
        return panel;
    }
    
    private JPanel createActionPanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 0));
        panel.setBackground(backgroundColor);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 0, 0, 0));
        
        JButton exportButton = new JButton("Export Report");
        exportButton.setFont(new Font("Segoe UI", Font.BOLD, 14));
        exportButton.setBackground(successGreen);
        exportButton.setForeground(Color.WHITE);
        exportButton.setPreferredSize(new Dimension(130, 40));
        exportButton.setBorderPainted(false);
        exportButton.setFocusPainted(false);
        exportButton.addActionListener(e -> exportReport());
        
        JButton printButton = new JButton("Print Report");
        printButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        printButton.setBackground(new Color(107, 114, 128));
        printButton.setForeground(Color.WHITE);
        printButton.setPreferredSize(new Dimension(120, 40));
        printButton.setBorderPainted(false);
        printButton.setFocusPainted(false);
        printButton.addActionListener(e -> printReport());
        
        JButton emailButton = new JButton("Email Report");
        emailButton.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        emailButton.setBackground(primaryBlue);
        emailButton.setForeground(Color.WHITE);
        emailButton.setPreferredSize(new Dimension(120, 40));
        emailButton.setBorderPainted(false);
        emailButton.setFocusPainted(false);
        emailButton.addActionListener(e -> emailReport());
        
        panel.add(exportButton);
        panel.add(printButton);
        panel.add(emailButton);
        
        return panel;
    }
    
    private void loadTransactionData() {
        // Sample data matching the screenshot
        Object[][] data = {
            {"2025-05-24", "ABC Trading Sdn Bhd", "INV-2025-003", "RM 890.75", "Bank Transfer", "TXN-240524001", "Completed"},
            {"2025-05-23", "XYZ Supplies Ltd", "INV-2025-004", "RM 1,245.50", "Cheque", "CHQ-240523001", "Completed"},
            {"2025-05-22", "Global Foods Enterprise", "INV-2025-005", "RM 567.25", "Bank Transfer", "TXN-240522001", "Completed"}
        };
        
        for (Object[] row : data) {
            tableModel.addRow(row);
        }
    }
    
    private void generateReport() {
        String reportType = (String) reportTypeCombo.getSelectedItem();
        String period = (String) periodCombo.getSelectedItem();
        String fromDate = fromDateField.getText();
        
        JOptionPane.showMessageDialog(this,
            "Generating " + reportType + " report...\n\n" +
            "Period: " + period + "\n" +
            "From Date: " + fromDate + "\n\n" +
            "Report will be generated and displayed.",
            "Generate Report",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void exportReport() {
        JOptionPane.showMessageDialog(this,
            "Exporting financial report to Excel...\n\nReport will be saved to Downloads folder.",
            "Export Report",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void printReport() {
        JOptionPane.showMessageDialog(this,
            "Printing financial report...\n\nReport will be sent to default printer.",
            "Print Report",
            JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void emailReport() {
        JOptionPane.showMessageDialog(this,
            "Emailing financial report...\n\nReport will be sent to specified recipients.",
            "Email Report",
            JOptionPane.INFORMATION_MESSAGE);
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
            if ("Completed".equals(status)) {
                label.setBackground(new Color(220, 252, 231)); // Light green
                label.setForeground(new Color(22, 101, 52)); // Dark green
            } else {
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
} 