package com.owsb.model;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Represents the history of changes to a document (PR or PO).
 * Used for audit trail and tracking changes.
 */
public class DocumentHistory implements Serializable {
    private String historyId;
    private String documentId; // PR ID or PO ID
    private String documentType; // PR or PO
    private String userId;
    private String userName;
    private String action; // CREATED, MODIFIED, APPROVED, REJECTED
    private String statusBefore;
    private String statusAfter;
    private String comments;
    private Date timestamp;
    private Map<String, Object> changedFields; // Field name -> new value

    private static final String DATA_FILE = "data/document_history.dat";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public DocumentHistory(String documentId, String documentType, String userId, 
                           String action, String statusBefore, String statusAfter, 
                           String comments, Map<String, Object> changedFields) {
        this.historyId = generateHistoryId();
        this.documentId = documentId;
        this.documentType = documentType;
        this.userId = userId;
        this.action = action;
        this.statusBefore = statusBefore;
        this.statusAfter = statusAfter;
        this.comments = comments;
        this.timestamp = new Date();
        this.changedFields = changedFields;
        
        // Get user name from User object
        User user = User.getUserById(userId);
        if (user != null) {
            this.userName = user.getName();
        } else {
            this.userName = "Unknown User";
        }
    }

    private String generateHistoryId() {
        return "H" + System.currentTimeMillis();
    }

    // Save history entry to data file
    public boolean saveHistory() {
        List<DocumentHistory> historyEntries = getAllHistoryEntries();
        historyEntries.add(this);
        return saveAllHistoryEntries(historyEntries);
    }

    // Get all history entries for a specific document
    public static List<DocumentHistory> getHistoryForDocument(String documentId) {
        List<DocumentHistory> allHistory = getAllHistoryEntries();
        List<DocumentHistory> documentHistory = new ArrayList<>();
        
        for (DocumentHistory entry : allHistory) {
            if (entry.getDocumentId().equals(documentId)) {
                documentHistory.add(entry);
            }
        }
        
        // Sort by timestamp (newest first)
        documentHistory.sort(Comparator.comparing(DocumentHistory::getTimestamp).reversed());
        return documentHistory;
    }

    // Create a history entry for PR creation
    public static DocumentHistory createPRCreationHistory(PurchaseRequisition pr, String userId) {
        Map<String, Object> changedFields = new HashMap<>();
        changedFields.put("prId", pr.getPrId());
        changedFields.put("items", pr.getItems());
        
        // Calculate total from items if needed
        double total = 0;
        for (PurchaseRequisition.PurchaseRequisitionItem item : pr.getItems()) {
            // Assuming each item contributes to the total
            // We would need price information here, but the PR doesn't have it yet
            total += item.getQuantity(); 
        }
        changedFields.put("totalItems", pr.getItems().size());
        
        return new DocumentHistory(
            pr.getPrId(),
            "PR",
            userId,
            "CREATED",
            null,
            "PENDING",
            "Purchase Requisition created",
            changedFields
        );
    }

    // Create a history entry for PR approval/rejection
    public static DocumentHistory createPRStatusChangeHistory(PurchaseRequisition pr, String userId, 
                                                             String oldStatus, String newStatus, String comments) {
        Map<String, Object> changedFields = new HashMap<>();
        changedFields.put("status", newStatus);
        
        return new DocumentHistory(
            pr.getPrId(),
            "PR",
            userId,
            newStatus.equals("APPROVED") ? "APPROVED" : "REJECTED",
            oldStatus,
            newStatus,
            comments,
            changedFields
        );
    }

    // Create a history entry for PO creation
    public static DocumentHistory createPOCreationHistory(PurchaseOrder po, String userId) {
        Map<String, Object> changedFields = new HashMap<>();
        changedFields.put("poId", po.getPoId());
        changedFields.put("items", po.getItems());
        changedFields.put("totalAmount", po.getTotalAmount());
        
        // Get supplier ID from the first item (assuming all items have the same supplier)
        String supplierId = "";
        if (!po.getItems().isEmpty()) {
            supplierId = po.getItems().get(0).getSupplierId();
        }
        changedFields.put("supplierId", supplierId);
        
        return new DocumentHistory(
            po.getPoId(),
            "PO",
            userId,
            "CREATED",
            null,
            "PENDING",
            "Purchase Order created",
            changedFields
        );
    }

