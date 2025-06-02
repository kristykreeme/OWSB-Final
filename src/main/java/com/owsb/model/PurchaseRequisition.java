package com.owsb.model;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class PurchaseRequisition {
    private String prId;
    private Date prDate;
    private Date requiredDate;
    private String status; // PENDING, APPROVED, REJECTED
    private String requestedBy; // User ID of Sales Manager who raised the PR
    private List<PurchaseRequisitionItem> items;
    
    // File paths
    private static final String PR_FILE = "data/purchase_requisitions.txt";
    private static final String PR_ITEMS_FILE = "data/pr_items.txt";
    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
    // Constructor
    public PurchaseRequisition(String prId, Date prDate, Date requiredDate, 
                             String status, String requestedBy) {
        this.prId = prId;
        this.prDate = prDate;
        this.requiredDate = requiredDate;
        this.status = status;
        this.requestedBy = requestedBy;
        this.items = new ArrayList<>();
    }
    
    // Default constructor
    public PurchaseRequisition() {
        this.items = new ArrayList<>();
    }
    
    // Getters and Setters
    public String getPrId() {
        return prId;
    }
    
    public void setPrId(String prId) {
        this.prId = prId;
    }
    
    public Date getPrDate() {
        return prDate;
    }
    
    public void setPrDate(Date prDate) {
        this.prDate = prDate;
    }
    
    public Date getRequiredDate() {
        return requiredDate;
    }
    
    public void setRequiredDate(Date requiredDate) {
        this.requiredDate = requiredDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getRequestedBy() {
        return requestedBy;
    }
    
    public void setRequestedBy(String requestedBy) {
        this.requestedBy = requestedBy;
    }
    
    public List<PurchaseRequisitionItem> getItems() {
        return items;
    }
    
    public void setItems(List<PurchaseRequisitionItem> items) {
        this.items = items;
    }
    
    public void addItem(PurchaseRequisitionItem item) {
        this.items.add(item);
    }
    
    // Save PR header to file
    public boolean savePR() {
        try {
            // Create directory if it doesn't exist
            File directory = new File("data");
            if (!directory.exists()) {
                directory.mkdir();
            }
            
            // Format dates to string
            String prDateStr = DATE_FORMAT.format(prDate);
            String requiredDateStr = DATE_FORMAT.format(requiredDate);
            
            // Append PR to file
            try (PrintWriter out = new PrintWriter(new FileWriter(PR_FILE, true))) {
                out.println(prId + "," + prDateStr + "," + requiredDateStr + "," + 
                          status + "," + requestedBy);
            }
            
            // Save PR items to file
            for (PurchaseRequisitionItem item : items) {
                item.setPrId(prId);
                item.savePRItem();
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Load all PRs from file
    public static List<PurchaseRequisition> getAllPRs() {
        List<PurchaseRequisition> prList = new ArrayList<>();
        try {
            File file = new File(PR_FILE);
            if (!file.exists()) {
                return prList;
            }
            
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] parts = line.split(",");
                    if (parts.length == 5) {
                        try {
                            PurchaseRequisition pr = new PurchaseRequisition(
                                parts[0], 
                                DATE_FORMAT.parse(parts[1]), 
                                DATE_FORMAT.parse(parts[2]), 
                                parts[3],
                                parts[4]
                            );
                            
                            // Load PR items
                            pr.setItems(PurchaseRequisitionItem.getItemsByPRId(parts[0]));
                            
                            prList.add(pr);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prList;
    }
    
    // Get PR by ID
    public static PurchaseRequisition getPRById(String prId) {
        List<PurchaseRequisition> prList = getAllPRs();
        for (PurchaseRequisition pr : prList) {
            if (pr.getPrId().equals(prId)) {
                return pr;
            }
        }
        return null;
    }
    
    // Get pending PRs (for purchase managers)
    public static List<PurchaseRequisition> getPendingPRs() {
        List<PurchaseRequisition> allPRs = getAllPRs();
        List<PurchaseRequisition> pendingPRs = new ArrayList<>();
        
        for (PurchaseRequisition pr : allPRs) {
            if (pr.getStatus().equals("PENDING")) {
                pendingPRs.add(pr);
            }
        }
        
        return pendingPRs;
    }
    
    // Generate new PR ID
    public static String generatePRId() {
        List<PurchaseRequisition> prList = getAllPRs();
        if (prList.isEmpty()) {
            return "PR-2025-001";
        }
        
        // Get current year
        Calendar cal = Calendar.getInstance();
        int year = cal.get(Calendar.YEAR);
        
        // Find the highest PR ID for the current year
        int highestId = 0;
        for (PurchaseRequisition pr : prList) {
            String idPart = pr.getPrId();
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
        return String.format("PR-%d-%03d", year, highestId + 1);
    }
    
    // Update PR status
    public boolean updateStatus(String newStatus) {
        List<PurchaseRequisition> prList = getAllPRs();
        boolean found = false;
        
        try {
            try (PrintWriter out = new PrintWriter(new FileWriter(PR_FILE))) {
                for (PurchaseRequisition pr : prList) {
                    if (!pr.getPrId().equals(this.prId)) {
                        String prDateStr = DATE_FORMAT.format(pr.getPrDate());
                        String requiredDateStr = DATE_FORMAT.format(pr.getRequiredDate());
                        
                        out.println(pr.getPrId() + "," + prDateStr + "," + requiredDateStr + "," + 
                                  pr.getStatus() + "," + pr.getRequestedBy());
                    } else {
                        String prDateStr = DATE_FORMAT.format(this.prDate);
                        String requiredDateStr = DATE_FORMAT.format(this.requiredDate);
                        
                        out.println(this.prId + "," + prDateStr + "," + requiredDateStr + "," + 
                                  newStatus + "," + this.requestedBy);
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
    
    // Item class for Purchase Requisition
    public static class PurchaseRequisitionItem {
        private String prId;
        private String itemCode;
        private int quantity;
        private String supplierId;
        
        // Constructor
        public PurchaseRequisitionItem(String prId, String itemCode, int quantity, String supplierId) {
            this.prId = prId;
            this.itemCode = itemCode;
            this.quantity = quantity;
            this.supplierId = supplierId;
        }
        
        // Default constructor
        public PurchaseRequisitionItem() {
        }
        
        // Getters and Setters
        public String getPrId() {
            return prId;
        }
        
        public void setPrId(String prId) {
            this.prId = prId;
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
        
        public String getSupplierId() {
            return supplierId;
        }
        
        public void setSupplierId(String supplierId) {
            this.supplierId = supplierId;
        }
        
        // Save PR item to file
        public boolean savePRItem() {
            try {
                // Create directory if it doesn't exist
                File directory = new File("data");
                if (!directory.exists()) {
                    directory.mkdir();
                }
                
                // Append PR item to file
                try (PrintWriter out = new PrintWriter(new FileWriter(PR_ITEMS_FILE, true))) {
                    out.println(prId + "," + itemCode + "," + quantity + "," + supplierId);
                }
                
                return true;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        }
        
        // Get PR items by PR ID
        public static List<PurchaseRequisitionItem> getItemsByPRId(String prId) {
            List<PurchaseRequisitionItem> prItems = new ArrayList<>();
            try {
                File file = new File(PR_ITEMS_FILE);
                if (!file.exists()) {
                    return prItems;
                }
                
                try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                    String line;
                    while ((line = br.readLine()) != null) {
                        String[] parts = line.split(",");
                        if (parts.length == 4 && parts[0].equals(prId)) {
                            PurchaseRequisitionItem item = new PurchaseRequisitionItem(
                                parts[0],
                                parts[1],
                                Integer.parseInt(parts[2]),
                                parts[3]
                            );
                            prItems.add(item);
                        }
                    }
                }
            } catch (IOException | NumberFormatException e) {
                e.printStackTrace();
            }
            return prItems;
        }
    }
} 