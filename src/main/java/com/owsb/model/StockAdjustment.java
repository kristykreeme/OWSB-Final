package com.owsb.model;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class StockAdjustment {
    private String adjustmentId;
    private String itemCode;
    private Date adjustmentDate;
    private String adjustmentType; // ADD or SUBTRACT
    private int quantity;
    private String reason;
    private String adjustedBy; // User ID
    
    private static final String FILE_PATH = "data/stock_adjustments.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    public StockAdjustment(String adjustmentId, String itemCode, Date adjustmentDate,
                         String adjustmentType, int quantity, String reason, String adjustedBy) {
        this.adjustmentId = adjustmentId;
        this.itemCode = itemCode;
        this.adjustmentDate = adjustmentDate;
        this.adjustmentType = adjustmentType;
        this.quantity = quantity;
        this.reason = reason;
        this.adjustedBy = adjustedBy;
    }
    
    // Getters and setters
    public String getAdjustmentId() {
        return adjustmentId;
    }
    
    public void setAdjustmentId(String adjustmentId) {
        this.adjustmentId = adjustmentId;
    }
    
    public String getItemCode() {
        return itemCode;
    }
    
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    
    public Date getAdjustmentDate() {
        return adjustmentDate;
    }
    
    public void setAdjustmentDate(Date adjustmentDate) {
        this.adjustmentDate = adjustmentDate;
    }
    
    public String getAdjustmentType() {
        return adjustmentType;
    }
    
    public void setAdjustmentType(String adjustmentType) {
        this.adjustmentType = adjustmentType;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    
    public String getAdjustedBy() {
        return adjustedBy;
    }
    
    public void setAdjustedBy(String adjustedBy) {
        this.adjustedBy = adjustedBy;
    }
    
    // Generate a new adjustment ID
    public static String generateAdjustmentId() {
        List<StockAdjustment> adjustments = getAllAdjustments();
        
        if (adjustments.isEmpty()) {
            return "A001";
        }
        
        int maxId = 0;
        for (StockAdjustment adjustment : adjustments) {
            try {
                int id = Integer.parseInt(adjustment.getAdjustmentId().substring(1));
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Skip invalid adjustment IDs
            }
        }
        
        return String.format("A%03d", maxId + 1);
    }
    
    // Save adjustment to file
    public boolean saveAdjustment() {
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
            
            // Check if adjustment already exists
            List<StockAdjustment> existingAdjustments = getAllAdjustments();
            for (StockAdjustment adjustment : existingAdjustments) {
                if (adjustment.getAdjustmentId().equals(this.adjustmentId)) {
                    return false; // Adjustment already exists
                }
            }
            
            // Append adjustment to file
            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(formatAdjustmentData());
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all adjustments from file
    public static List<StockAdjustment> getAllAdjustments() {
        List<StockAdjustment> adjustments = new ArrayList<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return adjustments;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 7) {
                    try {
                        StockAdjustment adjustment = new StockAdjustment(
                            data[0],
                            data[1],
                            DATE_FORMAT.parse(data[2]),
                            data[3],
                            Integer.parseInt(data[4]),
                            data[5],
                            data[6]
                        );
                        adjustments.add(adjustment);
                    } catch (ParseException | NumberFormatException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return adjustments;
    }
    
    // Get adjustments by item code
    public static List<StockAdjustment> getAdjustmentsByItem(String itemCode) {
        List<StockAdjustment> allAdjustments = getAllAdjustments();
        List<StockAdjustment> itemAdjustments = new ArrayList<>();
        
        for (StockAdjustment adjustment : allAdjustments) {
            if (adjustment.getItemCode().equals(itemCode)) {
                itemAdjustments.add(adjustment);
            }
        }
        
        return itemAdjustments;
    }
    
    // Get adjustments by date range
    public static List<StockAdjustment> getAdjustmentsByDateRange(Date startDate, Date endDate) {
        List<StockAdjustment> allAdjustments = getAllAdjustments();
        List<StockAdjustment> filteredAdjustments = new ArrayList<>();
        
        for (StockAdjustment adjustment : allAdjustments) {
            if ((adjustment.getAdjustmentDate().after(startDate) || adjustment.getAdjustmentDate().equals(startDate)) &&
                (adjustment.getAdjustmentDate().before(endDate) || adjustment.getAdjustmentDate().equals(endDate))) {
                filteredAdjustments.add(adjustment);
            }
        }
        
        return filteredAdjustments;
    }
    
    // Format adjustment data for file storage
    private String formatAdjustmentData() {
        return String.format("%s,%s,%s,%s,%d,%s,%s",
            adjustmentId,
            itemCode,
            DATE_FORMAT.format(adjustmentDate),
            adjustmentType,
            quantity,
            reason,
            adjustedBy
        );
    }
} 