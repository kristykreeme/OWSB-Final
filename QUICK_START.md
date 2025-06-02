# 🚀 OWSB Quick Start Guide

## ⚡ **Super Quick Start (2 Minutes)**

### **1. Prerequisites Check**
```bash
java -version
# If this fails, install Java first (see docs/TERMINAL_GUIDE.md)
```

### **2. Navigate to Project**
```bash
cd /path/to/your/owsb-project
# Example: cd /Users/yourname/Downloads/owsb-project
```

### **3. Run Application**
```bash
# macOS/Linux:
chmod +x run.sh
./run.sh

# Windows:
java -jar target/OWSB.jar
```

### **4. Login**
- **Username**: `admin`
- **Password**: `admin`

---

## 📋 **Essential Commands**

| Action | Command |
|--------|---------|
| **Check Java** | `java -version` |
| **Navigate to project** | `cd /path/to/owsb-project` |
| **Make executable** | `chmod +x build.sh run.sh` |
| **Build project** | `./build.sh` |
| **Run application** | `./run.sh` |
| **Direct run** | `java -jar target/OWSB.jar` |
| **Manual run** | `java -cp target/classes com.owsb.OWSB` |

---

## 🔐 **Test Accounts**

| Role | Username | Password |
|------|----------|----------|
| **Admin** | `admin` | `admin` |
| **Sales** | `sales` | `sales` |
| **Purchase** | `purchase` | `purchase` |
| **Finance** | `finance` | `finance` |
| **Inventory** | `inventory` | `inventory` |

---

## 🛠️ **Troubleshooting**

| Problem | Solution |
|---------|----------|
| **"java: command not found"** | Install Java JDK 8+ |
| **"Permission denied"** | `chmod +x build.sh run.sh` |
| **"No such file"** | Check you're in project directory |
| **GUI doesn't appear** | Ensure graphical environment |
| **Login fails** | Use `admin`/`admin` credentials |

---

## 📁 **Expected File Structure**
```
owsb-project/
├── build.sh          # Build script
├── run.sh            # Run script
├── pom.xml           # Maven config
├── README.md         # Documentation
├── src/              # Source code
├── target/           # Compiled files
├── data/             # Application data
└── docs/             # Documentation
```

---

## 🎯 **Success Indicators**

**Build Success:**
```
Build completed successfully!
JAR file created successfully: target/OWSB.jar
```

**Run Success:**
```
OWSB - Omega Wholesale Management System
Starting Application...
Creating LoginScreen...
```

**Login Success:**
```
Authentication successful for user: admin
Dashboard created, making it visible...
```

---

## 📖 **Need More Help?**

- **Complete Guide**: `docs/TERMINAL_GUIDE.md`
- **User Credentials**: `docs/USER_CREDENTIALS.md`
- **Installation**: `docs/INSTALLATION.md`
- **Project Info**: `README.md`

---

**🎉 Ready to run OWSB in under 2 minutes!** 