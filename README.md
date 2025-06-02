# OWSB - Omega Wholesale Sdn Bhd Management System

## Project Overview

OWSB is a comprehensive inventory and business management system designed for Omega Wholesale Sdn Bhd. The system provides role-based access control and manages various business operations including inventory management, sales tracking, purchase orders, supplier management, and financial reporting.

## Features

### Core Functionality
- **User Management**: Role-based access control with different permission levels
- **Inventory Management**: Track items, stock levels, and stock adjustments
- **Sales Management**: Daily sales entry and reporting
- **Purchase Management**: Purchase requisitions and purchase orders
- **Supplier Management**: Maintain supplier information and relationships
- **Financial Management**: Payment processing and financial reporting
- **Notification System**: Real-time notifications for important events

### User Roles
- **Admin**: Full system access
- **Sales Manager/Staff**: Sales operations and reporting
- **Purchase Manager/Staff**: Purchase requisitions and orders
- **Inventory Manager/Staff**: Stock management and adjustments
- **Finance Manager**: Financial operations and reporting

## Project Structure

```
OWSB/
├── src/
│   └── main/
│       ├── java/
│       │   └── com/
│       │       └── owsb/
│       │           ├── OWSB.java                 # Main application entry point
│       │           ├── model/                    # Data models
│       │           │   ├── User.java
│       │           │   ├── Item.java
│       │           │   ├── Supplier.java
│       │           │   ├── PurchaseOrder.java
│       │           │   ├── PurchaseRequisition.java
│       │           │   ├── DailySales.java
│       │           │   ├── StockAdjustment.java
│       │           │   ├── Notification.java
│       │           │   └── DocumentHistory.java
│       │           └── view/                     # User interface
│       │               ├── LoginScreen.java
│       │               ├── Dashboard.java
│       │               ├── components/           # Reusable UI components
│       │               │   └── UserProfileComponent.java
│       │               └── panels/               # Main application panels
│       │                   ├── DashboardPanel.java
│       │                   ├── ItemManagementPanel.java
│       │                   ├── SupplierManagementPanel.java
│       │                   ├── SalesDataEntryPanel.java
│       │                   ├── PurchaseRequisitionPanel.java
│       │                   ├── ViewPurchaseRequisitionsPanel.java
│       │                   ├── PurchaseOrderPanel.java
│       │                   ├── InventoryManagementPanel.java
│       │                   ├── UserRegistrationPanel.java
│       │                   ├── UserManagementPanel.java
│       │                   ├── PaymentProcessingPanel.java
│       │                   ├── FinancialReportsPanel.java
│       │                   ├── ReportsPanel.java
│       │                   └── NotificationPanel.java
│       └── resources/
│           ├── data/                             # Application data files
│           │   ├── users.txt
│           │   ├── items.txt
│           │   ├── suppliers.txt
│           │   ├── purchase_orders.txt
│           │   ├── purchase_requisitions.txt
│           │   ├── daily_sales.txt
│           │   ├── stock_adjustments.txt
│           │   ├── po_items.txt
│           │   └── pr_items.txt
│           └── config/
│               └── UserCredentials.txt
├── target/                                       # Compiled classes
├── docs/                                         # Documentation
├── lib/                                          # External libraries
├── build.sh                                     # Build script
├── run.sh                                       # Run script
└── README.md                                    # This file
```

## System Requirements

- **Java**: JDK 8 or higher
- **Operating System**: Windows, macOS, or Linux
- **Memory**: Minimum 512MB RAM
- **Storage**: 50MB free disk space

## Installation & Setup

### 1. Prerequisites
Ensure you have Java JDK 8 or higher installed:
```bash
java -version
javac -version
```

### 2. Clone/Download the Project
Download the project files to your local machine.

### 3. Build the Application
```bash
# Make the build script executable (Unix/Linux/macOS)
chmod +x build.sh

# Run the build script
./build.sh
```

### 4. Run the Application
```bash
# Make the run script executable (Unix/Linux/macOS)
chmod +x run.sh

# Run the application
./run.sh
```

## Default Login Credentials

### Admin Account
- **Username**: admin
- **Password**: admin

### Test Accounts
- **Sales Manager**: sales_manager / password
- **Purchase Manager**: purchase_manager / password
- **Inventory Manager**: inventory_manager / password
- **Finance Manager**: finance_manager / password

## Usage Guide

### 1. Login
- Start the application
- Enter your username and password
- Click "Login" to access the system

### 2. Navigation
- Use the sidebar menu to navigate between different modules
- The available modules depend on your user role
- Click on any menu item to switch to that panel

### 3. Key Operations

#### Inventory Management
- Add, edit, and delete items
- Track stock levels
- Record stock adjustments
- View low stock alerts

#### Sales Operations
- Enter daily sales data
- Generate sales reports
- Track sales performance

#### Purchase Management
- Create purchase requisitions
- Convert requisitions to purchase orders
- Track order status
- Manage supplier relationships

#### User Management (Admin only)
- Register new users
- Manage user accounts
- Assign roles and permissions

## Data Storage

The application uses text-based file storage for simplicity:
- All data files are stored in `src/main/resources/data/`
- Configuration files are in `src/main/resources/config/`
- Data is automatically saved when changes are made
- Backup your data files regularly

## Troubleshooting

### Common Issues

1. **Application won't start**
   - Check Java installation
   - Verify all files are present
   - Check file permissions

2. **Login fails**
   - Verify username/password
   - Check UserCredentials.txt file
   - Ensure proper file encoding

3. **Data not saving**
   - Check file permissions
   - Verify data directory exists
   - Ensure sufficient disk space

### Support
For technical support or questions, please refer to the project documentation or contact the development team.

## Development Notes

### Architecture
- **MVC Pattern**: Model-View-Controller architecture
- **Swing GUI**: Java Swing for user interface
- **File-based Storage**: Text files for data persistence
- **Role-based Security**: User authentication and authorization

### Code Organization
- **Models**: Data structures and business logic
- **Views**: User interface components
- **Controllers**: Event handling and business logic coordination

## License

This project is developed for educational/business purposes. All rights reserved.

## Version History

- **v1.0**: Initial release with core functionality
- **v1.1**: Enhanced UI and bug fixes
- **v1.2**: Added notification system and improved reporting

---

**Omega Wholesale Sdn Bhd Management System**  
*Streamlining business operations through technology*