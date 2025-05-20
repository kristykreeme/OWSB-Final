package model;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DailySales {
    private String salesId;
    private Date salesDate;
    private String itemCode;
    private int quantity;
    private double unitPrice;
    private double salesAmount;
    private String recordedBy; // User ID
    
    // File path for sales data
    private static final String FILE_PATH = "data/daily_sales.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    // Constructor
    public DailySales(String salesId, Date salesDate, String itemCode, int quantity, 
                     double unitPrice, double salesAmount, String recordedBy) {
        this.salesId = salesId;
        this.salesDate = salesDate;
        this.itemCode = itemCode;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.salesAmount = salesAmount;
        this.recordedBy = recordedBy;
    }
    
    // Alternative constructor for use in SalesDataEntryPanel
    public DailySales(String salesId, String itemCode, Date salesDate, int quantity, 
                     double salesAmount, String recordedBy) {
        this.salesId = salesId;
        this.itemCode = itemCode;
        this.salesDate = salesDate;
        this.quantity = quantity;
        
        // Get unit price from item
        Item item = Item.getItemByCode(itemCode);
        if (item != null) {
            this.unitPrice = item.getUnitPrice();
        } else {
            this.unitPrice = salesAmount / quantity; // Fallback calculation
        }
        
        this.salesAmount = salesAmount;
        this.recordedBy = recordedBy;
    }
    
    // Getters and Setters
    public String getSalesId() {
        return salesId;
    }
    
    public void setSalesId(String salesId) {
        this.salesId = salesId;
    }
    
    public Date getSalesDate() {
        return salesDate;
    }
    
    public void setSalesDate(Date salesDate) {
        this.salesDate = salesDate;
    }
    
    public String getItemCode() {
        return itemCode;
    }
    
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public double getSalesAmount() {
        return salesAmount;
    }
    
    public void setSalesAmount(double salesAmount) {
        this.salesAmount = salesAmount;
    }
    
    public String getRecordedBy() {
        return recordedBy;
    }
    
    public void setRecordedBy(String recordedBy) {
        this.recordedBy = recordedBy;
    }
    
    // Save sales record to file
    public boolean saveSales() {
        try {
            // Ensure data directory exists
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }
            
            // Create file if it doesn't exist
            File file = new File(FILE_PATH);
            if (!file.exists()) {
                file.createNewFile();
            }
            
            // Check if sales already exists
            List<DailySales> existingSales = getAllSales();
            for (DailySales sales : existingSales) {
                if (sales.getSalesId().equals(this.salesId)) {
                    return false; // Sales already exists
                }
            }
            
            // Append sales to file
            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(formatSalesData());
            }
            
            // Update item stock
            Item item = Item.getItemByCode(this.itemCode);
            if (item != null) {
                int newStock = item.getCurrentStock() - this.quantity;
                if (newStock < 0) {
                    return false; // Not enough stock
                }
                
                item.setCurrentStock(newStock);
                item.updateItem();
                
                // Create stock adjustment record
                StockAdjustment adjustment = new StockAdjustment(
                    StockAdjustment.generateAdjustmentId(),
                    this.itemCode,
                    new Date(),
                    "SUBTRACT",
                    this.quantity,
                    "Sales: " + this.salesId,
                    this.recordedBy
                );
                adjustment.saveAdjustment();
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Load all sales records from file
    public static List<DailySales> getAllSales() {
        List<DailySales> salesList = new ArrayList<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return salesList;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    try {
                        DailySales sales = new DailySales(
                            data[0],
                            DATE_FORMAT.parse(data[1]),
                            data[2],
                            Integer.parseInt(data[3]),
                            Double.parseDouble(data[4]),
                            Double.parseDouble(data[5]),
                            data[6]
                        );
                        salesList.add(sales);
                    } catch (ParseException | NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return salesList;
    }
    
    // Get sales by date range
    public static List<DailySales> getSalesByDateRange(Date startDate, Date endDate) {
        List<DailySales> allSales = getAllSales();
        List<DailySales> filteredSales = new ArrayList<>();
        
        for (DailySales sales : allSales) {
            if ((sales.getSalesDate().after(startDate) || sales.getSalesDate().equals(startDate)) &&
                (sales.getSalesDate().before(endDate) || sales.getSalesDate().equals(endDate))) {
                filteredSales.add(sales);
            }
        }
        
        return filteredSales;
    }
    
    // Get sales by specific date
    public static List<DailySales> getSalesByDate(Date date) {
        List<DailySales> allSales = getAllSales();
        List<DailySales> filteredSales = new ArrayList<>();
        
        // Convert date to string for comparison without time component
        String dateStr = DATE_FORMAT.format(date);
        
        for (DailySales sales : allSales) {
            String salesDateStr = DATE_FORMAT.format(sales.getSalesDate());
            if (salesDateStr.equals(dateStr)) {
                filteredSales.add(sales);
            }
        }
        
        return filteredSales;
    }
    
    // Get sales by item
    public static List<DailySales> getSalesByItem(String itemCode) {
        List<DailySales> allSales = getAllSales();
        List<DailySales> filteredSales = new ArrayList<>();
        
        for (DailySales sales : allSales) {
            if (sales.getItemCode().equals(itemCode)) {
                filteredSales.add(sales);
            }
        }
        
        return filteredSales;
    }
    
    // Format sales data for file storage
    private String formatSalesData() {
        return String.format("%s,%s,%s,%d,%.2f,%.2f,%s",
            salesId,
            DATE_FORMAT.format(salesDate),
            itemCode,
            quantity,
            unitPrice,
            salesAmount,
            recordedBy
        );
    }
    
    // Generate new sales ID
    public static String generateSalesId() {
        List<DailySales> allSales = getAllSales();
        
        if (allSales.isEmpty()) {
            return "S001";
        }
        
        int maxId = 0;
        for (DailySales sales : allSales) {
            try {
                int id = Integer.parseInt(sales.getSalesId().substring(1));
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Skip invalid sales IDs
            }
        }
        
        return String.format("S%03d", maxId + 1);
    }
    
    // Delete sales record
    public static boolean deleteSales(String salesId) {
        List<DailySales> salesList = getAllSales();
        boolean found = false;
        
        try {
            try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH))) {
                for (DailySales sales : salesList) {
                    if (!sales.getSalesId().equals(salesId)) {
                        String dateStr = DATE_FORMAT.format(sales.getSalesDate());
                        
                        out.println(sales.getSalesId() + "," + dateStr + "," + 
                                  sales.getItemCode() + "," + sales.getQuantity() + "," + 
                                  sales.getUnitPrice() + "," + sales.getSalesAmount() + "," + 
                                  sales.getRecordedBy());
                    } else {
                        found = true;
                        // Restore item stock
                        Item.updateStock(sales.getItemCode(), sales.getQuantity());
                    }
                }
            }
            return found;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update sales record
    public boolean updateSales(int oldQuantity) {
        List<DailySales> salesList = getAllSales();
        boolean found = false;
        
        try {
            try (PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH))) {
                for (DailySales sales : salesList) {
                    if (!sales.getSalesId().equals(this.salesId)) {
                        String dateStr = DATE_FORMAT.format(sales.getSalesDate());
                        
                        out.println(sales.getSalesId() + "," + dateStr + "," + 
                                  sales.getItemCode() + "," + sales.getQuantity() + "," + 
                                  sales.getUnitPrice() + "," + sales.getSalesAmount() + "," + 
                                  sales.getRecordedBy());
                    } else {
                        String dateStr = DATE_FORMAT.format(this.salesDate);
                        
                        out.println(this.salesId + "," + dateStr + "," + 
                                  this.itemCode + "," + this.quantity + "," + 
                                  this.unitPrice + "," + this.salesAmount + "," + 
                                  this.recordedBy);
                        found = true;
                        
                        // Update item stock (adjust for quantity difference)
                        int quantityDifference = oldQuantity - this.quantity;
                        if (quantityDifference != 0) {
                            Item.updateStock(this.itemCode, quantityDifference);
                        }
                    }
                }
            }
            return found;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Alias methods for quantity
    public int getSalesQuantity() {
        return quantity;
    }
    
    public void setSalesQuantity(int quantity) {
        this.quantity = quantity;
    }
} 