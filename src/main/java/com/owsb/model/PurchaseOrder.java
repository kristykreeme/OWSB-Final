package com.owsb.model;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PurchaseOrder {
    private String poId;
    private String prId; // Reference to Purchase Requisition
    private Date poDate;
    private Date deliveryDate;
    private String status; // PENDING, APPROVED, REJECTED, RECEIVED
    private String createdBy; // User ID of Purchase Manager who raised the PO
    private double totalAmount;
    private List<PurchaseOrderItem> items;
    
    // File paths
    private static final String PO_FILE = "data/purchase_orders.txt";
    private static final String PO_ITEMS_FILE = "data/po_items.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    // Constructor
    public PurchaseOrder(String poId, String prId, Date poDate, Date deliveryDate, 
                        String status, String createdBy, double totalAmount) {
        this.poId = poId;
        this.prId = prId;
        this.poDate = poDate;
        this.deliveryDate = deliveryDate;
        this.status = status;
        this.createdBy = createdBy;
        this.totalAmount = totalAmount;
        this.items = new ArrayList<>();
    }
    
    // Default constructor
    public PurchaseOrder() {
        this.items = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getPoId() {
        return poId;
    }
    
    public void setPoId(String poId) {
        this.poId = poId;
    }
    
    public String getPrId() {
        return prId;
    }
    
    public void setPrId(String prId) {
        this.prId = prId;
    }
    
    public Date getPoDate() {
        return poDate;
    }
    
    public void setPoDate(Date poDate) {
        this.poDate = poDate;
    }
    
    public Date getDeliveryDate() {
        return deliveryDate;
    }
    
    public void setDeliveryDate(Date deliveryDate) {
        this.deliveryDate = deliveryDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public double getTotalAmount() {
        return totalAmount;
    }
    
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public List<PurchaseOrderItem> getItems() {
        return items;
    }
    
    public void setItems(List<PurchaseOrderItem> items) {
        this.items = items;
    }
    
    public void addItem(PurchaseOrderItem item) {
        this.items.add(item);
    }
    
    // Calculate total amount based on items
    public void calculateTotal() {
        double total = 0;
        for (PurchaseOrderItem item : items) {
            total += item.getQuantity() * item.getUnitPrice();
        }
        this.totalAmount = total;
    }
    
    // Save PO header to file
    public boolean savePO() {
        try {
            // Create directory if it doesn't exist
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdir();
            }
            
            // Calculate total amount
            calculateTotal();
            
            // Format dates to string
            String poDateStr = DATE_FORMAT.format(poDate);
            String deliveryDateStr = DATE_FORMAT.format(deliveryDate);
            
            // Append PO to file
            try (PrintWriter out = new PrintWriter(new FileWriter(PO_FILE, true))) {
                out.println(poId + "," + prId + "," + poDateStr + "," + deliveryDateStr + "," + 
                          status + "," + createdBy + "," + totalAmount);
            }
            
            // Save PO items to file
            for (PurchaseOrderItem item : items) {
                item.setPoId(poId);
                item.savePOItem();
            }
            
            // Update PR status to "APPROVED"
            PurchaseRequisition pr = PurchaseRequisition.getPRById(prId);
            if (pr != null) {
                pr.updateStatus("APPROVED");
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Load all POs from file
    public static List<PurchaseOrder> getAllPOs() {
        List<PurchaseOrder> poList = new ArrayList<>();
        
        // Create directory if it doesn't exist
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }
        
        try {
            File file = new File(PO_FILE);
            if (!file.exists()) {
                // Create an empty file if it doesn't exist
                file.createNewFile();
                return poList;
            }
            
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 7) {
                        try {
                            PurchaseOrder po = new PurchaseOrder(
                                parts[0],
                                parts[1],
                                DATE_FORMAT.parse(parts[2]),
                                DATE_FORMAT.parse(parts[3]),
                                parts[4],
                                parts[5],
                                Double.parseDouble(parts[6])
                            );
                            
                            // Load PO items
                            po.setItems(PurchaseOrderItem.getItemsByPOId(parts[0]));
                            
                            poList.add(po);
                        } catch (ParseException | NumberFormatException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return poList;
    }
    
    // Get PO by ID
    public static PurchaseOrder getPOById(String poId) {
        List<PurchaseOrder> poList = getAllPOs();
        for (PurchaseOrder po : poList) {
            if (po.getPoId().equals(poId)) {
                return po;
            }
        }
        return null;
    }
    
    // Get pending POs (for finance managers)
    public static List<PurchaseOrder> getPendingPOs() {
        List<PurchaseOrder> allPOs = getAllPOs();
        List<PurchaseOrder> pendingPOs = new ArrayList<>();
        
        for (PurchaseOrder po : allPOs) {
            if (po.getStatus().equals("PENDING")) {
                pendingPOs.add(po);
            }
        }
        
        return pendingPOs;
    }
    
    // Generate new PO ID
    public static String generatePOId() {
        List<PurchaseOrder> poList = getAllPOs();
        if (poList.isEmpty()) {
            return "PO-2025-001";
        }
        
        // Get current year
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        
        // Find the highest PO ID for the current year
        int highestId = 0;
        for (PurchaseOrder po : poList) {
            String idPart = po.getPoId();
            if (idPart.contains(String.valueOf(year))) {
                try {
                    String numPart = idPart.substring(idPart.lastIndexOf('-') + 1);
                    int id = Integer.parseInt(numPart);
                    if (id > highestId) {
                        highestId = id;
                    }
                } catch (NumberFormatException e) {
                    // Ignore malformed IDs
                }
            }
        }
        
        // Generate new ID
        return String.format("PO-%d-%03d", year, highestId + 1);
    }
    
    // Update PO status
    public boolean updateStatus(String newStatus) {
        List<PurchaseOrder> poList = getAllPOs();
        boolean found = false;
        
        try {
            // Create directory if it doesn't exist
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdir();
            }
            
            // Create file if it doesn't exist
            File file = new File(PO_FILE);
            if (!file.exists()) {
                file.createNewFile();
            }
            
            try (PrintWriter out = new PrintWriter(new FileWriter(PO_FILE))) {
                for (PurchaseOrder po : poList) {
                    if (!po.getPoId().equals(this.poId)) {
                        String poDateStr = DATE_FORMAT.format(po.getPoDate());
                        String deliveryDateStr = DATE_FORMAT.format(po.getDeliveryDate());
                        
                        out.println(po.getPoId() + "," + po.getPrId() + "," + poDateStr + "," + 
                                  deliveryDateStr + "," + po.getStatus() + "," + 
                                  po.getCreatedBy() + "," + po.getTotalAmount());
                    } else {
                        String poDateStr = DATE_FORMAT.format(this.poDate);
                        String deliveryDateStr = DATE_FORMAT.format(this.deliveryDate);
                        
                        out.println(this.poId + "," + this.prId + "," + poDateStr + "," + 
                                  deliveryDateStr + "," + newStatus + "," + 
                                  this.createdBy + "," + this.totalAmount);
                        found = true;
                        this.status = newStatus;
                    }
                }
            }
            return found;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Receive PO (for inventory manager)
    public boolean receivePO() {
        if (updateStatus("RECEIVED")) {
            // Update inventory when order is received
            for (PurchaseOrderItem item : items) {
                Item.updateStock(item.getItemCode(), item.getQuantity());
            }
            return true;
        }
        return false;
    }
    
    // Item class for Purchase Order
    public static class PurchaseOrderItem {
        private String poId;
        private String itemCode;
        private int quantity;
        private double unitPrice;
        private String supplierId;
        
        // Constructor
        public PurchaseOrderItem(String poId, String itemCode, int quantity, 
                               double unitPrice, String supplierId) {
            this.poId = poId;
            this.itemCode = itemCode;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.supplierId = supplierId;
        }
        
        // Default constructor
        public PurchaseOrderItem() {
        }
        
        // Getters and Setters
        public String getPoId() {
            return poId;
        }
        
        public void setPoId(String poId) {
            this.poId = poId;
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
        
        public String getSupplierId() {
            return supplierId;
        }
        
        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }
        
        // Save PO item to file
        public boolean savePOItem() {
            try {
                // Create directory if it doesn't exist
                File directory = new File("data");
                if (!directory.exists()) {
                    directory.mkdir();
                }
                
                // Create file if it doesn't exist
                File file = new File(PO_ITEMS_FILE);
                if (!file.exists()) {
                    file.createNewFile();
                }
                
                // Append PO item to file
                try (PrintWriter out = new PrintWriter(new FileWriter(PO_ITEMS_FILE, true))) {
                    out.println(poId + "," + itemCode + "," + quantity + "," + 
                              unitPrice + "," + supplierId);
                }
                
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        
        // Get PO items by PO ID
        public static List<PurchaseOrderItem> getItemsByPOId(String poId) {
            List<PurchaseOrderItem> itemsList = new ArrayList<>();
            
            // Create directory if it doesn't exist
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdir();
            }
            
            try {
                File file = new File(PO_ITEMS_FILE);
                if (!file.exists()) {
                    // Create an empty file if it doesn't exist
                    file.createNewFile();
                    return itemsList;
                }
                
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 5 && parts[0].equals(poId)) {
                            PurchaseOrderItem item = new PurchaseOrderItem(
                                parts[0],
                                parts[1],
                                Integer.parseInt(parts[2]),
                                Double.parseDouble(parts[3]),
                                parts[4]
                            );
                            itemsList.add(item);
                        }
                    }
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
            
            return itemsList;
        }
    }
} 