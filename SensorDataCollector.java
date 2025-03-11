import com.fazecast.jSerialComm.SerialPort;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SensorDataCollector {
    private static final String DB_URL = "jdbc:mysql://localhost:3306/ledsmart";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Root123@SA";
    private static final String SERIAL_PORT = "COM18"; // Change this to your correct port
    private static final int BAUD_RATE = 9600;
    private static final Logger LOGGER = Logger.getLogger(SensorDataCollector.class.getName());

    public static void main(String[] args) {
        new SensorDataCollector().start();
    }

    public void start() {
        Connection conn = connectDatabase();
        SerialPort serialPort = connectSerialPort();

        if (conn == null || serialPort == null) {
            LOGGER.severe("Failed to connect to database or serial port. Exiting...");
            return;
        }

        while (true) {
            try {
                String data = readSerialData(serialPort);
                if (data != null) {
                    processAndInsertData(conn, data);
                }
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                LOGGER.log(Level.SEVERE, "Thread interrupted", e);
            }
        }
    }

    private Connection connectDatabase() {
        try {
            Connection conn = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
            LOGGER.info("✅ Successfully connected to the MySQL database.");
            return conn;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Database connection failed", e);
            return null;
        }
    }

    private SerialPort connectSerialPort() {
        SerialPort port = SerialPort.getCommPort(SERIAL_PORT);
        port.setBaudRate(BAUD_RATE);
        port.setNumDataBits(8);
        port.setNumStopBits(1);
        port.setParity(SerialPort.NO_PARITY);

        if (port.openPort()) {
            LOGGER.info("✅ Connected to serial port " + SERIAL_PORT);
            return port;
        } else {
            LOGGER.severe("❌ Serial connection failed.");
            return null;
        }
    }

    private String readSerialData(SerialPort serialPort) {
        byte[] buffer = new byte[1024];
        int numBytes = serialPort.readBytes(buffer, buffer.length);

        if (numBytes > 0) {
            return new String(buffer, 0, numBytes).trim();
        }
        return null;
    }

    private void processAndInsertData(Connection conn, String data) {
        try {
            String[] dataParts = data.split(",");
            String deviceId = getValue(dataParts, "Device_Name");
            double voltage = Double.parseDouble(getValue(dataParts, "LED_Voltage"));
            double current = Double.parseDouble(getValue(dataParts, "LED_Current"));
            double watts = Double.parseDouble(getValue(dataParts, "LED_Watts"));
            double tempCel = Double.parseDouble(getValue(dataParts, "Temperature"));
            double tempFer = Double.parseDouble(getValue(dataParts, "Temperature2"));
            double lightDensity = Double.parseDouble(getValue(dataParts, "Light_Density"));
            int lightPin = Integer.parseInt(getValue(dataParts, "Light_PIN"));
            int ir = Integer.parseInt(getValue(dataParts, "IR"));
            int input = Integer.parseInt(getValue(dataParts, "Input"));

            insertIntoDatabase(conn, deviceId, voltage, current, watts, tempCel, tempFer, lightDensity, lightPin, ir,
                    input);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "⚠️ Error parsing data: " + data, e);
        }
    }

    private String getValue(String[] dataParts, String key) {
        for (String part : dataParts) {
            String[] pair = part.split(":");
            if (pair.length == 2 && pair[0].trim().equals(key)) {
                return pair[1].trim();
            }
        }
        throw new IllegalArgumentException("Missing value for key: " + key);
    }

    private void insertIntoDatabase(Connection conn, String deviceId, double voltage, double current, double watts,
            double tempCel, double tempFer, double lightDensity, int lightPin, int ir, int input) {
        String procedureCall = "{CALL InsertSensorDataWithFault(?, ?, ?, ?, ?, ?, ?, ?, ?, ?)}";
        try (CallableStatement stmt = conn.prepareCall(procedureCall)) {
            stmt.setString(1, deviceId);
            stmt.setDouble(2, voltage);
            stmt.setDouble(3, current);
            stmt.setDouble(4, watts);
            stmt.setDouble(5, tempCel);
            stmt.setDouble(6, tempFer);
            stmt.setDouble(7, lightDensity);
            stmt.setInt(8, lightPin);
            stmt.setInt(9, ir);
            stmt.setInt(10, input);

            stmt.execute();
            LOGGER.info("✅ Data inserted successfully: " + deviceId);
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "❌ Database insert error", e);
        }
    }
}