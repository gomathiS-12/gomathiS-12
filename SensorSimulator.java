import java.util.Random;

public class SensorSimulator {
    private static final int THRESHOLD = 500;
    private static char set = '2';

    public static void main(String[] args) {
        Random random = new Random();
        boolean ledState = false;

        while (true) {
            int ldrValue = random.nextInt(1024);
            int voltageValue = random.nextInt(1024);
            int temperatureValue = random.nextInt(1024);
            boolean irState = random.nextBoolean();

            double voltage = (voltageValue * 5.0) / 1023.0;
            double current = (voltageValue - 2.5) / 0.1 / 1000;
            double watts = current * voltage;
            double temperature = (temperatureValue - 32) * 0.5;

            if (irState || set == '1') {
                ledState = true;
            } else {
                ledState = false;
            }
            if (set == '0') {
                ledState = false;
            }

            System.out.printf("Device_Name: Light1, LED_Voltage: %.2f, LED_Current: %.6f, LED_Watts: %.6f, Temperature: %.2f, Temperature2: %d, Light_Density: %d, Light_PIN: %s, Input: %c, IR: %s%n",
                voltage, current, watts, temperature, temperatureValue, ldrValue, ledState ? "ON" : "OFF", set, irState ? "DETECTED" : "NOT DETECTED");

            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
