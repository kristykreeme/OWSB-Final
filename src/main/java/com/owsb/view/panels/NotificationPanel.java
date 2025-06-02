package com.owsb.view.panels;

import com.owsb.model.Notification;
import com.owsb.model.User;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Panel for displaying user notifications
 */
public class NotificationPanel extends JPanel {
    private JPanel notificationsContainer;
    private JScrollPane scrollPane;
    private User currentUser;
    private JButton markAllReadBtn;
    private JLabel notificationCountLabel;
    
    private static final Color ACTION_COLOR = new Color(230, 126, 34);  // Orange
    private static final Color INFO_COLOR = new Color(52, 152, 219);    // Blue
    private static final Color WARNING_COLOR = new Color(231, 76, 60);  // Red
    private static final Color READ_BG_COLOR = new Color(245, 245, 245); // Light Gray
    
    public NotificationPanel(User user) {
        this.currentUser = user;
        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));
        
        // Header panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBorder(new EmptyBorder(0, 0, 10, 0));
        
        JLabel titleLabel = new JLabel("Notifications");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        headerPanel.add(titleLabel, BorderLayout.WEST);
        
        // Notification count and mark all read button
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        notificationCountLabel = new JLabel();
        updateNotificationCount();
        
        markAllReadBtn = new JButton("Mark All as Read");
        markAllReadBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                markAllNotificationsAsRead();
            }
        });
        
        actionsPanel.add(notificationCountLabel);
        actionsPanel.add(markAllReadBtn);
        headerPanel.add(actionsPanel, BorderLayout.EAST);
        
        add(headerPanel, BorderLayout.NORTH);
        
        // Notifications container
        notificationsContainer = new JPanel();
        notificationsContainer.setLayout(new BoxLayout(notificationsContainer, BoxLayout.Y_AXIS));
        
        scrollPane = new JScrollPane(notificationsContainer);
        scrollPane.setBorder(null);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);
        
        add(scrollPane, BorderLayout.CENTER);
        
        // Load notifications
        loadNotifications();
    }
    
    private void loadNotifications() {
        notificationsContainer.removeAll();
        
        List<Notification> notifications = Notification.getNotificationsForUser(currentUser.getUserId());
        
        if (notifications.isEmpty()) {
            JLabel emptyLabel = new JLabel("No notifications", JLabel.CENTER);
            emptyLabel.setFont(new Font("Arial", Font.PLAIN, 14));
            emptyLabel.setForeground(Color.GRAY);
            notificationsContainer.add(emptyLabel);
        } else {
            for (Notification notification : notifications) {
                notificationsContainer.add(createNotificationPanel(notification));
                // Add spacing between notifications
                notificationsContainer.add(Box.createRigidArea(new Dimension(0, 5)));
            }
        }
        
        notificationsContainer.revalidate();
        notificationsContainer.repaint();
        updateNotificationCount();
    }
    
    private JPanel createNotificationPanel(Notification notification) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(getColorForType(notification.getType())),
            new EmptyBorder(10, 10, 10, 10)
        ));
        
        // If notification is read, use a different background color
        if (notification.isRead()) {
            panel.setBackground(READ_BG_COLOR);
        } else {
            panel.setBackground(Color.WHITE);
        }
        
        // Title and time panel
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel(notification.getTitle());
        titleLabel.setFont(new Font("Arial", Font.BOLD, 14));
        titleLabel.setForeground(getColorForType(notification.getType()));
        
        JLabel timeLabel = new JLabel(notification.getTimeAgo());
        timeLabel.setFont(new Font("Arial", Font.ITALIC, 12));
        timeLabel.setForeground(Color.GRAY);
        
        headerPanel.add(titleLabel, BorderLayout.WEST);
        headerPanel.add(timeLabel, BorderLayout.EAST);
        
        // Message panel
        JLabel messageLabel = new JLabel("<html><body style='width: 300px'>" + notification.getMessage() + "</body></html>");
        messageLabel.setFont(new Font("Arial", Font.PLAIN, 13));
        
        // Action buttons panel
        JPanel actionsPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        actionsPanel.setOpaque(false);
        
        if (!notification.isRead()) {
            JButton markReadBtn = new JButton("Mark as Read");
            markReadBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    notification.markAsRead();
                    loadNotifications();
                }
            });
            actionsPanel.add(markReadBtn);
        }
        
        if (notification.getType().equals("ACTION_REQUIRED")) {
            JButton viewBtn = new JButton("View");
            viewBtn.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    navigateToRelatedItem(notification);
                    notification.markAsRead();
                    loadNotifications();
                }
            });
            actionsPanel.add(viewBtn);
        }
        
        JButton deleteBtn = new JButton("Delete");
        deleteBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                notification.deleteNotification();
                loadNotifications();
            }
        });
        actionsPanel.add(deleteBtn);
        
        // Add components to main panel
        panel.add(headerPanel, BorderLayout.NORTH);
        panel.add(messageLabel, BorderLayout.CENTER);
        panel.add(actionsPanel, BorderLayout.SOUTH);
        
        return panel;
    }
    
    private Color getColorForType(String type) {
        switch (type) {
            case "ACTION_REQUIRED":
                return ACTION_COLOR;
            case "WARNING":
                return WARNING_COLOR;
            case "INFO":
            default:
                return INFO_COLOR;
        }
    }
    
    private void navigateToRelatedItem(Notification notification) {
        // This method would be used to navigate to the related document
        // For now, we'll just show a message
        String entityType = notification.getRelatedEntityType();
        String entityId = notification.getRelatedEntityId();
        
        JOptionPane.showMessageDialog(this, 
            "Navigating to " + entityType + " " + entityId, 
            "Navigation", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // In a real implementation, we would call methods to switch to the appropriate panel
        // For example:
        // if (entityType.equals("PR")) {
        //     mainFrame.showPRPanel(entityId);
        // } else if (entityType.equals("PO")) {
        //     mainFrame.showPOPanel(entityId);
        // }
    }
    
    private void markAllNotificationsAsRead() {
        List<Notification> notifications = Notification.getNotificationsForUser(currentUser.getUserId());
        for (Notification notification : notifications) {
            if (!notification.isRead()) {
                notification.markAsRead();
            }
        }
        loadNotifications();
    }
    
    private void updateNotificationCount() {
        List<Notification> unreadNotifications = Notification.getUnreadNotificationsForUser(currentUser.getUserId());
        notificationCountLabel.setText(unreadNotifications.size() + " unread notifications");
    }
    
    // Refresh the notifications panel
    public void refreshNotifications() {
        loadNotifications();
    }
    
    // Get the number of unread notifications
    public int getUnreadNotificationCount() {
        return Notification.getUnreadNotificationsForUser(currentUser.getUserId()).size();
    }
} 