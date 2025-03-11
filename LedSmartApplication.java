import java.sql.*;
import java.util.*;

public class LedSmartApplication {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        LedSmartApplication app = new LedSmartApplication();

        while (true) {
            System.out.println("\n===== LED Smart System =====");
            System.out.println("1. Show Dashboard");
            System.out.println("2. Add New Device");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");

            int choice = scanner.nextInt();
            scanner.nextLine();  // Consume newline

            switch (choice

                case 2 -> app.addNewDevice(scanner);
                case 3 -> {
                    System.out.println("Exiting...");
                    scanner.close();
                    System.exit(0);
                }
                default -> System.out.println("Invalid choice. Try again.");
            }
        }

    }

    // ✅ Dashboard Display
    public void showDashboard() {
        System.out.println("\n--- Dashboard ---");
        System.out.println("Total Street Lights: " + getTotalStreetLights());
        System.out.println("Active Faults: " + getActiveFaults());
        System.out.println("Resolved Faults (Last 4 Weeks): " + getResolvedFaults());

        System.out.println("\n--- Fault Data ---");
                Object> row : getFaultData()) {
                println(row);
                
                
                
                
                ta
                ring, Object>>

    getFaultData() {
                """
                viceid, d.location, COALESCE(d2.light_status, 0) AS light_status, 
                   COALESCE(f.fault_status, 1) AS fault_status, d2.timestamp, 
                   CASE WHEN TIMESTAMPDIFF(MINUTE, d2.timestamp, NOW()) <= 5 THEN 1 ELSE 0 END AS device_status 
            FROM loc_table d 
            LEFT JOIN (SELECT faultdev_id, fault_status FROM fault_table WHERE (faultdev_id, reporttime) 
                       IN (SELECT faultdev_id, MAX(reporttime) FROM fault_table GROUP BY faultdev_id)) f 
                       ON d.deviceid = f.faultdev_id 
            LEFT JOIN (SELECT deviceid, light_status, timestamp FROM sensor_data WHERE (deviceid, timestamp) 
                       IN (SELECT deviceid, MAX(timestamp) FROM sensor_data GROUP BY deviceid)) d2 
                       ON d.deviceid = d2.deviceid;
        """;

        return DatabaseConnection.executeQuery(query);
    }

    // ✅ Helper Methods for Counting Data
    private int getTotalStreetLights() {
        return DatabaseConnection.executeCount("SELECT COUNT(DISTINCT deviceid) FROM loc_table");
    }

    private int getActiveFaults() {
        return DatabaseConnection.executeCount("SELECT COUNT(*) FROM fault_table WHERE fault_status = 0");
    }

    private int getResolvedFaults() {
        return DatabaseConnection.executeCount(
                "SELECT COUNT(DISTINCT faultdev_id) FROM fault_table WHERE fault_status = 1 AND reporttime >= DATE_SUB(NOW(), INTERVAL 4 WEEK)");
    }

    // ✅ Add New Device
    public void addNewDevice(Scanner scanner) {
        System.out.print("Enter Device ID: ");

        String deviceId = scanner.nextLine();
        System.out.print("Enter Latitude: ");
        String latitude = scanner.nextLine();
        System.out.print("Enter Longitude: ");
        String longitude = scanner.nextLine();
        System.out.print("Enter Location: ");
        String location = scanner.nextLine();

        String query = "INSERT INTO loc_table (deviceid, lat, lon, location) VALUES (?, ?, ?, ?)";
        if (DatabaseConnection.deviceExists(deviceId)) {
            System.out.println("❌ Device ID already exists.");
        } else if (DatabaseConnection.executeUpdate(query, deviceId, latitude, longitude, location)) {
            System.out.println("✅ Device details saved successfully.");
        } else {
            System.out.println("❌ Failed to save device details.");
        }
    }
}