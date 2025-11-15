# 📦 Inventory Monitoring & Reporting System - UI Guide

## ✨ Overview
This is a professional, interactive JavaFX-based Inventory Management System with separate interfaces for **Admin** and **User** roles.

---

## 🚀 How to Run the Application

### Option 1: Using Maven (Recommended)
```bash
cd "Inventory Monitoring And Reporting System"
mvn compile exec:java
```

### Option 2: Using JavaFX Maven Plugin
```bash
mvn clean javafx:run
```

### Option 3: Build JAR and Run
```bash
mvn clean package
java -jar target/InventoryMonitoringAndReportingSystem-1.0-SNAPSHOT-jar-with-dependencies.jar
```

---

## 🎨 UI Features

### 🔐 Login Screen
- **Beautiful gradient background** with purple theme
- **White card-style login form** with rounded corners and shadows
- **Username and Password** fields with placeholder text
- **Login button** with hover effects
- **Links** for registration and email verification
- **Real-time validation** and error messages

### 📝 Registration Screen
- **Email, Username, Password, and Role** fields
- **OTP verification** system integrated
- **Send OTP button** to receive verification code
- **Real-time validation** before registration
- **Automatic email verification** upon successful registration

---

## 👨‍💼 Admin Dashboard Features

### 🏠 Main Dashboard
- **Top Navigation Bar**
  - System title with gradient background
  - User info display (username)
  - Logout button

- **Left Sidebar Menu**
  - 📊 Dashboard
  - ➕ Add Product
  - 🗑️ Remove Product
  - ✏️ Update Product
  - 🔍 Search Product
  - 📄 Generate Report
  - 🔄 Refresh

- **Statistics Cards**
  - Total Products (Blue card)
  - Total Inventory Value (Green card)
  - Low Stock Alerts (Red card)
  
- **Product Table**
  - Displays: ID, Name, Category, Quantity, Price, Threshold, Stock Value
  - Sortable columns
  - Responsive design

### ➕ Add Product
- Dialog box with fields:
  - Product ID
  - Name
  - Category
  - Quantity
  - Price
  - Stock Threshold (default: 10)
- Add and Cancel buttons
- Input validation

### 🗑️ Remove Product
- Simple dialog asking for Product ID
- Confirmation before deletion
- Success/error messages

### ✏️ Update Product
- Dialog with:
  - Product ID
  - New Quantity
  - New Price
- Updates inventory in real-time

### 🔍 Search Product
- **Search by Name**: Enter product name to find specific items
- **Search by Price Range**: Filter products between min and max prices
- Results displayed in text area

### 📄 Generate Report
- Creates CSV report of all inventory
- Automatically sends email to admin
- Shows file path and confirmation

### 🔔 Stock Alerts
- **Automatic scheduler** runs every 5 minutes
- Checks products below threshold
- Updates dashboard statistics
- Shows low stock count in real-time

---

## 👤 User Dashboard Features

### 🏠 Main Dashboard
- **Top Navigation Bar**
  - Green gradient theme (different from admin)
  - User info display
  - Logout button

- **Left Sidebar Menu**
  - 📊 Dashboard
  - 📄 View All Products
  - 🔍 Search Product
  - 💰 Search by Price
  - 📚 View by Category
  - 🔄 Refresh

- **Statistics Cards**
  - Available Products (Blue)
  - Total Categories (Purple)

- **Product Catalog Table**
  - ID, Product Name, Category, Available Qty, Price
  - Clean, read-only view
  - All products visible

### 🔍 Search Features
1. **Search by Name**: Find specific products
2. **Search by Price Range**: Filter by min/max price
3. **View by Category**: See all products in a category

### 📊 View Options
- View all products
- Filter and search
- Refresh to reload data

---

## 🎨 Design Features

### Color Scheme
- **Admin**: Purple gradient (`#667eea` to `#764ba2`)
- **User**: Green gradient (`#2ecc71` to `#27ae60`)
- **Background**: Light gray (`#f5f7fa`)
- **Cards**: White with subtle shadows
- **Sidebar**: Dark (`#2c3e50`, `#34495e`)

### Modern UI Elements
✅ Gradient backgrounds
✅ Card-based layouts
✅ Hover effects on buttons
✅ Shadow effects (dropshadow)
✅ Rounded corners (border-radius)
✅ Professional typography
✅ Responsive tables
✅ Icon integration (emoji)
✅ Color-coded statistics

### Interactions
- **Hover effects** on all buttons
- **Click feedback** with style changes
- **Dialog boxes** for operations
- **Alert messages** for confirmations
- **Real-time updates** on data changes

---

## 🔧 Technical Features

### JavaFX Components Used
- `BorderPane`, `VBox`, `HBox` - Layouts
- `TableView` - Data display
- `Button`, `Label`, `TextField` - Controls
- `Dialog`, `Alert` - Popups
- `Scene`, `Stage` - Windows

### Programmatic UI
- **No FXML files** - All UI built in Java code
- **Complete control** over styling
- **Dynamic content** creation
- **Event handling** inline

### Database Integration
- Real-time data from MySQL
- CRUD operations
- Search functionality
- Report generation

### Background Services
- **Stock alert scheduler** (Admin only)
- Runs every 5 minutes
- Checks threshold levels
- Updates dashboard

---

## 📋 User Workflow

### For Admin:
1. **Login** with admin credentials
2. View **Dashboard Statistics**
3. **Add/Update/Remove** products
4. **Search** for specific items
5. **Generate reports** and send via email
6. Monitor **low stock alerts**
7. **Logout** when done

### For Regular User:
1. **Login** with user credentials
2. View **product catalog**
3. **Search products** by name, price, or category
4. **Browse inventory** (read-only)
5. **Logout** when done

---

## ⚙️ Configuration

### Database Setup
Ensure your MySQL database is configured with:
- Database: `inventory_db`
- Tables: `users`, `products`
- Proper credentials in `dbConnection.java`

### Email Service
Configure SMTP settings in `EmailService.java` for:
- OTP sending
- Report emailing

---

## 🎯 Key Features Summary

✨ **Professional UI** - Modern, clean design
🔐 **Secure Login** - OTP verification
👥 **Role-based Access** - Admin vs User
📊 **Real-time Stats** - Live dashboard updates
🔍 **Advanced Search** - Multiple search options
📧 **Email Integration** - Reports and OTP
⚡ **Fast Performance** - Efficient JavaFX rendering
📱 **Responsive** - Adapts to window size
🎨 **Custom Styling** - Beautiful gradients and effects

---

## 📞 Support
For issues or questions, check the database connection and ensure all services are running properly.

Enjoy using the Inventory Management System! 🚀

❤️ Contributing

Contributions are welcome!

Fork the repo
Create a branch: feature/my-feature
Commit changes
Push and submit a PR

⭐ Don’t forget to star the repository if this project helped you!
