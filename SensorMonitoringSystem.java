import java.util.Scanner;

public class SensorMonitoringSystem {
    private static char set = '2';
    private static final int threshold = 500;
    
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        
        while (true) {
            System.out.println("Enter command (1 = ON, 0 = OFF, 2 = AUTO): ");
            if (scanner.hasNext()) {
                char command = scanner.next().charAt(0);
                if (command == '1' || command == '0' || command == '2') {
                    set = command;
                }
            }
            
            int ldrValue = readLDR();
            boolean irDetected = readIR();
            boolean ledStatus = controlLED(ldrValue, irDetected);
            float voltage = readVoltage();
            float current = calculateCurrent(voltage);
            float power = calculatePower(voltage, current);
            float temperature = readTemperature();
            
            printSensorData(voltage, current, power, temperature, ldrValue, ledStatus, irDetected);
            
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
    private static int readLDR() {
        return (int) (Math.random() * 1024);
    }
    
    private static boolean readIR() {
        return Math.random() > 0.5;
    }
    
    private static boolean controlLED(int ldrValue, boolean irDetected) {
        if (set == '1' || irDetected) {
            return true;
        } else if (set == '2' && ldrValue > threshold) {
            return true;
        } else {
            return false;
        }
    }
    
    private static float readVoltage() {
        return (float) (Math.random() * 5.0);
    }
    
    private static float calculateCurrent(float voltage) {
        return (voltage - 2.5f) / 0.1f / 1000;
    }
    
    private static float calculatePower(float voltage, float current) {
        return voltage * current;
    }
    
    private static float readTemperature() {
        int rawValue = (int) (Math.random() * 1024);
        return (rawValue - 32) * 0.5f;
    }
    
    private static void printSensorData(float voltage, float current, float power, float temperature, int ldrValue, boolean ledStatus, boolean irDetected) {
        System.out.println("Device_Name: Light1");
        System.out.println("LED_Voltage: " + voltage);
        System.out.println("LED_Current: " + current);
        System.out.println("LED_Watts: " + power);
        System.out.println("Temperature: " + temperature);
        System.out.println("Light_Density: " + ldrValue);
        System.out.println("Light_PIN: " + (ledStatus ? "ON" : "OFF"));
        System.out.println("Input: " + set);
        System.out.println("IR: " + (irDetected ? "Detected" : "Not Detected"));
        System.out.println("------------------------------------------------");
    }
}
