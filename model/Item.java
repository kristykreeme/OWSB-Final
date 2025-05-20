package model;

import java.io.*;
import java.util.*;

public class Item {
    private String itemCode;
    private String itemName;
    private String description;
    private String category;
    private double unitPrice;
    private int currentStock;
    private int reorderLevel;
    private String supplierId;
    
    private static final String FILE_PATH = "data/items.txt";
    
    public Item(String itemCode, String itemName, String description, String category, 
                double unitPrice, int currentStock, int reorderLevel, String supplierId) {
        this.itemCode = itemCode;
        this.itemName = itemName;
        this.description = description;
        this.category = category;
        this.unitPrice = unitPrice;
        this.currentStock = currentStock;
        this.reorderLevel = reorderLevel;
        this.supplierId = supplierId;
    }
    
    // Getters and setters
    public String getItemCode() {
        return itemCode;
    }
    
    public void setItemCode(String itemCode) {
        this.itemCode = itemCode;
    }
    
    public String getItemName() {
        return itemName;
    }
    
    public void setItemName(String itemName) {
        this.itemName = itemName;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getCategory() {
        return category;
    }
    
    public void setCategory(String category) {
        this.category = category;
    }
    
    public double getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public int getCurrentStock() {
        return currentStock;
    }
    
    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }
    
    // Alias method for compatibility with existing code
    public int getStockQuantity() {
        return currentStock;
    }
    
    public int getReorderLevel() {
        return reorderLevel;
    }
    
    public void setReorderLevel(int reorderLevel) {
        this.reorderLevel = reorderLevel;
    }
    
    public String getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    
    // Generate a new item code
    public static String generateItemCode() {
        List<Item> items = getAllItems();
        
        if (items.isEmpty()) {
            return "I001";
        }
        
        int maxId = 0;
        for (Item item : items) {
            try {
                int itemId = Integer.parseInt(item.getItemCode().substring(1));
                if (itemId > maxId) {
                    maxId = itemId;
                }
            } catch (NumberFormatException e) {
                // Skip invalid item codes
            }
        }
        
        return String.format("I%03d", maxId + 1);
    }
    
    // Save item to file
    public boolean saveItem() {
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
            
            // Check if item already exists
            List<Item> existingItems = getAllItems();
            for (Item item : existingItems) {
                if (item.getItemCode().equals(this.itemCode)) {
                    return false; // Item already exists
                }
            }
            
            // Append item to file
            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(formatItemData());
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update existing item
    public boolean updateItem() {
        List<Item> items = getAllItems();
        boolean found = false;
        
        try {
            // Create temporary file
            File tempFile = new File("data/items_temp.txt");
            
            // Write all items to temp file, replacing the one to update
            try (FileWriter fw = new FileWriter(tempFile);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                for (Item item : items) {
                    if (item.getItemCode().equals(this.itemCode)) {
                        // Write updated item
                        out.println(formatItemData());
                        found = true;
                    } else {
                        // Write existing item
                        out.println(item.formatItemData());
                    }
                }
            }
            
            if (!found) {
                // Item not found
                tempFile.delete();
                return false;
            }
            
            // Replace original file with temp file
            File originalFile = new File(FILE_PATH);
            if (originalFile.exists()) {
                originalFile.delete();
            }
            
            return tempFile.renameTo(originalFile);
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Delete item
    public static boolean deleteItem(String itemCode) {
        List<Item> items = getAllItems();
        boolean found = false;
        
        try {
            // Create temporary file
            File tempFile = new File("data/items_temp.txt");
            
            // Write all items except the one to delete
            try (FileWriter fw = new FileWriter(tempFile);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                for (Item item : items) {
                    if (!item.getItemCode().equals(itemCode)) {
                        out.println(item.formatItemData());
                    } else {
                        found = true;
                    }
                }
            }
            
            if (!found) {
                // Item not found
                tempFile.delete();
                return false;
            }
            
            // Replace original file with temp file
            File originalFile = new File(FILE_PATH);
            if (originalFile.exists()) {
                originalFile.delete();
            }
            
            return tempFile.renameTo(originalFile);
            
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Get all items from file
    public static List<Item> getAllItems() {
        List<Item> items = new ArrayList<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return items;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 8) {
                    Item item = new Item(
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        Double.parseDouble(data[4]),
                        Integer.parseInt(data[5]),
                        Integer.parseInt(data[6]),
                        data[7]
                    );
                    items.add(item);
                }
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
        
        return items;
    }
    
    // Get item by code
    public static Item getItemByCode(String itemCode) {
        List<Item> items = getAllItems();
        for (Item item : items) {
            if (item.getItemCode().equals(itemCode)) {
                return item;
            }
        }
        return null;
    }
    
    // Get items below reorder level
    public static List<Item> getItemsBelowReorderLevel() {
        List<Item> items = getAllItems();
        List<Item> belowReorderLevel = new ArrayList<>();
        
        for (Item item : items) {
            if (item.getCurrentStock() <= item.getReorderLevel()) {
                belowReorderLevel.add(item);
            }
        }
        
        return belowReorderLevel;
    }
    
    // Get items by supplier
    public static List<Item> getItemsBySupplier(String supplierId) {
        List<Item> items = getAllItems();
        List<Item> supplierItems = new ArrayList<>();
        
        for (Item item : items) {
            if (item.getSupplierId().equals(supplierId)) {
                supplierItems.add(item);
            }
        }
        
        return supplierItems;
    }
    
    // Format item data for file storage
    private String formatItemData() {
        return String.format("%s,%s,%s,%s,%.2f,%d,%d,%s", 
            itemCode, itemName, description, category, 
            unitPrice, currentStock, reorderLevel, supplierId);
    }
    
    // Static method to update item stock
    public static boolean updateStock(String itemCode, int quantity) {
        Item item = getItemByCode(itemCode);
        if (item == null) {
            return false;
        }
        
        // Add quantity to current stock (positive to add, negative to subtract)
        int newStock = item.getCurrentStock() + quantity;
        if (newStock < 0) {
            // Don't allow negative stock
            return false;
        }
        
        item.setCurrentStock(newStock);
        return item.updateItem();
    }
    
    @Override
    public String toString() {
        return itemName;
    }
} 