import java.util.*;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
// Property class to hold property details
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
                "ID: %s | Type: %s | Address: %s | Price: %.2f | Area: %.1f sqft | Bedrooms: %d | Status: %s",
                propertyId, type, address, price, area, bedrooms,
                available ? "Available" : "Sold"
        );
    }
}

//buy sell records class
class BuySellRecord {
    private String propertyId;
    private String action;      // SOLD / AVAILABLE
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

//main class
public class RealEstateManagement {

    private static Scanner scanner = new Scanner(System.in);
    private static List<Property> properties = new ArrayList<>();
    private static List<BuySellRecord> history = new ArrayList<>();

    private static final String ADMIN_USER = "admin";
    private static final String ADMIN_PASS = "admin123";

    private static final String PROPERTIES_FILE = "properties.txt";
    private static final String HISTORY_FILE = "history.txt";

    private static final DateTimeFormatter DATE_FORMAT =
            DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static void main(String[] args) {
        loadPropertiesFromFile();
        loadHistoryFromFile();
        showLoginMenu();
    }

    // login menu
    private static void showLoginMenu() {
        System.out.println("--- NAVYA REAL ESTATE MANAGEMENT SYSTEM ---");

        while (true) {
            System.out.print("\nEnter Username: ");
            String user = scanner.nextLine().trim();

            System.out.print("Enter Password: ");
            String pass = scanner.nextLine().trim();

            if (user.equals(ADMIN_USER) && pass.equals(ADMIN_PASS)) {
                System.out.println("\nLogin Successful! Welcome Admin.\n");
                showMainMenu();
                return;
            } else {
                System.out.println("Incorrect username or password. Try again.\n");
            }
        }
    }

    // main menu
    private static void showMainMenu() {
        while (true) {
            System.out.println("... MAIN MENU ...");
            System.out.println("1. Add New Property");
            System.out.println("2. View All Properties");
            System.out.println("3. Search Properties by Address");
            System.out.println("4. Update Property Price");
            System.out.println("5. Delete Property");
            System.out.println("6. Mark Property as Sold");
            System.out.println("7. Mark Property as Available Again");
            System.out.println("8. View Transaction History");
            System.out.println("9. Logout");
            System.out.print("Choose option (1-9): ");

            String input = scanner.nextLine().trim();
            if (input.isEmpty()) {
                System.out.println("Please enter a number!\n");
                continue;
            }

            try {
                int choice = Integer.parseInt(input);

                switch (choice) {
                    case 1 -> addProperty();
                    case 2 -> viewProperties();
                    case 3 -> searchProperties();
                    case 4 -> updateProperty();
                    case 5 -> deleteProperty();
                    case 6 -> markAsSold();
                    case 7 -> markAsAvailable();
                    case 8 -> viewHistory();
                    case 9 -> {
                        System.out.println("Logged out successfully!");
                        return;
                    }
                    default -> System.out.println("Invalid choice! Please select 1-9.\n");
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input! Please enter a valid number.\n");
            }
        }
    }
    private static String readNonEmptyString(String prompt) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            if (!input.isBlank()) {
                return input;
            }
            System.out.println("This field cannot be empty! Please try again.");
        }
    }

