package com.owsb.model;

import java.io.*;
import java.util.*;
import java.text.SimpleDateFormat;

/**
 * Represents a notification in the system.
 * Used for alerting users about pending actions and status changes.
 */
public class Notification implements Serializable {
    private String notificationId;
    private String userId;
    private String title;
    private String message;
    private String type; // INFO, WARNING, ALERT, ACTION_REQUIRED
    private String relatedEntityId; // PR ID, PO ID, etc.
    private String relatedEntityType; // PR, PO, INVENTORY, etc.
    private Date createdDate;
    private boolean read;
    private String actionUrl; // For navigating to the related item

    private static final String DATA_FILE = "data/notifications.dat";
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public Notification(String userId, String title, String message, String type, 
                        String relatedEntityId, String relatedEntityType) {
        this.notificationId = generateNotificationId();
        this.userId = userId;
        this.title = title;
        this.message = message;
        this.type = type;
        this.relatedEntityId = relatedEntityId;
        this.relatedEntityType = relatedEntityType;
        this.createdDate = new Date();
        this.read = false;
        this.actionUrl = createActionUrl();
    }

    private String generateNotificationId() {
        return "N" + System.currentTimeMillis();
    }

    private String createActionUrl() {
        if (relatedEntityType == null) return "";
        
        switch (relatedEntityType) {
            case "PR":
                return "pr/" + relatedEntityId;
            case "PO":
                return "po/" + relatedEntityId;
            default:
                return "";
        }
    }

    // Save notification to data file
    public boolean saveNotification() {
        List<Notification> notifications = getAllNotifications();
        notifications.add(this);
        return saveAllNotifications(notifications);
    }

    // Mark notification as read
    public boolean markAsRead() {
        this.read = true;
        List<Notification> notifications = getAllNotifications();
        for (int i = 0; i < notifications.size(); i++) {
            if (notifications.get(i).getNotificationId().equals(this.notificationId)) {
                notifications.set(i, this);
                return saveAllNotifications(notifications);
            }
        }
        return false;
    }

    // Delete notification
    public boolean deleteNotification() {
        List<Notification> notifications = getAllNotifications();
        for (int i = 0; i < notifications.size(); i++) {
            if (notifications.get(i).getNotificationId().equals(this.notificationId)) {
                notifications.remove(i);
                return saveAllNotifications(notifications);
            }
        }
        return false;
    }

    // Get all notifications for a specific user
    public static List<Notification> getNotificationsForUser(String userId) {
        List<Notification> allNotifications = getAllNotifications();
        List<Notification> userNotifications = new ArrayList<>();
        
        for (Notification notification : allNotifications) {
            if (notification.getUserId().equals(userId)) {
                userNotifications.add(notification);
            }
        }
        
        // Sort by creation date (newest first)
        userNotifications.sort(Comparator.comparing(Notification::getCreatedDate).reversed());
        return userNotifications;
    }

    // Get all unread notifications for a specific user
    public static List<Notification> getUnreadNotificationsForUser(String userId) {
        List<Notification> allNotifications = getNotificationsForUser(userId);
        List<Notification> unreadNotifications = new ArrayList<>();
        
        for (Notification notification : allNotifications) {
            if (!notification.isRead()) {
                unreadNotifications.add(notification);
            }
        }
        
        return unreadNotifications;
    }

    // Create a new notification for PR approval
    public static Notification createPRApprovalNotification(String userId, PurchaseRequisition pr) {
        return new Notification(
            userId,
            "PR Approval Required",
            "Purchase Requisition " + pr.getPrId() + " is waiting for your approval.",
            "ACTION_REQUIRED",
            pr.getPrId(),
            "PR"
        );
    }

    // Create a new notification for PO approval
    public static Notification createPOApprovalNotification(String userId, PurchaseOrder po) {
        return new Notification(
            userId,
            "PO Approval Required",
            "Purchase Order " + po.getPoId() + " is waiting for your approval.",
            "ACTION_REQUIRED",
            po.getPoId(),
            "PO"
        );
    }

