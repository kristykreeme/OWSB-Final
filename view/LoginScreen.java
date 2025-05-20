package view;

import model.User;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class LoginScreen extends JFrame {
    private JTextField usernameField;
    private JPasswordField passwordField;
    private JButton loginButton;
    
    // Colors
    private Color primaryColor = new Color(15, 102, 202);  // Blue color for the left panel
    
    public LoginScreen() {
        // Setup frame
        setTitle("OWSB - Login");
        setSize(800, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // Main content panel with two sections
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        
        // Left panel (blue with logo)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(primaryColor);
        leftPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        JPanel logoPanel = new JPanel(new BorderLayout());
        logoPanel.setOpaque(false);
        
        JLabel titleLabel = new JLabel("OMEGA WHOLESALE");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        JLabel subtitleLabel = new JLabel("SDN BHD");
        subtitleLabel.setFont(new Font("Arial", Font.BOLD, 18));
        subtitleLabel.setForeground(Color.WHITE);
        subtitleLabel.setHorizontalAlignment(JLabel.CENTER);
        
        // Large circular logo
        JPanel circlePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g.create();
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                
                // Draw large circle
                g2d.setColor(new Color(255, 255, 255, 60));
                int size = Math.min(getWidth(), getHeight()) - 40;
                int x = (getWidth() - size) / 2;
                int y = (getHeight() - size) / 2;
                g2d.fillOval(x, y, size, size);
                
                g2d.dispose();
            }
        };
        circlePanel.setOpaque(false);
        
        logoPanel.add(titleLabel, BorderLayout.NORTH);
        logoPanel.add(circlePanel, BorderLayout.CENTER);
        logoPanel.add(subtitleLabel, BorderLayout.SOUTH);
        
        leftPanel.add(logoPanel, BorderLayout.CENTER);
        
        // Right panel (white with login form)
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(Color.WHITE);
        rightPanel.setBorder(new EmptyBorder(40, 40, 40, 40));
        
        // Login title
        JPanel loginTitlePanel = new JPanel(new BorderLayout());
        loginTitlePanel.setOpaque(false);
        loginTitlePanel.setBorder(new EmptyBorder(0, 0, 30, 0));
        
        JLabel loginLabel = new JLabel("Login");
        loginLabel.setFont(new Font("Arial", Font.BOLD, 24));
        loginTitlePanel.add(loginLabel, BorderLayout.NORTH);
        
        JLabel instructionsLabel = new JLabel("Enter your credentials to access the system");
        instructionsLabel.setForeground(Color.GRAY);
        instructionsLabel.setBorder(new EmptyBorder(5, 0, 0, 0));
        loginTitlePanel.add(instructionsLabel, BorderLayout.CENTER);
        
        rightPanel.add(loginTitlePanel, BorderLayout.NORTH);
        
        // Login form
        JPanel formPanel = new JPanel();
        formPanel.setLayout(new BoxLayout(formPanel, BoxLayout.Y_AXIS));
        formPanel.setOpaque(false);
        
        // Username
        JLabel usernameLabel = new JLabel("Username");
        usernameLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(usernameLabel);
        formPanel.add(Box.createVerticalStrut(5));
        
        usernameField = new JTextField(20);
        usernameField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        usernameField.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(usernameField);
        formPanel.add(Box.createVerticalStrut(20));
        
        // Password
        JLabel passwordLabel = new JLabel("Password");
        passwordLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        formPanel.add(passwordLabel);
        formPanel.add(Box.createVerticalStrut(5));
        
        passwordField = new JPasswordField(20);
        passwordField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 35));
        passwordField.setAlignmentX(Component.LEFT_ALIGNMENT);
        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    loginButton.doClick();
                }
            }
        });
        formPanel.add(passwordField);
        formPanel.add(Box.createVerticalStrut(30));
        
        // Login button
        loginButton = new JButton("LOGIN");
        loginButton.setBackground(primaryColor);
        loginButton.setForeground(Color.WHITE);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.setPreferredSize(new Dimension(Integer.MAX_VALUE, 40));
        loginButton.setMinimumSize(new Dimension(100, 40));
        loginButton.setFocusPainted(false);
        loginButton.setBorderPainted(false);
        loginButton.setOpaque(true);
        loginButton.setFont(new Font("Arial", Font.BOLD, 14));
        loginButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        loginButton.addActionListener(e -> attemptLogin());
        formPanel.add(loginButton);
        
        // Add another vertical strut for padding
        formPanel.add(Box.createVerticalStrut(10));
        
        JPanel formWrapper = new JPanel(new BorderLayout());
        formWrapper.setOpaque(false);
        formWrapper.add(formPanel, BorderLayout.CENTER);
        
        rightPanel.add(formWrapper, BorderLayout.CENTER);
        
        // Footer
        JPanel footerPanel = new JPanel(new BorderLayout());
        footerPanel.setOpaque(false);
        JLabel footerLabel = new JLabel("© 2023 Omega Wholesale Sdn Bhd");
        footerLabel.setHorizontalAlignment(JLabel.CENTER);
        footerLabel.setFont(new Font("Arial", Font.PLAIN, 12));
        footerLabel.setForeground(Color.GRAY);
        footerPanel.add(footerLabel, BorderLayout.CENTER);
        
        rightPanel.add(footerPanel, BorderLayout.SOUTH);
        
        // Add the two panels to the main panel
        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);
        
        // Set the content pane
        setContentPane(mainPanel);
    }
    
    private void attemptLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword());
        
        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Please enter both username and password", 
                "Login Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Authenticate user
        List<User> users = User.getAllUsers();
        
        if (users.isEmpty()) {
            // If no users exist, create default admin user
            User adminUser = new User("U001", "Administrator", "admin", "admin", "ADMIN");
            adminUser.saveUser();
            
            JOptionPane.showMessageDialog(this, 
                "Default admin user created. Username: admin, Password: admin", 
                "First Time Setup", 
                JOptionPane.INFORMATION_MESSAGE);
            
            users = User.getAllUsers();
        }
        
        User authenticatedUser = null;
        
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                authenticatedUser = user;
                break;
            }
        }
        
        if (authenticatedUser != null) {
            // Login successful
            JOptionPane.showMessageDialog(this, 
                "Welcome, " + authenticatedUser.getName(), 
                "Login Successful", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Open dashboard
            Dashboard dashboard = new Dashboard(authenticatedUser);
            dashboard.setVisible(true);
            
            // Close login screen
            this.dispose();
        } else {
            // Login failed
            JOptionPane.showMessageDialog(this, 
                "Invalid username or password", 
                "Login Failed", 
                JOptionPane.ERROR_MESSAGE);
            
            // Clear password field
            passwordField.setText("");
            
            // Focus on username field if empty, otherwise focus on password field
            if (username.isEmpty()) {
                usernameField.requestFocus();
            } else {
                passwordField.requestFocus();
            }
        }
    }
} 