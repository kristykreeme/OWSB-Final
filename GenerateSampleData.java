import model.*;
import java.util.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class GenerateSampleData {
    public static void main(String[] args) {
        System.out.println("Generating sample data for OWSB application...");
        
        // Create data directory
        java.io.File dataDir = new java.io.File("data");
        if (!dataDir.exists()) {
            dataDir.mkdir();
        }
        
        // Create users
        createUsers();
        
        // Create suppliers
        createSuppliers();
        
        // Create items
        createItems();
        
        // Create purchase requisitions
        createPurchaseRequisitions();
        
        // Create purchase orders
        createPurchaseOrders();
        
        // Create sales data
        createSalesData();
        
        // Create stock adjustments
        createStockAdjustments();
        
        System.out.println("Sample data generation complete!");
    }
    
    private static void createUsers() {
        // Admin user
        new User("U001", "Administrator", "admin", "admin", "ADMIN").saveUser();
        
        // Other users with specific roles
        new User("U002", "John Smith", "john", "pass123", "SALES_MANAGER").saveUser();
        new User("U003", "Maria Garcia", "maria", "pass123", "PURCHASE_MANAGER").saveUser();
        new User("U004", "David Lee", "david", "pass123", "INVENTORY_MANAGER").saveUser();
        new User("U005", "Sarah Johnson", "sarah", "pass123", "FINANCE_MANAGER").saveUser();
        new User("U006", "Ahmed Khan", "ahmed", "pass123", "SALES_STAFF").saveUser();
        new User("U007", "Lisa Wong", "lisa", "pass123", "PURCHASE_STAFF").saveUser();
        new User("U008", "Robert Chen", "robert", "pass123", "INVENTORY_STAFF").saveUser();
        
        System.out.println("Created users data");
    }
    
    private static void createSuppliers() {
        // Malaysian suppliers
        new Supplier("S001", "Global Foods Sdn Bhd", "Ali Rahman", "03-9876-5432", "ali@globalfoods.com", "123 Jalan Merdeka, Kuala Lumpur").saveSupplier();
        new Supplier("S002", "Premium Ingredients Co", "Tan Wei Ming", "03-8765-4321", "tanwm@premiumingredients.com", "456 Jalan Makmur, Selangor").saveSupplier();
        new Supplier("S003", "Freshway Produce", "Rajesh Kumar", "04-7654-3210", "rajesh@freshway.com", "789 Jalan Damai, Penang").saveSupplier();
        new Supplier("S004", "Eastern Spice Trading", "Nurul Huda", "05-6543-2109", "nurul@easternspice.com", "101 Jalan Harmoni, Kelantan").saveSupplier();
        new Supplier("S005", "Quality Dairy Products", "Jason Wong", "06-5432-1098", "jason@qualitydairy.com", "202 Jalan Sentosa, Johor").saveSupplier();
        
        // International suppliers
        new Supplier("S006", "Organic Farms Inc", "Michael Brown", "+1-555-123-4567", "michael@organicfarms.com", "789 Green Street, California, USA").saveSupplier();
        new Supplier("S007", "Asian Delights Co Ltd", "Somchai Patel", "+66-2-123-4567", "somchai@asiandelights.co.th", "45 Sukhumvit Road, Bangkok, Thailand").saveSupplier();
        new Supplier("S008", "European Gourmet GmbH", "Hans Schmidt", "+49-30-123456", "hans@europeangourmet.de", "Berliner Str. 123, Berlin, Germany").saveSupplier();
        new Supplier("S009", "Oceania Fresh Exports", "Emma Wilson", "+61-2-9876-5432", "emma@oceaniafresh.com.au", "42 Harbor Drive, Sydney, Australia").saveSupplier();
        new Supplier("S010", "Global Beverage Corp", "Carlos Rodriguez", "+34-91-123-4567", "carlos@globalbev.com", "Calle Mayor 28, Madrid, Spain").saveSupplier();
        
        System.out.println("Created suppliers data");
    }
    
    private static void createItems() {
        // Rice products
        new Item("I001", "Basmati Rice 5kg", "Premium quality basmati rice", "Grains", 25.90, 100, 20, "S001").saveItem();
        new Item("I002", "Jasmine Rice 10kg", "Fragrant Thai jasmine rice", "Grains", 45.50, 80, 15, "S001").saveItem();
        new Item("I003", "Brown Rice 2kg", "Healthy whole grain brown rice", "Grains", 12.80, 50, 10, "S001").saveItem();
        new Item("I004", "Glutinous Rice 1kg", "Thai sticky rice", "Grains", 8.90, 70, 15, "S007").saveItem();
        new Item("I005", "Wild Rice Mix 500g", "Specialty rice blend", "Grains", 15.90, 30, 10, "S006").saveItem();
        
        // Dairy products
        new Item("I006", "Fresh Milk 1L", "High-calcium fresh milk", "Dairy", 7.90, 120, 30, "S005").saveItem();
        new Item("I007", "Butter 250g", "Premium salted butter", "Dairy", 12.50, 60, 15, "S005").saveItem();
        new Item("I008", "Cheese Block 500g", "Cheddar cheese block", "Dairy", 28.90, 40, 10, "S005").saveItem();
        new Item("I009", "Greek Yogurt 500g", "Creamy Greek yogurt", "Dairy", 9.90, 45, 15, "S005").saveItem();
        new Item("I010", "Cream Cheese 300g", "Spreadable cream cheese", "Dairy", 14.50, 35, 10, "S008").saveItem();
        
        // Cooking oils & sauces
        new Item("I011", "Olive Oil 1L", "Extra virgin olive oil", "Cooking", 35.90, 45, 10, "S002").saveItem();
        new Item("I012", "Soy Sauce 750ml", "Premium soy sauce", "Cooking", 8.90, 75, 20, "S002").saveItem();
        new Item("I013", "Cooking Oil 5L", "Vegetable cooking oil", "Cooking", 26.90, 60, 15, "S002").saveItem();
        new Item("I014", "Sesame Oil 250ml", "Pure sesame oil", "Cooking", 12.90, 40, 10, "S007").saveItem();
        new Item("I015", "Fish Sauce 500ml", "Thai fish sauce", "Cooking", 7.50, 55, 15, "S007").saveItem();
        
        // Spices
        new Item("I016", "Ground Pepper 100g", "Fine ground black pepper", "Spices", 5.90, 80, 25, "S004").saveItem();
        new Item("I017", "Curry Powder 200g", "Mixed curry powder", "Spices", 9.80, 65, 20, "S004").saveItem();
        new Item("I018", "Cinnamon Sticks 50g", "Premium cinnamon sticks", "Spices", 7.50, 45, 15, "S004").saveItem();
        new Item("I019", "Cardamom Pods 30g", "Green cardamom pods", "Spices", 8.90, 30, 10, "S004").saveItem();
        new Item("I020", "Star Anise 50g", "Whole star anise", "Spices", 6.50, 40, 15, "S004").saveItem();
        
        // Dry goods
        new Item("I021", "Sugar 1kg", "Fine white sugar", "Dry Goods", 5.90, 100, 30, "S003").saveItem();
        new Item("I022", "Salt 500g", "Fine table salt", "Dry Goods", 2.90, 120, 40, "S003").saveItem();
        new Item("I023", "Flour 1kg", "All-purpose flour", "Dry Goods", 4.90, 90, 25, "S003").saveItem();
        new Item("I024", "Baking Powder 100g", "Double-acting baking powder", "Dry Goods", 3.50, 50, 15, "S003").saveItem();
        new Item("I025", "Corn Starch 500g", "Fine corn starch", "Dry Goods", 4.20, 60, 20, "S003").saveItem();
        
        // Pasta & noodles
        new Item("I026", "Spaghetti 500g", "Italian spaghetti", "Pasta", 6.90, 70, 20, "S008").saveItem();
        new Item("I027", "Egg Noodles 400g", "Fresh egg noodles", "Pasta", 5.50, 55, 15, "S002").saveItem();
        new Item("I028", "Rice Vermicelli 400g", "Thin rice noodles", "Pasta", 4.80, 65, 20, "S007").saveItem();
        new Item("I029", "Fusilli 500g", "Spiral pasta", "Pasta", 7.20, 45, 15, "S008").saveItem();
        new Item("I030", "Lasagna Sheets 300g", "Flat pasta sheets", "Pasta", 8.90, 35, 10, "S008").saveItem();
        
        // Beverages
        new Item("I031", "Green Tea 100g", "Japanese green tea leaves", "Beverages", 18.90, 40, 10, "S007").saveItem();
        new Item("I032", "Coffee Beans 500g", "Arabica coffee beans", "Beverages", 29.90, 50, 15, "S010").saveItem();
        new Item("I033", "Cocoa Powder 250g", "Pure cocoa powder", "Beverages", 12.50, 35, 10, "S010").saveItem();
        new Item("I034", "Fruit Juice 2L", "Mixed fruit juice", "Beverages", 9.90, 60, 20, "S009").saveItem();
        new Item("I035", "Coconut Water 1L", "Pure coconut water", "Beverages", 7.90, 50, 15, "S009").saveItem();
        
        // Low stock items for testing alerts
        new Item("I036", "Organic Quinoa 500g", "South American quinoa", "Grains", 18.90, 8, 10, "S006").saveItem();
        new Item("I037", "Truffle Oil 100ml", "Italian black truffle oil", "Cooking", 45.90, 5, 10, "S008").saveItem();
        new Item("I038", "Saffron 1g", "Premium Spanish saffron", "Spices", 25.90, 3, 5, "S010").saveItem();
        new Item("I039", "Maple Syrup 250ml", "Pure Canadian maple syrup", "Cooking", 22.90, 7, 10, "S006").saveItem();
        new Item("I040", "Vanilla Pods 5pc", "Madagascar vanilla pods", "Baking", 32.50, 2, 5, "S010").saveItem();
        
        System.out.println("Created items data");
    }
    
    private static void createPurchaseRequisitions() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            // Create dates
            Calendar cal = Calendar.getInstance();
            Date currentDate = cal.getTime();
            
            cal.add(Calendar.DAY_OF_MONTH, -15);
            Date twoWeeksAgo = cal.getTime();
            
            cal.add(Calendar.DAY_OF_MONTH, 7);
            Date oneWeekAhead = cal.getTime();
            
            cal.add(Calendar.DAY_OF_MONTH, 14);
            Date oneWeekAhead2 = cal.getTime();
            
            cal.add(Calendar.DAY_OF_MONTH, 7);
            Date twoWeeksAhead = cal.getTime();
            
            // First PR (Pending)
            PurchaseRequisition pr1 = new PurchaseRequisition("PR-2023-001", twoWeeksAgo, oneWeekAhead, "PENDING", "U004");
            pr1.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-001", "I001", 30, "S001"));
            pr1.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-001", "I002", 25, "S001"));
            pr1.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-001", "I003", 20, "S001"));
            pr1.savePR();
            
            // Second PR (Approved)
            PurchaseRequisition pr2 = new PurchaseRequisition("PR-2023-002", twoWeeksAgo, oneWeekAhead2, "APPROVED", "U004");
            pr2.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-002", "I006", 40, "S005"));
            pr2.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-002", "I007", 30, "S005"));
            pr2.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-002", "I008", 25, "S005"));
            pr2.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-002", "I009", 20, "S005"));
            pr2.savePR();
            
            // Third PR (Rejected)
            PurchaseRequisition pr3 = new PurchaseRequisition("PR-2023-003", twoWeeksAgo, twoWeeksAhead, "REJECTED", "U004");
            pr3.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-003", "I016", 35, "S004"));
            pr3.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-003", "I017", 30, "S004"));
            pr3.savePR();
            
            // Fourth PR (Approved)
            PurchaseRequisition pr4 = new PurchaseRequisition("PR-2023-004", currentDate, twoWeeksAhead, "APPROVED", "U003");
            pr4.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-004", "I011", 25, "S002"));
            pr4.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-004", "I012", 30, "S002"));
            pr4.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-004", "I013", 20, "S002"));
            pr4.savePR();
            
            // Fifth PR (Pending) - Low stock items
            PurchaseRequisition pr5 = new PurchaseRequisition("PR-2023-005", currentDate, oneWeekAhead, "PENDING", "U003");
            pr5.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-005", "I036", 20, "S006"));
            pr5.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-005", "I037", 15, "S008"));
            pr5.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-005", "I038", 10, "S010"));
            pr5.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-005", "I039", 15, "S006"));
            pr5.addItem(new PurchaseRequisition.PurchaseRequisitionItem("PR-2023-005", "I040", 10, "S010"));
            pr5.savePR();
            
            System.out.println("Created purchase requisitions data");
        } catch (Exception e) {
            System.err.println("Error creating purchase requisitions: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createPurchaseOrders() {
        try {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            
            // Create dates
            Calendar cal = Calendar.getInstance();
            Date currentDate = cal.getTime();
            
            cal.add(Calendar.DAY_OF_MONTH, -10);
            Date tenDaysAgo = cal.getTime();
            
            cal.add(Calendar.DAY_OF_MONTH, 20);
            Date tenDaysAhead = cal.getTime();
            
            cal.add(Calendar.DAY_OF_MONTH, 10);
            Date twentyDaysAhead = cal.getTime();
            
            // Create PO from the second PR (PR-2023-002)
            PurchaseOrder po1 = new PurchaseOrder("PO-2023-001", "PR-2023-002", tenDaysAgo, tenDaysAhead, "PENDING", "U003", 0);
            
            // Add items
            po1.addItem(new PurchaseOrder.PurchaseOrderItem("PO-2023-001", "I006", 40, 7.90, "S005"));
            po1.addItem(new PurchaseOrder.PurchaseOrderItem("PO-2023-001", "I007", 30, 12.50, "S005"));
            po1.addItem(new PurchaseOrder.PurchaseOrderItem("PO-2023-001", "I008", 25, 28.90, "S005"));
            po1.addItem(new PurchaseOrder.PurchaseOrderItem("PO-2023-001", "I009", 20, 9.90, "S005"));
            
            // Calculate and set total amount
            po1.calculateTotal();
            
            // Save PO
            po1.savePO();
            
            // Create PO from the fourth PR (PR-2023-004)
            PurchaseOrder po2 = new PurchaseOrder("PO-2023-002", "PR-2023-004", currentDate, twentyDaysAhead, "PENDING", "U003", 0);
            
            // Add items
            po2.addItem(new PurchaseOrder.PurchaseOrderItem("PO-2023-002", "I011", 25, 35.90, "S002"));
            po2.addItem(new PurchaseOrder.PurchaseOrderItem("PO-2023-002", "I012", 30, 8.90, "S002"));
            po2.addItem(new PurchaseOrder.PurchaseOrderItem("PO-2023-002", "I013", 20, 26.90, "S002"));
            
            // Calculate and set total amount
            po2.calculateTotal();
            
            // Save PO
            po2.savePO();
            
            System.out.println("Created purchase orders data");
        } catch (Exception e) {
            System.err.println("Error creating purchase orders: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createSalesData() {
        try {
            // Create daily sales records for the last two weeks
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -14); // Start 14 days ago
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int salesId = 1;
            
            // Sales for each day of the past two weeks
            for (int day = 0; day < 14; day++) {
                Date salesDate = cal.getTime();
                
                // Random sales for multiple items each day (more realistic pattern)
                String[] itemCodes = {
                    "I001", "I002", "I003", "I004", "I005", 
                    "I006", "I007", "I008", "I009", "I010",
                    "I011", "I012", "I013", "I014", "I015",
                    "I016", "I017", "I021", "I022", "I023",
                    "I026", "I027", "I031", "I032", "I034"
                };
                
                // Generate between 10-20 sales records per day (more on weekends)
                int salesCount = 10 + (int)(Math.random() * 10);
                if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || 
                    cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                    salesCount += 5; // More sales on weekends
                }
                
                for (int i = 0; i < salesCount; i++) {
                    // Select random item
                    String itemCode = itemCodes[(int)(Math.random() * itemCodes.length)];
                    Item item = Item.getItemByCode(itemCode);
                    
                    if (item != null) {
                        // Random sales quantity (1-10)
                        int quantity = 1 + (int)(Math.random() * 10);
                        double unitPrice = item.getUnitPrice();
                        double salesAmount = quantity * unitPrice;
                        
                        // Create sales record
                        String salesIdStr = String.format("S%04d", salesId++);
                        new DailySales(
                            salesIdStr, 
                            salesDate, 
                            itemCode, 
                            quantity, 
                            unitPrice, 
                            salesAmount, 
                            "U002" // Recorded by Sales Manager
                        ).saveSales();
                        
                        // Reduce item stock to reflect sales
                        int newStock = Math.max(0, item.getCurrentStock() - quantity);
                        item.setCurrentStock(newStock);
                        item.updateItem();
                    }
                }
                
                // Advance to next day
                cal.add(Calendar.DAY_OF_MONTH, 1);
            }
            
            System.out.println("Created sales data");
        } catch (Exception e) {
            System.err.println("Error creating sales data: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private static void createStockAdjustments() {
        try {
            // Create some stock adjustments for the past week
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DAY_OF_MONTH, -7); // Start 7 days ago
            
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            int adjustmentId = 1;
            
            // Different adjustment reasons
            String[] reasons = {
                "Damaged goods",
                "Inventory count correction",
                "Quality control check",
                "Expiry date reached",
                "Return to supplier",
                "New stock arrival",
                "Transfer between warehouses",
                "System adjustment"
            };
            
            // Item codes to adjust
            String[] itemsToAdjust = {
                "I001", "I006", "I011", "I016", "I021", "I026", "I031",
                "I003", "I008", "I013", "I018", "I023", "I028", "I033"
            };
            
            // Create about 20 adjustments
            for (int i = 0; i < 20; i++) {
                // Select random item
                String itemCode = itemsToAdjust[i % itemsToAdjust.length];
                Item item = Item.getItemByCode(itemCode);
                
                if (item != null) {
                    // Random adjustment date within the past week
                    cal.add(Calendar.DAY_OF_MONTH, (int)(Math.random() * 7) - 3);
                    Date adjustmentDate = cal.getTime();
                    
                    // Random adjustment type
                    String adjustmentType = Math.random() > 0.4 ? "ADD" : "SUBTRACT";
                    
                    // Random quantity (1-15)
                    int quantity = 1 + (int)(Math.random() * 15);
                    
                    // Random reason
                    String reason = reasons[(int)(Math.random() * reasons.length)];
                    
                    // Random user to make the adjustment
                    String userId = Math.random() > 0.7 ? "U004" : "U008"; // Inventory manager or staff
                    
                    // Create adjustment record
                    String adjustmentIdStr = String.format("ADJ%04d", adjustmentId++);
                    StockAdjustment adjustment = new StockAdjustment(
                        adjustmentIdStr,
                        itemCode,
                        adjustmentDate,
                        adjustmentType,
                        quantity,
                        reason,
                        userId
                    );
                    adjustment.saveAdjustment();
                    
                    // Update item stock to reflect adjustment
                    int currentStock = item.getCurrentStock();
                    int newStock;
                    
                    if (adjustmentType.equals("ADD")) {
                        newStock = currentStock + quantity;
                    } else {
                        newStock = Math.max(0, currentStock - quantity);
                    }
                    
                    item.setCurrentStock(newStock);
                    item.updateItem();
                }
            }
            
            System.out.println("Created stock adjustment data");
        } catch (Exception e) {
            System.err.println("Error creating stock adjustments: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 