    // Create a history entry for PO approval/rejection
    public static DocumentHistory createPOStatusChangeHistory(PurchaseOrder po, String userId, 
                                                            String oldStatus, String newStatus, String comments) {
        Map<String, Object> changedFields = new HashMap<>();
        changedFields.put("status", newStatus);
        
        return new DocumentHistory(
            po.getPoId(),
            "PO",
            userId,
            newStatus.equals("APPROVED") ? "APPROVED" : "REJECTED",
            oldStatus,
            newStatus,
            comments,
            changedFields
        );
    }

    // Create a history entry for PO modification
    public static DocumentHistory createPOModificationHistory(PurchaseOrder po, String userId, 
                                                             Map<String, Object> changedFields, String comments) {
        return new DocumentHistory(
            po.getPoId(),
            "PO",
            userId,
            "MODIFIED",
            po.getStatus(),
            po.getStatus(),
            comments,
            changedFields
        );
    }

    // Get all history entries
    public static List<DocumentHistory> getAllHistoryEntries() {
        List<DocumentHistory> historyEntries = new ArrayList<>();
        
        // Create data directory if it doesn't exist
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }
        
        // Check if data file exists
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return historyEntries;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            historyEntries = (List<DocumentHistory>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading document history: " + e.getMessage());
        }
        
        return historyEntries;
    }

    // Save all history entries
    private static boolean saveAllHistoryEntries(List<DocumentHistory> historyEntries) {
        // Create data directory if it doesn't exist
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(historyEntries);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving document history: " + e.getMessage());
            return false;
        }
    }

    // Get latest history entry for a document
    public static DocumentHistory getLatestHistoryForDocument(String documentId) {
        List<DocumentHistory> documentHistory = getHistoryForDocument(documentId);
        if (documentHistory.isEmpty()) {
            return null;
        }
        return documentHistory.get(0); // First entry is the newest due to sorting
    }

    // Get all history entries for a specific user
    public static List<DocumentHistory> getHistoryForUser(String userId) {
        List<DocumentHistory> allHistory = getAllHistoryEntries();
        List<DocumentHistory> userHistory = new ArrayList<>();
        
        for (DocumentHistory entry : allHistory) {
            if (entry.getUserId().equals(userId)) {
                userHistory.add(entry);
            }
        }
        
        // Sort by timestamp (newest first)
        userHistory.sort(Comparator.comparing(DocumentHistory::getTimestamp).reversed());
        return userHistory;
    }

    // Getters
    public String getHistoryId() {
        return historyId;
    }

    public String getDocumentId() {
        return documentId;
    }

    public String getDocumentType() {
        return documentType;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public String getAction() {
        return action;
    }

    public String getStatusBefore() {
        return statusBefore;
    }

    public String getStatusAfter() {
        return statusAfter;
    }

    public String getComments() {
        return comments;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public Map<String, Object> getChangedFields() {
        return changedFields;
    }

    // Format the timestamp as a string
    public String getFormattedTimestamp() {
        return dateFormat.format(timestamp);
    }

    // Get a human-readable representation of the action
    public String getActionDescription() {
        switch (action) {
            case "CREATED":
                return "Created";
            case "MODIFIED":
                return "Modified";
            case "APPROVED":
                return "Approved";
            case "REJECTED":
                return "Rejected";
            default:
                return action;
        }
    }

    // Get a human-readable description of the change
    public String getChangeDescription() {
        StringBuilder description = new StringBuilder();
        
        if (action.equals("CREATED")) {
            description.append("Created new ").append(documentType.equals("PR") ? "Purchase Requisition" : "Purchase Order");
        } else if (action.equals("MODIFIED")) {
            description.append("Modified ").append(documentType.equals("PR") ? "Purchase Requisition" : "Purchase Order");
            if (!changedFields.isEmpty()) {
                description.append(" (Changed: ");
                description.append(String.join(", ", changedFields.keySet()));
                description.append(")");
            }
        } else if (action.equals("APPROVED") || action.equals("REJECTED")) {
            description.append(action.equals("APPROVED") ? "Approved " : "Rejected ");
            description.append(documentType.equals("PR") ? "Purchase Requisition" : "Purchase Order");
            if (comments != null && !comments.isEmpty()) {
                description.append(" with comment: \"").append(comments).append("\"");
            }
        }
        
        return description.toString();
    }
} 