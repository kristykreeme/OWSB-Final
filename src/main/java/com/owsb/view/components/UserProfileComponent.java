package com.owsb.view.components;

import com.owsb.model.User;
import com.owsb.view.LoginScreen;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class UserProfileComponent {
    private User currentUser;
    private JFrame parentFrame;
    
    public UserProfileComponent(User user, JFrame parentFrame) {
        this.currentUser = user;
        this.parentFrame = parentFrame;
    }
    
    public JPanel createUserProfilePanel() {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        panel.setOpaque(false);
        
        JPanel badge = createUserBadge();
        panel.add(badge);
        
        return panel;
    }
    
    private JPanel createUserBadge() {
        JPanel badge = new JPanel(new BorderLayout());
        badge.setBackground(new Color(245, 245, 245));
        badge.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(12, 16, 12, 16)
        ));
        badge.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        
        // Avatar
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(new Color(25, 118, 210));
                g2d.fillOval(0, 0, 36, 36);
                g2d.setColor(Color.WHITE);
                g2d.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g2d.getFontMetrics();
                String initials = getUserInitials();
                int x = (36 - fm.stringWidth(initials)) / 2;
                int y = (36 + fm.getAscent()) / 2 - 2;
                g2d.drawString(initials, x, y);
                g2d.dispose();
            }
        };
        avatar.setPreferredSize(new Dimension(36, 36));
        avatar.setOpaque(false);
        
        // User info
        JPanel userInfo = new JPanel();
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBackground(new Color(245, 245, 245));
        userInfo.setBorder(BorderFactory.createEmptyBorder(0, 12, 0, 0));
        
        JLabel nameLabel = new JLabel(currentUser.getName());
        nameLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        nameLabel.setForeground(new Color(60, 60, 60));
        
        JLabel roleLabel = new JLabel(getRoleDisplayName());
        roleLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        roleLabel.setForeground(new Color(107, 114, 128));
        
        JLabel userEmailLabel = new JLabel(currentUser.getUsername() + "@owsb.com");
        userEmailLabel.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        userEmailLabel.setForeground(new Color(107, 114, 128));
        
        userInfo.add(nameLabel);
        userInfo.add(roleLabel);
        userInfo.add(userEmailLabel);
        
        badge.add(avatar, BorderLayout.WEST);
        badge.add(userInfo, BorderLayout.CENTER);
        
        // Add click listener for dropdown menu
        badge.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showUserMenu(badge, e.getX(), e.getY());
            }
            
            @Override
            public void mouseEntered(MouseEvent e) {
                badge.setBackground(new Color(235, 235, 235));
                userInfo.setBackground(new Color(235, 235, 235));
            }
            
            @Override
            public void mouseExited(MouseEvent e) {
                badge.setBackground(new Color(245, 245, 245));
                userInfo.setBackground(new Color(245, 245, 245));
            }
        });
        
        return badge;
    }
    
    private void showUserMenu(JComponent parent, int x, int y) {
        JPopupMenu menu = new JPopupMenu();
        menu.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(8, 0, 8, 0)
        ));
        
        // User info as a simple menu item
        JMenuItem userInfoItem = new JMenuItem("<html><b>" + currentUser.getName() + "</b><br/>" + 
                                              "<font color='gray'>" + currentUser.getUsername() + "@owsb.com</font></html>");
        userInfoItem.setEnabled(false);
        userInfoItem.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        menu.add(userInfoItem);
        
        menu.addSeparator();
        
        // Logout option
        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        logoutItem.setForeground(new Color(220, 53, 69));
        logoutItem.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));
        logoutItem.addActionListener(e -> logout());
        
        menu.add(logoutItem);
        
        // Show menu at the correct position
        menu.show(parent, 0, parent.getHeight() + 2);
    }
    
    private void logout() {
        int option = JOptionPane.showConfirmDialog(
            parentFrame,
            "Are you sure you want to logout?",
            "Confirm Logout",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.QUESTION_MESSAGE
        );
        
        if (option == JOptionPane.YES_OPTION) {
            parentFrame.dispose();
            SwingUtilities.invokeLater(() -> {
                new LoginScreen().setVisible(true);
            });
        }
    }
    
    private String getUserInitials() {
        String role = currentUser.getRole();
        switch (role) {
            case "ADMIN": return "AD";
            case "PURCHASE_MANAGER": return "PM";
            case "PURCHASE_STAFF": return "PS";
            case "SALES_MANAGER": return "SM";
            case "SALES_STAFF": return "SS";
            case "FINANCE_MANAGER": return "FM";
            case "INVENTORY_MANAGER": return "IM";
            case "INVENTORY_STAFF": return "IS";
            default: return currentUser.getName().substring(0, 1).toUpperCase();
        }
    }
    
    private String getRoleDisplayName() {
        String role = currentUser.getRole();
        switch (role) {
            case "ADMIN": return "Admin User";
            case "PURCHASE_MANAGER": return "Purchase Manager";
            case "PURCHASE_STAFF": return "Purchase Staff";
            case "SALES_MANAGER": return "Sales Manager";
            case "SALES_STAFF": return "Sales Staff";
            case "FINANCE_MANAGER": return "Finance Manager";
            case "INVENTORY_MANAGER": return "Inventory Manager";
            case "INVENTORY_STAFF": return "Inventory Staff";
            default: return "User";
        }
    }
} 