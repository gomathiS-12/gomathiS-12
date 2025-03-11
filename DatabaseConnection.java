class DatabaseConnection {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ledsmart";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root123@SA";

    // ✅ Fetch List of Maps (For Queries with Multiple Rows)
    public static List<Map<String, Object>> executeQuery(String query, Object... params) {
        List<Map<String, Object>> resultList = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = createStatement(conn, query, params);
                ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                Map<String, Object> row = new HashMap<>();
                for (int i = 1; i <= rs.getMetaData().getColumnCount(); i++) {
                    row.put(rs.getMetaData().getColumnName(i), rs.getObject(i));
                }
                resultList.add(row);
            }
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
        return resultList;
    }

    // ✅ Fetch Single Count Value (For Queries Returning One Number)
    public static int executeCount(String query, Object... params) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = createStatement(conn, query, params);
                ResultSet rs = stmt.executeQuery()) {
            return rs.next() ? rs.getInt(1) : 0;
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
        return 0;
    }

    // ✅ Execute Update (For INSERT, UPDATE, DELETE)
    public static boolean executeUpdate(String query, Object... params) {
        try (Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
                PreparedStatement stmt = createStatement(conn, query, params)) {
            return stmt.executeUpdate() > 0;
        } catch (SQLException e) {
            System.err.println("❌ Database error: " + e.getMessage());
        }
        return false;
    }

    // ✅ Check if a Device Exists
    public static boolean deviceExists(String deviceId) {
        return executeCount("SELECT COUNT(*) FROM loc_table WHERE deviceid = ?", deviceId) > 0;
    }

    // ✅ Utility Method to Prepare Statements with Parameters
    private static PreparedStatement createStatement(Connection conn, String query, Object... params)
            throws SQLException {
        PreparedStatement stmt = conn.prepareStatement(query);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt;
    }
}