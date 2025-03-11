import java.util.Random;

public class LDRSimulator {
    private static final int THRESHOLD = 500;

    public static void main(String[] args) {
        Random random = new Random(); // Simulate analog readings
        boolean ledState;

        while (true) {
            int ldrValue = random.nextInt(1024); // Simulating LDR analog input (0-1023)
            System.out.println("LDR Value: " + ldrValue);

            if (ldrValue < THRESHOLD) {
                ledState = true; // LED ON
            } else {
                ledState = false; // LED OFF
            }

            System.out.println("LED is " + (ledState ? "ON" : "OFF"));

            try {
                Thread.sleep(100); // Simulate Arduino delay
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
