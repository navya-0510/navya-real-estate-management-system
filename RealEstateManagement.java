import java.util.*;
import java.io.*;

// ========================= PROPERTY CLASS =========================
class Property {
    private String propertyId;
    private String type;
    private String address;
    private double price;
    private double area;
    private int bedrooms;
    private boolean available;

    public Property(String propertyId, String type, String address,
                    double price, double area, int bedrooms) {
        this.propertyId = propertyId;
        this.type = type;
        this.address = address;
        this.price = price;
        this.area = area;
        this.bedrooms = bedrooms;
        this.available = true;
    }

    // Getters
    public String getPropertyId() { return propertyId; }
    public String getType() { return type; }
    public String getAddress() { return address; }
    public double getPrice() { return price; }
    public double getArea() { return area; }
    public int getBedrooms() { return bedrooms; }
    public boolean isAvailable() { return available; }

    // Setters
    public void setPrice(double price) { this.price = price; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format(
                "ID: %s | Type: %s | Address: %s | Price: %.2f | Area: %.1f sqft | Bedrooms: %d | Available: %s",
                propertyId, type, address, price, area, bedrooms,
                available ? "Yes" : "No"
        );
    }
}

// ===================== BUY / SELL RECORD CLASS ====================
class BuySellRecord {
    private String propertyId;
    private String action;      // Bought / Sold
    private String date;

    public BuySellRecord(String propertyId, String action, String date) {
        this.propertyId = propertyId;
        this.action = action;
        this.date = date;
    }

    @Override
    public String toString() {
        return propertyId + "," + action + "," + date;
    }

    public String display() {
        return "Property ID: " + propertyId + " | Action: " + action + " | Date: " + date;
    }
}

// ========================= MAIN CLASS =============================
public class RealEstateManagement {

    private static Scanner scanner = new Scanner(System.in);

    private static List<Property> properties = new ArrayList<>();
    private static List<BuySellRecord> history = new ArrayList<>();

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    // Files
    private static final String PROPERTIES_FILE = "properties.txt";
    private static final String HISTORY_FILE = "history.txt";

    public static void main(String[] args) {
        loadPropertiesFromFile();
        loadHistoryFromFile();
        showLoginMenu();
    }

    // ======================= LOGIN MENU ============================
    private static void showLoginMenu() {
        System.out.println("===== NAVYA REAL ESTATE MANAGEMENT =====");

        while (true) {
            System.out.print("\nEnter Username: ");
            String user = scanner.nextLine();

            System.out.print("Enter Password: ");
            String pass = scanner.nextLine();

            if (user.equals(ADMIN_USER) && pass.equals(ADMIN_PASS)) {
                System.out.println("\nLogin Successful! Welcome Admin.");
                showMainMenu();
                return;
            } else {
                System.out.println("Incorrect Login. Try Again.");
            }
        }
    }

    // ======================= MAIN MENU =============================
    private static void showMainMenu() {
        while (true) {
            System.out.println("\n===== MAIN MENU =====");
            System.out.println("1. Add New Property");
            System.out.println("2. View All Properties");
            System.out.println("3. Search Properties");
            System.out.println("4. Update Property Price");
            System.out.println("5. Delete Property");
            System.out.println("6. Buy a Property");
            System.out.println("7. Sell a Property");
            System.out.println("8. View Buy/Sell History");
            System.out.println("9. Logout");
            System.out.print("Choose option: ");

            try {
                int choice = Integer.parseInt(scanner.nextLine());

                switch (choice) {
                    case 1 -> addProperty();
                    case 2 -> viewProperties();
                    case 3 -> searchProperties();
                    case 4 -> updateProperty();
                    case 5 -> deleteProperty();
                    case 6 -> buyProperty();
                    case 7 -> sellProperty();
                    case 8 -> viewHistory();
                    case 9 -> {
                        System.out.println("Logged out successfully!");
                        return;
                    }
                    default -> System.out.println("Invalid choice! Try again.");
                }

            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number!");
            }
        }
    }

    // ===================== ADD PROPERTY ===========================
    private static void addProperty() {
        System.out.println("\n=== ADD NEW PROPERTY ===");

        System.out.print("Enter Property ID: ");
        String id = scanner.nextLine();

        System.out.print("Enter Type: ");
        String type = scanner.nextLine();

        System.out.print("Enter Address: ");
        String address = scanner.nextLine();

        System.out.print("Enter Price: ");
        double price = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter Area (sqft): ");
        double area = Double.parseDouble(scanner.nextLine());

        System.out.print("Enter Bedrooms: ");
        int bedrooms = Integer.parseInt(scanner.nextLine());

        properties.add(new Property(id, type, address, price, area, bedrooms));
        savePropertiesToFile();
        System.out.println("Property added successfully!");
    }

    // ===================== VIEW PROPERTIES =========================
    private static void viewProperties() {
        if (properties.isEmpty()) {
            System.out.println("No properties available.");
            return;
        }

        System.out.println("\n=== ALL PROPERTIES ===");
        for (Property p : properties) {
            System.out.println(p);
        }
    }

