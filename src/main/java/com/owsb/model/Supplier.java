package com.owsb.model;

import java.io.*;
import java.util.*;

public class Supplier {
    private String supplierId;
    private String companyName;
    private String contactPerson;
    private String phoneNumber;
    private String email;
    private String address;
    private String city;
    private String state;
    private String zipCode;
    
    private static final String FILE_PATH = "data/suppliers.txt";
    
    public Supplier(String supplierId, String companyName, String contactPerson, 
                   String phoneNumber, String email, String address) {
        this.supplierId = supplierId;
        this.companyName = companyName;
        this.contactPerson = contactPerson;
        this.phoneNumber = phoneNumber;
        this.email = email;
        this.address = address;
        
        // Extract city, state, and zip code from address if possible
        try {
            if (address != null && address.contains(",")) {
                String[] parts = address.split(",");
                if (parts.length >= 3) {
                    // Assuming address format: "street, city, state zipcode"
                    this.city = parts[1].trim();
                    String[] stateZip = parts[2].trim().split(" ");
                    this.state = stateZip[0].trim();
                    if (stateZip.length > 1) {
                        this.zipCode = stateZip[1].trim();
                    } else {
                        this.zipCode = "";
                    }
                } else {
                    this.city = "";
                    this.state = "";
                    this.zipCode = "";
                }
            } else {
                this.city = "";
                this.state = "";
                this.zipCode = "";
            }
        } catch (Exception e) {
            this.city = "";
            this.state = "";
            this.zipCode = "";
        }
    }
    
    // Getters and setters
    public String getSupplierId() {
        return supplierId;
    }
    
    public void setSupplierId(String supplierId) {
        this.supplierId = supplierId;
    }
    
    public String getCompanyName() {
        return companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    
    public String getContactPerson() {
        return contactPerson;
    }
    
    public void setContactPerson(String contactPerson) {
        this.contactPerson = contactPerson;
    }
    
    public String getPhoneNumber() {
        return phoneNumber;
    }
    
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getCity() {
        return city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getZipCode() {
        return zipCode;
    }
    
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }
    
    // Alias methods for compatibility with existing code
    public String getSupplierName() {
        return companyName;
    }
    
    public void setSupplierName(String name) {
        this.companyName = name;
    }
    
    public String getPhone() {
        return phoneNumber;
    }
    
    public void setPhone(String phone) {
        this.phoneNumber = phone;
    }
    
    // For storing item codes associated with this supplier
    private List<String> itemsCodes = new ArrayList<>();
    
    public List<String> getItemsCodes() {
        // Get all items supplied by this supplier
        List<Item> items = Item.getItemsBySupplier(supplierId);
        List<String> codes = new ArrayList<>();
        for (Item item : items) {
            codes.add(item.getItemCode());
        }
        return codes;
    }
    
    public void setItemsCodes(List<String> itemsCodes) {
        this.itemsCodes = itemsCodes;
    }
    
    // Add an item code to the list of items this supplier provides
    public void addItemCode(String itemCode) {
        if (this.itemsCodes == null) {
            this.itemsCodes = new ArrayList<>();
        }
        
        // Only add if not already in the list
        if (!this.itemsCodes.contains(itemCode)) {
            this.itemsCodes.add(itemCode);
        }
    }
    
    // Generate a new supplier ID
    public static String generateSupplierId() {
        List<Supplier> suppliers = getAllSuppliers();
        
        if (suppliers.isEmpty()) {
            return "S001";
        }
        
        int maxId = 0;
        for (Supplier supplier : suppliers) {
            try {
                int id = Integer.parseInt(supplier.getSupplierId().substring(1));
                if (id > maxId) {
                    maxId = id;
                }
            } catch (NumberFormatException e) {
                // Skip invalid supplier IDs
            }
        }
        
        return String.format("S%03d", maxId + 1);
    }
    
    // Save supplier to file
    public boolean saveSupplier() {
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
            
            // Check if supplier already exists
            List<Supplier> existingSuppliers = getAllSuppliers();
            for (Supplier supplier : existingSuppliers) {
                if (supplier.getSupplierId().equals(this.supplierId)) {
                    return false; // Supplier already exists
                }
            }
            
            // Append supplier to file
            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(formatSupplierData());
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update existing supplier
    public boolean updateSupplier() {
        List<Supplier> suppliers = getAllSuppliers();
        boolean found = false;
        
        try {
            // Create temporary file
            File tempFile = new File("data/suppliers_temp.txt");
            
            // Write all suppliers to temp file, replacing the one to update
            try (FileWriter fw = new FileWriter(tempFile);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                for (Supplier supplier : suppliers) {
                    if (supplier.getSupplierId().equals(this.supplierId)) {
                        // Write updated supplier
                        out.println(formatSupplierData());
                        found = true;
                    } else {
                        // Write existing supplier
                        out.println(supplier.formatSupplierData());
                    }
                }
            }
            
            if (!found) {
                // Supplier not found
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
    
    // Delete supplier
    public static boolean deleteSupplier(String supplierId) {
        List<Supplier> suppliers = getAllSuppliers();
        boolean found = false;
        
        // Check if supplier has associated items
        List<Item> items = Item.getAllItems();
        for (Item item : items) {
            if (item.getSupplierId().equals(supplierId)) {
                return false; // Cannot delete supplier with associated items
            }
        }
        
        try {
            // Create temporary file
            File tempFile = new File("data/suppliers_temp.txt");
            
            // Write all suppliers except the one to delete
            try (FileWriter fw = new FileWriter(tempFile);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                for (Supplier supplier : suppliers) {
                    if (!supplier.getSupplierId().equals(supplierId)) {
                        out.println(supplier.formatSupplierData());
                    } else {
                        found = true;
                    }
                }
            }
            
            if (!found) {
                // Supplier not found
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
    
    // Get all suppliers from file
    public static List<Supplier> getAllSuppliers() {
        List<Supplier> suppliers = new ArrayList<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return suppliers;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 6) {
                    Supplier supplier = new Supplier(
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        data[4],
                        data[5]
                    );
                    suppliers.add(supplier);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return suppliers;
    }
    
    // Get supplier by ID
    public static Supplier getSupplierById(String supplierId) {
        List<Supplier> suppliers = getAllSuppliers();
        for (Supplier supplier : suppliers) {
            if (supplier.getSupplierId().equals(supplierId)) {
                return supplier;
            }
        }
        return null;
    }
    
    // Format supplier data for file storage
    private String formatSupplierData() {
        return String.format("%s,%s,%s,%s,%s,%s,%s,%s,%s", 
            supplierId, companyName, contactPerson, phoneNumber, email, address, city, state, zipCode);
    }
    
    @Override
    public String toString() {
        return companyName;
    }
} 