    private static double readPositiveDouble(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                double value = Double.parseDouble(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Value must be positive!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid number! Please enter a valid positive number.");
            }
        }
    }

    private static int readPositiveInt(String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                if (value > 0) return value;
                System.out.println("Value must be positive!");
            } catch (NumberFormatException e) {
                System.out.println("Invalid integer! Please enter a valid positive integer.");
            }
        }
    }

    //add property
    private static void addProperty() {
        System.out.println("\n=== ADD NEW PROPERTY ===");

        String id = readNonEmptyString("Enter Property ID (e.g., P001): ");

        // Check for duplicate ID
        for (Property p : properties) {
            if (p.getPropertyId().equalsIgnoreCase(id)) {
                System.out.println("Error: Property ID '" + id + "' already exists!\n");
                return;
            }
        }

        String type = readNonEmptyString("Enter Type (e.g., Apartment, House, Villa): ");
        String address = readNonEmptyString("Enter Address: ");
        double price = readPositiveDouble("Enter Price (₹): ");
        double area = readPositiveDouble("Enter Area (sqft): ");
        int bedrooms = readPositiveInt("Enter Number of Bedrooms: ");

        properties.add(new Property(id, type, address, price, area, bedrooms));
        savePropertiesToFile();
        System.out.println("Property added successfully!\n");
    }

    // view properties
    private static void viewProperties() {
        if (properties.isEmpty()) {
            System.out.println("\nNo properties available in the system.\n");
            return;
        }

        System.out.println("\n=== ALL PROPERTIES ===");
        for (Property p : properties) {
            System.out.println(p);
        }
        System.out.println();
    }

    //search
    private static void searchProperties() {
        if (properties.isEmpty()) {
            System.out.println("\nNo properties to search.\n");
            return;
        }

        String search = readNonEmptyString("Enter address keyword to search: ").toLowerCase();

        System.out.println("\n=== SEARCH RESULTS ===");
        boolean found = false;
        for (Property p : properties) {
            if (p.getAddress().toLowerCase().contains(search)) {
                System.out.println(p);
                found = true;
            }
        }

        if (!found) {
            System.out.println("No properties found matching '" + search + "'.\n");
        } else {
            System.out.println();
        }
    }

    // update
    private static void updateProperty() {
        if (properties.isEmpty()) {
            System.out.println("\nNo properties to update.\n");
            return;
        }

        viewProperties();
        String id = readNonEmptyString("Enter Property ID to update price: ");

        for (Property p : properties) {
            if (p.getPropertyId().equalsIgnoreCase(id)) {
                double newPrice = readPositiveDouble("Enter new price (₹): ");
                p.setPrice(newPrice);
                savePropertiesToFile();
                System.out.println("Price updated successfully!\n");
                return;
            }
        }
        System.out.println("Property ID not found!\n");
    }

    // delete
    private static void deleteProperty() {
        if (properties.isEmpty()) {
            System.out.println("\nNo properties to delete.\n");
            return;
        }

        viewProperties();
        String id = readNonEmptyString("Enter Property ID to delete: ");

        Iterator<Property> it = properties.iterator();
        while (it.hasNext()) {
            Property p = it.next();
            if (p.getPropertyId().equalsIgnoreCase(id)) {
                it.remove();
                savePropertiesToFile();
                System.out.println("Property deleted successfully!\n");
                return;
            }
        }
        System.out.println("Property ID not found!\n");
    }

    //mark as sold
    private static void markAsSold() {
        if (properties.isEmpty()) {
            System.out.println("\nNo properties available.\n");
            return;
        }

        viewProperties();
        String id = readNonEmptyString("Enter Property ID to mark as SOLD: ");

        for (Property p : properties) {
            if (p.getPropertyId().equalsIgnoreCase(id)) {
                if (!p.isAvailable()) {
                    System.out.println("This property is already marked as Sold!\n");
                    return;
                }

                p.setAvailable(false);
                String date = LocalDateTime.now().format(DATE_FORMAT);
                history.add(new BuySellRecord(id, "SOLD", date));
                savePropertiesToFile();
                saveHistoryToFile();
                System.out.println("Property marked as SOLD successfully!\n");
                return;
            }
        }
        System.out.println("Property ID not found!\n");
    }

    // mark as available
    private static void markAsAvailable() {
        if (properties.isEmpty()) {
            System.out.println("\nNo properties available.\n");
            return;
        }

        viewProperties();
        String id = readNonEmptyString("Enter Property ID to mark as AVAILABLE again: ");

        for (Property p : properties) {
            if (p.getPropertyId().equalsIgnoreCase(id)) {
                if (p.isAvailable()) {
                    System.out.println("This property is already Available!\n");
                    return;
                }

                p.setAvailable(true);
                String date = LocalDateTime.now().format(DATE_FORMAT);
                history.add(new BuySellRecord(id, "AVAILABLE AGAIN", date));
                savePropertiesToFile();
                saveHistoryToFile();
                System.out.println("Property marked as Available again successfully!\n");
                return;
            }
        }
        System.out.println("Property ID not found!\n");
    }

    // view history
    private static void viewHistory() {
        if (history.isEmpty()) {
            System.out.println("\nNo transaction history yet.\n");
            return;
        }

        System.out.println("\n=== TRANSACTION HISTORY ===");
        for (BuySellRecord r : history) {
            System.out.println(r.display());
        }
        System.out.println();
    }

    // file handling
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
        } catch (IOException e) {
            System.out.println("Error saving properties: " + e.getMessage() + "\n");
        }
    }

    private static void loadPropertiesFromFile() {
        File file = new File(PROPERTIES_FILE);
        if (!file.exists()) return;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", 7);
                if (parts.length < 7) continue; // Skip corrupted lines

                Property p = new Property(
                        parts[0], parts[1], parts[2],
                        Double.parseDouble(parts[3]),
                        Double.parseDouble(parts[4]),
                        Integer.parseInt(parts[5])
                );
                p.setAvailable(Boolean.parseBoolean(parts[6]));
                properties.add(p);
            }
        } catch (Exception e) {
            System.out.println("Error loading properties (some data may be corrupted): " + e.getMessage() + "\n");
        }
    }

    private static void saveHistoryToFile() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(HISTORY_FILE))) {
            for (BuySellRecord r : history) {
                pw.println(r.toString());
            }
        } catch (IOException e) {
            System.out.println("Error saving history: " + e.getMessage() + "\n");
        }
    }

    private static void loadHistoryFromFile() {
        File file = new File(HISTORY_FILE);
        if (!file.exists()) return;

        try (Scanner fileScanner = new Scanner(file)) {
            while (fileScanner.hasNextLine()) {
                String line = fileScanner.nextLine();
                if (line.trim().isEmpty()) continue;

                String[] parts = line.split(",", 3);
                if (parts.length < 3) continue;

                history.add(new BuySellRecord(parts[0], parts[1], parts[2]));
            }
        } catch (Exception e) {
            System.out.println("Error loading history: " + e.getMessage() + "\n");
        }
    }
}
