import java.io.DataOutputStream;
import java.net.Socket;
import java.util.Scanner;

/**
 * The {@code LauncherClients} class allows the user to launch
 * multiple clients at once by specifying the desired number.
 * <p>
 * Each client is started in a separate thread and assigned a unique ID.
 */
public class LauncherClients {
    private static final int SPORT = 8354;
    private static final String SHOST = "localhost";
    /**
     * Prompts the user for the number of clients and launches them.
     *
     * @param args not used.
     */
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        boolean valid = false;
        int numClients = 0;

         do {
            System.out.print("Quanti client vuoi collegare? [max 10]: ");
            if (scanner.hasNextInt()) {
                numClients = scanner.nextInt();
                valid = true;
                if (numClients > 10) {
                    System.out.println("Enter a number between 1 and 10.");
                }
            } else {
                System.out.println("Input non valido. Riprova.");
                scanner.nextLine();
            }
        }while (!valid && (numClients>10));

        try {
            // Send clients number to server
            Socket controlSocket = new Socket(SHOST, SPORT);
            DataOutputStream controlOut = new DataOutputStream(controlSocket.getOutputStream());
            controlOut.writeInt(numClients);
            controlOut.flush();
            controlSocket.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // start clients
        for (int i = 0; i < numClients; i++) {
            int id = i+1;
            new Thread(() -> {
                try {
                    Client.main(new String[]{String.valueOf(id)}); // chiama il main del client
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}
