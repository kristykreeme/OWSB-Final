package model;

import java.io.*;
import java.util.*;

public class User {
    private String userId;
    private String name;
    private String username;
    private String password;
    private String role; // ADMIN, SALES_MANAGER, PURCHASE_MANAGER, INVENTORY_MANAGER, FINANCE_MANAGER
    
    // File path for user data
    private static final String FILE_PATH = "data/users.txt";
    
    // Constructor
    public User(String userId, String name, String username, String password, String role) {
        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
        this.role = role;
    }
    
    // Default constructor
    public User() {
    }
    
    // Getters and Setters
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getRole() {
        return role;
    }
    
    public void setRole(String role) {
        this.role = role;
    }
    
    // Generate a new user ID
    public static String generateUserId() {
        List<User> users = getAllUsers();
        
        if (users.isEmpty()) {
            return "U001";
        }
        
        int maxId = 0;
        for (User user : users) {
            try {
                int userId = Integer.parseInt(user.getUserId().substring(1));
                if (userId > maxId) {
                    maxId = userId;
                }
            } catch (NumberFormatException e) {
                // Skip invalid user IDs
            }
        }
        
        return String.format("U%03d", maxId + 1);
    }
    
    // Save user to file
    public boolean saveUser() {
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
            
            // Check if user already exists
            List<User> existingUsers = getAllUsers();
            for (User user : existingUsers) {
                if (user.getUserId().equals(this.userId)) {
                    return false; // User already exists
                }
            }
            
            // Append user to file
            try (FileWriter fw = new FileWriter(file, true);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                out.println(formatUserData());
            }
            
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    
    // Update existing user
    public boolean updateUser() {
        List<User> users = getAllUsers();
        boolean found = false;
        
        try {
            // Ensure data directory exists
            File dataDir = new File("data");
            if (!dataDir.exists()) {
                dataDir.mkdir();
            }
            
            // Create temporary file
            File tempFile = new File("data/users_temp.txt");
            if (!tempFile.exists()) {
                tempFile.createNewFile();
            }
            
            // Write all users to temp file, replacing the one to update
            try (FileWriter fw = new FileWriter(tempFile);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                for (User user : users) {
                    if (user.getUserId().equals(this.userId)) {
                        // Write updated user
                        out.println(formatUserData());
                        found = true;
                    } else {
                        // Write existing user
                        out.println(user.formatUserData());
                    }
                }
            }
            
            if (!found) {
                // User not found
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
    
    // Delete user
    public static boolean deleteUser(String userId) {
        List<User> users = getAllUsers();
        boolean found = false;
        
        try {
            // Create temporary file
            File tempFile = new File("data/users_temp.txt");
            
            // Write all users except the one to delete
            try (FileWriter fw = new FileWriter(tempFile);
                 BufferedWriter bw = new BufferedWriter(fw);
                 PrintWriter out = new PrintWriter(bw)) {
                
                for (User user : users) {
                    if (!user.getUserId().equals(userId)) {
                        out.println(user.formatUserData());
                    } else {
                        found = true;
                    }
                }
            }
            
            if (!found) {
                // User not found
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
    
    // Get all users from file
    public static List<User> getAllUsers() {
        List<User> users = new ArrayList<>();
        
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            return users;
        }
        
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] data = line.split(",");
                if (data.length >= 5) {
                    User user = new User(
                        data[0],
                        data[1],
                        data[2],
                        data[3],
                        data[4]
                    );
                    users.add(user);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return users;
    }
    
    // Get user by ID
    public static User getUserById(String userId) {
        List<User> users = getAllUsers();
        for (User user : users) {
            if (user.getUserId().equals(userId)) {
                return user;
            }
        }
        return null;
    }
    
    // Format user data for file storage
    private String formatUserData() {
        return String.format("%s,%s,%s,%s,%s", 
            userId, name, username, password, role);
    }
    
    @Override
    public String toString() {
        return name + " (" + role + ")";
    }
} 