    // Create a new notification for status change
    public static Notification createStatusChangeNotification(String userId, String entityType, 
                                                             String entityId, String newStatus) {
        String title = entityType.equals("PR") ? "Purchase Requisition Status Changed" : "Purchase Order Status Changed";
        String message = entityType.equals("PR") ? 
                        "Purchase Requisition " + entityId + " has been " + newStatus.toLowerCase() + "." :
                        "Purchase Order " + entityId + " has been " + newStatus.toLowerCase() + ".";
        
        return new Notification(
            userId,
            title,
            message,
            "INFO",
            entityId,
            entityType
        );
    }

    // Get all notifications
    public static List<Notification> getAllNotifications() {
        List<Notification> notifications = new ArrayList<>();
        
        // Create data directory if it doesn't exist
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }
        
        // Check if data file exists
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            return notifications;
        }
        
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file))) {
            notifications = (List<Notification>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading notifications: " + e.getMessage());
        }
        
        return notifications;
    }

    // Save all notifications
    private static boolean saveAllNotifications(List<Notification> notifications) {
        // Create data directory if it doesn't exist
        File directory = new File("data");
        if (!directory.exists()) {
            directory.mkdir();
        }
        
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(DATA_FILE))) {
            oos.writeObject(notifications);
            return true;
        } catch (IOException e) {
            System.out.println("Error saving notifications: " + e.getMessage());
            return false;
        }
    }

    // Create a new notification for inventory delivery
    public static Notification createInventoryDeliveryNotification(String userId, PurchaseOrder po) {
        return new Notification(
            userId,
            "Inventory Delivery Expected",
            "Purchase Order " + po.getPoId() + " delivery is expected on " + 
            new SimpleDateFormat("dd/MM/yyyy").format(po.getDeliveryDate()) + ".",
            "INFO",
            po.getPoId(),
            "PO"
        );
    }

    // Create a batch of notifications for all users with a specific role
    public static void createNotificationsForRole(String role, String title, String message, 
                                                 String type, String relatedEntityId, String relatedEntityType) {
        List<User> users = User.getAllUsers();
        for (User user : users) {
            if (user.getRole().equals(role)) {
                Notification notification = new Notification(
                    user.getUserId(),
                    title,
                    message,
                    type,
                    relatedEntityId,
                    relatedEntityType
                );
                notification.saveNotification();
            }
        }
    }

    // Getters and setters
    public String getNotificationId() {
        return notificationId;
    }

    public String getUserId() {
        return userId;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getType() {
        return type;
    }

    public String getRelatedEntityId() {
        return relatedEntityId;
    }

    public String getRelatedEntityType() {
        return relatedEntityType;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public boolean isRead() {
        return read;
    }

    public String getActionUrl() {
        return actionUrl;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    // Format the creation date as a string
    public String getFormattedCreationDate() {
        return dateFormat.format(createdDate);
    }

    // Get a nice human-readable time representation (e.g., "2 hours ago", "Yesterday")
    public String getTimeAgo() {
        long now = System.currentTimeMillis();
        long diff = now - createdDate.getTime();
        
        long minutes = diff / (60 * 1000);
        long hours = diff / (60 * 60 * 1000);
        long days = diff / (24 * 60 * 60 * 1000);
        
        if (minutes < 1) {
            return "Just now";
        } else if (minutes < 60) {
            return minutes + " minute" + (minutes > 1 ? "s" : "") + " ago";
        } else if (hours < 24) {
            return hours + " hour" + (hours > 1 ? "s" : "") + " ago";
        } else if (days < 7) {
            return days + " day" + (days > 1 ? "s" : "") + " ago";
        } else {
            return new SimpleDateFormat("dd MMM yyyy").format(createdDate);
        }
    }
} 