    // ======================== SEARCH ===============================
    private static void searchProperties() {
        System.out.print("Enter address to search: ");
        String search = scanner.nextLine().toLowerCase();

        boolean found = false;
        for (Property p : properties) {
            if (p.getAddress().toLowerCase().contains(search)) {
                System.out.println(p);
                found = true;
            }
        }

        if (!found) System.out.println("No matching properties found.");
    }

    // ======================== UPDATE ===============================
    private static void updateProperty() {
        viewProperties();
        if (properties.isEmpty()) return;

        System.out.print("Enter Property ID to update: ");
        String id = scanner.nextLine();

        for (Property p : properties) {
            if (p.getPropertyId().equals(id)) {
                System.out.print("Enter new price: ");
                double newPrice = Double.parseDouble(scanner.nextLine());
                p.setPrice(newPrice);

                savePropertiesToFile();
                System.out.println("Price updated successfully!");
                return;
            }
        }

        System.out.println("Property not found!");
    }

    // ======================== DELETE ===============================
    private static void deleteProperty() {
        viewProperties();
        if (properties.isEmpty()) return;

        System.out.print("Enter Property ID to delete: ");
        String id = scanner.nextLine();

        Iterator<Property> it = properties.iterator();
        while (it.hasNext()) {
            if (it.next().getPropertyId().equals(id)) {
                it.remove();
                savePropertiesToFile();
                System.out.println("Property deleted successfully!");
                return;
            }
        }
        System.out.println("Property not found!");
    }

    // ======================== BUY PROPERTY ==========================
    private static void buyProperty() {
        viewProperties();
        if (properties.isEmpty()) return;

        System.out.print("Enter Property ID to BUY: ");
        String id = scanner.nextLine();

        for (Property p : properties) {
            if (p.getPropertyId().equals(id)) {

                if (!p.isAvailable()) {
                    System.out.println("This property is already SOLD!");
                    return;
                }

                p.setAvailable(false);

                history.add(new BuySellRecord(id, "BOUGHT", new Date().toString()));
                savePropertiesToFile();
                saveHistoryToFile();
                System.out.println("Property bought successfully!");
                return;
            }
        }

        System.out.println("Property not found!");
    }

    // ======================== SELL PROPERTY =========================
    private static void sellProperty() {
        viewProperties();
        if (properties.isEmpty()) return;

        System.out.print("Enter Property ID to SELL: ");
        String id = scanner.nextLine();

        for (Property p : properties) {
            if (p.getPropertyId().equals(id)) {

                if (p.isAvailable()) {
                    System.out.println("This property is not bought yet. Cannot sell!");
                    return;
                }

                p.setAvailable(true);

                history.add(new BuySellRecord(id, "SOLD", new Date().toString()));
                savePropertiesToFile();
                saveHistoryToFile();
                System.out.println("Property sold successfully!");
                return;
            }
        }

        System.out.println("Property not found!");
    }

    // ======================== VIEW HISTORY ==========================
    private static void viewHistory() {
        if (history.isEmpty()) {
            System.out.println("No buy/sell activity yet.");
            return;
        }

        System.out.println("\n=== BUY / SELL HISTORY ===");
        for (BuySellRecord r : history) {
            System.out.println(r.display());
        }
    }

    // ======================== FILE HANDLING ==========================
    private static void savePropertiesToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(PROPERTIES_FILE))) {
            for (Property p : properties) {
                pw.println(
                        p.getPropertyId() + "," +
                        p.getType() + "," +
                        p.getAddress() + "," +
                        p.getPrice() + "," +
                        p.getArea() + "," +
                        p.getBedrooms() + "," +
                        p.isAvailable()
                );
            }
        } catch (Exception e) {
            System.out.println("Error saving properties: " + e.getMessage());
        }
    }

    private static void loadPropertiesFromFile() {
        File file = new File(PROPERTIES_FILE);
        if (!file.exists()) return;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String[] d = fileScanner.nextLine().split(",");

                Property p = new Property(
                        d[0], d[1], d[2],
                        Double.parseDouble(d[3]),
                        Double.parseDouble(d[4]),
                        Integer.parseInt(d[5])
                );
                p.setAvailable(Boolean.parseBoolean(d[6]));

                properties.add(p);
            }
        } catch (Exception e) {
            System.out.println("Error loading properties: " + e.getMessage());
        }
    }

    private static void saveHistoryToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(HISTORY_FILE))) {
            for (BuySellRecord r : history) {
                pw.println(r.toString());
            }
        } catch (Exception e) {
            System.out.println("Error saving history: " + e.getMessage());
        }
    }

    private static void loadHistoryFromFile() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) return;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String[] d = fileScanner.nextLine().split(",");
                history.add(new BuySellRecord(d[0], d[1], d[2]));
            }
        } catch (Exception e) {
            System.out.println("Error loading history: " + e.getMessage());
        }
    }
}
