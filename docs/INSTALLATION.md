# Installation Guide

## Prerequisites

### Java Development Kit (JDK)
- **Required Version**: JDK 8 or higher
- **Recommended**: JDK 11 or JDK 17 (LTS versions)

#### Installing JDK

**Windows:**
1. Download JDK from [Oracle](https://www.oracle.com/java/technologies/downloads/) or [OpenJDK](https://openjdk.org/)
2. Run the installer and follow the setup wizard
3. Set JAVA_HOME environment variable
4. Add JDK bin directory to PATH

**macOS:**
```bash
# Using Homebrew
brew install openjdk@11

# Or download from Oracle/OpenJDK websites
```

**Linux (Ubuntu/Debian):**
```bash
sudo apt update
sudo apt install openjdk-11-jdk
```

**Linux (CentOS/RHEL):**
```bash
sudo yum install java-11-openjdk-devel
```

### Verify Installation
```bash
java -version
javac -version
```

## Installation Methods

### Method 1: Using Build Scripts (Recommended)

1. **Download/Clone the Project**
   ```bash
   # If using Git
   git clone <repository-url>
   cd omega-wholesale-management
   
   # Or extract from ZIP file
   unzip owsb-project.zip
   cd owsb-project
   ```

2. **Make Scripts Executable (Unix/Linux/macOS)**
   ```bash
   chmod +x build.sh
   chmod +x run.sh
   ```

3. **Build the Application**
   ```bash
   ./build.sh
   ```

4. **Run the Application**
   ```bash
   ./run.sh
   ```

### Method 2: Using Maven (If Available)

1. **Build with Maven**
   ```bash
   mvn clean compile
   ```

2. **Create JAR file**
   ```bash
   mvn package
   ```

3. **Run the Application**
   ```bash
   java -jar target/omega-wholesale-management-1.2.0.jar
   ```

### Method 3: Manual Compilation

1. **Create Target Directory**
   ```bash
   mkdir -p target/classes
   ```

2. **Copy Resources**
   ```bash
   cp -r src/main/resources/* target/classes/
   ```

3. **Compile Java Files**
   ```bash
   find src/main/java -name "*.java" > sources.txt
   javac -d target/classes -cp target/classes @sources.txt
   ```

4. **Run the Application**
   ```bash
   java -cp target/classes com.owsb.OWSB
   ```

## Post-Installation Setup

### 1. First Run
- The application will create necessary data directories automatically
- Default admin user will be created on first run
- Login with: username `admin`, password `admin`

### 2. Data Directory Structure
After first run, you'll see:
```
src/main/resources/
├── data/
│   ├── users.txt
│   ├── items.txt
│   ├── suppliers.txt
│   ├── purchase_orders.txt
│   ├── purchase_requisitions.txt
│   ├── daily_sales.txt
│   ├── stock_adjustments.txt
│   ├── po_items.txt
│   └── pr_items.txt
└── config/
    └── UserCredentials.txt
```

### 3. Initial Configuration
1. Login as admin
2. Create additional user accounts
3. Set up initial inventory items
4. Configure suppliers
5. Customize system settings

## Troubleshooting

### Common Issues

**1. "java: command not found"**
- Solution: Install JDK and ensure it's in your PATH

**2. "Permission denied" on scripts**
- Solution: Make scripts executable with `chmod +x *.sh`

**3. "ClassNotFoundException"**
- Solution: Ensure all Java files are compiled and in the classpath

**4. Data files not found**
- Solution: Ensure you're running from the project root directory

**5. GUI doesn't appear**
- Solution: Ensure you have a graphical environment (not running in headless mode)

### Getting Help

1. Check the main README.md file
2. Review the troubleshooting section
3. Verify all prerequisites are met
4. Check file permissions and directory structure

## System Requirements

### Minimum Requirements
- **OS**: Windows 7+, macOS 10.10+, or Linux
- **RAM**: 512MB
- **Storage**: 50MB free space
- **Display**: 1024x768 resolution

### Recommended Requirements
- **OS**: Windows 10+, macOS 11+, or modern Linux distribution
- **RAM**: 1GB or more
- **Storage**: 100MB free space
- **Display**: 1280x720 or higher resolution

## Security Considerations

1. **Default Passwords**: Change default admin password immediately
2. **File Permissions**: Ensure data files have appropriate permissions
3. **Backup**: Regularly backup data files
4. **Updates**: Keep Java runtime updated for security patches

## Next Steps

After successful installation:
1. Read the User Guide (docs/USER_GUIDE.md)
2. Review the API Documentation (docs/API.md)
3. Check the FAQ (docs/FAQ.md)
4. Explore the sample data and features 