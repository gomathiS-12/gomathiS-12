import java.util.Random;

public class LM35Simulator {
    private static final int SENSOR_PIN = 1;
    private static final double VOLTAGE_REFERENCE = 5.0;
    private static final int ADC_RESOLUTION = 1024;

    public static void main(String[] args) {
        Random random = new Random(); // Simulate analog readings

        while (true) {
            int reading = random.nextInt(ADC_RESOLUTION); // Simulating analog input (0-1023)
            double voltage = reading * (VOLTAGE_REFERENCE / ADC_RESOLUTION);
            double temperatureC = voltage * 100;
            double temperatureF = (temperatureC * 9.0 / 5.0) + 32.0;

            System.out.printf("Temperature: %.2f°C  |  %.2f°F%n", temperatureC, temperatureF);

            try {
                Thread.sleep(1000); // Simulate delay between readings
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
